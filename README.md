### GCS file uploader
- How to deploy to Google Kubernetes
- Shell script for github
- build api for creating folder and file in GCS and add data to it

Controller -> Service -> connects to GCP GCS and stores data

First we create the controller for uploading data:
```java

@Slf4j
@RestController
public class GCSController {

    @Value("${gcs.bucket}")
    private String gcsBucket;

    @PostMapping("/uploaddata/{folder_name}")
    public ResponseEntity<String> uploadData(@RequestBody(required = true) byte[] requestEntity,
                                             @PathVariable("folder_name") String folderName) {
        log.info("Received folder name for creation: {}", folderName);

        Storage storage = StorageOptions.getDefaultInstance().getService();
        String folder = String.format("%s/file_%s.csv",
                folderName,
                System.currentTimeMillis());
        BlobId blobId = BlobId.of(this.gcsBucket, folder);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        Blob blob = storage.create(blobInfo, requestEntity);
        System.out.println(blob);
        return ResponseEntity
                .ok()
                .body("Successful upload to " + folderName + "/");
    }

}


```

### Here is how I fixed gcloud cli setup:
https://askubuntu.com/questions/1156409/conflicting-values-set-for-option-signed-by-regarding-source
Then we set up google cloud on our computer:
```bash
#### Step 1: Remove sudo rm google-cloud-sdk.list
cd /etc/apt/sources.list.d
sudo rm google-cloud-sdk.list

#### Step 2: Reinstall Google Cloud again
sudo snap remove google-cloud-sdk
sudo apt-get install apt-transport-https ca-certificates gnupg -y
echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | sudo tee -a /etc/apt/sources.list.d/google-cloud-sdk.list
sudo curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key --keyring /usr/share/keyrings/cloud.google.gpg add -
sudo apt-get update && sudo apt-get install google-cloud-sdk
gcloud init
```
We then login to google cloud:
```bash
gcloud auth login
gcloud auth application-default login
```

We can also set the project with:
```bash
gcloud config set project <Project_ID>
```
For the moment we will create the bucket on google cloud:
![image](https://user-images.githubusercontent.com/27693622/232205321-4533d3e4-9965-4e95-b4ef-48130d741318.png)

We should also add the bucket name to the application.properties:
```properties
gcs.bucket=tom-london-bucket
```

### Note
This curl request works with HttpRequestEntity:
```bash
curl --verbose -k -X POST -H "Content-Type: application/text; charset=UTF-8" --data-binary "hello" "http://localhost:8080/uploaddata/folder1"
```


### Docker
- How to write dockerfile for Spring Boot application
- How to build docker image using the dockerfile
- How to run docker container using the docker image
- How to exec into the container
This link is useful for gcloud application-default auth login inside the docker container:
https://medium.com/datamindedbe/application-default-credentials-477879e31cb5

### How to enable GCR (Google Container Registry) on GCP (Google Cloud Platform) project
![image](https://user-images.githubusercontent.com/27693622/233775130-3611d815-3ab9-4038-bb86-c1fe5b62be14.png)



### Notes on config for GCP
- the default region is us-central1
- cat .config/gcloud/configurations/config_default shows the set region of the project
- We have seen how to set the region:
    - gcloud config set compute/region us-central1
- We were successful in getting the sha value for the image on GCR
    - gcloud container images list-tags --format="get(digest,digest[7:])" IMAGE-NAME
    - but we couldn't work out how to get the region from the sha value
- This is what the sha contains:
  gcloud container images describe gcr.io/devops-rnd-383110/gcswebapp@sha256:7f538d912ed11db461b3260576490ce6e6ae78347783e7c514c20ef471cfea19
  image_summary:
  digest: sha256:7f538d912ed11db461b3260576490ce6e6ae78347783e7c514c20ef471cfea19
  fully_qualified_digest: gcr.io/devops-rnd-383110/gcswebapp@sha256:7f538d912ed11db461b3260576490ce6e6ae78347783e7c514c20ef471cfea19
  registry: gcr.io
  repository: devops-rnd-383110/gcswebapp

Plan for next week:
- learn kubernetes - how to deploy spring boot app to kubernetes


### Export Kubeconfig
If using multiple config files for clusters:
```bash
export KUBECONFIG="/home/tom/.kube/config"
```
Add cluster to config
```bash
gcloud container clusters get-credentials tom-cluster --region us-central1 --project devops-rnd-383110
```
Select cluster in config:
```bash
tom@tom-ubuntu:~/Projects$ kubectx
gke_devops-rnd-383110_us-central1_tom-cluster
minikube
tom@tom-ubuntu:~/Projects$ kubectx gke_devops-rnd-383110_us-central1_tom-cluster 
✔ Switched to context "gke_devops-rnd-383110_us-central1_tom-cluster".
```

List namespaces:
```bash
kubectl get ns
```
Get events:
```bash
kubectl get events
```

There is a useful cheatsheet here for kubernetes:
https://kubernetes.io/docs/reference/kubectl/cheatsheet/

This is how to get logs:
```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectl logs gcs-uploader-webapp-6446865d8-ng8n8 
Error from server (BadRequest): container "gcs-uploader-webapp" in pod "gcs-uploader-webapp-6446865d8-ng8n8" is waiting to start: ContainerCreating
```

This cheatsheet is useful for kubectl:
https://kubernetes.io/docs/reference/kubectl/cheatsheet/

#### Config map
We have config map and secrets

### Kubernetes history
![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/8c69e790-d1ef-4c2d-963e-8624d1a7c0cf)

### Vcluster
https://loft.sh/docs/getting-started/install
https://www.vcluster.com/docs/getting-started/setup#download-vcluster-cli

After above all, you loft agent will fail for gcp gke, to make it work, you need to add tcp ports 8443 and 9443 in gcp gke master firewall rule

```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ curl -L -o loft "https://github.com/loft-sh/loft/releases/latest/download/loft-linux-amd64" && sudo install -c -m 0755 loft /usr/local/bin
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0
100 52.6M  100 52.6M    0     0  11.9M      0  0:00:04  0:00:04 --:--:-- 15.5M
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ loft --version
loft version 3.1.1
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ loft start

[info]   Welcome to Loft!
[info]   This installer will help you configure and deploy Loft.

? Enter your email address to create the login for your admin user tomspencerlondon@gmail.com
```
VCluster is an open-source tool developed by Loft Labs
Loft installs K3s distribution instead of K8 by default. It is configurable.
Loft simplifies the management and operation of Kubernetes clusters for development.
VCluster allows users to create lightweight on demand Kuberneetes clusters within a shared Kubernetes cluster
Shared Kubernetes cluster is useful when you want to have one cluster with multiple namespaces for multiple teams.
It provides a secure and isolated environment for development and testing workflows.
VCluster saves money and offers flexibility. 
Engineers can easily create and delete virtual clusters.

![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/5822590b-548c-467b-878f-b168197943fa)

![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/d48aef7f-047f-46db-a933-12e4512627d2)

![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/c03fc038-086b-4350-8845-53400f89f6cc)

Project - create multiple projects
![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/5f0f0c23-a794-4132-aafe-f6fdf9cb0956)

- host cluster is the cluster we are doing virtualization
- platform cluster is the one where loft is installed

```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectl get ns
NAME                              STATUS   AGE
default                           Active   7d
gke-gmp-system                    Active   7d
gke-managed-filestorecsi          Active   7d
gmp-public                        Active   7d
kube-node-lease                   Active   7d
kube-public                       Active   7d
kube-system                       Active   7d
loft                              Active   25m
loft-default-v-starter-vcluster   Active   44s
loft-p-default                    Active   22m
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectx
gke_devops-rnd-383110_us-central1_autopilot-cluster-1
gke_devops-rnd-383110_us-central1_in28minutes-cluster
gke_devops-rnd-383110_us-central1_web-app-cluster

```
![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/29ea03f1-0861-4328-bb91-eb5d0dad9c5e)

```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ loft login --insecure https://localhost:9898
[info]   If the browser does not open automatically, please navigate to https://localhost:9898/login?cli=true
[info]   If you have problems logging in, please navigate to https://localhost:9898/profile/access-keys, click on 'Create Access Key' and then login via 'loft login https://localhost:9898 --access-key ACCESS_KEY --insecure'
[done] √ Successfully logged into Loft instance https://localhost:9898
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ loft use vcluster starter-vcluster --project default
[done] √ Successfully updated kube context to use virtual cluster starter-vcluster in project default
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectx
gke_devops-rnd-383110_us-central1_autopilot-cluster-1
gke_devops-rnd-383110_us-central1_in28minutes-cluster
gke_devops-rnd-383110_us-central1_web-app-cluster
loft-vcluster_starter-vcluster_default
```

### Steps to deploy application to virtual cluster
Step 1 - set up kubernetes cluster on GKE with sufficient CPUs
Step 2 - connect to the cluster and install loft
Step 3 - go to the loft ui and create vcluster
Step 4 - connect to your loft vcluster
Step 5 - deploy application to vcluster


```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectx
gke_devops-rnd-383110_us-central1-c_cluster-virtualization
gke_devops-rnd-383110_us-central1_autopilot-cluster-1
gke_devops-rnd-383110_us-central1_in28minutes-cluster
gke_devops-rnd-383110_us-central1_web-app-cluster
loft-vcluster_starter-vcluster_default
```