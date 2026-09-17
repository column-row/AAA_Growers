def test_get_products_list(client):
    res = client.get("/api/products")
    assert res.status_code == 200
    json_data = res.get_json()
    assert json_data["success"] is True
    assert len(json_data["data"]["items"]) >= 1


def test_create_product_admin(client, admin_token):
    res = client.post("/api/products", json={
        "name": "Organic Avocados",
        "sku": "FRT-AVO-999",
        "unit": "box",
        "price": 800.0,
        "initial_stock": 50,
        "low_stock_threshold": 10
    }, headers={"Authorization": f"Bearer {admin_token}"})
    assert res.status_code == 201
    assert res.get_json()["data"]["sku"] == "FRT-AVO-999"
    assert res.get_json()["data"]["current_stock"] == 50


def test_create_product_forbidden_for_customer(client, customer_token):
    res = client.post("/api/products", json={
        "name": "Unauthorized Product",
        "sku": "UNAUTH-001",
        "price": 100.0
    }, headers={"Authorization": f"Bearer {customer_token}"})
    assert res.status_code == 403


def test_toggle_product_status(client, admin_token):
    res = client.patch("/api/products/1/toggle-status", headers={"Authorization": f"Bearer {admin_token}"})
    assert res.status_code == 200
    assert res.get_json()["data"]["is_active"] is False
