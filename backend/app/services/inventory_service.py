from datetime import datetime
from app.models import db, Inventory, InventoryLog, Product


class InventoryService:

    @staticmethod
    def get_inventory_status(low_stock_only=False, out_of_stock_only=False, page=1, per_page=20):
        query = Inventory.query.join(Product)

        if low_stock_only:
            query = query.filter(Inventory.current_stock <= Inventory.low_stock_threshold)

        if out_of_stock_only:
            query = query.filter(Inventory.current_stock <= 0)

        total = query.count()
        items = query.order_by(Inventory.current_stock.asc()).offset((page - 1) * per_page).limit(per_page).all()
        return [i.to_dict() for i in items], total

    @staticmethod
    def get_inventory_by_product_id(product_id: int):
        inv = Inventory.query.filter_by(product_id=product_id).first()
        return inv.to_dict() if inv else None

    @staticmethod
    def adjust_stock(product_id: int, quantity_change: int, movement_type: str, notes=None, reference_id=None, user_id=None):
        """
        Adjust inventory by adding (positive quantity_change) or reducing (negative quantity_change).
        """
        inv = Inventory.query.filter_by(product_id=product_id).first()
        if not inv:
            # Create inventory record if not found
            inv = Inventory(product_id=product_id, current_stock=0, low_stock_threshold=10)
            db.session.add(inv)
            db.session.flush()

        prev_stock = inv.current_stock
        new_stock = prev_stock + quantity_change

        if new_stock < 0:
            return False, f"Insufficient stock: current stock is {prev_stock}, attempted reduction of {abs(quantity_change)}", None

        inv.current_stock = new_stock
        if quantity_change > 0:
            inv.last_restocked_at = datetime.utcnow()

        log = InventoryLog(
            product_id=product_id,
            change_quantity=quantity_change,
            previous_stock=prev_stock,
            new_stock=new_stock,
            movement_type=movement_type,
            reference_id=reference_id,
            notes=notes,
            created_by=user_id
        )
        db.session.add(log)
        db.session.commit()

        return True, "Stock adjusted successfully", {
            "inventory": inv.to_dict(),
            "log": log.to_dict()
        }

    @staticmethod
    def update_thresholds(product_id: int, low_stock_threshold=None, reorder_quantity=None):
        inv = Inventory.query.filter_by(product_id=product_id).first()
        if not inv:
            return False, "Inventory record not found", None

        if low_stock_threshold is not None:
            inv.low_stock_threshold = int(low_stock_threshold)
        if reorder_quantity is not None:
            inv.reorder_quantity = int(reorder_quantity)

        db.session.commit()
        return True, "Thresholds updated successfully", inv.to_dict()

    @staticmethod
    def get_inventory_logs(product_id=None, movement_type=None, limit=50):
        query = InventoryLog.query
        if product_id:
            query = query.filter_by(product_id=product_id)
        if movement_type:
            query = query.filter_by(movement_type=movement_type)

        logs = query.order_by(InventoryLog.created_at.desc()).limit(limit).all()
        return [l.to_dict() for l in logs]
