# Load Balancing Approaches

> Stack reference: `unified-stack-standard-springboot-aws.md`

## What It Is
Load balancing distributes traffic across multiple servers/instances.

## Why We Use It
- Improve availability and performance.
- Prevent overload of single instance.
- Support scaling and failover.

## What We Use
- L4/L7 load balancers
- Round robin, least connections, weighted routing
- Client-side and server-side balancing

## How to Implement
1. Put services behind load balancer endpoints.
2. Configure health checks and auto-removal of unhealthy targets.
3. Choose algorithm based on workload profile.
4. Use sticky sessions only when unavoidable.
5. Add autoscaling with LB metrics integration.
6. Perform load tests and tune connection/timeouts.

## Achievements
- Better response times and system stability.
- Seamless failover during node issues.
- Higher horizontal scalability.

## Important Code Example

```nginx
upstream account_backend {
    least_conn; # Sends new request to server with fewest active connections
    server 10.0.1.11:8080;
    server 10.0.1.12:8080;
}

server {
    listen 80;
    location /api/v1/accounts/ {
        proxy_pass http://account_backend; # Forwards request to healthy backend pool
    }
}
```
