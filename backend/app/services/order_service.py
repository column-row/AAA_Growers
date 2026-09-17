import uuid
from datetime import datetime
from app.models import db, Order, OrderItem, OrderStatus, Product, Inventory, InventoryLog, Notification, Dispatch
from app.utils.validators import validate_required_fields


class OrderService:

    @staticmethod
    def create_order(customer_id: int, data: dict):
        required = ["items", "delivery_address", "delivery_city", "delivery_phone"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        items_data = data.get("items", [])
        if not items_data or not isinstance(items_data, list):
            return False, "Order must contain at least one item", None

        # Validate product availability and sufficient stock
        order_items_to_create = []
        total_amount = 0.0

        for item in items_data:
            product_id = item.get("product_id")
            quantity = int(item.get("quantity", 1))

            if quantity <= 0:
                return False, f"Invalid quantity ({quantity}) for item", None

            product = Product.query.get(product_id)
            if not product or not product.is_active:
                return False, f"Product ID {product_id} is unavailable or not found", None

            current_stock = product.current_stock
            if current_stock < quantity:
                return False, f"Insufficient stock for '{product.name}'. Available: {current_stock}, Requested: {quantity}", None

            unit_price = float(product.price)
            subtotal = unit_price * quantity
            total_amount += subtotal

            order_items_to_create.append({
                "product_id": product.id,
                "product_name": product.name,
                "unit_price": unit_price,
                "quantity": quantity,
                "subtotal": subtotal
            })

        discount_amount = float(data.get("discount_amount", 0.0))
        shipping_fee = float(data.get("shipping_fee", 300.0 if total_amount < 3000 else 0.0))
        tax_amount = float(data.get("tax_amount", 0.0))
        net_amount = max(0.0, total_amount - discount_amount + shipping_fee + tax_amount)

        # Generate unique order number
        order_number = f"ORD-{datetime.utcnow().strftime('%Y%m%d')}-{uuid.uuid4().hex[:6].upper()}"

        order = Order(
            order_number=order_number,
            customer_id=customer_id,
            total_amount=total_amount,
            discount_amount=discount_amount,
            shipping_fee=shipping_fee,
            tax_amount=tax_amount,
            net_amount=net_amount,
            status=OrderStatus.PENDING,
            payment_status="PENDING",
            delivery_address=data.get("delivery_address").strip(),
            delivery_city=data.get("delivery_city").strip(),
            delivery_phone=data.get("delivery_phone").strip(),
            notes=data.get("notes")
        )
        db.session.add(order)
        db.session.flush()

        for item_info in order_items_to_create:
            order_item = OrderItem(
                order_id=order.id,
                product_id=item_info["product_id"],
                product_name=item_info["product_name"],
                unit_price=item_info["unit_price"],
                quantity=item_info["quantity"],
                subtotal=item_info["subtotal"]
            )
            db.session.add(order_item)

            # Atomically reserve / deduct inventory stock
            inv = Inventory.query.filter_by(product_id=item_info["product_id"]).first()
            if inv:
                prev = inv.current_stock
                inv.current_stock -= item_info["quantity"]
                log = InventoryLog(
                    product_id=item_info["product_id"],
                    change_quantity=-item_info["quantity"],
                    previous_stock=prev,
                    new_stock=inv.current_stock,
                    movement_type="SALE_DEDUCTION",
                    reference_id=order.order_number,
                    notes=f"Deducted for Order {order.order_number}",
                    created_by=customer_id
                )
                db.session.add(log)

        # Auto-create notification for customer
        notification = Notification(
            user_id=customer_id,
            title="Order Placed Successfully",
            message=f"Your order {order.order_number} for KES {net_amount:,.2f} has been placed. Proceed to payment.",
            type="ORDER"
        )
        db.session.add(notification)

        db.session.commit()
        return True, "Order created successfully", order.to_dict()

    @staticmethod
    def get_order_by_id(order_id: int, user_id=None, role=None):
        order = Order.query.get(order_id)
        if not order:
            return None
        # Access control: non-staff can only view their own orders
        if role not in ["ADMIN", "INVENTORY_MANAGER", "FINANCE_MANAGER", "DISPATCH_MANAGER"] and user_id and order.customer_id != user_id:
            return None
        return order.to_dict()

    @staticmethod
    def get_orders(customer_id=None, status=None, payment_status=None, page=1, per_page=20):
        query = Order.query

        if customer_id:
            query = query.filter_by(customer_id=customer_id)
        if status:
            query = query.filter_by(status=status)
        if payment_status:
            query = query.filter_by(payment_status=payment_status)

        total = query.count()
        orders = query.order_by(Order.placed_at.desc()).offset((page - 1) * per_page).limit(per_page).all()
        return [o.to_dict() for o in orders], total

    @staticmethod
    def update_order_status(order_id: int, new_status: str, notes=None):
        order = Order.query.get(order_id)
        if not order:
            return False, "Order not found", None

        if new_status not in OrderStatus.ALL:
            return False, f"Invalid order status '{new_status}'", None

        old_status = order.status
        order.status = new_status

        # If cancelled from an active state, restore inventory stock
        if new_status == OrderStatus.CANCELLED and old_status != OrderStatus.CANCELLED:
            for item in order.items:
                inv = Inventory.query.filter_by(product_id=item.product_id).first()
                if inv:
                    prev = inv.current_stock
                    inv.current_stock += item.quantity
                    log = InventoryLog(
                        product_id=item.product_id,
                        change_quantity=item.quantity,
                        previous_stock=prev,
                        new_stock=inv.current_stock,
                        movement_type="RETURN",
                        reference_id=order.order_number,
                        notes=f"Restored stock from cancelled order {order.order_number}"
                    )
                    db.session.add(log)

        # If moving to PROCESSING or DISPATCHED, ensure a dispatch record exists
        if new_status in [OrderStatus.PROCESSING, OrderStatus.DISPATCHED]:
            if not order.dispatch:
                dsp_number = f"DSP-{datetime.utcnow().strftime('%Y%m%d')}-{order.id:04d}"
                dispatch = Dispatch(
                    dispatch_number=dsp_number,
                    order_id=order.id,
                    delivery_address=f"{order.delivery_address}, {order.delivery_city}",
                    status="PENDING" if new_status == OrderStatus.PROCESSING else "ASSIGNED",
                    tracking_notes=notes or f"Order transitioned to {new_status}"
                )
                db.session.add(dispatch)

        # Send notification to customer
        notif = Notification(
            user_id=order.customer_id,
            title=f"Order Status: {new_status}",
            message=f"Your order {order.order_number} status is now {new_status}.",
            type="ORDER"
        )
        db.session.add(notif)

        db.session.commit()
        return True, f"Order status updated to {new_status}", order.to_dict()
