# Scalability Approaches

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Scalability is the ability of a system to handle growth in users, traffic, and data.

## Why We Use It
- Maintain performance during demand spikes.
- Support business growth without major rewrites.
- Control cost while scaling reliably.

## What We Use
- Horizontal scaling of stateless services
- Caching and CDN
- Async queues and event-driven processing
- Database sharding/read replicas/partitioning

## How to Implement
1. Keep services stateless for easy horizontal scaling.
2. Add caching layers for high-read traffic.
3. Offload heavy/slow tasks to queues and workers.
4. Scale databases with read replicas and partitioning strategy.
5. Use autoscaling policies from real workload metrics.
6. Run load tests and capacity planning regularly.

## Achievements
- Stable performance under growth.
- Better cost-performance balance.
- More predictable user experience at scale.

## Important Code Example

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: checkout-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: checkout-service
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 65 # Scale out when average CPU crosses 65%
```
