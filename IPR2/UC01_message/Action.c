Action()
{
    char *request_body;
    char *response;
    char *clientId;
    char *messageText;
    char *topic = NULL; // Инициализируем как NULL
    char command[256];
    int clientIdNumber;

    // Получаем значения параметров
    clientId = lr_eval_string("{clientId}"); // Параметр clientId
    messageText = lr_eval_string("{message}"); // Параметр message

    // Преобразуем clientId из строки в число
    clientIdNumber = atoi(clientId);

    // Логируем значения параметров
    lr_output_message("clientId: %s (as number: %d)", clientId, clientIdNumber);
    lr_output_message("message: %s", messageText);

    // Начало транзакции для измерения времени выполнения запроса
    lr_start_transaction("Send_Message");

    // Формируем тело запроса с использованием параметров
    request_body = lr_eval_string("{\"clientId\": \"{clientId}\", \"message\": \"{message}\"}");

    // Логируем тело запроса
    lr_output_message("Sending request with body: %s", request_body);

    // Регистрируем параметр для извлечения тела ответа
    web_reg_save_param("response", "LB=", "RB=", "Search=Body", LAST);

    // Отправляем POST-запрос
    web_custom_request("send_message",
        "URL=http://localhost:8080/message",
        "Method=POST",
        "Body={\"clientId\": \"{clientId}\", \"message\": \"{message}\"}",
        "TargetFrame=",
        "Resource=0",
        "RecContentType=application/json",
        "Referer=",
        "Snapshot=t1.inf",
        "Mode=HTML",
        "EncType=application/json", // Указываем тип содержимого
        LAST);

    // Извлекаем тело ответа
    response = lr_eval_string("{response}");

    // Логируем ответ от сервиса
    lr_output_message("Response from service: %s", response);

    // Проверяем статус ответа
    if (strstr(response, "\"status\":\"success\"") != NULL) {
        // Успешный ответ
        lr_output_message("Request successful.");

        // Проверяем, было ли сообщение обогащено
        if (strstr(response, "\"enrichedMessage\":\"Message enriched and sent to Kafka\"") != NULL) {
            lr_output_message("Message was enriched and sent to Kafka.");
            topic = "enriched-messages-topic"; // Обогащенные сообщения
        } else if (strstr(response, "\"enrichedMessage\":\"Message sent to Kafka without enrichment\"") != NULL) {
            lr_output_message("Message was sent to Kafka without enrichment.");
            topic = "raw-messages-topic"; // Необогащенные сообщения
        } else {
            lr_output_message("Unknown enrichedMessage value in response. Message will not be sent to Kafka.");
            topic = NULL; // Не отправляем в Kafka
        }

        lr_end_transaction("Send_Message", LR_PASS); // Успешное завершение
    } else {
        // Ошибка
        lr_error_message("Request failed. Response: %s", response);
        lr_end_transaction("Send_Message", LR_FAIL); // Ошибка
        topic = NULL; // Не отправляем в Kafka
    }

    // Если топик выбран, отправляем сообщение в Kafka
    if (topic != NULL) {
        // Логируем выбранный топик
        lr_output_message("Selected Kafka topic: %s", topic);

        // Используем kafka-console-consumer для чтения сообщения из Kafka
        sprintf(command, "kafka-console-consumer --bootstrap-server localhost:9092 --topic %s --max-messages 1 --timeout-ms 5000", topic);

        // Логируем команду для Kafka
        lr_output_message("Executing Kafka command: %s", command);

        // Выполняем команду
        system(command);

        // Логируем завершение выполнения команды Kafka
        lr_output_message("Kafka command executed successfully.");
    } else {
        // Логируем, что сообщение не отправлено в Kafka
        lr_output_message("Message will not be sent to Kafka.");
    }

    return 0;
}