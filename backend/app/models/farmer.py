from datetime import datetime
from app.models import db


class Farmer(db.Model):
    __tablename__ = "farmers"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False, index=True)
    farm_name = db.Column(db.String(200), nullable=False)
    farm_location = db.Column(db.String(255), nullable=False, index=True)
    farm_size_acres = db.Column(db.Numeric(8, 2), default=0.00)
    crops_grown = db.Column(db.String(500), nullable=True)
    farming_experience_years = db.Column(db.Integer, default=0)
    national_id = db.Column(db.String(50), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    bookings = db.relationship("TrainingBooking", backref="farmer", lazy="dynamic", cascade="all, delete-orphan")
    certifications = db.relationship("Certification", backref="farmer", lazy="dynamic", cascade="all, delete-orphan")

    def to_dict(self):
        return {
            "id": self.id,
            "user_id": self.user_id,
            "full_name": self.user.full_name if self.user else "Farmer",
            "email": self.user.email if self.user else None,
            "phone": self.user.phone if self.user else None,
            "farm_name": self.farm_name,
            "farm_location": self.farm_location,
            "farm_size_acres": float(self.farm_size_acres) if self.farm_size_acres else 0.0,
            "crops_grown": self.crops_grown,
            "farming_experience_years": self.farming_experience_years,
            "national_id": self.national_id,
            "total_trainings_attended": self.bookings.filter_by(status="COMPLETED").count() if hasattr(self.bookings, 'filter_by') else 0,
            "certifications_count": self.certifications.count() if hasattr(self.certifications, 'count') else 0,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }
