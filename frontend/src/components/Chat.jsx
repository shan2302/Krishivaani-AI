
import { useEffect, useRef, useState } from 'react';
import Message from './Message';
import './Chat.css';

function Chat() {
  const [messages, setMessages] = useState([
    {
      id: 1,
      role: 'assistant',
      content:
        'Namaste! I am KrishiVaani AI. Ask me anything about farming, crops, soil health, or irrigation.',
      time: 'Welcome',
    },
  ]);

  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  async function sendMessage(event) {
    event.preventDefault();

    const text = input.trim();

    if (!text || loading) return;

    const userMessage = {
      id: Date.now(),
      role: 'user',
      content: text,
      time: new Date().toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      }),
    };

    setMessages((previous) => [...previous, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const apiUrl = import.meta.env.VITE_CHAT_API_URL;

      if (!apiUrl) {
        throw new Error(
          'Chat API is not configured. Set VITE_CHAT_API_URL after confirming the backend endpoint.'
        );
      }

      const token = localStorage.getItem('kv_token');

      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        body: JSON.stringify({
          content: text,
          language: 'en',
        }),
      });

      if (!response.ok) {
        throw new Error(`Chat API returned HTTP ${response.status}`);
      }

      const data = await response.json();

      const answer =
        data.data?.answer ??
        data.data?.content ??
        data.data?.response ??
        data.message ??
        data.data?.message;

      if (typeof answer !== 'string' || !answer.trim()) {
        throw new Error('The API response did not contain a text answer.');
      }

      setMessages((previous) => [
        ...previous,
        {
          id: Date.now() + 1,
          role: 'assistant',
          content: answer,
          time: new Date().toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
          }),
        },
      ]);
    } catch (error) {
      setMessages((previous) => [
        ...previous,
        {
          id: Date.now() + 2,
          role: 'assistant',
          content: `Sorry, I couldn't get a response. ${error.message}`,
          time: 'Error',
        },
      ]);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="chat-page">
      <aside className="chat-sidebar">
        <div className="brand">
          <span className="brand-icon">🌾</span>
          <div>
            <h2>KrishiVaani</h2>
            <small>AI Farming Assistant</small>
          </div>
        </div>

        <button
          className="new-chat-button"
          onClick={() =>
            setMessages([
              {
                id: Date.now(),
                role: 'assistant',
                content:
                  'Namaste! What would you like to know about farming today?',
              },
            ])
          }
        >
          + New conversation
        </button>

        <div className="sidebar-info">
          <span>YOUR FARMING COMPANION</span>
          <p>Get help with crops, soil, irrigation, and agricultural practices.</p>
        </div>

        <div className="sidebar-footer">
          <span className="status-dot" />
          Local development
        </div>
      </aside>

      <main className="chat-main">
        <header className="chat-header">
          <div>
            <h1>Farm Assistant</h1>
            <p>Ask questions. Grow smarter.</p>
          </div>

          <button
            className="logout-button"
            onClick={() => {
              localStorage.removeItem('kv_token');
              window.location.href = '/login';
            }}
          >
            Logout
          </button>
        </header>

        <section className="chat-messages" aria-live="polite">
          <div className="welcome-heading">
            <span className="welcome-icon">🌱</span>
            <h2>Your farm, your questions.</h2>
            <p>How can KrishiVaani help you today?</p>
          </div>

          {messages.map((message) => (
            <Message key={message.id} message={message} />
          ))}

          {loading && (
            <div className="typing-indicator" role="status">
              <span />
              <span />
              <span />
              <small>KrishiVaani is thinking...</small>
            </div>
          )}

          <div ref={bottomRef} />
        </section>

        <div className="chat-composer">
          <form onSubmit={sendMessage}>
            <textarea
              value={input}
              onChange={(event) => setInput(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === 'Enter' && !event.shiftKey) {
                  event.preventDefault();
                  event.currentTarget.form.requestSubmit();
                }
              }}
              placeholder="Ask about crops, soil, irrigation..."
              aria-label="Your message"
              rows={2}
              disabled={loading}
            />

            <button
              type="submit"
              disabled={!input.trim() || loading}
              aria-label="Send message"
            >
              ➤
            </button>
          </form>

          <small>Press Enter to send · Shift + Enter for a new line</small>
        </div>
      </main>
    </div>
  );
}

export default Chat;
