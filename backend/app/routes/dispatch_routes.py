from flask import Blueprint, request, g
from app.services.dispatch_service import DispatchService
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import role_required, auth_required, UserRoles

dispatch_bp = Blueprint("dispatches", __name__, url_prefix="/api")


@dispatch_bp.route("/dispatches", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.DISPATCH_MANAGER, UserRoles.DRIVER)
def get_dispatches():
    status = request.args.get("status")
    driver_id = request.args.get("driver_id", type=int)
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = DispatchService.get_dispatches(driver_id=driver_id, status=status, page=page, per_page=per_page)
    return paginated_response(items, total, page, per_page, "Dispatches retrieved successfully")


@dispatch_bp.route("/dispatches/<int:dispatch_id>", methods=["GET"])
@auth_required()
def get_dispatch(dispatch_id):
    dispatch = DispatchService.get_dispatch_by_id(dispatch_id)
    if not dispatch:
        return error_response("Dispatch record not found", status_code=404)
    return success_response(dispatch, "Dispatch details retrieved")


@dispatch_bp.route("/dispatches", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.DISPATCH_MANAGER)
def create_dispatch():
    data = request.get_json() or {}
    order_id = data.get("order_id")
    if not order_id:
        return error_response("Order ID is required", status_code=400)

    ok, msg, res = DispatchService.create_dispatch(
        order_id=order_id,
        driver_id=data.get("driver_id"),
        delivery_address=data.get("delivery_address"),
        notes=data.get("notes")
    )
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@dispatch_bp.route("/dispatches/<int:dispatch_id>/assign-driver", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.DISPATCH_MANAGER)
def assign_driver(dispatch_id):
    data = request.get_json() or {}
    driver_id = data.get("driver_id")
    if not driver_id:
        return error_response("Driver ID is required", status_code=400)

    ok, msg, res = DispatchService.assign_driver(dispatch_id, driver_id)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@dispatch_bp.route("/dispatches/<int:dispatch_id>/status", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.DISPATCH_MANAGER, UserRoles.DRIVER)
def update_dispatch_status(dispatch_id):
    data = request.get_json() or {}
    status = data.get("status")
    notes = data.get("notes")
    current_location = data.get("current_location")

    if not status:
        return error_response("Status is required", status_code=400)

    ok, msg, res = DispatchService.update_dispatch_status(
        dispatch_id, status, notes=notes, current_location=current_location
    )
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@dispatch_bp.route("/drivers", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.DISPATCH_MANAGER)
def get_drivers():
    available_only = request.args.get("available_only", "false").lower() in ("true", "1")
    drivers = DispatchService.get_drivers(available_only=available_only)
    return success_response(drivers, "Drivers list retrieved")
