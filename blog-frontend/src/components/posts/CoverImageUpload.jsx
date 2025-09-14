import React, { useState, useRef } from "react";
import { Upload, X, Image as ImageIcon } from "lucide-react";

const CoverImageUpload = ({
  currentImage,
  onImageChange,
  isUploading,
  className = "",
}) => {
  const [previewUrl, setPreviewUrl] = useState(null);
  const [dragActive, setDragActive] = useState(false);
  const fileInputRef = useRef(null);

  const handleFileSelect = (file) => {
    if (file && file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setPreviewUrl(e.target.result);
      };
      reader.readAsDataURL(file);

      onImageChange(file);
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

  const clearImage = () => {
    setPreviewUrl(null);
    onImageChange(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const openFileDialog = (e) => {
    e?.preventDefault();
    e?.stopPropagation();
    fileInputRef.current?.click();
  };

  const getImageSrc = () => {
    if (previewUrl) return previewUrl;
    if (currentImage && currentImage !== "default.jpg") {
      return `/img/posts/${currentImage}`;
    }
    return null;
  };

  const imageSrc = getImageSrc();

  return (
    <div className={`space-y-4 ${className}`}>
      <label className="block text-sm font-medium text-gray-700">
        Cover Image
      </label>

      {/* Image Preview */}
      {imageSrc && (
        <div className="relative">
          <div className="relative w-full h-64 rounded-lg overflow-hidden bg-gray-100 border">
            <img
              src={imageSrc}
              alt="Cover preview"
              className="w-full h-full object-cover"
            />
            {/* Remove Button */}
            <button
              type="button"
              onClick={clearImage}
              className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white p-1.5 rounded-full shadow-lg transition-colors"
              disabled={isUploading}
            >
              <X size={16} />
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            Click the X to remove the image or drag a new one to replace
          </p>
        </div>
      )}

      {/* Upload Area */}
      <div
        className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors cursor-pointer ${
          dragActive
            ? "border-primary-500 bg-primary-50"
            : "border-gray-300 hover:border-gray-400"
        } ${imageSrc ? "h-32" : "h-64"}`}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onClick={openFileDialog}
      >
        <div className="flex flex-col items-center justify-center h-full">
          {isUploading ? (
            <>
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mb-4"></div>
              <p className="text-sm text-gray-600">Uploading...</p>
            </>
          ) : (
            <>
              <ImageIcon className="h-12 w-12 text-gray-400 mb-4" />
              <div className="text-sm text-gray-600 mb-2">
                <span className="font-medium text-primary-600 hover:text-primary-500">
                  Click to upload
                </span>{" "}
                or drag and drop
              </div>
              <p className="text-xs text-gray-500">
                PNG, JPG, GIF up to 10MB • Recommended: 1200x630px
              </p>
            </>
          )}
        </div>
      </div>

      {/* Hidden File Input */}
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        onChange={handleFileInputChange}
        className="hidden"
      />
    </div>
  );
};

export default CoverImageUpload;
