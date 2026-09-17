from flask import Blueprint, request, g
from app.services.order_service import OrderService
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import auth_required, role_required, UserRoles

order_bp = Blueprint("orders", __name__, url_prefix="/api/orders")


@order_bp.route("", methods=["POST"])
@auth_required()
def create_order():
    data = request.get_json() or {}
    ok, msg, res = OrderService.create_order(g.current_user_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@order_bp.route("", methods=["GET"])
@auth_required()
def get_orders():
    user_id = g.current_user_id
    user_role = g.current_user_role
    status = request.args.get("status")
    payment_status = request.args.get("payment_status")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    # If customer, filter only by their customer_id
    customer_id = None if user_role in [
        UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER, UserRoles.FINANCE_MANAGER, UserRoles.DISPATCH_MANAGER
    ] else user_id

    # Admin can filter by specific customer
    if not customer_id and request.args.get("customer_id"):
        customer_id = request.args.get("customer_id", type=int)

    items, total = OrderService.get_orders(
        customer_id=customer_id,
        status=status,
        payment_status=payment_status,
        page=page,
        per_page=per_page
    )
    return paginated_response(items, total, page, per_page, "Orders retrieved successfully")


@order_bp.route("/<int:order_id>", methods=["GET"])
@auth_required()
def get_order(order_id):
    order = OrderService.get_order_by_id(order_id, user_id=g.current_user_id, role=g.current_user_role)
    if not order:
        return error_response("Order not found or access unauthorized", status_code=404)
    return success_response(order, "Order retrieved successfully")


@order_bp.route("/<int:order_id>/status", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.DISPATCH_MANAGER, UserRoles.INVENTORY_MANAGER, UserRoles.FINANCE_MANAGER)
def update_status(order_id):
    data = request.get_json() or {}
    new_status = data.get("status")
    notes = data.get("notes")
    if not new_status:
        return error_response("Status is required", status_code=400)

    ok, msg, res = OrderService.update_order_status(order_id, new_status, notes=notes)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@order_bp.route("/<int:order_id>/cancel", methods=["POST"])
@auth_required()
def cancel_order(order_id):
    ok, msg, res = OrderService.update_order_status(order_id, "CANCELLED", notes="Cancelled by user/admin")
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)
