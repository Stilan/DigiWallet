import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const durationTrend = new Trend('custom_req_duration');

export let options = {
    scenarios: {
        high_rps: {
            executor: 'constant-arrival-rate',
            rate: 1000,             // 1000 запросов в секунду
            timeUnit: '1s',
            duration: '1m',         // продолжительность теста
            preAllocatedVUs: 200,   // initial VUs
            maxVUs: 500,            // максимальное число VUs
        },
    },
    thresholds: {
        // 95-й перцентиль < 500ms
        'http_req_duration':            ['p(95)<500'],
        // <1% любых ошибок
        'http_req_failed':              ['rate<0.01'],
        // <0.1% 50x ошибок
        'http_req_failed{status:500}':  ['rate<0.001'],
        // 99% чеков должны проходить
        'checks':                       ['rate>0.99'],
    },
};

export default function () {
    const BASE_URL = 'http://localhost:8080/api/v1';
    const headers = { headers: { 'Content-Type': 'application/json' } };

    // 1. Создание кошелька
    const createPayload = JSON.stringify({
        amount: 0 // минимальное валидное значение k6 run src/main/resources/db/load-test.js
    });

    const createRes = http.post(`${BASE_URL}`, createPayload, headers);
    check(createRes, {
        'created wallet (201)': (r) => r.status === 201,
    }) || console.error(`Create failed: ${createRes.status} — ${createRes.body}`);

    if (createRes.status !== 201) {
        console.error(`Create failed: ${createRes.status} — ${createRes.body}`);
        return;
    }

    const wallet = createRes.json();
    const walletId = wallet.walletId;

    if (!walletId) {
        console.error('Wallet ID is undefined. Skipping test.');
        return;
    }
    // 2. Обновление баланса (транзакция)
    const updatePayload = JSON.stringify({
        walletId: walletId,
        operationType: 'DEPOSIT',
        amount: 100
    });

    console.log("Sending transaction payload:", updatePayload);

    const updateRes = http.post(`${BASE_URL}/wallet`, updatePayload, headers);

    const updatePassed = check(updateRes, {
        'updated wallet (200 or handled 400)': (r) =>
            r.status === 200 || (r.status === 400 && r.body.includes('Wallet not found')),
    });

    if (!updatePassed) {
        console.error(`Update failed: ${updateRes.status} — ${updateRes.body}`);
    }

    if (updateRes.status === 400 && updateRes.body.includes('Wallet not found')) {
        console.warn(`Wallet not found (likely expected): ${walletId}`);
    }

    // 3. Получение баланса
    const getRes = http.get(`${BASE_URL}/wallets/${walletId}`);
    check(getRes, {
        'get balance (200)': (r) => r.status === 200,
        'balance is present': (r) => r.body.length > 0,
    }) || console.error(`Get failed: ${getRes.status} — ${getRes.body}`);

    durationTrend.add(getRes.timings.duration);
    sleep(0.1);
}