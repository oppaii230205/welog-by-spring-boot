import api from "./api";

export const authService = {
  login: (credentials) => api.post("/auth/signin", credentials),
  register: (userData) => api.post("/auth/signup", userData),
};
