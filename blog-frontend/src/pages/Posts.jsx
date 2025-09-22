import React, { useState, useEffect, useCallback } from "react";
import { useSearchParams } from "react-router-dom";
import { postService } from "../services/postService";
import PostCard from "../components/posts/PostCard";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { Search, Filter, Grid, List } from "lucide-react";

const Posts = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [viewMode, setViewMode] = useState("grid"); // "grid" or "list"
  const [searchParams] = useSearchParams();
  const searchQuery = searchParams.get("search");

  const fetchPosts = useCallback(
    async (pageNum = 0, reset = false) => {
      try {
        setLoading(true);
        let response;

        if (searchQuery) {
          response = await postService.searchPosts(searchQuery, pageNum, 12);
        } else {
          response = await postService.getAll(pageNum, 12);
        }

        const newPosts = response.data.content || response.data;

        if (reset) {
          setPosts(newPosts);
        } else {
          setPosts((prev) => [...prev, ...newPosts]);
        }

        setHasMore(newPosts.length === 12);
        setPage(pageNum);
      } catch (err) {
        setError("Failed to fetch posts");
        console.error("Error fetching posts:", err);
      } finally {
        setLoading(false);
      }
    },
    [searchQuery]
  );

  // Fetch posts when component mounts or search query changes
  useEffect(() => {
    fetchPosts(0, true);
  }, [searchQuery, fetchPosts]);

  const loadMore = () => {
    if (!loading && hasMore) {
      fetchPosts(page + 1, false);
    }
  };

  if (loading && posts.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                {searchQuery ? `Search Results` : "All Posts"}
              </h1>
              {searchQuery && (
                <p className="text-gray-600 mt-2">
                  Showing results for "{searchQuery}" ({posts.length} posts
                  found)
                </p>
              )}
            </div>

            {/* View Mode Toggle */}
            <div className="flex items-center space-x-2 bg-white rounded-lg p-1 shadow-sm">
              <button
                onClick={() => setViewMode("grid")}
                className={`p-2 rounded transition-colors ${
                  viewMode === "grid"
                    ? "bg-primary-100 text-primary-600"
                    : "text-gray-500 hover:text-gray-700"
                }`}
                title="Grid view"
              >
                <Grid size={18} />
              </button>
              <button
                onClick={() => setViewMode("list")}
                className={`p-2 rounded transition-colors ${
                  viewMode === "list"
                    ? "bg-primary-100 text-primary-600"
                    : "text-gray-500 hover:text-gray-700"
                }`}
                title="List view"
              >
                <List size={18} />
              </button>
            </div>
          </div>
        </div>

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-700">{error}</p>
          </div>
        )}

        {/* Posts Grid/List */}
        {posts.length > 0 ? (
          <>
            <div
              className={
                viewMode === "grid"
                  ? "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
                  : "space-y-6"
              }
            >
              {posts.map((post) => (
                <PostCard key={post.id} post={post} viewMode={viewMode} />
              ))}
            </div>

            {/* Load More Button */}
            {hasMore && (
              <div className="mt-12 text-center">
                <button
                  onClick={loadMore}
                  disabled={loading}
                  className="px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 font-medium"
                >
                  {loading ? "Loading..." : "Load More Posts"}
                </button>
              </div>
            )}
          </>
        ) : (
          /* Empty State */
          <div className="text-center py-16">
            <Search size={64} className="mx-auto text-gray-300 mb-4" />
            <h3 className="text-xl font-medium text-gray-900 mb-2">
              {searchQuery ? "No posts found" : "No posts available"}
            </h3>
            <p className="text-gray-500 mb-6">
              {searchQuery
                ? `We couldn't find any posts matching "${searchQuery}". Try different keywords.`
                : "Be the first to create a post!"}
            </p>

            {!searchQuery && (
              <a
                href="/create-post"
                className="inline-flex items-center px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-all duration-200 font-medium"
              >
                Create Your First Post
              </a>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Posts;
