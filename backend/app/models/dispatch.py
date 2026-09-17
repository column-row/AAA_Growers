from datetime import datetime
from app.models import db


class DispatchStatus:
    PENDING = "PENDING"
    ASSIGNED = "ASSIGNED"
    IN_TRANSIT = "IN_TRANSIT"
    DELIVERED = "DELIVERED"
    FAILED = "FAILED"

    ALL = [PENDING, ASSIGNED, IN_TRANSIT, DELIVERED, FAILED]


class Driver(db.Model):
    __tablename__ = "drivers"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False)
    license_number = db.Column(db.String(100), unique=True, nullable=False)
    vehicle_registration = db.Column(db.String(50), nullable=False)
    vehicle_type = db.Column(db.String(100), nullable=False, default="Van")
    is_available = db.Column(db.Boolean, default=True, index=True)
    current_location = db.Column(db.String(255), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    dispatches = db.relationship("Dispatch", backref="driver_rel", lazy="dynamic")

    def to_dict(self):
        return {
            "id": self.id,
            "user_id": self.user_id,
            "name": self.user.full_name if self.user else "Driver",
            "phone": self.user.phone if self.user else None,
            "email": self.user.email if self.user else None,
            "license_number": self.license_number,
            "vehicle_registration": self.vehicle_registration,
            "vehicle_type": self.vehicle_type,
            "is_available": self.is_available,
            "current_location": self.current_location,
            "total_deliveries": self.dispatches.filter_by(status="DELIVERED").count() if hasattr(self.dispatches, 'filter_by') else 0,
            "created_at": self.created_at.isoformat() if self.created_at else None
        }


class Dispatch(db.Model):
    __tablename__ = "dispatches"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    dispatch_number = db.Column(db.String(100), unique=True, nullable=False, index=True)
    order_id = db.Column(db.Integer, db.ForeignKey("orders.id", ondelete="CASCADE"), unique=True, nullable=False)
    driver_id = db.Column(db.Integer, db.ForeignKey("drivers.id", ondelete="SET NULL"), nullable=True, index=True)
    delivery_address = db.Column(db.Text, nullable=False)
    dispatch_date = db.Column(db.DateTime, nullable=True)
    delivery_date = db.Column(db.DateTime, nullable=True)
    status = db.Column(db.String(50), nullable=False, default=DispatchStatus.PENDING, index=True)
    tracking_notes = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "dispatch_number": self.dispatch_number,
            "order_id": self.order_id,
            "order_number": self.order.order_number if self.order else None,
            "driver_id": self.driver_id,
            "driver_name": self.driver_rel.user.full_name if self.driver_rel and self.driver_rel.user else "Unassigned",
            "driver_phone": self.driver_rel.user.phone if self.driver_rel and self.driver_rel.user else None,
            "vehicle_reg": self.driver_rel.vehicle_registration if self.driver_rel else None,
            "delivery_address": self.delivery_address,
            "dispatch_date": self.dispatch_date.isoformat() if self.dispatch_date else None,
            "delivery_date": self.delivery_date.isoformat() if self.delivery_date else None,
            "status": self.status,
            "tracking_notes": self.tracking_notes,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }
