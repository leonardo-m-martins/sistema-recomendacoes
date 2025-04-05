curl -X GET http://localhost:8080/api/avaliacao/

curl -X POST http://localhost:8080/api/avaliacao/ ^
-H "Content-Type: application/json" ^
-d @avaliacaoCreate.json

curl -X PATCH http://localhost:8080/api/avaliacao/1 ^
-H "Content-Type: application/json" ^
-d @avaliacaoPatch.json

curl -X PUT http://localhost:8080/api/avaliacao/1 ^
-H "Content-Type: application/json" ^
-d @avaliacaoPut.json

curl -X DELETE http://localhost:8080/api/avaliacao/6
