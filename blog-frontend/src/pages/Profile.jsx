import React, { useState, useEffect, useCallback } from "react";
import { useAuth } from "../context/AuthContext";
import { userService } from "../services/userService";
import { postService } from "../services/postService";
import LoadingSpinner from "../components/common/LoadingSpinner";
import ProfileStats from "../components/profile/ProfileStats";
import EditProfileModal from "../components/profile/EditProfileModal";
import PostCard from "../components/posts/PostCard";
import {
  Edit,
  Settings,
  Shield,
  Calendar,
  Mail,
  User as UserIcon,
} from "lucide-react";
import API_URL from "../config";

const Profile = () => {
  const { user, updateUser } = useAuth();
  const [profileData, setProfileData] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [userStats, setUserStats] = useState({});
  const [loading, setLoading] = useState(true);
  const [postsLoading, setPostsLoading] = useState(true);
  const [error, setError] = useState("");
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [updateLoading, setUpdateLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("posts");

  const fetchProfileData = useCallback(async () => {
    try {
      setLoading(true);
      const response = await userService.getUserById(user.id);
      setProfileData(response.data);
    } catch (err) {
      setError("Failed to fetch profile data");
      console.error("Error fetching profile:", err);
    } finally {
      setLoading(false);
    }
  }, [user]);

  const fetchUserPosts = useCallback(async () => {
    try {
      setPostsLoading(true);
      // This would need to be implemented in your backend to filter by user
      const response = await postService.getAll();
      // Filter posts by current user (assuming posts have author info)
      const filteredPosts = response.data.filter(
        (post) => post.author?.id === user.id
      );
      setUserPosts(filteredPosts);
    } catch (err) {
      console.error("Error fetching user posts:", err);
    } finally {
      setPostsLoading(false);
    }
  }, [user]);

  const fetchUserStats = useCallback(async () => {
    try {
      // Mock stats - you would implement these endpoints in your backend
      const stats = {
        totalPosts: userPosts.length,
        totalComments: 0, // Would come from API
        totalViews: 0, // Would come from API
        joinDate: profileData?.createdAt || user?.createdAt,
      };
      setUserStats(stats);
    } catch (err) {
      console.error("Error fetching user stats:", err);
    }
  }, [userPosts.length, profileData?.createdAt, user?.createdAt]);

  useEffect(() => {
    if (user?.id) {
      fetchProfileData();
      fetchUserPosts();
      fetchUserStats();
    }
  }, [user, fetchProfileData, fetchUserPosts, fetchUserStats]);

  const handleUpdateProfile = async (updateData, isFormData = false) => {
    try {
      setUpdateLoading(true);

      let response;
      if (isFormData) {
        // Handle file upload
        response = await userService.updateMe(updateData);
      } else {
        // Handle regular JSON update
        response = await userService.updateUser(user.id, updateData);
      }

      setProfileData(response.data);

      // Update the auth context with new user data
      const updatedUser = { ...user, ...response.data };
      updateUser(updatedUser);

      setIsEditModalOpen(false);
    } catch (err) {
      console.error("Error updating profile:", err);
      throw err;
    } finally {
      setUpdateLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "Unknown";
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const getAvatarSrc = () => {
    if (profileData?.photo && profileData.photo !== "default.png") {
      return profileData.photo.includes("http")
        ? profileData.photo
        : `${API_URL}/img/users/${profileData.photo}`;
    }
    return null;
  };

  const tabs = [
    { id: "posts", label: "Posts", count: userPosts.length },
    { id: "about", label: "About", count: null },
  ];

  if (loading) return <LoadingSpinner />;
  if (error)
    return <div className="text-center text-red-600 py-8">{error}</div>;

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Profile Header */}
        <div className="bg-white rounded-lg shadow-sm border overflow-hidden mb-8">
          {/* Cover Photo */}
          <div className="h-48 bg-gradient-to-r from-primary-500 to-primary-700"></div>

          {/* Profile Info */}
          <div className="relative px-6 pb-6">
            <div className="flex flex-col sm:flex-row sm:items-end sm:space-x-6">
              {/* Avatar */}
              <div className="relative -mt-16 mb-4 sm:mb-0">
                <div className="w-32 h-32 rounded-full overflow-hidden bg-white p-2 shadow-lg">
                  {getAvatarSrc() ? (
                    <img
                      src={getAvatarSrc()}
                      alt={profileData?.name}
                      className="w-full h-full rounded-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full rounded-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center">
                      <span className="text-white text-3xl font-bold">
                        {profileData?.name?.charAt(0)?.toUpperCase() || "U"}
                      </span>
                    </div>
                  )}
                </div>
              </div>

              {/* User Info */}
              <div className="flex-1 sm:pb-4">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
                  <div>
                    <h1 className="text-2xl font-bold text-gray-900">
                      {profileData?.name || "User"}
                    </h1>
                    <div className="flex items-center text-gray-600 mt-1">
                      <Mail size={16} className="mr-2" />
                      <span>{profileData?.email}</span>
                    </div>
                    <div className="flex items-center text-gray-500 mt-1">
                      <Calendar size={16} className="mr-2" />
                      <span>Joined {formatDate(profileData?.createdAt)}</span>
                    </div>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex space-x-3 mt-4 sm:mt-0">
                    <button
                      onClick={() => setIsEditModalOpen(true)}
                      className="flex items-center px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
                    >
                      <Edit size={16} className="mr-2" />
                      Edit Profile
                    </button>
                    <button className="flex items-center px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors">
                      <Settings size={16} className="mr-2" />
                      Settings
                    </button>
                  </div>
                </div>

                {/* User Roles */}
                {profileData?.roles && profileData.roles.length > 0 && (
                  <div className="flex items-center mt-3">
                    <Shield size={16} className="mr-2 text-gray-500" />
                    <div className="flex space-x-2">
                      {profileData.roles.map((role) => (
                        <span
                          key={role}
                          className="px-2 py-1 text-xs font-medium bg-primary-100 text-primary-700 rounded-full"
                        >
                          {role.replace("ROLE_", "")}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="mb-8">
          <ProfileStats stats={userStats} />
        </div>

        {/* Tabs */}
        <div className="bg-white rounded-lg shadow-sm border">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8 px-6">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`py-4 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                    activeTab === tab.id
                      ? "border-primary-500 text-primary-600"
                      : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                  }`}
                >
                  {tab.label}
                  {tab.count !== null && (
                    <span className="ml-2 bg-gray-100 text-gray-900 py-0.5 px-2 rounded-full text-xs">
                      {tab.count}
                    </span>
                  )}
                </button>
              ))}
            </nav>
          </div>

          {/* Tab Content */}
          <div className="p-6">
            {activeTab === "posts" && (
              <div>
                {postsLoading ? (
                  <LoadingSpinner />
                ) : userPosts.length > 0 ? (
                  <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                    {userPosts.map((post) => (
                      <PostCard key={post.id} post={post} />
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-12">
                    <UserIcon
                      size={48}
                      className="mx-auto text-gray-400 mb-4"
                    />
                    <h3 className="text-lg font-medium text-gray-900 mb-2">
                      No posts yet
                    </h3>
                    <p className="text-gray-500">
                      Start sharing your thoughts with the world!
                    </p>
                  </div>
                )}
              </div>
            )}

            {activeTab === "about" && (
              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-medium text-gray-900 mb-4">
                    About {profileData?.name}
                  </h3>
                  <div className="bg-gray-50 rounded-lg p-4">
                    <p className="text-gray-600">
                      This user hasn't added a bio yet.
                    </p>
                  </div>
                </div>

                <div>
                  <h4 className="text-md font-medium text-gray-900 mb-3">
                    Account Information
                  </h4>
                  <dl className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        Member since
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {formatDate(profileData?.createdAt)}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        Last updated
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {formatDate(profileData?.updatedAt)}
                      </dd>
                    </div>
                  </dl>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Edit Profile Modal */}
      <EditProfileModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        user={profileData}
        onSave={handleUpdateProfile}
        isLoading={updateLoading}
      />
    </div>
  );
};

export default Profile;
