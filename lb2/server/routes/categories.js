const express = require('express');
const db = require('../db');
const { cache, invalidateCategoryCache } = require('../cache');
const { authenticate } = require('../middleware/auth');

const router = express.Router();

router.get('/', (req, res) => {
  const cached = cache.get('categories_list');
  if (cached) return res.json(cached);

  const categories = db.prepare('SELECT * FROM categories ORDER BY name').all();
  cache.set('categories_list', categories);
  res.json(categories);
});

router.post('/', authenticate, (req, res) => {
  const { name } = req.body;
  if (!name) return res.status(400).json({ error: 'Назва категорії обов\'язкова' });

  const slug = name
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^a-z0-9-а-яёіїєґ]/gi, '');

  try {
    const result = db
      .prepare('INSERT INTO categories (name, slug) VALUES (?, ?)')
      .run(name, slug);
    invalidateCategoryCache();
    res.status(201).json({ id: Number(result.lastInsertRowid), name, slug });
  } catch {
    res.status(409).json({ error: 'Така категорія вже існує' });
  }
});

module.exports = router;
