# AWS S3

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Amazon S3 is object storage for files, media, backups, and documents.

## Why We Use It
- Highly durable and scalable storage.
- Cost-effective for unstructured data.
- Integrates with CDN and serverless workflows.

## What We Use
- Buckets with lifecycle policies
- Versioning and encryption
- Pre-signed URLs for secure file access

## How to Implement
1. Create separate buckets by environment and data type.
2. Enable encryption (SSE-S3 or SSE-KMS).
3. Apply bucket policies and least-privilege IAM access.
4. Enable versioning and lifecycle rules.
5. Use pre-signed URLs for upload/download from frontend.
6. Add logging and access monitoring.

## Achievements
- Secure and scalable file handling.
- Lower storage management overhead.
- Better compliance and retention control.

## Important Code Example

```java
public String createUploadUrl(String bucket, String key) {
    // Generates temporary URL so frontend can upload directly to S3
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
    // For upload use PutObjectPresignRequest; kept short for concept demo
    return s3Presigner.presignGetObject(b -> b.signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(request)).url().toString();
}
```

```bash
# Enforces encryption at rest on bucket by default
aws s3api put-bucket-encryption \
  --bucket my-secure-bucket \
  --server-side-encryption-configuration '{
    "Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]
  }'
```
