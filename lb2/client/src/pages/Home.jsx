import { useState, useEffect, useCallback } from 'react';
import api from '../api/index.js';
import ArticleCard from '../components/ArticleCard.jsx';

export default function Home() {
  const [articles, setArticles] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchInput, setSearchInput] = useState('');
  const [search, setSearch] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    api.get('/api/categories').then(res => setCategories(res.data));
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearch(searchInput);
      setPage(1);
    }, 350);
    return () => clearTimeout(timer);
  }, [searchInput]);

  const fetchArticles = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, limit: 9 };
      if (search) params.search = search;
      if (categoryId) params.category_id = categoryId;
      const res = await api.get('/api/articles', { params });
      setArticles(res.data.articles);
      setTotalPages(res.data.totalPages);
      setTotal(res.data.total);
    } catch {
      setArticles([]);
    } finally {
      setLoading(false);
    }
  }, [search, categoryId, page]);

  useEffect(() => {
    fetchArticles();
  }, [fetchArticles]);

  const handleCategoryChange = e => {
    setCategoryId(e.target.value);
    setPage(1);
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Блог</h1>
        <p className="page-subtitle">
          {total > 0 ? `Знайдено статей: ${total}` : 'Статей поки немає'}
        </p>
      </div>

      <div className="filters">
        <input
          type="text"
          className="form-control search-input"
          placeholder="Пошук за назвою або змістом..."
          value={searchInput}
          onChange={e => setSearchInput(e.target.value)}
        />
        <select
          className="form-control filter-select"
          value={categoryId}
          onChange={handleCategoryChange}
        >
          <option value="">Всі категорії</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="loading">
          <div className="loading-spinner" />
          Завантаження статей...
        </div>
      ) : articles.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📭</div>
          <p>Статей не знайдено. Спробуйте інший пошук або категорію.</p>
        </div>
      ) : (
        <div className="articles-grid">
          {articles.map(article => (
            <ArticleCard key={article.id} article={article} />
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            className="btn btn-secondary btn-sm"
            disabled={page === 1}
            onClick={() => setPage(p => p - 1)}
          >
            ← Назад
          </button>
          <span className="page-info">
            Сторінка {page} з {totalPages}
          </span>
          <button
            className="btn btn-primary btn-sm"
            disabled={page === totalPages}
            onClick={() => setPage(p => p + 1)}
          >
            Вперед →
          </button>
        </div>
      )}
    </div>
  );
}
