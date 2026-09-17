from app.models import db, Supplier
from app.utils.validators import validate_required_fields, validate_email


class SupplierService:

    @staticmethod
    def get_suppliers(category=None, status=None):
        query = Supplier.query
        if category:
            query = query.filter_by(supply_category=category)
        if status:
            query = query.filter_by(status=status)
        suppliers = query.order_by(Supplier.name.asc()).all()
        return [s.to_dict() for s in suppliers]

    @staticmethod
    def get_supplier_by_id(supplier_id: int):
        s = Supplier.query.get(supplier_id)
        return s.to_dict() if s else None

    @staticmethod
    def create_supplier(data: dict):
        required = ["name", "contact_person", "email", "phone", "supply_category"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        email = data.get("email", "").strip().lower()
        if not validate_email(email):
            return False, "Invalid email address format", None

        if Supplier.query.filter_by(email=email).first():
            return False, "Supplier with this email already exists", None

        supplier = Supplier(
            name=data.get("name").strip(),
            contact_person=data.get("contact_person").strip(),
            email=email,
            phone=data.get("phone").strip(),
            address=data.get("address"),
            supply_category=data.get("supply_category").strip(),
            status=data.get("status", "ACTIVE"),
            rating=float(data.get("rating", 5.0))
        )
        db.session.add(supplier)
        db.session.commit()
        return True, "Supplier created successfully", supplier.to_dict()

    @staticmethod
    def update_supplier(supplier_id: int, data: dict):
        supplier = Supplier.query.get(supplier_id)
        if not supplier:
            return False, "Supplier not found", None

        if "name" in data:
            supplier.name = data["name"].strip()
        if "contact_person" in data:
            supplier.contact_person = data["contact_person"].strip()
        if "phone" in data:
            supplier.phone = data["phone"].strip()
        if "address" in data:
            supplier.address = data["address"]
        if "supply_category" in data:
            supplier.supply_category = data["supply_category"].strip()
        if "status" in data:
            supplier.status = data["status"]
        if "rating" in data:
            supplier.rating = float(data["rating"])

        db.session.commit()
        return True, "Supplier updated successfully", supplier.to_dict()
