import { Link } from 'react-router-dom';

function formatDate(str) {
  return new Date(str).toLocaleDateString('uk-UA', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

export default function ArticleCard({ article }) {
  return (
    <div className="card">
      {article.category && (
        <span className="card-category">{article.category.name}</span>
      )}
      <Link to={`/articles/${article.id}`} className="card-title">
        {article.title}
      </Link>
      <p className="card-excerpt">{article.excerpt}</p>
      <div className="card-meta">
        <span>{article.author?.username}</span>
        <span className="card-meta-dot">·</span>
        <span>{formatDate(article.created_at)}</span>
        <span className="card-meta-dot">·</span>
        <span>{article.comments_count} коментарів</span>
      </div>
    </div>
  );
}
