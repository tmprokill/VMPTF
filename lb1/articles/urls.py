from django.urls import path
from . import views

urlpatterns = [
    path("", views.article_list, name="article-list"),
    path("<int:pk>/", views.article_detail, name="article-detail"),
    path("api/articles/", views.ArticleListAPIView.as_view(), name="api-article-list"),
    path("api/articles/<int:pk>/", views.ArticleDetailAPIView.as_view(), name="api-article-detail"),
]
