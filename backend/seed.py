"""
AAA Growers - Database Seed Script
Populates the database with initial roles, sample users for all 10 user roles,
categories, products, inventory, sample orders, training sessions, bookings,
certifications, suppliers, drivers, dispatches, and feedback.
"""
from datetime import datetime, date, time, timedelta
from app import create_app
from app.models import (
    db, Role, User, Category, Product, Inventory, InventoryLog,
    Order, OrderItem, Payment, Farmer, Trainer, TrainingSession,
    TrainingBooking, Certification, Supplier, Driver, Dispatch,
    Feedback, Contact, Notification
)


def seed_database():
    app = create_app("development")
    with app.app_context():
        print("Creating all database tables...")
        db.create_all()

        # 1. Seed Roles
        roles_data = [
            ("ADMIN", "System Administrator with full management access"),
            ("CUSTOMER", "Produce Buyer / E-Commerce Customer"),
            ("FARMER", "Registered Outgrower & Agricultural Producer"),
            ("INVENTORY_MANAGER", "Warehouse Stock and Inventory Controller"),
            ("FINANCE_MANAGER", "Financial auditor, payments and revenue controller"),
            ("SUPPLIER", "Agricultural input supplier (seeds, fertilizer, equipment)"),
            ("DISPATCH_MANAGER", "Logistics, fleet coordinator and dispatch supervisor"),
            ("SERVICE_MANAGER", "Customer support and quality control officer"),
            ("TRAINER", "Agricultural extension officer and training instructor"),
            ("DRIVER", "Logistics delivery driver and route courier"),
        ]
        for name, desc in roles_data:
            if not Role.query.filter_by(name=name).first():
                db.session.add(Role(name=name, description=desc))
        db.session.commit()
        print("[OK] Roles seeded.")

        # 2. Seed Users across all 10 roles (Password: Password123! for all)
        users_data = [
            ("Admin", "System", "admin@aaagrowers.co.ke", "+254700000001", "ADMIN", "Nairobi", "AAA Growers HQ"),
            ("John", "Kamau", "customer@aaagrowers.co.ke", "+254711111111", "CUSTOMER", "Nairobi", "Westlands, Parklands Med Plaza"),
            ("Mary", "Wambui", "farmer@aaagrowers.co.ke", "+254722222222", "FARMER", "Naivasha", "South Lake Road, Farm Block 4"),
            ("Peter", "Ochieng", "inventory@aaagrowers.co.ke", "+254733333333", "INVENTORY_MANAGER", "Nairobi", "Embakasi Logistics Center"),
            ("Sarah", "Mutua", "finance@aaagrowers.co.ke", "+254744444444", "FINANCE_MANAGER", "Nairobi", "Finance Wing, AAA Tower"),
            ("GreenCrop", "AgroSupplies", "supplier@aaagrowers.co.ke", "+254755555555", "SUPPLIER", "Nakuru", "Industrial Area, Gate 12"),
            ("David", "Kariuki", "dispatch@aaagrowers.co.ke", "+254766666666", "DISPATCH_MANAGER", "Nairobi", "JKIA Cargo Hub"),
            ("Grace", "Akinyi", "service@aaagrowers.co.ke", "+254777777777", "SERVICE_MANAGER", "Nairobi", "Customer Experience Hub"),
            ("Dr. Samuel", "Kipchoge", "trainer@aaagrowers.co.ke", "+254788888888", "TRAINER", "Eldoret", "Agronomy Research Station"),
            ("James", "Mwangi", "driver@aaagrowers.co.ke", "+254799999999", "DRIVER", "Nairobi", "Fleet Station 1"),
        ]

        user_map = {}
        for fname, lname, email, phone, role, city, addr in users_data:
            u = User.query.filter_by(email=email).first()
            if not u:
                u = User(
                    first_name=fname,
                    last_name=lname,
                    email=email,
                    phone=phone,
                    role=role,
                    status="ACTIVE",
                    city=city,
                    address=addr
                )
                u.set_password("Password123!")
                db.session.add(u)
                db.session.flush()
            user_map[role] = u
        db.session.commit()
        print("[OK] Users seeded (10 user roles).")

        # Profiles
        farmer_u = user_map["FARMER"]
        if not Farmer.query.filter_by(user_id=farmer_u.id).first():
            farmer = Farmer(
                user_id=farmer_u.id,
                farm_name="Wambui Green Meadows Farm",
                farm_location="Naivasha, South Lake Ward",
                farm_size_acres=12.5,
                crops_grown="French Beans, Snow Peas, Rosemary, Avocado",
                farming_experience_years=7,
                national_id="29482716"
            )
            db.session.add(farmer)

        trainer_u = user_map["TRAINER"]
        if not Trainer.query.filter_by(user_id=trainer_u.id).first():
            trainer = Trainer(
                user_id=trainer_u.id,
                specialization="Export Agronomy & GlobalG.A.P. Standards",
                qualifications="PhD Horticulture (UoN), Certified Lead Auditor GlobalG.A.P.",
                bio="Over 15 years experience coaching outgrowers in EU export quality and integrated pest management."
            )
            db.session.add(trainer)

        driver_u = user_map["DRIVER"]
        if not Driver.query.filter_by(user_id=driver_u.id).first():
            driver = Driver(
                user_id=driver_u.id,
                license_number="DL-KEN-99824",
                vehicle_registration="KDC 482B",
                vehicle_type="Refrigerated Isuzu 3-Ton Truck",
                is_available=True,
                current_location="Nairobi Dispatch Center"
            )
            db.session.add(driver)
        db.session.commit()
        print("[OK] Farmer, Trainer, and Driver profiles seeded.")

        # 3. Seed Categories
        categories_data = [
            ("Fresh Vegetables", "fresh-vegetables", "Farm fresh, crisp and organic export-grade vegetables", "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600"),
            ("Export Cut Flowers", "export-cut-flowers", "Premium quality roses, lilies and fresh cut flowers for global markets", "https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=600"),
            ("Fresh Herbs & Spices", "herbs-spices", "Aromatic rosemary, basil, mint, coriander and organic culinary herbs", "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600"),
            ("Fruits & Berries", "fruits-berries", "Naturally ripened avocados, passion fruits, berries and orchard harvests", "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=600"),
            ("Agricultural Inputs & Seeds", "farm-inputs", "Certified drought-resistant seeds, bio-fertilizers and organic soil nutrients", "https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=600"),
        ]
        cat_map = {}
        for name, slug, desc, img in categories_data:
            c = Category.query.filter_by(slug=slug).first()
            if not c:
                c = Category(name=name, slug=slug, description=desc, image_url=img, is_active=True)
                db.session.add(c)
                db.session.flush()
            cat_map[name] = c
        db.session.commit()
        print("[OK] Categories seeded.")

        # 4. Seed Products & Inventory
        products_data = [
            ("Fresh Vegetables", "Premium French Fine Beans", "VEG-FFB-001", "Hand-picked extra-fine tender green beans grown in rich volcanic soils.", "kg", 280.00, 180.00, "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=600", 450, 50, True),
            ("Fresh Vegetables", "Snow Peas (Mangetout)", "VEG-SNP-002", "Crisp, sweet export quality snow peas rich in vitamins.", "kg", 320.00, 210.00, "https://images.unsplash.com/photo-1515543237350-b3eea1ec8082?w=600", 320, 40, True),
            ("Fresh Vegetables", "Tenderstem Broccoli", "VEG-TSB-003", "Succulent stem broccoli with tender florets, organically nurtured.", "kg", 450.00, 300.00, "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=600", 180, 30, True),
            ("Export Cut Flowers", "Red Naomi Grand Roses", "FLW-RNR-001", "Vibrant deep-red long-stem velvet roses with sublime fragrance (bunch of 20).", "bunch", 1200.00, 750.00, "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600", 250, 30, True),
            ("Export Cut Flowers", "Avalanche White Roses", "FLW-AWR-002", "Pristine white export luxury roses with 60cm stem length (bunch of 20).", "bunch", 1150.00, 720.00, "https://images.unsplash.com/photo-1533038590840-1cde6e668a91?w=600", 190, 25, False),
            ("Fresh Herbs & Spices", "Fresh Sweet Genovese Basil", "HRB-BAS-001", "Lush aromatic basil leaves packaged in nitrogen-sealed freshness pouches.", "pack (250g)", 150.00, 80.00, "https://images.unsplash.com/photo-1608686207856-001b95cf60ca?w=600", 400, 50, True),
            ("Fresh Herbs & Spices", "Fresh Rosemary Stems", "HRB-RSM-002", "Woody organic rosemary stems bursting with aromatic essential oils.", "pack (250g)", 160.00, 85.00, "https://images.unsplash.com/photo-1515586000433-a5bc720b3603?w=600", 350, 40, False),
            ("Fruits & Berries", "Hass Avocado Export Grade A", "FRT-HSA-001", "Rich creamy Hass avocados with high oil content and smooth nutty flavor.", "box (4kg)", 850.00, 520.00, "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600", 500, 60, True),
            ("Fruits & Berries", "Sweet Purple Passion Fruits", "FRT-PSN-002", "High brix tropical purple passion fruits loaded with juicy aromatic pulp.", "kg", 380.00, 230.00, "https://images.unsplash.com/photo-1589135233689-d562f4476629?w=600", 280, 35, True),
            ("Agricultural Inputs & Seeds", "Bio-Organic Foliar Fertilizer 5L", "INP-BFF-001", "Eco-certified microbial foliar fertilizer enhancing chlorophyll synthesis.", "litre", 2400.00, 1600.00, "https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=600", 75, 15, False),
        ]

        prod_map = {}
        for cat_name, pname, sku, desc, unit, price, cost, img, stock, threshold, feat in products_data:
            p = Product.query.filter_by(sku=sku).first()
            if not p:
                cat = cat_map.get(cat_name)
                p = Product(
                    category_id=cat.id if cat else None,
                    name=pname,
                    sku=sku,
                    description=desc,
                    unit=unit,
                    price=price,
                    cost_price=cost,
                    image_url=img,
                    is_active=True,
                    is_featured=feat
                )
                db.session.add(p)
                db.session.flush()

                inv = Inventory(
                    product_id=p.id,
                    current_stock=stock,
                    low_stock_threshold=threshold,
                    reorder_quantity=stock // 2
                )
                db.session.add(inv)

                log = InventoryLog(
                    product_id=p.id,
                    change_quantity=stock,
                    previous_stock=0,
                    new_stock=stock,
                    movement_type="INITIAL",
                    reference_id=f"INIT-{sku}",
                    notes="Initial harvest warehouse intake",
                    created_by=user_map["INVENTORY_MANAGER"].id
                )
                db.session.add(log)
            prod_map[sku] = p
        db.session.commit()
        print("[OK] Products and Inventory stock seeded.")

        # 5. Seed Training Sessions, Bookings & Certifications
        trainer_rec = Trainer.query.first()
        farmer_rec = Farmer.query.first()

        if trainer_rec and not TrainingSession.query.first():
            s1 = TrainingSession(
                trainer_id=trainer_rec.id,
                title="GlobalG.A.P. Compliance & Export Quality Control",
                description="Pesticide residue management, trace-back documentation and hygiene standards for European export markets.",
                category="Quality Standards",
                training_date=date.today() + timedelta(days=5),
                start_time=time(9, 0),
                end_time=time(13, 0),
                location="AAA Training Academy, Naivasha Center",
                capacity=35,
                status="UPCOMING"
            )
            s2 = TrainingSession(
                trainer_id=trainer_rec.id,
                title="Precision Drip Irrigation & Water Conservation",
                description="Modern soil-moisture telemetry, solar automated drip lines and tailored fertigation schedules.",
                category="Irrigation & Water",
                training_date=date.today() + timedelta(days=12),
                start_time=time(10, 0),
                end_time=time(15, 0),
                location="Agronomy Field Lab, Block 7 Naivasha",
                capacity=25,
                status="UPCOMING"
            )
            s3 = TrainingSession(
                trainer_id=trainer_rec.id,
                title="Integrated Pest & Disease Management (IPM)",
                description="Biological control agents, beneficial predator insect habitats and residue-free harvests.",
                category="Pest Management",
                training_date=date.today() - timedelta(days=10),
                start_time=time(9, 0),
                end_time=time(14, 0),
                location="Naivasha Agricultural Hub",
                capacity=30,
                status="COMPLETED"
            )
            db.session.add_all([s1, s2, s3])
            db.session.flush()

            if farmer_rec:
                b1 = TrainingBooking(
                    training_id=s3.id,
                    farmer_id=farmer_rec.id,
                    booking_date=datetime.utcnow() - timedelta(days=14),
                    status="COMPLETED",
                    notes="Attended all practical modules and passed assessment."
                )
                b2 = TrainingBooking(
                    training_id=s1.id,
                    farmer_id=farmer_rec.id,
                    booking_date=datetime.utcnow() - timedelta(days=2),
                    status="BOOKED",
                    notes="Registered for upcoming export standards training."
                )
                db.session.add_all([b1, b2])
                db.session.flush()

                # Generate certificate
                cert = Certification(
                    certificate_number="AAA-CERT-2026-0089",
                    farmer_id=farmer_rec.id,
                    training_id=s3.id,
                    issue_date=date.today() - timedelta(days=10),
                    expiry_date=date.today() + timedelta(days=720),
                    title="Certificate of Competency: Integrated Pest Management (IPM)",
                    verification_hash="e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                    status="ACTIVE"
                )
                db.session.add(cert)
            db.session.commit()
            print("[OK] Training sessions, bookings, and certificates seeded.")

        # 6. Seed Suppliers
        if not Supplier.query.first():
            s_list = [
                Supplier(name="AgroGreen Seeds East Africa", contact_person="Erick Odhiambo", email="erick@agrogreenseeds.co.ke", phone="+254710123456", address="Industrial Area Road A, Nairobi", supply_category="Seeds & Seedlings", status="ACTIVE", rating=4.90),
                Supplier(name="BioSoil Nutrients Ltd", contact_person="Patricia Moraa", email="info@biosoilnutrients.com", phone="+254720987654", address="Nakuru Agro Park, Suite 4", supply_category="Organic Fertilizers", status="ACTIVE", rating=4.85),
                Supplier(name="EcoPack Kenya Limited", contact_person="Kibet Sang", email="sales@ecopackkenya.co.ke", phone="+254730654321", address="Mombasa Road Logistics Center, Nairobi", supply_category="Biodegradable Packaging", status="ACTIVE", rating=4.70),
            ]
            db.session.add_all(s_list)
            db.session.commit()
            print("[OK] Suppliers seeded.")

        # 7. Seed Sample Orders, Payments and Dispatches
        cust_u = user_map["CUSTOMER"]
        driver_rec = Driver.query.first()
        if not Order.query.first() and prod_map:
            # Order 1: Delivered
            o1 = Order(
                order_number="ORD-2026-1001",
                customer_id=cust_u.id,
                total_amount=2800.00,
                discount_amount=0.00,
                shipping_fee=300.00,
                tax_amount=0.00,
                net_amount=3100.00,
                status="DELIVERED",
                payment_status="PAID",
                delivery_address="Parklands Med Plaza, 4th Floor, Suite 402",
                delivery_city="Nairobi",
                delivery_phone="+254711111111",
                notes="Chilled cold-chain handling requested."
            )
            db.session.add(o1)
            db.session.flush()

            oi1 = OrderItem(order_id=o1.id, product_id=prod_map["VEG-FFB-001"].id, product_name=prod_map["VEG-FFB-001"].name, unit_price=280.00, quantity=10, subtotal=2800.00)
            db.session.add(oi1)

            pay1 = Payment(
                order_id=o1.id,
                user_id=cust_u.id,
                amount=3100.00,
                payment_method="MPESA",
                transaction_reference="MPESA-QKW8294719",
                status="SUCCESS",
                payment_provider="MPESA_DAR_EXPRESS",
                paid_at=datetime.utcnow() - timedelta(days=2)
            )
            db.session.add(pay1)

            dsp1 = Dispatch(
                dispatch_number="DSP-2026-0001",
                order_id=o1.id,
                driver_id=driver_rec.id if driver_rec else None,
                delivery_address=o1.delivery_address,
                dispatch_date=datetime.utcnow() - timedelta(days=2),
                delivery_date=datetime.utcnow() - timedelta(days=2) + timedelta(hours=3),
                status="DELIVERED",
                tracking_notes="Delivered to receptionist at Suite 402."
            )
            db.session.add(dsp1)

            # Order 2: In transit
            o2 = Order(
                order_number="ORD-2026-1002",
                customer_id=cust_u.id,
                total_amount=5900.00,
                discount_amount=200.00,
                shipping_fee=0.00,
                tax_amount=0.00,
                net_amount=5700.00,
                status="DISPATCHED",
                payment_status="PAID",
                delivery_address="Westlands, School Lane Villa 12",
                delivery_city="Nairobi",
                delivery_phone="+254711111111",
                notes="Call on arrival."
            )
            db.session.add(o2)
            db.session.flush()

            oi2_1 = OrderItem(order_id=o2.id, product_id=prod_map["FLW-RNR-001"].id, product_name=prod_map["FLW-RNR-001"].name, unit_price=1200.00, quantity=3, subtotal=3600.00)
            oi2_2 = OrderItem(order_id=o2.id, product_id=prod_map["FRT-HSA-001"].id, product_name=prod_map["FRT-HSA-001"].name, unit_price=850.00, quantity=2, subtotal=1700.00)
            oi2_3 = OrderItem(order_id=o2.id, product_id=prod_map["HRB-BAS-001"].id, product_name=prod_map["HRB-BAS-001"].name, unit_price=150.00, quantity=4, subtotal=600.00)
            db.session.add_all([oi2_1, oi2_2, oi2_3])

            pay2 = Payment(
                order_id=o2.id,
                user_id=cust_u.id,
                amount=5700.00,
                payment_method="CARD",
                transaction_reference="CARD-TX-998241940",
                status="SUCCESS",
                payment_provider="AAA_CYBERSOURCE_GATEWAY",
                paid_at=datetime.utcnow() - timedelta(hours=4)
            )
            db.session.add(pay2)

            dsp2 = Dispatch(
                dispatch_number="DSP-2026-0002",
                order_id=o2.id,
                driver_id=driver_rec.id if driver_rec else None,
                delivery_address=o2.delivery_address,
                dispatch_date=datetime.utcnow() - timedelta(hours=1),
                status="IN_TRANSIT",
                tracking_notes="Driver en route along Waiyaki Way."
            )
            db.session.add(dsp2)

            db.session.commit()
            print("[OK] Orders, Payments, and Dispatches seeded.")

        # 8. Seed Feedback, Contacts, Notifications
        if not Feedback.query.first():
            fb1 = Feedback(user_id=cust_u.id, rating=5, category="Produce Quality", comment="The French beans and roses were exceptionally fresh and delivered in chilled packaging. Highly impressed!", is_reviewed=True)
            fb2 = Feedback(user_id=farmer_u.id, rating=5, category="Farmer Training", comment="Dr. Kipchoge's practical session on biological pest control completely transformed my yield and pesticide savings.", is_reviewed=True)
            db.session.add_all([fb1, fb2])

            c1 = Contact(full_name="Hassan Ali", email="hassan@freshimports.ae", phone="+971501234567", subject="Bulk Export Order Inquiry for Dubai", message="We are looking to source 5 tonnes of Hass Avocados and 2 tonnes of French Beans weekly on contract.", status="IN_PROGRESS")
            c2 = Contact(full_name="Jane Njeri", email="jane.njeri@greenretail.co.ke", phone="+254702987111", subject="Supermarket Wholesale Partnership", message="Requesting catalog and contract pricing for fresh culinary herbs and berries in Nairobi branches.", status="NEW")
            db.session.add_all([c1, c2])

            n1 = Notification(user_id=cust_u.id, title="Order Confirmed", message="Your order ORD-2026-1001 was delivered successfully.", type="ORDER", is_read=True)
            n2 = Notification(user_id=cust_u.id, title="Order Out for Delivery", message="Order ORD-2026-1002 is on the way with driver James Mwangi.", type="DISPATCH", is_read=False)
            n3 = Notification(user_id=farmer_u.id, title="Certificate Ready", message="Your Integrated Pest Management certificate is available in your portal.", type="TRAINING", is_read=False)
            db.session.add_all([n1, n2, n3])

            db.session.commit()
            print("[OK] Feedback, Contacts, and Notifications seeded.")

        print("\n========================================================")
        print("Database seeding completed successfully!")
        print("Default Login credentials:")
        print("  Admin:    admin@aaagrowers.co.ke     / Password123!")
        print("  Customer: customer@aaagrowers.co.ke  / Password123!")
        print("  Farmer:   farmer@aaagrowers.co.ke    / Password123!")
        print("  Trainer:  trainer@aaagrowers.co.ke   / Password123!")
        print("========================================================")


if __name__ == "__main__":
    seed_database()
