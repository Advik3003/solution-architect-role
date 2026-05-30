package com.interview.platform.account.cloud;

import java.time.Duration;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3PresignService {

    private final S3Presigner s3Presigner;

    public S3PresignService(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    public String createDownloadUrl(String bucket, String key) {
        // Pre-signed URL enables secure temporary file access without sharing credentials.
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .getObjectRequest(getObjectRequest)
            .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
