import api from "./api";

export const userService = {
  getUserById: (id) => api.get(`/users/${id}`),
  updateUser: (id, userData) => api.patch(`/users/${id}`, userData),
  updateMe: (formData) =>
    api.patch("/users/updateMe", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }),
  getAllUsers: (page = 0, size = 10) =>
    api.get(`/users?page=${page}&size=${size}`),
  deleteUser: (id) => api.delete(`/users/${id}`),
};
