# Local Docker Testing Guide

Test your Welog application with Docker locally before deploying to Render.

## 🚀 Quick Start

### 1. Ensure Uploads Directory Exists

```powershell
# Create uploads directory structure
New-Item -ItemType Directory -Force -Path uploads/img/users
New-Item -ItemType Directory -Force -Path uploads/img/posts

# Verify structure
tree uploads /F
```

### 2. Build Spring Boot JAR

```powershell
# Clean and build
./gradlew clean build

# Verify JAR exists
ls build/libs/

# Expected output: welog-0.0.1-SNAPSHOT.jar
```

### 3. Create Environment File

```powershell
# Copy template
Copy-Item .env.example .env

# Edit with your Supabase credentials
notepad .env
```

**Update these values in `.env`:**

```env
DB_URL=jdbc:postgresql://db.xxxxxxxxxxxxx.supabase.co:5432/postgres
DB_USERNAME=postgres.xxxxxxxxxxxxx
DB_PASSWORD=your-actual-supabase-password
JWT_SECRET=generate-a-long-random-string-here
```

### 4. Start Docker Container

```powershell
# Start services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

## 🧪 Testing Image Upload & Persistence

### Test 1: Upload User Avatar

1. Start the application
2. Register or login
3. Upload an avatar image
4. Note the image URL in browser DevTools
5. Verify image loads: `http://localhost:8080/api/v1/img/users/filename.jpg`

### Test 2: Upload Post Cover Image

1. Create a new post
2. Upload a cover image
3. Verify image displays in post
4. Check image URL: `http://localhost:8080/api/v1/img/posts/filename.jpg`

### Test 3: Verify Persistence After Restart

```powershell
# 1. Upload some images (avatar + post cover)

# 2. Check they exist locally
ls uploads/img/users/
ls uploads/img/posts/

# 3. Restart Docker container
docker-compose restart backend

# 4. Wait for restart
Start-Sleep -Seconds 10

# 5. Verify images still load in browser
# Visit: http://localhost:8080/api/v1/img/users/your-avatar.jpg
```

✅ **Success**: Images should load correctly after restart!

## 📊 Volume Management

### Check Volume Mounts

```powershell
# Inspect container
docker inspect welog-backend | Select-String -Pattern "Mounts" -Context 5,10

# Should show:
# "Source": "./uploads"
# "Destination": "/app/uploads"
```

### Verify Directory Structure Inside Container

```powershell
# Access container shell
docker-compose exec backend /bin/bash

# Inside container:
ls -la /app/
ls -la /app/uploads/
ls -la /app/uploads/img/users/
ls -la /app/uploads/img/posts/

# Exit
exit
```

Expected output:

```
drwxr-xr-x 2 root root 4096 ... /app/uploads/img/users/
drwxr-xr-x 2 root root 4096 ... /app/uploads/img/posts/
```

### Check Image Files

```powershell
# List uploaded user avatars
ls uploads/img/users/

# List uploaded post covers
ls uploads/img/posts/

# View file details
Get-ChildItem uploads/img/ -Recurse | Select-Object Name, Length, LastWriteTime
```

## 🔍 Troubleshooting

### Issue: Images Not Loading (404)

**Symptoms:**

- POST request to upload succeeds (200 OK)
- GET request for image fails (404 Not Found)

**Solutions:**

1. **Check WebConfig resource handlers:**

   ```powershell
   # Verify WebConfig.java has correct paths
   Select-String -Path "src/main/java/com/example/welog/config/WebConfig.java" -Pattern "addResourceHandler"
   ```

2. **Check upload directory path:**

   ```powershell
   # Verify application.properties
   Select-String -Path "src/main/resources/application.properties" -Pattern "app.upload.dir"

   # Should be: app.upload.dir=uploads/img
   ```

3. **Verify files were actually saved:**

   ```powershell
   ls uploads/img/users/ -Recurse
   ls uploads/img/posts/ -Recurse
   ```

4. **Check container logs:**
   ```powershell
   docker-compose logs backend | Select-String -Pattern "upload"
   docker-compose logs backend | Select-String -Pattern "error"
   ```

### Issue: Permission Denied

**Symptoms:**

- Error: "Failed to store file"
- IOException in logs

**Solutions:**

1. **Check directory permissions:**

   ```powershell
   # Windows: Ensure directory is not read-only
   Get-Item uploads -Force | Select-Object Attributes

   # Should not have ReadOnly attribute
   ```

2. **Fix permissions inside container:**

   ```powershell
   docker-compose exec backend chmod -R 755 /app/uploads
   ```

3. **Verify Dockerfile creates directories:**
   ```dockerfile
   RUN mkdir -p /app/uploads/img/users && \
       mkdir -p /app/uploads/img/posts && \
       chmod -R 755 /app/uploads
   ```

### Issue: Images Disappear After Restart

**Symptoms:**

- Images work initially
- After `docker-compose down` and `docker-compose up`, images are gone

**Solutions:**

1. **Verify volume mount in docker-compose.yml:**

   ```yaml
   volumes:
     - ./uploads:/app/uploads # Bind mount (persists locally)
   ```

2. **Don't use `-v` flag when stopping:**

   ```powershell
   # ❌ Wrong - deletes volumes:
   docker-compose down -v

   # ✅ Correct - keeps volumes:
   docker-compose down
   ```

3. **Check local uploads folder exists:**
   ```powershell
   Test-Path uploads/
   # Should return: True
   ```

### Issue: Cannot Connect to Database

**Symptoms:**

- Backend starts but crashes immediately
- Error: "Connection refused" or "Authentication failed"

**Solutions:**

1. **Verify Supabase credentials:**

   ```powershell
   # Check .env file
   Get-Content .env | Select-String -Pattern "DB_"
   ```

2. **Test connection manually:**

   ```powershell
   # Using psql (if installed)
   $env:PGPASSWORD="your-password"
   psql -h db.xxxxx.supabase.co -U postgres.xxxxx -d postgres -c "SELECT 1;"
   ```

3. **Check Supabase connection pooler:**
   - Use **Direct connection** (not Pooler) for Spring Boot
   - Port should be `5432` (not `6543`)

### Issue: Docker Build Fails

**Symptoms:**

- Error: "COPY failed: no source files were specified"
- Error: "Cannot find JAR file"

**Solutions:**

1. **Ensure JAR is built:**

   ```powershell
   ./gradlew clean build
   ls build/libs/
   ```

2. **Check .dockerignore doesn't exclude JAR:**

   ```powershell
   Select-String -Path ".dockerignore" -Pattern "!build/libs/\*.jar"

   # Should have this line to allow JAR
   ```

3. **Rebuild Docker image:**
   ```powershell
   docker-compose build --no-cache backend
   docker-compose up -d
   ```

## 📝 Development Workflow

### Making Code Changes

```powershell
# 1. Make your changes

# 2. Rebuild JAR
./gradlew clean build

# 3. Rebuild and restart Docker
docker-compose up -d --build

# 4. View logs
docker-compose logs -f backend
```

### Quick Restart (No Code Changes)

```powershell
# Just restart container
docker-compose restart backend

# Or stop and start
docker-compose stop backend
docker-compose start backend
```

### Clean Everything and Start Fresh

```powershell
# Stop and remove containers
docker-compose down

# Remove uploaded images (optional - be careful!)
Remove-Item uploads/img/users/* -Force
Remove-Item uploads/img/posts/* -Force

# Rebuild and start
./gradlew clean build
docker-compose up -d --build
```

## 🎯 Testing Checklist

Before deploying to Render, verify:

- [ ] JAR builds successfully
- [ ] Docker container starts without errors
- [ ] Backend health endpoint works: `http://localhost:8080/actuator/health`
- [ ] Can register new user
- [ ] Can login
- [ ] Can upload user avatar
- [ ] Avatar persists after restart
- [ ] Can create post
- [ ] Can upload post cover image
- [ ] Post cover persists after restart
- [ ] Images are accessible at `/api/v1/img/users/filename.jpg`
- [ ] Images are accessible at `/api/v1/img/posts/filename.jpg`
- [ ] Local `uploads/` folder contains uploaded files
- [ ] CORS allows frontend (if testing with frontend)

## 🔗 Useful Commands

```powershell
# Build JAR
./gradlew clean build

# Start all services
docker-compose up -d

# Start with rebuild
docker-compose up -d --build

# View logs (follow mode)
docker-compose logs -f backend

# View last 100 lines
docker-compose logs --tail=100 backend

# Restart service
docker-compose restart backend

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v

# Access container shell
docker-compose exec backend /bin/bash

# Check container status
docker-compose ps

# View resource usage
docker stats welog-backend

# Clean up Docker system
docker system prune -a
```

## 📚 File Locations

| File                 | Purpose                          |
| -------------------- | -------------------------------- |
| `Dockerfile`         | Container configuration          |
| `docker-compose.yml` | Multi-service orchestration      |
| `.env`               | Environment variables (local)    |
| `.env.example`       | Template for .env                |
| `.dockerignore`      | Files excluded from Docker build |
| `uploads/`           | Local image storage (bind mount) |
| `build/libs/*.jar`   | Compiled Spring Boot application |

## ✅ Ready for Production

Once all tests pass locally, you're ready to deploy to Render!

Follow: [RENDER_DEPLOYMENT_GUIDE.md](./RENDER_DEPLOYMENT_GUIDE.md)

---

**Happy Testing! 🧪**
