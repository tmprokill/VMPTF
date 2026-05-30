# Tasks

1. Створіть Django-проект та додаток, який має модель "Стаття" з полями "Заголовок", "Текст" та "Дата публікації".
2. Розширте модель "Стаття", додавши поле "Автор". За допомогою Django ORM, виведіть на екран інформацію про всі статті конкретного автора.
3. Створіть веб-додаток для відображення списку користувачів. Кожен користувач повинен включати ім'я та електронну пошту. Додайте можливість позначати користувачів у відповідності до їх груп - ЦА, постійні клієнти та інші.
4. Створіть новий компонент в який інтегруйте роботу з публічним API (наприклад, JSONPlaceholder) та виведіть реальні дані на вашому веб-сайті. Інформація має бути дотичною до вашого завдання з рівня 3.

## Структура проекту

```
lb1/
├── manage.py
├── db.sqlite3
│
├── config/                   # Налаштування проекту
│   ├── settings.py
│   ├── urls.py
│   ├── asgi.py
│   └── wsgi.py
│
└── articles/                 # Додаток статей
    ├── models.py             # Модель Article
    ├── views.py              # Веб-вигляди + API
    ├── serializers.py        # DRF серіалізатор
    ├── urls.py               # Маршрути
    ├── admin.py              # Адмін-панель
    ├── management/
    │   └── commands/
    │       └── seed_articles.py  # Команда наповнення БД
    └── templates/
        └── articles/
            ├── base.html         # Базовий шаблон (темна тема)
            ├── article_list.html # Список статей
            └── article_detail.html # Окрема стаття
```

## Встановлення та запуск

### 1. Активувати віртуальне середовище

```powershell
# Windows
.\venv\Scripts\activate
```

### 2. Встановити залежності

```powershell
pip install django djangorestframework
```

### 3. Застосувати міграції

```powershell
python manage.py migrate
```

### 4. Наповнити базу даних тестовими даними

```powershell
# Додати статті (пропускає дублікати)
python manage.py seed_articles

# Очистити БД і заповнити заново
python manage.py seed_articles --flush
```

### 5. Запустити сервер

```powershell
python manage.py runserver
```

Відкрити у браузері: http://127.0.0.1:8000/

## Маршрути

| URL | Опис |
|-----|------|
| `/` | Список статей з фільтром за автором |
| `/<id>/` | Сторінка окремої статті |
| `/admin/` | Адмін-панель Django |
| `/api/articles/` | JSON API — список статей |
| `/api/articles/<id>/` | JSON API — одна стаття |
| `/api/articles/?author=Ім'я` | JSON API — фільтр за автором |

## Модель Article

| Поле | Тип | Опис |
|------|-----|------|
| `id` | AutoField | Первинний ключ |
| `title` | CharField(200) | Заголовок |
| `text` | TextField | Текст статті |
| `published_at` | DateTimeField | Дата публікації |
| `author` | CharField(100) | Автор |

## Адмін-панель

Створити суперкористувача:

```powershell
python manage.py createsuperuser
```

Відкрити: http://127.0.0.1:8000/admin/
