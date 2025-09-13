import React from "react";
import { Link } from "react-router-dom";
import { Calendar, User } from "lucide-react";
import API_URL from "../../config";

const PostCard = ({ post }) => {
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
      {post.coverImage && (
        <Link to={`/posts/${post.id}`}>
          <img
            src={`${API_URL}/img/posts/${post.coverImage}`}
            alt={post.title}
            className="w-full h-48 object-cover hover:opacity-90 transition-opacity"
          />
        </Link>
      )}
      <div className="p-6">
        <h2 className="text-xl font-semibold text-gray-900 mb-3">
          <Link
            to={`/posts/${post.id}`}
            className="hover:text-primary-600 transition-colors"
          >
            {post.title}
          </Link>
        </h2>

        <p className="text-gray-600 mb-4 line-clamp-3">
          {post.excerpt || (post.content && post.content.substring(0, 150))}...
        </p>

        <div className="flex items-center justify-between text-sm text-gray-500">
          <div className="flex items-center">
            <User size={16} className="mr-1" />
            <span>{post.author?.name || "Unknown Author"}</span>
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
      </div>
    </div>
  );
};

export default PostCard;
