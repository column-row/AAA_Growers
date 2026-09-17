from flask import Blueprint, request, g
from app.services.payment_service import PaymentService
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import auth_required, role_required, UserRoles

payment_bp = Blueprint("payments", __name__, url_prefix="/api/payments")


@payment_bp.route("", methods=["POST"])
@auth_required()
def process_payment():
    data = request.get_json() or {}
    ok, msg, res = PaymentService.process_payment(g.current_user_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@payment_bp.route("", methods=["GET"])
@auth_required()
def get_payments():
    user_id = g.current_user_id
    user_role = g.current_user_role
    status = request.args.get("status")
    order_id = request.args.get("order_id", type=int)
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    filter_user_id = None if user_role in [UserRoles.ADMIN, UserRoles.FINANCE_MANAGER] else user_id
    if not filter_user_id and request.args.get("user_id"):
        filter_user_id = request.args.get("user_id", type=int)

    items, total = PaymentService.get_payments(
        user_id=filter_user_id,
        order_id=order_id,
        status=status,
        page=page,
        per_page=per_page
    )
    return paginated_response(items, total, page, per_page, "Payments retrieved successfully")


@payment_bp.route("/<int:payment_id>", methods=["GET"])
@auth_required()
def get_payment(payment_id):
    payment = PaymentService.get_payment_by_id(payment_id)
    if not payment:
        return error_response("Payment not found", status_code=404)
    return success_response(payment, "Payment retrieved successfully")


@payment_bp.route("/<int:payment_id>/refund", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.FINANCE_MANAGER)
def refund_payment(payment_id):
    data = request.get_json() or {}
    reason = data.get("reason", "Customer requested refund")
    ok, msg, res = PaymentService.refund_payment(payment_id, reason=reason)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)
