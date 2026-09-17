import hashlib
import uuid
from datetime import datetime, date, timedelta
from app.models import db, Certification, CertificateStatus, TrainingBooking, BookingStatus, Farmer, TrainingSession, Notification


class CertificationService:

    @staticmethod
    def generate_certificate_for_booking(booking_id: int, expiry_days=730):
        """
        Marks booking as COMPLETED and automatically generates an official verified Certificate.
        """
        booking = TrainingBooking.query.get(booking_id)
        if not booking:
            return False, "Booking not found", None

        booking.status = BookingStatus.COMPLETED

        farmer = booking.farmer
        session = booking.session

        # Check if already issued
        existing_cert = Certification.query.filter_by(farmer_id=farmer.id, training_id=session.id).first()
        if existing_cert:
            db.session.commit()
            return True, "Certificate already generated", existing_cert.to_dict()

        # Generate unique certificate number & cryptographic verification hash
        cert_number = f"AAA-CERT-{datetime.utcnow().year}-{uuid.uuid4().hex[:6].upper()}"
        hash_input = f"{cert_number}:{farmer.id}:{session.id}:{datetime.utcnow().isoformat()}"
        verification_hash = hashlib.sha256(hash_input.encode('utf-8')).hexdigest()

        cert = Certification(
            certificate_number=cert_number,
            farmer_id=farmer.id,
            training_id=session.id,
            issue_date=date.today(),
            expiry_date=date.today() + timedelta(days=expiry_days),
            title=f"Certificate of Agronomic Competency: {session.title}",
            verification_hash=verification_hash,
            status=CertificateStatus.ACTIVE
        )
        db.session.add(cert)

        # Notify Farmer
        notif = Notification(
            user_id=farmer.user_id,
            title="Certificate of Completion Issued",
            message=f"Congratulations! Your certificate ({cert.certificate_number}) for '{session.title}' is now available.",
            type="TRAINING"
        )
        db.session.add(notif)

        db.session.commit()
        return True, "Certificate generated successfully", cert.to_dict()

    @staticmethod
    def get_certifications(farmer_id=None, status=None, page=1, per_page=20):
        query = Certification.query
        if farmer_id:
            query = query.filter_by(farmer_id=farmer_id)
        if status:
            query = query.filter_by(status=status)

        total = query.count()
        certs = query.order_by(Certification.issue_date.desc()).offset((page - 1) * per_page).limit(per_page).all()
        return [c.to_dict() for c in certs], total

    @staticmethod
    def get_certificate_by_id(cert_id: int):
        cert = Certification.query.get(cert_id)
        return cert.to_dict() if cert else None

    @staticmethod
    def verify_certificate(certificate_number_or_hash: str):
        search_val = certificate_number_or_hash.strip()
        cert = Certification.query.filter(
            (Certification.certificate_number == search_val) |
            (Certification.verification_hash == search_val)
        ).first()

        if not cert:
            return False, "Certificate not found or invalid", None

        return True, "Certificate is authentic and valid", cert.to_dict()
