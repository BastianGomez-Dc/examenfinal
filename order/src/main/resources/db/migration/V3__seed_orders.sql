INSERT INTO "order" (id, client_id, equipment_id, technician_id, status, total, created_date) VALUES
(1, 1, 1, 1, 'RECEIVED', 15000.00, CURRENT_DATE),
(2, 2, 2, 2, 'IN_REPAIR', 45000.00, CURRENT_DATE),
(3, 3, 3, 3, 'READY', 25000.00, CURRENT_DATE),
(4, 4, 4, 4, 'DELIVERED', 18000.00, CURRENT_DATE),
(5, 5, 5, 5, 'RECEIVED', 12000.00, CURRENT_DATE);

SELECT setval('order_id_seq', (SELECT MAX(id) FROM "order"));

INSERT INTO order_items (id, order_id, service_id, service_name, unit_price, quantity) VALUES
(1, 1, 1, 'General diagnostics', 15000.00, 1),
(2, 2, 2, 'Hard drive replacement', 45000.00, 1),
(3, 3, 3, 'OS reinstall', 25000.00, 1),
(4, 4, 4, 'Internal cleaning', 18000.00, 1),
(5, 5, 5, 'Wi-Fi network setup', 12000.00, 1);

SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));
