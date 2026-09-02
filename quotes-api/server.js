const http = require('http');
const { URL } = require('url');
const quotesHandler = require('./api/quotes');
const quotesByAnimeHandler = require('./api/quotes/anime');
const availableAnimeHandler = require('./api/available/anime');

const PORT = process.env.PORT || 8787;

function route(req, res) {
  const { pathname } = new URL(req.url, `http://localhost:${PORT}`);

  if (pathname === '/api/quotes' || pathname === '/quotes') {
    return quotesHandler(req, res);
  }
  if (pathname === '/api/quotes/anime' || pathname === '/quotes/anime') {
    return quotesByAnimeHandler(req, res);
  }
  if (pathname === '/api/available/anime' || pathname === '/available/anime') {
    return availableAnimeHandler(req, res);
  }
  if (pathname === '/' || pathname === '/health') {
    res.statusCode = 200;
    res.setHeader('Content-Type', 'application/json');
    return res.end(JSON.stringify({
      status: 'ok',
      service: 'cita-quotes-api',
      endpoints: [
        'GET /api/quotes',
        'GET /api/quotes?page=1',
        'GET /api/quotes/anime?title=Naruto&page=1',
        'GET /api/available/anime',
      ],
    }));
  }

  res.statusCode = 404;
  res.setHeader('Content-Type', 'application/json');
  res.end(JSON.stringify({ error: 'Not found' }));
}

http.createServer(route).listen(PORT, () => {
  console.log(`Cita quotes API listening on http://localhost:${PORT}`);
  console.log(`Base URL for Cita: http://localhost:${PORT}/api/`);
});
