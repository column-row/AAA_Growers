import re


def validate_email(email: str) -> bool:
    """Validate email address format."""
    if not email or not isinstance(email, str):
        return False
    pattern = r"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$"
    return bool(re.match(pattern, email.strip()))


def validate_phone(phone: str) -> bool:
    """Validate telephone number format."""
    if not phone or not isinstance(phone, str):
        return False
    clean = re.sub(r"[\s\-\(\)\+]", "", phone)
    return len(clean) >= 9 and clean.isdigit()


def validate_required_fields(data: dict, required_fields: list) -> list:
    """Check for missing or empty required fields in payload dictionary."""
    missing = []
    if not data or not isinstance(data, dict):
        return required_fields
    for field in required_fields:
        val = data.get(field)
        if val is None or (isinstance(val, str) and not val.strip()):
            missing.append(field)
    return missing
