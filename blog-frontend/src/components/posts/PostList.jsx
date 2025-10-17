import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { postService } from "../../services/postService";
import { likeService } from "../../services/likeService";
import PostCard from "./PostCard";
import LoadingSpinner from "../common/LoadingSpinner";
import {
  BookOpen,
  Calendar,
  TrendingUp,
  Star,
  Clock,
  Eye,
  Heart,
  MessageCircle,
  ArrowRight,
} from "lucide-react";
import API_URL from "../../config";

const PostList = ({ limit = 6, showFeatured = true }) => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await postService.getAll(0, limit + 3);

      // Fetch likes for each post
      const postsWithLikes = await Promise.all(
        response.data.map(async (post) => {
          try {
            const likesResponse = await likeService.getPostLikes(post.id);
            return { ...post, likesCount: likesResponse.data.length };
          } catch {
            return { ...post, likesCount: 0 };
          }
        })
      );

      setPosts(postsWithLikes.slice(0, limit));
    } catch (err) {
      setError("Failed to fetch posts");
      console.error("Error fetching posts:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error)
    return <div className="text-center text-red-600 py-8">{error}</div>;

  if (posts.length === 0) {
    return (
      <div className="col-span-full text-center py-12">
        <div className="inline-flex items-center justify-center w-16 h-16 bg-gray-100 rounded-full mb-4">
          <BookOpen className="w-8 h-8 text-gray-400" />
        </div>
        <p className="text-gray-500 text-lg">No posts found</p>
      </div>
    );
  }

  // Featured post (first post)
  const featuredPost = posts[0];
  const regularPosts = posts.slice(1);

  return (
    <div className="space-y-8">
      {/* Featured Post - Hero Layout */}
      {showFeatured && featuredPost && (
        <div className="group relative bg-white rounded-3xl shadow-xl overflow-hidden hover:shadow-2xl transition-all duration-500 border border-gray-100">
          <div className="grid md:grid-cols-2 gap-0">
            {/* Image Section */}
            <Link
              to={`/posts/${featuredPost.id}`}
              className="relative overflow-hidden h-full min-h-[400px]"
            >
              {featuredPost.coverImage ? (
                <img
                  src={
                    featuredPost.coverImage.includes("http")
                      ? featuredPost.coverImage
                      : `${API_URL}/img/posts/${featuredPost.coverImage}`
                  }
                  alt={featuredPost.title}
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700"
                />
              ) : (
                <div className="w-full h-full bg-gradient-to-br from-primary-500 via-purple-500 to-pink-500 flex items-center justify-center">
                  <BookOpen className="w-24 h-24 text-white/30" />
                </div>
              )}
              {/* Overlay Gradient */}
              <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/20 to-transparent md:bg-gradient-to-r"></div>

              {/* Featured Badge */}
              <div className="absolute top-6 left-6 z-10">
                <div className="flex items-center space-x-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white px-4 py-2 rounded-full shadow-lg backdrop-blur-sm">
                  <Star className="w-4 h-4 fill-current" />
                  <span className="text-sm font-bold">Featured</span>
                </div>
              </div>
            </Link>

            {/* Content Section */}
            <div className="p-8 md:p-12 flex flex-col justify-center">
              {/* Category/Tags */}
              <div className="flex flex-wrap gap-2 mb-4">
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-primary-100 text-primary-700">
                  <TrendingUp className="w-3 h-3 mr-1" />
                  Trending
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-700">
                  Latest
                </span>
              </div>

              {/* Title */}
              <Link to={`/posts/${featuredPost.id}`}>
                <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4 line-clamp-3 group-hover:text-primary-600 transition-colors duration-300">
                  {featuredPost.title}
                </h2>
              </Link>

              {/* Excerpt */}
              <p className="text-gray-600 text-lg mb-6 line-clamp-3 leading-relaxed">
                {featuredPost.excerpt ||
                  featuredPost.content?.substring(0, 180)}
                ...
              </p>

              {/* Author & Metadata */}
              <div className="flex flex-wrap items-center gap-6 mb-6 text-sm text-gray-500">
                {/* Author */}
                <div className="flex items-center space-x-3">
                  {featuredPost.author?.photo ? (
                    <img
                      src={`${API_URL}/img/users/${featuredPost.author.photo}`}
                      alt={featuredPost.author.name}
                      className="w-10 h-10 rounded-full object-cover ring-2 ring-white shadow"
                    />
                  ) : (
                    <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-semibold text-sm ring-2 ring-white shadow">
                      {featuredPost.author?.name?.charAt(0).toUpperCase()}
                    </div>
                  )}
                  <div>
                    <p className="font-medium text-gray-900">
                      {featuredPost.author?.name}
                    </p>
                    <p className="text-xs text-gray-500">Author</p>
                  </div>
                </div>

                {/* Stats */}
                <div className="flex items-center space-x-4">
                  <div className="flex items-center space-x-1 text-red-500">
                    <Heart className="w-4 h-4 fill-current" />
                    <span className="font-semibold">
                      {featuredPost.likesCount || 0}
                    </span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <Calendar className="w-4 h-4" />
                    <span>
                      {new Date(featuredPost.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>

              {/* Read More Button */}
              <Link
                to={`/posts/${featuredPost.id}`}
                className="inline-flex items-center text-primary-600 hover:text-primary-700 font-semibold group/btn"
              >
                <span>Read Full Article</span>
                <ArrowRight className="w-5 h-5 ml-2 group-hover/btn:translate-x-1 transition-transform" />
              </Link>
            </div>
          </div>
        </div>
      )}

      {/* Regular Posts Grid */}
      {regularPosts.length > 0 && (
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          {regularPosts.map((post, index) => (
            <div
              key={post.id}
              className="group bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-2xl transition-all duration-500 hover:-translate-y-2 border border-gray-100 animate-fade-in-up"
              style={{ animationDelay: `${index * 100}ms` }}
            >
              {/* Image */}
              <Link
                to={`/posts/${post.id}`}
                className="block relative overflow-hidden"
              >
                {post.coverImage ? (
                  <img
                    src={
                      post.coverImage.includes("http")
                        ? post.coverImage
                        : `${API_URL}/img/posts/${post.coverImage}`
                    }
                    alt={post.title}
                    className="w-full h-56 object-cover group-hover:scale-110 transition-transform duration-700"
                  />
                ) : (
                  <div className="w-full h-56 bg-gradient-to-br from-primary-400 via-purple-400 to-pink-400 flex items-center justify-center">
                    <BookOpen className="w-16 h-16 text-white/30" />
                  </div>
                )}
                {/* Gradient Overlay */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>

                {/* Read Time Badge */}
                <div className="absolute top-4 right-4">
                  <div className="flex items-center space-x-1 bg-white/95 backdrop-blur-sm px-3 py-1.5 rounded-full shadow-lg">
                    <Clock className="w-3.5 h-3.5 text-gray-600" />
                    <span className="text-xs font-medium text-gray-700">
                      5 min read
                    </span>
                  </div>
                </div>
              </Link>

              {/* Content */}
              <div className="p-6">
                {/* Title */}
                <Link to={`/posts/${post.id}`}>
                  <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-2 group-hover:text-primary-600 transition-colors duration-300 leading-tight">
                    {post.title}
                  </h3>
                </Link>

                {/* Excerpt */}
                <p className="text-gray-600 text-sm mb-4 line-clamp-3 leading-relaxed">
                  {post.excerpt || post.content?.substring(0, 120)}...
                </p>

                {/* Divider */}
                <div className="border-t border-gray-100 pt-4 mt-4">
                  {/* Author & Stats */}
                  <div className="flex items-center justify-between">
                    {/* Author */}
                    <div className="flex items-center space-x-2">
                      {post.author?.photo ? (
                        <img
                          src={`${API_URL}/img/users/${post.author.photo}`}
                          alt={post.author.name}
                          className="w-8 h-8 rounded-full object-cover ring-2 ring-gray-100"
                        />
                      ) : (
                        <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-semibold text-xs ring-2 ring-gray-100">
                          {post.author?.name?.charAt(0).toUpperCase()}
                        </div>
                      )}
                      <div>
                        <p className="text-sm font-medium text-gray-900">
                          {post.author?.name}
                        </p>
                        <p className="text-xs text-gray-500">
                          {new Date(post.createdAt).toLocaleDateString(
                            "en-US",
                            {
                              month: "short",
                              day: "numeric",
                            }
                          )}
                        </p>
                      </div>
                    </div>

                    {/* Like Count */}
                    <div className="flex items-center space-x-3 text-gray-500 text-sm">
                      <div className="flex items-center space-x-1 hover:text-red-500 transition-colors">
                        <Heart className="w-4 h-4" />
                        <span className="font-semibold">
                          {post.likesCount || 0}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Empty State for Single Post */}
      {!showFeatured && posts.length === 1 && (
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          <PostCard post={posts[0]} />
        </div>
      )}
    </div>
  );
};

export default PostList;
