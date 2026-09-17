from flask import Blueprint, request, g
from app.services.training_service import TrainingService
from app.services.certification_service import CertificationService
from app.models import User, Farmer
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import auth_required, role_required, UserRoles

training_bp = Blueprint("trainings", __name__, url_prefix="/api/trainings")


@training_bp.route("", methods=["GET"])
def get_sessions():
    status = request.args.get("status")
    category = request.args.get("category")
    upcoming_only = request.args.get("upcoming_only", "false").lower() in ("true", "1")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = TrainingService.get_sessions(
        status=status,
        category=category,
        upcoming_only=upcoming_only,
        page=page,
        per_page=per_page
    )
    return paginated_response(items, total, page, per_page, "Training sessions retrieved successfully")


@training_bp.route("/<int:session_id>", methods=["GET"])
def get_session(session_id):
    session = TrainingService.get_session_by_id(session_id)
    if not session:
        return error_response("Training session not found", status_code=404)
    return success_response(session, "Training session retrieved")


@training_bp.route("", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def create_session():
    data = request.get_json() or {}
    ok, msg, res = TrainingService.create_session(data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@training_bp.route("/<int:session_id>", methods=["PUT"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def update_session(session_id):
    data = request.get_json() or {}
    ok, msg, res = TrainingService.update_session(session_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@training_bp.route("/<int:session_id>/book", methods=["POST"])
@auth_required()
def book_training(session_id):
    user_id = g.current_user_id
    user = User.query.get(user_id)
    if not user or not user.farmer_profile:
        return error_response("Only registered farmers can book training sessions", status_code=403)

    data = request.get_json() or {}
    notes = data.get("notes")
    ok, msg, res = TrainingService.book_training(session_id, user.farmer_profile.id, notes=notes)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@training_bp.route("/my-bookings", methods=["GET"])
@auth_required()
def get_my_bookings():
    user_id = g.current_user_id
    user = User.query.get(user_id)
    if not user or not user.farmer_profile:
        return error_response("No farmer profile associated with this account", status_code=404)

    bookings = TrainingService.get_farmer_bookings(user.farmer_profile.id)
    return success_response(bookings, "Farmer bookings retrieved successfully")


@training_bp.route("/<int:session_id>/bookings", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def get_session_bookings(session_id):
    bookings = TrainingService.get_session_bookings(session_id)
    return success_response(bookings, "Session bookings retrieved")


@training_bp.route("/bookings/<int:booking_id>/cancel", methods=["POST"])
@auth_required()
def cancel_booking(booking_id):
    user = User.query.get(g.current_user_id)
    farmer_id = user.farmer_profile.id if user and user.farmer_profile else None

    # If admin/trainer, bypass farmer_id check
    if g.current_user_role in [UserRoles.ADMIN, UserRoles.TRAINER]:
        farmer_id = None

    ok, msg, res = TrainingService.cancel_booking(booking_id, farmer_id=farmer_id)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg)


@training_bp.route("/bookings/<int:booking_id>/complete", methods=["POST"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def complete_booking_and_certify(booking_id):
    """Marks farmer training attendance as completed and generates official certificate."""
    ok, msg, res = CertificationService.generate_certificate_for_booking(booking_id)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, "Training marked completed and Certificate issued successfully!")
