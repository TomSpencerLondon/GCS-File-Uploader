### cli login for google cloud
gcloud auth login

### application login for hitting google apis from running application
gcloud auth application-default login

### kubectx to change context


### Here is how I fixed gcloud cli setup:
https://askubuntu.com/questions/1156409/conflicting-values-set-for-option-signed-by-regarding-source

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


### bind gcloud service account with kubernetes service account:
In order to pull images from GCR we need to create a GCP service account with workloadIdentityUser role on GCP.
The command also binds the gcp service account with the kubernetes service account:
```bash
echo "Binding GCP SA with k8 SA"
#Bind the k8 service account with GCP S.A
gcloud iam service-accounts add-iam-policy-binding gcswebapp@devops-rnd-383110.iam.gserviceaccount.com \
            --role roles/iam.workloadIdentityUser \
            --member "serviceAccount:devops-rnd-383110.svc.id.goog[default/gcswebapp]"
```

On the other side we need to bind the Kubernetes service account with the GCP service account:
```bash

echo "Binding k8 SA with GCP SA"
# Now annotate the k8 SA
kubectl annotate serviceaccount gcswebapp \
            --namespace default \
            iam.gke.io/gcp-service-account=gcswebapp@devops-rnd-383110.iam.gserviceaccount.com

```

This is how to view the configuration we added:
```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectl get sa gcswebapp -o yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  annotations:
    iam.gke.io/gcp-service-account: gcswebapp@devops-rnd-383110.iam.gserviceaccount.com
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"v1","kind":"ServiceAccount","metadata":{"annotations":{},"creationTimestamp":null,"name":"gcswebapp","namespace":"default"}}
  creationTimestamp: "2023-05-06T09:39:43Z"
  name: gcswebapp
  namespace: default
  resourceVersion: "32640"
  uid: d145fb30-7e8b-4cef-84d6-fc6f6f4b8b30
```
Now we can see that the deployment is working:
```bash
tom@tom-ubuntu:~/Projects/gcs-file-uploader/kubernetes$ kubectl get deployments
NAME                  READY   UP-TO-DATE   AVAILABLE   AGE
gcs-uploader-webapp   1/1     1            1           12m
```

#### Tasks
- Kubernetes architecture:
  - What is API server?
  - Resource Controller
  - Scheduler
  - GCP - node pulls
  - Network policies on GCP - GKE Calico

Create cluster from Kubernetes Engine:
https://console.cloud.google.com/kubernetes/list/overview?hl=en&project=devops-rnd-383110

Devops toolkit
https://www.youtube.com/@DevOpsToolkit/search?query=kubernetes

Devops with Nana

us-central1
10.109.0.0/17


Googe notes from Samarth:
https://docs.google.com/document/d/1HaQvUUT5MriB09dW18N5mr4HPP1BuJvqV5jzvkZkdPM/edit