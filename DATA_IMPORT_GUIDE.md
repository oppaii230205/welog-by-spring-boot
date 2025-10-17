# 📊 Data Import Guide - Dev.to Scraped Posts

This guide shows you how to import the scraped dev.to blog posts into your Spring Boot blog database for practicing with large, realistic datasets.

## 🎯 What You'll Get

After importing, your database will have:

- **Hundreds of real blog posts** with actual content
- **Diverse authors** from the dev.to community
- **Popular tags** like JavaScript, Python, React, etc.
- **Realistic data relationships** between posts, authors, and tags
- **Great for testing** pagination, search, filtering, and database optimization

## 🚀 Import Methods

### Method 1: Command Line Runner (Recommended)

Run your Spring Boot app with the `import` profile:

```bash
# Navigate to your Spring Boot project root
cd "d:\D old\Studying\JavaSpringBoot\Welog_v2\welog"

# Run with import profile
./gradlew bootRun --args="--spring.profiles.active=import"

# Or with custom JSON file path
./gradlew bootRun --args="--spring.profiles.active=import /path/to/your/scraped_data.json"
```

### Method 2: REST API (For Admins)

If you prefer using the API or want to import during runtime:

```bash
# First, make sure you have admin role, then:
POST http://localhost:8080/api/v1/admin/import/posts
Content-Type: application/json
Authorization: Bearer YOUR_ADMIN_JWT_TOKEN

# Or with custom file path:
POST http://localhost:8080/api/v1/admin/import/posts?filePath=path/to/file.json
```

## 📁 File Locations

The import looks for your scraped JSON file at:

```
scrapy_project/blogcrawler/data/devto_posts.json
```

You can also specify custom paths:

```bash
# Absolute path
--spring.profiles.active=import "C:\path\to\your\data.json"

# Relative path
--spring.profiles.active=import "data/my_scraped_posts.json"
```

## 🔧 How the Import Works

### 📝 Data Transformation

The import service automatically:

1. **Creates Authors**:

   - Generates unique emails like `authorusername@devto.imported.local`
   - Sets default password: `imported123` (encoded)
   - Assigns `ROLE_USER` to all imported authors
   - Uses default avatar for all

2. **Processes Posts**:

   - Cleans HTML content (removes SVG, CSS classes)
   - Generates unique slugs to avoid conflicts
   - Creates excerpts from content if not provided
   - Handles missing or malformed data gracefully

3. **Manages Tags**:

   - Creates new tags or links to existing ones
   - Normalizes tag names (lowercase, trimmed)
   - Associates tags with posts via many-to-many relationship

4. **Prevents Duplicates**:
   - Skips posts with existing slugs
   - Won't import the same post twice

### 🛡️ Security & Validation

- ✅ **SQL Injection Safe**: Uses JPA/Hibernate with parameterized queries
- ✅ **Memory Efficient**: Processes posts one by one, not all at once
- ✅ **Transaction Safe**: Each post import is atomic
- ✅ **Error Tolerant**: Continues importing even if some posts fail
- ✅ **Admin Only**: REST API requires admin authentication

## 📊 Import Statistics

After import, you'll see output like:

```
✅ Successfully imported: 150 posts
⏭️ Skipped (duplicates): 5 posts
❌ Errors: 2 posts
📊 Total processed: 157 posts
🎉 Great! You now have 150 new sample posts with realistic data!
```

## 🔍 Verify Your Import

### Check Database Tables

```sql
-- Count imported posts
SELECT COUNT(*) FROM posts WHERE author_id IN (
    SELECT id FROM users WHERE email LIKE '%@devto.imported.local'
);

-- See popular imported tags
SELECT t.name, COUNT(pt.post_id) as post_count
FROM tags t
JOIN posts_tags pt ON t.id = pt.tag_id
JOIN posts p ON pt.post_id = p.id
JOIN users u ON p.author_id = u.id
WHERE u.email LIKE '%@devto.imported.local'
GROUP BY t.name
ORDER BY post_count DESC
LIMIT 10;

-- Check imported authors
SELECT name, email FROM users
WHERE email LIKE '%@devto.imported.local'
LIMIT 5;
```

### Test Your Frontend

1. Visit your blog homepage - you should see many new posts
2. Try searching for popular terms like "javascript", "react", "python"
3. Test pagination with the increased post count
4. Browse by different tags

## 🎓 Learning Opportunities

Now that you have realistic data, practice:

### 🔍 Database Optimization

```sql
-- Add indexes for better performance
CREATE INDEX idx_posts_title_search ON posts USING gin(to_tsvector('english', title));
CREATE INDEX idx_posts_author_created ON posts(author_id, created_at);

-- Analyze query performance
EXPLAIN ANALYZE SELECT * FROM posts WHERE title ILIKE '%javascript%' LIMIT 10;
```

### 📈 Advanced Queries

```sql
-- Find most popular authors by post count
SELECT u.name, COUNT(p.id) as post_count
FROM users u
JOIN posts p ON u.id = p.author_id
GROUP BY u.id, u.name
ORDER BY post_count DESC;

-- Posts with most tags
SELECT p.title, COUNT(pt.tag_id) as tag_count
FROM posts p
JOIN posts_tags pt ON p.id = pt.post_id
GROUP BY p.id, p.title
ORDER BY tag_count DESC;

-- Tag popularity over time
SELECT t.name, DATE(p.created_at) as post_date, COUNT(*) as posts_per_day
FROM tags t
JOIN posts_tags pt ON t.id = pt.tag_id
JOIN posts p ON pt.post_id = p.id
WHERE t.name IN ('javascript', 'python', 'react')
GROUP BY t.name, DATE(p.created_at)
ORDER BY post_date DESC;
```

### 🧪 Testing Scenarios

- **Pagination**: Test with 1000+ posts
- **Search**: Full-text search across large content
- **Filtering**: Multiple tag combinations
- **Performance**: Response times with realistic data load

## 🚨 Troubleshooting

### Common Issues

**"File not found"**

```bash
# Make sure you're in the right directory
ls scrapy_project/blogcrawler/data/devto_posts.json

# Or use absolute path
--spring.profiles.active=import "d:\path\to\your\scrapy_project\blogcrawler\data\devto_posts.json"
```

**"Import failed - Connection refused"**

- Ensure PostgreSQL is running on port 5431
- Check your `application.properties` database settings
- Verify credentials: `postgres / mysecretpassword`

**"Role not found"**

- Make sure your database has the default roles created
- Run your app normally first to initialize roles
- Or manually insert: `INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');`

**"Memory issues with large files"**

- The import processes one post at a time, so memory should be fine
- If needed, split large JSON files into smaller chunks

**"Posts not showing in frontend"**

- Check that imported posts have `deleted_at IS NULL`
- Verify the author relationship exists
- Clear any frontend caching

## 🎯 Next Steps

1. **Run the import** to get your sample data
2. **Explore the data** in your database browser
3. **Test your frontend** with realistic content
4. **Optimize queries** as you find performance bottlenecks
5. **Add features** like analytics, recommendations, or advanced search

## 📚 Integration with Your Existing Code

The imported data works seamlessly with your existing:

- ✅ Post controller and services
- ✅ User authentication system
- ✅ Tag management
- ✅ Search functionality
- ✅ Frontend components
- ✅ Like and notification systems

No code changes needed - just more data to work with!

---

**Happy learning with realistic data!** 🚀📊

_P.S. Remember these are sample posts for learning. In production, always respect content licenses and attribution._
