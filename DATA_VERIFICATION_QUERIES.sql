-- 📊 Data Import Verification Queries
-- Run these queries to verify your imported data and explore the results

-- ===== BASIC COUNTS =====
-- Total posts in database
SELECT COUNT(*) as total_posts FROM posts WHERE deleted_at IS NULL;

-- Total imported posts (from dev.to authors)
SELECT COUNT(*) as imported_posts FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' AND p.deleted_at IS NULL;

-- Total authors created during import
SELECT COUNT(*) as imported_authors FROM users 
WHERE email LIKE '%@devto.imported.local' AND deleted_at IS NULL;

-- ===== SAMPLE DATA EXPLORATION =====
-- Show sample imported posts
SELECT 
    p.title,
    u.name as author_name,
    p.created_at,
    LENGTH(p.content) as content_length
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' 
  AND p.deleted_at IS NULL
ORDER BY p.created_at DESC
LIMIT 10;

-- ===== AUTHOR ANALYSIS =====
-- Top authors by post count
SELECT 
    u.name as author_name,
    COUNT(p.id) as post_count,
    u.email
FROM users u
JOIN posts p ON u.id = p.author_id
WHERE u.email LIKE '%@devto.imported.local' 
  AND u.deleted_at IS NULL 
  AND p.deleted_at IS NULL
GROUP BY u.id, u.name, u.email
ORDER BY post_count DESC
LIMIT 10;

-- ===== CONTENT ANALYSIS =====
-- Posts by content length
SELECT 
    CASE 
        WHEN LENGTH(content) < 1000 THEN 'Short (< 1K chars)'
        WHEN LENGTH(content) < 5000 THEN 'Medium (1-5K chars)'  
        WHEN LENGTH(content) < 10000 THEN 'Long (5-10K chars)'
        ELSE 'Very Long (10K+ chars)'
    END as content_category,
    COUNT(*) as post_count,
    AVG(LENGTH(content)) as avg_length
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' AND p.deleted_at IS NULL
GROUP BY content_category
ORDER BY avg_length;

-- ===== POPULAR TOPICS =====
-- Most common words in titles (simplified analysis)
SELECT 
    word,
    COUNT(*) as frequency
FROM (
    SELECT 
        TRIM(LOWER(
            REGEXP_REPLACE(
                REGEXP_REPLACE(p.title, '[^a-zA-Z0-9\s]', ' ', 'g'),
                '\s+', ' ', 'g'
            )
        )) as cleaned_title
    FROM posts p
    JOIN users u ON p.author_id = u.id 
    WHERE u.email LIKE '%@devto.imported.local' AND p.deleted_at IS NULL
) t,
LATERAL (
    SELECT TRIM(word) as word
    FROM unnest(string_to_array(cleaned_title, ' ')) as word
    WHERE LENGTH(TRIM(word)) > 3  -- Only words longer than 3 characters
) words
WHERE word NOT IN ('with', 'from', 'your', 'this', 'that', 'they', 'them', 'their', 'there', 'what', 'when', 'where', 'will', 'would', 'could', 'should')
GROUP BY word
HAVING COUNT(*) >= 2  -- Only show words that appear at least twice
ORDER BY frequency DESC, word
LIMIT 20;

-- ===== DATABASE PERFORMANCE =====
-- Check if posts have proper indexes
SELECT 
    indexname,
    indexdef
FROM pg_indexes 
WHERE tablename = 'posts';

-- ===== DATA QUALITY CHECKS =====
-- Posts without excerpts
SELECT COUNT(*) as posts_without_excerpts
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' 
  AND (p.excerpt IS NULL OR LENGTH(TRIM(p.excerpt)) = 0)
  AND p.deleted_at IS NULL;

-- Posts without titles (should be 0)
SELECT COUNT(*) as posts_without_titles
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' 
  AND (p.title IS NULL OR LENGTH(TRIM(p.title)) = 0)
  AND p.deleted_at IS NULL;

-- ===== SEARCH TESTING QUERIES =====
-- Test your search functionality with these
SELECT title, excerpt 
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' 
  AND p.title ILIKE '%javascript%' 
  AND p.deleted_at IS NULL
LIMIT 5;

SELECT title, excerpt 
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' 
  AND p.title ILIKE '%react%' 
  AND p.deleted_at IS NULL
LIMIT 5;

SELECT title, excerpt 
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE u.email LIKE '%@devto.imported.local' 
  AND (p.title ILIKE '%AI%' OR p.title ILIKE '%artificial intelligence%')
  AND p.deleted_at IS NULL
LIMIT 5;

-- ===== PAGINATION TESTING =====
-- Test pagination with your new large dataset
SELECT 
    p.title,
    u.name as author,
    p.created_at
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE p.deleted_at IS NULL
ORDER BY p.created_at DESC
LIMIT 20 OFFSET 0;  -- First page

SELECT 
    p.title,
    u.name as author,
    p.created_at
FROM posts p
JOIN users u ON p.author_id = u.id 
WHERE p.deleted_at IS NULL
ORDER BY p.created_at DESC
LIMIT 20 OFFSET 20;  -- Second page