from datetime import datetime, date, time
from app.models import db, TrainingSession, TrainingBooking, SessionStatus, BookingStatus, Farmer, Trainer, Notification
from app.utils.validators import validate_required_fields


class TrainingService:

    @staticmethod
    def get_sessions(status=None, category=None, upcoming_only=False, page=1, per_page=20):
        query = TrainingSession.query

        if status:
            query = query.filter_by(status=status)
        if category:
            query = query.filter_by(category=category)
        if upcoming_only:
            query = query.filter(TrainingSession.training_date >= date.today())

        total = query.count()
        sessions = query.order_by(TrainingSession.training_date.asc()).offset((page - 1) * per_page).limit(per_page).all()
        return [s.to_dict() for s in sessions], total

    @staticmethod
    def get_session_by_id(session_id: int):
        session = TrainingSession.query.get(session_id)
        if not session:
            return None
        return session.to_dict()

    @staticmethod
    def create_session(data: dict):
        required = ["trainer_id", "title", "description", "training_date", "start_time", "end_time", "location"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        trainer = Trainer.query.get(data.get("trainer_id"))
        if not trainer:
            return False, "Selected trainer not found", None

        # Parse date and times
        try:
            t_date = datetime.strptime(data.get("training_date"), "%Y-%m-%d").date()
            s_time = datetime.strptime(data.get("start_time"), "%H:%M").time() if len(data.get("start_time")) <= 5 else datetime.strptime(data.get("start_time"), "%H:%M:%S").time()
            e_time = datetime.strptime(data.get("end_time"), "%H:%M").time() if len(data.get("end_time")) <= 5 else datetime.strptime(data.get("end_time"), "%H:%M:%S").time()
        except Exception as e:
            return False, f"Invalid date or time format. Expected YYYY-MM-DD and HH:MM. Error: {str(e)}", None

        session = TrainingSession(
            trainer_id=trainer.id,
            title=data.get("title").strip(),
            description=data.get("description").strip(),
            category=data.get("category", "General Farming"),
            training_date=t_date,
            start_time=s_time,
            end_time=e_time,
            location=data.get("location").strip(),
            capacity=int(data.get("capacity", 30)),
            status=data.get("status", SessionStatus.UPCOMING),
            materials_url=data.get("materials_url")
        )
        db.session.add(session)
        db.session.commit()

        return True, "Training session created successfully", session.to_dict()

    @staticmethod
    def update_session(session_id: int, data: dict):
        session = TrainingSession.query.get(session_id)
        if not session:
            return False, "Training session not found", None

        if "title" in data:
            session.title = data["title"].strip()
        if "description" in data:
            session.description = data["description"].strip()
        if "category" in data:
            session.category = data["category"].strip()
        if "location" in data:
            session.location = data["location"].strip()
        if "capacity" in data:
            session.capacity = int(data["capacity"])
        if "status" in data:
            session.status = data["status"]
        if "materials_url" in data:
            session.materials_url = data["materials_url"]
        if "trainer_id" in data:
            session.trainer_id = int(data["trainer_id"])

        if "training_date" in data:
            session.training_date = datetime.strptime(data["training_date"], "%Y-%m-%d").date()
        if "start_time" in data:
            session.start_time = datetime.strptime(data["start_time"], "%H:%M").time() if len(data["start_time"]) <= 5 else datetime.strptime(data["start_time"], "%H:%M:%S").time()
        if "end_time" in data:
            session.end_time = datetime.strptime(data["end_time"], "%H:%M").time() if len(data["end_time"]) <= 5 else datetime.strptime(data["end_time"], "%H:%M:%S").time()

        db.session.commit()
        return True, "Training session updated successfully", session.to_dict()

    @staticmethod
    def book_training(training_id: int, farmer_id: int, notes=None):
        session = TrainingSession.query.get(training_id)
        if not session:
            return False, "Training session not found", None

        farmer = Farmer.query.get(farmer_id)
        if not farmer:
            return False, "Farmer profile not found", None

        # Check existing booking
        existing = TrainingBooking.query.filter_by(training_id=training_id, farmer_id=farmer_id).first()
        if existing and existing.status in ["BOOKED", "ATTENDED", "COMPLETED"]:
            return False, f"Farmer is already enrolled in this session with status: {existing.status}", None

        # Check capacity constraint
        if session.is_full:
            return False, f"Session is fully booked ({session.booked_count}/{session.capacity} slots filled)", None

        if session.status not in [SessionStatus.UPCOMING, SessionStatus.ONGOING]:
            return False, f"Cannot book session with status '{session.status}'", None

        if existing:
            existing.status = BookingStatus.BOOKED
            existing.booking_date = datetime.utcnow()
            existing.notes = notes
            booking = existing
        else:
            booking = TrainingBooking(
                training_id=training_id,
                farmer_id=farmer_id,
                status=BookingStatus.BOOKED,
                notes=notes
            )
            db.session.add(booking)

        # Notify farmer
        notif = Notification(
            user_id=farmer.user_id,
            title="Training Booked Successfully",
            message=f"You are confirmed for '{session.title}' on {session.training_date.strftime('%d %b %Y')} at {session.location}.",
            type="TRAINING"
        )
        db.session.add(notif)

        db.session.commit()
        return True, "Training session booked successfully", booking.to_dict()

    @staticmethod
    def cancel_booking(booking_id: int, farmer_id=None):
        booking = TrainingBooking.query.get(booking_id)
        if not booking:
            return False, "Booking not found", None

        if farmer_id and booking.farmer_id != farmer_id:
            return False, "Unauthorized to cancel this booking", None

        booking.status = BookingStatus.CANCELLED
        db.session.commit()
        return True, "Booking cancelled successfully", booking.to_dict()

    @staticmethod
    def get_farmer_bookings(farmer_id: int):
        bookings = TrainingBooking.query.filter_by(farmer_id=farmer_id).order_by(TrainingBooking.created_at.desc()).all()
        return [b.to_dict() for b in bookings]

    @staticmethod
    def get_session_bookings(training_id: int):
        bookings = TrainingBooking.query.filter_by(training_id=training_id).all()
        return [b.to_dict() for b in bookings]
