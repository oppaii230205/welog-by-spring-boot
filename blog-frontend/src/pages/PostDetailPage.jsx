import React, { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { postService } from "../services/postService";
import { commentService } from "../services/commentService";
import LoadingSpinner from "../components/common/LoadingSpinner";
import CommentSection from "../components/comments/CommentSection";
import LikeButton from "../components/common/LikeButton";
import { useAuth } from "../context/AuthContext";
import {
  ArrowLeft,
  Calendar,
  User,
  Clock,
  MessageCircle,
  Edit,
  Trash2,
  Share2,
  BookmarkPlus,
  Eye,
  Heart,
} from "lucide-react";

import API_URL from "../config";

const PostDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);

  // **COMMENTS ARE ALREADY NESTED**
  // Transform flat comments list into nested structure
  // const buildCommentTree = (comments) => {
  //   const commentMap = new Map();
  //   const rootComments = [];

  //   // First pass: create map of all comments
  //   comments.forEach((comment) => {
  //     commentMap.set(comment.id, { ...comment, replies: [] });
  //   });

  //   // Second pass: build the tree structure
  //   comments.forEach((comment) => {
  //     if (comment.parentId) {
  //       const parent = commentMap.get(comment.parentId);
  //       if (parent) {
  //         parent.replies.push(commentMap.get(comment.id));
  //       }
  //     } else {
  //       rootComments.push(commentMap.get(comment.id));
  //     }
  //   });

  //   return rootComments;
  // };

  const fetchPostAndComments = useCallback(async () => {
    try {
      setLoading(true);
      const [postResponse, commentsResponse] = await Promise.all([
        postService.getById(id),
        commentService.getRootCommentsByPostId(id),
      ]);

      setPost(postResponse.data);

      // FIXME: Check the structure of commentsResponse.data
      // Transform flat comments into nested structure
      console.log("Comments fetched:", commentsResponse.data);

      // const nestedComments = buildCommentTree(commentsResponse.data);
      // console.log("Nested Comments:", nestedComments);
      setComments(commentsResponse.data);
    } catch (err) {
      setError("Failed to fetch post or comments");
      console.error("Error:", err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchPostAndComments();
  }, [fetchPostAndComments]);

  const handleAddComment = async (commentData) => {
    try {
      setCommentLoading(true);
      await commentService.createByPostId(id, commentData);

      // Refresh comments to get updated nested structure
      const commentsResponse = await commentService.getRootCommentsByPostId(id);
      // const nestedComments = buildCommentTree(commentsResponse.data);
      setComments(commentsResponse.data);
    } catch (error) {
      console.error("Error adding comment:", error);
      throw error;
    } finally {
      setCommentLoading(false);
    }
  };

  const handleReplyComment = async (replyData) => {
    try {
      await commentService.createByPostId(id, replyData);

      // Refresh comments to get updated nested structure
      const commentsResponse = await commentService.getRootCommentsByPostId(id);
      // const nestedComments = buildCommentTree(commentsResponse.data);
      setComments(commentsResponse.data);
    } catch (error) {
      console.error("Error adding reply:", error);
      throw error;
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await commentService.delete(commentId);

      // Refresh comments to get updated nested structure
      const commentsResponse = await commentService.getRootCommentsByPostId(id);
      // const nestedComments = buildCommentTree(commentsResponse.data);
      setComments(commentsResponse.data);
    } catch (err) {
      console.error("Error deleting comment:", err);
      throw err;
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const formatReadTime = (content) => {
    const wordsPerMinute = 200;
    const words = content.split(/\s+/).length;
    const minutes = Math.ceil(words / wordsPerMinute);
    return `${minutes} min read`;
  };

  if (loading) return <LoadingSpinner />;
  if (error)
    return <div className="text-center text-red-600 py-8">{error}</div>;
  if (!post) return <div className="text-center py-8">Post not found</div>;

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Hero Section with Cover Image */}
      {post.coverImage && (
        <div className="relative h-96 md:h-[500px] overflow-hidden">
          <img
            src={`${API_URL}/img/posts/${post.coverImage}`}
            alt={post.title}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/30 to-transparent" />

          {/* Back Button Overlay */}
          <div className="absolute top-6 left-6">
            <button
              onClick={() => navigate(-1)}
              className="flex items-center px-4 py-2 bg-white/90 hover:bg-white text-gray-800 rounded-full shadow-lg backdrop-blur-sm transition-all duration-200"
            >
              <ArrowLeft size={18} className="mr-2" />
              Back
            </button>
          </div>

          {/* Title Overlay */}
          <div className="absolute bottom-0 left-0 right-0 p-6 md:p-12">
            <div className="max-w-4xl mx-auto">
              <h1 className="text-3xl md:text-5xl lg:text-6xl font-bold text-white mb-4 leading-tight drop-shadow-2xl">
                {post.title}
              </h1>
              <div className="flex flex-wrap items-center gap-4 text-white/90">
                <div className="flex items-center">
                  {post.author?.photo ? (
                    <img
                      src={`${API_URL}/img/users/${post.author.photo}`}
                      alt={post.author.name}
                      className="w-10 h-10 rounded-full mr-3 object-cover border-2 border-white/30"
                    />
                  ) : (
                    <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center mr-3 border-2 border-white/30">
                      <span className="text-white text-sm font-bold">
                        {post.author?.name?.charAt(0)?.toUpperCase() || "U"}
                      </span>
                    </div>
                  )}
                  <div>
                    <p className="font-medium text-white">
                      {post.author?.name || "Unknown Author"}
                    </p>
                    <p className="text-white/70 text-sm">
                      {formatDate(post.createdAt)} •{" "}
                      {formatReadTime(post.content)}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Content Section */}
      <div className="relative">
        {/* Without Cover Image - Header */}
        {!post.coverImage && (
          <div className="bg-white border-b">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
              <button
                onClick={() => navigate(-1)}
                className="flex items-center text-gray-600 hover:text-gray-900 transition-colors mb-6"
              >
                <ArrowLeft size={20} className="mr-2" />
                Back to Posts
              </button>

              <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6 leading-tight">
                {post.title}
              </h1>

              <div className="flex flex-wrap items-center gap-6 text-gray-600">
                <div className="flex items-center">
                  {post.author?.photo ? (
                    <img
                      src={`${API_URL}/img/users/${post.author.photo}`}
                      alt={post.author.name}
                      className="w-12 h-12 rounded-full mr-3 object-cover"
                    />
                  ) : (
                    <div className="w-12 h-12 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center mr-3">
                      <span className="text-white text-sm font-bold">
                        {post.author?.name?.charAt(0)?.toUpperCase() || "U"}
                      </span>
                    </div>
                  )}
                  <div>
                    <p className="font-medium text-gray-900">
                      {post.author?.name || "Unknown Author"}
                    </p>
                    <p className="text-gray-600 text-sm">
                      {formatDate(post.createdAt)} •{" "}
                      {formatReadTime(post.content)}
                    </p>
                  </div>
                </div>

                <div className="flex items-center">
                  <MessageCircle size={18} className="mr-2" />
                  <span>{comments.length} comments</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Main Article Content */}
        <article className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-12">
          {/* Article Body */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-8">
            <div className="p-8 md:p-12">
              {/* Excerpt if available */}
              {post.excerpt && (
                <div className="bg-gradient-to-r from-primary-50 to-blue-50 border-l-4 border-primary-500 p-6 mb-8 rounded-r-lg">
                  <p className="text-lg text-gray-700 font-medium italic leading-relaxed">
                    {post.excerpt}
                  </p>
                </div>
              )}

              {/* Content */}
              <div className="prose prose-lg prose-gray max-w-none">
                <div
                  className="text-gray-800 leading-relaxed"
                  style={{
                    fontSize: "1.125rem",
                    lineHeight: "1.75",
                  }}
                  dangerouslySetInnerHTML={{
                    __html: post.content
                      .replace(
                        /\*\*(.*?)\*\*/g,
                        '<strong class="font-semibold text-gray-900">$1</strong>'
                      )
                      .replace(/\*(.*?)\*/g, '<em class="italic">$1</em>')
                      .replace(/\n\n/g, '</p><p class="mb-6">')
                      .replace(/\n/g, "<br/>"),
                  }}
                />
              </div>
            </div>

            {/* Article Footer with Tags */}
            {post.tags && post.tags.length > 0 && (
              <div className="px-8 md:px-12 pb-8">
                <div className="border-t border-gray-100 pt-6">
                  <h3 className="text-sm font-semibold text-gray-700 mb-4">
                    Related Topics:
                  </h3>
                  <div className="flex flex-wrap gap-2">
                    {post.tags.map((tag) => (
                      <span
                        key={tag.id}
                        className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gradient-to-r from-primary-100 to-blue-100 text-primary-800 hover:from-primary-200 hover:to-blue-200 transition-all cursor-pointer"
                      >
                        #{tag.name}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Action Bar */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-8">
            <div className="p-6 md:p-8">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <LikeButton
                    postId={post.id}
                    initialLikeCount={post.likeCount || 0}
                  />

                  <button className="flex items-center space-x-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all duration-200">
                    <Share2 size={18} />
                    <span className="text-sm font-medium">Share</span>
                  </button>

                  <button className="flex items-center space-x-2 text-gray-600 hover:text-green-600 hover:bg-green-50 px-3 py-2 rounded-lg transition-all duration-200">
                    <BookmarkPlus size={18} />
                    <span className="text-sm font-medium">Save</span>
                  </button>
                </div>

                <div className="flex items-center space-x-4 text-gray-500 text-sm">
                  <div className="flex items-center">
                    <Eye size={16} className="mr-1" />
                    <span>2.1k views</span>
                  </div>

                  <div className="flex items-center">
                    <Clock size={16} className="mr-1" />
                    <span>{formatReadTime(post.content)}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Comments Section */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="border-b border-gray-100 p-6 md:p-8">
              <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-gray-900">Discussion</h2>
                <div className="flex items-center text-gray-600">
                  <MessageCircle size={20} className="mr-2" />
                  <span className="font-medium">
                    {comments.length} comments
                  </span>
                </div>
              </div>
              <p className="text-gray-600 mt-2">
                Join the conversation and share your thoughts
              </p>
            </div>

            <div className="p-6 md:p-8">
              <CommentSection
                comments={comments}
                onAddComment={handleAddComment}
                onDeleteComment={handleDeleteComment}
                onReplyComment={handleReplyComment}
                currentUser={user}
                loading={commentLoading}
              />
            </div>
          </div>
        </article>
      </div>
    </div>
  );
};

export default PostDetailPage;
