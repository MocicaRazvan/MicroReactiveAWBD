$dockerHubUsername = "razvanmocica"

# services array
$services = @(
    @{ name = "config-server"; context = "./config-server"; dockerfile = "Dockerfile" },
    @{ name = "eureka"; context = "./service-registry"; dockerfile = "Dockerfile" },
    @{ name = "gateway-service"; context = "./gateway-service"; dockerfile = "Dockerfile" },
    @{ name = "user-service"; context = "."; dockerfile = "user-service/Dockerfile" },
    @{ name = "comment-service"; context = "."; dockerfile = "comment-service/Dockerfile" },
    @{ name = "exercise-service"; context = "."; dockerfile = "exercise-service/Dockerfile" },
    @{ name = "invoices-service"; context = "./invoices-service"; dockerfile = "Dockerfile" },
    @{ name = "order-service"; context = "."; dockerfile = "order-service/Dockerfile" },
    @{ name = "post-service"; context = "."; dockerfile = "post-service/Dockerfile" },
    @{ name = "training-service"; context = "."; dockerfile = "training-service/Dockerfile" },
    @{ name = "nextjs"; context = "./client-micro"; dockerfile = "Dockerfile" }
)

# function to build and push Docker images
function BuildAndPush-DockerImage
{
    param (
        [string]$imageName,
        [string]$context,
        [string]$dockerfile
    )

    try
    {
        Write-Output "Building image for $imageName from context $context and Dockerfile $dockerfile..."
        docker build -t $imageName -f "$context/$dockerfile" $context 2>&1

        Write-Output "Pushing image $imageName to Docker Hub..."
        docker push $imageName 2>&1
    }
    catch
    {
        Write-Output "Error occurred while processing $imageName"
        $_
    }
}

foreach ($service in $services)
{
    Write-Output "Building and pushing image for $( $service.name )..."

    $imageName = "$dockerHubUsername/$( $service.name ):latest"
    $context = $service.context
    $dockerfile = $service.dockerfile

    BuildAndPush-DockerImage -imageName $imageName -context $context -dockerfile $dockerfile
}

Write-Output "All images have been built and pushed to Docker Hub."

Write-Output "Press any key to close."
Pause
