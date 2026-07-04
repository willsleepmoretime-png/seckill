USE seckill_db;

INSERT INTO goods (id, name, description, image_url, price, stock, status, create_time, update_time)
VALUES
  (900001, 'Cache Test Phone', 'Redis cache benchmark goods 1', '/images/test-phone.png', 3999.00, 10000, 1, NOW(), NOW()),
  (900002, 'Cache Test Laptop', 'Redis cache benchmark goods 2', '/images/test-laptop.png', 6999.00, 8000, 1, NOW(), NOW()),
  (900003, 'Cache Test Watch', 'Redis cache benchmark goods 3', '/images/test-watch.png', 1299.00, 6000, 1, NOW(), NOW()),
  (900004, 'Cache Test Camera', 'Redis cache benchmark goods 4', '/images/test-camera.png', 2999.00, 5000, 1, NOW(), NOW()),
  (900005, 'Cache Test Keyboard', 'Redis cache benchmark goods 5', '/images/test-keyboard.png', 499.00, 12000, 1, NOW(), NOW()),
  (900006, 'Cache Test Mouse', 'Redis cache benchmark goods 6', '/images/test-mouse.png', 299.00, 12000, 1, NOW(), NOW()),
  (900007, 'Cache Test Monitor', 'Redis cache benchmark goods 7', '/images/test-monitor.png', 1599.00, 7000, 1, NOW(), NOW()),
  (900008, 'Cache Test Earbuds', 'Redis cache benchmark goods 8', '/images/test-earbuds.png', 899.00, 9000, 1, NOW(), NOW()),
  (900009, 'Cache Test Tablet', 'Redis cache benchmark goods 9', '/images/test-tablet.png', 3299.00, 6500, 1, NOW(), NOW()),
  (900010, 'Cache Test Router', 'Redis cache benchmark goods 10', '/images/test-router.png', 599.00, 11000, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  image_url = VALUES(image_url),
  price = VALUES(price),
  stock = VALUES(stock),
  status = VALUES(status),
  update_time = NOW();

INSERT INTO seckill (id, goods_id, seckill_price, stock_count, start_time, end_time, status, version, create_time, update_time)
VALUES
  (910001, 900001, 1999.00, 3000, '2026-07-03 00:00:00', '2026-12-31 23:59:59', 1, 0, NOW(), NOW()),
  (910002, 900002, 4599.00, 2000, '2026-07-03 00:00:00', '2026-12-31 23:59:59', 1, 0, NOW(), NOW()),
  (910003, 900003, 699.00, 2500, '2026-07-03 00:00:00', '2026-12-31 23:59:59', 1, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  goods_id = VALUES(goods_id),
  seckill_price = VALUES(seckill_price),
  stock_count = VALUES(stock_count),
  start_time = VALUES(start_time),
  end_time = VALUES(end_time),
  status = VALUES(status),
  version = VALUES(version),
  update_time = NOW();
