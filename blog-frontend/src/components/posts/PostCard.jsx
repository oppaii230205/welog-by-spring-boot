import React from "react";
import { Link } from "react-router-dom";
import { Calendar, User, MessageCircle } from "lucide-react";
import LikeButton from "../common/LikeButton";
import API_URL from "../../config";

const PostCard = ({ post, viewMode = "grid" }) => {
  if (viewMode === "list") {
    return (
      <div className="bg-white rounded-lg shadow-md border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300">
        <div className="flex">
          {post.coverImage && (
            <Link
              to={`/posts/${post.id}`}
              className="block relative overflow-hidden flex-shrink-0"
            >
              <img
                src={
                  post.coverImage.includes("http")
                    ? post.coverImage
                    : `${API_URL}/img/posts/${post.coverImage}`
                }
                alt={post.title}
                className="w-48 h-32 object-cover hover:scale-105 transition-transform duration-300"
              />
              <div className="absolute inset-0 bg-black/0 hover:bg-black/10 transition-colors duration-300"></div>
            </Link>
          )}
          <div className="flex-1 p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-2 line-clamp-2">
              <Link
                to={`/posts/${post.id}`}
                className="hover:text-primary-600 transition-colors duration-200"
              >
                {post.title}
              </Link>
            </h2>

            <p className="text-gray-600 mb-4 line-clamp-2 leading-relaxed">
              {post.excerpt || (post.content && post.content.substring(0, 200))}
              ...
            </p>

            <div className="flex items-center justify-between">
              {/* Author and Date */}
              <div className="flex items-center space-x-4 text-sm text-gray-500">
                <div className="flex items-center">
                  {post.author?.photo ? (
                    <img
                      src={
                        post.author.photo.includes("http")
                          ? post.author.photo
                          : `${API_URL}/img/users/${post.author.photo}`
                      }
                      alt={post.author.name}
                      className="w-5 h-5 rounded-full mr-2 object-cover"
                    />
                  ) : (
                    <User size={16} className="mr-2" />
                  )}
                  <span className="font-medium">
                    {post.author?.name || "Unknown Author"}
                  </span>
                </div>

                <div className="flex items-center">
                  <Calendar size={16} className="mr-1" />
                  <span>
                    {post.createdAt
                      ? new Date(post.createdAt).toLocaleDateString()
                      : "Unknown date"}
                  </span>
                </div>
              </div>

              {/* Interaction Buttons */}
              <div className="flex items-center space-x-2">
                <LikeButton
                  postId={post.id}
                  initialLikeCount={post.likeCount || 0}
                />

                <Link
                  to={`/posts/${post.id}#comments`}
                  className="flex items-center space-x-1 text-gray-600 hover:text-primary-600 px-3 py-2 rounded-lg hover:bg-primary-50 transition-all duration-200"
                >
                  <MessageCircle size={18} />
                  <span className="text-sm font-medium">
                    {post.commentCount || 0}
                  </span>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Grid view (default)
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-all duration-300 border border-gray-100">
      {post.coverImage && (
        <Link
          to={`/posts/${post.id}`}
          className="block relative overflow-hidden"
        >
          <img
            src={
              post.coverImage.includes("http")
                ? post.coverImage
                : `${API_URL}/img/posts/${post.coverImage}`
            }
            alt={post.title}
            className="w-full h-48 object-cover hover:scale-105 transition-transform duration-300"
          />
          <div className="absolute inset-0 bg-black/0 hover:bg-black/10 transition-colors duration-300"></div>
        </Link>
      )}
      <div className="p-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-3 line-clamp-2">
          <Link
            to={`/posts/${post.id}`}
            className="hover:text-primary-600 transition-colors duration-200"
          >
            {post.title}
          </Link>
        </h2>

        <p className="text-gray-600 mb-4 line-clamp-3 leading-relaxed">
          {post.excerpt || (post.content && post.content.substring(0, 150))}...
        </p>

        <div className="flex items-center justify-between">
          {/* Author and Date */}
          <div className="flex items-center space-x-4 text-sm text-gray-500">
            <div className="flex items-center">
              {post.author?.photo ? (
                <img
                  src={
                    post.author.photo.includes("http")
                      ? post.author.photo
                      : `${API_URL}/img/users/${post.author.photo}`
                  }
                  alt={post.author.name}
                  className="w-5 h-5 rounded-full mr-2 object-cover"
                />
              ) : (
                <User size={16} className="mr-2" />
              )}
              <span className="font-medium">
                {post.author?.name || "Unknown Author"}
              </span>
            </div>

            <div className="flex items-center">
              <Calendar size={16} className="mr-1" />
              <span>
                {post.createdAt
                  ? new Date(post.createdAt).toLocaleDateString()
                  : "Unknown date"}
              </span>
            </div>
          </div>

          {/* Interaction Buttons */}
          <div className="flex items-center space-x-2">
            <LikeButton
              postId={post.id}
              initialLikeCount={post.likeCount || 0}
            />

            <Link
              to={`/posts/${post.id}#comments`}
              className="flex items-center space-x-1 text-gray-600 hover:text-primary-600 px-3 py-2 rounded-lg hover:bg-primary-50 transition-all duration-200"
            >
              <MessageCircle size={18} />
              <span className="text-sm font-medium">
                {post.commentCount || 0}
              </span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostCard;
