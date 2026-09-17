from flask import Blueprint, request, g
from app.services.feedback_service import FeedbackService
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import auth_required, role_required, UserRoles

feedback_bp = Blueprint("feedback", __name__, url_prefix="/api")


# Customer / Farmer Feedback
@feedback_bp.route("/feedback", methods=["POST"])
@auth_required()
def submit_feedback():
    data = request.get_json() or {}
    ok, msg, res = FeedbackService.submit_feedback(g.current_user_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@feedback_bp.route("/feedback", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.SERVICE_MANAGER)
def get_all_feedback():
    category = request.args.get("category")
    is_reviewed = request.args.get("is_reviewed")
    if is_reviewed is not None:
        is_reviewed = is_reviewed.lower() in ("true", "1")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = FeedbackService.get_all_feedback(category=category, is_reviewed=is_reviewed, page=page, per_page=per_page)
    return paginated_response(items, total, page, per_page, "Feedback records retrieved")


# Contact Us Form (Public + Admin management)
@feedback_bp.route("/contacts", methods=["POST"])
def submit_contact():
    data = request.get_json() or {}
    ok, msg, res = FeedbackService.submit_contact(data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@feedback_bp.route("/contacts", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.SERVICE_MANAGER)
def get_all_contacts():
    status = request.args.get("status")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = FeedbackService.get_all_contacts(status=status, page=page, per_page=per_page)
    return paginated_response(items, total, page, per_page, "Contact inquiries retrieved")


@feedback_bp.route("/contacts/<int:contact_id>/status", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.SERVICE_MANAGER)
def update_contact_status(contact_id):
    data = request.get_json() or {}
    status = data.get("status")
    if not status:
        return error_response("Status is required", status_code=400)

    ok, msg, res = FeedbackService.update_contact_status(contact_id, status)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


# Notifications
@feedback_bp.route("/notifications", methods=["GET"])
@auth_required()
def get_notifications():
    unread_only = request.args.get("unread_only", "false").lower() in ("true", "1")
    notifications = FeedbackService.get_user_notifications(g.current_user_id, unread_only=unread_only)
    return success_response(notifications, "Notifications retrieved")


@feedback_bp.route("/notifications/<int:notification_id>/read", methods=["PUT"])
@auth_required()
def mark_read(notification_id):
    ok, msg, res = FeedbackService.mark_notification_read(notification_id, g.current_user_id)
    if not ok:
        return error_response(msg, status_code=404)
    return success_response(res, msg)
