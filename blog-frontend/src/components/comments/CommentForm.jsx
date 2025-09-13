import React, { useState } from "react";
import { Send } from "lucide-react";

const CommentForm = ({ onSubmit, loading }) => {
  const [content, setContent] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;

    try {
      await onSubmit({ content });
      setContent(""); // Clear form on success
    } catch (error) {
      console.error("Error submitting comment:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="mb-8">
      <div className="bg-white rounded-lg border p-4">
        <label
          htmlFor="comment"
          className="block text-sm font-medium text-gray-700 mb-2"
        >
          Add a comment
        </label>
        <textarea
          id="comment"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Share your thoughts..."
          rows="4"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 resize-none"
          disabled={loading}
        />

        <div className="flex justify-between items-center mt-3">
          <span
            className={`text-sm ${
              content.length > 500 ? "text-red-600" : "text-gray-500"
            }`}
          >
            {content.length}/500
          </span>
          <button
            type="submit"
            disabled={loading || !content.trim() || content.length > 500}
            className="flex items-center bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? (
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
            ) : (
              <Send size={16} className="mr-2" />
            )}
            Post Comment
          </button>
        </div>
      </div>
    </form>
  );
};

export default CommentForm;
