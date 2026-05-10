import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/index.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function CreateArticle() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [excerpt, setExcerpt] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    api.get('/api/categories').then(res => setCategories(res.data));
  }, [user, navigate]);

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await api.post('/api/articles', {
        title,
        content,
        excerpt: excerpt.trim() || undefined,
        category_id: categoryId ? parseInt(categoryId) : undefined,
      });
      navigate(`/articles/${res.data.id}`);
    } catch (err) {
      setError(err.response?.data?.error || 'Помилка при публікації статті');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-wrapper">
      <h1>Нова стаття</h1>
      {error && <div className="error">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Заголовок *</label>
          <input
            type="text"
            className="form-control"
            value={title}
            onChange={e => setTitle(e.target.value)}
            placeholder="Введіть заголовок статті"
            required
            autoFocus
          />
        </div>

        <div className="form-group">
          <label>Категорія</label>
          <select
            className="form-control"
            value={categoryId}
            onChange={e => setCategoryId(e.target.value)}
          >
            <option value="">Без категорії</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Анотація (необов'язково)</label>
          <input
            type="text"
            className="form-control"
            value={excerpt}
            onChange={e => setExcerpt(e.target.value)}
            placeholder="Короткий опис для карток на головній сторінці"
          />
        </div>

        <div className="form-group">
          <label>Зміст *</label>
          <textarea
            className="form-control"
            value={content}
            onChange={e => setContent(e.target.value)}
            placeholder="Напишіть текст статті..."
            rows={14}
            style={{ minHeight: 280 }}
            required
          />
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Публікуємо...' : 'Опублікувати'}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate('/')}
          >
            Скасувати
          </button>
        </div>
      </form>
    </div>
  );
}
