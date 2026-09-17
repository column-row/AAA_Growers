from datetime import datetime
from werkzeug.security import generate_password_hash, check_password_hash
from app.models import db


class Role(db.Model):
    __tablename__ = "roles"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50), unique=True, nullable=False)
    description = db.Column(db.String(255), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "description": self.description,
            "created_at": self.created_at.isoformat() if self.created_at else None
        }


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    first_name = db.Column(db.String(100), nullable=False)
    last_name = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(150), unique=True, nullable=False, index=True)
    phone = db.Column(db.String(30), nullable=True)
    password_hash = db.Column(db.String(255), nullable=False)
    role = db.Column(db.String(50), nullable=False, default="CUSTOMER", index=True)
    status = db.Column(db.String(20), default="ACTIVE", index=True)  # ACTIVE, INACTIVE, SUSPENDED
    avatar_url = db.Column(db.String(500), nullable=True)
    address = db.Column(db.Text, nullable=True)
    city = db.Column(db.String(100), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    orders = db.relationship("Order", backref="customer", lazy="dynamic", foreign_keys="Order.customer_id")
    payments = db.relationship("Payment", backref="user", lazy="dynamic")
    farmer_profile = db.relationship("Farmer", backref="user", uselist=False, cascade="all, delete-orphan")
    trainer_profile = db.relationship("Trainer", backref="user", uselist=False, cascade="all, delete-orphan")
    driver_profile = db.relationship("Driver", backref="user", uselist=False, cascade="all, delete-orphan")
    notifications = db.relationship("Notification", backref="user", lazy="dynamic", cascade="all, delete-orphan")
    feedback = db.relationship("Feedback", backref="user", lazy="dynamic")

    def set_password(self, password: str):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password: str) -> bool:
        if not self.password_hash or not password:
            return False
        # If it's a bcrypt-format placeholder or standard werkzeug hash
        if self.password_hash.startswith("$2b$") or self.password_hash.startswith("$2a$"):
            try:
                import bcrypt
                return bcrypt.checkpw(password.encode('utf-8'), self.password_hash.encode('utf-8'))
            except Exception:
                pass
        return check_password_hash(self.password_hash, password)

    @property
    def full_name(self):
        return f"{self.first_name} {self.last_name}".strip()

    def to_dict(self, include_sensitive=False):
        data = {
            "id": self.id,
            "first_name": self.first_name,
            "last_name": self.last_name,
            "full_name": self.full_name,
            "email": self.email,
            "phone": self.phone,
            "role": self.role,
            "status": self.status,
            "avatar_url": self.avatar_url,
            "address": self.address,
            "city": self.city,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }
        if self.farmer_profile:
            data["farmer_profile"] = self.farmer_profile.to_dict()
        if self.trainer_profile:
            data["trainer_profile"] = self.trainer_profile.to_dict()
        if self.driver_profile:
            data["driver_profile"] = self.driver_profile.to_dict()
        return data
