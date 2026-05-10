const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const db = require('../db');
const { authenticate, JWT_SECRET } = require('../middleware/auth');

const router = express.Router();

router.post('/register', (req, res) => {
  const { username, email, password } = req.body;
  if (!username || !email || !password)
    return res.status(400).json({ error: 'Всі поля обов\'язкові' });
  if (password.length < 6)
    return res.status(400).json({ error: 'Пароль повинен містити мінімум 6 символів' });

  try {
    const passwordHash = bcrypt.hashSync(password, 10);
    const result = db
      .prepare('INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)')
      .run(username, email, passwordHash);
    const user = { id: Number(result.lastInsertRowid), username, email };
    const token = jwt.sign(user, JWT_SECRET, { expiresIn: '7d' });
    res.status(201).json({ token, user });
  } catch (err) {
    if (err.message.includes('UNIQUE'))
      res.status(409).json({ error: 'Користувач з таким іменем або email вже існує' });
    else
      res.status(500).json({ error: 'Помилка сервера' });
  }
});

router.post('/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password)
    return res.status(400).json({ error: 'Введіть email та пароль' });

  const user = db.prepare('SELECT * FROM users WHERE email = ?').get(email);
  if (!user || !bcrypt.compareSync(password, user.password_hash))
    return res.status(401).json({ error: 'Невірний email або пароль' });

  const payload = { id: user.id, username: user.username, email: user.email };
  const token = jwt.sign(payload, JWT_SECRET, { expiresIn: '7d' });
  res.json({ token, user: payload });
});

router.get('/me', authenticate, (req, res) => {
  const user = db
    .prepare('SELECT id, username, email, created_at FROM users WHERE id = ?')
    .get(req.user.id);
  if (!user) return res.status(404).json({ error: 'Користувача не знайдено' });
  res.json(user);
});

module.exports = router;
