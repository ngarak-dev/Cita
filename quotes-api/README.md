# Cita Quotes API

Drop-in replacement for the old Anime Chan endpoints used by Cita.

## Seed data

- **~1,055 quotes** across **~104 anime titles**
- Merged from open sources (AniQuotesAPI MIT, HuggingFace anime-quotes CSV, Yurippe sample, Animechan.io samples)
- Expand later with `npm run expand` (Animechan.io is rate-limited on the free tier)

## Endpoints (Anime Chan-compatible)

Base URL after deploy should end with `/api/`:

| Method | Path | Behavior |
|--------|------|----------|
| GET | `/quotes` | 10 random quotes |
| GET | `/quotes?page=1` | Paginated 10 quotes |
| GET | `/quotes/anime?title=Naruto&page=1` | Quotes for an anime |
| GET | `/available/anime` | Sorted list of anime title strings |

Response shape:

```json
[{ "anime": "Naruto", "character": "Itachi Uchiha", "quote": "..." }]
```

## Run locally

```bash
cd quotes-api
npm start
# http://localhost:8787/api/
```

Point Cita at `http://10.0.2.2:8787/api/` from an Android emulator, or your LAN IP from a device.

## Deploy to Vercel

```bash
cd quotes-api
npx vercel
```

Then set `ANIME_BASE_URL` in the Android app to `https://YOUR_PROJECT.vercel.app/api/`.

## License

API code: MIT.  
Quote text remains attributed to the respective anime works; seed files include third-party licenses where applicable (`data/SOURCE_LICENSE.txt`).
