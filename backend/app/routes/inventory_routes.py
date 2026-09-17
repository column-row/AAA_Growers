from flask import Blueprint, request, g
from app.services.inventory_service import InventoryService
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import role_required, UserRoles

inventory_bp = Blueprint("inventory", __name__, url_prefix="/api/inventory")


@inventory_bp.route("", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER, UserRoles.FINANCE_MANAGER)
def get_inventory():
    low_stock_only = request.args.get("low_stock_only", "false").lower() in ("true", "1")
    out_of_stock_only = request.args.get("out_of_stock_only", "false").lower() in ("true", "1")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = InventoryService.get_inventory_status(
        low_stock_only=low_stock_only,
        out_of_stock_only=out_of_stock_only,
        page=page,
        per_page=per_page
    )
    return paginated_response(items, total, page, per_page, "Inventory status retrieved successfully")


@inventory_bp.route("/<int:product_id>", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def get_product_inventory(product_id):
    inv = InventoryService.get_inventory_by_product_id(product_id)
    if not inv:
        return error_response("Inventory record not found", status_code=404)
    return success_response(inv, "Inventory details retrieved")


@inventory_bp.route("/<int:product_id>", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def update_inventory(product_id):
    """Adjust inventory stock and/or thresholds."""
    data = request.get_json() or {}

    # If quantity change requested
    if "change_quantity" in data:
        change_qty = int(data.get("change_quantity", 0))
        movement_type = data.get("movement_type", "ADJUSTMENT")
        notes = data.get("notes")
        ref_id = data.get("reference_id")

        ok, msg, res = InventoryService.adjust_stock(
            product_id=product_id,
            quantity_change=change_qty,
            movement_type=movement_type,
            notes=notes,
            reference_id=ref_id,
            user_id=g.current_user_id
        )
        if not ok:
            return error_response(msg, status_code=400)
        return success_response(res, msg)

    # Threshold update
    low_stock = data.get("low_stock_threshold")
    reorder = data.get("reorder_quantity")
    ok, msg, res = InventoryService.update_thresholds(product_id, low_stock, reorder)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@inventory_bp.route("/logs", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def get_inventory_logs():
    product_id = request.args.get("product_id", type=int)
    movement_type = request.args.get("movement_type")
    limit = request.args.get("limit", 50, type=int)

    logs = InventoryService.get_inventory_logs(product_id=product_id, movement_type=movement_type, limit=limit)
    return success_response(logs, "Inventory logs retrieved successfully")
