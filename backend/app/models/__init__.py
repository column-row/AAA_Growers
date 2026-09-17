from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

from app.models.user import User, Role
from app.models.product import Category, Product
from app.models.inventory import Inventory, InventoryLog, MovementType
from app.models.order import Order, OrderItem, OrderStatus
from app.models.payment import Payment, PaymentMethod, PaymentStatus
from app.models.farmer import Farmer
from app.models.trainer import Trainer
from app.models.training import TrainingSession, TrainingBooking, SessionStatus, BookingStatus
from app.models.certification import Certification, CertificateStatus
from app.models.supplier import Supplier
from app.models.dispatch import Driver, Dispatch, DispatchStatus
from app.models.feedback import Feedback, Contact, Notification

__all__ = [
    "db",
    "User",
    "Role",
    "Category",
    "Product",
    "Inventory",
    "InventoryLog",
    "MovementType",
    "Order",
    "OrderItem",
    "OrderStatus",
    "Payment",
    "PaymentMethod",
    "PaymentStatus",
    "Farmer",
    "Trainer",
    "TrainingSession",
    "TrainingBooking",
    "SessionStatus",
    "BookingStatus",
    "Certification",
    "CertificateStatus",
    "Supplier",
    "Driver",
    "Dispatch",
    "DispatchStatus",
    "Feedback",
    "Contact",
    "Notification"
]
