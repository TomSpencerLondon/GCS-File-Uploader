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
    public ResponseEntity<String> uploadData(@RequestBody(required = true) HttpEntity<byte[]> requestEntity,
                                             @PathVariable("folder_name") String folderName) {
        log.info("Received folder name for creation: {}", folderName);
        if (requestEntity.getBody() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Empty request body");
        } else {
            Storage storage = StorageOptions.getDefaultInstance().getService();
            String folder = String.format("%s/file_%s.csv",
                    folderName,
                    System.currentTimeMillis());
            BlobId blobId = BlobId.of(this.gcsBucket, folder);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
            Blob blob = storage.create(blobInfo, requestEntity.getBody());
            System.out.println(blob);
            return ResponseEntity
                    .ok()
                    .body("Successful upload to " + folderName + "/");
        }
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








