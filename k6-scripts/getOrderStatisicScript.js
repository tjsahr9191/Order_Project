import http from 'k6/http';
import { sleep } from 'k6';

// 특정 범위의 랜덤 정수를 생성하는 도우미 함수
function randomIntBetween(min, max) {
    // min과 max 사이의 랜덤 정수를 반환합니다.
    return Math.floor(Math.random() * (max - min + 1) + min);
}

// 테스트 옵션: 부하 시나리오를 정의합니다.
export const options = {
    stages: [
        { duration: '10m', target: 6000 }
    ]
};

// k6 테스트의 메인 함수 (가상 유저가 반복 실행하는 코드)
export default function () {
    // 1. 요청에 사용할 랜덤 파라미터를 생성합니다.
    const randomPage = randomIntBetween(1, 100);
    const randomMinAmount = randomIntBetween(0, 99999); // 100,000 미만 (0 ~ 99,999)

    // 2. 랜덤 파라미터를 포함하여 API URL을 동적으로 구성합니다.
    const url = `http://15.165.251.129:8080/api/orders/statistics/stats/v1?page=${randomPage}&minAmount=${randomMinAmount}`;

    // 3. 구성된 URL로 GET 요청을 보냅니다.
    http.get(url);

    // 4. 다음 요청을 보내기 전 1초간 대기합니다.
    sleep(1);
}