#!/usr/bin/env python3
"""
Simple spider diagnostics without external dependencies
"""

import sys
import os
import re

def analyze_spider_code():
    """Analyze the spider code for common issues"""
    
    print("🔍 Analyzing spider code...")
    
    spider_file = os.path.join(os.path.dirname(__file__), 'blogcrawler', 'blogcrawler', 'spiders', 'postspider.py')
    
    if not os.path.exists(spider_file):
        print(f"❌ Spider file not found: {spider_file}")
        return False
    
    try:
        with open(spider_file, 'r', encoding='utf-8') as f:
            content = f.read()
            
        print(f"✅ Spider file found ({len(content)} characters)")
        
        # Check for common issues
        issues = []
        
        # Check CSS selectors
        if '..' in content:
            issues.append("Found double dots (..) in CSS selectors - this is invalid")
            
        # Check for malformed selectors
        css_selector_pattern = r'["\'][^"\']*\.\.[^"\']*["\']'
        malformed_selectors = re.findall(css_selector_pattern, content)
        if malformed_selectors:
            issues.append(f"Found malformed CSS selectors: {malformed_selectors}")
        
        # Check for missing comma in lists
        lines = content.split('\n')
        for i, line in enumerate(lines):
            if line.strip().endswith("'") and i + 1 < len(lines):
                next_line = lines[i + 1].strip()
                if next_line.startswith('#') and not line.strip().endswith(','):
                    issues.append(f"Missing comma at line {i + 1}: {line.strip()}")
        
        # Check for yield statements
        if 'yield Request' not in content:
            issues.append("No 'yield Request' statements found - spider may not be making requests")
            
        if 'yield item' not in content:
            issues.append("No 'yield item' statements found - spider may not be returning data")
        
        # Report issues
        if issues:
            print("❌ Found issues:")
            for issue in issues:
                print(f"  - {issue}")
            return False
        else:
            print("✅ No obvious issues found in spider code")
            return True
            
    except Exception as e:
        print(f"❌ Error analyzing spider: {e}")
        return False


def check_settings():
    """Check settings configuration"""
    
    print("\n🔍 Checking settings...")
    
    settings_file = os.path.join(os.path.dirname(__file__), 'blogcrawler', 'blogcrawler', 'settings.py')
    
    if not os.path.exists(settings_file):
        print(f"❌ Settings file not found: {settings_file}")
        return False
        
    try:
        with open(settings_file, 'r', encoding='utf-8') as f:
            content = f.read()
            
        print(f"✅ Settings file found")
        
        # Check key settings
        if 'ROBOTSTXT_OBEY = True' in content:
            print("✅ ROBOTSTXT_OBEY is enabled")
        else:
            print("⚠️  ROBOTSTXT_OBEY not found or disabled")
            
        if 'ITEM_PIPELINES' in content:
            print("✅ ITEM_PIPELINES configured")
        else:
            print("❌ ITEM_PIPELINES not configured")
            
        if 'DOWNLOAD_DELAY' in content:
            print("✅ DOWNLOAD_DELAY configured")
        else:
            print("⚠️  DOWNLOAD_DELAY not configured")
            
        return True
        
    except Exception as e:
        print(f"❌ Error checking settings: {e}")
        return False


def analyze_log_for_errors():
    """Analyze the scrapy log for specific error patterns"""
    
    print("\n🔍 Analyzing log file...")
    
    log_file = os.path.join(os.path.dirname(__file__), 'blogcrawler', 'scrapy.log')
    
    if not os.path.exists(log_file):
        print(f"❌ Log file not found: {log_file}")
        return False
        
    try:
        with open(log_file, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # Count errors
        error_lines = [line for line in content.split('\n') if 'ERROR' in line]
        info_lines = [line for line in content.split('\n') if 'INFO' in line]
        
        print(f"📊 Log analysis:")
        print(f"  - Total ERROR lines: {len(error_lines)}")
        print(f"  - Total INFO lines: {len(info_lines)}")
        
        # Look for specific error patterns
        css_errors = [line for line in error_lines if 'Expected ident, got <DELIM' in line]
        if css_errors:
            print(f"❌ Found {len(css_errors)} CSS parsing errors")
            print(f"  Sample error: {css_errors[0][:100]}...")
            
        # Look for successful parsing
        parsing_lines = [line for line in info_lines if 'Parsing post:' in line]
        print(f"📄 Attempted to parse {len(parsing_lines)} posts")
        
        # Look for items scraped
        item_lines = [line for line in content.split('\n') if 'scraped 0 items' in line]
        if item_lines:
            print(f"❌ Spider scraped 0 items - this confirms the issue")
            
        return len(css_errors) == 0
        
    except Exception as e:
        print(f"❌ Error analyzing log: {e}")
        return False


def provide_recommendations():
    """Provide recommendations based on analysis"""
    
    print("\n💡 Recommendations:")
    print("1. Install Scrapy: pip install scrapy")
    print("2. Fix the CSS selector syntax error (double dots)")
    print("3. Test with a single page first: scrapy crawl devto -a pages=1")
    print("4. Check robots.txt compliance")
    print("5. Verify dev.to website structure hasn't changed")
    
    print("\n🔧 Quick fixes:")
    print("1. CSS Selector fix:")
    print("   Change: '..crayons-article__header__meta .crayons-link.fw-bold::text'")
    print("   To:     '.crayons-article__header__meta .crayons-link.fw-bold::text'")
    
    print("\n2. Add debug logging to spider:")
    print("   self.logger.info(f'Found {len(post_urls)} URLs')")
    print("   self.logger.info(f'Processing item: {item}')")


if __name__ == "__main__":
    print("🛠️  Scrapy Spider Diagnostics")
    print("=" * 50)
    
    all_good = True
    
    all_good &= analyze_spider_code()
    all_good &= check_settings()
    all_good &= analyze_log_for_errors()
    
    provide_recommendations()
    
    print("\n" + "=" * 50)
    if all_good:
        print("🎉 Spider appears to be correctly configured!")
    else:
        print("⚠️  Issues found - see recommendations above")