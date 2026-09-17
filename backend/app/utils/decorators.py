from functools import wraps
from flask import g
from flask_jwt_extended import verify_jwt_in_request, get_jwt_identity
from app.utils.responses import error_response


class UserRoles:
    ADMIN = "ADMIN"
    CUSTOMER = "CUSTOMER"
    FARMER = "FARMER"
    INVENTORY_MANAGER = "INVENTORY_MANAGER"
    FINANCE_MANAGER = "FINANCE_MANAGER"
    SUPPLIER = "SUPPLIER"
    DISPATCH_MANAGER = "DISPATCH_MANAGER"
    SERVICE_MANAGER = "SERVICE_MANAGER"
    TRAINER = "TRAINER"
    DRIVER = "DRIVER"

    ALL_STAFF = [
        ADMIN,
        INVENTORY_MANAGER,
        FINANCE_MANAGER,
        DISPATCH_MANAGER,
        SERVICE_MANAGER,
        TRAINER
    ]


def auth_required():
    """Verify that a valid JWT token is present in the request."""
    def decorator(fn):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            try:
                verify_jwt_in_request()
                identity = get_jwt_identity()
                g.current_user_id = identity.get("id") if isinstance(identity, dict) else identity
                g.current_user_role = identity.get("role") if isinstance(identity, dict) else None
                g.current_user_email = identity.get("email") if isinstance(identity, dict) else None
                return fn(*args, **kwargs)
            except Exception as e:
                return error_response(f"Authentication required: {str(e)}", status_code=401)
        return wrapper
    return decorator


def role_required(*allowed_roles):
    """Enforce that the authenticated user possesses at least one of the allowed roles."""
    def decorator(fn):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            try:
                verify_jwt_in_request()
                identity = get_jwt_identity()
                user_role = identity.get("role") if isinstance(identity, dict) else None
                user_id = identity.get("id") if isinstance(identity, dict) else identity
                g.current_user_id = user_id
                g.current_user_role = user_role
                g.current_user_email = identity.get("email") if isinstance(identity, dict) else None

                # ADMIN always has superuser bypass access
                if user_role == UserRoles.ADMIN:
                    return fn(*args, **kwargs)

                if user_role not in allowed_roles:
                    return error_response(
                        f"Access denied: Role '{user_role}' is not authorized for this action",
                        status_code=403
                    )
                return fn(*args, **kwargs)
            except Exception as e:
                return error_response(f"Authentication failed: {str(e)}", status_code=401)
        return wrapper
    return decorator
