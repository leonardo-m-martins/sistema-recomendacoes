curl -X GET http://localhost:8080/api/genero/

curl -X POST http://localhost:8080/api/genero/ ^
-H "Content-Type: application/json" ^
-d @generoCreate.json

curl -X PATCH http://localhost:8080/api/genero/5 ^
-H "Content-Type: application/json" ^
-d @generoPatch.json

curl -X PUT http://localhost:8080/api/genero/5 ^
-H "Content-Type: application/json" ^
-d @generoPut.json

curl -X DELETE http://localhost:8080/api/genero/5
