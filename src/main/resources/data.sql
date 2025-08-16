-- 회원 데이터 3건 추가
INSERT INTO member (created_at, updated_at, name, email, city, street, zipcode)
VALUES
    (NOW(), NOW(), '홍길동', 'gildong@example.com', '서울', '세종대로', '04524'),
    (NOW(), NOW(), '이순신', 'sunshin@example.com', '부산', '중앙대로', '48924'),
    (NOW(), NOW(), '김유신', 'yushin@example.com', '광주', '무진대로', '61945');

-- 상품 데이터 5건 추가
INSERT INTO product (created_at, updated_at, name, product_no, price, stock)
VALUES
    (NOW(), NOW(), '노트북', 'P0001', 1500000, 10),
    (NOW(), NOW(), '마우스', 'P0002', 50000, 100),
    (NOW(), NOW(), '키보드', 'P0003', 80000, 50),
    (NOW(), NOW(), '27인치 모니터', 'P0004', 300000, 30),
    (NOW(), NOW(), '웹캠', 'P0005', 70000, 80);

-- 첫 번째 주문에 대한 배송 및 결제 정보 추가
INSERT INTO delivery (created_at, updated_at, city, street, zipcode, delivery_status)
VALUES (NOW(), NOW(), '서울', '세종대로', '04524', 'ORDER');
INSERT INTO payment (created_at, updated_at, payment_method, tid)
VALUES (NOW(), NOW(), 'CARD', 'TID_ABC123');

-- 첫 번째 주문 추가 (홍길동 회원, 3개 상품 주문)
INSERT INTO orders (created_at, updated_at, member_id, delivery_id, payment_id, name, city, street, zipcode, no, total_price, tid)
VALUES (NOW(), NOW(), 1, 1, 1, '홍길동', '서울', '세종대로', '04524', 'ORD001', 1630000, 'TID_ABC123');

-- 첫 번째 주문에 대한 상세 내역 추가
INSERT INTO order_detail (created_at, updated_at, order_id, product_id, price, quantity)
VALUES
    (NOW(), NOW(), 1, 1, 1500000, 1), -- 노트북 1개
    (NOW(), NOW(), 1, 2, 50000, 1),   -- 마우스 1개
    (NOW(), NOW(), 1, 3, 80000, 1);    -- 키보드 1개

-- 두 번째 주문에 대한 배송 및 결제 정보 추가
INSERT INTO delivery (created_at, updated_at, city, street, zipcode, delivery_status)
VALUES (NOW(), NOW(), '부산', '해운대로', '48094', 'ORDER');
INSERT INTO payment (created_at, updated_at, payment_method, tid)
VALUES (NOW(), NOW(), 'MONEY', 'TID_XYZ789');

-- 두 번째 주문 추가 (이순신 회원, 2개 상품 주문)
INSERT INTO orders (created_at, updated_at, member_id, delivery_id, payment_id, name, city, street, zipcode, no, total_price, tid)
VALUES (NOW(), NOW(), 2, 2, 2, '이순신', '부산', '해운대로', '48094', 'ORD002', 670000, 'TID_XYZ789');

-- 두 번째 주문에 대한 상세 내역 추가
INSERT INTO order_detail (created_at, updated_at, order_id, product_id, price, quantity)
VALUES
    (NOW(), NOW(), 2, 4, 300000, 2), -- 27인치 모니터 2개
    (NOW(), NOW(), 2, 5, 70000, 1);   -- 웹캠 1개