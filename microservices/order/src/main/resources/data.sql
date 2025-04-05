INSERT INTO orders (customer_id, asset_name, order_side, order_size, price, status, create_date)
VALUES (10, 'ASELS', 'BUY', 1.0, 65000.00, 'PENDING', '2025-04-04T12:30:00');

INSERT INTO orders (customer_id, asset_name, order_side, order_size, price, status, create_date)
VALUES (10, 'TRY', 'SELL', 10.0, 3200.50, 'MATCHED', '2025-04-03T16:15:00');

INSERT INTO orders (customer_id, asset_name, order_side, order_size, price, status, create_date)
VALUES (103, 'XRP', 'BUY', 1000.0, 0.75, 'CANCELLED', '2025-04-02T09:45:00');

INSERT INTO orders (customer_id, asset_name, order_side, order_size, price, status, create_date)
VALUES (101, 'SOL', 'SELL', 50.0, 120.00, 'PENDING', '2025-04-01T18:20:00');

INSERT INTO orders (customer_id, asset_name, order_side, order_size, price, status, create_date)
VALUES (104, 'DOT', 'BUY', 200.0, 6.30, 'MATCHED', '2025-03-30T11:00:00');
