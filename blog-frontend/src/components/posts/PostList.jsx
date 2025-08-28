import React, { useState, useEffect } from "react";
import { postService } from "../../services/postService";
import PostCard from "./PostCard";
import LoadingSpinner from "../common/LoadingSpinner";

const PostList = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await postService.getAll();
      setPosts(response.data);
    } catch (err) {
      setError("Failed to fetch posts");
      console.error("Error fetching posts:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;
  if (error)
    return <div className="text-center text-red-600 py-8">{error}</div>;

  return (
    <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}

      {posts.length === 0 && (
        <div className="col-span-full text-center py-12">
          <p className="text-gray-500 text-lg">No posts found</p>
        </div>
      )}
    </div>
  );
};

export default PostList;
