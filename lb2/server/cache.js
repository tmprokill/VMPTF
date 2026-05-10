const NodeCache = require('node-cache');

const cache = new NodeCache({ stdTTL: 300, checkperiod: 60 });

function invalidateArticleCache(articleId) {
  const listKeys = cache.keys().filter(k => k.startsWith('articles_list_'));
  if (listKeys.length) cache.del(listKeys);
  if (articleId) cache.del(`articles_${articleId}`);
}

function invalidateCategoryCache() {
  cache.del('categories_list');
}

module.exports = { cache, invalidateArticleCache, invalidateCategoryCache };
