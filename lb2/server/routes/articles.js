const express = require('express');
const db = require('../db');
const { cache, invalidateArticleCache } = require('../cache');
const { authenticate } = require('../middleware/auth');

const router = express.Router();

router.get('/', (req, res) => {
  const { search = '', category_id = '', page = '1', limit = '9' } = req.query;
  const pageNum = Math.max(1, parseInt(page));
  const limitNum = Math.min(50, Math.max(1, parseInt(limit)));
  const offset = (pageNum - 1) * limitNum;

  const cacheKey = `articles_list_${search}_${category_id}_${pageNum}_${limitNum}`;
  const cached = cache.get(cacheKey);
  if (cached) return res.json(cached);

  const conditions = [];
  const params = [];

  if (search) {
    conditions.push('(a.title LIKE ? OR a.content LIKE ?)');
    params.push(`%${search}%`, `%${search}%`);
  }
  if (category_id) {
    conditions.push('a.category_id = ?');
    params.push(parseInt(category_id));
  }

  const where = conditions.length ? 'WHERE ' + conditions.join(' AND ') : '';

  const { total } = db.prepare(`SELECT COUNT(*) as total FROM articles a ${where}`).get(...params);

  const rows = db
    .prepare(
      `SELECT a.id, a.title, a.excerpt, a.content, a.created_at, a.updated_at,
              u.id as author_id, u.username as author_name,
              c.id as category_id, c.name as category_name, c.slug as category_slug,
              (SELECT COUNT(*) FROM comments WHERE article_id = a.id) as comments_count
       FROM articles a
       LEFT JOIN users u ON a.user_id = u.id
       LEFT JOIN categories c ON a.category_id = c.id
       ${where}
       ORDER BY a.created_at DESC
       LIMIT ? OFFSET ?`
    )
    .all(...params, limitNum, offset);

  const articles = rows.map(r => ({
    id: r.id,
    title: r.title,
    excerpt: r.excerpt || r.content.substring(0, 200) + '...',
    created_at: r.created_at,
    updated_at: r.updated_at,
    comments_count: r.comments_count,
    author: { id: r.author_id, username: r.author_name },
    category: r.category_id
      ? { id: r.category_id, name: r.category_name, slug: r.category_slug }
      : null,
  }));

  const result = { articles, total, page: pageNum, totalPages: Math.ceil(total / limitNum) };
  cache.set(cacheKey, result);
  res.json(result);
});

router.get('/:id', (req, res) => {
  const { id } = req.params;
  const cacheKey = `articles_${id}`;
  const cached = cache.get(cacheKey);
  if (cached) return res.json(cached);

  const r = db
    .prepare(
      `SELECT a.id, a.title, a.content, a.excerpt, a.created_at, a.updated_at,
              u.id as author_id, u.username as author_name,
              c.id as category_id, c.name as category_name, c.slug as category_slug
       FROM articles a
       LEFT JOIN users u ON a.user_id = u.id
       LEFT JOIN categories c ON a.category_id = c.id
       WHERE a.id = ?`
    )
    .get(id);

  if (!r) return res.status(404).json({ error: 'Статтю не знайдено' });

  const article = {
    id: r.id,
    title: r.title,
    content: r.content,
    excerpt: r.excerpt,
    created_at: r.created_at,
    updated_at: r.updated_at,
    author: { id: r.author_id, username: r.author_name },
    category: r.category_id
      ? { id: r.category_id, name: r.category_name, slug: r.category_slug }
      : null,
  };

  cache.set(cacheKey, article);
  res.json(article);
});

router.post('/', authenticate, (req, res) => {
  const { title, content, category_id, excerpt } = req.body;
  if (!title || !content)
    return res.status(400).json({ error: 'Заголовок та зміст обов\'язкові' });

  const result = db
    .prepare(
      'INSERT INTO articles (title, content, excerpt, user_id, category_id) VALUES (?, ?, ?, ?, ?)'
    )
    .run(title, content, excerpt || null, req.user.id, category_id || null);

  invalidateArticleCache();
  res.status(201).json({ id: Number(result.lastInsertRowid), message: 'Статтю створено' });
});

router.put('/:id', authenticate, (req, res) => {
  const { id } = req.params;
  const article = db.prepare('SELECT * FROM articles WHERE id = ?').get(id);
  if (!article) return res.status(404).json({ error: 'Статтю не знайдено' });
  if (article.user_id !== req.user.id) return res.status(403).json({ error: 'Доступ заборонено' });

  const { title, content, category_id, excerpt } = req.body;
  db.prepare(
    `UPDATE articles
     SET title = ?, content = ?, excerpt = ?, category_id = ?, updated_at = CURRENT_TIMESTAMP
     WHERE id = ?`
  ).run(
    title ?? article.title,
    content ?? article.content,
    excerpt ?? article.excerpt,
    category_id ?? article.category_id,
    id
  );

  invalidateArticleCache(id);
  res.json({ message: 'Статтю оновлено' });
});

router.delete('/:id', authenticate, (req, res) => {
  const { id } = req.params;
  const article = db.prepare('SELECT * FROM articles WHERE id = ?').get(id);
  if (!article) return res.status(404).json({ error: 'Статтю не знайдено' });
  if (article.user_id !== req.user.id) return res.status(403).json({ error: 'Доступ заборонено' });

  db.prepare('DELETE FROM articles WHERE id = ?').run(id);
  invalidateArticleCache(id);
  res.json({ message: 'Статтю видалено' });
});

module.exports = router;
