import http from 'k6/http';
import { check } from 'k6';

const API_KEY = __ENV.API_KEY;

export const options = { vus: 10, duration: '10s' };

export default function () {
  const res = http.post(
    'http://localhost/v1/chat',
    JSON.stringify({ prompt: 'hi' }),
    { headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${API_KEY}` } }
  );
  check(res, { 'status is 200 or 429': (r) => r.status === 200 || r.status === 429 });
}
