#!/usr/bin/env python3
"""
Setup and run script for dev.to blog scraper
"""

import os
import sys
import subprocess
import json
from pathlib import Path


def check_python_version():
    """Check if Python version is compatible"""
    if sys.version_info < (3, 8):
        print("❌ Error: Python 3.8 or higher is required")
        sys.exit(1)
    print(f"✅ Python {sys.version.split()[0]} detected")


def install_requirements():
    """Install required packages"""
    print("📦 Installing requirements...")
    try:
        subprocess.run([sys.executable, "-m", "pip", "install", "-r", "requirements.txt"], 
                      check=True, capture_output=True, text=True)
        print("✅ Requirements installed successfully")
    except subprocess.CalledProcessError as e:
        print(f"❌ Error installing requirements: {e}")
        print("Please run: pip install -r requirements.txt")
        return False
    return True


def create_data_directory():
    """Create data directory for exports"""
    data_dir = Path("data")
    data_dir.mkdir(exist_ok=True)
    print(f"📁 Created data directory: {data_dir.absolute()}")


def setup_database_config():
    """Help user configure database settings"""
    print("\n🔧 Database Configuration")
    print("Please update the database settings in blogcrawler/settings.py")
    print("Default configuration:")
    print("  - Host: localhost")
    print("  - Port: 5431")
    print("  - Database: postgres")
    print("  - User: postgres")
    print("  - Password: (you need to set this)")
    
    config_file = Path("blogcrawler/settings.py")
    if config_file.exists():
        print(f"📝 Edit this file: {config_file.absolute()}")
    

def show_usage_examples():
    """Show how to run the spider"""
    print("\n🚀 Usage Examples:")
    print("="*50)
    
    examples = [
        {
            "description": "Basic crawl (5 pages per feed)",
            "command": "scrapy crawl devto"
        },
        {
            "description": "Crawl 10 pages per feed",
            "command": "scrapy crawl devto -a pages=10"
        },
        {
            "description": "Crawl with custom output file",
            "command": "scrapy crawl devto -o output.json"
        },
        {
            "description": "Crawl with detailed logging", 
            "command": "scrapy crawl devto -L DEBUG"
        },
        {
            "description": "Test specific URL",
            "command": 'scrapy shell "https://dev.to/some-post-url"'
        }
    ]
    
    for i, example in enumerate(examples, 1):
        print(f"{i}. {example['description']}")
        print(f"   {example['command']}")
        print()


def run_test_crawl():
    """Run a small test crawl"""
    print("🧪 Running test crawl...")
    try:
        result = subprocess.run([
            "scrapy", "crawl", "devto", 
            "-a", "pages=1",
            "-s", "LOG_LEVEL=INFO"
        ], capture_output=True, text=True, cwd="blogcrawler")
        
        if result.returncode == 0:
            print("✅ Test crawl completed successfully!")
        else:
            print("❌ Test crawl failed:")
            print(result.stderr)
            
    except FileNotFoundError:
        print("❌ Scrapy not found. Make sure it's installed correctly.")


def main():
    """Main setup function"""
    print("🕷️  Dev.to Blog Scraper Setup")
    print("="*40)
    
    # Check Python version
    check_python_version()
    
    # Create necessary directories
    create_data_directory()
    
    # Install requirements
    if not install_requirements():
        return
    
    # Database configuration guidance
    setup_database_config()
    
    # Show usage examples
    show_usage_examples()
    
    # Ask if user wants to run test
    print("🤔 Would you like to run a test crawl? (y/N): ", end="")
    response = input().strip().lower()
    
    if response in ['y', 'yes']:
        run_test_crawl()
    
    print("\n🎉 Setup complete! Happy scraping!")
    print("📖 Don't forget to:")
    print("   1. Update database credentials in settings.py")
    print("   2. Ensure PostgreSQL is running")
    print("   3. Be respectful to dev.to's servers")


if __name__ == "__main__":
    main()