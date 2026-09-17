from datetime import datetime
from app.models import db


class PaymentMethod:
    MPESA = "MPESA"
    CARD = "CARD"
    BANK_TRANSFER = "BANK_TRANSFER"
    CASH_ON_DELIVERY = "CASH_ON_DELIVERY"

    ALL = [MPESA, CARD, BANK_TRANSFER, CASH_ON_DELIVERY]


class PaymentStatus:
    PENDING = "PENDING"
    SUCCESS = "SUCCESS"
    FAILED = "FAILED"
    REFUNDED = "REFUNDED"

    ALL = [PENDING, SUCCESS, FAILED, REFUNDED]


class Payment(db.Model):
    __tablename__ = "payments"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    order_id = db.Column(db.Integer, db.ForeignKey("orders.id", ondelete="CASCADE"), nullable=False, index=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id", ondelete="RESTRICT"), nullable=False)
    amount = db.Column(db.Numeric(12, 2), nullable=False)
    payment_method = db.Column(db.String(50), nullable=False)  # MPESA, CARD, BANK_TRANSFER, CASH_ON_DELIVERY
    transaction_reference = db.Column(db.String(100), unique=True, nullable=False, index=True)
    status = db.Column(db.String(50), nullable=False, default=PaymentStatus.PENDING, index=True)
    payment_provider = db.Column(db.String(50), default="MOCK_PAYMENT_GATEWAY")
    raw_response = db.Column(db.Text, nullable=True)
    paid_at = db.Column(db.DateTime, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "order_id": self.order_id,
            "order_number": self.order.order_number if self.order else None,
            "user_id": self.user_id,
            "user_name": self.user.full_name if self.user else "Customer",
            "amount": float(self.amount),
            "payment_method": self.payment_method,
            "transaction_reference": self.transaction_reference,
            "status": self.status,
            "payment_provider": self.payment_provider,
            "paid_at": self.paid_at.isoformat() if self.paid_at else None,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }
