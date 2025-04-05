curl -X GET http://localhost:8080/api/usuario/

curl -X POST http://localhost:8080/api/usuario/ ^
-H "Content-Type: application/json" ^
-d @usuarioCreate.json

curl -X PATCH http://localhost:8080/api/usuario/1 ^
-H "Content-Type: application/json" ^
-d @usuarioPatch.json

curl -X PUT http://localhost:8080/api/usuario/1 ^
-H "Content-Type: application/json" ^
-d @usuarioPut.json

curl -X DELETE http://localhost:8080/api/usuario/6