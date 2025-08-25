import http from 'k6/http';
import { check, sleep } from 'k6';

// 랜덤 정수를 생성하는 도우미 함수
function randomIntBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1) + min);
}

// 테스트 옵션
export const options = {
    stages: [
        { duration: '10m', target: 6000 }
    ],
};

// k6 테스트 메인 함수
export default function () {
    // 1. URL과 쿼리 파라미터 설정
    const memberId = randomIntBetween(1, 100000);
    const url = `http://localhost:8080/api/orders?memberId=${memberId}`;

    // 2. Request Body의 productValues 배열 생성
    // DTO의 List<CreateOrderRequest.ProductInfo>에 해당합니다.
    const productValues = [
        {
            productId: randomIntBetween(1, 200),
            price: randomIntBetween(10000, 50000), // 10,000원에서 50,000원 사이의 가격
            quantity: randomIntBetween(1, 3),
        },
        // 필요하다면 여기에 상품을 더 추가할 수 있습니다.
        // {
        //     productId: randomIntBetween(201, 400),
        //     price: randomIntBetween(5000, 20000),
        //     quantity: randomIntBetween(1, 2),
        // }
    ];

    // 3. productValues를 기반으로 totalAmount 동적 계산
    // DTO의 totalAmount 필드에 해당합니다.
    const totalAmount = productValues.reduce((sum, p) => sum + (p.price * p.quantity), 0);

    // 4. 최종 Request Body (`payload`) 생성
    const payload = JSON.stringify({
        totalAmount: totalAmount,
        orderName: `테스트 주문 ${randomIntBetween(1, 1000)}`, // DTO의 orderName 필드
        productValues: productValues
    });

    // 5. Request Headers 설정
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // 6. POST 요청 실행 및 결과 확인
    const res = http.post(url, payload, params);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    sleep(1);
}