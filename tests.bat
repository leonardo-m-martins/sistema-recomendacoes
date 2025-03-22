curl http://localhost:8080/api/all
curl -X POST http://localhost:8080/api/add -d username=teste -d password=123456789
curl http://localhost:8080/api/all
curl -X POST http://localhost:8080/api/remove -d username=teste
curl http://localhost:8080/api/all