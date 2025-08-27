import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 옵션
export const options = {
    stages: [
        { duration: '10m', target: 6000 }
    ],
};

// k6 테스트 메인 함수
export default function () {
    // 1. URL과 쿼리 파라미터 설정
    const url = `http://3.38.53.23:80/ready`;

    // 6. POST 요청 실행 및 결과 확인
    const res = http.post(url);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    sleep(1);
}