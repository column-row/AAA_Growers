from datetime import datetime
from flask_jwt_extended import create_access_token
from app.models import db, User, Role, Farmer, Trainer, Driver, Supplier
from app.utils.validators import validate_email, validate_required_fields


class AuthService:

    @staticmethod
    def register_user(data: dict):
        required = ["first_name", "last_name", "email", "password"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        email = data.get("email", "").strip().lower()
        if not validate_email(email):
            return False, "Invalid email address format", None

        if User.query.filter_by(email=email).first():
            return False, "An account with this email already exists", None

        role = data.get("role", "CUSTOMER").strip().upper()
        allowed_roles = [
            "ADMIN", "CUSTOMER", "FARMER", "INVENTORY_MANAGER", "FINANCE_MANAGER",
            "SUPPLIER", "DISPATCH_MANAGER", "SERVICE_MANAGER", "TRAINER", "DRIVER"
        ]
        if role not in allowed_roles:
            role = "CUSTOMER"

        user = User(
            first_name=data.get("first_name", "").strip(),
            last_name=data.get("last_name", "").strip(),
            email=email,
            phone=data.get("phone", "").strip() if data.get("phone") else None,
            role=role,
            status="ACTIVE",
            city=data.get("city", "").strip() if data.get("city") else None,
            address=data.get("address", "").strip() if data.get("address") else None,
            avatar_url=data.get("avatar_url")
        )
        user.set_password(data.get("password"))
        db.session.add(user)
        db.session.flush()

        # If user registered as FARMER, create farmer profile
        if role == "FARMER":
            farmer = Farmer(
                user_id=user.id,
                farm_name=data.get("farm_name", f"{user.first_name}'s Farm"),
                farm_location=data.get("farm_location", data.get("city", "Naivasha")),
                farm_size_acres=data.get("farm_size_acres", 1.0),
                crops_grown=data.get("crops_grown", "Vegetables"),
                farming_experience_years=data.get("farming_experience_years", 1),
                national_id=data.get("national_id")
            )
            db.session.add(farmer)

        # If user registered as TRAINER
        elif role == "TRAINER":
            trainer = Trainer(
                user_id=user.id,
                specialization=data.get("specialization", "Agronomy & Export Standards"),
                qualifications=data.get("qualifications", "BSc Agriculture"),
                bio=data.get("bio", "Agricultural Instructor")
            )
            db.session.add(trainer)

        # If user registered as DRIVER
        elif role == "DRIVER":
            driver = Driver(
                user_id=user.id,
                license_number=data.get("license_number", f"DL-{user.id:05d}"),
                vehicle_registration=data.get("vehicle_registration", "KDC 100A"),
                vehicle_type=data.get("vehicle_type", "Refrigerated Van"),
                is_available=True
            )
            db.session.add(driver)

        # If user registered as SUPPLIER
        elif role == "SUPPLIER":
            supplier = Supplier(
                name=data.get("company_name", f"{user.full_name} Agro-Supplies"),
                contact_person=user.full_name,
                email=email,
                phone=user.phone or "N/A",
                address=user.address or user.city or "Nairobi",
                supply_category=data.get("supply_category", "Seeds & Agrochemicals"),
                status="ACTIVE"
            )
            db.session.add(supplier)

        db.session.commit()

        token = create_access_token(
            identity={"id": user.id, "email": user.email, "role": user.role}
        )

        return True, "Registration successful", {
            "token": token,
            "user": user.to_dict()
        }

    @staticmethod
    def login_user(email: str, password: str):
        if not email or not password:
            return False, "Email and password are required", None

        email = email.strip().lower()
        user = User.query.filter_by(email=email).first()

        if not user or not user.check_password(password):
            return False, "Invalid email or password", None

        if user.status != "ACTIVE":
            return False, f"Account is {user.status.lower()}. Please contact system administration.", None

        token = create_access_token(
            identity={"id": user.id, "email": user.email, "role": user.role}
        )

        return True, "Login successful", {
            "token": token,
            "user": user.to_dict()
        }

    @staticmethod
    def get_user_profile(user_id: int):
        user = User.query.get(user_id)
        if not user:
            return False, "User not found", None
        return True, "Profile retrieved successfully", user.to_dict()

    @staticmethod
    def update_user_profile(user_id: int, data: dict):
        user = User.query.get(user_id)
        if not user:
            return False, "User not found", None

        if "first_name" in data:
            user.first_name = data["first_name"].strip()
        if "last_name" in data:
            user.last_name = data["last_name"].strip()
        if "phone" in data:
            user.phone = data["phone"].strip()
        if "city" in data:
            user.city = data["city"].strip()
        if "address" in data:
            user.address = data["address"].strip()
        if "avatar_url" in data:
            user.avatar_url = data["avatar_url"]

        if user.role == "FARMER" and user.farmer_profile:
            fp = user.farmer_profile
            if "farm_name" in data:
                fp.farm_name = data["farm_name"]
            if "farm_location" in data:
                fp.farm_location = data["farm_location"]
            if "farm_size_acres" in data:
                fp.farm_size_acres = data["farm_size_acres"]
            if "crops_grown" in data:
                fp.crops_grown = data["crops_grown"]
            if "farming_experience_years" in data:
                fp.farming_experience_years = data["farming_experience_years"]

        db.session.commit()
        return True, "Profile updated successfully", user.to_dict()
