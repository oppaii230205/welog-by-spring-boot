import React, { useState, useRef, useEffect, useCallback } from "react";
import { Search, X, Clock, TrendingUp } from "lucide-react";
import { postService } from "../../services/postService";
import { useNavigate } from "react-router-dom";

import API_URL from "../../config";

const SearchBar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [recentSearches, setRecentSearches] = useState([]);
  const searchRef = useRef(null);
  const navigate = useNavigate();

  // Close search when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Load recent searches from localStorage
  useEffect(() => {
    const saved = localStorage.getItem("recentSearches");
    if (saved) {
      setRecentSearches(JSON.parse(saved));
    }
  }, []);

  const performSearch = useCallback(async () => {
    try {
      setLoading(true);
      const response = await postService.searchPosts(searchQuery, 0, 5);
      setSearchResults(response.data.content);
    } catch (error) {
      console.error("Search error:", error);
      setSearchResults([]);
    } finally {
      setLoading(false);
    }
  }, [searchQuery]);

  // Debounced search function
  useEffect(() => {
    const delayedSearch = setTimeout(() => {
      if (searchQuery.trim().length >= 2) {
        performSearch();
      } else {
        setSearchResults([]);
      }
    }, 300);

    return () => clearTimeout(delayedSearch);
  }, [searchQuery, performSearch]);

  const handleSearch = (query = searchQuery) => {
    if (!query.trim()) return;

    // Save to recent searches
    const updatedRecent = [
      query,
      ...recentSearches.filter((item) => item !== query),
    ].slice(0, 5);
    setRecentSearches(updatedRecent);
    localStorage.setItem("recentSearches", JSON.stringify(updatedRecent));

    // Navigate to search results
    navigate(`/posts?search=${encodeURIComponent(query)}`);
    setIsOpen(false);
    setSearchQuery("");
  };

  const handleResultClick = (post) => {
    navigate(`/posts/${post.id}`);
    setIsOpen(false);
    setSearchQuery("");
  };

  const clearRecentSearches = () => {
    setRecentSearches([]);
    localStorage.removeItem("recentSearches");
  };

  const truncateText = (text, maxLength = 100) => {
    return text.length > maxLength
      ? text.substring(0, maxLength) + "..."
      : text;
  };

  return (
    <div className="relative" ref={searchRef}>
      {/* Search Input */}
      <div className="relative">
        <div
          className={`relative flex items-center transition-all duration-300 ${
            isOpen
              ? "w-80 bg-white border-2 border-primary-500 shadow-lg"
              : "w-64 bg-gray-50 hover:bg-gray-100 border border-gray-200"
          } rounded-full overflow-hidden`}
        >
          <Search
            size={18}
            className={`ml-3 transition-colors duration-200 ${
              isOpen ? "text-primary-600" : "text-gray-400"
            }`}
          />
          <input
            type="text"
            placeholder="Search posts..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onFocus={() => setIsOpen(true)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handleSearch();
              }
              if (e.key === "Escape") {
                setIsOpen(false);
                setSearchQuery("");
              }
            }}
            className="w-full px-3 py-2 text-sm bg-transparent border-none outline-none placeholder-gray-500"
          />
          {searchQuery && (
            <button
              onClick={() => {
                setSearchQuery("");
                setSearchResults([]);
              }}
              className="mr-3 text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X size={16} />
            </button>
          )}
        </div>
      </div>

      {/* Search Dropdown */}
      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-xl border border-gray-200 z-50 max-h-96 overflow-hidden">
          {/* Search Results */}
          {searchQuery.trim() && (
            <div className="border-b border-gray-100">
              <div className="px-4 py-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Search Results
              </div>

              {loading ? (
                <div className="p-4">
                  <div className="flex items-center space-x-3 animate-pulse">
                    <div className="w-12 h-12 bg-gray-200 rounded"></div>
                    <div className="flex-1">
                      <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                      <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                    </div>
                  </div>
                </div>
              ) : searchResults.length > 0 ? (
                <div className="max-h-60 overflow-y-auto">
                  {searchResults.map((post) => (
                    <div
                      key={post.id}
                      onClick={() => handleResultClick(post)}
                      className="flex items-start p-4 hover:bg-gray-50 cursor-pointer transition-colors"
                    >
                      {post.coverImage && (
                        <img
                          src={`${API_URL}/img/posts/${post.coverImage}`}
                          alt={post.title}
                          className="w-12 h-12 rounded object-cover mr-3 flex-shrink-0"
                        />
                      )}
                      <div className="flex-1 min-w-0">
                        <h4 className="text-sm font-medium text-gray-900 line-clamp-1 mb-1">
                          {post.title}
                        </h4>
                        <p className="text-xs text-gray-500 line-clamp-2">
                          {truncateText(post.content || post.excerpt || "")}
                        </p>
                        <div className="flex items-center mt-1 text-xs text-gray-400">
                          <span>{post.author?.name}</span>
                          <span className="mx-1">•</span>
                          <span>
                            {new Date(post.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : searchQuery.length >= 2 ? (
                <div className="p-4 text-center text-gray-500">
                  <Search size={32} className="mx-auto mb-2 text-gray-300" />
                  <p className="text-sm">No posts found for "{searchQuery}"</p>
                  <p className="text-xs mt-1">Try different keywords</p>
                </div>
              ) : (
                <div className="p-4 text-center text-gray-500">
                  <p className="text-sm">
                    Type at least 2 characters to search
                  </p>
                </div>
              )}

              {searchQuery.trim() && searchResults.length > 0 && (
                <div className="border-t border-gray-100 p-3">
                  <button
                    onClick={() => handleSearch()}
                    className="w-full text-sm text-primary-600 hover:text-primary-700 font-medium transition-colors"
                  >
                    View all results for "{searchQuery}"
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Recent Searches */}
          {!searchQuery.trim() && recentSearches.length > 0 && (
            <div>
              <div className="flex items-center justify-between px-4 py-2 border-b border-gray-100">
                <div className="text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Recent Searches
                </div>
                <button
                  onClick={clearRecentSearches}
                  className="text-xs text-gray-400 hover:text-gray-600 transition-colors"
                >
                  Clear
                </button>
              </div>
              <div className="max-h-32 overflow-y-auto">
                {recentSearches.map((search, index) => (
                  <div
                    key={index}
                    onClick={() => handleSearch(search)}
                    className="flex items-center px-4 py-2 hover:bg-gray-50 cursor-pointer transition-colors"
                  >
                    <Clock size={14} className="text-gray-400 mr-3" />
                    <span className="text-sm text-gray-700">{search}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Empty State */}
          {!searchQuery.trim() && recentSearches.length === 0 && (
            <div className="p-6 text-center">
              <TrendingUp size={32} className="mx-auto mb-2 text-gray-300" />
              <p className="text-sm text-gray-500 mb-1">
                Discover amazing content
              </p>
              <p className="text-xs text-gray-400">
                Search for posts, topics, or authors
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default SearchBar;
