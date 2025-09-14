import React, { useState, useRef } from "react";
import {
  Bold,
  Italic,
  Link,
  List,
  ListOrdered,
  Quote,
  Code,
  Eye,
  Edit3,
} from "lucide-react";

const RichTextEditor = ({ value, onChange, placeholder, className = "" }) => {
  const [showPreview, setShowPreview] = useState(false);
  const textareaRef = useRef(null);

  const insertMarkdown = (before, after = "") => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = value.substring(start, end);

    const newText =
      value.substring(0, start) +
      before +
      selectedText +
      after +
      value.substring(end);

    onChange({ target: { value: newText } });

    // Set cursor position after the inserted text
    setTimeout(() => {
      const newPosition =
        start + before.length + selectedText.length + after.length;
      textarea.setSelectionRange(newPosition, newPosition);
      textarea.focus();
    }, 0);
  };

  const formatButtons = [
    {
      icon: Bold,
      title: "Bold",
      action: () => insertMarkdown("**", "**"),
      shortcut: "Ctrl+B",
    },
    {
      icon: Italic,
      title: "Italic",
      action: () => insertMarkdown("*", "*"),
      shortcut: "Ctrl+I",
    },
    {
      icon: Link,
      title: "Link",
      action: () => insertMarkdown("[", "](url)"),
      shortcut: "Ctrl+K",
    },
    {
      icon: Quote,
      title: "Quote",
      action: () => insertMarkdown("> "),
      shortcut: "Ctrl+>",
    },
    {
      icon: Code,
      title: "Code",
      action: () => insertMarkdown("`", "`"),
      shortcut: "Ctrl+`",
    },
    {
      icon: List,
      title: "Bullet List",
      action: () => insertMarkdown("- "),
      shortcut: "Ctrl+L",
    },
    {
      icon: ListOrdered,
      title: "Numbered List",
      action: () => insertMarkdown("1. "),
      shortcut: "Ctrl+Shift+L",
    },
  ];

  const handleKeyDown = (e) => {
    // Handle keyboard shortcuts
    if (e.ctrlKey || e.metaKey) {
      switch (e.key) {
        case "b":
          e.preventDefault();
          insertMarkdown("**", "**");
          break;
        case "i":
          e.preventDefault();
          insertMarkdown("*", "*");
          break;
        case "k":
          e.preventDefault();
          insertMarkdown("[", "](url)");
          break;
        default:
          break;
      }
    }
  };

  const renderPreview = (text) => {
    // Simple markdown to HTML conversion for preview
    return text
      .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
      .replace(/\*(.*?)\*/g, "<em>$1</em>")
      .replace(/`(.*?)`/g, '<code class="bg-gray-100 px-1 rounded">$1</code>')
      .replace(
        /^> (.*$)/gim,
        '<blockquote class="border-l-4 border-gray-300 pl-4 italic">$1</blockquote>'
      )
      .replace(/^- (.*$)/gim, "<li>$1</li>")
      .replace(/^1\. (.*$)/gim, "<li>$1</li>")
      .replace(
        /\[([^\]]+)\]\(([^)]+)\)/g,
        '<a href="$2" class="text-primary-600 underline">$1</a>'
      )
      .replace(/\n/g, "<br>");
  };

  return (
    <div
      className={`border border-gray-300 rounded-lg overflow-hidden ${className}`}
    >
      {/* Toolbar */}
      <div className="bg-gray-50 border-b border-gray-300 p-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-1">
            {formatButtons.map((button) => {
              const IconComponent = button.icon;
              return (
                <button
                  key={button.title}
                  type="button"
                  onClick={button.action}
                  title={`${button.title} (${button.shortcut})`}
                  className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-200 rounded transition-colors"
                >
                  <IconComponent size={16} />
                </button>
              );
            })}
          </div>

          <button
            type="button"
            onClick={() => setShowPreview(!showPreview)}
            className="flex items-center px-3 py-1 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-200 rounded transition-colors"
          >
            {showPreview ? (
              <>
                <Edit3 size={14} className="mr-1" />
                Edit
              </>
            ) : (
              <>
                <Eye size={14} className="mr-1" />
                Preview
              </>
            )}
          </button>
        </div>
      </div>

      {/* Content Area */}
      <div className="relative">
        {showPreview ? (
          // Preview Mode
          <div
            className="p-4 min-h-[300px] prose prose-sm max-w-none"
            dangerouslySetInnerHTML={{
              __html:
                renderPreview(value) ||
                '<p class="text-gray-400">Nothing to preview yet...</p>',
            }}
          />
        ) : (
          // Edit Mode
          <textarea
            ref={textareaRef}
            value={value}
            onChange={onChange}
            onKeyDown={handleKeyDown}
            placeholder={placeholder}
            className="w-full p-4 min-h-[300px] resize-none border-0 focus:outline-none focus:ring-0"
          />
        )}
      </div>

      {/* Footer */}
      <div className="bg-gray-50 border-t border-gray-300 px-4 py-2">
        <div className="flex justify-between items-center text-xs text-gray-500">
          <span>
            Supports Markdown formatting • Use toolbar or keyboard shortcuts
          </span>
          <span>{value.length} characters</span>
        </div>
      </div>
    </div>
  );
};

export default RichTextEditor;
