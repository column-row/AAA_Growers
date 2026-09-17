from datetime import datetime
from app.models import db


class OrderStatus:
    PENDING = "PENDING"
    PAID = "PAID"
    PROCESSING = "PROCESSING"
    DISPATCHED = "DISPATCHED"
    DELIVERED = "DELIVERED"
    CANCELLED = "CANCELLED"

    ALL = [PENDING, PAID, PROCESSING, DISPATCHED, DELIVERED, CANCELLED]


class Order(db.Model):
    __tablename__ = "orders"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    order_number = db.Column(db.String(50), unique=True, nullable=False, index=True)
    customer_id = db.Column(db.Integer, db.ForeignKey("users.id", ondelete="RESTRICT"), nullable=False, index=True)
    total_amount = db.Column(db.Numeric(12, 2), nullable=False, default=0.00)
    discount_amount = db.Column(db.Numeric(12, 2), nullable=False, default=0.00)
    shipping_fee = db.Column(db.Numeric(12, 2), nullable=False, default=0.00)
    tax_amount = db.Column(db.Numeric(12, 2), nullable=False, default=0.00)
    net_amount = db.Column(db.Numeric(12, 2), nullable=False, default=0.00)
    status = db.Column(db.String(50), nullable=False, default=OrderStatus.PENDING, index=True)
    payment_status = db.Column(db.String(50), nullable=False, default="PENDING", index=True)
    delivery_address = db.Column(db.Text, nullable=False)
    delivery_city = db.Column(db.String(100), nullable=False)
    delivery_phone = db.Column(db.String(30), nullable=False)
    notes = db.Column(db.Text, nullable=True)
    placed_at = db.Column(db.DateTime, default=datetime.utcnow, index=True)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    items = db.relationship("OrderItem", backref="order", lazy="joined", cascade="all, delete-orphan")
    payments = db.relationship("Payment", backref="order", lazy="dynamic", cascade="all, delete-orphan")
    dispatch = db.relationship("Dispatch", backref="order", uselist=False, cascade="all, delete-orphan")

    def to_dict(self):
        return {
            "id": self.id,
            "order_number": self.order_number,
            "customer_id": self.customer_id,
            "customer_name": self.customer.full_name if self.customer else "Unknown Customer",
            "customer_email": self.customer.email if self.customer else None,
            "customer_phone": self.customer.phone if self.customer else None,
            "total_amount": float(self.total_amount),
            "discount_amount": float(self.discount_amount),
            "shipping_fee": float(self.shipping_fee),
            "tax_amount": float(self.tax_amount),
            "net_amount": float(self.net_amount),
            "status": self.status,
            "payment_status": self.payment_status,
            "delivery_address": self.delivery_address,
            "delivery_city": self.delivery_city,
            "delivery_phone": self.delivery_phone,
            "notes": self.notes,
            "items_count": len(self.items) if self.items else 0,
            "items": [item.to_dict() for item in self.items] if self.items else [],
            "dispatch": self.dispatch.to_dict() if self.dispatch else None,
            "placed_at": self.placed_at.isoformat() if self.placed_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }


class OrderItem(db.Model):
    __tablename__ = "order_items"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    order_id = db.Column(db.Integer, db.ForeignKey("orders.id", ondelete="CASCADE"), nullable=False, index=True)
    product_id = db.Column(db.Integer, db.ForeignKey("products.id", ondelete="RESTRICT"), nullable=False, index=True)
    product_name = db.Column(db.String(200), nullable=False)
    unit_price = db.Column(db.Numeric(12, 2), nullable=False)
    quantity = db.Column(db.Integer, nullable=False, default=1)
    subtotal = db.Column(db.Numeric(12, 2), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "order_id": self.order_id,
            "product_id": self.product_id,
            "product_name": self.product_name,
            "product_image": self.product.image_url if self.product else None,
            "product_sku": self.product.sku if self.product else None,
            "product_unit": self.product.unit if self.product else "kg",
            "unit_price": float(self.unit_price),
            "quantity": self.quantity,
            "subtotal": float(self.subtotal),
            "created_at": self.created_at.isoformat() if self.created_at else None
        }
