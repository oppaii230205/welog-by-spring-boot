# Quick Reference - Docker & Render Setup

## 🚀 Quick Commands

### Local Development

```powershell
# Build & Start
./gradlew clean build
docker-compose up -d

# View Logs
docker-compose logs -f backend

# Restart
docker-compose restart backend

# Stop
docker-compose down

# Test
./test-docker-images.ps1

# Shell Access
docker-compose exec backend /bin/bash
```

### Git Deploy

```powershell
git add .
git commit -m "Your message"
git push origin main
```

---

## 📁 Critical Files

| File                     | Purpose                                 |
| ------------------------ | --------------------------------------- |
| `Dockerfile`             | Container setup with upload directories |
| `docker-compose.yml`     | Local orchestration with volumes        |
| `.env`                   | Local secrets (Supabase, JWT)           |
| `WebConfig.java`         | Image serving configuration             |
| `application.properties` | Upload directory path                   |

---

## 🔑 Environment Variables

```env
# Required for both Local & Render
DB_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
DB_USERNAME=postgres.xxxxx
DB_PASSWORD=your-supabase-password
JWT_SECRET=64-character-random-string
JWT_EXPIRATION_MS=86400000
UPLOAD_DIR=/app/uploads/img
```

---

## 🌐 Image URLs

### Upload Endpoints

```
POST /api/v1/users/updateMe       (avatar)
POST /api/v1/posts/{id}/coverImage (cover)
```

### Access Endpoints

```
GET /api/v1/img/users/{filename}
GET /api/v1/img/posts/{filename}
GET /img/users/{filename}
GET /img/posts/{filename}
```

---

## 📦 Render Setup Steps

### 1. Backend (Web Service)

- Environment: **Docker**
- Dockerfile: `./Dockerfile`
- Disk: `/app/uploads` (1 GB+)
- Add all environment variables

### 2. Frontend (Static Site)

- Root: `blog-frontend`
- Build: `npm install && npm run build`
- Publish: `dist`
- Env: `VITE_API_URL=https://your-backend.onrender.com/api/v1`

---

## ✅ Testing Checklist

### Local

- [ ] JAR built
- [ ] .env created
- [ ] Docker started
- [ ] Images upload
- [ ] Images persist after restart

### Render

- [ ] Backend deployed
- [ ] Disk attached
- [ ] Frontend deployed
- [ ] Images upload
- [ ] Images persist after redeploy

---

## 🐛 Quick Troubleshooting

| Problem              | Solution                         |
| -------------------- | -------------------------------- |
| Images return 404    | Check WebConfig paths            |
| Images disappear     | Check volume/disk mount          |
| Permission denied    | `chmod -R 755 /app/uploads`      |
| DB connection failed | Verify Supabase credentials      |
| Build failed         | Ensure JAR exists in build/libs/ |

---

## 📊 Volume Mapping

```yaml
# Local (docker-compose.yml)
volumes:
  - ./uploads:/app/uploads

# Render
Disk: /app/uploads (1 GB)
```

---

## 🔗 Documentation

- **Complete Guide**: `DEPLOYMENT_COMPLETE_GUIDE.md`
- **Render Steps**: `RENDER_DEPLOYMENT_GUIDE.md`
- **Local Testing**: `DOCKER_TESTING_GUIDE.md`
- **Architecture**: `DOCKER_IMAGE_ARCHITECTURE.md`

---

## 💡 Pro Tips

1. **Always test locally first** with docker-compose
2. **Never use** `docker-compose down -v` (deletes volumes)
3. **Use strong secrets** (64+ chars for JWT)
4. **Monitor disk usage** on Render dashboard
5. **Keep uploads/ in .gitignore**

---

## 🎯 Success Verification

```powershell
# 1. Health check
curl http://localhost:8080/actuator/health

# 2. Test image
curl http://localhost:8080/api/v1/img/users/test.jpg

# 3. Check logs
docker-compose logs backend | Select-String "error"

# 4. Verify persistence
docker-compose restart backend
# Then re-test image URL
```

---

## 🆘 Emergency Commands

```powershell
# Clean restart
docker-compose down
docker-compose up -d --build

# Rebuild JAR
./gradlew clean build --no-daemon

# Fix permissions
docker-compose exec backend chmod -R 755 /app/uploads

# View all logs
docker-compose logs --tail=100 backend

# System cleanup
docker system prune -a
```

---

## 📞 Support Resources

- **Render Docs**: https://render.com/docs
- **Render Status**: https://status.render.com
- **Supabase Docs**: https://supabase.com/docs
- **Community**: https://community.render.com

---

**Keep this file handy for quick reference! 📌**
