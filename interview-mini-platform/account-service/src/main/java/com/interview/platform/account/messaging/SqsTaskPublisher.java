package com.interview.platform.account.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SqsTaskPublisher {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public SqsTaskPublisher(SqsClient sqsClient, @Value("${app.aws.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    public void publishTask(String body) {
        // SQS is managed queueing; no broker maintenance required.
        sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(body).build());
    }
}
