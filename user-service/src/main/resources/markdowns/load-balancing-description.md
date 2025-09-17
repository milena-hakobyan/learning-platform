# Spring Cloud LoadBalancer Test Results – USER-SERVICE

## Objective
Test client-side load balancing for `USER-SERVICE` with multiple instances registered in Eureka, and observe request distribution using different algorithms.

---

## Setup
- `USER-SERVICE` scaled to 3 instances using Docker Compose.
- Default load balancing algorithm: Round-robin.

---

## Observations

### 1. Default Round-Robin
- Requests were routed sequentially across the three instances.
- Example request distribution:

| Request # | Instance   |
|-----------|------------|
| 1         | Instance 1 |
| 2         | Instance 2 |
| 3         | Instance 3 |
| 4         | Instance 1 |
| 5         | Instance 2 |

- Confirms that Round-robin distributes requests evenly in order.

### 2. Random Load Balancer
- Configured custom `RandomLoadBalancer` for `USER-SERVICE`.
- Requests were routed randomly to one of the three instances.
- Example request distribution:

| Request # | Instance   |
|-----------|------------|
| 1         | Instance 2 |
| 2         | Instance 1 |
| 3         | Instance 3 |
| 4         | Instance 3 |
| 5         | Instance 1 |

- Confirms that the random algorithm selects instances non-sequentially as expected.

---

## Conclusion
- Spring Cloud LoadBalancer effectively spreads requests across multiple service instances.
- Changing the load balancing algorithm from Round-robin to Random works as intended.
- Ensures better load distribution and can be configured per service as needed.
