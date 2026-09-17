from datetime import datetime
from app.models import db


class SessionStatus:
    UPCOMING = "UPCOMING"
    ONGOING = "ONGOING"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

    ALL = [UPCOMING, ONGOING, COMPLETED, CANCELLED]


class BookingStatus:
    BOOKED = "BOOKED"
    ATTENDED = "ATTENDED"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

    ALL = [BOOKED, ATTENDED, COMPLETED, CANCELLED]


class TrainingSession(db.Model):
    __tablename__ = "training_sessions"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    trainer_id = db.Column(db.Integer, db.ForeignKey("trainers.id", ondelete="RESTRICT"), nullable=False, index=True)
    title = db.Column(db.String(255), nullable=False)
    description = db.Column(db.Text, nullable=False)
    category = db.Column(db.String(100), default="General Farming")
    training_date = db.Column(db.Date, nullable=False, index=True)
    start_time = db.Column(db.Time, nullable=False)
    end_time = db.Column(db.Time, nullable=False)
    location = db.Column(db.String(255), nullable=False)
    capacity = db.Column(db.Integer, nullable=False, default=30)
    status = db.Column(db.String(50), nullable=False, default=SessionStatus.UPCOMING, index=True)
    materials_url = db.Column(db.String(500), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    bookings = db.relationship("TrainingBooking", backref="session", lazy="dynamic", cascade="all, delete-orphan")
    certifications = db.relationship("Certification", backref="training_session", lazy="dynamic")

    @property
    def booked_count(self):
        if not hasattr(self.bookings, 'filter'):
            return 0
        return self.bookings.filter(TrainingBooking.status.in_(["BOOKED", "ATTENDED", "COMPLETED"])).count()

    @property
    def is_full(self):
        return self.booked_count >= self.capacity

    @property
    def available_slots(self):
        return max(0, self.capacity - self.booked_count)

    def to_dict(self):
        return {
            "id": self.id,
            "trainer_id": self.trainer_id,
            "trainer_name": self.trainer_rel.user.full_name if self.trainer_rel and self.trainer_rel.user else "Staff Trainer",
            "trainer_specialization": self.trainer_rel.specialization if self.trainer_rel else None,
            "title": self.title,
            "description": self.description,
            "category": self.category,
            "training_date": self.training_date.isoformat() if self.training_date else None,
            "start_time": self.start_time.strftime("%H:%M") if self.start_time else None,
            "end_time": self.end_time.strftime("%H:%M") if self.end_time else None,
            "location": self.location,
            "capacity": self.capacity,
            "booked_count": self.booked_count,
            "available_slots": self.available_slots,
            "is_full": self.is_full,
            "status": self.status,
            "materials_url": self.materials_url,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }


class TrainingBooking(db.Model):
    __tablename__ = "training_bookings"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    training_id = db.Column(db.Integer, db.ForeignKey("training_sessions.id", ondelete="CASCADE"), nullable=False, index=True)
    farmer_id = db.Column(db.Integer, db.ForeignKey("farmers.id", ondelete="CASCADE"), nullable=False, index=True)
    booking_date = db.Column(db.DateTime, default=datetime.utcnow)
    status = db.Column(db.String(50), nullable=False, default=BookingStatus.BOOKED, index=True)
    notes = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    __table_args__ = (
        db.UniqueConstraint("training_id", "farmer_id", name="uq_farmer_session"),
    )

    def to_dict(self):
        return {
            "id": self.id,
            "training_id": self.training_id,
            "training_title": self.session.title if self.session else None,
            "training_date": self.session.training_date.isoformat() if self.session and self.session.training_date else None,
            "training_time": f"{self.session.start_time.strftime('%H:%M')} - {self.session.end_time.strftime('%H:%M')}" if self.session and self.session.start_time and self.session.end_time else None,
            "training_location": self.session.location if self.session else None,
            "farmer_id": self.farmer_id,
            "farmer_name": self.farmer.user.full_name if self.farmer and self.farmer.user else "Farmer",
            "farmer_phone": self.farmer.user.phone if self.farmer and self.farmer.user else None,
            "farm_name": self.farmer.farm_name if self.farmer else None,
            "booking_date": self.booking_date.isoformat() if self.booking_date else None,
            "status": self.status,
            "notes": self.notes,
            "created_at": self.created_at.isoformat() if self.created_at else None
        }
