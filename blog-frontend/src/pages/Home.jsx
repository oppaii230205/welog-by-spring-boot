import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import PostList from "../components/posts/PostList";
import { useAuth } from "../context/AuthContext";
import {
  ArrowRight,
  BookOpen,
  Users,
  TrendingUp,
  Sparkles,
  Edit3,
  Heart,
  MessageSquare,
  Eye,
  Clock,
  Award,
  Zap,
} from "lucide-react";
import { postService } from "../services/postService";
import { likeService } from "../services/likeService";
import API_URL from "../config";

const Home = () => {
  const { isAuthenticated } = useAuth();
  const [stats, setStats] = useState({
    totalPosts: 0,
    totalAuthors: 0,
    totalLikes: 0,
  });
  const [trendingPosts, setTrendingPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch posts to calculate stats
        const postsResponse = await postService.getAll(0, 200);
        const posts = await Promise.all(
          postsResponse.data.map(async (post) => {
            const likesResponse = await likeService.getPostLikes(post.id);
            return { ...post, likesCount: likesResponse.data.length };
          })
        );

        // Calculate stats
        const uniqueAuthors = new Set(posts.map((p) => p.author?.id)).size;
        const totalLikes = posts.reduce(
          (sum, p) => sum + (p.likesCount || 0),
          0
        );

        setStats({
          totalPosts: posts.length,
          totalAuthors: uniqueAuthors,
          totalLikes: totalLikes,
        });

        // Get trending posts (posts with most likes)
        const trending = posts
          .sort((a, b) => (b.likesCount || 0) - (a.likesCount || 0))
          .slice(0, 3);
        setTrendingPosts(trending);
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-white">
      {/* Hero Section with Animated Gradient */}
      <section className="relative overflow-hidden bg-gradient-to-br from-primary-600 via-primary-700 to-purple-700 text-white">
        {/* Animated Background Elements */}
        <div className="absolute inset-0 overflow-hidden">
          <div className="absolute -top-40 -right-40 w-80 h-80 bg-white/10 rounded-full blur-3xl animate-pulse"></div>
          <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-500/20 rounded-full blur-3xl animate-pulse delay-1000"></div>
          <div className="absolute top-1/2 left-1/2 w-96 h-96 bg-blue-400/10 rounded-full blur-3xl animate-pulse delay-2000"></div>
        </div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32">
          <div className="text-center">
            {/* Badge */}
            <div className="inline-flex items-center space-x-2 bg-white/20 backdrop-blur-sm px-4 py-2 rounded-full mb-8 animate-fade-in">
              <Sparkles className="w-4 h-4" />
              <span className="text-sm font-medium">
                Your Creative Writing Hub
              </span>
            </div>

            {/* Main Heading */}
            <h1 className="text-5xl md:text-7xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-100 animate-fade-in-up">
              Welcome to Welog
            </h1>
            <p className="text-xl md:text-2xl mb-4 text-blue-100 max-w-3xl mx-auto animate-fade-in-up delay-100">
              Share your thoughts, stories, and ideas with a vibrant community
            </p>
            <p className="text-base md:text-lg mb-12 text-blue-200 max-w-2xl mx-auto animate-fade-in-up delay-200">
              Join thousands of writers, developers, and creators sharing their
              knowledge
            </p>

            {/* CTA Buttons */}
            {!isAuthenticated ? (
              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center animate-fade-in-up delay-300">
                <Link
                  to="/register"
                  className="group relative bg-white text-primary-600 px-8 py-4 rounded-xl font-semibold hover:bg-blue-50 transition-all duration-300 shadow-xl hover:shadow-2xl hover:scale-105 inline-flex items-center"
                >
                  <Edit3 className="w-5 h-5 mr-2 group-hover:rotate-12 transition-transform" />
                  Start Writing
                  <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
                </Link>
                <Link
                  to="/posts"
                  className="group border-2 border-white/80 backdrop-blur-sm text-white px-8 py-4 rounded-xl font-semibold hover:bg-white hover:text-primary-600 transition-all duration-300 inline-flex items-center"
                >
                  <BookOpen className="w-5 h-5 mr-2" />
                  Explore Posts
                  <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
                </Link>
              </div>
            ) : (
              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center animate-fade-in-up delay-300">
                <Link
                  to="/create-post"
                  className="group relative bg-white text-primary-600 px-8 py-4 rounded-xl font-semibold hover:bg-blue-50 transition-all duration-300 shadow-xl hover:shadow-2xl hover:scale-105 inline-flex items-center"
                >
                  <Edit3 className="w-5 h-5 mr-2 group-hover:rotate-12 transition-transform" />
                  Create New Post
                  <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
                </Link>
                <Link
                  to="/posts"
                  className="group border-2 border-white/80 backdrop-blur-sm text-white px-8 py-4 rounded-xl font-semibold hover:bg-white hover:text-primary-600 transition-all duration-300 inline-flex items-center"
                >
                  <BookOpen className="w-5 h-5 mr-2" />
                  Browse All Posts
                </Link>
              </div>
            )}
          </div>
        </div>

        {/* Wave Separator */}
        <div className="absolute bottom-0 left-0 right-0">
          <svg
            viewBox="0 0 1440 120"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className="w-full h-auto"
          >
            <path
              d="M0 0L60 10C120 20 240 40 360 46.7C480 53 600 47 720 43.3C840 40 960 40 1080 46.7C1200 53 1320 67 1380 73.3L1440 80V120H1380C1320 120 1200 120 1080 120C960 120 840 120 720 120C600 120 480 120 360 120C240 120 120 120 60 120H0V0Z"
              fill="rgb(249, 250, 251)"
            />
          </svg>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-12 -mt-1">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Total Posts */}
            <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 hover:shadow-xl transition-all duration-300 hover:-translate-y-1 group">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-gradient-to-br from-blue-500 to-primary-600 rounded-xl group-hover:scale-110 transition-transform">
                  <BookOpen className="w-6 h-6 text-white" />
                </div>
                <div className="flex items-center text-green-600 text-sm font-medium">
                  <TrendingUp className="w-4 h-4 mr-1" />
                  <span>Live</span>
                </div>
              </div>
              <h3 className="text-gray-600 text-sm font-medium mb-2">
                Total Posts
              </h3>
              <p className="text-4xl font-bold text-gray-900 mb-1">
                {loading ? "..." : stats.totalPosts.toLocaleString()}
              </p>
              <p className="text-gray-500 text-sm">Published articles</p>
            </div>

            {/* Total Authors */}
            <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 hover:shadow-xl transition-all duration-300 hover:-translate-y-1 group">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-gradient-to-br from-purple-500 to-pink-600 rounded-xl group-hover:scale-110 transition-transform">
                  <Users className="w-6 h-6 text-white" />
                </div>
                <div className="flex items-center text-green-600 text-sm font-medium">
                  <TrendingUp className="w-4 h-4 mr-1" />
                  <span>Growing</span>
                </div>
              </div>
              <h3 className="text-gray-600 text-sm font-medium mb-2">
                Active Authors
              </h3>
              <p className="text-4xl font-bold text-gray-900 mb-1">
                {loading ? "..." : stats.totalAuthors.toLocaleString()}
              </p>
              <p className="text-gray-500 text-sm">Content creators</p>
            </div>

            {/* Total Likes */}
            <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 hover:shadow-xl transition-all duration-300 hover:-translate-y-1 group">
              <div className="flex items-center justify-between mb-4">
                <div className="p-3 bg-gradient-to-br from-red-500 to-pink-600 rounded-xl group-hover:scale-110 transition-transform">
                  <Heart className="w-6 h-6 text-white" />
                </div>
                <div className="flex items-center text-green-600 text-sm font-medium">
                  <TrendingUp className="w-4 h-4 mr-1" />
                  <span>+12%</span>
                </div>
              </div>
              <h3 className="text-gray-600 text-sm font-medium mb-2">
                Total Reactions
              </h3>
              <p className="text-4xl font-bold text-gray-900 mb-1">
                {loading ? "..." : stats.totalLikes.toLocaleString()}
              </p>
              <p className="text-gray-500 text-sm">Community engagement</p>
            </div>
          </div>
        </div>
      </section>

      {/* Trending Posts Section */}
      {trendingPosts.length > 0 && (
        <section className="py-16 bg-gradient-to-b from-white to-gray-50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-12">
              <div className="inline-flex items-center space-x-2 bg-gradient-to-r from-orange-500 to-red-500 text-white px-4 py-2 rounded-full mb-4">
                <Zap className="w-4 h-4" />
                <span className="text-sm font-semibold">Trending Now</span>
              </div>
              <h2 className="text-4xl font-bold text-gray-900 mb-4">
                Most Popular Posts
              </h2>
              <p className="text-gray-600 text-lg max-w-2xl mx-auto">
                Discover what the community is loving right now
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              {trendingPosts.map((post, index) => (
                <div
                  key={post.id}
                  className="group bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border border-gray-100"
                >
                  {/* Rank Badge */}
                  <div className="absolute top-4 left-4 z-10">
                    <div className="bg-gradient-to-r from-yellow-400 to-orange-500 text-white w-12 h-12 rounded-full flex items-center justify-center font-bold text-lg shadow-lg">
                      #{index + 1}
                    </div>
                  </div>

                  {/* Cover Image */}
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
                        className="w-full h-48 object-cover group-hover:scale-110 transition-transform duration-500"
                      />
                    ) : (
                      <div className="w-full h-48 bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center">
                        <BookOpen className="w-16 h-16 text-white/50" />
                      </div>
                    )}
                    <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                  </Link>

                  {/* Content */}
                  <div className="p-6">
                    <Link to={`/posts/${post.id}`}>
                      <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-2 group-hover:text-primary-600 transition-colors">
                        {post.title}
                      </h3>
                    </Link>

                    <p className="text-gray-600 text-sm mb-4 line-clamp-2">
                      {post.excerpt || post.content?.substring(0, 100)}...
                    </p>

                    {/* Author */}
                    <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                      <div className="flex items-center space-x-2">
                        {post.author?.photo ? (
                          <img
                            src={`${API_URL}/img/users/${post.author.photo}`}
                            alt={post.author.name}
                            className="w-8 h-8 rounded-full object-cover"
                          />
                        ) : (
                          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-white font-semibold text-sm">
                            {post.author?.name?.charAt(0).toUpperCase()}
                          </div>
                        )}
                        <span className="text-sm font-medium text-gray-700">
                          {post.author?.name}
                        </span>
                      </div>

                      <div className="flex items-center space-x-1 text-red-500">
                        <Heart className="w-4 h-4 fill-current" />
                        <span className="text-sm font-semibold">
                          {post.likesCount || 0}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">
              Why Choose Welog?
            </h2>
            <p className="text-gray-600 text-lg max-w-2xl mx-auto">
              Everything you need to share your stories and connect with readers
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {/* Feature 1 */}
            <div className="text-center group">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-blue-500 to-primary-600 rounded-2xl mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-300 shadow-lg">
                <Edit3 className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">
                Easy Writing
              </h3>
              <p className="text-gray-600">
                Intuitive editor with rich formatting options for beautiful
                posts
              </p>
            </div>

            {/* Feature 2 */}
            <div className="text-center group">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-purple-500 to-pink-600 rounded-2xl mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-300 shadow-lg">
                <Users className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">
                Active Community
              </h3>
              <p className="text-gray-600">
                Connect with like-minded writers and engaged readers
              </p>
            </div>

            {/* Feature 3 */}
            <div className="text-center group">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-green-500 to-emerald-600 rounded-2xl mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-300 shadow-lg">
                <MessageSquare className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">
                Engage & Discuss
              </h3>
              <p className="text-gray-600">
                Comment, like, and have meaningful conversations
              </p>
            </div>

            {/* Feature 4 */}
            <div className="text-center group">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-orange-500 to-red-600 rounded-2xl mb-6 group-hover:scale-110 group-hover:rotate-6 transition-all duration-300 shadow-lg">
                <Award className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-3">
                Grow Your Reach
              </h3>
              <p className="text-gray-600">
                Build your audience and establish your voice
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Latest Posts Section */}
      <section className="py-20 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <div className="inline-flex items-center space-x-2 bg-primary-100 text-primary-700 px-4 py-2 rounded-full mb-4">
              <Clock className="w-4 h-4" />
              <span className="text-sm font-semibold">Fresh Content</span>
            </div>
            <h2 className="text-4xl font-bold text-gray-900 mb-4">
              Latest Posts
            </h2>
            <p className="text-gray-600 text-lg max-w-2xl mx-auto">
              Discover the most recent stories from our community
            </p>
          </div>

          <PostList limit={6} showFeatured={true} />

          <div className="text-center mt-16">
            <Link
              to="/posts"
              className="group inline-flex items-center bg-gradient-to-r from-primary-600 to-purple-600 text-white px-8 py-4 rounded-xl font-semibold hover:from-primary-700 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-xl hover:scale-105"
            >
              View All Posts
              <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
            </Link>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      {!isAuthenticated && (
        <section className="py-20 bg-gradient-to-r from-primary-600 via-purple-600 to-pink-600 relative overflow-hidden">
          {/* Animated Background */}
          <div className="absolute inset-0">
            <div className="absolute top-0 left-1/4 w-96 h-96 bg-white/10 rounded-full blur-3xl animate-pulse"></div>
            <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-purple-400/20 rounded-full blur-3xl animate-pulse delay-1000"></div>
          </div>

          <div className="relative max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-white">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Ready to Share Your Story?
            </h2>
            <p className="text-xl mb-8 text-blue-100">
              Join our community of passionate writers and start creating today
            </p>
            <Link
              to="/register"
              className="inline-flex items-center bg-white text-primary-600 px-10 py-5 rounded-xl font-bold hover:bg-blue-50 transition-all duration-300 shadow-2xl hover:shadow-3xl hover:scale-105 text-lg"
            >
              <Edit3 className="w-6 h-6 mr-2" />
              Get Started for Free
              <ArrowRight className="w-6 h-6 ml-2" />
            </Link>
          </div>
        </section>
      )}
    </div>
  );
};

export default Home;
