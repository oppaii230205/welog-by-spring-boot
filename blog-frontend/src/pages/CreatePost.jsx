import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { postService } from "../services/postService";
import CoverImageUpload from "../components/posts/CoverImageUpload";
import RichTextEditor from "../components/common/RichTextEditor";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { Save, Eye, ArrowLeft, FileText, Type, AlignLeft } from "lucide-react";

const CreatePost = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    excerpt: "",
    coverImage: null,
  });
  const [selectedImageFile, setSelectedImageFile] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState({});
  const [showPreview, setShowPreview] = useState(false);

  // Redirect if not authenticated TODO: Do the same for logged in users who request to /login again
  React.useEffect(() => {
    if (!isAuthenticated) {
      navigate("/login");
    }
  }, [isAuthenticated, navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    const fieldName = name || "content"; // Default to content for RichTextEditor

    setFormData((prev) => ({
      ...prev,
      [fieldName]: value,
    }));

    // Clear error when user starts typing
    if (errors[fieldName]) {
      setErrors((prev) => ({
        ...prev,
        [fieldName]: "",
      }));
    }
  };

  const handleImageChange = (file) => {
    setSelectedImageFile(file);
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.title.trim()) {
      newErrors.title = "Title is required";
    } else if (formData.title.length < 3) {
      newErrors.title = "Title must be at least 3 characters";
    }

    if (!formData.content.trim()) {
      newErrors.content = "Content is required";
    } else if (formData.content.length < 10) {
      newErrors.content = "Content must be at least 10 characters";
    }

    if (formData.excerpt && formData.excerpt.length > 200) {
      newErrors.excerpt = "Excerpt must be less than 200 characters";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const generateExcerpt = (content) => {
    // Remove HTML tags and get first 150 characters
    const plainText = content.replace(/<[^>]*>/g, "");
    return plainText.length > 150
      ? plainText.substring(0, 150) + "..."
      : plainText;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      // For now, we'll handle image uploads separately
      // In a full implementation, you'd upload the image first, get the filename,
      // then include it in the post data

      const postData = {
        title: formData.title.trim(),
        content: formData.content.trim(),
        excerpt: formData.excerpt.trim() || generateExcerpt(formData.content),
        coverImage: /*selectedImageFile ? selectedImageFile.name :*/ null, // For now, just the filename
      };

      const response = await postService.create(postData);

      // If there's a selected image, upload it
      if (selectedImageFile) {
        await postService.uploadCoverImage(response.data.id, selectedImageFile);
      }

      navigate(`/posts/${response.data.id}`);
    } catch (error) {
      console.error("Error creating post:", error);

      if (error.response?.data?.message) {
        setErrors({ general: error.response.data.message });
      } else {
        setErrors({ general: "Failed to create post. Please try again." });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDraft = () => {
    // Save to localStorage as draft
    const draftData = {
      ...formData,
      timestamp: new Date().toISOString(),
    };
    localStorage.setItem("postDraft", JSON.stringify(draftData));
    alert("Draft saved locally!");
  };

  const loadDraft = () => {
    const draft = localStorage.getItem("postDraft");
    if (draft) {
      const draftData = JSON.parse(draft);
      setFormData({
        title: draftData.title || "",
        content: draftData.content || "",
        excerpt: draftData.excerpt || "",
        coverImage: draftData.coverImage || null,
      });
    }
  };

  React.useEffect(() => {
    // Check for existing draft on component mount
    const draft = localStorage.getItem("postDraft");
    if (draft) {
      const shouldLoad = window.confirm(
        "Found a saved draft. Would you like to load it?"
      );
      if (shouldLoad) {
        loadDraft();
      }
    }
  }, []);

  if (!isAuthenticated) {
    return <LoadingSpinner />;
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-sm border p-6 mb-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => navigate(-1)}
                className="text-gray-500 hover:text-gray-700 transition-colors"
              >
                <ArrowLeft size={24} />
              </button>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">
                  Create New Post
                </h1>
                <p className="text-gray-600">
                  Share your thoughts with the world
                </p>
              </div>
            </div>

            <div className="flex items-center space-x-3">
              <button
                type="button"
                onClick={handleDraft}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 transition-colors"
              >
                Save Draft
              </button>
              <button
                type="button"
                onClick={() => setShowPreview(!showPreview)}
                className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
              >
                <Eye size={16} className="mr-2" />
                {showPreview ? "Edit" : "Preview"}
              </button>
            </div>
          </div>
        </div>

        {/* Error Display */}
        {errors.general && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md mb-6">
            {errors.general}
          </div>
        )}

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Form Section */}
          <div className="lg:col-span-2">
            <form onSubmit={handleSubmit} className="space-y-8">
              {/* Cover Image */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <CoverImageUpload
                  currentImage={formData.coverImage}
                  onImageChange={handleImageChange}
                  isUploading={false}
                />
              </div>

              {/* Title */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <div className="flex items-center mb-4">
                  <Type size={20} className="text-gray-500 mr-2" />
                  <label
                    htmlFor="title"
                    className="text-sm font-medium text-gray-700"
                  >
                    Post Title
                  </label>
                </div>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  placeholder="Enter your post title..."
                  className={`w-full text-2xl font-bold border-0 border-b-2 bg-transparent focus:outline-none focus:border-primary-500 transition-colors placeholder-gray-400 ${
                    errors.title ? "border-red-500" : "border-gray-200"
                  }`}
                  maxLength={100}
                />
                {errors.title && (
                  <p className="text-red-600 text-sm mt-2">{errors.title}</p>
                )}
                <div className="flex justify-between items-center mt-2">
                  <span className="text-xs text-gray-500">
                    This will be your post's main headline
                  </span>
                  <span
                    className={`text-xs ${
                      formData.title.length > 80
                        ? "text-red-500"
                        : "text-gray-500"
                    }`}
                  >
                    {formData.title.length}/100
                  </span>
                </div>
              </div>

              {/* Excerpt */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <div className="flex items-center mb-4">
                  <AlignLeft size={20} className="text-gray-500 mr-2" />
                  <label
                    htmlFor="excerpt"
                    className="text-sm font-medium text-gray-700"
                  >
                    Excerpt (Optional)
                  </label>
                </div>
                <textarea
                  id="excerpt"
                  name="excerpt"
                  value={formData.excerpt}
                  onChange={handleInputChange}
                  placeholder="Write a brief summary of your post... (auto-generated if left empty)"
                  rows="3"
                  className={`w-full px-0 py-2 border-0 border-b-2 bg-transparent resize-none focus:outline-none focus:border-primary-500 transition-colors placeholder-gray-400 ${
                    errors.excerpt ? "border-red-500" : "border-gray-200"
                  }`}
                  maxLength={200}
                />
                {errors.excerpt && (
                  <p className="text-red-600 text-sm mt-2">{errors.excerpt}</p>
                )}
                <div className="flex justify-between items-center mt-2">
                  <span className="text-xs text-gray-500">
                    This appears in post previews and search results
                  </span>
                  <span
                    className={`text-xs ${
                      formData.excerpt.length > 180
                        ? "text-red-500"
                        : "text-gray-500"
                    }`}
                  >
                    {formData.excerpt.length}/200
                  </span>
                </div>
              </div>

              {/* Content */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <div className="flex items-center mb-4">
                  <FileText size={20} className="text-gray-500 mr-2" />
                  <label
                    htmlFor="content"
                    className="text-sm font-medium text-gray-700"
                  >
                    Post Content
                  </label>
                </div>
                <RichTextEditor
                  value={formData.content}
                  onChange={handleInputChange}
                  placeholder="Tell your story... (supports Markdown)"
                />
                {errors.content && (
                  <p className="text-red-600 text-sm mt-2">{errors.content}</p>
                )}
              </div>

              {/* Submit Button */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <button
                  type="submit"
                  disabled={
                    isSubmitting ||
                    !formData.title.trim() ||
                    !formData.content.trim()
                  }
                  className="w-full flex items-center justify-center px-6 py-3 bg-primary-600 text-white font-medium rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {isSubmitting ? (
                    <>
                      <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-3"></div>
                      Publishing...
                    </>
                  ) : (
                    <>
                      <Save size={20} className="mr-3" />
                      Publish Post
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="sticky top-8 space-y-6">
              {/* Publishing Tips */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                  Publishing Tips
                </h3>
                <ul className="space-y-3 text-sm text-gray-600">
                  <li className="flex items-start">
                    <span className="w-2 h-2 bg-primary-500 rounded-full mt-2 mr-3 flex-shrink-0"></span>
                    Use a compelling title that describes your post
                  </li>
                  <li className="flex items-start">
                    <span className="w-2 h-2 bg-primary-500 rounded-full mt-2 mr-3 flex-shrink-0"></span>
                    Add a cover image to make your post more engaging
                  </li>
                  <li className="flex items-start">
                    <span className="w-2 h-2 bg-primary-500 rounded-full mt-2 mr-3 flex-shrink-0"></span>
                    Write a clear excerpt for better discoverability
                  </li>
                  <li className="flex items-start">
                    <span className="w-2 h-2 bg-primary-500 rounded-full mt-2 mr-3 flex-shrink-0"></span>
                    Use Markdown for formatting (bold, italic, links)
                  </li>
                </ul>
              </div>

              {/* Post Status */}
              <div className="bg-white rounded-lg shadow-sm border p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">
                  Post Status
                </h3>
                <div className="space-y-3">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Author:</span>
                    <span className="font-medium">{user?.name || "You"}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Status:</span>
                    <span className="text-yellow-600 font-medium">Draft</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Created:</span>
                    <span className="font-medium">
                      {new Date().toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreatePost;
