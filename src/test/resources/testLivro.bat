curl -X GET http://localhost:8080/api/livro/

curl -X POST http://localhost:8080/api/livro/ ^
-H "Content-Type: application/json" ^
-d @livroCreate.json

curl -X PATCH http://localhost:8080/api/livro/10 ^
-H "Content-Type: application/json" ^
-d @livroPatch.json

curl -X PUT http://localhost:8080/api/livro/10 ^
-H "Content-Type: application/json" ^
-d @livroPut.json
