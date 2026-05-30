# AWS Lambda

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
AWS Lambda is serverless compute to run code on-demand without managing servers.

## Why We Use It
- Good for event-driven and short-running tasks.
- Pay only for execution time.
- Fast integration with AWS services.

## What We Use
- Lambda with API Gateway, S3, SQS, EventBridge triggers
- IAM roles and environment variables
- CloudWatch logs and metrics

## How to Implement
1. Identify event-driven tasks (file processing, notifications, hooks).
2. Write small stateless functions with clear timeout limits.
3. Configure triggers and IAM least privilege.
4. Store secrets in Secrets Manager/Parameter Store.
5. Add retries and DLQ for asynchronous invocations.
6. Monitor cold starts, duration, and error rates.

## Achievements
- Faster feature delivery for event workloads.
- Lower operational overhead.
- Cost efficiency for intermittent tasks.

## Important Code Example

```javascript
export const handler = async (event) => {
  // Reads SQS message payload from event records
  for (const record of event.Records || []) {
    const body = JSON.parse(record.body);
    console.log("Processing order:", body.orderId); // Visible in CloudWatch logs
  }

  // Returning success removes message from queue
  return { statusCode: 200, body: "Processed" };
};
```

```bash
# Deploys zipped function code to Lambda
aws lambda update-function-code \
  --function-name process-orders \
  --zip-file fileb://function.zip
```
