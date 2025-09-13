import React, { useState } from "react";
import { User, Calendar, Trash2, Edit } from "lucide-react";

const CommentItem = ({ comment, onDelete, currentUser }) => {
  const [isDeleting, setIsDeleting] = useState(false);

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

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const canModify =
    currentUser &&
    (currentUser.id === comment.user?.id ||
      currentUser.roles?.includes("ROLE_ADMIN") ||
      currentUser.roles?.includes("ROLE_SUPER_ADMIN"));

  return (
    <div className="bg-white rounded-lg p-6 shadow-sm border">
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
            <User size={20} className="text-primary-600" />
          </div>
          <div>
            <h4 className="font-semibold text-gray-900">
              {comment.user?.name || "Anonymous"}
            </h4>
            <div className="flex items-center text-sm text-gray-500">
              <Calendar size={14} className="mr-1" />
              <span>{formatDate(comment.createdAt)}</span>
            </div>
          </div>
        </div>

        {canModify && (
          <div className="flex space-x-2">
            <button
              onClick={handleDelete}
              disabled={isDeleting}
              className="text-red-600 hover:text-red-800 disabled:opacity-50 transition-colors"
              title="Delete comment"
            >
              <Trash2 size={16} />
            </button>
          </div>
        )}
      </div>

      <div className="text-gray-800 leading-relaxed">{comment.content}</div>
    </div>
  );
};

export default CommentItem;
