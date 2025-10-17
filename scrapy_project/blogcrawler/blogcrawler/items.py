# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy
from itemloaders.processors import TakeFirst, MapCompose, Join
from w3lib.html import remove_tags
import re


def clean_text(text):
    """Clean and normalize text content"""
    if text:
        # Remove extra whitespace and newlines
        text = re.sub(r'\s+', ' ', text.strip())
        return text
    return None


def extract_number(text):
    """Extract numbers from text (for likes, comments, etc.)"""
    if text:
        numbers = re.findall(r'\d+', text)
        return int(numbers[0]) if numbers else 0
    return 0


class DevToPostItem(scrapy.Item):
    # Post identification
    post_id = scrapy.Field(
        output_processor=TakeFirst()
    )
    url = scrapy.Field(
        output_processor=TakeFirst()
    )
    slug = scrapy.Field(
        output_processor=TakeFirst()
    )
    
    # Post content
    title = scrapy.Field(
        input_processor=MapCompose(clean_text),
        output_processor=TakeFirst()
    )
    content = scrapy.Field(
        input_processor=MapCompose(remove_tags, clean_text),
        output_processor=Join('\n')
    )
    excerpt = scrapy.Field(
        input_processor=MapCompose(clean_text),
        output_processor=TakeFirst()
    )
    
    # Author information
    author_name = scrapy.Field(
        input_processor=MapCompose(clean_text),
        output_processor=TakeFirst()
    )
    author_username = scrapy.Field(
        input_processor=MapCompose(clean_text),
        output_processor=TakeFirst()
    )
    author_profile_url = scrapy.Field(
        output_processor=TakeFirst()
    )
    author_avatar = scrapy.Field(
        output_processor=TakeFirst()
    )
    
    # Post metadata
    published_at = scrapy.Field(
        output_processor=TakeFirst()
    )
    updated_at = scrapy.Field(
        output_processor=TakeFirst()
    )
    reading_time = scrapy.Field(
        input_processor=MapCompose(extract_number),
        output_processor=TakeFirst()
    )
    
    # Engagement metrics
    likes_count = scrapy.Field(
        input_processor=MapCompose(extract_number),
        output_processor=TakeFirst()
    )
    comments_count = scrapy.Field(
        input_processor=MapCompose(extract_number),
        output_processor=TakeFirst()
    )
    bookmarks_count = scrapy.Field(
        input_processor=MapCompose(extract_number),
        output_processor=TakeFirst()
    )
    
    # Post classification
    tags = scrapy.Field()  # List of tags
    cover_image = scrapy.Field(
        output_processor=TakeFirst()
    )
    
    # Technical metadata
    scraped_at = scrapy.Field(
        output_processor=TakeFirst()
    )
    source_website = scrapy.Field(
        output_processor=TakeFirst()
    )


class DevToAuthorItem(scrapy.Item):
    """Separate item for author information"""
    username = scrapy.Field(
        output_processor=TakeFirst()
    )
    name = scrapy.Field(
        input_processor=MapCompose(clean_text),
        output_processor=TakeFirst()
    )
    bio = scrapy.Field(
        input_processor=MapCompose(clean_text),
        output_processor=TakeFirst()
    )
    profile_url = scrapy.Field(
        output_processor=TakeFirst()
    )
    avatar_url = scrapy.Field(
        output_processor=TakeFirst()
    )
    followers_count = scrapy.Field(
        input_processor=MapCompose(extract_number),
        output_processor=TakeFirst()
    )
    posts_count = scrapy.Field(
        input_processor=MapCompose(extract_number),
        output_processor=TakeFirst()
    )
    scraped_at = scrapy.Field(
        output_processor=TakeFirst()
    )
