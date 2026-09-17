from datetime import datetime
from app.models import db


class Trainer(db.Model):
    __tablename__ = "trainers"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False, index=True)
    specialization = db.Column(db.String(255), nullable=False)
    qualifications = db.Column(db.Text, nullable=True)
    bio = db.Column(db.Text, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    sessions = db.relationship("TrainingSession", backref="trainer_rel", lazy="dynamic")

    def to_dict(self):
        return {
            "id": self.id,
            "user_id": self.user_id,
            "full_name": self.user.full_name if self.user else "Trainer",
            "email": self.user.email if self.user else None,
            "phone": self.user.phone if self.user else None,
            "specialization": self.specialization,
            "qualifications": self.qualifications,
            "bio": self.bio,
            "sessions_count": self.sessions.count() if hasattr(self.sessions, 'count') else 0,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }
