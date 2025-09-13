import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { LogOut, User, Plus } from "lucide-react";
import API_URL from "../../config";

const Header = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <Link to="/" className="text-xl font-bold text-gray-900">
            Welog
          </Link>

          <nav className="flex items-center space-x-4">
            <Link to="/posts" className="text-gray-600 hover:text-gray-900">
              Posts
            </Link>

            {isAuthenticated ? (
              <div className="flex items-center space-x-4">
                <Link
                  to="/create-post"
                  className="flex items-center text-sm bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700"
                >
                  <Plus size={16} className="mr-1" />
                  New Post
                </Link>
                <Link
                  to="/profile"
                  className="flex items-center text-gray-600 hover:text-gray-900"
                >
                  {/* {console.log(user)} */}
                  <img
                    src={`${API_URL}/img/users/${user?.photo}`}
                    alt={user?.name}
                    className="w-8 h-8 rounded-full mr-1"
                  />
                  {user.name}
                </Link>
                <button
                  onClick={handleLogout}
                  className="flex items-center text-gray-600 hover:text-gray-900"
                >
                  <LogOut size={18} />
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link to="/login" className="text-gray-600 hover:text-gray-900">
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700"
                >
                  Sign Up
                </Link>
              </div>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;
