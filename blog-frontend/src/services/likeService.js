import api from "./api";

export const likeService = {
  // Like a post
  likePost: (postId, userId) =>
    api.post(`/posts/${postId}/likes?userId=${userId}`),

  // FINISH: Unlike a post (if we implement this later)
  unlikePost: (postId, userId) =>
    api.delete(`/posts/${postId}/likes?userId=${userId}`),

  // Get all users who liked a post
  getPostLikes: (postId) => api.get(`/posts/${postId}/likes`),

  // Check if user has liked a post (utility method)
  hasUserLiked: async (postId, userId) => {
    try {
      const response = await api.get(`/posts/${postId}/likes`);
      return response.data.some((user) => user.id === userId);
    } catch (error) {
      console.error("Error checking like status:", error);
      return false;
    }
  },
};
