import re
from app.models import db, Product, Category, Inventory, InventoryLog
from app.utils.validators import validate_required_fields


def slugify(text: str) -> str:
    text = text.lower().strip()
    text = re.sub(r"[^\w\s-]", "", text)
    return re.sub(r"[\s_-]+", "-", text)


class ProductService:

    @staticmethod
    def get_all_products(category_id=None, search=None, is_active=None, is_featured=None, in_stock_only=False, page=1, per_page=20):
        query = Product.query

        if category_id:
            query = query.filter(Product.category_id == category_id)

        if search:
            term = f"%{search.strip()}%"
            query = query.filter(
                (Product.name.ilike(term)) |
                (Product.sku.ilike(term)) |
                (Product.description.ilike(term))
            )

        if is_active is not None:
            query = query.filter(Product.is_active == is_active)

        if is_featured is not None:
            query = query.filter(Product.is_featured == is_featured)

        if in_stock_only:
            query = query.join(Inventory).filter(Inventory.current_stock > 0)

        total = query.count()
        products = query.order_by(Product.id.desc()).offset((page - 1) * per_page).limit(per_page).all()

        return [p.to_dict() for p in products], total

    @staticmethod
    def get_product_by_id(product_id: int):
        product = Product.query.get(product_id)
        if not product:
            return None
        return product.to_dict()

    @staticmethod
    def create_product(data: dict, user_id=None):
        required = ["name", "sku", "price"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        sku = data.get("sku", "").strip().upper()
        if Product.query.filter_by(sku=sku).first():
            return False, f"A product with SKU '{sku}' already exists", None

        product = Product(
            category_id=data.get("category_id"),
            name=data.get("name", "").strip(),
            sku=sku,
            description=data.get("description", "").strip() if data.get("description") else None,
            unit=data.get("unit", "kg").strip(),
            price=float(data.get("price", 0.0)),
            cost_price=float(data.get("cost_price", 0.0)) if data.get("cost_price") is not None else 0.0,
            image_url=data.get("image_url"),
            is_active=data.get("is_active", True),
            is_featured=data.get("is_featured", False)
        )
        db.session.add(product)
        db.session.flush()

        # Initialize inventory
        initial_stock = int(data.get("initial_stock", 0))
        threshold = int(data.get("low_stock_threshold", 10))
        reorder = int(data.get("reorder_quantity", 50))

        inventory = Inventory(
            product_id=product.id,
            current_stock=initial_stock,
            low_stock_threshold=threshold,
            reorder_quantity=reorder
        )
        db.session.add(inventory)

        if initial_stock > 0:
            log = InventoryLog(
                product_id=product.id,
                change_quantity=initial_stock,
                previous_stock=0,
                new_stock=initial_stock,
                movement_type="INITIAL",
                reference_id=f"INIT-PROD-{product.id}",
                notes="Initial stock recorded during product creation",
                created_by=user_id
            )
            db.session.add(log)

        db.session.commit()
        return True, "Product created successfully", product.to_dict()

    @staticmethod
    def update_product(product_id: int, data: dict):
        product = Product.query.get(product_id)
        if not product:
            return False, "Product not found", None

        if "name" in data:
            product.name = data["name"].strip()
        if "category_id" in data:
            product.category_id = data["category_id"]
        if "description" in data:
            product.description = data["description"]
        if "unit" in data:
            product.unit = data["unit"]
        if "price" in data:
            product.price = float(data["price"])
        if "cost_price" in data:
            product.cost_price = float(data["cost_price"])
        if "image_url" in data:
            product.image_url = data["image_url"]
        if "is_active" in data:
            product.is_active = bool(data["is_active"])
        if "is_featured" in data:
            product.is_featured = bool(data["is_featured"])

        if "low_stock_threshold" in data and product.inventory:
            product.inventory.low_stock_threshold = int(data["low_stock_threshold"])

        db.session.commit()
        return True, "Product updated successfully", product.to_dict()

    @staticmethod
    def toggle_product_status(product_id: int):
        product = Product.query.get(product_id)
        if not product:
            return False, "Product not found", None
        product.is_active = not product.is_active
        db.session.commit()
        status_str = "activated" if product.is_active else "deactivated"
        return True, f"Product {status_str} successfully", product.to_dict()

    @staticmethod
    def delete_product(product_id: int):
        product = Product.query.get(product_id)
        if not product:
            return False, "Product not found"
        db.session.delete(product)
        db.session.commit()
        return True, "Product deleted successfully"

    # Category operations
    @staticmethod
    def get_all_categories():
        categories = Category.query.order_by(Category.name.asc()).all()
        return [c.to_dict() for c in categories]

    @staticmethod
    def create_category(data: dict):
        name = data.get("name", "").strip()
        if not name:
            return False, "Category name is required", None

        slug = data.get("slug") or slugify(name)
        if Category.query.filter_by(slug=slug).first():
            return False, f"Category with slug '{slug}' already exists", None

        category = Category(
            name=name,
            slug=slug,
            description=data.get("description"),
            image_url=data.get("image_url"),
            is_active=data.get("is_active", True)
        )
        db.session.add(category)
        db.session.commit()
        return True, "Category created successfully", category.to_dict()

    @staticmethod
    def update_category(category_id: int, data: dict):
        category = Category.query.get(category_id)
        if not category:
            return False, "Category not found", None

        if "name" in data:
            category.name = data["name"].strip()
        if "description" in data:
            category.description = data["description"]
        if "image_url" in data:
            category.image_url = data["image_url"]
        if "is_active" in data:
            category.is_active = bool(data["is_active"])

        db.session.commit()
        return True, "Category updated successfully", category.to_dict()
