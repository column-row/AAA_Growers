-- ==============================================================================
-- AAA GROWERS - SQL Seed Data
-- ==============================================================================

USE aaa_growers;

-- 1. Roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'System Administrator with full access'),
('CUSTOMER', 'Retail / Wholesale Produce Customer'),
('FARMER', 'Registered Outgrower & Agricultural Producer'),
('INVENTORY_MANAGER', 'Warehouse and Inventory Controller'),
('FINANCE_MANAGER', 'Financial auditor, payments and revenue controller'),
('SUPPLIER', 'Agricultural input supplier (seeds, fertilizer, equipment)'),
('DISPATCH_MANAGER', 'Logistics, fleet coordinator and dispatch supervisor'),
('SERVICE_MANAGER', 'Customer support, quality control and ticketing officer'),
('TRAINER', 'Agricultural extension officer and training instructor'),
('DRIVER', 'Logistics delivery driver and route courier')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 2. Initial Roles & Users (Password: Admin@123 for all seeded users -> bcrypt hash: $2b$12$4vQp29e8F7sV0uU7mG9OteJ2G3kQn5P9e8F7sV0uU7mG9OteJ2G3k or standard werkzeug hash)
-- Werkzeug generated scrypt hash for 'Password123!':
-- 'scrypt:32768:8:1$1uR6a1N0qZ3$1ffca961c0d4ff54fe2a188f6c4ffefcefbdf3556d10c1c8a1435272a87a229a43a6d71b8eeef70014a60dcff23b20756784d0b16aef74bcaae1ff7ebfe18ee1'

INSERT INTO users (id, first_name, last_name, email, phone, password_hash, role, status, city, address) VALUES
(1, 'Admin', 'System', 'admin@aaagrowers.co.ke', '+254700000001', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'ADMIN', 'ACTIVE', 'Nairobi', 'AAA Growers HQ, Old Airport North Road'),
(2, 'John', 'Kamau', 'customer@aaagrowers.co.ke', '+254711111111', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'CUSTOMER', 'ACTIVE', 'Nairobi', 'Westlands, Parklands Med Plaza'),
(3, 'Mary', 'Wambui', 'farmer@aaagrowers.co.ke', '+254722222222', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'FARMER', 'ACTIVE', 'Naivasha', 'Farm Block 4B, South Lake Road'),
(4, 'Peter', 'Ochieng', 'inventory@aaagrowers.co.ke', '+254733333333', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'INVENTORY_MANAGER', 'ACTIVE', 'Nairobi', 'Warehouse 3, Embakasi Logistics Center'),
(5, 'Sarah', 'Mutua', 'finance@aaagrowers.co.ke', '+254744444444', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'FINANCE_MANAGER', 'ACTIVE', 'Nairobi', 'Finance Wing, AAA Tower'),
(6, 'GreenCrop', 'AgroSupplies', 'supplier@aaagrowers.co.ke', '+254755555555', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'SUPPLIER', 'ACTIVE', 'Nakuru', 'Industrial Area, Gate 12'),
(7, 'David', 'Kariuki', 'dispatch@aaagrowers.co.ke', '+254766666666', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'DISPATCH_MANAGER', 'ACTIVE', 'Nairobi', 'Central Distribution Hub, JKIA'),
(8, 'Grace', 'Akinyi', 'service@aaagrowers.co.ke', '+254777777777', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'SERVICE_MANAGER', 'ACTIVE', 'Nairobi', 'Customer Experience Desk'),
(9, 'Dr. Samuel', 'Kipchoge', 'trainer@aaagrowers.co.ke', '+254788888888', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'TRAINER', 'ACTIVE', 'Eldoret', 'Agronomy Research Center'),
(10, 'James', 'Mwangi', 'driver@aaagrowers.co.ke', '+254799999999', '$2b$12$K1J8oK5b7tYwzP3wD0Zc6e4eN/8oA4rS9kL3yJ2tP8mZ7wQ5yB0xC', 'DRIVER', 'ACTIVE', 'Nairobi', 'Fleet Station 1');

-- 3. Categories
INSERT INTO categories (id, name, slug, description, image_url) VALUES
(1, 'Fresh Vegetables', 'fresh-vegetables', 'Farm fresh, crisp and organic export-grade vegetables', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600'),
(2, 'Export Cut Flowers', 'export-cut-flowers', 'Premium quality roses, lilies and fresh cut flowers for global markets', 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?w=600'),
(3, 'Fresh Herbs & Spices', 'herbs-spices', 'Aromatic rosemary, basil, mint, coriander and organic culinary herbs', 'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600'),
(4, 'Fruits & Berries', 'fruits-berries', 'Naturally ripened avocados, passion fruits, berries and orchard harvests', 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=600'),
(5, 'Agricultural Inputs & Seeds', 'farm-inputs', 'Certified drought-resistant seeds, bio-fertilizers and organic soil nutrients', 'https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=600');

-- 4. Products
INSERT INTO products (id, category_id, name, sku, description, unit, price, cost_price, image_url, is_active, is_featured) VALUES
(1, 1, 'Premium French Fine Beans', 'VEG-FFB-001', 'Hand-picked extra-fine tender green beans grown in volcanic soils.', 'kg', 280.00, 180.00, 'https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=600', 1, 1),
(2, 1, 'Snow Peas (Mangetout)', 'VEG-SNP-002', 'Crisp, sweet export quality snow peas rich in vitamins.', 'kg', 320.00, 210.00, 'https://images.unsplash.com/photo-1515543237350-b3eea1ec8082?w=600', 1, 1),
(3, 1, 'Tenderstem Broccoli', 'VEG-TSB-003', 'Succulent stem broccoli with tender florets, organically nurtured.', 'kg', 450.00, 300.00, 'https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=600', 1, 1),
(4, 2, 'Red Naomi Grand Roses', 'FLW-RNR-001', 'Vibrant deep-red long-stem velvet roses with sublime fragrance (bunch of 20).', 'bunch', 1200.00, 750.00, 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600', 1, 1),
(5, 2, 'Avalanche White Roses', 'FLW-AWR-002', 'Pristine white export luxury roses with 60cm stem length (bunch of 20).', 'bunch', 1150.00, 720.00, 'https://images.unsplash.com/photo-1533038590840-1cde6e668a91?w=600', 1, 0),
(6, 3, 'Fresh Sweet Genovese Basil', 'HRB-BAS-001', 'Lush aromatic basil leaves packaged in nitrogen-sealed freshness pouches.', 'pack (250g)', 150.00, 80.00, 'https://images.unsplash.com/photo-1608686207856-001b95cf60ca?w=600', 1, 1),
(7, 3, 'Fresh Rosemary Stems', 'HRB-RSM-002', 'Woody organic rosemary stems bursting with aromatic essential oils.', 'pack (250g)', 160.00, 85.00, 'https://images.unsplash.com/photo-1515586000433-a5bc720b3603?w=600', 1, 0),
(8, 4, 'Hass Avocado Export Grade A', 'FRT-HSA-001', 'Rich creamy Hass avocados with high oil content and smooth nutty flavor.', 'box (4kg)', 850.00, 520.00, 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=600', 1, 1),
(9, 4, 'Sweet Purple Passion Fruits', 'FRT-PSN-002', 'High brix tropical purple passion fruits loaded with juicy aromatic pulp.', 'kg', 380.00, 230.00, 'https://images.unsplash.com/photo-1589135233689-d562f4476629?w=600', 1, 1),
(10, 5, 'Bio-Organic Foliar Fertilizer 5L', 'INP-BFF-001', 'Eco-certified microbial foliar fertilizer enhancing chlorophyll synthesis.', 'litre', 2400.00, 1600.00, 'https://images.unsplash.com/photo-1585314062340-f1a5a7c9328d?w=600', 1, 0);

-- 5. Inventory
INSERT INTO inventory (product_id, current_stock, low_stock_threshold, reorder_quantity, last_restocked_at) VALUES
(1, 450, 50, 200, NOW()),
(2, 320, 40, 150, NOW()),
(3, 180, 30, 100, NOW()),
(4, 250, 30, 100, NOW()),
(5, 190, 25, 80, NOW()),
(6, 400, 50, 150, NOW()),
(7, 350, 40, 120, NOW()),
(8, 500, 60, 250, NOW()),
(9, 280, 35, 100, NOW()),
(10, 75, 15, 50, NOW());

-- 6. Inventory Logs
INSERT INTO inventory_logs (product_id, change_quantity, previous_stock, new_stock, movement_type, reference_id, notes, created_by) VALUES
(1, 450, 0, 450, 'INITIAL', 'INIT-2026-001', 'Initial stock intake from harvest Block A', 4),
(4, 250, 0, 250, 'INITIAL', 'INIT-2026-002', 'Fresh rose bloom harvest storage', 4),
(8, 500, 0, 500, 'INITIAL', 'INIT-2026-003', 'Avocado packhouse container delivery', 4);

-- 7. Farmers
INSERT INTO farmers (id, user_id, farm_name, farm_location, farm_size_acres, crops_grown, farming_experience_years, national_id) VALUES
(1, 3, 'Wambui Green Meadows Farm', 'Naivasha, South Lake Ward', 12.50, 'French Beans, Snow Peas, Rosemary, Avocado', 7, '29482716');

-- 8. Trainers
INSERT INTO trainers (id, user_id, specialization, qualifications, bio) VALUES
(1, 9, 'Export Agronomy & GlobalG.A.P. Standards', 'PhD Horticulture (UoN), Certified Lead Auditor GlobalG.A.P.', 'Dr. Samuel Kipchoge has over 15 years experience coaching commercial outgrowers in export standards, pest management and organic soil health.');

-- 9. Training Sessions
INSERT INTO training_sessions (id, trainer_id, title, description, category, training_date, start_time, end_time, location, capacity, status) VALUES
(1, 1, 'GlobalG.A.P. Compliance & Export Quality Control', 'Comprehensive training on pesticide residue limits, trace-back documentation and hygiene standards for EU export markets.', 'Quality Standards', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), '09:00:00', '13:00:00', 'AAA Training Academy, Naivasha Center', 35, 'UPCOMING'),
(2, 1, 'Precision Drip Irrigation & Water Conservation', 'Learn modern soil-moisture sensors, solar drip automation and fertigation schedules to double crop yields with 40% less water.', 'Irrigation & Water', DATE_ADD(CURRENT_DATE, INTERVAL 12 DAY), '10:00:00', '15:00:00', 'Agronomy Field Lab, Block 7 Naivasha', 25, 'UPCOMING'),
(3, 1, 'Integrated Pest & Disease Management (IPM)', 'Biological pest controls, beneficial insect habitat creation and reducing chemical sprays in avocado and bean farming.', 'Pest Management', DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), '09:00:00', '14:00:00', 'Naivasha Agricultural Hub', 30, 'COMPLETED');

-- 10. Training Bookings
INSERT INTO training_bookings (id, training_id, farmer_id, booking_date, status, notes) VALUES
(1, 3, 1, DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 'COMPLETED', 'Attended all sessions and passed practical evaluation.'),
(2, 1, 1, DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 'BOOKED', 'Registered for upcoming GlobalG.A.P. masterclass.');

-- 11. Certifications
INSERT INTO certifications (id, certificate_number, farmer_id, training_id, issue_date, expiry_date, title, verification_hash, status) VALUES
(1, 'AAA-CERT-2026-0089', 1, 3, DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 720 DAY), 'Certificate of Excellence in Integrated Pest Management (IPM)', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'ACTIVE');

-- 12. Suppliers
INSERT INTO suppliers (id, name, contact_person, email, phone, address, supply_category, status, rating) VALUES
(1, 'AgroGreen Seeds East Africa', 'Erick Odhiambo', 'erick@agrogreenseeds.co.ke', '+254710123456', 'Industrial Area Road A, Nairobi', 'Seeds & Seedlings', 'ACTIVE', 4.90),
(2, 'BioSoil Nutrients Ltd', 'Patricia Moraa', 'info@biosoilnutrients.com', '+254720987654', 'Nakuru Agro Park, Suite 4', 'Organic Fertilizers', 'ACTIVE', 4.85),
(3, 'EcoPack Kenya Limited', 'Kibet Sang', 'sales@ecopackkenya.co.ke', '+254730654321', 'Mombasa Road Logistics Center, Nairobi', 'Biodegradable Packaging', 'ACTIVE', 4.70);

-- 13. Drivers
INSERT INTO drivers (id, user_id, license_number, vehicle_registration, vehicle_type, is_available, current_location) VALUES
(1, 10, 'DL-KEN-99824', 'KDC 482B', 'Refrigerated Isuzu 3-Ton Truck', 1, 'Nairobi Dispatch Center');

-- 14. Sample Orders
INSERT INTO orders (id, order_number, customer_id, total_amount, discount_amount, shipping_fee, tax_amount, net_amount, status, payment_status, delivery_address, delivery_city, delivery_phone, notes) VALUES
(1, 'ORD-2026-1001', 2, 2800.00, 0.00, 300.00, 0.00, 3100.00, 'PAID', 'PAID', 'Parklands Med Plaza, 4th Floor, Suite 402', 'Nairobi', '+254711111111', 'Please ensure fresh cold-chain delivery.'),
(2, 'ORD-2026-1002', 2, 5950.00, 200.00, 350.00, 0.00, 6100.00, 'DISPATCHED', 'PAID', 'Westlands, School Lane Villa 12', 'Nairobi', '+254711111111', 'Deliver before 3 PM');

-- 15. Order Items
INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal) VALUES
(1, 1, 'Premium French Fine Beans', 280.00, 10, 2800.00),
(2, 4, 'Red Naomi Grand Roses', 1200.00, 3, 3600.00),
(2, 8, 'Hass Avocado Export Grade A', 850.00, 2, 1700.00),
(2, 6, 'Fresh Sweet Genovese Basil', 150.00, 4, 600.00);

-- 16. Payments
INSERT INTO payments (id, order_id, user_id, amount, payment_method, transaction_reference, status, payment_provider, paid_at) VALUES
(1, 1, 2, 3100.00, 'MPESA', 'MPESA-QKW8294719', 'SUCCESS', 'MPESA_EXPRESS', NOW()),
(2, 2, 2, 6100.00, 'CARD', 'CARD-TX-998241940', 'SUCCESS', 'VISA_MOCK_GATEWAY', NOW());

-- 17. Dispatches
INSERT INTO dispatches (id, dispatch_number, order_id, driver_id, delivery_address, dispatch_date, status, tracking_notes) VALUES
(1, 'DSP-2026-0001', 2, 1, 'Westlands, School Lane Villa 12, Nairobi', NOW(), 'IN_TRANSIT', 'Driver James Mwangi departed distribution center at 11:30 AM.');

-- 18. Feedback
INSERT INTO feedback (id, user_id, rating, category, comment, is_reviewed) VALUES
(1, 2, 5, 'Produce Quality', 'The French beans and roses were exceptionally fresh and delivered in chilled packaging. Highly impressed!', 1),
(2, 3, 5, 'Farmer Training', 'Dr. Kipchoge practical session on biological pest control completely transformed my yield and pesticide savings.', 1);

-- 19. Contacts
INSERT INTO contacts (id, full_name, email, phone, subject, message, status) VALUES
(1, 'Hassan Ali', 'hassan@freshimports.ae', '+971501234567', 'Bulk Export Order Inquiry for Dubai', 'We are looking to source 5 tonnes of Hass Avocados and 2 tonnes of French Beans weekly.', 'IN_PROGRESS'),
(2, 'Jane Njeri', 'jane.njeri@greenretail.co.ke', '+254702987111', 'Supermarket Wholesale Partnership', 'Requesting catalog and contract pricing for fresh culinary herbs and berries.', 'NEW');

-- 20. Notifications
INSERT INTO notifications (id, user_id, title, message, type, is_read) VALUES
(1, 2, 'Order Confirmed', 'Your order ORD-2026-1001 has been confirmed and is being packed for delivery.', 'ORDER', 1),
(2, 2, 'Order On The Way', 'Order ORD-2026-1002 is now out for delivery with driver James Mwangi.', 'DISPATCH', 0),
(3, 3, 'Certificate Issued', 'Congratulations Mary! Your certificate for Integrated Pest Management is ready to view and download.', 'TRAINING', 0);
