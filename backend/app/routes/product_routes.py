from flask import Blueprint, request, g
from app.services.product_service import ProductService
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import role_required, UserRoles

product_bp = Blueprint("products", __name__, url_prefix="/api")


@product_bp.route("/products", methods=["GET"])
def get_products():
    category_id = request.args.get("category_id", type=int)
    search = request.args.get("search")
    is_active = request.args.get("is_active", default=None)
    if is_active is not None:
        is_active = is_active.lower() in ("true", "1")
    is_featured = request.args.get("is_featured", default=None)
    if is_featured is not None:
        is_featured = is_featured.lower() in ("true", "1")
    in_stock_only = request.args.get("in_stock_only", "false").lower() in ("true", "1")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = ProductService.get_all_products(
        category_id=category_id,
        search=search,
        is_active=is_active,
        is_featured=is_featured,
        in_stock_only=in_stock_only,
        page=page,
        per_page=per_page
    )
    return paginated_response(items, total, page, per_page, "Products retrieved successfully")


@product_bp.route("/products/<int:product_id>", methods=["GET"])
def get_product(product_id):
    product = ProductService.get_product_by_id(product_id)
    if not product:
        return error_response("Product not found", status_code=404)
    return success_response(product, "Product retrieved successfully")


@product_bp.route("/products", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def create_product():
    data = request.get_json() or {}
    ok, msg, res = ProductService.create_product(data, user_id=g.current_user_id)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@product_bp.route("/products/<int:product_id>", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def update_product(product_id):
    data = request.get_json() or {}
    ok, msg, res = ProductService.update_product(product_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@product_bp.route("/products/<int:product_id>/toggle-status", methods=["PATCH"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def toggle_product_status(product_id):
    ok, msg, res = ProductService.toggle_product_status(product_id)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@product_bp.route("/products/<int:product_id>", methods=["DELETE"])
@role_required(UserRoles.ADMIN)
def delete_product(product_id):
    ok, msg = ProductService.delete_product(product_id)
    if not ok:
        return error_response(msg, status_code=404)
    return success_response(None, msg)


# Category Endpoints
@product_bp.route("/categories", methods=["GET"])
def get_categories():
    categories = ProductService.get_all_categories()
    return success_response(categories, "Categories retrieved successfully")


@product_bp.route("/categories", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def create_category():
    data = request.get_json() or {}
    ok, msg, res = ProductService.create_category(data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@product_bp.route("/categories/<int:category_id>", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def update_category(category_id):
    data = request.get_json() or {}
    ok, msg, res = ProductService.update_category(category_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)
