def test_create_order_reduces_inventory(client, customer_token):
    # Product ID 1 has 100 stock
    res = client.post("/api/orders", json={
        "items": [
            {"product_id": 1, "quantity": 10}
        ],
        "delivery_address": "Kileleshwa, Nairobi",
        "delivery_city": "Nairobi",
        "delivery_phone": "+254711111111"
    }, headers={"Authorization": f"Bearer {customer_token}"})

    assert res.status_code == 201
    order_data = res.get_json()["data"]
    assert order_data["total_amount"] == 2500.0
    assert order_data["status"] == "PENDING"

    # Verify inventory was reduced from 100 to 90
    prod_res = client.get("/api/products/1")
    assert prod_res.get_json()["data"]["current_stock"] == 90


def test_order_exceeds_stock_fails(client, customer_token):
    # Attempt ordering 500 items when only 100 available
    res = client.post("/api/orders", json={
        "items": [
            {"product_id": 1, "quantity": 500}
        ],
        "delivery_address": "Nairobi",
        "delivery_city": "Nairobi",
        "delivery_phone": "+254711111111"
    }, headers={"Authorization": f"Bearer {customer_token}"})

    assert res.status_code == 400
    assert "Insufficient stock" in res.get_json()["message"]


def test_mock_payment_flow(client, customer_token):
    # 1. Create order
    order_res = client.post("/api/orders", json={
        "items": [{"product_id": 1, "quantity": 2}],
        "delivery_address": "Westlands",
        "delivery_city": "Nairobi",
        "delivery_phone": "+254711111111"
    }, headers={"Authorization": f"Bearer {customer_token}"})
    order_id = order_res.get_json()["data"]["id"]
    net_amt = order_res.get_json()["data"]["net_amount"]

    # 2. Process MPESA Payment
    pay_res = client.post("/api/payments", json={
        "order_id": order_id,
        "amount": net_amt,
        "payment_method": "MPESA"
    }, headers={"Authorization": f"Bearer {customer_token}"})

    assert pay_res.status_code == 201
    pay_data = pay_res.get_json()["data"]
    assert pay_data["status"] == "SUCCESS"
    assert pay_data["transaction_reference"].startswith("MPESA-")

    # 3. Verify Order updated to PAID
    chk_order = client.get(f"/api/orders/{order_id}", headers={"Authorization": f"Bearer {customer_token}"})
    assert chk_order.get_json()["data"]["payment_status"] == "PAID"
    assert chk_order.get_json()["data"]["status"] == "PAID"
