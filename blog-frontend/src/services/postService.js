import api from "./api";

export const postService = {
  getAll: (page = 0, size = 20) => api.get(`/posts?page=${page}&size=${size}`),
  getById: (id) => api.get(`/posts/${id}`),
  create: (postData) => {
    // Check if postData is FormData (for file upload)
    if (postData instanceof FormData) {
      return api.post("/posts", postData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
    }
    // Regular JSON request
    return api.post("/posts", postData);
  },
  update: (id, postData) => {
    // Check if postData is FormData (for file upload)
    if (postData instanceof FormData) {
      return api.patch(`/posts/${id}`, postData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
    }
    // Regular JSON request
    return api.patch(`/posts/${id}`, postData);
  },
  uploadCoverImage: (id, imageFile) => {
    const formData = new FormData();
    formData.append("coverImage", imageFile);
    return api.post(`/posts/${id}/coverImage`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
  delete: (id) => api.delete(`/posts/${id}`),
};
