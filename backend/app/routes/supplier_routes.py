from flask import Blueprint, request
from app.services.supplier_service import SupplierService
from app.utils.responses import success_response, error_response
from app.utils.decorators import role_required, UserRoles

supplier_bp = Blueprint("suppliers", __name__, url_prefix="/api/suppliers")


@supplier_bp.route("", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER, UserRoles.SUPPLIER)
def get_suppliers():
    category = request.args.get("category")
    status = request.args.get("status")
    suppliers = SupplierService.get_suppliers(category=category, status=status)
    return success_response(suppliers, "Suppliers retrieved successfully")


@supplier_bp.route("/<int:supplier_id>", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def get_supplier(supplier_id):
    s = SupplierService.get_supplier_by_id(supplier_id)
    if not s:
        return error_response("Supplier not found", status_code=404)
    return success_response(s, "Supplier details retrieved")


@supplier_bp.route("", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def create_supplier():
    data = request.get_json() or {}
    ok, msg, res = SupplierService.create_supplier(data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@supplier_bp.route("/<int:supplier_id>", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER)
def update_supplier(supplier_id):
    data = request.get_json() or {}
    ok, msg, res = SupplierService.update_supplier(supplier_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)
