import React from "react";
import CommentItem from "./CommentItem";

const CommentList = ({ comments, onDeleteComment, currentUser }) => {
  if (comments.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        <p>No comments yet. Be the first to comment!</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {comments.map((comment) => (
        <CommentItem
          key={comment.id}
          comment={comment}
          onDelete={onDeleteComment}
          currentUser={currentUser}
        />
      ))}
    </div>
  );
};

export default CommentList;
