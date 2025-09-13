import React from "react";
import { FileText, MessageCircle, Calendar, Eye } from "lucide-react";

const ProfileStats = ({ stats }) => {
  const defaultStats = {
    totalPosts: 0,
    totalComments: 0,
    totalViews: 0,
    joinDate: new Date().toISOString(),
    ...stats,
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const statItems = [
    {
      icon: FileText,
      label: "Posts",
      value: defaultStats.totalPosts,
      color: "text-blue-600",
      bgColor: "bg-blue-100",
    },
    {
      icon: MessageCircle,
      label: "Comments",
      value: defaultStats.totalComments,
      color: "text-green-600",
      bgColor: "bg-green-100",
    },
    {
      icon: Eye,
      label: "Views",
      value: defaultStats.totalViews,
      color: "text-purple-600",
      bgColor: "bg-purple-100",
    },
  ];

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Statistics</h3>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        {statItems.map((stat) => {
          const IconComponent = stat.icon;
          return (
            <div
              key={stat.label}
              className="flex items-center p-4 bg-gray-50 rounded-lg"
            >
              <div className={`p-3 rounded-full ${stat.bgColor} mr-4`}>
                <IconComponent size={24} className={stat.color} />
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
                <p className="text-sm text-gray-600">{stat.label}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Join Date */}
      <div className="flex items-center text-sm text-gray-600">
        <Calendar size={16} className="mr-2" />
        <span>Joined {formatDate(defaultStats.joinDate)}</span>
      </div>
    </div>
  );
};

export default ProfileStats;
