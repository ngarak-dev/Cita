const { getQuotesByAnime, setCors, sendJson } = require('../../lib/quotes');

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
  const title = url.searchParams.get('title') || '';
  const page = url.searchParams.get('page') || '1';

  if (!title.trim()) {
    return sendJson(res, 400, { error: 'Missing required query parameter: title' });
  }

  return sendJson(res, 200, getQuotesByAnime(title, page));
};
