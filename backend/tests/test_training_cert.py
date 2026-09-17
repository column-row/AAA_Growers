from datetime import date, timedelta


def test_create_and_book_training_session(client, trainer_token, farmer_token):
    # 1. Trainer creates session
    session_res = client.post("/api/trainings", json={
        "trainer_id": 1,
        "title": "Bio-Fertilizer Workshop",
        "description": "Practical composting and liquid bio-fertilizer formulation",
        "category": "Organic Inputs",
        "training_date": (date.today() + timedelta(days=7)).strftime("%Y-%m-%d"),
        "start_time": "09:00",
        "end_time": "13:00",
        "location": "Naivasha Training Field",
        "capacity": 2
    }, headers={"Authorization": f"Bearer {trainer_token}"})

    assert session_res.status_code == 201
    session_id = session_res.get_json()["data"]["id"]

    # 2. Farmer books session
    book_res = client.post(f"/api/trainings/{session_id}/book", json={
        "notes": "Looking forward to learning composting"
    }, headers={"Authorization": f"Bearer {farmer_token}"})

    assert book_res.status_code == 201
    assert book_res.get_json()["data"]["status"] == "BOOKED"
    booking_id = book_res.get_json()["data"]["id"]

    # 3. Trainer marks completed and issues certificate
    complete_res = client.post(f"/api/trainings/bookings/{booking_id}/complete", headers={"Authorization": f"Bearer {trainer_token}"})
    assert complete_res.status_code == 200
    cert_data = complete_res.get_json()["data"]
    assert cert_data["certificate_number"].startswith("AAA-CERT-")
    assert cert_data["status"] == "ACTIVE"
    assert "verification_hash" in cert_data

    # 4. Verify certificate public endpoint
    verify_res = client.get(f"/api/certifications/verify/{cert_data['certificate_number']}")
    assert verify_res.status_code == 200
    assert verify_res.get_json()["success"] is True
