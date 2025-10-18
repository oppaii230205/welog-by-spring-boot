# Deploying Welog to Render with Supabase

Complete guide for deploying your Welog blog to Render with Supabase PostgreSQL and persistent image storage.

## 🎯 Architecture Overview

- **Database**: Supabase PostgreSQL (already configured)
- **Backend**: Render Web Service (Docker-based Spring Boot)
- **Frontend**: Render Static Site (React/Vite)
- **Storage**: Render Persistent Disk (for uploaded images)

---

## 📋 Prerequisites

- ✅ GitHub account with your Welog repository
- ✅ Render account (sign up at https://render.com)
- ✅ Supabase project with PostgreSQL (already set up)
- ✅ Code built and tested locally

---

## 🚀 Part 1: Prepare Your Repository

### Step 1: Ensure Required Files Exist

Your repository should have:

- ✅ `Dockerfile` (Spring Boot container)
- ✅ `docker-compose.yml` (local testing)
- ✅ `.env.example` (environment template)
- ✅ `.dockerignore` (optimized builds)
- ✅ `build/libs/welog-*.jar` (built JAR)

### Step 2: Build the JAR

```powershell
# Build the Spring Boot application
./gradlew clean build

# Verify JAR exists
ls build/libs/
```

You should see: `welog-0.0.1-SNAPSHOT.jar` or similar.

### Step 3: Push to GitHub

```powershell
git add .
git commit -m "Prepare for Render deployment with persistent storage"
git push origin main
```

---

## ☕ Part 2: Deploy Spring Boot Backend on Render

### Step 1: Create Web Service

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository
4. Configure the service:
   - **Name**: `welog-backend`
   - **Region**: Choose closest (e.g., Oregon, Frankfurt)
   - **Branch**: `main`
   - **Root Directory**: Leave empty
   - **Environment**: **Docker**
   - **Dockerfile Path**: `./Dockerfile`
   - **Docker Build Context Path**: `.`
   - **Plan**: **Free** (or Starter for $7/month)

### Step 2: Configure Environment Variables

Click **"Environment"** tab and add these variables:

| Key                 | Value                                             | Notes                           |
| ------------------- | ------------------------------------------------- | ------------------------------- |
| `DB_URL`            | `jdbc:postgresql://[SUPABASE_HOST]:5432/postgres` | From Supabase connection string |
| `DB_USERNAME`       | `postgres.xxxxx`                                  | Your Supabase username          |
| `DB_PASSWORD`       | `your-supabase-password`                          | From Supabase settings          |
| `JWT_SECRET`        | `[Generate 64-char random string]`                | See generation below            |
| `JWT_EXPIRATION_MS` | `86400000`                                        | 24 hours in milliseconds        |
| `UPLOAD_DIR`        | `/app/uploads/img`                                | Path inside container           |

**Get Supabase Connection Details:**

1. Go to Supabase Dashboard → Settings → Database
2. Copy the **Connection String** (Direct connection)
3. Format: `jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres`

**Generate JWT Secret (PowerShell):**

```powershell
-join (1..64 | ForEach { [char]((65..90) + (97..122) + (48..57) | Get-Random) })
```

### Step 3: Add Persistent Disk for Images

1. Scroll to **"Disks"** section
2. Click **"Add Disk"**
   - **Name**: `uploads`
   - **Mount Path**: `/app/uploads`
   - **Size**: `1 GB` (Free tier) or `10 GB` (Starter)
3. Click **"Save"**

⚠️ **Critical**: The mount path must be `/app/uploads` to match your configuration!

### Step 4: Configure Health Check (Optional but Recommended)

Add Spring Boot Actuator to your `build.gradle` if not already present:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

Then in Render:

1. Scroll to **"Health Check Path"**
2. Set to: `/actuator/health`

### Step 5: Deploy Backend

1. Click **"Create Web Service"**
2. Render will:
   - Clone your repository
   - Build Docker image with your JAR
   - Create persistent disk
   - Start the application
3. Wait for deployment (5-10 minutes)
4. Note your backend URL: `https://welog-backend.onrender.com`

---

## ⚛️ Part 3: Deploy React Frontend

### Step 1: Update Frontend Configuration

Edit `blog-frontend/config.env`:

```env
VITE_API_URL=https://welog-backend.onrender.com/api/v1
```

Or create `blog-frontend/.env.production`:

```env
VITE_API_URL=https://welog-backend.onrender.com/api/v1
```

### Step 2: Commit and Push

```powershell
git add blog-frontend/config.env
git commit -m "Update API URL for Render production"
git push
```

### Step 3: Create Static Site on Render

1. Click **"New +"** → **"Static Site"**
2. Connect your GitHub repository
3. Configure:
   - **Name**: `welog-frontend`
   - **Region**: Same as backend
   - **Branch**: `main`
   - **Root Directory**: `blog-frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
   - **Plan**: **Free**

### Step 4: Add Environment Variable

In **Environment Variables**:

| Key            | Value                                       |
| -------------- | ------------------------------------------- |
| `VITE_API_URL` | `https://welog-backend.onrender.com/api/v1` |

### Step 5: Deploy Frontend

1. Click **"Create Static Site"**
2. Render will:
   - Install dependencies
   - Build React app with Vite
   - Deploy to global CDN
3. Note your frontend URL: `https://welog-frontend.onrender.com`

---

## 🔧 Part 4: Configure CORS

Your backend needs to allow requests from the frontend.

### Update WebConfig.java

Edit `src/main/java/com/example/welog/config/WebConfig.java`:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins(
                "https://welog-frontend.onrender.com",  // Production frontend
                "http://localhost:5173",                 // Local development
                "http://localhost:3000"                  // Alternative local
            )
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

Or use environment variable for flexibility:

```java
@Value("${allowed.origins:https://welog-frontend.onrender.com,http://localhost:5173}")
private String allowedOrigins;

@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

Then add to Render environment variables:

```
ALLOWED_ORIGINS=https://welog-frontend.onrender.com,http://localhost:5173
```

### Redeploy Backend

```powershell
git add .
git commit -m "Configure CORS for production"
git push
```

Render will automatically redeploy.

---

## ✅ Part 5: Verify Deployment

### 1. Test Backend Health

Visit: `https://welog-backend.onrender.com/actuator/health`

Expected response:

```json
{
  "status": "UP"
}
```

### 2. Test API Endpoints

```powershell
# Get all posts
Invoke-RestMethod -Uri "https://welog-backend.onrender.com/api/v1/posts"

# Test image serving
Invoke-WebRequest -Uri "https://welog-backend.onrender.com/api/v1/img/users/test.jpg"
```

### 3. Test Frontend

Visit: `https://welog-frontend.onrender.com`

**Checklist:**

- ✅ Home page loads
- ✅ Posts display correctly
- ✅ User avatars load
- ✅ Login/Register works
- ✅ Can create new post
- ✅ Can upload images (user avatar, post cover)
- ✅ Uploaded images persist after backend restart

### 4. Test Image Persistence

1. Upload an avatar via the app
2. Note the image URL
3. In Render Dashboard → welog-backend → Manual Deploy → **Deploy latest commit**
4. Wait for restart
5. Verify image still loads (should work because of persistent disk!)

---

## 🐛 Troubleshooting

### Backend Won't Start

**Check Logs:**

1. Render Dashboard → welog-backend → Logs
2. Look for errors like:
   - Database connection failed
   - JAR not found
   - Port binding issues

**Common Solutions:**

1. **Database Connection Error:**

   ```
   Verify DB_URL format:
   jdbc:postgresql://db.xxxxxxxxxxxxx.supabase.co:5432/postgres

   Check DB_USERNAME and DB_PASSWORD match Supabase
   ```

2. **JAR Not Found:**

   ```powershell
   # Rebuild locally
   ./gradlew clean build

   # Verify JAR exists
   ls build/libs/

   # Push and redeploy
   git push
   ```

3. **Upload Directory Not Writable:**
   ```
   Check Render Disk mount path is exactly: /app/uploads
   Check UPLOAD_DIR environment variable: /app/uploads/img
   ```

### Images Not Loading

**1. Check Image URLs in Frontend:**

Correct format:

```javascript
// User avatar
https://welog-backend.onrender.com/api/v1/img/users/user_123_1234567890.jpg

// Post cover
https://welog-backend.onrender.com/api/v1/img/posts/post_456_1234567890.jpg
```

**2. Verify Static Resource Handler:**

Check `WebConfig.java` has:

```java
registry.addResourceHandler("/api/v1/img/users/**")
        .addResourceLocations("file:" + uploadDir + "/users/");
```

**3. Test Direct Access:**

```powershell
curl https://welog-backend.onrender.com/api/v1/img/users/filename.jpg -v
```

**4. Check Disk Mount:**

In Render Dashboard → welog-backend → Disks:

- Verify disk is attached
- Check mount path is `/app/uploads`
- Check disk usage

### Frontend Can't Connect to Backend

**1. CORS Error:**

Check browser console for:

```
Access to XMLHttpRequest blocked by CORS policy
```

Solution: Update CORS configuration (see Part 4)

**2. Wrong API URL:**

Verify `VITE_API_URL` in frontend environment variables matches backend URL.

**3. Backend Sleeping (Free Tier):**

Free tier services spin down after 15 minutes of inactivity.

- First request takes 30-60 seconds
- Consider upgrading to Starter plan ($7/month) for 24/7 uptime

---

## 💾 Part 6: Backup & Recovery

### Backup Persistent Disk

Render doesn't have built-in backup for free tier. Use this strategy:

**1. Manual Backup via API:**

Create an endpoint to list and download images:

```java
@GetMapping("/admin/backup/images")
public ResponseEntity<List<String>> listImages() {
    // List all images in uploads directory
    // Return as JSON
}

@GetMapping("/admin/backup/download/{type}/{filename}")
public ResponseEntity<Resource> downloadImage(@PathVariable String type,
                                              @PathVariable String filename) {
    // Stream the file for download
}
```

**2. Sync to Cloud Storage (Recommended):**

Consider migrating to cloud storage for production:

- **Cloudinary** (free tier: 25GB storage, 25GB bandwidth)
- **AWS S3** (pay-as-you-go)
- **Supabase Storage** (integrated with your database!)

### Backup Supabase Database

```powershell
# Using pg_dump (install PostgreSQL tools)
$env:PGPASSWORD="your-password"
pg_dump -h db.xxxxx.supabase.co -U postgres.xxxxx -d postgres -F c -f backup.dump

# Restore
pg_restore -h db.xxxxx.supabase.co -U postgres.xxxxx -d postgres -c backup.dump
```

Or use Supabase Dashboard → Database → Backups (automatic on paid plans).

---

## 📊 Part 7: Monitoring & Maintenance

### Monitor Service Health

**Render Dashboard:**

- CPU & Memory usage
- Request logs
- Error rates
- Disk usage

**Set Up Alerts:**

1. Render → Settings → Notifications
2. Add email/Slack webhook
3. Get notified of:
   - Deployment failures
   - Service crashes
   - High resource usage

### View Logs

```powershell
# Real-time logs (Render CLI)
npm install -g @render-oss/cli
render login
render logs welog-backend --tail
```

Or use Render Dashboard → Logs tab.

### Check Disk Usage

Render Dashboard → welog-backend → Disks → uploads

Monitor usage and clean up old images if needed.

---

## ⚠️ Free Tier Limitations

| Resource          | Free Tier       | Starter ($7/mo) |
| ----------------- | --------------- | --------------- |
| **RAM**           | 512 MB          | 2 GB            |
| **CPU**           | Shared          | Shared          |
| **Disk**          | 1 GB            | Unlimited       |
| **Bandwidth**     | 100 GB/mo       | 100 GB/mo       |
| **Sleep**         | 15 min inactive | Always on       |
| **Build Time**    | 15 min          | 30 min          |
| **Custom Domain** | ✅ Yes          | ✅ Yes          |
| **HTTPS**         | ✅ Yes          | ✅ Yes          |

**Upgrade Recommendations:**

- Image-heavy blog with >100 uploads → Upgrade disk
- High traffic (>1000 daily users) → Starter plan
- Need 24/7 uptime → Starter plan

---

## 🎨 Part 8: Custom Domain (Optional)

### Add Custom Domain

1. **For Backend:**

   - Render → welog-backend → Settings → Custom Domain
   - Add: `api.yourdomain.com`
   - Add CNAME in your DNS: `api.yourdomain.com → welog-backend.onrender.com`

2. **For Frontend:**

   - Render → welog-frontend → Settings → Custom Domain
   - Add: `blog.yourdomain.com` or `yourdomain.com`
   - Add CNAME in your DNS: `blog.yourdomain.com → welog-frontend.onrender.com`

3. **Update Frontend Config:**

   ```env
   VITE_API_URL=https://api.yourdomain.com/api/v1
   ```

4. **Update CORS:**
   Add your domain to allowed origins in `WebConfig.java`.

5. **Wait for DNS Propagation:**
   Usually 5-60 minutes.

---

## 🚀 Part 9: Performance Optimization

### 1. Enable Response Compression

Add to `application.properties`:

```properties
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
server.compression.min-response-size=1024
```

### 2. Configure Caching

Already done in `WebConfig.java`:

```java
.setCachePeriod(3600); // 1 hour cache for images
```

### 3. Optimize Database Queries

Enable connection pooling:

```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
```

### 4. Add Redis Caching (Optional)

For high-traffic applications, add Redis:

1. Render → New → Redis
2. Add to Spring Boot:
   ```gradle
   implementation 'org.springframework.boot:spring-boot-starter-data-redis'
   ```
3. Configure caching for posts, users, etc.

---

## 📚 Additional Resources

- [Render Docs](https://render.com/docs)
- [Render Docker Deployment](https://render.com/docs/docker)
- [Render Disks](https://render.com/docs/disks)
- [Supabase Docs](https://supabase.com/docs)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)

---

## ✅ Deployment Checklist

- [ ] Build JAR locally: `./gradlew clean build`
- [ ] Push code to GitHub
- [ ] Create backend web service on Render
- [ ] Add all environment variables (DB, JWT, UPLOAD_DIR)
- [ ] Add persistent disk at `/app/uploads` (1 GB minimum)
- [ ] Deploy backend and verify health endpoint
- [ ] Create frontend static site on Render
- [ ] Update frontend API URL
- [ ] Deploy frontend
- [ ] Test CORS (login, create post)
- [ ] Upload test image
- [ ] Restart backend and verify image persists
- [ ] Configure custom domain (optional)
- [ ] Set up monitoring/alerts

---

## 🎉 Success!

Your Welog blog is now live on Render with:

- ✅ Supabase PostgreSQL database
- ✅ Persistent image storage
- ✅ Docker-based deployment
- ✅ HTTPS enabled
- ✅ Global CDN for frontend
- ✅ Automatic deployments on git push

**Access your blog:**

- Frontend: `https://welog-frontend.onrender.com`
- Backend API: `https://welog-backend.onrender.com/api/v1`

---

## 🆘 Need Help?

- **Render Community**: https://community.render.com
- **Render Status**: https://status.render.com
- **Supabase Discord**: https://discord.supabase.com
- **Check Logs**: Render Dashboard → Service → Logs

**Happy Blogging! 🎊**
