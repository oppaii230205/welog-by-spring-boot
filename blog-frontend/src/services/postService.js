import api from "./api";

export const postService = {
  getAll: (page = 0, size = 10) => api.get(`/posts?page=${page}&size=${size}`),
  getById: (id) => api.get(`/posts/${id}`),
  create: (postData) => api.post("/posts", postData),
  update: (id, postData) => api.patch(`/posts/${id}`, postData),
  delete: (id) => api.delete(`/posts/${id}`),
};
