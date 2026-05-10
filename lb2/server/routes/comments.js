const express = require('express');
const db = require('../db');
const { authenticate } = require('../middleware/auth');

const router = express.Router();

router.get('/:articleId', (req, res) => {
  const rows = db
    .prepare(
      `SELECT c.id, c.content, c.created_at, u.id as user_id, u.username
       FROM comments c
       JOIN users u ON c.user_id = u.id
       WHERE c.article_id = ?
       ORDER BY c.created_at ASC`
    )
    .all(req.params.articleId);

  res.json(
    rows.map(c => ({
      id: c.id,
      content: c.content,
      created_at: c.created_at,
      author: { id: c.user_id, username: c.username },
    }))
  );
});

router.post('/:articleId', authenticate, (req, res) => {
  const { content } = req.body;
  const { articleId } = req.params;

  if (!content || !content.trim())
    return res.status(400).json({ error: 'Текст коментаря обов\'язковий' });

  const article = db.prepare('SELECT id FROM articles WHERE id = ?').get(articleId);
  if (!article) return res.status(404).json({ error: 'Статтю не знайдено' });

  const result = db
    .prepare('INSERT INTO comments (article_id, user_id, content) VALUES (?, ?, ?)')
    .run(articleId, req.user.id, content.trim());

  res.status(201).json({
    id: Number(result.lastInsertRowid),
    content: content.trim(),
    created_at: new Date().toISOString(),
    author: { id: req.user.id, username: req.user.username },
  });
});

module.exports = router;
