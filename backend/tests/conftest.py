import pytest
from app import create_app
from app.models import db, User, Role, Category, Product, Inventory, Farmer, Trainer, TrainingSession


@pytest.fixture(scope="function")
def app():
    app = create_app("testing")
    with app.app_context():
        db.create_all()

        # Seed roles
        for role_name in ["ADMIN", "CUSTOMER", "FARMER", "INVENTORY_MANAGER", "FINANCE_MANAGER", "TRAINER", "DRIVER"]:
            db.session.add(Role(name=role_name, description=f"{role_name} role"))

        # Seed admin
        admin = User(first_name="Admin", last_name="User", email="admin@test.com", role="ADMIN", status="ACTIVE")
        admin.set_password("Password123!")
        db.session.add(admin)

        # Seed customer
        customer = User(first_name="John", last_name="Customer", email="customer@test.com", role="CUSTOMER", status="ACTIVE")
        customer.set_password("Password123!")
        db.session.add(customer)

        # Seed farmer
        farmer_u = User(first_name="Mary", last_name="Farmer", email="farmer@test.com", role="FARMER", status="ACTIVE")
        farmer_u.set_password("Password123!")
        db.session.add(farmer_u)
        db.session.flush()

        farmer = Farmer(user_id=farmer_u.id, farm_name="Naivasha Organic", farm_location="Naivasha", farm_size_acres=5.0)
        db.session.add(farmer)

        # Seed trainer
        trainer_u = User(first_name="Dr.", last_name="Trainer", email="trainer@test.com", role="TRAINER", status="ACTIVE")
        trainer_u.set_password("Password123!")
        db.session.add(trainer_u)
        db.session.flush()

        trainer = Trainer(user_id=trainer_u.id, specialization="Agronomy")
        db.session.add(trainer)

        # Seed Category & Product
        cat = Category(name="Vegetables", slug="vegetables")
        db.session.add(cat)
        db.session.flush()

        prod = Product(
            category_id=cat.id,
            name="Fine French Beans",
            sku="VEG-TEST-001",
            price=250.00,
            unit="kg",
            is_active=True
        )
        db.session.add(prod)
        db.session.flush()

        inv = Inventory(product_id=prod.id, current_stock=100, low_stock_threshold=15)
        db.session.add(inv)

        db.session.commit()

        yield app

        db.session.remove()
        db.drop_all()


@pytest.fixture(scope="function")
def client(app):
    return app.test_client()


def get_token(client, email, password):
    resp = client.post("/api/auth/login", json={"email": email, "password": password})
    return resp.get_json()["data"]["token"]


@pytest.fixture(scope="function")
def admin_token(client):
    return get_token(client, "admin@test.com", "Password123!")


@pytest.fixture(scope="function")
def customer_token(client):
    return get_token(client, "customer@test.com", "Password123!")


@pytest.fixture(scope="function")
def farmer_token(client):
    return get_token(client, "farmer@test.com", "Password123!")


@pytest.fixture(scope="function")
def trainer_token(client):
    return get_token(client, "trainer@test.com", "Password123!")
