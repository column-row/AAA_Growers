from flask import jsonify


def success_response(data=None, message="Operation successful", status_code=200):
    """Format standard API success response."""
    payload = {
        "success": True,
        "message": message,
        "data": data if data is not None else {}
    }
    return jsonify(payload), status_code


def error_response(message="An error occurred", errors=None, status_code=400):
    """Format standard API error response."""
    payload = {
        "success": False,
        "message": message,
        "errors": errors if errors is not None else []
    }
    return jsonify(payload), status_code


def paginated_response(items, total, page, per_page, message="Records retrieved successfully"):
    """Format standard paginated list response."""
    total_pages = (total + per_page - 1) // per_page if per_page > 0 else 1
    payload = {
        "success": True,
        "message": message,
        "data": {
            "items": items,
            "pagination": {
                "total": total,
                "page": page,
                "per_page": per_page,
                "total_pages": total_pages,
                "has_next": page < total_pages,
                "has_prev": page > 1
            }
        }
    }
    return jsonify(payload), 200
