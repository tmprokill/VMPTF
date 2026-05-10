import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import api from '../api/index.js';
import { useAuth } from '../context/AuthContext.jsx';
import CommentSection from '../components/CommentSection.jsx';

function formatDate(str) {
  return new Date(str).toLocaleDateString('uk-UA', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

export default function ArticlePage() {
  const { id } = useParams();
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    api
      .get(`/api/articles/${id}`)
      .then(res => setArticle(res.data))
      .catch(() => setError('Статтю не знайдено'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleDelete = async () => {
    if (!window.confirm('Видалити цю статтю?')) return;
    try {
      await api.delete(`/api/articles/${id}`);
      navigate('/');
    } catch (err) {
      alert(err.response?.data?.error || 'Помилка при видаленні');
    }
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="loading-spinner" />
        Завантаження...
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ maxWidth: 760, margin: '40px auto' }}>
        <div className="error">{error}</div>
        <Link to="/" className="btn btn-secondary">← Назад до блогу</Link>
      </div>
    );
  }

  if (!article) return null;

  const isAuthor = user && user.id === article.author?.id;

  return (
    <div className="article-container">
      <div>
        <Link to="/" style={{ color: 'var(--text-muted)', textDecoration: 'none', fontSize: '0.875rem' }}>
          ← Назад до блогу
        </Link>
      </div>

      <div style={{ marginTop: 20 }}>
        {article.category && (
          <span className="badge">{article.category.name}</span>
        )}
        <h1 className="article-title">{article.title}</h1>
        <div className="article-meta">
          <span>
            Автор: <strong>{article.author?.username}</strong>
          </span>
          <span>·</span>
          <span>{formatDate(article.created_at)}</span>
          {article.updated_at !== article.created_at && (
            <>
              <span>·</span>
              <span>Оновлено: {formatDate(article.updated_at)}</span>
            </>
          )}
          {isAuthor && (
            <button onClick={handleDelete} className="btn btn-danger btn-sm" style={{ marginLeft: 'auto' }}>
              Видалити статтю
            </button>
          )}
        </div>
      </div>

      <div className="article-body">{article.content}</div>

      <CommentSection articleId={id} />
    </div>
  );
}
