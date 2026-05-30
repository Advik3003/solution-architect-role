package com.interview.platform.account.cloud;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Service
public class SecretManagerService {

    private final SecretsManagerClient secretsManagerClient;

    public SecretManagerService(SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    public String getSecret(String secretId) {
        // Pulls secrets at runtime; avoids hardcoding credentials in source.
        return secretsManagerClient
            .getSecretValue(GetSecretValueRequest.builder().secretId(secretId).build())
            .secretString();
    }
}
