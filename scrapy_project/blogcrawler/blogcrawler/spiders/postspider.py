import scrapy
from scrapy import Request
from urllib.parse import urljoin, urlparse
import re
from datetime import datetime
from ..items import DevToPostItem
import logging


class DevToSpider(scrapy.Spider):
    name = "devto"
    allowed_domains = ["dev.to"]
    
    # Starting URLs - different feeds to get variety
    start_urls = [
        "https://dev.to",  # Latest posts
        "https://dev.to/top/week",  # Top weekly posts
        "https://dev.to/top/month",  # Top monthly posts
        "https://dev.to/latest",  # Latest posts
    ]
    
    # Custom settings for this spider
    custom_settings = {
        'DOWNLOAD_DELAY': 3,  # Be extra respectful to dev.to
        'CONCURRENT_REQUESTS_PER_DOMAIN': 1,
    }
    
    def __init__(self, pages=5, *args, **kwargs):
        super(DevToSpider, self).__init__(*args, **kwargs)
        self.max_pages = int(pages)  # Number of pages to crawl per feed
        self.scraped_urls = set()  # Track scraped URLs to avoid duplicates
        
    def start_requests(self):
        """Generate initial requests"""
        for url in self.start_urls:
            yield Request(
                url=url,
                callback=self.parse_feed,
                meta={'page': 1, 'feed_url': url}
            )
    
    def parse_feed(self, response):
        """Parse the main feed pages and extract post URLs"""
        current_page = response.meta.get('page', 1)
        feed_url = response.meta.get('feed_url')
        
        self.logger.info(f"Parsing feed: {feed_url}, Page: {current_page}")
        
        # Extract post URLs from the feed
        # Dev.to uses different selectors for different layouts
        post_selectors = [
            'article.crayons-story h2 a::attr(href)',  # Main feed
            'article h3 a::attr(href)',  # Alternative layout
            '.crayons-story__title a::attr(href)',  # Another variation
            'h2.crayons-story__title a::attr(href)',  # Direct selector
        ]
        
        post_urls = []
        for selector in post_selectors:
            urls = response.css(selector).getall()
            post_urls.extend(urls)
            if urls:  # If we found URLs with this selector, break
                break
        
        # Convert relative URLs to absolute
        post_urls = [urljoin(response.url, url) for url in post_urls if url]
        
        self.logger.info(f"Found {len(post_urls)} post URLs on page {current_page}")
        
        # Request individual posts
        for post_url in post_urls:
            if post_url not in self.scraped_urls:
                self.scraped_urls.add(post_url)
                yield Request(
                    url=post_url,
                    callback=self.parse_post,
                    meta={'dont_cache': True}
                )
        
        # Follow pagination if we haven't reached max pages
        if current_page < self.max_pages:
            # Look for next page button
            next_page_selectors = [
                'a[aria-label="Next page"]::attr(href)',
                '.next_page::attr(href)',
                'a[rel="next"]::attr(href)',
            ]
            
            next_page = None
            for selector in next_page_selectors:
                next_page = response.css(selector).get()
                if next_page:
                    break
            
            if next_page:
                next_url = urljoin(response.url, next_page)
                yield Request(
                    url=next_url,
                    callback=self.parse_feed,
                    meta={'page': current_page + 1, 'feed_url': feed_url}
                )
            else:
                # If no next button, try constructing URL with page parameter
                if '?' not in feed_url:
                    next_url = f"{feed_url}?page={current_page + 1}"
                else:
                    next_url = f"{feed_url}&page={current_page + 1}"
                
                yield Request(
                    url=next_url,
                    callback=self.parse_feed,
                    meta={'page': current_page + 1, 'feed_url': feed_url}
                )
    
    def parse_post(self, response):
        """Parse individual blog post"""
        self.logger.info(f"Parsing post: {response.url}")
        
        try:
            # Create item
            item = DevToPostItem()
            
            # Basic post information
            item['url'] = response.url
            item['slug'] = self.extract_slug(response.url)
            
            # Title - try multiple selectors
            title = (
                response.css('h1.crayons-article__header__title::text').get() or
                response.css('h1[data-article-title]::text').get() or
                response.css('h1.fs-3xl::text').get() or
                response.css('h1::text').get()
            )
            item['title'] = title.strip() if title else "No Title"
            
            # Content - main article body
            content_selectors = [
                '#article-body',
                '.crayons-article__main',
                '[data-article-body]',
                'div.spec__body',
            ]
            
            content = None
            for selector in content_selectors:
                content_element = response.css(selector)
                if content_element:
                    content = content_element.get()
                    break
            
            item['content'] = self.clean_html_content(content) if content else ""
            
            # Excerpt - try to get from meta or first paragraph
            excerpt = (
                response.css('meta[name="description"]::attr(content)').get() or
                response.css('.crayons-article__subheader::text').get()
            )
            if not excerpt and content:
                # Extract first paragraph as excerpt
                first_p = response.css('#article-body p::text').get()
                excerpt = first_p[:300] + "..." if first_p and len(first_p) > 300 else first_p
            
            item['excerpt'] = excerpt
            
            # Author information
            author_selectors = {
                'name': [
                    '.crayons-article__header__meta a[data-user-card-trigger-uid]::text',
                    '.profile-preview-card__content h2::text',
                    '[data-author-name]::text',
                    '.author a::text'
                ],
                'username': [
                    '.crayons-article__header__meta .crayons-link.fw-bold::text',
                    '.crayons-layout__sidebar-right .crayons-link.crayons-subtitle-2::text',
                ],
                'profile_url': [
                    '.crayons-article__header__meta a[data-user-card-trigger-uid]::attr(href)',
                    '.profile-preview-card__content a::attr(href)',
                ],
                'avatar': [
                    '.crayons-article__header__meta img::attr(src)',
                    '.profile-preview-card img::attr(src)',
                ]
            }
            
            for field, selectors in author_selectors.items():
                value = None
                for selector in selectors:
                    value = response.css(selector).get()
                    if value:
                        break
                
                if field == 'username' and value:
                    # Extract username from URL
                    # username = value.strip('/').split('/')[-1] if value else None
                    username = value.strip()
                    item['author_username'] = username
                elif field == 'profile_url' and value:
                    item['author_profile_url'] = urljoin(response.url, value)
                elif field == 'avatar' and value:
                    item['author_avatar'] = urljoin(response.url, value) if value.startswith('/') else value
                else:
                    item[f'author_{field}'] = value
            
            # Publication date
            pub_date = (
                response.css('time::attr(datetime)').get() or
                response.css('[datetime]::attr(datetime)').get()
            )
            item['published_at'] = pub_date
            item['updated_at'] = pub_date  # Assuming same for now
            
            # Reading time
            reading_time_text = response.css('.crayons-article__header__meta *:contains("min read")::text').get()
            if reading_time_text:
                reading_time = re.search(r'(\d+)', reading_time_text)
                item['reading_time'] = int(reading_time.group(1)) if reading_time else 0
            else:
                item['reading_time'] = 0
            
            # Engagement metrics
            # Reactions (likes)
            reactions = response.css('[data-reactions-count]::attr(data-reactions-count)').get()
            item['likes_count'] = int(reactions) if reactions and reactions.isdigit() else 0
            
            # Comments count
            comments = (
                response.css('[data-comments-count]::attr(data-comments-count)').get() or
                response.css('a[href*="#comments"] span::text').get()
            )
            item['comments_count'] = self.extract_number(comments) if comments else 0
            
            # Bookmarks (if available)
            item['bookmarks_count'] = 0  # Not easily accessible on dev.to
            
            # Tags
            tags = response.css('.spec__tags .crayons-tag::attr(href)').getall()
            item['tags'] = [tag[3:] for tag in tags if tag.strip()]
            
            # Cover image
            cover_image = (
                response.css('.crayons-article__cover__image img::attr(src)').get() or
                response.css('meta[property="og:image"]::attr(content)').get()
            )
            if cover_image and cover_image.startswith('/'):
                cover_image = urljoin(response.url, cover_image)
            item['cover_image'] = cover_image
            
            yield item
            
        except Exception as e:
            self.logger.error(f"Error parsing post {response.url}: {str(e)}")
    
    def extract_slug(self, url):
        """Extract slug from URL"""
        parsed = urlparse(url)
        path_parts = [part for part in parsed.path.split('/') if part]
        return path_parts[-1] if path_parts else ""
    
    def clean_html_content(self, html_content):
        """Basic HTML cleaning for content"""
        if not html_content:
            return ""
        
        # Remove script and style tags
        html_content = re.sub(r'<script[^>]*>.*?</script>', '', html_content, flags=re.DOTALL | re.IGNORECASE)
        html_content = re.sub(r'<style[^>]*>.*?</style>', '', html_content, flags=re.DOTALL | re.IGNORECASE)
        
        # You might want to keep some HTML formatting or convert to markdown
        # For now, we'll keep the HTML as is for the database
        return html_content.strip()
    
    def extract_number(self, text):
        """Extract first number from text"""
        if text:
            numbers = re.findall(r'\d+', str(text))
            return int(numbers[0]) if numbers else 0
        return 0
