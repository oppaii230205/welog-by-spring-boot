import React, { useState } from "react";
import {
  User,
  Calendar,
  Trash2,
  Reply,
  ChevronDown,
  ChevronUp,
  MessageCircle,
} from "lucide-react";

import API_URL from "../../config";

const NestedComment = ({
  comment,
  onDelete,
  onReply,
  currentUser,
  // level = 0,
  maxLevel = 3,
}) => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [isReplying, setIsReplying] = useState(false);
  const [showReplies, setShowReplies] = useState(true);
  const [replyContent, setReplyContent] = useState("");
  const [isSubmittingReply, setIsSubmittingReply] = useState(false);

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this comment?")) {
      setIsDeleting(true);
      try {
        await onDelete(comment.id);
      } catch (error) {
        console.error("Error deleting comment:", error);
      } finally {
        setIsDeleting(false);
      }
    }
  };

  const handleReplySubmit = async (e) => {
    e.preventDefault();
    if (!replyContent.trim()) return;

    setIsSubmittingReply(true);
    try {
      await onReply({
        content: replyContent,
        parentId: comment.id,
      });
      setReplyContent("");
      setIsReplying(false);
    } catch (error) {
      console.error("Error submitting reply:", error);
    } finally {
      setIsSubmittingReply(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getRelativeTime = (dateString) => {
    const now = new Date();
    const commentDate = new Date(dateString);
    const diffInHours = Math.floor((now - commentDate) / (1000 * 60 * 60));

    if (diffInHours < 1) return "just now";
    if (diffInHours < 24) return `${diffInHours}h ago`;
    if (diffInHours < 48) return "1 day ago";
    return `${Math.floor(diffInHours / 24)} days ago`;
  };

  const canModify =
    currentUser &&
    (currentUser.id === comment.user?.id ||
      currentUser.roles?.includes("ROLE_ADMIN") ||
      currentUser.roles?.includes("ROLE_SUPER_ADMIN"));

  const canReply = currentUser && comment.level < maxLevel;
  const hasReplies = comment.replies && comment.replies.length > 0;

  // Calculate indentation based on level
  const marginLeft = comment.level > 1 ? `${(comment.level - 1) * 2}rem` : "0";

  return (
    <div
      className={`${
        comment.level > 1 ? "border-l-2 border-gray-200 pl-4" : ""
      }`}
      style={{ marginLeft: comment.level > 1 ? marginLeft : "0" }}
    >
      <div className="bg-white rounded-lg p-4 mb-4 shadow-sm border hover:shadow-md transition-shadow">
        {/* Comment Header */}
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center space-x-3">
            <div className="relative">
              {comment.user?.photo ? (
                <img
                  src={`${API_URL}/img/users/${comment.user.photo}`}
                  alt={comment.user.name}
                  className="w-8 h-8 rounded-full object-cover"
                />
              ) : (
                <div className="w-8 h-8 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center">
                  <span className="text-white text-sm font-medium">
                    {comment.user?.name?.charAt(0)?.toUpperCase() || "U"}
                  </span>
                </div>
              )}
              {comment.level > 1 && (
                <div className="absolute -left-1 -top-1 w-3 h-3 bg-primary-500 rounded-full border-2 border-white"></div>
              )}
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h4 className="font-semibold text-gray-900 text-sm">
                  {comment.user?.name || "Anonymous"}
                </h4>
                <span className="text-xs text-gray-500">•</span>
                <span
                  className="text-xs text-gray-500"
                  title={formatDate(comment.createdAt)}
                >
                  {getRelativeTime(comment.createdAt)}
                </span>
              </div>
              {comment.level > 1 && (
                <div className="text-xs text-gray-400 flex items-center mt-1">
                  <Reply size={12} className="mr-1" />
                  Reply to thread
                </div>
              )}
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center space-x-2">
            {canReply && (
              <button
                onClick={() => setIsReplying(!isReplying)}
                className="text-gray-500 hover:text-primary-600 text-xs font-medium transition-colors"
              >
                <Reply size={14} className="inline mr-1" />
                Reply
              </button>
            )}
            {canModify && (
              <button
                onClick={handleDelete}
                disabled={isDeleting}
                className="text-red-500 hover:text-red-700 disabled:opacity-50 transition-colors"
                title="Delete comment"
              >
                <Trash2 size={14} />
              </button>
            )}
          </div>
        </div>

        {/* Comment Content */}
        <div className="text-gray-800 leading-relaxed text-sm mb-3">
          {comment.content}
        </div>

        {/* Reply Form */}
        {isReplying && (
          <form
            onSubmit={handleReplySubmit}
            className="mt-4 p-3 bg-gray-50 rounded-lg"
          >
            <textarea
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
              placeholder="Write a reply..."
              rows="3"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none text-sm"
              disabled={isSubmittingReply}
            />
            <div className="flex justify-between items-center mt-2">
              <span
                className={`text-xs ${
                  replyContent.length > 280 ? "text-red-600" : "text-gray-500"
                }`}
              >
                {replyContent.length}/300
              </span>
              <div className="flex space-x-2">
                <button
                  type="button"
                  onClick={() => {
                    setIsReplying(false);
                    setReplyContent("");
                  }}
                  className="px-3 py-1 text-xs text-gray-600 hover:text-gray-800 transition-colors"
                  disabled={isSubmittingReply}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={
                    isSubmittingReply ||
                    !replyContent.trim() ||
                    replyContent.length > 300
                  }
                  className="px-3 py-1 bg-primary-600 text-white text-xs rounded-md hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {isSubmittingReply ? "Replying..." : "Reply"}
                </button>
              </div>
            </div>
          </form>
        )}

        {/* Replies Toggle */}
        {hasReplies && (
          <div className="mt-3 pt-3 border-t border-gray-100">
            <button
              onClick={() => setShowReplies(!showReplies)}
              className="flex items-center text-xs text-gray-600 hover:text-gray-800 font-medium transition-colors"
            >
              {showReplies ? (
                <ChevronUp size={14} className="mr-1" />
              ) : (
                <ChevronDown size={14} className="mr-1" />
              )}
              <MessageCircle size={14} className="mr-1" />
              {comment.replies.length}{" "}
              {comment.replies.length === 1 ? "reply" : "replies"}
            </button>
          </div>
        )}
      </div>

      {/* Nested Replies */}
      {hasReplies && showReplies && (
        <div className="space-y-2">
          {comment.replies.map((reply) => (
            <NestedComment
              key={reply.id}
              comment={reply}
              onDelete={onDelete}
              onReply={onReply}
              currentUser={currentUser}
              // level={level + 1} // Use level from comment object
              maxLevel={maxLevel}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default NestedComment;
