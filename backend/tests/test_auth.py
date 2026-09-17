def test_register_success(client):
    res = client.post("/api/auth/register", json={
        "first_name": "Alice",
        "last_name": "Moraa",
        "email": "alice@test.com",
        "password": "Password123!",
        "role": "CUSTOMER",
        "phone": "+254700112233",
        "city": "Nairobi"
    })
    assert res.status_code == 201
    json_data = res.get_json()
    assert json_data["success"] is True
    assert "token" in json_data["data"]
    assert json_data["data"]["user"]["email"] == "alice@test.com"


def test_register_duplicate_email_fails(client):
    res = client.post("/api/auth/register", json={
        "first_name": "Duplicate",
        "last_name": "User",
        "email": "admin@test.com",
        "password": "Password123!"
    })
    assert res.status_code == 400
    assert res.get_json()["success"] is False


def test_login_success(client):
    res = client.post("/api/auth/login", json={
        "email": "customer@test.com",
        "password": "Password123!"
    })
    assert res.status_code == 200
    json_data = res.get_json()
    assert json_data["success"] is True
    assert json_data["data"]["user"]["role"] == "CUSTOMER"


def test_login_invalid_credentials(client):
    res = client.post("/api/auth/login", json={
        "email": "customer@test.com",
        "password": "WrongPassword!"
    })
    assert res.status_code == 401
    assert res.get_json()["success"] is False


def test_get_current_user_profile(client, customer_token):
    res = client.get("/api/auth/me", headers={"Authorization": f"Bearer {customer_token}"})
    assert res.status_code == 200
    assert res.get_json()["data"]["email"] == "customer@test.com"
