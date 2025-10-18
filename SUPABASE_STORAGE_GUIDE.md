# Supabase Storage Setup & Render Deployment Guide

Complete guide for migrating from local file storage to Supabase Storage and deploying to Render.

## 🎯 What Changed

Your Welog application now uses **Supabase Storage** instead of local file storage:

- ✅ All images stored in Supabase Storage buckets
- ✅ Images served via Supabase CDN (fast global delivery)
- ✅ No persistent disk needed on Render
- ✅ Automatic backups and scalability
- ✅ Works perfectly on Render's free tier

---

## 📦 Part 1: Set Up Supabase Storage

### Step 1: Create Storage Buckets

1. Go to your **Supabase Dashboard**
2. Navigate to **Storage** in the left sidebar
3. Click **"New bucket"**

#### Create User Avatars Bucket

- **Name**: `user-avatars`
- **Public bucket**: ✅ **Yes** (check this box)
- **File size limit**: `2048` KB (2 MB)
- **Allowed MIME types**: `image/jpeg,image/jpg,image/png,image/webp,image/gif`

Click **"Create bucket"**

#### Create Post Covers Bucket

- **Name**: `post-covers`
- **Public bucket**: ✅ **Yes** (check this box)
- **File size limit**: `5120` KB (5 MB)
- **Allowed MIME types**: `image/jpeg,image/jpg,image/png,image/webp,image/gif`

Click **"Create bucket"**

### Step 2: Configure Bucket Policies (Optional)

For additional security, you can set up RLS policies:

1. Click on bucket → **Policies**
2. Add policies for authenticated uploads:

```sql
-- Allow authenticated users to upload to user-avatars
CREATE POLICY "Allow authenticated uploads"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'user-avatars');

-- Allow public reads
CREATE POLICY "Allow public reads"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'user-avatars');

-- Allow users to delete their own files
CREATE POLICY "Allow user deletes"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'user-avatars');
```

Repeat for `post-covers` bucket.

### Step 3: Get Supabase Credentials

1. Go to **Settings** → **API** in your Supabase Dashboard
2. Copy these values:

```
Project URL: https://xxxxxxxxxxxxx.supabase.co
anon/public key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

⚠️ **Important**: Use the **anon/public** key, NOT the service_role key!

---

## 🔧 Part 2: Configure Your Application

### Step 1: Update .env File

Copy `.env.example` to `.env` and update with your credentials:

```env
# Supabase PostgreSQL Database
DB_URL=jdbc:postgresql://db.xxxxxxxxxxxxx.supabase.co:5432/postgres
DB_USERNAME=postgres.xxxxxxxxxxxxx
DB_PASSWORD=your-actual-database-password

# Supabase Storage
SUPABASE_URL=https://xxxxxxxxxxxxx.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# JWT Configuration
JWT_SECRET=your-64-character-random-secret-key-here
JWT_EXPIRATION_MS=86400000
```

**Generate JWT Secret:**

```powershell
-join (1..64 | ForEach { [char]((65..90) + (97..122) + (48..57) | Get-Random) })
```

### Step 2: Verify Build Configuration

The following files have been updated automatically:

✅ `build.gradle` - Added WebClient dependency  
✅ `SupabaseConfig.java` - WebClient configuration  
✅ `SupabaseStorageService.java` - Upload/delete logic  
✅ `UserService.java` - Avatar upload to Supabase  
✅ `PostService.java` - Cover image upload to Supabase  
✅ `WebConfig.java` - Removed local resource handlers  
✅ `application.properties` - Added Supabase properties  
✅ `docker-compose.yml` - Updated environment variables  
✅ `Dockerfile` - Simplified (no local storage needed)

---

## 🧪 Part 3: Test Locally

### Step 1: Build the Application

```powershell
# Clean and build
./gradlew clean build

# Verify JAR exists
ls build/libs/
```

### Step 2: Start Docker Container

```powershell
# Create .env from template
Copy-Item .env.example .env
# Edit .env with your Supabase credentials
notepad .env

# Start Docker
docker-compose up -d

# View logs
docker-compose logs -f backend
```

### Step 3: Test Image Upload

1. **Open browser**: http://localhost:8080
2. **Register/Login** to your account
3. **Upload avatar**:

   - Go to profile/settings
   - Click avatar upload
   - Select an image
   - Submit

4. **Verify in Supabase**:

   - Go to Supabase Dashboard → Storage → user-avatars
   - You should see your uploaded image with a UUID filename

5. **Check image URL**:

   - Right-click on avatar in your app
   - Copy image address
   - Should be: `https://xxxxx.supabase.co/storage/v1/object/public/user-avatars/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.jpg`

6. **Test post cover upload**:
   - Create a new post
   - Upload a cover image
   - Verify in Supabase Dashboard → Storage → post-covers

### Step 4: Test Image Persistence

```powershell
# Restart container
docker-compose restart backend

# Wait for restart
Start-Sleep -Seconds 10

# Open app again
# Images should still load from Supabase CDN ✅
```

---

## 🚀 Part 4: Deploy to Render

### Step 1: Push to GitHub

```powershell
git add .
git commit -m "Migrate to Supabase Storage for image hosting"
git push origin main
```

### Step 2: Create/Update Backend Service on Render

1. Go to [Render Dashboard](https://dashboard.render.com)
2. If you already have a backend service:
   - Click on **welog-backend** → **Environment**
3. If creating new service:
   - Click **"New +"** → **"Web Service"**
   - Connect GitHub repository
   - Name: `welog-backend`
   - Environment: **Docker**
   - Dockerfile: `./Dockerfile`

### Step 3: Configure Environment Variables

Add/Update these environment variables:

| Key                 | Value                                                  | Notes            |
| ------------------- | ------------------------------------------------------ | ---------------- |
| `DB_URL`            | `jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres` | From Supabase    |
| `DB_USERNAME`       | `postgres.xxxxx`                                       | From Supabase    |
| `DB_PASSWORD`       | Your Supabase DB password                              | From Supabase    |
| `SUPABASE_URL`      | `https://xxxxx.supabase.co`                            | Your project URL |
| `SUPABASE_KEY`      | `eyJhbGc...`                                           | anon/public key  |
| `JWT_SECRET`        | 64-character random string                             | Generate new     |
| `JWT_EXPIRATION_MS` | `86400000`                                             | 24 hours         |

⚠️ **Important**: Remove any `UPLOAD_DIR` variable if present - no longer needed!

### Step 4: Remove Persistent Disk (If Exists)

Since you no longer need local storage:

1. Go to **welog-backend** → **Disks**
2. If you see any disk attached, click **"Delete"**
3. Confirm deletion

### Step 5: Deploy

1. Click **"Manual Deploy"** → **"Deploy latest commit"**
2. Or just save environment variables - Render will auto-deploy
3. Wait 5-10 minutes for deployment
4. Check logs for any errors

### Step 6: Verify Deployment

```powershell
# Test health endpoint
Invoke-WebRequest -Uri "https://welog-backend.onrender.com/actuator/health"

# Should return: {"status":"UP"}
```

---

## ✅ Part 5: Testing Checklist

### Backend Testing

- [ ] Service deployed successfully on Render
- [ ] Health endpoint returns 200 OK
- [ ] No errors in Render logs
- [ ] Environment variables are set correctly

### Image Upload Testing

- [ ] Can register/login on deployed app
- [ ] Can upload user avatar
- [ ] Avatar appears in Supabase Storage → user-avatars
- [ ] Avatar displays in app with Supabase URL
- [ ] Can upload post cover image
- [ ] Cover appears in Supabase Storage → post-covers
- [ ] Cover displays in post with Supabase URL

### Persistence Testing

- [ ] Upload an avatar
- [ ] Note the Supabase URL
- [ ] Trigger manual deploy on Render
- [ ] Wait for redeploy
- [ ] Avatar still loads from Supabase ✅

### Frontend Integration (If Needed)

Since your database stores full Supabase URLs now, frontend should work automatically:

```javascript
// Old (local storage)
<img src={`${API_URL}/img/users/${user.photo}`} />

// New (Supabase storage)
<img src={user.photo} />  // photo is already full URL

// Or with fallback
<img
  src={user.photo || `https://ui-avatars.com/api/?name=${user.name}`}
  alt={user.name}
/>
```

---

## 📊 Architecture Comparison

### Before (Local Storage)

```
┌─────────────────────┐
│  Render Container   │
│  ┌───────────────┐  │
│  │ Spring Boot   │  │
│  └───────┬───────┘  │
│          │           │
│          ▼           │
│  ┌───────────────┐  │
│  │ Persistent    │  │ ❌ Required paid plan
│  │ Disk (1GB)    │  │ ❌ Not available on free tier
│  └───────────────┘  │
└─────────────────────┘
```

### After (Supabase Storage)

```
┌─────────────────────┐        ┌──────────────────┐
│  Render Container   │        │     Supabase     │
│  ┌───────────────┐  │        │  ┌────────────┐  │
│  │ Spring Boot   │──┼───────▶│  │  Storage   │  │
│  └───────────────┘  │ API    │  │  Buckets   │  │
│                     │        │  └────────────┘  │
│  ✅ Free tier works │        │  ✅ Built-in CDN │
│  ✅ No disk needed  │        │  ✅ Auto backups │
└─────────────────────┘        └──────────────────┘
```

---

## 🎉 Benefits of Supabase Storage

| Feature                   | Local Storage           | Supabase Storage            |
| ------------------------- | ----------------------- | --------------------------- |
| **Render Free Tier**      | ❌ No persistent disk   | ✅ Works perfectly          |
| **Scalability**           | ❌ Limited to disk size | ✅ Unlimited                |
| **Performance**           | ⚠️ Same server          | ✅ Global CDN               |
| **Backups**               | ❌ Manual               | ✅ Automatic                |
| **Cost**                  | $7/month minimum        | ✅ Free tier: 1GB storage   |
| **Image Transformations** | ❌ Not available        | ✅ Optional resize/optimize |
| **Bandwidth**             | ⚠️ Counts against app   | ✅ Separate CDN bandwidth   |

---

## 🐛 Troubleshooting

### Issue: 401 Unauthorized when uploading

**Cause**: Wrong Supabase API key

**Solution**:

1. Verify you're using **anon/public** key, not service_role
2. Check key in Supabase Dashboard → Settings → API
3. Update `SUPABASE_KEY` environment variable

### Issue: 404 Not Found when uploading

**Cause**: Bucket doesn't exist or wrong bucket name

**Solution**:

1. Verify buckets exist: `user-avatars` and `post-covers`
2. Check bucket names match exactly (case-sensitive)
3. Ensure buckets are set to **Public**

### Issue: CORS error when uploading

**Cause**: Supabase CORS not configured for your domain

**Solution**:

1. Supabase Dashboard → Settings → API
2. Add your Render URL to allowed origins
3. Or use `*` for testing (not recommended for production)

### Issue: Image uploads but doesn't display

**Cause**: Bucket not public

**Solution**:

1. Go to Storage → Click bucket
2. Settings → **Public bucket**: ✅ Enable
3. Click **Save**

### Issue: Old local images not working

**Expected**: Local filenames (like `user_1_123456.jpg`) won't work anymore

**Solution**:

1. Users need to re-upload their avatars
2. Or migrate old images to Supabase manually
3. Or implement fallback logic in frontend

---

## 📚 API Reference

### SupabaseStorageService Methods

```java
// Upload file to bucket
String uploadFile(MultipartFile file, String bucketName) throws IOException

// Delete file from bucket
void deleteFile(String fileUrl, String bucketName)

// Get public URL for file
String getPublicUrl(String bucketName, String filename)
```

### Example Usage

```java
// In your service
@Autowired
private SupabaseStorageService supabaseStorageService;

// Upload avatar
String avatarUrl = supabaseStorageService.uploadFile(photoFile, "user-avatars");
user.setPhoto(avatarUrl);

// Delete old avatar
supabaseStorageService.deleteFile(user.getPhoto(), "user-avatars");

// Upload post cover
String coverUrl = supabaseStorageService.uploadFile(coverFile, "post-covers");
post.setCoverImage(coverUrl);
```

---

## 🔐 Security Best Practices

### 1. Use RLS Policies

Enable Row Level Security on storage buckets:

```sql
ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;

-- Only allow authenticated users to upload
CREATE POLICY "Authenticated uploads"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id IN ('user-avatars', 'post-covers'));
```

### 2. Validate File Types

Already implemented in `SupabaseStorageService`:

```java
// Content type validation
String contentType = file.getContentType();
if (contentType == null || !contentType.startsWith("image/")) {
    throw new IllegalArgumentException("Only images allowed");
}
```

### 3. Limit File Sizes

Configured in Supabase bucket settings:

- User avatars: 2 MB max
- Post covers: 5 MB max

Also enforced in Spring Boot:

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 4. Use Environment Variables

Never hardcode credentials:

```properties
# ✅ Good
supabase.url=${SUPABASE_URL}
supabase.key=${SUPABASE_KEY}

# ❌ Bad
supabase.url=https://xxxxx.supabase.co
supabase.key=eyJhbGc...
```

---

## 📈 Monitoring

### Check Upload Stats

Supabase Dashboard → Storage → Buckets:

- View total files
- Check storage usage
- Monitor bandwidth

### Check Logs

**Backend Logs (Render)**:

```
2024-10-18 10:30:15 INFO  SupabaseStorageService - Uploading file to Supabase: bucket=user-avatars, filename=uuid.jpg, size=123456 bytes
2024-10-18 10:30:16 INFO  SupabaseStorageService - File uploaded successfully: https://...
```

**Application Logs**:

```powershell
# View real-time logs
docker-compose logs -f backend | Select-String "Supabase"
```

---

## 🎯 Next Steps

1. ✅ **Test locally** with Docker
2. ✅ **Push to GitHub**
3. ✅ **Deploy to Render**
4. ✅ **Test production uploads**
5. ✅ **Update frontend** (if using local URLs)
6. ⭐ **Optional**: Set up image transformations
7. ⭐ **Optional**: Migrate existing images

---

## 🆘 Need Help?

- **Supabase Docs**: https://supabase.com/docs/guides/storage
- **Supabase Discord**: https://discord.supabase.com
- **Render Docs**: https://render.com/docs
- **Render Community**: https://community.render.com

---

## 🎊 Success!

Your Welog application now uses **Supabase Storage** for all image uploads:

- ✅ Works on Render's free tier
- ✅ Global CDN delivery
- ✅ Automatic backups
- ✅ Unlimited scalability
- ✅ No persistent disk needed

**Your application is production-ready! 🚀**
