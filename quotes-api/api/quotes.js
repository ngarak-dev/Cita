const {
  getRandomQuotes,
  getQuotesPage,
  setCors,
  sendJson,
} = require('../lib/quotes');

module.exports = (req, res) => {
  if (req.method === 'OPTIONS') {
    setCors(res);
    res.statusCode = 204;
    return res.end();
  }

  if (req.method !== 'GET') {
    return sendJson(res, 405, { error: 'Method not allowed' });
  }

  const url = new URL(req.url, 'http://localhost');
  const page = url.searchParams.get('page');

  // Old Anime Chan behavior:
  // - no page  → 10 random quotes
  // - page=N   → paginated slice of 10
  if (page == null || page === '') {
    return sendJson(res, 200, getRandomQuotes(10));
  }

  return sendJson(res, 200, getQuotesPage(page));
};
