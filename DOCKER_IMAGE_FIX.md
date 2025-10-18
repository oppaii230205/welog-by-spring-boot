# Docker Image Loading - Quick Fix Summary

## 🎯 Problem

Images uploaded to your Welog blog don't load when running in Docker because:

1. The `uploads/` folder doesn't persist between container restarts
2. Volume mapping wasn't configured in docker-compose
3. Directory structure wasn't created inside the container

## ✅ Solution Applied

### 1. Updated Dockerfile

**Created directory structure inside container:**

```dockerfile
RUN mkdir -p /app/uploads/img/users && \
    mkdir -p /app/uploads/img/posts && \
    chmod -R 755 /app/uploads
```

### 2. Created docker-compose.yml

**Added volume mapping for persistence:**

```yaml
volumes:
  - ./uploads:/app/uploads # Maps local uploads to container
```

This ensures images persist on your local machine and survive container restarts.

### 3. Enhanced WebConfig.java

**Added multiple image serving paths:**

- `/api/v1/img/users/**` - Official API path
- `/api/v1/img/posts/**` - Official API path
- `/img/users/**` - Direct access
- `/img/posts/**` - Direct access

All paths now have 1-hour caching enabled.

### 4. Created .env.example

**Template for Supabase connection:**

- Database URL, username, password
- JWT secret configuration
- Upload directory path

### 5. Updated .dockerignore

**Optimized Docker builds:**

- Excludes local `uploads/` from being copied
- Uses volume mount instead
- Reduces image size

## 📁 File Changes

| File                 | Change                                                |
| -------------------- | ----------------------------------------------------- |
| `Dockerfile`         | ✅ Creates upload directories with proper permissions |
| `docker-compose.yml` | ✅ New file - Volume mapping for persistence          |
| `WebConfig.java`     | ✅ Enhanced static resource handlers with caching     |
| `.env.example`       | ✅ New file - Environment variable template           |
| `.dockerignore`      | ✅ Excludes local uploads folder                      |

## 🚀 How to Use

### Local Development

```powershell
# 1. Create uploads directory
New-Item -ItemType Directory -Force -Path uploads/img/users
New-Item -ItemType Directory -Force -Path uploads/img/posts

# 2. Build JAR
./gradlew clean build

# 3. Create .env from template
Copy-Item .env.example .env
# Edit .env with your Supabase credentials

# 4. Start Docker
docker-compose up -d

# 5. Test image upload
# - Upload avatar at http://localhost:8080
# - Verify image at http://localhost:8080/api/v1/img/users/filename.jpg

# 6. Test persistence
docker-compose restart backend
# Image should still load!
```

### Render Deployment

```powershell
# 1. Push to GitHub
git add .
git commit -m "Fix Docker image loading with persistent storage"
git push

# 2. Follow RENDER_DEPLOYMENT_GUIDE.md
# Key steps:
# - Create web service from GitHub repo
# - Add persistent disk at /app/uploads (1GB minimum)
# - Set environment variables (DB_URL, JWT_SECRET, etc.)
# - Deploy and test!
```

## ✅ Testing Checklist

- [ ] Local `uploads/` directory exists
- [ ] Docker container starts successfully
- [ ] Can upload user avatar
- [ ] Avatar loads at `/api/v1/img/users/filename.jpg`
- [ ] Can upload post cover image
- [ ] Post cover loads at `/api/v1/img/posts/filename.jpg`
- [ ] After `docker-compose restart`, images still load
- [ ] Local `uploads/` folder contains the uploaded files

## 🎯 Image Access Patterns

### In Frontend (React)

```javascript
// User avatar
const avatarUrl = `${API_URL}/img/users/${user.photo}`;
// or
const avatarUrl = `${API_URL}/api/v1/img/users/${user.photo}`;

// Post cover
const coverUrl = `${API_URL}/img/posts/${post.coverImage}`;
// or
const coverUrl = `${API_URL}/api/v1/img/posts/${post.coverImage}`;
```

### Direct Access

```
http://localhost:8080/api/v1/img/users/user_1_1234567890.jpg
http://localhost:8080/api/v1/img/posts/post_5_1234567890.jpg

or

http://localhost:8080/img/users/user_1_1234567890.jpg
http://localhost:8080/img/posts/post_5_1234567890.jpg
```

## 🔧 Configuration Summary

### Docker Compose Volume

```yaml
backend:
  volumes:
    - ./uploads:/app/uploads # Local folder mapped to container
```

### Environment Variables

```env
DB_URL=jdbc:postgresql://your-supabase-host:5432/postgres
DB_USERNAME=postgres.xxxxx
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key
UPLOAD_DIR=/app/uploads/img
```

### Render Persistent Disk

```
Name: uploads
Mount Path: /app/uploads
Size: 1 GB (or more)
```

## 📚 Next Steps

1. **Test Locally**: Follow `DOCKER_TESTING_GUIDE.md`
2. **Deploy to Render**: Follow `RENDER_DEPLOYMENT_GUIDE.md`
3. **Monitor**: Check disk usage in Render dashboard
4. **Optimize**: Consider cloud storage (Cloudinary, S3) for scaling

## 🆘 Troubleshooting

### Images Still Not Loading?

1. **Check volume mount:**

   ```powershell
   docker inspect welog-backend | Select-String "Mounts"
   ```

2. **Verify directory exists in container:**

   ```powershell
   docker-compose exec backend ls -la /app/uploads/img/users/
   ```

3. **Check logs:**

   ```powershell
   docker-compose logs backend | Select-String "upload"
   ```

4. **Verify WebConfig paths:**
   Should have `/api/v1/img/users/**` and `/api/v1/img/posts/**`

5. **Test direct file access:**
   ```powershell
   curl http://localhost:8080/api/v1/img/users/test.jpg -v
   ```

## 🎉 Benefits

✅ **Persistent Storage**: Images survive container restarts
✅ **Supabase Compatible**: Works with your Supabase PostgreSQL
✅ **Render Ready**: Easy deployment with persistent disk
✅ **Local Development**: Test with docker-compose before deploying
✅ **Optimized**: Caching enabled, proper permissions, clean builds

---

**Your image loading issue is now fixed! 🎊**

Test locally with Docker Compose, then deploy to Render with confidence!
