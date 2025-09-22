import React, { useState, useEffect } from "react";
import { Heart } from "lucide-react";
import { likeService } from "../../services/likeService";
import { useAuth } from "../../context/AuthContext";

const LikeButton = ({
  postId,
  initialLikeCount = 0,
  initialIsLiked = false,
  onLikeChange,
}) => {
  const [isLiked, setIsLiked] = useState(initialIsLiked);
  const [likeCount, setLikeCount] = useState(initialLikeCount);
  const [isLoading, setIsLoading] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const { user, isAuthenticated } = useAuth();

  // Check if user has liked this post on component mount
  useEffect(() => {
    const checkLikeStatus = async () => {
      if (isAuthenticated && user && postId) {
        try {
          const hasLiked = await likeService.hasUserLiked(postId, user.id);
          setIsLiked(hasLiked);
        } catch (error) {
          console.error("Error checking like status:", error);
        }
      }
    };

    checkLikeStatus();
  }, [postId, user, isAuthenticated]);

  // Fetch current like count
  useEffect(() => {
    const fetchLikeCount = async () => {
      if (postId) {
        try {
          const response = await likeService.getPostLikes(postId);
          setLikeCount(response.data.length);
        } catch (error) {
          console.error("Error fetching like count:", error);
        }
      }
    };

    fetchLikeCount();
  }, [postId]);

  const handleLike = async () => {
    if (!isAuthenticated) {
      // Could show a login modal or redirect to login
      alert("Please log in to like posts");
      return;
    }

    if (isLoading) return;

    try {
      setIsLoading(true);
      setIsAnimating(true);

      if (!isLiked) {
        // Like the post
        await likeService.likePost(postId, user.id);
        setIsLiked(true);
        setLikeCount((prev) => prev + 1);

        // Trigger animation
        setTimeout(() => setIsAnimating(false), 600);

        // Notify parent component if callback provided
        if (onLikeChange) {
          onLikeChange(true, likeCount + 1);
        }
      } else {
        // Unlike functionality would go here if implemented in backend
        await likeService.unlikePost(postId, user.id);
        setIsLiked(false);
        setLikeCount((prev) => Math.max(0, prev - 1));

        // Trigger animation
        setTimeout(() => setIsAnimating(false), 600);

        // Notify parent component if callback provided
        if (onLikeChange) {
          onLikeChange(false, likeCount - 1);
        }
      }
    } catch (error) {
      console.error("Error liking post:", error);
      // Revert optimistic update on error
      if (!isLiked) {
        setIsLiked(false);
        setLikeCount((prev) => Math.max(0, prev - 1));
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onClick={handleLike}
      disabled={isLoading}
      className={`flex items-center space-x-2 px-3 py-2 rounded-lg transition-all duration-200 group ${
        isLiked
          ? "text-red-500 bg-red-50 hover:bg-red-100"
          : "text-gray-600 hover:text-red-500 hover:bg-red-50"
      } ${isLoading ? "opacity-50 cursor-not-allowed" : "cursor-pointer"}`}
    >
      <div className="relative">
        <Heart
          size={18}
          className={`transition-all duration-200 ${
            isLiked ? "fill-current text-red-500" : "group-hover:fill-current"
          } ${isAnimating ? "animate-pulse scale-125" : ""}`}
        />

        {/* Animation hearts */}
        {isAnimating && (
          <>
            <Heart
              size={12}
              className="absolute -top-1 -left-1 fill-current text-red-400 animate-ping"
            />
            <Heart
              size={14}
              className="absolute -top-2 -right-1 fill-current text-red-300 animate-bounce"
            />
          </>
        )}
      </div>

      <span
        className={`text-sm font-medium transition-colors duration-200 ${
          isLiked ? "text-red-600" : "text-gray-600 group-hover:text-red-600"
        }`}
      >
        {likeCount}
      </span>

      {/* Loading spinner */}
      {isLoading && (
        <div className="w-3 h-3 border border-current border-t-transparent rounded-full animate-spin opacity-50" />
      )}
    </button>
  );
};

export default LikeButton;
