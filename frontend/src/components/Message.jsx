
import React from 'react';

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
          {message.content}
        </div>

        {message.time && (
          <small className="message-time">{message.time}</small>
        )}
      </div>
    </div>
  );
}

export default Message;
