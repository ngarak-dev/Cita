const { getAvailableAnime, setCors, sendJson } = require('../../lib/quotes');

module.exports = (req, res) => {
  if (req.method === 'OPTIONS') {
    setCors(res);
    res.statusCode = 204;
    return res.end();
  }

  if (req.method !== 'GET') {
    return sendJson(res, 405, { error: 'Method not allowed' });
  }

  return sendJson(res, 200, getAvailableAnime());
};
