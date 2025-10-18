# Test Docker Image Loading
# This script tests that images are properly uploaded and persisted

Write-Host "🧪 Testing Docker Image Configuration..." -ForegroundColor Cyan

# Step 1: Check prerequisites
Write-Host "`n📋 Checking prerequisites..." -ForegroundColor Yellow

if (-not (Test-Path "build/libs/welog-*.jar")) {
    Write-Host "❌ JAR file not found. Building..." -ForegroundColor Red
    ./gradlew clean build
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Build failed!" -ForegroundColor Red
        exit 1
    }
}
Write-Host "   ✅ JAR file exists" -ForegroundColor Green

if (-not (Test-Path ".env")) {
    Write-Host "❌ .env file not found. Creating from template..." -ForegroundColor Red
    Copy-Item .env.example .env
    Write-Host "   ⚠️  Please edit .env with your Supabase credentials!" -ForegroundColor Yellow
    notepad .env
    Read-Host "Press Enter after updating .env file"
}
Write-Host "   ✅ .env file exists" -ForegroundColor Green

# Step 2: Create uploads directory structure
Write-Host "`n📁 Creating uploads directory..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path uploads/img/users | Out-Null
New-Item -ItemType Directory -Force -Path uploads/img/posts | Out-Null
Write-Host "   ✅ Directory structure created" -ForegroundColor Green

# Step 3: Create test images
Write-Host "`n🖼️  Creating test images..." -ForegroundColor Yellow
$testUserImage = "uploads/img/users/test-user.jpg"
$testPostImage = "uploads/img/posts/test-post.jpg"

# Create minimal valid JPEG files (1x1 pixel)
$jpegBytes = @(0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0xFF, 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32, 0xFF, 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00, 0xFF, 0xC4, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xC4, 0x00, 0x14, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xDA, 0x00, 0x08, 0x01, 0x01, 0x00, 0x00, 0x3F, 0x00, 0x7F, 0xFF, 0xD9)

[System.IO.File]::WriteAllBytes((Resolve-Path $testUserImage), $jpegBytes)
[System.IO.File]::WriteAllBytes((Resolve-Path $testPostImage), $jpegBytes)

Write-Host "   ✅ Created: $testUserImage" -ForegroundColor Green
Write-Host "   ✅ Created: $testPostImage" -ForegroundColor Green

# Step 4: Start Docker Compose
Write-Host "`n🐳 Starting Docker Compose..." -ForegroundColor Yellow
docker-compose down 2>$null
docker-compose up -d --build

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker Compose failed to start!" -ForegroundColor Red
    Write-Host "`n📋 Checking Docker logs:" -ForegroundColor Yellow
    docker-compose logs --tail=50 backend
    exit 1
}
Write-Host "   ✅ Docker Compose started" -ForegroundColor Green

# Step 5: Wait for backend to be ready
Write-Host "`n⏳ Waiting for backend to start (30 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Step 6: Test health endpoint
Write-Host "`n🔍 Testing health endpoint..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 10 -ErrorAction Stop
    if ($healthResponse.StatusCode -eq 200) {
        Write-Host "   ✅ Backend is healthy" -ForegroundColor Green
    }
} catch {
    Write-Host "   ⚠️  Health check failed (might be disabled): $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "   Continuing with image tests..." -ForegroundColor Yellow
}

# Step 7: Test image accessibility
Write-Host "`n🧪 Testing image endpoints..." -ForegroundColor Yellow

$baseUrl = "http://localhost:8080"
$endpoints = @(
    "/api/v1/img/users/test-user.jpg",
    "/api/v1/img/posts/test-post.jpg",
    "/img/users/test-user.jpg",
    "/img/posts/test-post.jpg"
)

$passedTests = 0
$totalTests = $endpoints.Count

foreach ($endpoint in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl$endpoint" -Method Get -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "   ✅ $endpoint - OK (${$response.ContentLength} bytes)" -ForegroundColor Green
            $passedTests++
        }
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "   ❌ $endpoint - FAILED (Status: $statusCode)" -ForegroundColor Red
    }
}

# Step 8: Test persistence
Write-Host "`n🔄 Testing persistence (restarting container)..." -ForegroundColor Yellow
docker-compose restart backend
Start-Sleep -Seconds 15

Write-Host "   Testing images after restart..." -ForegroundColor Yellow
foreach ($endpoint in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl$endpoint" -Method Get -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "   ✅ $endpoint - Still accessible after restart" -ForegroundColor Green
        }
    } catch {
        Write-Host "   ❌ $endpoint - Lost after restart!" -ForegroundColor Red
    }
}

# Step 9: Verify local files
Write-Host "`n📂 Verifying local files..." -ForegroundColor Yellow
$userFiles = Get-ChildItem -Path "uploads/img/users/" -File -ErrorAction SilentlyContinue
$postFiles = Get-ChildItem -Path "uploads/img/posts/" -File -ErrorAction SilentlyContinue

Write-Host "   User images: $($userFiles.Count) file(s)" -ForegroundColor Cyan
Write-Host "   Post images: $($postFiles.Count) file(s)" -ForegroundColor Cyan

# Step 10: Check Docker logs for errors
Write-Host "`n📋 Checking for errors in logs..." -ForegroundColor Yellow
$logs = docker-compose logs backend 2>&1 | Select-String -Pattern "error|exception|failed" -CaseSensitive:$false

if ($logs.Count -gt 0) {
    Write-Host "   ⚠️  Found $($logs.Count) potential issues:" -ForegroundColor Yellow
    $logs | Select-Object -First 5 | ForEach-Object { Write-Host "      $_" -ForegroundColor Gray }
} else {
    Write-Host "   ✅ No errors found in logs" -ForegroundColor Green
}

# Summary
Write-Host "`n" + "="*60 -ForegroundColor Cyan
Write-Host "📊 Test Summary" -ForegroundColor Cyan
Write-Host "="*60 -ForegroundColor Cyan
Write-Host "Endpoints tested: $totalTests" -ForegroundColor White
Write-Host "Passed: $passedTests" -ForegroundColor Green
Write-Host "Failed: $($totalTests - $passedTests)" -ForegroundColor $(if ($passedTests -eq $totalTests) { "Green" } else { "Red" })

if ($passedTests -eq $totalTests) {
    Write-Host "`n✅ All tests passed! Your Docker image configuration is working correctly." -ForegroundColor Green
    Write-Host "`n🚀 Next steps:" -ForegroundColor Cyan
    Write-Host "   1. Test with real image uploads via the frontend" -ForegroundColor White
    Write-Host "   2. Verify persistence with: docker-compose restart backend" -ForegroundColor White
    Write-Host "   3. Deploy to Render following RENDER_DEPLOYMENT_GUIDE.md" -ForegroundColor White
} else {
    Write-Host "`n⚠️  Some tests failed. Check the errors above." -ForegroundColor Yellow
    Write-Host "`n🔧 Troubleshooting:" -ForegroundColor Cyan
    Write-Host "   1. Check logs: docker-compose logs backend" -ForegroundColor White
    Write-Host "   2. Verify WebConfig.java has correct resource handlers" -ForegroundColor White
    Write-Host "   3. Check .env has correct database credentials" -ForegroundColor White
    Write-Host "   4. Ensure app.upload.dir=uploads/img in application.properties" -ForegroundColor White
}

Write-Host "`n💡 Useful commands:" -ForegroundColor Cyan
Write-Host "   View logs: docker-compose logs -f backend" -ForegroundColor White
Write-Host "   Stop: docker-compose down" -ForegroundColor White
Write-Host "   Restart: docker-compose restart backend" -ForegroundColor White
Write-Host "   Shell access: docker-compose exec backend /bin/bash" -ForegroundColor White

Write-Host ""
