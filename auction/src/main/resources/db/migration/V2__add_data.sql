INSERT INTO auctions (
    uid,
    title,
    description,
    base_price,
    seller_id,
    seller_name,
    seller_email,
    seller_phone,
    status,
    start_time,
    end_time,
    created_at,
    updated_at
) VALUES
-- 1
('a1111111-1111-1111-1111-111111111111',
 'MacBook Pro M2',
 'MacBook Pro M2, 16GB RAM, 512GB SSD',
 95000,
 'kc-101','Ravi Kumar','ravi@gmail.com','+91-9000000001',
 'DRAFT',
 NOW()+INTERVAL '1 day', NOW()+INTERVAL '6 days', NOW(), NULL),

-- 2
('a2222222-2222-2222-2222-222222222222',
 'iPhone 14 Pro',
 'iPhone 14 Pro 256GB',
 82000,
 'kc-102','Anjali Sharma','anjali@gmail.com','+91-9000000002',
 'OPEN',
 NOW()-INTERVAL '2 hours', NOW()+INTERVAL '3 days', NOW(), NULL),

-- 3
('a3333333-3333-3333-3333-333333333333',
 'Royal Enfield Classic 350',
 '2019 model, single owner',
 120000,
 'kc-103','Suresh Reddy','suresh@gmail.com','+91-9000000003',
 'OPEN',
 NOW()-INTERVAL '1 day', NOW()+INTERVAL '5 days', NOW(), NULL),

-- 4
('a4444444-4444-4444-4444-444444444444',
 'Gaming PC RTX 3080',
 'Ryzen 9, RTX 3080, 32GB RAM',
 150000,
 'kc-104','Neha Verma','neha@gmail.com','+91-9000000004',
 'CLOSED',
 NOW()-INTERVAL '8 days', NOW()-INTERVAL '2 days', NOW(), NOW()-INTERVAL '2 days'),

-- 5
('a5555555-5555-5555-5555-555555555555',
 'Samsung Galaxy S23 Ultra',
 '12GB RAM, 256GB Storage',
 78000,
 'kc-105','Amit Patel','amit@gmail.com','+91-9000000005',
 'OPEN',
 NOW()-INTERVAL '5 hours', NOW()+INTERVAL '2 days', NOW(), NULL),

-- 6
('a6666666-6666-6666-6666-666666666666',
 'PlayStation 5',
 'PS5 Disc Edition',
 45000,
 'kc-106','Karan Singh','karan@gmail.com','+91-9000000006',
 'DRAFT',
 NOW()+INTERVAL '2 days', NOW()+INTERVAL '7 days', NOW(), NULL),

-- 7
('a7777777-7777-7777-7777-777777777777',
 'Canon EOS R Camera',
 'Mirrorless camera with lens',
 92000,
 'kc-107','Priya Nair','priya@gmail.com','+91-9000000007',
 'OPEN',
 NOW()-INTERVAL '6 hours', NOW()+INTERVAL '4 days', NOW(), NULL),

-- 8
('a8888888-8888-8888-8888-888888888888',
 'iPad Pro M1',
 '11-inch iPad Pro',
 65000,
 'kc-108','Rahul Mehta','rahul@gmail.com','+91-9000000008',
 'CLOSED',
 NOW()-INTERVAL '12 days', NOW()-INTERVAL '6 days', NOW(), NOW()-INTERVAL '6 days'),

-- 9
('a9999999-9999-9999-9999-999999999999',
 'OnePlus 11',
 '12GB RAM, 256GB',
 55000,
 'kc-109','Sneha Iyer','sneha@gmail.com','+91-9000000009',
 'OPEN',
 NOW()-INTERVAL '1 hour', NOW()+INTERVAL '1 day', NOW(), NULL),

-- 10
('b1111111-1111-1111-1111-111111111111',
 'Dell XPS 15',
 'Intel i7, 32GB RAM',
 110000,
 'kc-110','Arjun Rao','arjun@gmail.com','+91-9000000010',
 'DRAFT',
 NOW()+INTERVAL '3 days', NOW()+INTERVAL '8 days', NOW(), NULL),

-- 11
('b2222222-2222-2222-2222-222222222222',
 'Sony WH-1000XM5',
 'Noise Cancelling Headphones',
 28000,
 'kc-111','Pooja Malhotra','pooja@gmail.com','+91-9000000011',
 'OPEN',
 NOW()-INTERVAL '30 minutes', NOW()+INTERVAL '1 day', NOW(), NULL),

-- 12
('b3333333-3333-3333-3333-333333333333',
 'Apple Watch Ultra',
 'GPS + Cellular',
 72000,
 'kc-112','Nikhil Jain','nikhil@gmail.com','+91-9000000012',
 'CLOSED',
 NOW()-INTERVAL '7 days', NOW()-INTERVAL '1 day', NOW(), NOW()-INTERVAL '1 day'),

-- 13
('b4444444-4444-4444-4444-444444444444',
 'GoPro Hero 11',
 'Action Camera',
 42000,
 'kc-113','Meera Joshi','meera@gmail.com','+91-9000000013',
 'OPEN',
 NOW()-INTERVAL '3 hours', NOW()+INTERVAL '2 days', NOW(), NULL),

-- 14
('b5555555-5555-5555-5555-555555555555',
 'AirPods Pro 2',
 'Active Noise Cancellation',
 21000,
 'kc-114','Vikas Khanna','vikas@gmail.com','+91-9000000014',
 'DRAFT',
 NOW()+INTERVAL '1 day', NOW()+INTERVAL '5 days', NOW(), NULL),

-- 15
('b6666666-6666-6666-6666-666666666666',
 'Asus ROG Laptop',
 'Gaming Laptop RTX 3070',
 135000,
 'kc-115','Harsha Vardhan','harsha@gmail.com','+91-9000000015',
 'OPEN',
 NOW()-INTERVAL '4 hours', NOW()+INTERVAL '6 days', NOW(), NULL);
