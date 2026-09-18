# Observability Architecture — Digital Banking Platform

Full observability stack: **traces + metrics + logs + alerts + dashboards**.

## Stack
| Layer | Tool | Port |
|-------|------|------|
| Tracing | Zipkin (via OTel Collector) | 9411 |
| Metrics | Prometheus | 9090 |
| Logs | Loki + Promtail | 3100 |
| Dashboards | Grafana | 3000 |
| Alerting | Alertmanager | 9093 |
| Collector | OpenTelemetry Collector | 4317/4318 |

## Run
```bash
docker-compose up -d --build