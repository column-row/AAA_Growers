import os
from flask import Flask, jsonify
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from app.config import config_by_name
from app.models import db
from app.utils.responses import error_response

# Import Blueprints
from app.routes.auth_routes import auth_bp
from app.routes.product_routes import product_bp
from app.routes.order_routes import order_bp
from app.routes.payment_routes import payment_bp
from app.routes.inventory_routes import inventory_bp
from app.routes.training_routes import training_bp
from app.routes.certification_routes import certification_bp
from app.routes.dispatch_routes import dispatch_bp
from app.routes.supplier_routes import supplier_bp
from app.routes.feedback_routes import feedback_bp
from app.routes.user_routes import user_bp
from app.routes.report_routes import report_bp

jwt = JWTManager()


def create_app(config_name=None):
    """Application factory for Flask app."""
    if not config_name:
        config_name = os.getenv("FLASK_ENV", "development")

    app = Flask(__name__)
    app.config.from_object(config_by_name.get(config_name, config_by_name["default"]))

    # Initialize extensions
    db.init_app(app)
    jwt.init_app(app)
    CORS(app, resources={r"/api/*": {"origins": "*"}})

    # JWT Error handlers
    @jwt.unauthorized_loader
    def unauthorized_callback(callback):
        return error_response("Missing or invalid Authorization Header", status_code=401)

    @jwt.invalid_token_loader
    def invalid_token_callback(callback):
        return error_response("Invalid JWT token", status_code=401)

    @jwt.expired_token_loader
    def expired_token_callback(jwt_header, jwt_payload):
        return error_response("Token has expired. Please log in again.", status_code=401)

    # Global HTTP Error Handlers
    @app.errorhandler(404)
    def handle_404(e):
        return error_response("Endpoint or resource not found", status_code=404)

    @app.errorhandler(405)
    def handle_405(e):
        return error_response("HTTP method not allowed for this route", status_code=405)

    @app.errorhandler(500)
    def handle_500(e):
        return error_response(f"Internal server error: {str(e)}", status_code=500)

    # Health check route
    @app.route("/api/health", methods=["GET"])
    def health_check():
        return jsonify({
            "status": "healthy",
            "service": "AAA Growers API",
            "version": "1.0.0"
        }), 200

    # Register all blueprints
    app.register_blueprint(auth_bp)
    app.register_blueprint(product_bp)
    app.register_blueprint(order_bp)
    app.register_blueprint(payment_bp)
    app.register_blueprint(inventory_bp)
    app.register_blueprint(training_bp)
    app.register_blueprint(certification_bp)
    app.register_blueprint(dispatch_bp)
    app.register_blueprint(supplier_bp)
    app.register_blueprint(feedback_bp)
    app.register_blueprint(user_bp)
    app.register_blueprint(report_bp)

    return app
