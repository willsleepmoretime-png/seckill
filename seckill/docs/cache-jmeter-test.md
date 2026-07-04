# Redis API Cache + JMeter Test Guide

## 1. Prepare Test Data

Run the SQL in `src/test/resources/test-data/cache_test_data.sql` against `seckill_db`.

It creates:

- 10 goods rows, ids `900001` to `900010`
- 3 seckill rows, ids `910001` to `910003`
- in-progress seckill windows from `2026-07-03 00:00:00` to `2026-12-31 23:59:59`

## 2. Start Dependencies

Check `src/main/resources/application.yml`:

- App: `http://localhost:8028`
- MySQL: `localhost:3306/seckill_db`
- Redis: `192.168.50.129:6379`
- RabbitMQ: `localhost:5672`

Start the Spring Boot app after MySQL, Redis, and RabbitMQ are ready.

## 3. Warm Redis Stock

Use a login token, then preheat the seckill stock:

```bash
curl -X POST http://localhost:8028/api/user/register \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"18800000001\",\"password\":\"123456\"}"

curl -X POST http://localhost:8028/api/user/login \
  -H "Content-Type: application/json" \
  -d "{\"phone\":\"18800000001\",\"password\":\"123456\"}"

curl -X POST http://localhost:8028/api/seckill/910001/preheat \
  -H "Authorization: <token-from-login>"
```

Redis stock keys:

- `seckill:stock:910001`
- `seckill:stock:910002`
- `seckill:stock:910003`

## 4. Cache API Test Flow

The stats endpoint only reads counters:

```http
GET /api/cache/stats
```

To create meaningful Redis/cache data, JMeter should call these first:

```http
GET /api/goods/900001
GET /api/goods/list?order=asc
GET /api/seckill/910001
GET /api/seckill/list/in-progress
GET /api/goods/999999999
```

Expected behavior:

- First existing id request: Redis miss + DB query, then data is cached.
- Repeated existing id request: Caffeine hit first; if local cache expires, Redis hit.
- Non-existing id request: Bloom filter reject if the id is not in the Bloom filter.
- `GET /api/cache/stats`: observe `redisHit`, `redisMiss`, `dbQuery`, `bloomReject`, and Caffeine stats.

## 5. JMeter Setup

Use these CSV files:

- `src/test/resources/jmeter/users.csv`
- `src/test/resources/jmeter/cache_targets.csv`

Recommended Thread Group:

- Threads: `50`
- Ramp-up: `10`
- Loop count: `20`

JMeter samplers:

1. `POST /api/user/register`
   - Body: `{"phone":"${phone}","password":"${password}"}`
   - This may return "phone already registered" after the first run. That is OK.
2. `POST /api/user/login`
   - Body: `{"phone":"${phone}","password":"${password}"}`
   - JSON Extractor: `token = $.data.token`
3. HTTP Header Manager
   - `Authorization: ${token}`
   - `Content-Type: application/json`
4. `GET ${path}`
   - Read `path` from `cache_targets.csv`
5. `GET /api/cache/stats`

Use Summary Report or Aggregate Report to compare:

- average latency
- p95 / p99 latency
- error percentage
- throughput
- stats response values before and after the run

## 6. Useful Redis Checks

```bash
redis-cli -h 192.168.50.129 -p 6379 keys "goods:*"
redis-cli -h 192.168.50.129 -p 6379 keys "seckill:*"
redis-cli -h 192.168.50.129 -p 6379 get "seckill:stock:910001"
```

If you want to retest cold cache:

```bash
redis-cli -h 192.168.50.129 -p 6379 del "goods:detail:900001"
redis-cli -h 192.168.50.129 -p 6379 del "goods:list:on-sale:asc"
redis-cli -h 192.168.50.129 -p 6379 del "seckill:detail:910001"
redis-cli -h 192.168.50.129 -p 6379 del "seckill:list:in-progress"
```
