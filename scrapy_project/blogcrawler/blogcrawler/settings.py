# Scrapy settings for blogcrawler project
#
# For simplicity, this file contains only settings considered important or
# commonly used. You can find more settings consulting the documentation:
#
#     https://docs.scrapy.org/en/latest/topics/settings.html
#     https://docs.scrapy.org/en/latest/topics/downloader-middleware.html
#     https://docs.scrapy.org/en/latest/topics/spider-middleware.html

BOT_NAME = "blogcrawler"

SPIDER_MODULES = ["blogcrawler.spiders"]
NEWSPIDER_MODULE = "blogcrawler.spiders"

ADDONS = {}

# Crawl responsibly by identifying yourself (and your website) on the user-agent
USER_AGENT = "blogcrawler/1.0 (+https://github.com/yourusername/welog) Educational Purpose"

# Obey robots.txt rules
ROBOTSTXT_OBEY = True

# Concurrency and throttling settings - Be respectful to dev.to
CONCURRENT_REQUESTS = 8
CONCURRENT_REQUESTS_PER_DOMAIN = 2
DOWNLOAD_DELAY = 2  # 2 seconds delay between requests
RANDOMIZE_DOWNLOAD_DELAY = 0.5  # Random delay (0.5 * to 1.5 * DOWNLOAD_DELAY)

# AutoThrottle extension settings
AUTOTHROTTLE_ENABLED = True
AUTOTHROTTLE_START_DELAY = 1
AUTOTHROTTLE_MAX_DELAY = 10
AUTOTHROTTLE_TARGET_CONCURRENCY = 2.0
AUTOTHROTTLE_DEBUG = False  # Enable to see throttling stats

# Memory usage optimization
REACTOR_THREADPOOL_MAXSIZE = 20

# Enable cookies (needed for some features)
COOKIES_ENABLED = True

# Disable Telnet Console (enabled by default)
#TELNETCONSOLE_ENABLED = False

# Override the default request headers:
#DEFAULT_REQUEST_HEADERS = {
#    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
#    "Accept-Language": "en",
#}

# Enable or disable spider middlewares
# See https://docs.scrapy.org/en/latest/topics/spider-middleware.html
#SPIDER_MIDDLEWARES = {
#    "blogcrawler.middlewares.BlogcrawlerSpiderMiddleware": 543,
#}

# Enable or disable downloader middlewares
# See https://docs.scrapy.org/en/latest/topics/downloader-middleware.html
#DOWNLOADER_MIDDLEWARES = {
#    "blogcrawler.middlewares.BlogcrawlerDownloaderMiddleware": 543,
#}

# Enable or disable extensions
# See https://docs.scrapy.org/en/latest/topics/extensions.html
#EXTENSIONS = {
#    "scrapy.extensions.telnet.TelnetConsole": None,
#}

# Configure item pipelines
# See https://docs.scrapy.org/en/latest/topics/item-pipeline.html
ITEM_PIPELINES = {
    "blogcrawler.pipelines.ValidationPipeline": 300,
    "blogcrawler.pipelines.DuplicatesPipeline": 400,
    # "blogcrawler.pipelines.DatabasePipeline": 500,
    "blogcrawler.pipelines.JsonExportPipeline": 600,
}

# Database configuration
DATABASE_CONFIG = {
    'host': 'localhost',
    'port': 5431,
    'database': 'postgres',
    'user': 'postgres',
    'password': 'mysecretpassword'  # Change this to your actual password
}

# Export settings
FEEDS = {
    'data/devto_posts_%(time)s.json': {
        'format': 'json',
        'encoding': 'utf8',
        'indent': 2,
    },
    'data/devto_posts_%(time)s.csv': {
        'format': 'csv',
        'encoding': 'utf8',
    }
}

# Logging
LOG_LEVEL = 'INFO'
LOG_FILE = 'scrapy.log'

# Enable and configure HTTP caching (disabled by default)
# See https://docs.scrapy.org/en/latest/topics/downloader-middleware.html#httpcache-middleware-settings
#HTTPCACHE_ENABLED = True
#HTTPCACHE_EXPIRATION_SECS = 0
#HTTPCACHE_DIR = "httpcache"
#HTTPCACHE_IGNORE_HTTP_CODES = []
#HTTPCACHE_STORAGE = "scrapy.extensions.httpcache.FilesystemCacheStorage"

# Set settings whose default value is deprecated to a future-proof value
FEED_EXPORT_ENCODING = "utf-8"
