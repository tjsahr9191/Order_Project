import http from 'k6/http';
import { sleep } from 'k6';

// 1. 'k6/experimental/random' import 구문을 완전히 삭제합니다.

// 필요하다면, 특정 범위의 정수를 생성하는 도우미 함수를 직접 만듭니다.
// 이 함수는 Math.random()을 사용하여 동일한 기능을 수행합니다.
function randomIntBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1) + min);
}

// 테스트 옵션 (예시)
export const options = {
    stages: [
        // 10분에 걸쳐 vus(virtual users, 가상 유저수)가 6000에 도달하도록 설정
        { duration: '10m', target: 6000 }
    ],
};

// k6 테스트의 메인 함수
export default function () {
    // 2. 위에서 만든 도우미 함수를 사용해 랜덤 ID를 생성합니다.
    const randomUserID = randomIntBetween(1, 100000); // 1부터 200 사이의 랜덤 정수 생성

    // 생성된 랜덤 ID를 사용해 API에 요청을 보냅니다. (예시)
    http.get(`http://localhost:8080/api/orders/${randomUserID}`);
    sleep(1); // 1초 대기
}