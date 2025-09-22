import React from "react";
import { Link } from "react-router-dom";
import PostList from "../components/posts/PostList";
import { useAuth } from "../context/AuthContext";
import { ArrowRight } from "lucide-react";

const Home = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-500 to-primary-700 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl md:text-6xl font-bold mb-6">
            Welcome to Welog
          </h1>
          <p className="text-xl md:text-2xl mb-8 opacity-90">
            Share your thoughts, stories, and ideas with the world
          </p>
          {!isAuthenticated && (
            <div className="space-x-4">
              <Link
                to="/register"
                className="bg-white text-primary-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors"
              >
                Get Started
              </Link>
              <Link
                to="/posts"
                className="border-2 border-white text-white px-8 py-3 rounded-lg font-semibold hover:bg-white hover:text-primary-600 transition-colors"
              >
                Browse Posts
              </Link>
            </div>
          )}
        </div>
      </section>

      {/* Featured Posts Section */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Latest Posts
            </h2>
            <p className="text-gray-600 text-lg">
              Discover the most recent stories from our community
            </p>
          </div>

          <PostList />

          <div className="text-center mt-12">
            <Link
              to="/posts"
              className="inline-flex items-center text-primary-600 hover:text-primary-700 font-semibold"
            >
              View All Posts
              <ArrowRight size={20} className="ml-2" />
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
