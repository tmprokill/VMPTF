import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/index.js';
import { useAuth } from '../context/AuthContext.jsx';

function formatDate(str) {
  return new Date(str).toLocaleString('uk-UA', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export default function CommentSection({ articleId }) {
  const [comments, setComments] = useState([]);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    api
      .get(`/api/comments/${articleId}`)
      .then(res => setComments(res.data))
      .finally(() => setLoading(false));
  }, [articleId]);

  const handleSubmit = async e => {
    e.preventDefault();
    if (!content.trim()) return;
    setSubmitting(true);
    setError('');
    try {
      const res = await api.post(`/api/comments/${articleId}`, { content });
      setComments(prev => [...prev, res.data]);
      setContent('');
    } catch (err) {
      setError(err.response?.data?.error || 'Помилка при додаванні коментаря');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="comments-section">
      <h2 className="comments-title">
        Коментарі ({loading ? '...' : comments.length})
      </h2>

      {loading ? (
        <div className="loading">
          <div className="loading-spinner" />
          Завантаження...
        </div>
      ) : comments.length === 0 ? (
        <p style={{ color: 'var(--text-muted)', marginBottom: 20 }}>
          Ще немає коментарів. Будьте першим!
        </p>
      ) : (
        comments.map(comment => (
          <div key={comment.id} className="comment">
            <div className="comment-header">
              <span className="comment-author">{comment.author.username}</span>
              <span className="comment-date">{formatDate(comment.created_at)}</span>
            </div>
            <p className="comment-content">{comment.content}</p>
          </div>
        ))
      )}

      {user ? (
        <form onSubmit={handleSubmit} className="comment-form-card">
          <h3>Додати коментар</h3>
          {error && <div className="error">{error}</div>}
          <div className="form-group">
            <textarea
              className="form-control"
              value={content}
              onChange={e => setContent(e.target.value)}
              placeholder="Напишіть коментар..."
              rows={3}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Відправляємо...' : 'Відправити'}
          </button>
        </form>
      ) : (
        <div className="comment-form-card" style={{ textAlign: 'center' }}>
          <p style={{ color: 'var(--text-muted)' }}>
            <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600 }}>
              Увійдіть
            </Link>
            , щоб залишити коментар
          </p>
        </div>
      )}
    </div>
  );
}
