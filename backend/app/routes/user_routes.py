from flask import Blueprint, request
from app.models import db, User, Role, Farmer
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import role_required, UserRoles
from app.services.auth_service import AuthService

user_bp = Blueprint("users", __name__, url_prefix="/api")


@user_bp.route("/users", methods=["GET"])
@role_required(UserRoles.ADMIN)
def get_users():
    role = request.args.get("role")
    status = request.args.get("status")
    search = request.args.get("search")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    query = User.query
    if role:
        query = query.filter_by(role=role)
    if status:
        query = query.filter_by(status=status)
    if search:
        term = f"%{search.strip()}%"
        query = query.filter(
            (User.first_name.ilike(term)) |
            (User.last_name.ilike(term)) |
            (User.email.ilike(term)) |
            (User.phone.ilike(term))
        )

    total = query.count()
    users = query.order_by(User.id.desc()).offset((page - 1) * per_page).limit(per_page).all()
    return paginated_response([u.to_dict() for u in users], total, page, per_page, "Users retrieved")


@user_bp.route("/users", methods=["POST"])
@role_required(UserRoles.ADMIN)
def create_user():
    data = request.get_json() or {}
    ok, msg, res = AuthService.register_user(data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@user_bp.route("/users/<int:user_id>", methods=["PUT"])
@role_required(UserRoles.ADMIN)
def update_user(user_id):
    user = User.query.get(user_id)
    if not user:
        return error_response("User not found", status_code=404)

    data = request.get_json() or {}
    if "first_name" in data:
        user.first_name = data["first_name"].strip()
    if "last_name" in data:
        user.last_name = data["last_name"].strip()
    if "phone" in data:
        user.phone = data["phone"].strip()
    if "role" in data:
        user.role = data["role"].strip().upper()
    if "status" in data:
        user.status = data["status"].strip().upper()
    if "city" in data:
        user.city = data["city"].strip()
    if "address" in data:
        user.address = data["address"].strip()

    if "password" in data and data["password"]:
        user.set_password(data["password"])

    db.session.commit()
    return success_response(user.to_dict(), "User updated successfully")


@user_bp.route("/users/<int:user_id>", methods=["DELETE"])
@role_required(UserRoles.ADMIN)
def delete_user(user_id):
    user = User.query.get(user_id)
    if not user:
        return error_response("User not found", status_code=404)
    db.session.delete(user)
    db.session.commit()
    return success_response(None, "User deleted successfully")


# Farmer Specific Directory
@user_bp.route("/farmers", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def get_farmers():
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    query = Farmer.query
    total = query.count()
    farmers = query.offset((page - 1) * per_page).limit(per_page).all()
    return paginated_response([f.to_dict() for f in farmers], total, page, per_page, "Farmers retrieved")


@user_bp.route("/farmers/<int:farmer_id>", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def get_farmer(farmer_id):
    f = Farmer.query.get(farmer_id)
    if not f:
        return error_response("Farmer not found", status_code=404)
    return success_response(f.to_dict(), "Farmer details retrieved")
