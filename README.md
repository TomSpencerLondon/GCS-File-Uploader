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
