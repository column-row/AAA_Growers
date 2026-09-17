def test_dashboard_kpis(client, admin_token):
    res = client.get("/api/reports/dashboard", headers={"Authorization": f"Bearer {admin_token}"})
    assert res.status_code == 200
    data = res.get_json()["data"]
    assert "total_customers" in data
    assert "total_farmers" in data
    assert "total_products" in data
    assert "total_orders" in data
    assert "total_sales" in data
    assert "inventory_alerts" in data


def test_sales_report(client, admin_token):
    res = client.get("/api/reports/sales?period=monthly", headers={"Authorization": f"Bearer {admin_token}"})
    assert res.status_code == 200
    data = res.get_json()["data"]
    assert data["period"] == "monthly"
    assert "total_revenue" in data
    assert "data" in data


def test_inventory_report(client, admin_token):
    res = client.get("/api/reports/inventory", headers={"Authorization": f"Bearer {admin_token}"})
    assert res.status_code == 200
    data = res.get_json()["data"]
    assert "total_inventory_items" in data
    assert "total_stock_valuation" in data
