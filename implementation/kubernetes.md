# Kubernetes

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Kubernetes is a container orchestration platform for deploying, scaling, and managing microservices.

## Why We Use It
- Standardized deployment platform.
- Automated scaling, healing, and rollout.
- Better portability across cloud providers.

## What We Use
- Deployments, Services, Ingress
- HPA (Horizontal Pod Autoscaler)
- ConfigMaps and Secrets
- Readiness/liveness probes

## How to Implement
1. Containerize services with Docker.
2. Create Kubernetes manifests or Helm charts.
3. Define resource requests/limits for each service.
4. Configure probes for health-based routing and restart.
5. Enable autoscaling by CPU/memory/custom metrics.
6. Use rolling/canary deployments with rollback support.

## Achievements
- High availability and easier operations.
- Better resource utilization.
- Faster, safer release lifecycle.

## Important Code Example

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account-service
spec:
  replicas: 3 # Runs three pods for availability
  selector:
    matchLabels:
      app: account-service
  template:
    metadata:
      labels:
        app: account-service
    spec:
      containers:
        - name: app
          image: myrepo/account-service:1.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness # Traffic only after service is ready
              port: 8080
```
