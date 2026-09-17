from datetime import datetime, date, timedelta
from sqlalchemy import func, case
from app.models import db, Order, OrderItem, Payment, Product, Category, Inventory, TrainingSession, TrainingBooking, Certification, User, Farmer


class ReportService:

    @staticmethod
    def get_dashboard_summary():
        """Aggregated KPIs for Administrator Dashboard."""
        total_customers = User.query.filter_by(role="CUSTOMER").count()
        total_farmers = Farmer.query.count()
        total_products = Product.query.count()
        total_orders = Order.query.count()
        pending_orders = Order.query.filter_by(status="PENDING").count()
        completed_orders = Order.query.filter_by(status="DELIVERED").count()

        # Total revenue from successful payments
        total_sales = db.session.query(func.coalesce(func.sum(Payment.amount), 0.0)).filter(Payment.status == "SUCCESS").scalar()

        upcoming_training = TrainingSession.query.filter(
            TrainingSession.training_date >= date.today(),
            TrainingSession.status == "UPCOMING"
        ).count()

        # Low stock alerts
        inventory_alerts = Inventory.query.filter(Inventory.current_stock <= Inventory.low_stock_threshold).count()

        return {
            "total_customers": total_customers,
            "total_farmers": total_farmers,
            "total_products": total_products,
            "total_orders": total_orders,
            "pending_orders": pending_orders,
            "completed_orders": completed_orders,
            "total_sales": float(total_sales),
            "upcoming_training": upcoming_training,
            "inventory_alerts": inventory_alerts
        }

    @staticmethod
    def get_sales_report(period="monthly", start_date=None, end_date=None):
        """
        Sales analytics supporting daily, weekly, monthly, and annual groupings.
        """
        query = db.session.query(
            func.date(Payment.paid_at).label("day"),
            func.count(Payment.id).label("transactions_count"),
            func.sum(Payment.amount).label("total_revenue")
        ).filter(Payment.status == "SUCCESS")

        if start_date:
            try:
                s_d = datetime.strptime(start_date, "%Y-%m-%d")
                query = query.filter(Payment.paid_at >= s_d)
            except Exception:
                pass

        if end_date:
            try:
                e_d = datetime.strptime(end_date, "%Y-%m-%d") + timedelta(days=1)
                query = query.filter(Payment.paid_at < e_d)
            except Exception:
                pass

        # If no dates provided, use default periods
        today = datetime.utcnow()
        if not start_date and not end_date:
            if period == "daily":
                query = query.filter(Payment.paid_at >= today - timedelta(days=1))
            elif period == "weekly":
                query = query.filter(Payment.paid_at >= today - timedelta(days=7))
            elif period == "monthly":
                query = query.filter(Payment.paid_at >= today - timedelta(days=30))
            elif period == "annual":
                query = query.filter(Payment.paid_at >= today - timedelta(days=365))

        records = query.group_by(func.date(Payment.paid_at)).order_by(func.date(Payment.paid_at).asc()).all()

        total_rev = sum(float(r.total_revenue or 0) for r in records)
        total_txs = sum(int(r.transactions_count or 0) for r in records)

        trend_data = []
        for r in records:
            day_str = r.day.strftime("%Y-%m-%d") if hasattr(r.day, 'strftime') else str(r.day)
            trend_data.append({
                "date": day_str,
                "transactions": int(r.transactions_count),
                "revenue": float(r.total_revenue or 0.0)
            })

        return {
            "period": period,
            "total_revenue": total_rev,
            "total_transactions": total_txs,
            "average_order_value": (total_rev / total_txs) if total_txs > 0 else 0.0,
            "data": trend_data
        }

    @staticmethod
    def get_orders_report(status=None, start_date=None, end_date=None):
        """Order distribution, status breakdown and timeline."""
        query = Order.query

        if status:
            query = query.filter_by(status=status)
        if start_date:
            try:
                s_d = datetime.strptime(start_date, "%Y-%m-%d")
                query = query.filter(Order.placed_at >= s_d)
            except Exception:
                pass
        if end_date:
            try:
                e_d = datetime.strptime(end_date, "%Y-%m-%d") + timedelta(days=1)
                query = query.filter(Order.placed_at < e_d)
            except Exception:
                pass

        orders = query.order_by(Order.placed_at.desc()).all()

        # Status breakdown
        status_counts = {}
        for o in orders:
            status_counts[o.status] = status_counts.get(o.status, 0) + 1

        total_value = sum(float(o.net_amount) for o in orders)

        return {
            "total_orders": len(orders),
            "total_value": total_value,
            "status_distribution": status_counts,
            "orders": [o.to_dict() for o in orders[:100]]
        }

    @staticmethod
    def get_inventory_report():
        """Stock valuation, alerts, and category distributions."""
        inventories = Inventory.query.join(Product).all()

        total_items = len(inventories)
        low_stock_count = sum(1 for i in inventories if i.current_stock <= i.low_stock_threshold)
        out_of_stock_count = sum(1 for i in inventories if i.current_stock <= 0)
        total_valuation = sum(i.current_stock * float(i.product.price) for i in inventories if i.product)

        items_data = [i.to_dict() for i in inventories]

        return {
            "total_inventory_items": total_items,
            "low_stock_count": low_stock_count,
            "out_of_stock_count": out_of_stock_count,
            "total_stock_valuation": total_valuation,
            "items": items_data
        }

    @staticmethod
    def get_training_report():
        """Training programs, farmer attendees, and certification statistics."""
        total_sessions = TrainingSession.query.count()
        upcoming_sessions = TrainingSession.query.filter_by(status="UPCOMING").count()
        completed_sessions = TrainingSession.query.filter_by(status="COMPLETED").count()
        total_bookings = TrainingBooking.query.count()
        total_certifications = Certification.query.count()

        sessions = TrainingSession.query.order_by(TrainingSession.training_date.desc()).all()

        return {
            "total_sessions": total_sessions,
            "upcoming_sessions": upcoming_sessions,
            "completed_sessions": completed_sessions,
            "total_bookings": total_bookings,
            "total_certifications_issued": total_certifications,
            "sessions": [s.to_dict() for s in sessions]
        }
