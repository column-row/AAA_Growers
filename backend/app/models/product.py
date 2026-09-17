from datetime import datetime
from app.models import db


class Category(db.Model):
    __tablename__ = "categories"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(100), unique=True, nullable=False)
    slug = db.Column(db.String(120), unique=True, nullable=False, index=True)
    description = db.Column(db.Text, nullable=True)
    image_url = db.Column(db.String(500), nullable=True)
    is_active = db.Column(db.Boolean, default=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    products = db.relationship("Product", backref="category_rel", lazy="dynamic")

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "slug": self.slug,
            "description": self.description,
            "image_url": self.image_url,
            "is_active": self.is_active,
            "product_count": self.products.count() if hasattr(self.products, 'count') else 0,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }


class Product(db.Model):
    __tablename__ = "products"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    category_id = db.Column(db.Integer, db.ForeignKey("categories.id", ondelete="SET NULL"), nullable=True, index=True)
    name = db.Column(db.String(200), nullable=False, index=True)
    sku = db.Column(db.String(100), unique=True, nullable=False, index=True)
    description = db.Column(db.Text, nullable=True)
    unit = db.Column(db.String(50), nullable=False, default="kg")
    price = db.Column(db.Numeric(12, 2), nullable=False, default=0.00)
    cost_price = db.Column(db.Numeric(12, 2), nullable=True, default=0.00)
    image_url = db.Column(db.String(500), nullable=True)
    is_active = db.Column(db.Boolean, default=True, index=True)
    is_featured = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    inventory = db.relationship("Inventory", backref="product", uselist=False, cascade="all, delete-orphan")
    order_items = db.relationship("OrderItem", backref="product", lazy="dynamic")

    @property
    def current_stock(self):
        return self.inventory.current_stock if self.inventory else 0

    @property
    def is_in_stock(self):
        return self.current_stock > 0

    @property
    def is_low_stock(self):
        if not self.inventory:
            return False
        return self.inventory.current_stock <= self.inventory.low_stock_threshold

    def to_dict(self):
        return {
            "id": self.id,
            "category_id": self.category_id,
            "category_name": self.category_rel.name if self.category_rel else None,
            "name": self.name,
            "sku": self.sku,
            "description": self.description,
            "unit": self.unit,
            "price": float(self.price) if self.price is not None else 0.0,
            "cost_price": float(self.cost_price) if self.cost_price is not None else 0.0,
            "image_url": self.image_url,
            "is_active": self.is_active,
            "is_featured": self.is_featured,
            "current_stock": self.current_stock,
            "is_in_stock": self.is_in_stock,
            "is_low_stock": self.is_low_stock,
            "low_stock_threshold": self.inventory.low_stock_threshold if self.inventory else 10,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }
