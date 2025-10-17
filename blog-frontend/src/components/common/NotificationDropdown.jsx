import React, { useState, useEffect, useRef, useCallback } from "react";
import { Bell, Heart, MessageCircle, User, X, Dot } from "lucide-react";
import { notificationService } from "../../services/notificationService";
import { useAuth } from "../../context/AuthContext";

import API_URL from "../../config";

const NotificationDropdown = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const dropdownRef = useRef(null);
  const { user } = useAuth();

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Fetch notifications when dropdown opens
  const fetchNotifications = useCallback(async () => {
    if (!user) return;

    try {
      setLoading(true);
      const response = await notificationService.getUserNotifications(user.id);
      setNotifications(response.data);
      // Count unread notifications (assuming we have isRead field)
      const unread = response.data.filter((notif) => !notif.read).length;
      setUnreadCount(unread);
    } catch (error) {
      console.error("Error fetching notifications:", error);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    if (/*isOpen &&*/ user) {
      fetchNotifications();
    }
  }, [isOpen, user, fetchNotifications]);

  const markAllAsRead = async () => {
    if (!user) return;

    try {
      await notificationService.markAllAsRead(user.id);
      setNotifications((prev) =>
        prev.map((notif) => ({ ...notif, read: true }))
      );
      setUnreadCount(0);
    } catch (error) {
      console.error("Error marking notifications as read:", error);
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case "LIKE":
        return <Heart size={16} className="text-red-500" />;
      case "COMMENT":
        return <MessageCircle size={16} className="text-blue-500" />;
      case "FOLLOW":
        return <User size={16} className="text-green-500" />;
      default:
        return <Bell size={16} className="text-gray-500" />;
    }
  };

  const formatNotificationTime = (createdAt) => {
    const now = new Date();
    const notificationDate = new Date(createdAt);
    const diffInHours = Math.floor((now - notificationDate) / (1000 * 60 * 60));

    if (diffInHours < 1) return "Just now";
    if (diffInHours < 24) return `${diffInHours}h ago`;
    if (diffInHours < 48) return "1 day ago";
    return `${Math.floor(diffInHours / 24)} days ago`;
  };

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
  };

  return (
    <div className="relative" ref={dropdownRef}>
      {/* Notification Bell Button */}
      <button
        onClick={toggleDropdown}
        className="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2"
        aria-label="Notifications"
      >
        <Bell size={20} />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full min-w-[18px] h-[18px] flex items-center justify-center font-medium animate-pulse">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown Menu */}
      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-lg border border-gray-200 z-50 max-h-96 overflow-hidden">
          {/* Header */}
          <div className="px-4 py-3 border-b border-gray-100 bg-gradient-to-r from-primary-50 to-blue-50">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-semibold text-gray-900">
                Notifications
              </h3>
              <button
                onClick={() => setIsOpen(false)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X size={16} />
              </button>
            </div>
            {unreadCount > 0 && (
              <div className="flex items-center justify-between mt-1">
                <span className="text-xs text-gray-600">
                  {unreadCount} new notification{unreadCount !== 1 ? "s" : ""}
                </span>
                <button
                  onClick={markAllAsRead}
                  className="text-xs text-gray-500 hover:text-gray-800 transition-colors cursor-pointer"
                >
                  Mark all as read
                </button>
              </div>
            )}
          </div>

          {/* Notifications List */}
          <div className="max-h-64 overflow-y-auto">
            {loading ? (
              <div className="p-4">
                {[...Array(3)].map((_, i) => (
                  <div
                    key={i}
                    className="flex items-start space-x-3 p-3 animate-pulse"
                  >
                    <div className="w-8 h-8 bg-gray-300 rounded-full"></div>
                    <div className="flex-1 space-y-2">
                      <div className="h-3 bg-gray-300 rounded w-3/4"></div>
                      <div className="h-2 bg-gray-300 rounded w-1/2"></div>
                    </div>
                  </div>
                ))}
              </div>
            ) : notifications.length === 0 ? (
              <div className="p-6 text-center">
                <Bell size={32} className="mx-auto text-gray-400 mb-2" />
                <p className="text-sm text-gray-600">No notifications yet</p>
                <p className="text-xs text-gray-500 mt-1">
                  You'll see notifications here when people interact with your
                  posts
                </p>
              </div>
            ) : (
              <div className="divide-y divide-gray-100">
                {notifications.map((notification) => (
                  <div
                    key={notification.id}
                    className={`p-3 hover:bg-gray-50 transition-colors cursor-pointer ${
                      !notification.read
                        ? "bg-blue-50 border-l-4 border-l-blue-500"
                        : ""
                    }`}
                  >
                    <div className="flex items-start space-x-3">
                      {/* Sender Avatar */}
                      <div className="relative flex-shrink-0">
                        {notification.sender?.photo ? (
                          <img
                            src={
                              notification.sender.photo.includes("http")
                                ? notification.sender.photo
                                : `${API_URL}/img/users/${notification.sender.photo}`
                            }
                            alt={notification.sender.name}
                            className="w-8 h-8 rounded-full object-cover"
                          />
                        ) : (
                          <div className="w-8 h-8 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center">
                            <span className="text-white text-xs font-medium">
                              {notification.sender?.name
                                ?.charAt(0)
                                ?.toUpperCase() || "U"}
                            </span>
                          </div>
                        )}

                        {/* Type icon overlay */}
                        <div className="absolute -bottom-1 -right-1 bg-white rounded-full p-0.5">
                          {getNotificationIcon(notification.type)}
                        </div>
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <p className="text-sm text-gray-900 line-clamp-2">
                          {notification.message}
                        </p>
                        <div className="flex items-center mt-1 space-x-2">
                          <span className="text-xs text-gray-500">
                            {formatNotificationTime(notification.createdAt)}
                          </span>
                          {!notification.read && (
                            <Dot className="text-blue-500" size={16} />
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Footer */}
          {notifications.length > 0 && (
            <div className="px-4 py-3 border-t border-gray-100 bg-gray-50">
              <button className="w-full text-sm text-primary-600 hover:text-primary-700 font-medium transition-colors">
                View all notifications
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default NotificationDropdown;
