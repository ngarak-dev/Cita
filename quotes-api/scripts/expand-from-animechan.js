#!/usr/bin/env node
/**
 * Optional expander: pull more quotes from api.animechan.io (rate-limited).
 * Free tier is ~100 requests/day — run sparingly.
 *
 * Usage:
 *   node scripts/expand-from-animechan.js
 *   ANIMECHAN_API_KEY=xxx node scripts/expand-from-animechan.js
 */
const fs = require('fs');
const path = require('path');

const DATA = path.join(__dirname, '..', 'data', 'quotes.json');
const BASE = 'https://api.animechan.io/v1';
const TITLES = [
  'Naruto', 'One Piece', 'Bleach', 'Death Note', 'Attack on Titan',
  'Fullmetal Alchemist: Brotherhood', 'Cowboy Bebop', 'Demon Slayer',
  'Jujutsu Kaisen', 'My Hero Academia', 'Hunter x Hunter', 'Steins;Gate',
  'Code Geass', 'Fairy Tail', 'Sword Art Online', 'Tokyo Ghoul',
  'Neon Genesis Evangelion', 'Dragon Ball Z', 'Spy x Family', 'Vinland Saga',
];

async function fetchJson(url) {
  const headers = { Accept: 'application/json' };
  if (process.env.ANIMECHAN_API_KEY) {
    headers['x-api-key'] = process.env.ANIMECHAN_API_KEY;
  }
  const res = await fetch(url, { headers });
  if (!res.ok) throw new Error(`${res.status} ${url}`);
  return res.json();
}

function sleep(ms) {
  return new Promise((r) => setTimeout(r, ms));
}

(async () => {
  const existing = JSON.parse(fs.readFileSync(DATA, 'utf8'));
  const seen = new Set(existing.map((q) => q.quote.toLowerCase()));
  let added = 0;

  for (const title of TITLES) {
    const url = `${BASE}/quotes?${new URLSearchParams({ anime: title, page: '1' })}`;
    try {
      const body = await fetchJson(url);
      const rows = body.data || [];
      for (const row of rows) {
        const quote = (row.content || '').trim();
        if (!quote || seen.has(quote.toLowerCase())) continue;
        seen.add(quote.toLowerCase());
        existing.push({
          anime: row.anime?.name || title,
          character: row.character?.name || 'Unknown',
          quote,
        });
        added += 1;
      }
      console.log(`${title}: +${rows.length} fetched`);
    } catch (err) {
      console.warn(`${title}: ${err.message}`);
    }
    await sleep(400);
  }

  fs.writeFileSync(DATA, JSON.stringify(existing, null, 2));
  console.log(`Done. Added ${added}. Total ${existing.length}.`);
})();
