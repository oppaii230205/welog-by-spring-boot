import React, { useState, useRef } from "react";
import { Camera, Upload, X } from "lucide-react";
import API_URL from "../../config";

const AvatarUpload = ({ currentAvatar, onAvatarChange, isUploading }) => {
  const [previewUrl, setPreviewUrl] = useState(null);
  const [dragActive, setDragActive] = useState(false);
  const fileInputRef = useRef(null);

  const handleFileSelect = (file) => {
    if (file && file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        // console.log(e.target.result);
        setPreviewUrl(e.target.result);
      };
      reader.readAsDataURL(file);

      onAvatarChange(file);
    }
  };

  const handleFileInputChange = (e) => {
    const file = e.target.files[0];
    handleFileSelect(file);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragActive(false);

    const file = e.dataTransfer.files[0];
    handleFileSelect(file);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragActive(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setDragActive(false);
  };

  const clearPreview = () => {
    setPreviewUrl(null);
    onAvatarChange(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const openFileDialog = (e) => {
    e?.preventDefault(); // Key
    e?.stopPropagation();
    // Add a small delay to prevent modal close
    setTimeout(() => {
      fileInputRef.current?.click();
    }, 100);
  };

  const getAvatarSrc = () => {
    if (previewUrl) return previewUrl;
    if (currentAvatar && currentAvatar !== "default.png") {
      return `${API_URL}/img/users/${currentAvatar}`;
    }
    return null;
  };

  return (
    <div
      className="flex flex-col items-center space-y-4"
      onClick={(e) => e.stopPropagation()}
    >
      {/* Avatar Display */}
      <div className="relative">
        <div className="w-32 h-32 rounded-full overflow-hidden bg-gray-200 border-4 border-white shadow-lg">
          {getAvatarSrc() ? (
            <img
              src={getAvatarSrc()}
              alt="Avatar"
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full bg-gradient-to-br from-primary-400 to-primary-600 flex items-center justify-center">
              <span className="text-white text-3xl font-bold">
                {currentAvatar?.charAt(0)?.toUpperCase() || "U"}
              </span>
            </div>
          )}
        </div>

        {/* Camera Icon Button */}
        <button
          type="button" // Add type="button" to prevent form submission
          onClick={openFileDialog}
          disabled={isUploading}
          className="absolute bottom-0 right-0 bg-primary-600 hover:bg-primary-700 text-white p-2 rounded-full shadow-lg transition-colors disabled:opacity-50"
        >
          <Camera size={20} />
        </button>

        {/* Clear Preview Button */}
        {previewUrl && (
          <button
            onClick={clearPreview}
            className="absolute top-0 right-0 bg-red-500 hover:bg-red-600 text-white p-1 rounded-full shadow-lg transition-colors"
          >
            <X size={16} />
          </button>
        )}
      </div>

      {/* Drag & Drop Area */}
      <div
        className={`w-full max-w-md border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
          dragActive
            ? "border-primary-500 bg-primary-50"
            : "border-gray-300 hover:border-gray-400"
        }`}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
      >
        <Upload className="mx-auto h-12 w-12 text-gray-400 mb-4" />
        <div className="text-sm text-gray-600">
          <button
            onClick={openFileDialog}
            disabled={isUploading}
            className="font-medium text-primary-600 hover:text-primary-500 disabled:opacity-50"
          >
            Click to upload
          </button>{" "}
          or drag and drop
        </div>
        <p className="text-xs text-gray-500 mt-2">PNG, JPG, GIF up to 10MB</p>
      </div>

      {/* Hidden File Input */}
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        onChange={handleFileInputChange}
        className="hidden"
      />

      {/* Upload Status */}
      {isUploading && (
        <div className="flex items-center space-x-2 text-sm text-gray-600">
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary-600"></div>
          <span>Uploading...</span>
        </div>
      )}
    </div>
  );
};

export default AvatarUpload;
