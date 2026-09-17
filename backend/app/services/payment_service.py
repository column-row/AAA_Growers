import uuid
from datetime import datetime
from app.models import db, Payment, PaymentMethod, PaymentStatus, Order, OrderStatus, Notification
from app.utils.validators import validate_required_fields


class PaymentService:

    @staticmethod
    def process_payment(user_id: int, data: dict):
        required = ["order_id", "amount", "payment_method"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        order_id = data.get("order_id")
        order = Order.query.get(order_id)
        if not order:
            return False, "Order not found", None

        if order.payment_status == "PAID":
            return False, f"Order {order.order_number} has already been paid", None

        amount = float(data.get("amount", 0.0))
        payment_method = data.get("payment_method", "").strip().upper()

        if payment_method not in PaymentMethod.ALL:
            return False, f"Invalid payment method '{payment_method}'. Allowed: {', '.join(PaymentMethod.ALL)}", None

        # Generate mock transaction reference based on payment method
        if payment_method == PaymentMethod.MPESA:
            tx_ref = f"MPESA-{datetime.utcnow().strftime('%Y%m%d%H%M')}-{uuid.uuid4().hex[:6].upper()}"
            provider = "MPESA_DAR_EXPRESS"
        elif payment_method == PaymentMethod.CARD:
            tx_ref = f"CARD-{datetime.utcnow().strftime('%Y%m%d')}-{uuid.uuid4().hex[:8].upper()}"
            provider = "AAA_CYBERSOURCE_GATEWAY"
        elif payment_method == PaymentMethod.BANK_TRANSFER:
            tx_ref = f"EFT-{datetime.utcnow().strftime('%Y%m%d')}-{uuid.uuid4().hex[:6].upper()}"
            provider = "KCB_IPRS_DIRECT"
        else:
            tx_ref = f"COD-{order.order_number}"
            provider = "CASH_COLLECTION"

        # Mock payment outcome simulation (defaults to SUCCESS, but can pass simulate_status in testing)
        simulated_status = data.get("simulate_status", PaymentStatus.SUCCESS).upper()
        if simulated_status not in PaymentStatus.ALL:
            simulated_status = PaymentStatus.SUCCESS

        payment = Payment(
            order_id=order.id,
            user_id=user_id,
            amount=amount,
            payment_method=payment_method,
            transaction_reference=tx_ref,
            status=simulated_status,
            payment_provider=provider,
            paid_at=datetime.utcnow() if simulated_status == PaymentStatus.SUCCESS else None
        )
        db.session.add(payment)

        if simulated_status == PaymentStatus.SUCCESS:
            order.payment_status = "PAID"
            if order.status == OrderStatus.PENDING:
                order.status = OrderStatus.PAID

            # Notify user
            notif = Notification(
                user_id=user_id,
                title="Payment Received",
                message=f"Payment of KES {amount:,.2f} for order {order.order_number} was successful (Ref: {tx_ref}).",
                type="ORDER"
            )
            db.session.add(notif)
        elif simulated_status == PaymentStatus.FAILED:
            order.payment_status = "FAILED"

        db.session.commit()
        return True, f"Payment processed with status: {simulated_status}", payment.to_dict()

    @staticmethod
    def get_payment_by_id(payment_id: int):
        payment = Payment.query.get(payment_id)
        if not payment:
            return None
        return payment.to_dict()

    @staticmethod
    def get_payments(user_id=None, order_id=None, status=None, page=1, per_page=20):
        query = Payment.query
        if user_id:
            query = query.filter_by(user_id=user_id)
        if order_id:
            query = query.filter_by(order_id=order_id)
        if status:
            query = query.filter_by(status=status)

        total = query.count()
        payments = query.order_by(Payment.created_at.desc()).offset((page - 1) * per_page).limit(per_page).all()
        return [p.to_dict() for p in payments], total

    @staticmethod
    def refund_payment(payment_id: int, reason=None):
        payment = Payment.query.get(payment_id)
        if not payment:
            return False, "Payment record not found", None

        if payment.status != PaymentStatus.SUCCESS:
            return False, f"Cannot refund payment with status '{payment.status}'", None

        payment.status = PaymentStatus.REFUNDED
        if payment.order:
            payment.order.payment_status = "REFUNDED"

        notif = Notification(
            user_id=payment.user_id,
            title="Payment Refunded",
            message=f"Your payment of KES {payment.amount:,.2f} (Ref: {payment.transaction_reference}) has been refunded.",
            type="ORDER"
        )
        db.session.add(notif)
        db.session.commit()

        return True, "Payment refunded successfully", payment.to_dict()
