from flask import Blueprint, request
from app.services.report_service import ReportService
from app.utils.responses import success_response
from app.utils.decorators import role_required, UserRoles

report_bp = Blueprint("reports", __name__, url_prefix="/api/reports")


@report_bp.route("/dashboard", methods=["GET"])
@role_required(
    UserRoles.ADMIN, UserRoles.FINANCE_MANAGER, UserRoles.INVENTORY_MANAGER,
    UserRoles.DISPATCH_MANAGER, UserRoles.SERVICE_MANAGER, UserRoles.TRAINER
)
def get_dashboard_kpis():
    data = ReportService.get_dashboard_summary()
    return success_response(data, "Dashboard summary retrieved successfully")


@report_bp.route("/sales", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.FINANCE_MANAGER)
def get_sales_report():
    period = request.args.get("period", "monthly")
    start_date = request.args.get("start_date")
    end_date = request.args.get("end_date")

    data = ReportService.get_sales_report(period=period, start_date=start_date, end_date=end_date)
    return success_response(data, "Sales report generated successfully")


@report_bp.route("/orders", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.FINANCE_MANAGER, UserRoles.DISPATCH_MANAGER)
def get_orders_report():
    status = request.args.get("status")
    start_date = request.args.get("start_date")
    end_date = request.args.get("end_date")

    data = ReportService.get_orders_report(status=status, start_date=start_date, end_date=end_date)
    return success_response(data, "Orders report generated successfully")


@report_bp.route("/inventory", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.INVENTORY_MANAGER, UserRoles.FINANCE_MANAGER)
def get_inventory_report():
    data = ReportService.get_inventory_report()
    return success_response(data, "Inventory report generated successfully")


@report_bp.route("/training", methods=["GET"])
@role_required(UserRoles.ADMIN, UserRoles.TRAINER)
def get_training_report():
    data = ReportService.get_training_report()
    return success_response(data, "Training report generated successfully")
