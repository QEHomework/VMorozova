  1) Тело запроса содержит JSON с полями:

     {
       "clientId": "<для обогащенного запроса числа от 1 до 10>",
       "message": "<любая строка>"
     }

  2) БД PostrgeSQL

  CREATE TABLE clients (
  client_id VARCHAR PRIMARY KEY,
  name VARCHAR NOT NULL,
  email VARCHAR,
  phone VARCHAR);
  INSERT INTO clients (client_id, name, email, phone) VALUES
  ('1', 'Иван Иванов', 'ivan.ivanov@example.com', '+79161234567'),
  ('2', 'Мария Петрова', 'maria.petrova@example.com', '+79162345678'),
  ('3', 'Алексей Смирнов', 'aleksey.smirnov@example.com', '+79163456789'),
  ('4', 'Ольга Кузнецова', 'olga.kuznetsova@example.com', '+79164567890'),
  ('5', 'Сергей Васильев', 'sergey.vasiliev@example.com', '+79165678901'),
  ('6', 'Анна Новикова', 'anna.novikova@example.com', '+79166789012'),
  ('7', 'Дмитрий Морозов', 'dmitry.morozov@example.com', '+79167890123'),
  ('8', 'Елена Волкова', 'elena.volkova@example.com', '+79168901234'),
  ('9', 'Павел Романов', 'pavel.romanov@example.com', '+79169012345'),
  ('10', 'Татьяна Лебедева', 'tatiana.lebedeva@example.com', '+79160123456');

  3) KAFKA
  Запуск zookeeper:
  C:\kafka\bin\windows\zookeeper-server-start.bat config\zookeeper.properties
  Запуск kafka:
  C:\kafka\bin\windows\kafka-server-start.bat config\server.properties

  Топик для обогащенных сообщений:
  C:\kafka\bin\kafka-topics.sh --create --topic enriched-messages-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
  Подписка:
  C:\kafka\bin\kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic enriched-messages-topic --from-beginning
  Топик для необогащенных сообщений:
  C:\kafka\bin\kafka-topics.sh --create --topic raw-messages-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
  Подписка:
  C:\kafka\bin\kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic raw-messages-topic --from-beginning

  4) ConfigService
  По умолчанию выставлены значения 0
  Установить задержку для сервиса message в 5000 мс: GET http://localhost:8080/setDelay?serviceName=message&delayMs=5000
  Установить частоту ошибок для сервиса message в 2%: GET http://localhost:8080/setErrorRate?serviceName=message&errorRate=2

  5) System controller
  Реализован через библиотеку, поэтому запрос отправлять на фиксированный /actuator/health

  6) Метрики
  http_server_requests_seconds_count количество HTTP-запросов.
  http_server_requests_seconds_sum время обработки запроса
  http_errors_total Количество ошибок
  kafka_messages_sent Количество отправленных сообщений в Kafka

