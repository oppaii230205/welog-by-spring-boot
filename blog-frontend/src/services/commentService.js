import api from "./api";

export const commentService = {
  getByPostId: (postId) => api.get(`/posts/${postId}/comments`),
  getRootCommentsByPostId: (postId) =>
    api.get(`/posts/${postId}/root-comments`),
  get: (id) => api.get(`/comments/${id}`),
  create: (commentData) => api.post("/comments", commentData),
  createByPostId: (postId, commentData) =>
    api.post(`/posts/${postId}/comments`, commentData),
  update: (id, commentData) => api.patch(`/comments/${id}`, commentData),
  delete: (id) => api.delete(`/comments/${id}`),
};
