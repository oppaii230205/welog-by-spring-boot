import axios from "axios";

const API_BASE_URL = import.meta.env
  .VITE_API_URL; /*|| "http://localhost:8080/api/v1"*/

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add auth token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle auth errors - FIXED: Only redirect on 401 for protected routes, not login attempts
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const originalRequest = error.config;

    // Only redirect to login if it's NOT a login request and we get 401
    if (
      error.response?.status === 401 &&
      !originalRequest.url.includes("/auth/signin") &&
      !originalRequest.url.includes("/auth/signup")
    ) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  }
);

export default api;
