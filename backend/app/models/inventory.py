from datetime import datetime
from sqlalchemy.orm import foreign
from app.models import db


class MovementType:
    INITIAL = "INITIAL"
    PURCHASE_ORDER = "PURCHASE_ORDER"
    SALE_DEDUCTION = "SALE_DEDUCTION"
    RESTOCK = "RESTOCK"
    ADJUSTMENT = "ADJUSTMENT"
    RETURN = "RETURN"
    DAMAGE = "DAMAGE"


class Inventory(db.Model):
    __tablename__ = "inventory"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    product_id = db.Column(db.Integer, db.ForeignKey("products.id", ondelete="CASCADE"), unique=True, nullable=False)
    current_stock = db.Column(db.Integer, nullable=False, default=0, index=True)
    low_stock_threshold = db.Column(db.Integer, nullable=False, default=10, index=True)
    reorder_quantity = db.Column(db.Integer, nullable=False, default=50)
    last_restocked_at = db.Column(db.DateTime, nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    logs = db.relationship(
        "InventoryLog",
        primaryjoin="Inventory.product_id == foreign(InventoryLog.product_id)",
        lazy="dynamic",
        cascade="all, delete-orphan",
        overlaps="product_rel"
    )

    def to_dict(self):
        return {
            "id": self.id,
            "product_id": self.product_id,
            "product_name": self.product.name if self.product else None,
            "product_sku": self.product.sku if self.product else None,
            "category_name": self.product.category_rel.name if self.product and self.product.category_rel else None,
            "current_stock": self.current_stock,
            "low_stock_threshold": self.low_stock_threshold,
            "reorder_quantity": self.reorder_quantity,
            "is_low_stock": self.current_stock <= self.low_stock_threshold,
            "is_out_of_stock": self.current_stock <= 0,
            "last_restocked_at": self.last_restocked_at.isoformat() if self.last_restocked_at else None,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None
        }


class InventoryLog(db.Model):
    __tablename__ = "inventory_logs"

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    product_id = db.Column(db.Integer, db.ForeignKey("products.id", ondelete="CASCADE"), nullable=False, index=True)
    change_quantity = db.Column(db.Integer, nullable=False)  # positive for addition, negative for reduction
    previous_stock = db.Column(db.Integer, nullable=False)
    new_stock = db.Column(db.Integer, nullable=False)
    movement_type = db.Column(db.String(50), nullable=False, index=True)
    reference_id = db.Column(db.String(100), nullable=True)
    notes = db.Column(db.Text, nullable=True)
    created_by = db.Column(db.Integer, db.ForeignKey("users.id", ondelete="SET NULL"), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

    # Relationships
    author = db.relationship("User", foreign_keys=[created_by])
    product_rel = db.relationship("Product", foreign_keys=[product_id], overlaps="logs")

    def to_dict(self):
        return {
            "id": self.id,
            "product_id": self.product_id,
            "product_name": self.product_rel.name if self.product_rel else None,
            "change_quantity": self.change_quantity,
            "previous_stock": self.previous_stock,
            "new_stock": self.new_stock,
            "movement_type": self.movement_type,
            "reference_id": self.reference_id,
            "notes": self.notes,
            "created_by": self.created_by,
            "creator_name": self.author.full_name if self.author else "System",
            "created_at": self.created_at.isoformat() if self.created_at else None
        }
