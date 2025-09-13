import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { postService } from "../services/postService";
import { commentService } from "../services/commentService";
import { userService } from "../services/userService";
import LoadingSpinner from "../components/common/LoadingSpinner";
import CommentList from "../components/comments/CommentList";
import CommentForm from "../components/comments/CommentForm";
import { useAuth } from "../context/AuthContext";
import {
  ArrowLeft,
  Calendar,
  User,
  Clock,
  MessageCircle,
  Edit,
  Trash2,
} from "lucide-react";
import API_URL from "../config";

const PostDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const [author, setAuthor] = useState(null);
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);

  useEffect(() => {
    fetchPostAndComments();
  }, [id]);

  const fetchPostAndComments = async () => {
    try {
      setLoading(true);
      const [postResponse, commentsResponse] = await Promise.all([
        postService.getById(id),
        commentService.getByPostId(id),
      ]);

      // console.log("Post data:", postResponse.data);

      const authorResponse = await userService.getUserById(
        postResponse.data.author.id
      );
      setAuthor(authorResponse.data);

      setPost(postResponse.data);
      setComments(commentsResponse.data);
    } catch (err) {
      setError("Failed to load post");
      console.error("Error fetching post:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (commentData) => {
    try {
      setCommentLoading(true);
      const response = await commentService.create({
        ...commentData,
        postId: parseInt(id),
      });

      setComments((prev) => [response.data, ...prev]);
      return response;
    } finally {
      setCommentLoading(false);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await commentService.delete(commentId);
      setComments((prev) => prev.filter((comment) => comment.id !== commentId));
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
    <div className="min-h-screen bg-gray-50">
      {/* Header with Back Button */}
      <div className="bg-white border-b">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeft size={20} className="mr-2" />
            Back to Posts
          </button>
        </div>
      </div>

      {/* Main Content */}
      <article className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Post Header */}
        <header className="mb-8">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 mb-6 leading-tight">
            {post.title}
          </h1>

          {/* Meta Information */}
          <div className="flex flex-wrap items-center gap-4 text-gray-600 mb-6">
            <div className="flex items-center">
              <img
                src={
                  author?.photo
                    ? `${API_URL}/img/users/${author.photo}`
                    : `${API_URL}/img/users/default.png`
                }
                alt={author?.name || "Unknown Author"}
                className="w-8 h-8 rounded-full mr-2"
              />
              <span className="font-medium">
                {post.author?.name || "Unknown Author"}
              </span>
            </div>

            <div className="flex items-center">
              <Calendar size={18} className="mr-2" />
              <span>{formatDate(post.createdAt)}</span>
            </div>

            <div className="flex items-center">
              <Clock size={18} className="mr-2" />
              <span>{formatReadTime(post.content)}</span>
            </div>

            <div className="flex items-center">
              <MessageCircle size={18} className="mr-2" />
              <span>{comments.length} comments</span>
            </div>
          </div>

          {/* Cover Image */}
          {post.coverImage && (
            <div className="mb-8 rounded-lg overflow-hidden">
              <img
                src={`${API_URL}/img/posts/${post.coverImage}`}
                alt={post.title}
                className="w-full h-64 md:h-96 object-cover"
              />
            </div>
          )}
        </header>

        {/* Post Content */}
        <div className="prose prose-lg max-w-none mb-12">
          <div
            className="text-gray-800 leading-relaxed"
            dangerouslySetInnerHTML={{
              __html: post.content.replace(/\n/g, "<br/>"),
            }}
          />
        </div>

        {/* Tags */}
        {post.tags && post.tags.length > 0 && (
          <div className="mb-8">
            <h3 className="text-sm font-semibold text-gray-700 mb-3">Tags:</h3>
            <div className="flex flex-wrap gap-2">
              {post.tags.map((tag) => (
                <span
                  key={tag.id}
                  className="bg-primary-100 text-primary-800 text-sm px-3 py-1 rounded-full"
                >
                  #{tag.name}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* Comments Section */}
        <section className="border-t pt-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">
            Comments ({comments.length})
          </h2>

          {/* Comment Form */}
          {isAuthenticated ? (
            <CommentForm onSubmit={handleAddComment} loading={commentLoading} />
          ) : (
            <div className="bg-gray-50 rounded-lg p-6 text-center mb-8">
              <p className="text-gray-600 mb-4">
                Please sign in to leave a comment
              </p>
              <Link
                to="/login"
                className="bg-primary-600 text-white px-6 py-2 rounded-md hover:bg-primary-700 transition-colors"
              >
                Sign In
              </Link>
            </div>
          )}

          {/* Comments List */}
          <CommentList
            comments={comments}
            onDeleteComment={handleDeleteComment}
            currentUser={user}
          />
        </section>
      </article>
    </div>
  );
};

export default PostDetailPage;
