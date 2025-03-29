Action()
{
    char *request_body;
    char *response;
    char *clientId;
    char *messageText;
    char *topic = NULL; // �������������� ��� NULL
    char command[256];
    int clientIdNumber;

    // �������� �������� ����������
    clientId = lr_eval_string("{clientId}"); // �������� clientId
    messageText = lr_eval_string("{message}"); // �������� message

    // ����������� clientId �� ������ � �����
    clientIdNumber = atoi(clientId);

    // �������� �������� ����������
    lr_output_message("clientId: %s (as number: %d)", clientId, clientIdNumber);
    lr_output_message("message: %s", messageText);

    // ������ ���������� ��� ��������� ������� ���������� �������
    lr_start_transaction("Send_Message");

    // ��������� ���� ������� � �������������� ����������
    request_body = lr_eval_string("{\"clientId\": \"{clientId}\", \"message\": \"{message}\"}");

    // �������� ���� �������
    lr_output_message("Sending request with body: %s", request_body);

    // ������������ �������� ��� ���������� ���� ������
    web_reg_save_param("response", "LB=", "RB=", "Search=Body", LAST);

    // ���������� POST-������
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
        "EncType=application/json", // ��������� ��� �����������
        LAST);

    // ��������� ���� ������
    response = lr_eval_string("{response}");

    // �������� ����� �� �������
    lr_output_message("Response from service: %s", response);

    // ��������� ������ ������
    if (strstr(response, "\"status\":\"success\"") != NULL) {
        // �������� �����
        lr_output_message("Request successful.");

        // ���������, ���� �� ��������� ���������
        if (strstr(response, "\"enrichedMessage\":\"Message enriched and sent to Kafka\"") != NULL) {
            lr_output_message("Message was enriched and sent to Kafka.");
            topic = "enriched-messages-topic"; // ����������� ���������
        } else if (strstr(response, "\"enrichedMessage\":\"Message sent to Kafka without enrichment\"") != NULL) {
            lr_output_message("Message was sent to Kafka without enrichment.");
            topic = "raw-messages-topic"; // ������������� ���������
        } else {
            lr_output_message("Unknown enrichedMessage value in response. Message will not be sent to Kafka.");
            topic = NULL; // �� ���������� � Kafka
        }

        lr_end_transaction("Send_Message", LR_PASS); // �������� ����������
    } else {
        // ������
        lr_error_message("Request failed. Response: %s", response);
        lr_end_transaction("Send_Message", LR_FAIL); // ������
        topic = NULL; // �� ���������� � Kafka
    }

    // ���� ����� ������, ���������� ��������� � Kafka
    if (topic != NULL) {
        // �������� ��������� �����
        lr_output_message("Selected Kafka topic: %s", topic);

        // ���������� kafka-console-consumer ��� ������ ��������� �� Kafka
        sprintf(command, "kafka-console-consumer --bootstrap-server localhost:9092 --topic %s --max-messages 1 --timeout-ms 5000", topic);

        // �������� ������� ��� Kafka
        lr_output_message("Executing Kafka command: %s", command);

        // ��������� �������
        system(command);

        // �������� ���������� ���������� ������� Kafka
        lr_output_message("Kafka command executed successfully.");
    } else {
        // ��������, ��� ��������� �� ���������� � Kafka
        lr_output_message("Message will not be sent to Kafka.");
    }

    return 0;
}