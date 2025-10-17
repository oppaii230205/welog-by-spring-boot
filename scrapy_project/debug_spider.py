#!/usr/bin/env python3
"""
Debug script to test dev.to spider configuration and CSS selectors
"""

import requests
from urllib.parse import urljoin
import sys
import os

# Add the parent directory to path so we can import the spider
sys.path.append(os.path.join(os.path.dirname(__file__), '..', 'blogcrawler'))

def test_dev_to_selectors():
    """Test CSS selectors against actual dev.to pages"""
    
    print("🔍 Testing dev.to CSS selectors...")
    
    # Test URLs
    test_urls = [
        "https://dev.to",
        "https://dev.to/latest"
    ]
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    
    for url in test_urls:
        print(f"\n📄 Testing URL: {url}")
        
        try:
            response = requests.get(url, headers=headers, timeout=10)
            response.raise_for_status()
            
            html_content = response.text
            print(f"✅ Successfully fetched {len(html_content)} characters")
            
            # Test for post links
            post_selectors = [
                'article.crayons-story h2 a',
                'article h3 a', 
                '.crayons-story__title a',
                'h2.crayons-story__title a',
            ]
            
            found_posts = False
            for selector in post_selectors:
                # Simple check - look for the class/tag patterns in HTML
                if 'crayons-story' in html_content and 'href=' in html_content:
                    found_posts = True
                    print(f"✅ Found potential post links (pattern: {selector})")
                    break
            
            if not found_posts:
                print("❌ No post links found with current selectors")
                # Show some sample HTML structure
                print("\n📋 Sample HTML structure:")
                lines = html_content.split('\n')
                for i, line in enumerate(lines[:50]):
                    if 'article' in line.lower() or 'story' in line.lower():
                        print(f"Line {i}: {line.strip()[:100]}...")
                        
        except requests.RequestException as e:
            print(f"❌ Error fetching {url}: {e}")
            
        except Exception as e:
            print(f"❌ Unexpected error: {e}")


def test_individual_post():
    """Test parsing a single dev.to post"""
    
    print("\n🔍 Testing individual post parsing...")
    
    # Get a sample post URL from the main feed
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
    }
    
    try:
        # Get main page first
        response = requests.get("https://dev.to/latest", headers=headers, timeout=10)
        response.raise_for_status()
        
        html = response.text
        
        # Look for first post URL (simple text search)
        import re
        post_urls = re.findall(r'href="(/[^/]+/[^"]+)"', html)
        
        if post_urls:
            post_url = "https://dev.to" + post_urls[0]
            print(f"📄 Testing post URL: {post_url}")
            
            post_response = requests.get(post_url, headers=headers, timeout=10)
            post_response.raise_for_status()
            
            post_html = post_response.text
            print(f"✅ Successfully fetched post ({len(post_html)} characters)")
            
            # Test key selectors
            selectors_to_test = {
                'title': ['h1.crayons-article__header__title', 'h1[data-article-title]', 'h1.fs-3xl', 'h1'],
                'content': ['#article-body', '.crayons-article__main', '[data-article-body]'],
                'author': ['.crayons-article__header__meta a[data-user-card-trigger-uid]']
            }
            
            for field, selectors in selectors_to_test.items():
                found = False
                for selector in selectors:
                    # Simple check for key parts of selector
                    selector_parts = selector.replace('[', ' ').replace(']', ' ').replace('#', ' ').replace('.', ' ').split()
                    if any(part in post_html for part in selector_parts if len(part) > 2):
                        print(f"✅ {field.title()} selector likely working: {selector}")
                        found = True
                        break
                        
                if not found:
                    print(f"❌ {field.title()} selectors may need updating")
        else:
            print("❌ No post URLs found in main feed")
            
    except Exception as e:
        print(f"❌ Error testing individual post: {e}")


def validate_spider_configuration():
    """Validate spider configuration without running it"""
    
    print("\n🔍 Validating spider configuration...")
    
    try:
        # Import and check spider
        from blogcrawler.spiders.postspider import DevToSpider
        
        spider = DevToSpider()
        
        print(f"✅ Spider name: {spider.name}")
        print(f"✅ Allowed domains: {spider.allowed_domains}")
        print(f"✅ Start URLs: {len(spider.start_urls)}")
        
        # Check if all required methods exist
        required_methods = ['start_requests', 'parse_feed', 'parse_post']
        for method in required_methods:
            if hasattr(spider, method):
                print(f"✅ Method {method} exists")
            else:
                print(f"❌ Method {method} missing")
                
        return True
        
    except ImportError as e:
        print(f"❌ Cannot import spider: {e}")
        return False
    except Exception as e:
        print(f"❌ Error validating spider: {e}")
        return False


if __name__ == "__main__":
    print("🚀 Dev.to Spider Debug Tool")
    print("=" * 50)
    
    # Run tests
    validate_spider_configuration()
    test_dev_to_selectors()
    test_individual_post()
    
    print("\n" + "=" * 50)
    print("🏁 Debug complete!")