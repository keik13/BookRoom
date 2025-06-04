# bigdata-api-inform-exchange

## Инструкция локального запуска

Для работы требуется установить следующие приложения:

- [docker](https://docs.docker.com/install/)
- [docker-compose](https://docs.docker.com/compose/install/)
- Java Development Kit 11
- [sbt](https://www.scala-sbt.org/download.html)

## Запуск сервисов

В корне репозитория находится файл [docker-compose.yml](./docker-compose.yml).
С его помощью можно запустить необходимые для работы сервисы командной: ``$ docker compose up -d``.

Сервис запускается командной ``$ sbt run``, сделанной в корневой дериктории проекта.

### Первый запуск

Для минимального кейса использования приложения необходимо:

- Поднять postgres (``docker compose up -d postgres``)
- Запустить BookRoom

После этого можно сделать запросы, например,

```
curl -i --request POST -d '{"roomId":1, "employee": "Ivan Ivanov", "beginAt": "2025-06-05T14:25:00.400Z" , "endAt": "2025-06-05T18:00:00.000Z"}' "http://localhost:8080/book"
```

```
curl -i --request GET "http://localhost:8080/room/1"
```

```
curl -i --request GET "http://localhost:8080/room"
```