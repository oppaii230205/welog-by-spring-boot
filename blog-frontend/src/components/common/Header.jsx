import React, { useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import {
  LogOut,
  User,
  Plus,
  ChevronDown,
  Settings,
  BookOpen,
} from "lucide-react";
import NotificationDropdown from "./NotificationDropdown";
import SearchBar from "./SearchBar";
import API_URL from "../../config";

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const userMenuRef = useRef(null);

  // Close user menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setIsUserMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/login");
    setIsUserMenuOpen(false);
  };

  return (
    <header className="bg-white/95 backdrop-blur-sm shadow-sm border-b border-gray-200 sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Left: Logo */}
          <div className="flex-shrink-0">
            <Link
              to="/"
              className="flex items-center text-2xl font-bold bg-gradient-to-r from-primary-600 to-blue-600 bg-clip-text text-transparent hover:from-primary-700 hover:to-blue-700 transition-all duration-200"
            >
              <BookOpen size={28} className="mr-2 text-primary-600" />
              Welog
            </Link>
          </div>

          {/* Center: Navigation & Search */}
          <div className="hidden lg:flex items-center space-x-8 flex-1 justify-center max-w-2xl">
            {/* Navigation */}
            <nav className="flex items-center space-x-6">
              <Link
                to="/posts"
                className="text-gray-700 hover:text-primary-600 font-medium transition-colors duration-200 relative group"
              >
                Posts
                <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-primary-600 group-hover:w-full transition-all duration-200"></span>
              </Link>

              {isAuthenticated && (
                <Link
                  to="/create-post"
                  className="flex items-center bg-gradient-to-r from-primary-600 to-blue-600 hover:from-primary-700 hover:to-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-all duration-200 shadow-sm hover:shadow-md transform hover:-translate-y-0.5"
                >
                  <Plus size={16} className="mr-2" />
                  New Post
                </Link>
              )}
            </nav>

            {/* Search Bar */}
            <div className="flex-1 max-w-md">
              <SearchBar />
            </div>
          </div>

          {/* Mobile Navigation & Search */}
          <div className="lg:hidden flex-1 mx-4">
            <SearchBar />
          </div>

          {/* Right: User Menu & Notifications */}
          <div className="flex items-center space-x-3">
            {isAuthenticated ? (
              <>
                {/* Notifications */}
                <NotificationDropdown />

                {/* User Menu */}
                <div className="relative" ref={userMenuRef}>
                  <button
                    onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
                    className="flex items-center space-x-2 text-gray-700 hover:text-gray-900 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 rounded-lg p-1"
                  >
                    <div className="flex items-center space-x-2">
                      {user?.photo ? (
                        <img
                          src={`${API_URL}/img/users/${user.photo}`}
                          alt={user.name}
                          className="w-8 h-8 rounded-full object-cover border-2 border-white shadow-sm"
                        />
                      ) : (
                        <div className="w-8 h-8 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center border-2 border-white shadow-sm">
                          <span className="text-white text-sm font-medium">
                            {user?.name?.charAt(0)?.toUpperCase() || "U"}
                          </span>
                        </div>
                      )}
                      <span className="hidden md:block font-medium text-sm">
                        {user?.name}
                      </span>
                      <ChevronDown
                        size={16}
                        className={`transition-transform duration-200 ${
                          isUserMenuOpen ? "rotate-180" : ""
                        }`}
                      />
                    </div>
                  </button>

                  {/* User Dropdown Menu */}
                  {isUserMenuOpen && (
                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-200 py-1 z-50">
                      <Link
                        to="/profile"
                        onClick={() => setIsUserMenuOpen(false)}
                        className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 hover:text-primary-600 transition-colors duration-200"
                      >
                        <User size={16} className="mr-3" />
                        My Profile
                      </Link>

                      <Link
                        to="/settings"
                        onClick={() => setIsUserMenuOpen(false)}
                        className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 hover:text-primary-600 transition-colors duration-200"
                      >
                        <Settings size={16} className="mr-3" />
                        Settings
                      </Link>

                      <hr className="my-1 border-gray-200" />

                      <button
                        onClick={handleLogout}
                        className="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors duration-200"
                      >
                        <LogOut size={16} className="mr-3" />
                        Sign Out
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <div className="flex items-center space-x-4">
                <Link
                  to="/login"
                  className="text-gray-700 hover:text-primary-600 font-medium transition-colors duration-200"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-gradient-to-r from-primary-600 to-blue-600 hover:from-primary-700 hover:to-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-all duration-200 shadow-sm hover:shadow-md transform hover:-translate-y-0.5"
                >
                  Sign Up
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
