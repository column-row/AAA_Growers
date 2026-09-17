from flask import Blueprint, request, g
from app.services.auth_service import AuthService
from app.utils.responses import success_response, error_response
from app.utils.decorators import auth_required

auth_bp = Blueprint("auth", __name__, url_prefix="/api/auth")


@auth_bp.route("/register", methods=["POST"])
def register():
    data = request.get_json() or {}
    ok, msg, res = AuthService.register_user(data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=201)


@auth_bp.route("/login", methods=["POST"])
def login():
    data = request.get_json() or {}
    email = data.get("email")
    password = data.get("password")
    ok, msg, res = AuthService.login_user(email, password)
    if not ok:
        return error_response(msg, status_code=401)
    return success_response(res, msg, status_code=200)


@auth_bp.route("/me", methods=["GET"])
@auth_required()
def get_current_user():
    ok, msg, res = AuthService.get_user_profile(g.current_user_id)
    if not ok:
        return error_response(msg, status_code=404)
    return success_response(res, msg, status_code=200)


@auth_bp.route("/profile", methods=["PUT"])
@auth_required()
def update_profile():
    data = request.get_json() or {}
    ok, msg, res = AuthService.update_user_profile(g.current_user_id, data)
    if not ok:
        return error_response(msg, status_code=400)
    return success_response(res, msg, status_code=200)
