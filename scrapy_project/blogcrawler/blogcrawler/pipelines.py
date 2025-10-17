# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html

import json
import logging
import hashlib
from datetime import datetime
from itemadapter import ItemAdapter
import psycopg2
from psycopg2.extras import RealDictCursor
import scrapy


class ValidationPipeline:
    """Validate and clean item data"""
    
    def process_item(self, item, spider):
        adapter = ItemAdapter(item)
        
        # Validate required fields
        required_fields = ['title', 'url', 'author_name']
        for field in required_fields:
            if not adapter.get(field):
                raise scrapy.exceptions.DropItem(f"Missing required field: {field}")
        
        # Clean and validate URL
        url = adapter.get('url')
        if not url.startswith(('http://', 'https://')):
            raise scrapy.exceptions.DropItem(f"Invalid URL: {url}")
        
        # Ensure numeric fields are integers
        numeric_fields = ['likes_count', 'comments_count', 'bookmarks_count', 'reading_time']
        for field in numeric_fields:
            value = adapter.get(field)
            if value is not None:
                try:
                    adapter[field] = int(value)
                except (ValueError, TypeError):
                    adapter[field] = 0
        
        # Add scraped timestamp
        adapter['scraped_at'] = datetime.now().isoformat()
        adapter['source_website'] = 'dev.to'
        
        # Generate unique ID based on URL
        url_hash = hashlib.md5(url.encode()).hexdigest()
        adapter['post_id'] = f"devto_{url_hash[:12]}"
        
        return item


class DuplicatesPipeline:
    """Filter out duplicate items based on URL"""
    
    def __init__(self):
        self.seen_urls = set()
    
    def process_item(self, item, spider):
        adapter = ItemAdapter(item)
        url = adapter['url']
        
        if url in self.seen_urls:
            raise scrapy.exceptions.DropItem(f"Duplicate item found: {url}")
        else:
            self.seen_urls.add(url)
            return item


class DatabasePipeline:
    """Store items in PostgreSQL database"""
    
    def __init__(self, db_config):
        self.db_config = db_config
        self.connection = None
        
    @classmethod
    def from_crawler(cls, crawler):
        db_config = crawler.settings.getdict("DATABASE_CONFIG")
        return cls(db_config=db_config)
    
    def open_spider(self, spider):
        try:
            self.connection = psycopg2.connect(**self.db_config)
            self.connection.autocommit = True
            self.create_tables()
            logging.info("Connected to PostgreSQL database")
        except Exception as e:
            logging.error(f"Error connecting to database: {e}")
    
    def close_spider(self, spider):
        if self.connection:
            self.connection.close()
            logging.info("Database connection closed")
    
    def create_tables(self):
        """Create tables if they don't exist"""
        cursor = self.connection.cursor()
        
        # Create scraped_posts table
        create_table_query = """
        CREATE TABLE IF NOT EXISTS scraped_posts (
            id SERIAL PRIMARY KEY,
            post_id VARCHAR(50) UNIQUE NOT NULL,
            url TEXT UNIQUE NOT NULL,
            slug VARCHAR(255),
            title TEXT NOT NULL,
            content TEXT,
            excerpt TEXT,
            author_name VARCHAR(255) NOT NULL,
            author_username VARCHAR(255),
            author_profile_url TEXT,
            author_avatar TEXT,
            published_at TIMESTAMP,
            updated_at TIMESTAMP,
            reading_time INTEGER DEFAULT 0,
            likes_count INTEGER DEFAULT 0,
            comments_count INTEGER DEFAULT 0,
            bookmarks_count INTEGER DEFAULT 0,
            tags JSONB,
            cover_image TEXT,
            scraped_at TIMESTAMP DEFAULT NOW(),
            source_website VARCHAR(50) DEFAULT 'dev.to',
            created_at TIMESTAMP DEFAULT NOW(),
            updated_at_db TIMESTAMP DEFAULT NOW()
        );
        
        CREATE INDEX IF NOT EXISTS idx_scraped_posts_post_id ON scraped_posts(post_id);
        CREATE INDEX IF NOT EXISTS idx_scraped_posts_author ON scraped_posts(author_name);
        CREATE INDEX IF NOT EXISTS idx_scraped_posts_published ON scraped_posts(published_at);
        CREATE INDEX IF NOT EXISTS idx_scraped_posts_tags ON scraped_posts USING GIN(tags);
        """
        
        cursor.execute(create_table_query)
        cursor.close()
        logging.info("Database tables created/verified")
    
    def process_item(self, item, spider):
        if not self.connection:
            return item
            
        try:
            cursor = self.connection.cursor(cursor_factory=RealDictCursor)
            adapter = ItemAdapter(item)
            
            # Convert tags list to JSON
            tags = adapter.get('tags', [])
            if isinstance(tags, list):
                tags_json = json.dumps(tags)
            else:
                tags_json = json.dumps([])
            
            # Convert date strings to proper format (if needed)
            published_at = adapter.get('published_at')
            updated_at = adapter.get('updated_at')
            
            # Insert or update item
            insert_query = """
            INSERT INTO scraped_posts (
                post_id, url, slug, title, content, excerpt,
                author_name, author_username, author_profile_url, author_avatar,
                published_at, updated_at, reading_time,
                likes_count, comments_count, bookmarks_count,
                tags, cover_image, scraped_at, source_website
            ) VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
            ) ON CONFLICT (post_id) DO UPDATE SET
                title = EXCLUDED.title,
                content = EXCLUDED.content,
                likes_count = EXCLUDED.likes_count,
                comments_count = EXCLUDED.comments_count,
                bookmarks_count = EXCLUDED.bookmarks_count,
                tags = EXCLUDED.tags,
                scraped_at = EXCLUDED.scraped_at,
                updated_at_db = NOW()
            """
            
            cursor.execute(insert_query, (
                adapter.get('post_id'),
                adapter.get('url'),
                adapter.get('slug'),
                adapter.get('title'),
                adapter.get('content'),
                adapter.get('excerpt'),
                adapter.get('author_name'),
                adapter.get('author_username'),
                adapter.get('author_profile_url'),
                adapter.get('author_avatar'),
                published_at,
                updated_at,
                adapter.get('reading_time', 0),
                adapter.get('likes_count', 0),
                adapter.get('comments_count', 0),
                adapter.get('bookmarks_count', 0),
                tags_json,
                adapter.get('cover_image'),
                adapter.get('scraped_at'),
                adapter.get('source_website')
            ))
            
            cursor.close()
            logging.info(f"Stored item: {adapter.get('title', 'Unknown')[:50]}...")
            
        except Exception as e:
            logging.error(f"Error storing item in database: {e}")
            
        return item


class JsonExportPipeline:
    """Export items to JSON file for backup"""
    
    def open_spider(self, spider):
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.filename = f"data/devto_backup_{timestamp}.json"
        self.file = open(self.filename, 'w', encoding='utf-8')
        self.items = []
    
    def close_spider(self, spider):
        json.dump(self.items, self.file, indent=2, ensure_ascii=False)
        self.file.close()
        logging.info(f"Exported {len(self.items)} items to {self.filename}")
    
    def process_item(self, item, spider):
        self.items.append(ItemAdapter(item).asdict())
        return item
