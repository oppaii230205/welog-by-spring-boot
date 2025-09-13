import api from "./api";

export const commentService = {
  getByPostId: (postId) => api.get(`/posts/${postId}/comments`),
  create: (commentData) => api.post("/comments", commentData),
  update: (id, commentData) => api.patch(`/comments/${id}`, commentData),
  delete: (id) => api.delete(`/comments/${id}`),
};
