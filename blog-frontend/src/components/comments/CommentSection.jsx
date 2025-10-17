import React, { useState } from "react";
import { User, MessageCircle, Send } from "lucide-react";
import NestedComment from "./NestedComment";

import API_URL from "../../config";

const CommentSection = ({
  comments = [],
  onAddComment,
  onDeleteComment,
  onReplyComment,
  currentUser,
  loading = false,
}) => {
  const [newComment, setNewComment] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    setIsSubmitting(true);
    try {
      await onAddComment({ content: newComment });
      setNewComment("");
    } catch (error) {
      console.error("Error adding comment:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  // Count total comments including all nested replies
  const countTotalComments = (comments) => {
    let total = 0;
    comments.forEach((comment) => {
      total += 1; // Count the comment itself
      if (comment.replies && comment.replies.length > 0) {
        total += countTotalComments(comment.replies); // Count nested replies
      }
    });
    return total;
  };

  const totalCommentsCount = countTotalComments(comments);

  // Since comments are already nested structures from parent component,
  // we don't need to filter for top-level comments
  const topLevelComments = comments;

  return (
    <div className="space-y-6">
      {/* Comment Form */}
      {currentUser ? (
        <div className="bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl border border-gray-200 p-6">
          <div className="flex items-center space-x-4 mb-6">
            {currentUser?.photo ? (
              <img
                src={
                  currentUser.photo.includes("http")
                    ? currentUser.photo
                    : `${API_URL}/img/users/${currentUser.photo}`
                }
                alt={currentUser.name}
                className="w-10 h-10 rounded-full object-cover border-2 border-white shadow-sm"
              />
            ) : (
              <div className="w-10 h-10 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center shadow-sm">
                <span className="text-white text-sm font-bold">
                  {currentUser?.name?.charAt(0)?.toUpperCase() || "U"}
                </span>
              </div>
            )}
            <div>
              <p className="font-semibold text-gray-900">{currentUser.name}</p>
              <p className="text-sm text-gray-600">Share your thoughts</p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="relative">
              <textarea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="What are your thoughts? Share your perspective on this post..."
                rows="4"
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none placeholder-gray-500 bg-white"
                disabled={isSubmitting}
              />
              <div className="absolute bottom-3 right-3 text-xs text-gray-400">
                {newComment.length}/500
              </div>
            </div>

            <div className="flex items-center justify-between pt-2">
              <div className="flex items-center space-x-4 text-xs text-gray-500">
                <div className="flex items-center">
                  <User size={14} className="mr-1" />
                  <span>Commenting as {currentUser.name}</span>
                </div>
                <div className="flex items-center">
                  <MessageCircle size={14} className="mr-1" />
                  <span>Be respectful and constructive</span>
                </div>
              </div>

              <button
                type="submit"
                disabled={
                  isSubmitting || !newComment.trim() || newComment.length > 500
                }
                className="flex items-center px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-sm hover:shadow-md"
              >
                {isSubmitting ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    Posting...
                  </>
                ) : (
                  <>
                    <Send size={16} className="mr-2" />
                    Post Comment
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      ) : (
        <div className="bg-gray-50 rounded-lg border p-6 text-center">
          <MessageCircle size={32} className="mx-auto text-gray-400 mb-3" />
          <p className="text-gray-600 mb-3">Join the conversation</p>
          <p className="text-sm text-gray-500">
            Please log in to leave a comment
          </p>
        </div>
      )}

      {/* Comments Header */}
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-gray-900 flex items-center">
          <MessageCircle size={20} className="mr-2" />
          Comments ({totalCommentsCount})
        </h3>
        {comments.length > 0 && (
          <select className="text-sm border border-gray-300 rounded-md px-3 py-1 focus:outline-none focus:ring-2 focus:ring-primary-500">
            <option value="newest">Newest first</option>
            <option value="oldest">Oldest first</option>
            <option value="popular">Most popular</option>
          </select>
        )}
      </div>

      {/* Comments List */}
      {loading ? (
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => (
            <div
              key={i}
              className="bg-white rounded-lg p-6 shadow-sm border animate-pulse"
            >
              <div className="flex items-center space-x-3 mb-3">
                <div className="w-8 h-8 bg-gray-300 rounded-full"></div>
                <div className="space-y-1">
                  <div className="h-4 bg-gray-300 rounded w-24"></div>
                  <div className="h-3 bg-gray-300 rounded w-16"></div>
                </div>
              </div>
              <div className="space-y-2">
                <div className="h-4 bg-gray-300 rounded"></div>
                <div className="h-4 bg-gray-300 rounded w-3/4"></div>
              </div>
            </div>
          ))}
        </div>
      ) : topLevelComments.length > 0 ? (
        <div className="space-y-4">
          {topLevelComments.map((comment) => (
            <NestedComment
              key={comment.id}
              comment={comment}
              onDelete={onDeleteComment}
              onReply={onReplyComment}
              currentUser={currentUser}
              // level={0} // Start at level 1 for top-level comments, use level from comment object
              maxLevel={3} // 1, 2, 3 = 3 levels total
            />
          ))}
        </div>
      ) : (
        <div className="text-center py-12 bg-white rounded-lg border">
          <MessageCircle size={48} className="mx-auto text-gray-400 mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            No comments yet
          </h3>
          <p className="text-gray-500">
            {currentUser
              ? "Be the first to share your thoughts!"
              : "Log in to start the conversation"}
          </p>
        </div>
      )}
    </div>
  );
};

export default CommentSection;
