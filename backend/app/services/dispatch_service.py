import uuid
from datetime import datetime
from app.models import db, Dispatch, Driver, Order, OrderStatus, DispatchStatus, Notification
from app.utils.validators import validate_required_fields


class DispatchService:

    @staticmethod
    def get_dispatches(driver_id=None, status=None, page=1, per_page=20):
        query = Dispatch.query
        if driver_id:
            query = query.filter_by(driver_id=driver_id)
        if status:
            query = query.filter_by(status=status)

        total = query.count()
        dispatches = query.order_by(Dispatch.created_at.desc()).offset((page - 1) * per_page).limit(per_page).all()
        return [d.to_dict() for d in dispatches], total

    @staticmethod
    def get_dispatch_by_id(dispatch_id: int):
        d = Dispatch.query.get(dispatch_id)
        return d.to_dict() if d else None

    @staticmethod
    def create_dispatch(order_id: int, driver_id=None, delivery_address=None, notes=None):
        order = Order.query.get(order_id)
        if not order:
            return False, "Order not found", None

        existing = Dispatch.query.filter_by(order_id=order_id).first()
        if existing:
            if driver_id:
                existing.driver_id = driver_id
                existing.status = DispatchStatus.ASSIGNED
                db.session.commit()
            return True, "Dispatch record exists", existing.to_dict()

        dsp_number = f"DSP-{datetime.utcnow().strftime('%Y%m%d')}-{order_id:04d}"
        address = delivery_address or f"{order.delivery_address}, {order.delivery_city}"

        dispatch = Dispatch(
            dispatch_number=dsp_number,
            order_id=order_id,
            driver_id=driver_id,
            delivery_address=address,
            status=DispatchStatus.ASSIGNED if driver_id else DispatchStatus.PENDING,
            tracking_notes=notes or "Dispatch registered for fulfilment"
        )
        db.session.add(dispatch)
        db.session.commit()
        return True, "Dispatch created successfully", dispatch.to_dict()

    @staticmethod
    def assign_driver(dispatch_id: int, driver_id: int):
        dispatch = Dispatch.query.get(dispatch_id)
        if not dispatch:
            return False, "Dispatch not found", None

        driver = Driver.query.get(driver_id)
        if not driver:
            return False, "Driver not found", None

        dispatch.driver_id = driver.id
        dispatch.status = DispatchStatus.ASSIGNED
        driver.is_available = False

        if dispatch.order:
            notif = Notification(
                user_id=dispatch.order.customer_id,
                title="Driver Assigned",
                message=f"Driver {driver.user.full_name} ({driver.vehicle_registration}) has been assigned to your order.",
                type="DISPATCH"
            )
            db.session.add(notif)

        db.session.commit()
        return True, f"Driver {driver.user.full_name} assigned successfully", dispatch.to_dict()

    @staticmethod
    def update_dispatch_status(dispatch_id: int, new_status: str, notes=None, current_location=None):
        dispatch = Dispatch.query.get(dispatch_id)
        if not dispatch:
            return False, "Dispatch not found", None

        if new_status not in DispatchStatus.ALL:
            return False, f"Invalid dispatch status '{new_status}'", None

        dispatch.status = new_status
        if notes:
            dispatch.tracking_notes = notes

        if new_status == DispatchStatus.IN_TRANSIT:
            dispatch.dispatch_date = datetime.utcnow()
            if dispatch.order:
                dispatch.order.status = OrderStatus.DISPATCHED

        elif new_status == DispatchStatus.DELIVERED:
            dispatch.delivery_date = datetime.utcnow()
            if dispatch.order:
                dispatch.order.status = OrderStatus.DELIVERED
            if dispatch.driver_rel:
                dispatch.driver_rel.is_available = True

        if current_location and dispatch.driver_rel:
            dispatch.driver_rel.current_location = current_location

        if dispatch.order:
            notif = Notification(
                user_id=dispatch.order.customer_id,
                title=f"Delivery Update: {new_status}",
                message=f"Your delivery for order {dispatch.order.order_number} is now {new_status}.",
                type="DISPATCH"
            )
            db.session.add(notif)

        db.session.commit()
        return True, f"Dispatch updated to {new_status}", dispatch.to_dict()

    # Drivers list & CRUD
    @staticmethod
    def get_drivers(available_only=False):
        query = Driver.query
        if available_only:
            query = query.filter_by(is_available=True)
        drivers = query.all()
        return [d.to_dict() for d in drivers]
