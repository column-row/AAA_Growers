from flask import Blueprint, request, g
from app.services.certification_service import CertificationService
from app.models import User
from app.utils.responses import success_response, error_response, paginated_response
from app.utils.decorators import auth_required, UserRoles

certification_bp = Blueprint("certifications", __name__, url_prefix="/api/certifications")


@certification_bp.route("", methods=["GET"])
@auth_required()
def get_certifications():
    user = User.query.get(g.current_user_id)
    farmer_id = None

    if g.current_user_role == UserRoles.FARMER:
        if not user or not user.farmer_profile:
            return error_response("No farmer profile found", status_code=404)
        farmer_id = user.farmer_profile.id
    elif request.args.get("farmer_id"):
        farmer_id = request.args.get("farmer_id", type=int)

    status = request.args.get("status")
    page = request.args.get("page", 1, type=int)
    per_page = request.args.get("per_page", 20, type=int)

    items, total = CertificationService.get_certifications(
        farmer_id=farmer_id,
        status=status,
        page=page,
        per_page=per_page
    )
    return paginated_response(items, total, page, per_page, "Certificates retrieved successfully")


@certification_bp.route("/<int:cert_id>", methods=["GET"])
@auth_required()
def get_certificate(cert_id):
    cert = CertificationService.get_certificate_by_id(cert_id)
    if not cert:
        return error_response("Certificate not found", status_code=404)
    return success_response(cert, "Certificate details retrieved")


@certification_bp.route("/verify/<string:identifier>", methods=["GET"])
def verify_certificate(identifier):
    """Public certificate verification endpoint."""
    ok, msg, res = CertificationService.verify_certificate(identifier)
    if not ok:
        return error_response(msg, status_code=404)
    return success_response(res, msg)
