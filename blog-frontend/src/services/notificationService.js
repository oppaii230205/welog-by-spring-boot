import api from "./api";

export const notificationService = {
  // Get all notifications for the current user
  getUserNotifications: (userId) => api.get(`/users/${userId}/notifications`),

  // FINISH: Mark notification as read (if we implement this later)
  markAsRead: (notificationId) =>
    api.patch(`/notifications/${notificationId}/read`),

  // FINISH: Mark all notifications as read (if we implement this later)
  markAllAsRead: (userId) =>
    api.patch(`/users/${userId}/notifications/read-all`),

  // Get unread count (if we implement this later) (can derive from notifications list in frontend)
  getUnreadCount: (userId) =>
    api.get(`/users/${userId}/notifications/unread-count`),
};
