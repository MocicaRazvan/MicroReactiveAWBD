Write-Output "Initializing Kubernetes cluster..."
kind create cluster --config kind-config.yml
Write-Output "`n-----------------------------------------------------`n"


Write-Output "Installing NGINX Ingress..."
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
Write-Output "`n-----------------------------------------------------`n"


Write-Output "Waiting for NGINX Ingress to be ready..."
Start-Sleep -Seconds 10
kubectl wait --namespace ingress-nginx `
  --for=condition=ready pod `
  --selector=app.kubernetes.io/component=controller `
  --timeout=180s
Write-Output "`n-----------------------------------------------------`n"

Write-Output "Installing Cloud Provider KIND..."
go install sigs.k8s.io/cloud-provider-kind@latest

Write-Output "Starting Cloud Provider KIND..."

Start-Process "cloud-provider-kind"

Write-Output "`n-----------------------------------------------------`n"

# ensure cloud provider kind is running
Write-Output "Ensuring Cloud Provider KIND is running and connected..."
Start-Sleep -Seconds 10
$cloudProviderKindProcess = Get-Process -Name "cloud-provider-kind" -ErrorAction SilentlyContinue
if ($null -eq $cloudProviderKindProcess)
{
    Write-Error "Cloud Provider KIND did not start correctly."
    exit 1
}
else
{
    Write-Output "Cloud Provider KIND is running."
}

Write-Output "`n-----------------------------------------------------`n"
Write-Output "Kubernetes cluster with NGINX Ingress and LoadBalancer support is ready!"
Write-Output "`n-----------------------------------------------------`n"

# apply k8s resources
Write-Output "Applying the k8s resources..."
kubectl apply -f ../k8s
Start-Sleep -Seconds 10
Write-Output "`n-----------------------------------------------------`n"

Write-Output "Happy Sailing!"
