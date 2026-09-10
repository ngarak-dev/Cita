# Cita: From Play Store App to a Big Product

**Strategic blueprint** · September 2026  
**Status:** Live on Google Play (`me.ngarak.cita`) · v1.4 / versionCode 5  
**Purpose:** Decide what to build, what not to build, and how to turn Cita into a serious business — not how to squeeze more ad impressions out of a quote browser.

---

## Executive verdict (read this first)

Cita today is a **small, offline, ad-supported Android anime-quote viewer** with ~1,055 quotes across ~104 titles, image save/share, and AdMob as the only real monetization.

That is a **valid MVP**. It is **not** a venture-scale product category by itself.

**The hard truth:** “Anime quotes app” is a crowded, low-defensibility niche. Competitors already ship 100k+ quotes, favorites, widgets, and wallpapers. Pure catalog apps race to the bottom on ads and ratings. If Cita stays “browse quotes → watch ad → save JPEG,” it will remain a side project with occasional downloads — not a company.

**The opportunity:** Anime fans don’t need another quote database. They need a **daily emotional ritual** — a place where iconic lines become *their* identity objects: shared, collected, personalized, and culturally fluent. Quotes are the *atom*. The product should be the *habit, identity layer, and share engine* around those atoms.

**Recommended north star (winning strategy):**

> **Cita becomes the default way anime fans capture, customize, and share the lines that define them — a quote-native creative & social layer for anime culture.**

Not “another quotes list.” Not “AI girlfriend chat.” Not “MyAnimeList lite.”

**Quote → Card → Identity → Share → Community.**

That path can become big. A better Random tab cannot.

### Decision log

| Date | Decision |
|---|---|
| Sep 2026 | **Committed** to recommended path: Card Studio (create→share) + Aesthetic Ritual (widget/daily later), with AI taste & community as later layers. |
| Sep 2026 | **Phase 1 started in code:** honest positioning, Favorites tab, search, copy + “Made with Cita” card mark, shared save/share sheet, starter free save credits, app-open ads paused, Firebase funnel events (`quote_view` / `save` / `share` / `copy` / `favorite_toggle` / `quote_search`). |
| Sep 2026 | **Phase 2 started in code:** Card Studio templates (Classic/Midnight/Resolve/Heartbreak/Chaos), Daily Drop on Random, home-screen widget, mood lanes, first-run anime onboarding (up to 3). |
| Sep 2026 | **Phase 3 started in code:** Stories/Feed export, Aurora referral template unlock, invite share/redeem, watermark policy via `CitaPlus`, Indonesian strings, growth playbook + ASO draft. |

---

## Part 1 — Honest diagnosis of Cita today

### What Cita is good at

- **Clear job-to-be-done:** find a memorable anime line and turn it into something shareable.
- **Offline reliability:** quotes don’t die when an API dies (you already learned this the hard way with Anime Chan).
- **Complete micro-loop:** browse → open → save image → system share. Many quote apps stop at “copy text.”
- **Lightweight:** no account friction, fast to open, understandable in seconds.
- **Brand seed:** *Cita* (“quote” in Spanish) is short, memorable, and ownable if you build meaning around it.

### What Cita is getting wrong

1. **Positioning is commodity.** “Free anime quotes from hundreds of series” is what every competitor claims. Your Play copy even overstates coverage (“600+”) while the catalog is ~104 titles — trust debt before growth.
2. **Content depth is shallow and skewed.** A few titles have 100+ quotes; dozens have one. Users open their favorite show, see emptiness, and churn.
3. **Monetization fights the product.** Banners on every surface + app-open + gated saves trains users that Cita is an *ad container that happens to have quotes*. That kills share-worthiness and reviews.
4. **No reason to return tomorrow.** No daily ritual, widget, streak, collection, or social pull. Random refresh is not retention.
5. **No owned distribution.** No TikTok/Reels/Pinterest engine, no widget presence, no creator pipeline, no community. Play Store SEO alone will not build a category leader.
6. **Android-only + no identity graph.** Fine for MVP. Fatal if you never expand — anime culture is cross-platform and image-first.
7. **Product surface is “database UX.”** Title chips, paginated lists, bottom sheets. Functional. Not loveable. Not screenshot-worthy. Not meme-worthy.

### What we are probably underestimating

- **Share quality > quote quantity.** One stunning card that gets reposted beats 50,000 plain text quotes.
- **Anime is a language of identity.** People don’t just like quotes — they use them as personality, status, and affiliation (like lyrics apps did for music).
- **Short-form video is the real discovery channel** for this category, not Play Store search for “anime quotes.”
- **Rights & brand safety** will matter the moment you scale visuals, AI character likeness, or UGC that looks official.
- **AdMob fill and account approval** are business-continuity risks; ads as *sole* monetization is fragile.

### What we should stop doing

- Stop marketing catalog size we don’t have.
- Stop treating “more ad units” as product progress.
- Stop building remote-API theater if the product is offline-first (or go all-in remote — don’t half-do both).
- Stop shipping feature duplicates (save/ad logic thrice) instead of one sharp experience.
- Stop assuming “open source GitHub + Buy Me a Coffee” is a growth strategy for consumers.

### What we should double down on

- **The shareable quote card** as the core artifact.
- **Emotional / iconic moments**, not encyclopedic completeness.
- **Offline-first trust** (with smart remote updates later).
- **Creator-grade customization** (typography, scenes, stickers, templates).
- **Daily ritual surfaces** (widget, notification, lock-screen-worthy moment).

### Assumptions that must be validated (or Cita stays a hobby)

| Assumption | How to falsify |
|---|---|
| Users care enough to save/share weekly | Measure share rate, save rate, D7 |
| Beautiful cards outperform plain lists | A/B card studio vs current sheet |
| Specific subcultures (e.g. shonen motivation, romance, “sad hours”) convert better than “all anime” | Cohort by entry anime / mood |
| Ads on save destroy shares | Compare share rate with/without gate |
| Organic TikTok can acquire cheaper than ads | Post 30 cards; track installs via UTM/smart link |

---

## Part 2 — What problem should Cita own?

### The wrong problem

“Help people find anime quotes.”

Google, Reddit, Pinterest, MAL forums, and 20 Play Store apps already do this. Owning “search for quotes” is a SEO war you will lose.

### The right problem

**Anime fans lack a dedicated tool to turn emotional moments into personal, beautiful, shareable identity objects — and to keep those objects in their daily life.**

Adjacent problems Cita can own over time:

1. **Expression:** “I need this line to look like *me* when I post it.”
2. **Ritual:** “I want something that hits every morning the way my favorite character would.”
3. **Belonging:** “I want to see what my corner of fandom is quoting this week.”
4. **Creation:** “I want to make quote content without Canva + screenshot hell.”

### Ideal users (not “anyone who likes anime”)

**Primary ICP (first beachhead):**

- Age ~16–28  
- Watches anime weekly  
- Posts or consumes anime content on TikTok / Instagram / Discord / WhatsApp Status  
- Already screenshots quotes or uses ugly text-on-image apps  
- Emotionally attaches to characters (motivation, heartbreak, ambition, found family)

**Secondary ICPs (later):**

- Aesthetic / wallpaper / widget collectors  
- Discord anime server mods / community managers  
- Small creators who need daily quote content  
- Non-English markets (PT-BR, ES, ID, PH) where anime + WhatsApp sharing is huge

**Not the ideal first user:**

- Casual “I watched Naruto once”  
- People hunting encyclopedic quote databases  
- Users who only want AI romance chat (different product, different morals/brand risk)

### Why choose Cita over alternatives?

Only if Cita is clearly:

1. **The best-looking quote cards in anime** (branded aesthetic, not generic Canva).  
2. **The fastest path from feeling → post.**  
3. **Culturally fluent** (right line, right show, right mood — not random filler).  
4. **Habitual** (shows up on home screen / daily without hunting).  
5. **Community-aware** without becoming a toxic social network on day one.

### What would make Cita difficult to ignore?

- A visual language so distinctive that people recognize “that’s a Cita card” in the wild.  
- A **Quote of the Day / moment engine** that feels curated by a tasteful otaku editor, not an RNG.  
- Templates that map to fandom events (season finales, birthday of characters, meme formats).  
- Later: a living catalog that updates with seasonal anime *in days*, not app releases.  
- Even later: social proof — “12k people saved this line this week.”

### What creates daily / weekly return?

| Cadence | Mechanism |
|---|---|
| Daily | Widget + push “Today’s line” + streak for saving/sharing |
| Weekly | Themed packs (“Monday motivation,” “Sunday sadness”), new seasonal drops |
| Event | Episode air dates, movie releases, character days |
| Social | Friends’ shared cards, challenges, duets of quote reactions |

### Word-of-mouth & organic spread

Quotes spread when they are:

- Visually striking in a feed  
- Emotionally timed  
- Easy to remix  
- Credited in a way that advertises the app (“Made with Cita”)

**Growth engine thesis:** Cita should manufacture *shareable media*, not *sessions*. Every saved card is a billboard.

### Competitive advantage (moat candidates)

Weak moats: quote count, AdMob integration, offline JSON.  
Stronger moats:

1. **Brand + visual system** for anime quote cards  
2. **Taste graph** (which quotes hit for which moods/fandoms)  
3. **Creator templates & community packs**  
4. **Distribution presence** (widgets, short-video pipeline, Discord)  
5. **Licensed / partner content** later (studios, publishers — hard but real)  
6. **Data from shares** — what lines travel — feeding better curation

### Monetization without destroying love

**Principle:** Never gate the emotional core (reading a quote) behind an ad. Gate *power* and *polish*.

| Model | Fit | Risk |
|---|---|---|
| Soft ads (rare interstitial, no app-open spam) | Early cash | Hurt retention if dense |
| Remove-ads IAP | Easy | Caps revenue |
| **Cita Pro subscription** (widgets, templates, HD export, no ads, packs) | Best long-term | Needs clear value |
| Template marketplace (creator rev-share) | Scale | Ops + trust |
| Brand / seasonal pack sponsorships | Upside | Brand safety |

**Stop:** Rewarding users for watching ads to unlock basic saves as the *primary* loop. It teaches the wrong behavior.

### Features worth building vs distractions

**Worth it (on the winning path):**

- Card studio (fonts, layouts, backgrounds, stickers)  
- Favorites / collections / “My lines”  
- Home widget + daily quote  
- Search by anime, character, mood  
- Massive quality catalog expansion (curated, not scraped junk)  
- One-tap share to Stories / TikTok-ready aspect ratios  
- Light social: public packs, likes on cards (not a full feed day one)  
- iOS + web card maker (distribution)

**Distractions (look big, usually kill focus):**

- Full anime tracking (MAL already won)  
- Episode discussion forums  
- AI girlfriend / NSFW character chat  
- Generic “AI chatbot that quotes Naruto”  
- Crypto, points economies, gacha for quotes  
- Building a huge custom backend before share/retention metrics move  
- Cloning OtaQuotes feature-for-feature

### How AI can make Cita significantly more powerful (without becoming Creepy Chat)

Use AI as **infrastructure for taste and creation**, not as the product identity.

High-leverage AI:

1. **Mood & moment tagging** at catalog scale (“grief,” “resolve,” “found family”).  
2. **Card layout suggestions** matched to quote length and tone.  
3. **Semantic search** (“quotes about never giving up from shonen”).  
4. **Personal daily picks** from on-device / private taste signals.  
5. **Translation & localization** of packs for ID/ES/PT markets.  
6. **Moderation** for UGC packs.  
7. **Creator assist:** “Turn this screenshot OCR into a clean Cita card.”

Avoid (for brand & platform risk):

- Impersonating living VAs / official characters in chat  
- Deepfake character romance as core loop  
- Unlimited generative quotes that feel fake to fans (authenticity is the point)

### Turning Cita into more than an app

A serious Cita company looks like:

- **Consumer apps** (Android, iOS)  
- **Web studio** (make a card in browser, export)  
- **Content brand** (TikTok/Reels/Shorts posting daily Cita cards)  
- **Creator platform** (packs, templates)  
- **API / embed** later for Discord bots, websites (“Powered by Cita”)  
- **Physical / merch** long-shot: print packs, journals (after brand heat)

---

## Part 3 — Multiple big strategic directions

### Direction A — “Aesthetic Ritual” (Daily inspiration + widgets)

1. **Core idea:** Cita is the Calm/Hallow for anime fans — daily lines, wallpapers, widgets, gentle motivation.  
2. **Users:** Aesthetic anime fans, Gen Z wellness-adjacent.  
3. **Problem:** Motivation apps feel generic; anime lines hit harder emotionally.  
4. **Why care:** Identity-aligned inspiration.  
5. **Market:** Large overlap of wellness + anime; proven by apps like AnimeQuotes-style products.  
6. **Growth:** Widgets, App Store screenshots, TikTok “day in my life” aesthetics.  
7. **Monetization:** Subscription for themes/widgets/wallpapers.  
8. **Advantage:** Habit surface on home screen.  
9. **Risks:** Crowded; easy to copy; can feel soft/generic.  
10. **Build:** Widgets, reminders, wallpaper packs, themes.  
11. **Difficulty:** Medium.  
12. **Why big:** Habit products compound; subscriptions work.

### Direction B — “Card Studio for Fandom” (Create → Share) ★ recommended core

1. **Core idea:** Best-in-class anime quote card maker + library. Instagram Stories of anime culture.  
2. **Users:** Sharers and micro-creators.  
3. **Problem:** Making good-looking quote images is annoying; results look trashy.  
4. **Why care:** Status and expression in social feeds.  
5. **Market:** Massive social content demand; every fandom recycles quotes.  
6. **Growth:** Watermarked virality, template challenges, creator affiliates.  
7. **Monetization:** Pro templates, HD export, pack marketplace.  
8. **Advantage:** Brand recognition of cards in the wild + template library.  
9. **Risks:** Design quality bar; copyright on backgrounds/art.  
10. **Build:** Studio editor, export presets, catalog, favorites.  
11. **Difficulty:** Medium–hard (design craft).  
12. **Why big:** Every share is acquisition; content business + software.

### Direction C — “Living Quote Network” (Social / community packs)

1. **Core idea:** Users submit, vote, and remix quotes into packs; Cita is the fandom quote Reddit + Pinterest.  
2. **Users:** Power fans, Discord communities.  
3. **Problem:** Best quotes are scattered; no home for fandom taste.  
4. **Why care:** Belonging + contribution.  
5. **Market:** Huge UGC potential; hard to moderate.  
6. **Growth:** Network effects from packs and remixes.  
7. **Monetization:** Creator tips, featured packs, Pro.  
8. **Advantage:** Community graph.  
9. **Risks:** Cold start, moderation, copyright, toxicity.  
10. **Build:** Accounts, UGC pipeline, ranking, trust & safety.  
11. **Difficulty:** Hard.  
12. **Why big:** True network effects if it works.

### Direction D — “Seasonal Companion” (Tied to airing anime)

1. **Core idea:** As new episodes drop, Cita drops spoiler-aware highlight lines and discussion cards.  
2. **Users:** Weekly episode watchers.  
3. **Problem:** Recaps and spoiler spaces are noisy; fans want shareable moments fast.  
4. **Why care:** Timeliness = cultural relevance.  
5. **Market:** Seasonal anime audience is passionate and recurring.  
6. **Growth:** SEO + social around episode titles; Discord bots.  
7. **Monetization:** Premium early packs; brand deals with streamers.  
8. **Advantage:** Editorial speed + partnerships.  
9. **Risks:** Spoiler liability; ops-heavy; licensing.  
10. **Build:** Editorial CMS, spoiler controls, schedule pipeline.  
11. **Difficulty:** Hard operationally.  
12. **Why big:** Becomes part of the weekly anime ritual.

### Direction E — “AI Taste Layer” (Semantic discovery + personal radio)

1. **Core idea:** “Spotify DJ, but for anime lines that match your mood and history.”  
2. **Users:** Heavy consumers who want personalized hits.  
3. **Problem:** Random/list browsing doesn’t match emotional state.  
4. **Why care:** Right quote at the right moment feels magical.  
5. **Market:** Personalization is expected; few do it well for quotes.  
6. **Growth:** “Cita knew what I needed” stories.  
7. **Monetization:** Pro personalization + packs.  
8. **Advantage:** Taste model trained on save/share data.  
9. **Risks:** Thin catalog makes AI useless; privacy; sameness.  
10. **Build:** Tagging, embeddings, on-device or server ranking.  
11. **Difficulty:** Medium if catalog is rich; useless if not.  
12. **Why big:** Personalization compounds with data.

### Direction F — “Infrastructure” (API + Discord + embeds)

1. **Core idea:** Become Animechan++ — the quote infrastructure for developers *and* consumers.  
2. **Users:** Developers, bot makers; secondary consumers.  
3. **Problem:** Unreliable quote APIs; weak consumer layer.  
4. **Why care:** B2D revenue + distribution via bots.  
5. **Market:** Niche but paid (API subscriptions).  
6. **Growth:** Developer evangelism.  
7. **Monetization:** API tiers (you already have `quotes-api` seeds).  
8. **Advantage:** Data + reliability.  
9. **Risks:** Small market; doesn’t create consumer love alone.  
10. **Build:** Hosted API, keys, dashboards, Discord bot.  
11. **Difficulty:** Medium.  
12. **Why big:** Unlikely alone; strong *supporting* moat for B/C/D.

### Direction G — “Pivot hard: Anime creation suite”

1. **Core idea:** Expand beyond quotes into edits, AMVs, thumbnail text, etc.  
2. **Users:** Content creators.  
3. **Problem:** Creator toolchain is fragmented.  
4. **Why care:** Professional need.  
5. **Market:** Large creator economy.  
6. **Growth:** YouTube/TikTok creator circles.  
7. **Monetization:** SaaS.  
8. **Advantage:** Broad suite.  
9. **Risks:** CapCut already won; loses focus; capital intensive.  
10. **Build:** Heavy media tooling.  
11. **Difficulty:** Very hard.  
12. **Why big:** Huge market — but wrong starting point from current Cita.

### Ranking (potential × feasibility × defensibility)

| Rank | Direction | Score rationale |
|---|---|---|
| **1** | **B — Card Studio for Fandom** | Highest viral loop; fits current save/share DNA; defensible via brand/templates; feasible for a small team |
| **2** | **A — Aesthetic Ritual** | Strong retention via widgets; easier than social; weaker virality alone — combine with B |
| **3** | **E — AI Taste Layer** | Multiplier on B/A once catalog + behavior data exist; not a standalone launch |
| **4** | **D — Seasonal Companion** | High cultural heat; ops-heavy; great Phase 3–4 add-on |
| **5** | **C — Living Quote Network** | Biggest upside; cold start & trust & safety; Phase 4 |
| **6** | **F — Infrastructure** | Supporting business, not consumer rocket |
| **7** | **G — Full creator suite** | Too broad; dilutes Cita |

**Synthesis:** Lead with **B**, bolt on **A** for retention, use **E** as intelligence, grow into **D** and **C**. Keep **F** as a side engine when the catalog is world-class.

---

## Part 4 — The winning strategy

### Why this wins

It extends what Cita already does (browse → save image → share) into something people *show off*. It creates organic acquisition without paying for every install. It supports subscriptions without paywalling the soul of the product. It avoids the AI-chat bloodbath and the MAL war.

### Ideal positioning

> **Cita — Make the lines that made you.**  
> The anime quote studio for fans who share what they feel.

Alternative sharper line:

> **Your feelings. Their words. Your card.**

### Core value proposition

In under 30 seconds, turn an iconic anime line into a beautiful card that looks native in Stories, Discord, and chats — then keep your favorite lines alive on your home screen every day.

### Ideal first user

A 19-year-old who finishes an episode, feels something, opens Cita, makes a card in one minute, posts it to Instagram Stories, and gets replies. Next morning their widget shows another line from the same emotional world.

### The aha moment

**First time they export a card that looks better than anything they’ve made before — and share it without shame.**

Secondary aha: widget shows a perfect line on a rough morning.

### Core user loop

```
Discover (mood / show / daily)
   → Feel (read the line)
   → Craft (card studio)
   → Share (Stories / chat)
   → Collect (favorites / packs)
   → Return (widget / daily / new packs)
```

### Retention strategy

1. **Home screen widget** (non-negotiable for habit).  
2. **Daily Drop** with editorial taste.  
3. **Collections** (“My resolve,” “Cry lines”).  
4. **Streaks** for opening Daily Drop or sharing (careful: don’t gamify into spam).  
5. **Seasonal packs** synced to what fans are watching.  
6. Later: social proof on cards + following pack creators.

### Growth loop

```
User creates card
 → Shares with subtle “Cita” mark
 → Viewer taps / searches / scans QR or link in bio
 → New user creates their first card in <60s
 → Repeat
```

Supporting loops:

- TikTok account posting top cards daily (owned media).  
- Discord bot: `/cita mood:resolve` in anime servers.  
- Template challenges (“Make this week’s finale line”).

### Monetization strategy

**Phase ladder:**

1. **Now:** Reduce ad hostility; keep light banners if needed; remove app-open as default.  
2. **Soon:** One-time **Remove Ads** + **Pro templates** IAP.  
3. **PMF:** **Cita Plus** subscription — widgets customization, HD/no watermark, exclusive packs, advanced studio.  
4. **Scale:** Pack marketplace (70/30 creator split), brand-sponsored seasonal packs.

**Pricing intuition (validate locally):** Plus at a fan-friendly $2.99–$4.99/mo or $19.99–$29.99/yr in primary markets; lower in emerging markets.

### Competitive moat

1. Recognizable card brand  
2. Best template library for anime emotional moments  
3. Share graph → taste model  
4. Creator ecosystem  
5. Multi-surface presence (app + widget + bot + web)

### Long-term vision

Cita is the **identity and expression layer for anime language** — the place lines become culture objects. The Android app is the first client of a media + software company.

---

## Part 5 — Roadmap

### Phase 1 — Fix the Foundation (0–8 weeks)

**Goal:** Stop leaking trust and retention before you add ambition.

Highest-impact actions:

1. **Truth in packaging:** Fix Play listing and README (real catalog stats). Align privacy docs with offline reality.  
2. **Catalog quality sprint:** Expand to a *credible* core — e.g. 300–500 titles with *depth on the top 50*, not 5,000 one-liners. Kill empty-anime disappointment.  
3. **Detox ads:** Kill or heavily limit app-open; never block reading; make save less punitive; fix negative `quote_views`.  
4. **Favorites + copy text** (table stakes every competitor has).  
5. **Search** (anime, character, quote text).  
6. **Instrument analytics properly:** funnels for open → quote view → save → share.  
7. **Polish the card output:** Even before full studio — better typography, branding, aspect ratios for Stories.

**Exit criteria:** Share rate up; crash-free; listing honesty; D1/D7 baseline known.

### Phase 2 — Find Product-Market Fit (2–4 months)

**Goal:** Prove people return and share because of Cita’s cards/ritual — not because of ads.

Validate:

1. **Card Studio v1** (3–5 killer templates, fonts, colorways, logo mark).  
2. **Daily Drop + Widget.**  
3. **Mood lanes** (Resolve / Heartbreak / Chaos / Comfort) — even if manually curated.  
4. **Onboarding:** pick 3 favorite anime → personalized Random.  
5. **iOS decision point:** if Android shares work, start iOS or web export page for creators.  
6. **Talk to 20 users** who shared; watch session recordings if possible.

**Metrics that define PMF here:**

- % of new users who share once in first 24h  
- D7 retention for users who shared ≥1 card  
- Organic installs attributable to shares / social  
- Willingness to pay survey / paywall test for Plus

**Exit criteria:** “Shared ≥1 card” cohort retains meaningfully better; qualitative love for cards; clear Plus buyer persona.

### Phase 3 — Create Growth (4–9 months)

**Goal:** Make acquisition a loop, not a hope.

Highest-impact actions:

1. **Owned content engine:** 1 TikTok/Reels/Shorts channel posting Cita cards daily.  
2. **Watermark strategy:** elegant, removable in Plus.  
3. **Discord bot + 50 target servers.**  
4. **Referral:** unlock a template when a friend installs.  
5. **ASO for intent keywords** (“anime quote maker,” “anime story quotes”) — not just “anime quotes.”  
6. **Creator seeding:** give Plus free to 100 micro-creators.  
7. **Localization** for 1–2 high-sharing languages (start with one: Indonesian or Portuguese).

**Exit criteria:** Sustainable organic % of installs; CAC understandable; content engine can run weekly without founder burnout (process, not heroics).

### Phase 4 — Build a Moat (9–18 months)

**Goal:** Become hard to copy overnight.

1. **Pack marketplace / creator program.**  
2. **Taste model** from saves/shares (Direction E).  
3. **Seasonal editorial desk** (Direction D lite).  
4. **Web studio** for desktop creators.  
5. **Brand partnerships** (careful, scarce).  
6. **Community features with sharp scope** (public packs, remix — not infinite chat).

### Phase 5 — Scale (18–36 months)

**Goal:** Company, not app.

1. Cross-platform parity (Android, iOS, Web).  
2. Multi-region catalogs and community mods.  
3. Subscription + marketplace as majority revenue.  
4. Possible B2B: “Cita for communities” / API.  
5. Hire: editor-in-chief (taste), growth designer, mobile eng, trust & safety part-time.  
6. Expand adjacent formats: short quote videos, lock-screen packs — still quote-native.

---

## Part 6 — Metrics

### North-star metric

**Weekly Shared Cards (WSC)** — number of distinct users who successfully share ≥1 Cita card per week.

Why: aligns product, virality, and love. Downloads lie; ad impressions lie harder.

### Supporting metrics

| Funnel | Metric | Notes |
|---|---|---|
| **Acquisition** | Installs, organic %, source (Play / TikTok / Discord / referral) | Phase 3+ obsess over organic |
| **Activation** | Time-to-first-quote-view; **time-to-first-share**; % share in 24h | Activation = share, not open |
| **Retention** | D1 / D7 / D30; widget-added rate; Daily Drop open rate | Widget add is leading indicator |
| **Engagement** | Quotes viewed / session; studio edits / session; favorites depth | Watch for empty browsing |
| **Conversion** | Free → Remove Ads / Plus trial → paid | Phase 2–3 |
| **Revenue** | ARPU, ARPPU, ad eCPM, net after store fees | Don’t optimize ads until retention stable |
| **Referral** | Invites sent, invite accept, K-factor | Phase 3 |
| **Viral** | Shares / user / week; estimated impressions; install from share | Couples to WSC |

### What matters by stage

| Stage | Obsess over | Ignore / deprioritize |
|---|---|---|
| Phase 1 | Crash-free, share rate, honesty, D1 baseline | Revenue maximization |
| Phase 2 | D7 of sharers, WSC, pay intent | Vanity download spikes |
| Phase 3 | Organic install %, K-factor, content CAC | Premature enterprise deals |
| Phase 4–5 | Net revenue, marketplace GMV, retention quality | Feature count |

---

## Part 7 — If Cita became a huge company

### 3-year vision (ambitious but imaginable)

Cita is the recognized brand for anime quote cards across Android & iOS, with:

- Millions of MAU in anime-heavy regions  
- A consumer subscription that feels obvious to fans who post weekly  
- A small but real creator economy for packs/templates  
- A content channel that is itself a media property  
- Widgets as a major engagement surface  
- A catalog famous for *taste*, not just size  
- Revenue mix: subscriptions > ads; marketplace emerging

Cita is mentioned the way people mention Shazam or CapCut in their niches: “Just Cita it.”

### 5-year vision (ecosystem)

Cita is an **anime expression platform**:

- Apps + web studio + Discord/Telegram bots  
- Creator marketplace and seasonal brand drops  
- Licensing relationships for official art packs  
- API used by fan sites and community tools  
- Possibly adjacent products under the same brand (journals, printed “line of the year,” educational packs)  
- Data advantage: the world’s best understanding of which anime lines move people — used ethically for discovery, not creepy profiling  

**Company shape:** independent media-tech studio, or a strategic acquisition target for a larger anime/entertainment/social company that needs an expression layer.

### What “huge” does *not* mean

- Beating MyAnimeList at tracking  
- Becoming Character.AI  
- Winning by having the largest scraped quote dump  
- Living forever on interstitial ads

---

## Part 8 — Critical challenges & missing opportunities

### Competitors doing better (today)

- **Larger catalogs / favorites** (e.g. OtaQuotes-style apps): table stakes Cita lacks.  
- **Widgets + wallpapers + ritual** (inspiration-oriented anime quote apps): better habit design.  
- **Pinterest / TikTok:** better discovery of quote *images* without installing anything.  
- **Canva:** better general card making (but not anime-native).  
- **Animechan:** owns developer mindshare for quote APIs.

### Underserved market Cita can take

**Anime-native, mobile-first quote *creation* and *identity display*** — especially in markets where WhatsApp/Telegram status and Instagram Stories are the social fabric, and where English-only aesthetic apps under-serve local fandom languages.

### Strategic risks to name out loud

1. **Copyright** on backgrounds, character art, and “official-looking” cards.  
2. **Platform policy** if UGC or AI drifts NSFW.  
3. **Founder bandwidth** — content + product + growth is three jobs.  
4. **Premature social** — building feeds before cards are loved.  
5. **Ad addiction** — short-term cash that prevents Plus conversion.  
6. **Catalog stagnation** — offline JSON without an update pipeline becomes a fossil.

---

## Part 9 — Decision framework (use this weekly)

Before building anything, ask:

1. Does this increase **Weekly Shared Cards** or **D7 of sharers**?  
2. Does this make a Cita card more recognizable in the wild?  
3. Does this create a habit surface (widget/daily) or a growth loop (share/referral)?  
4. Would a user pay for this without resentment?  
5. Are we copying a database feature — or building expression?

If a feature is “nice for a quotes app” but fails these, **don’t build it**.

---

## Part 10 — Immediate recommendations (next 30 days)

1. Rewrite Play Store positioning around **shareable quote cards**, not “600+ anime.”  
2. Ship **Favorites + Search + better export card**.  
3. Start a **manual content channel** (even before studio v2) posting cards made *as if* Cita Studio exists — test creative.  
4. Instrument **share** as the primary event in Firebase.  
5. Draft **Cita Plus** value stack on paper; don’t code paywall until share loop moves.  
6. Plan catalog expansion with an editorial rubric (iconic > obscure filler).  
7. Kill the most hated ad surfaces.

---

## Closing

Cita should not try to be the biggest quote database on Earth.

Cita should try to be **the most loved way anime fans turn emotion into something they can hold, show, and return to.**

The Play Store listing is a door.  
The card is the product.  
The share is the growth.  
The daily ritual is the business.  
The community and taste graph are the moat.

Build toward that — and Cita can become a real company.  
Optimize toward “more quotes + more ads,” and it will remain what it is today: a live app that few people need.

---

*Document owner: Cita founding team*  
*Living strategy — revisit after Phase 1 metrics, not after every feature idea.*
