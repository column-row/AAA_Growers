from datetime import datetime
from app.models import db


class CertificateStatus:
    ACTIVE = "ACTIVE"
    REVOKED = "REVOKED"
    EXPIRED = "EXPIRED"

    ALL = [ACTIVE, REVOKED, EXPIRED]


class Certification(db.Model):
    __tablename__ = "certifications"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    certificate_number = db.Column(db.String(100), unique=True, nullable=False, index=True)
    farmer_id = db.Column(db.Integer, db.ForeignKey("farmers.id", ondelete="CASCADE"), nullable=False, index=True)
    training_id = db.Column(db.Integer, db.ForeignKey("training_sessions.id", ondelete="RESTRICT"), nullable=False, index=True)
    issue_date = db.Column(db.Date, nullable=False)
    expiry_date = db.Column(db.Date, nullable=True)
    title = db.Column(db.String(255), nullable=False)
    verification_hash = db.Column(db.String(128), unique=True, nullable=False)
    status = db.Column(db.String(50), nullable=False, default=CertificateStatus.ACTIVE, index=True)
    pdf_url = db.Column(db.String(500), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "certificate_number": self.certificate_number,
            "farmer_id": self.farmer_id,
            "farmer_name": self.farmer.user.full_name if self.farmer and self.farmer.user else "Farmer",
            "farm_name": self.farmer.farm_name if self.farmer else None,
            "farmer_national_id": self.farmer.national_id if self.farmer else None,
            "training_id": self.training_id,
            "training_title": self.training_session.title if self.training_session else None,
            "trainer_name": self.training_session.trainer_rel.user.full_name if self.training_session and self.training_session.trainer_rel and self.training_session.trainer_rel.user else "AAA Certified Agronomist",
            "issue_date": self.issue_date.isoformat() if self.issue_date else None,
            "expiry_date": self.expiry_date.isoformat() if self.expiry_date else None,
            "title": self.title,
            "verification_hash": self.verification_hash,
            "status": self.status,
            "pdf_url": self.pdf_url,
            "created_at": self.created_at.isoformat() if self.created_at else None
        }
