# Dev.to Blog Scraper

A comprehensive Scrapy-based web scraper for collecting blog posts from dev.to for educational purposes and database learning.

## 🎯 Purpose

This project helps you:

- Learn web scraping with Scrapy
- Work with large datasets in PostgreSQL
- Practice database design and optimization
- Build data pipelines
- Understand ethical web scraping practices

## 🚀 Quick Start

### 1. Prerequisites

- Python 3.8+
- PostgreSQL database
- Git (optional)

### 2. Installation

```bash
# Clone or download the project
cd scrapy_project

# Run setup script
python setup.py

# Or install manually
pip install -r requirements.txt
```

### 3. Configuration

Edit `blogcrawler/settings.py` and update database credentials:

```python
DATABASE_CONFIG = {
    'host': 'localhost',
    'port': 5431,
    'database': 'postgres',
    'user': 'postgres',
    'password': 'your_password_here'  # Update this!
}
```

### 4. Run the Spider

```bash
# Navigate to scrapy project
cd blogcrawler

# Basic crawl (5 pages per feed)
scrapy crawl devto

# Crawl more pages
scrapy crawl devto -a pages=10

# Export to specific file
scrapy crawl devto -o data/devto_posts.json
```

## 📊 Data Structure

### Scraped Fields

| Field                | Description                      | Type       |
| -------------------- | -------------------------------- | ---------- |
| `post_id`            | Unique identifier                | String     |
| `url`                | Post URL                         | String     |
| `title`              | Post title                       | String     |
| `content`            | Full post content (HTML)         | Text       |
| `excerpt`            | Post summary                     | String     |
| `author_name`        | Author display name              | String     |
| `author_username`    | Author username                  | String     |
| `author_profile_url` | Author profile URL               | String     |
| `author_avatar`      | Author avatar image URL          | String     |
| `published_at`       | Publication timestamp            | DateTime   |
| `reading_time`       | Estimated reading time (minutes) | Integer    |
| `likes_count`        | Number of reactions/likes        | Integer    |
| `comments_count`     | Number of comments               | Integer    |
| `bookmarks_count`    | Number of bookmarks              | Integer    |
| `tags`               | Post tags                        | JSON Array |
| `cover_image`        | Cover image URL                  | String     |
| `scraped_at`         | When item was scraped            | DateTime   |

### Database Schema

The scraper automatically creates a `scraped_posts` table with optimized indexes:

```sql
CREATE TABLE scraped_posts (
    id SERIAL PRIMARY KEY,
    post_id VARCHAR(50) UNIQUE NOT NULL,
    url TEXT UNIQUE NOT NULL,
    title TEXT NOT NULL,
    content TEXT,
    author_name VARCHAR(255) NOT NULL,
    -- ... other fields
    tags JSONB,  -- Searchable JSON for tags
    created_at TIMESTAMP DEFAULT NOW()
);

-- Optimized indexes
CREATE INDEX idx_scraped_posts_author ON scraped_posts(author_name);
CREATE INDEX idx_scraped_posts_published ON scraped_posts(published_at);
CREATE INDEX idx_scraped_posts_tags ON scraped_posts USING GIN(tags);
```

## 🔧 Configuration Options

### Spider Arguments

- `pages`: Number of pages to crawl per feed (default: 5)
  ```bash
  scrapy crawl devto -a pages=20
  ```

### Custom Settings

Edit `settings.py` to customize:

```python
# Crawling speed (be respectful!)
DOWNLOAD_DELAY = 2  # seconds between requests
CONCURRENT_REQUESTS_PER_DOMAIN = 1

# Enable/disable pipelines
ITEM_PIPELINES = {
    "blogcrawler.pipelines.ValidationPipeline": 300,
    "blogcrawler.pipelines.DuplicatesPipeline": 400,
    "blogcrawler.pipelines.DatabasePipeline": 500,  # Comment out to disable DB
}

# Export formats
FEEDS = {
    'data/posts_%(time)s.json': {'format': 'json'},
    'data/posts_%(time)s.csv': {'format': 'csv'},
}
```

## 📈 Data Analysis Examples

### Connect to Database

```python
import psycopg2
import pandas as pd

# Connect to database
conn = psycopg2.connect(
    host="localhost",
    port=5431,
    database="postgres",
    user="postgres",
    password="your_password"
)

# Load data into pandas
df = pd.read_sql("SELECT * FROM scraped_posts", conn)
```

### Basic Analysis Queries

```sql
-- Top authors by post count
SELECT author_name, COUNT(*) as post_count
FROM scraped_posts
GROUP BY author_name
ORDER BY post_count DESC
LIMIT 10;

-- Most popular tags
SELECT tag, COUNT(*) as usage_count
FROM scraped_posts,
     jsonb_array_elements_text(tags) as tag
GROUP BY tag
ORDER BY usage_count DESC
LIMIT 20;

-- Posts by reading time
SELECT
    CASE
        WHEN reading_time <= 3 THEN 'Quick (1-3 min)'
        WHEN reading_time <= 10 THEN 'Medium (4-10 min)'
        ELSE 'Long (10+ min)'
    END as reading_category,
    COUNT(*) as post_count,
    AVG(likes_count) as avg_likes
FROM scraped_posts
GROUP BY reading_category;

-- Top posts by engagement
SELECT title, author_name, likes_count, comments_count, published_at
FROM scraped_posts
ORDER BY (likes_count + comments_count) DESC
LIMIT 10;
```

## 🛠️ Advanced Usage

### Testing Individual URLs

```bash
# Test parsing a specific post
scrapy shell "https://dev.to/some-post-url"

# In the shell:
>>> from blogcrawler.spiders.postspider import DevToSpider
>>> spider = DevToSpider()
>>> item = next(spider.parse_post(response))
>>> print(item)
```

### Custom Pipelines

Create your own pipeline for specific processing:

```python
# blogcrawler/pipelines.py
class CustomAnalyticsPipeline:
    def process_item(self, item, spider):
        # Your custom logic here
        adapter = ItemAdapter(item)

        # Example: Sentiment analysis
        # adapter['sentiment'] = analyze_sentiment(adapter['content'])

        return item
```

### Monitoring and Logging

```bash
# Detailed logging
scrapy crawl devto -L DEBUG

# Log to file
scrapy crawl devto -L INFO --logfile=scraper.log

# Monitor progress
tail -f scraper.log
```

## 🚨 Ethical Considerations

### Respect robots.txt

- The scraper obeys `robots.txt` by default
- `ROBOTSTXT_OBEY = True` in settings.py

### Rate Limiting

- Built-in delays between requests
- AutoThrottle enabled to adapt to server load
- Respectful concurrent request limits

### Terms of Service

- Only scrape publicly available content
- Don't overwhelm the server
- Use data responsibly for educational purposes

### Best Practices

1. **Start small**: Test with few pages first
2. **Monitor impact**: Check server response times
3. **Cache responses**: Use Scrapy's built-in caching for development
4. **Handle errors gracefully**: The spider includes comprehensive error handling

## 🔍 Troubleshooting

### Common Issues

**Database Connection Error**

```
Solution: Check PostgreSQL is running and credentials are correct
```

**No items scraped**

```bash
# Debug with shell
scrapy shell "https://dev.to"
# Check if selectors work:
response.css('article.crayons-story h2 a::attr(href)').getall()
```

**Rate Limited**

```
Solution: Increase DOWNLOAD_DELAY in settings.py
```

### Debug Mode

```bash
# Run with debug logging
scrapy crawl devto -L DEBUG

# Or set in settings.py
LOG_LEVEL = 'DEBUG'
```

## 📊 Integration with Your Blog Project

### Export for Spring Boot

```python
# Convert scraped data for your blog
import json
import psycopg2

conn = psycopg2.connect(...)
cursor = conn.cursor()

cursor.execute("""
    SELECT
        title,
        content,
        author_name as author,
        published_at,
        tags,
        cover_image
    FROM scraped_posts
    WHERE published_at >= '2024-01-01'
    ORDER BY published_at DESC
""")

posts = cursor.fetchall()

# Export for Spring Boot import
blog_posts = []
for post in posts:
    blog_posts.append({
        'title': post[0],
        'content': post[1],
        'author': {'name': post[2]},
        'publishedAt': post[3].isoformat(),
        'tags': post[4],
        'coverImage': post[5]
    })

with open('blog_import.json', 'w') as f:
    json.dump(blog_posts, f, indent=2)
```

## 🎓 Learning Objectives

After using this scraper, you'll understand:

1. **Web Scraping**: CSS selectors, XPath, handling dynamic content
2. **Data Processing**: ETL pipelines, data validation, deduplication
3. **Database Design**: Indexes, JSON fields, query optimization
4. **Scalability**: Handling large datasets, batch processing
5. **Ethics**: Responsible scraping, rate limiting, legal considerations

## 📚 Next Steps

1. **Enhance the Spider**: Add more fields, handle JavaScript content
2. **Advanced Analytics**: Implement NLP for content analysis
3. **Real-time Processing**: Set up scheduled crawling
4. **Data Visualization**: Build dashboards with your data
5. **API Integration**: Feed data into your Spring Boot application

## 📝 License

This project is for educational purposes. Please respect dev.to's terms of service and use responsibly.

---

Happy scraping! 🕷️ Remember to always be respectful to the websites you're crawling.
