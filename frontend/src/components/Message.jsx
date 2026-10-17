import React from 'react';

function renderInline(text) {
  if (!text) return null;
  // Match **bold** tokens
  const parts = text.split(/(\*\*.*?\*\*)/g);
  return parts.map((part, index) => {
    if (part.startsWith('**') && part.endsWith('**') && part.length > 4) {
      return <strong key={index}>{part.slice(2, -2)}</strong>;
    }
    return part;
  });
}

function formatMessage(content) {
  if (!content) return '';

  const lines = content.split('\n');
  return lines.map((line, index) => {
    const trimmed = line.trim();

    // Markdown Headers
    if (trimmed.startsWith('### ')) {
      return (
        <h4 key={index} style={{ margin: '10px 0 4px', fontSize: '15px', fontWeight: 700 }}>
          {renderInline(trimmed.slice(4))}
        </h4>
      );
    }
    if (trimmed.startsWith('## ')) {
      return (
        <h3 key={index} style={{ margin: '12px 0 6px', fontSize: '16px', fontWeight: 700 }}>
          {renderInline(trimmed.slice(3))}
        </h3>
      );
    }
    if (trimmed.startsWith('# ')) {
      return (
        <h2 key={index} style={{ margin: '14px 0 8px', fontSize: '18px', fontWeight: 700 }}>
          {renderInline(trimmed.slice(2))}
        </h2>
      );
    }

    // Bullet Points
    if (trimmed.startsWith('• ') || trimmed.startsWith('- ') || trimmed.startsWith('* ')) {
      const bulletText = trimmed.replace(/^[•\-\*]\s*/, '');
      return (
        <div key={index} style={{ display: 'flex', gap: '8px', margin: '4px 0 4px 6px' }}>
          <span>•</span>
          <span>{renderInline(bulletText)}</span>
        </div>
      );
    }

    // Numbered list item like "1. ", "2. "
    const numberedMatch = trimmed.match(/^(\d+\.)\s+(.+)$/);
    if (numberedMatch) {
      return (
        <div key={index} style={{ display: 'flex', gap: '6px', margin: '4px 0 4px 6px' }}>
          <span style={{ fontWeight: 600 }}>{numberedMatch[1]}</span>
          <span>{renderInline(numberedMatch[2])}</span>
        </div>
      );
    }

    // Empty line / paragraph break
    if (!trimmed) {
      return <div key={index} style={{ height: '8px' }} />;
    }

    // Regular paragraph line
    return (
      <div key={index} style={{ margin: '2px 0' }}>
        {renderInline(line)}
      </div>
    );
  });
}

function Message({ message }) {
  const isUser = message.role === 'user';

  return (
    <div className={`chat-message ${isUser ? 'user-message' : 'assistant-message'}`}>
      <div className="message-avatar">
        {isUser ? 'You' : 'KV'}
      </div>

      <div className="message-content">
        <div className="message-sender">
          {isUser ? 'You' : 'KrishiVaani AI'}
        </div>

        <div className="message-bubble">
          {formatMessage(message.content)}
        </div>

        {message.time && (
          <small className="message-time">{message.time}</small>
        )}
      </div>
    </div>
  );
}

export default Message;
