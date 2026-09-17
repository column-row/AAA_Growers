from app.models import db, Feedback, Contact, Notification
from app.utils.validators import validate_required_fields, validate_email


class FeedbackService:

    @staticmethod
    def submit_feedback(user_id: int, data: dict):
        comment = data.get("comment", "").strip()
        if not comment:
            return False, "Comment is required", None

        rating = int(data.get("rating", 5))
        if rating < 1 or rating > 5:
            return False, "Rating must be between 1 and 5", None

        fb = Feedback(
            user_id=user_id,
            rating=rating,
            category=data.get("category", "General"),
            comment=comment,
            is_reviewed=False
        )
        db.session.add(fb)
        db.session.commit()
        return True, "Feedback submitted successfully. Thank you!", fb.to_dict()

    @staticmethod
    def get_all_feedback(category=None, is_reviewed=None, page=1, per_page=20):
        query = Feedback.query
        if category:
            query = query.filter_by(category=category)
        if is_reviewed is not None:
            query = query.filter_by(is_reviewed=is_reviewed)

        total = query.count()
        feedbacks = query.order_by(Feedback.created_at.desc()).offset((page - 1) * per_page).limit(per_page).all()
        return [f.to_dict() for f in feedbacks], total

    @staticmethod
    def submit_contact(data: dict):
        required = ["full_name", "email", "subject", "message"]
        missing = validate_required_fields(data, required)
        if missing:
            return False, f"Missing required fields: {', '.join(missing)}", None

        email = data.get("email", "").strip().lower()
        if not validate_email(email):
            return False, "Invalid email address format", None

        contact = Contact(
            full_name=data.get("full_name").strip(),
            email=email,
            phone=data.get("phone"),
            subject=data.get("subject").strip(),
            message=data.get("message").strip(),
            status="NEW"
        )
        db.session.add(contact)
        db.session.commit()
        return True, "Your message has been sent to our customer care team.", contact.to_dict()

    @staticmethod
    def get_all_contacts(status=None, page=1, per_page=20):
        query = Contact.query
        if status:
            query = query.filter_by(status=status)

        total = query.count()
        contacts = query.order_by(Contact.created_at.desc()).offset((page - 1) * per_page).limit(per_page).all()
        return [c.to_dict() for c in contacts], total

    @staticmethod
    def update_contact_status(contact_id: int, status: str):
        c = Contact.query.get(contact_id)
        if not c:
            return False, "Contact message not found", None
        c.status = status
        db.session.commit()
        return True, f"Contact status updated to {status}", c.to_dict()

    @staticmethod
    def get_user_notifications(user_id: int, unread_only=False):
        query = Notification.query.filter_by(user_id=user_id)
        if unread_only:
            query = query.filter_by(is_read=False)
        notifications = query.order_by(Notification.created_at.desc()).limit(30).all()
        return [n.to_dict() for n in notifications]

    @staticmethod
    def mark_notification_read(notification_id: int, user_id: int):
        n = Notification.query.filter_by(id=notification_id, user_id=user_id).first()
        if n:
            n.is_read = True
            db.session.commit()
            return True, "Notification marked as read", n.to_dict()
        return False, "Notification not found", None
