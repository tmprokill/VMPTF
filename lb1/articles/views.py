from django.shortcuts import render, get_object_or_404
from rest_framework import generics
from .models import Article
from .serializers import ArticleSerializer


def article_list(request):
    author_filter = request.GET.get("author", "")
    articles = Article.objects.all()
    if author_filter:
        articles = articles.filter(author__icontains=author_filter)
    authors = Article.objects.values_list("author", flat=True).distinct().order_by("author")
    return render(request, "articles/article_list.html", {
        "articles": articles,
        "authors": authors,
        "author_filter": author_filter,
    })


def article_detail(request, pk):
    article = get_object_or_404(Article, pk=pk)
    return render(request, "articles/article_detail.html", {"article": article})


class ArticleListAPIView(generics.ListAPIView):
    serializer_class = ArticleSerializer

    def get_queryset(self):
        queryset = Article.objects.all()
        author = self.request.query_params.get("author")
        if author:
            queryset = queryset.filter(author__icontains=author)
        return queryset


class ArticleDetailAPIView(generics.RetrieveAPIView):
    queryset = Article.objects.all()
    serializer_class = ArticleSerializer
