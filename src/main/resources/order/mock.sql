-- 사용할 데이터베이스를 선택합니다.
USE order_project;

-- 대량 INSERT 시 제약 조건 검사를 잠시 비활성화하여 성능을 향상시킵니다.
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;

-- 회원 (member) 1,000,000명 생성
INSERT INTO member (created_at, updated_at, deleted_at, city, email, name, street, zipcode)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    NOW(),
    NOW(),
    NULL,
    CONCAT('City', n),
    CONCAT('user', LPAD(n, 7, '0'), '@example.com'),
    CONCAT('User', LPAD(n, 7, '0')),
    CONCAT('Street', n),
    LPAD(n, 5, '0')
FROM numbers;

-- 상품 (product) 10,000개 생성
INSERT INTO product (created_at, updated_at, deleted_at, price, stock, name, product_no)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 10000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 10000
)
SELECT
    NOW(),
    NOW(),
    NULL,
    FLOOR(10000 + RAND() * 90000),
    FLOOR(10 + RAND() * 990),
    CONCAT('Product', LPAD(n, 5, '0')),
    CONCAT('P', LPAD(n, 9, '0'))
FROM numbers;

-- 결제 (payment) 1,000,000건 생성
INSERT INTO payment (created_at, updated_at, deleted_at, tid, payment_method)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    NOW(),
    NOW(),
    NULL,
    MD5(RAND()),
    IF(MOD(n, 2) = 0, 'CARD', 'MONEY')
FROM numbers;

-- 배송 (delivery) 1,000,000건 생성 (성능 개선)
INSERT INTO delivery (created_at, updated_at, deleted_at, city, street, zipcode, delivery_status)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    NOW(),
    NOW(),
    NULL,
    CONCAT('RcvCity', n), -- 서브쿼리 대신 직접 생성하여 성능 향상
    CONCAT('RcvStreet', n),
    LPAD(FLOOR(RAND() * 99999), 5, '0'),
    IF(MOD(n, 5) = 0, 'CANCELLED', 'ORDER')
FROM numbers;

-- 주문 (orders) 1,000,000건 생성 (delivery_id 추가)
INSERT INTO orders (total_price, created_at, updated_at, deleted_at, member_id, payment_id, delivery_id, city, name, no, street, tid, zipcode)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    FLOOR(20000 + RAND() * 200000),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY),
    NOW(),
    NULL,
    1 + FLOOR(RAND() * 1000000), -- member_id (1 ~ 1,000,000)
    n,                           -- payment_id (1 ~ 1,000,000, 1:1 매핑)
    n,                           -- delivery_id (1 ~ 1,000,000, 1:1 매핑)
    CONCAT('RcvCity', n),
    CONCAT('Receiver', n),
    CONCAT('ORD', LPAD(n, 9, '0')),
    CONCAT('RcvStreet', n),
    (SELECT p.tid FROM payment p WHERE p.payment_id = n),
    LPAD(FLOOR(RAND() * 99999), 5, '0')
FROM numbers;

-- 주문상세 (order_detail) 3,000,000건 생성
INSERT INTO order_detail (price, quantity, created_at, updated_at, deleted_at, order_id, product_id)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 3000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 3000000
)
SELECT
    FLOOR(10000 + RAND() * 90000),
    1 + FLOOR(RAND() * 5),
    NOW(),
    NOW(),
    NULL,
    1 + FLOOR(RAND() * 1000000), -- order_id (1 ~ 1,000,000)
    1 + FLOOR(RAND() * 10000)    -- product_id (1 ~ 10,000)
FROM numbers;

-- 비활성화했던 제약 조건 검사를 다시 활성화합니다.
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;