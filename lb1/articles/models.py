from django.db import models


class Article(models.Model):
    title = models.CharField(max_length=200, verbose_name="Заголовок")
    text = models.TextField(verbose_name="Текст")
    published_at = models.DateTimeField(verbose_name="Дата публікації")
    author = models.CharField(max_length=100, verbose_name="Автор")

    class Meta:
        ordering = ["-published_at"]
        verbose_name = "Стаття"
        verbose_name_plural = "Статті"

    def __str__(self):
        return self.title
