-- 사용할 데이터베이스를 선택합니다.
USE order_db;

-- 대량 INSERT 시 제약 조건 검사를 잠시 비활성화하여 성능을 향상시킵니다.
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET SQL_LOG_BIN=0; -- 바이너리 로깅 비활성화 (선택 사항, 대량 작업 시 성능 향상)

-- 1. 회원 (member) 1,000,000명 생성
-- Member Entity + Address Embeddable
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

-- 2. 상품 (product) 10,000개 생성
-- Product Entity
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

-- 3. 배송 (delivery) 1,000,000건 생성
-- Delivery Entity + Address Embeddable
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
    CONCAT('RcvCity', n),
    CONCAT('RcvStreet', n),
    LPAD(FLOOR(RAND() * 99999), 5, '0'),
    IF(MOD(n, 5) = 0, 'CANCELLED', 'ORDER')
FROM numbers;


-- 4. 결제 (payment) 1,000,000건 생성 (엔티티 필드 모두 반영)
-- Payment Entity + Amount, CardInfo Embeddable
INSERT INTO payment (created_at, updated_at, deleted_at, approved_at, order_no, payment_method, tid, total_amount, discount_amount, tax_free_amount, card_issuer_name, card_purchase_name, card_install_month)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    NOW(),
    NOW(),
    NULL,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) MINUTE), -- 생성 시간 근처로 결제 승인 시간 설정
    CONCAT('ORD', LPAD(n, 9, '0')), -- 주문 번호
    IF(MOD(n, 2) = 0, 'CARD', 'MONEY'), -- 결제 수단
    MD5(RAND()), -- 거래 ID
    FLOOR(20000 + RAND() * 200000), -- 총 결제 금액
    FLOOR(1000 + RAND() * 10000),   -- 할인 금액
    0,                               -- 비과세 금액
    'KakaoPay Issuer',               -- 카드 발급사
    'KakaoPay Purchase',             -- 카드 매입사
    '00'                             -- 할부 개월
FROM numbers;

-- 5. 주문 (orders) 1,000,000건 생성 (엔티티 필드 모두 반영)
-- Order Entity + Address Embeddable
INSERT INTO orders (created_at, updated_at, deleted_at, member_id, payment_id, delivery_id, city, street, zipcode, name, no, tid, total_amount, real_price, total_discount)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */ 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY), -- 생성일 (과거 1년 랜덤)
    NOW(),
    NULL,
    1 + FLOOR(RAND() * 1000000), -- member_id (랜덤)
    n,                           -- payment_id (1:1 매핑)
    n,                           -- delivery_id (1:1 매핑)
    CONCAT('RcvCity', n),        -- 수취인 도시
    CONCAT('RcvStreet', n),      -- 수취인 주소
    LPAD(FLOOR(RAND() * 99999), 5, '0'), -- 수취인 우편번호
    CONCAT('Product', LPAD(1 + FLOOR(RAND()*10000), 5, '0'), ' 외'), -- 주문명
    CONCAT('ORD', LPAD(n, 9, '0')), -- 주문 번호
    MD5(RAND()), -- 거래 ID
    (SELECT p.total_amount FROM payment p WHERE p.payment_id = n) AS total_amount, -- 총 금액 (payment와 일치)
    (SELECT p.total_amount - p.discount_amount FROM payment p WHERE p.payment_id = n) AS real_price, -- 실 결제 금액
    (SELECT p.discount_amount FROM payment p WHERE p.payment_id = n) AS total_discount -- 총 할인 금액
FROM numbers;

-- 6. 주문상세 (order_detail) 3,000,000건 생성
-- OrderDetail Entity
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
    1 + FLOOR(RAND() * 1000000), -- order_id (랜덤)
    1 + FLOOR(RAND() * 10000)    -- product_id (랜덤)
FROM numbers;

-- 7. 주문 통계 (order_stats) 100,000건 생성
-- OrderStats Entity
-- INSERT INTO order_stats (member_id, email, order_count, total_amount, avg_amount, last_order_date, updated_at)
-- WITH RECURSIVE numbers AS (
--     SELECT /*+ SET_VAR(cte_max_recursion_depth = 100000) */ 1 AS n
--     UNION ALL
--     SELECT n + 1 FROM numbers WHERE n < 100000
-- )
-- SELECT
--     n,          -- member_id
--     CONCAT('user', LPAD(n, 7, '0'), '@example.com'), -- 이메일
--     FLOOR(1 + RAND() * 20), -- 주문 횟수
--     FLOOR(50000 + RAND() * 2000000), -- 총 주문 금액
--     FLOOR(20000 + RAND() * 100000), -- 평균 주문 금액
--     DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), -- 마지막 주문일
--     NOW()       -- 업데이트 시간
-- FROM numbers;


-- 비활성화했던 제약 조건 검사를 다시 활성화합니다.
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET SQL_LOG_BIN=1; -- 바이너리 로깅 다시 활성화