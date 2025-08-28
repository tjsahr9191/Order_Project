-- 데이터베이스가 존재하면 삭제하고 새로 생성합니다.
DROP DATABASE IF EXISTS order_db;
CREATE DATABASE order_db;

-- 사용할 데이터베이스를 선택합니다.
USE order_db;

-- 1. 회원 (member) 테이블
-- 'email'은 고유해야 하므로 UNIQUE 인덱스를 추가합니다.
CREATE TABLE member (
                        member_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email           VARCHAR(255) NOT NULL,
                        name            VARCHAR(255) NOT NULL,
                        city            VARCHAR(255),
                        street          VARCHAR(255),
                        zipcode         VARCHAR(255),
                        created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at      DATETIME(6),
                        UNIQUE INDEX uk_member_email (email)
) COMMENT '회원 정보';

-- 2. 상품 (product) 테이블
-- 'product_no'는 고유해야 하므로 UNIQUE 인덱스를 추가합니다.
CREATE TABLE product (
                         product_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name            VARCHAR(255) NOT NULL,
                         product_no      VARCHAR(255) NOT NULL,
                         price           INT NOT NULL,
                         stock           INT NOT NULL,
                         created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                         deleted_at      DATETIME(6),
                         UNIQUE INDEX uk_product_product_no (product_no)
) COMMENT '상품 정보';

-- 3. 배송 (delivery) 테이블
CREATE TABLE delivery (
                          delivery_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
                          delivery_status VARCHAR(255) NOT NULL,
                          city            VARCHAR(255),
                          street          VARCHAR(255),
                          zipcode         VARCHAR(255),
                          created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                          updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                          deleted_at      DATETIME(6)
) COMMENT '배송 정보';

-- 4. 결제 (payment) 테이블
-- 'order_no'와 'tid'는 검색에 자주 사용될 수 있으므로 인덱스를 추가합니다.
CREATE TABLE payment (
                         payment_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_no            VARCHAR(255) NOT NULL,
                         payment_method      VARCHAR(255) NOT NULL,
                         tid                 VARCHAR(255) NOT NULL,
                         total_amount        INT NOT NULL,
                         discount_amount     INT,
                         tax_free_amount     INT,
                         card_issuer_name    VARCHAR(255),
                         card_purchase_name  VARCHAR(255),
                         card_install_month  VARCHAR(255),
                         approved_at         DATETIME(6),
                         created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                         deleted_at          DATETIME(6),
                         INDEX idx_payment_order_no (order_no),
                         INDEX idx_payment_tid (tid)
) COMMENT '결제 정보';

-- 5. 주문 (orders) 테이블
-- member, payment, delivery 테이블과 관계를 맺습니다.
-- payment_id와 delivery_id는 1:1 관계이므로 UNIQUE 제약 조건을 추가합니다.
CREATE TABLE orders (
                        order_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id        BIGINT NOT NULL,
                        payment_id       BIGINT NOT NULL,
                        delivery_id      BIGINT NOT NULL,
                        name             VARCHAR(255) NOT NULL,
                        no               VARCHAR(255) NOT NULL,
                        tid              VARCHAR(255),
                        total_amount     INT NOT NULL,
                        total_discount   INT,
                        real_price       INT NOT NULL,
                        city             VARCHAR(255),
                        street           VARCHAR(255),
                        zipcode          VARCHAR(255),
                        created_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        deleted_at       DATETIME(6),
                        UNIQUE INDEX uk_orders_payment_id (payment_id),
                        UNIQUE INDEX uk_orders_delivery_id (delivery_id),
                        INDEX idx_orders_no (no),
                        CONSTRAINT fk_orders_to_member FOREIGN KEY (member_id) REFERENCES member (member_id),
                        CONSTRAINT fk_orders_to_payment FOREIGN KEY (payment_id) REFERENCES payment (payment_id),
                        CONSTRAINT fk_orders_to_delivery FOREIGN KEY (delivery_id) REFERENCES delivery (delivery_id)
) COMMENT '주문 정보';

-- 6. 주문 상세 (order_detail) 테이블
-- orders, product 테이블과 관계를 맺습니다.
CREATE TABLE order_detail (
                              order_detail_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
                              order_id         BIGINT NOT NULL,
                              product_id       BIGINT NOT NULL,
                              price            INT NOT NULL,
                              quantity         INT NOT NULL,
                              created_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                              updated_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                              deleted_at       DATETIME(6),
                              CONSTRAINT fk_order_detail_to_orders FOREIGN KEY (order_id) REFERENCES orders (order_id),
                              CONSTRAINT fk_order_detail_to_product FOREIGN KEY (product_id) REFERENCES product (product_id)
) COMMENT '주문 상세 정보';

-- 7. 주문 통계 (order_stats) 테이블 (주석 처리된 INSERT 문 기준)
-- CREATE TABLE order_stats (
--     order_stats_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
--     member_id        BIGINT NOT NULL,
--     email            VARCHAR(255) NOT NULL,
--     order_count      INT NOT NULL,
--     total_amount     BIGINT NOT NULL,
--     avg_amount       INT NOT NULL,
--     last_order_date  DATETIME(6),
--     updated_at       DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
--     UNIQUE INDEX uk_order_stats_member_id (member_id)
-- ) COMMENT '회원별 주문 통계';