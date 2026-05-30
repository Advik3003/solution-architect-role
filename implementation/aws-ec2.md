# AWS EC2

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Amazon EC2 provides virtual servers for running applications and services.

## Why We Use It
- Full control over OS and runtime.
- Useful for legacy workloads and custom stacks.
- Flexible scaling with autoscaling groups.

## What We Use
- Auto Scaling Groups
- Launch templates
- Security groups and IAM roles

## How to Implement
1. Build a hardened base image (AMI).
2. Deploy instances via launch templates.
3. Place instances behind load balancer.
4. Enable autoscaling policies based on metrics.
5. Patch and monitor instances regularly.
6. Use IAM roles instead of static credentials.

## Achievements
- Controlled compute environment.
- Elastic scaling for variable traffic.
- Better reliability with health-based replacement.

## Important Code Example

```bash
# Creates launch template for immutable instance configuration
aws ec2 create-launch-template \
  --launch-template-name app-template \
  --version-description v1 \
  --launch-template-data '{
    "ImageId":"ami-1234567890",
    "InstanceType":"t3.medium",
    "IamInstanceProfile":{"Name":"app-ec2-role"},
    "SecurityGroupIds":["sg-123abc"]
  }'
```

```bash
# Creates auto scaling group behind load balancer target group
aws autoscaling create-auto-scaling-group \
  --auto-scaling-group-name app-asg \
  --launch-template LaunchTemplateName=app-template,Version=1 \
  --min-size 2 --max-size 8 --desired-capacity 2 \
  --vpc-zone-identifier subnet-a,subnet-b \
  --target-group-arns arn:aws:elasticloadbalancing:region:acct:targetgroup/app-tg/123
```
