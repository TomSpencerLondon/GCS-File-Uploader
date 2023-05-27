## Ipv4 address classes
#### 1. Class A

Class A starts from 10.0.0.0 to 10.255.255.255
Cidr range starts from 10.0.0.0/8 to 10.0.0.0/32

IPs:
10.0.0.0
10.0.0.1
...
10.0.0.255

10.0.1.0
10.0.1.1
...
10.0.1.255
...
10.0.2.0
10.0.2.1
10.0.2.2
...
10.0.2.255
10.0.3.0
10.0.3.1
...

Cidr range 8 means you can change
_.0.0.0
^
not change

How do we calculate the total number of hosts that can reside in a network?
eg. vpc cidr is - 10.0.0.0/8
- 32 - 8 = 24
Therefore total number of hosts is equal to 2^24

#### 2. Class B
172.16.0.0/12
172.16.0.0/32

starting IP:
172.16.0.0

last IP:
172.31.255.255

#### 3. Class C
- 192.168.0.0/16
- 192.168.0.0/32


====
Challenge:
Create technical architecture - networking + GKE
Kubeflow installed in GKE for MLOps team
Current MLOps users are 400
Each user creates up to 20 or 30 Jupyter notebooks using the Kubeflow UI
Each notework (created by user) creates a pod in Kubernetes cluster
Each pod uses an IP address from the IP range
Calculate the CIDR range for the use case assuming users will increase to 600

600 * 30 = 18000 IP addresses

This link is useful for cidr ranges:
https://www.ipaddressguide.com/cidr

![image](https://user-images.githubusercontent.com/27693622/236614129-ac00e508-1dc2-447a-b425-09fa0ed08c78.png)


kubectl port-forward svc/ingest-webapp 8080:80