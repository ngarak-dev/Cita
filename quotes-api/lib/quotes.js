const fs = require('fs');
const path = require('path');

const PAGE_SIZE = 10;

let cache = null;

function loadQuotes() {
  if (cache) return cache;
  const file = path.join(__dirname, '..', 'data', 'quotes.json');
  const raw = JSON.parse(fs.readFileSync(file, 'utf8'));
  cache = raw.map((item) => ({
    anime: item.anime,
    character: item.character || 'Unknown',
    quote: item.quote,
  }));
  return cache;
}

function shuffle(list) {
  const arr = list.slice();
  for (let i = arr.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]];
  }
  return arr;
}

function pageSlice(list, page) {
  const p = Math.max(1, Number(page) || 1);
  const start = (p - 1) * PAGE_SIZE;
  return list.slice(start, start + PAGE_SIZE);
}

function getRandomQuotes(count = PAGE_SIZE) {
  return shuffle(loadQuotes()).slice(0, count);
}

function getQuotesPage(page) {
  return pageSlice(loadQuotes(), page);
}

function getQuotesByAnime(title, page) {
  const needle = String(title || '').trim().toLowerCase();
  const filtered = loadQuotes().filter((q) => q.anime.toLowerCase() === needle
    || q.anime.toLowerCase().includes(needle));
  return pageSlice(filtered, page);
}

function getAvailableAnime() {
  const names = [...new Set(loadQuotes().map((q) => q.anime))];
  return names.sort((a, b) => a.localeCompare(b));
}

function setCors(res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  res.setHeader('Content-Type', 'application/json; charset=utf-8');
}

function sendJson(res, status, body) {
  setCors(res);
  res.statusCode = status;
  res.end(JSON.stringify(body));
}

module.exports = {
  PAGE_SIZE,
  loadQuotes,
  getRandomQuotes,
  getQuotesPage,
  getQuotesByAnime,
  getAvailableAnime,
  setCors,
  sendJson,
};
