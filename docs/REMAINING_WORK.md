# Cita — Remaining work

Living backlog after the high-leverage “Next” pass (share validation, Plus paper + gated Remove Ads, catalog sprint II, Daily Line push, widget customize, pt-BR, ASO).

**North star:** Weekly Shared Cards (WSC) — distinct users who share ≥1 card/week.  
**Strategy source:** `CITA_PRODUCT_STRATEGY.md`

---

## Shipped recently (do not re-build)

- Dark charcoal/yellow UI + immersive quote detail  
- Hash/procedural covers, stickers, collections, Card Studio templates  
- Catalog depth (~1,243 quotes after sprint II), Play listing draft, content engine doc  
- WSC tracker + Home/Favorites hints  
- Branded splash (light + night)  
- **FileProvider** share URIs + share validation checklist  
- Widget → Daily Drop deep-link + **widget mood/anime configure**  
- Last-used template + Stories format memory  
- Share haptic + success toast  
- Daily Drop day streak + **opt-in Today’s line notifications** (~9:00)  
- Recently viewed on Home  
- Copy Play UTM link + invite code (menu + About)  
- Home tips / empty-state hints / Favorites → Daily Drop CTA  
- **Remove Ads IAP wiring** behind `ENABLE_REMOVE_ADS_IAP=false` + `CITA_PLUS_VALUE_STACK.md`  
- **Portuguese (pt-BR)** strings  
- Ops scaffolds: `SHARE_VALIDATION.md`, `CONTENT_ENGINE_TRACKER.md`, `SHARER_INTERVIEWS.md`

---

## Next (ops / gated — you do these outside the IDE)

| Priority | Item | Status |
|---|---|---|
| P0 | **Manual share validation on Pixel** (WhatsApp / Instagram / Files) | Checklist: `SHARE_VALIDATION.md` |
| P0 | **Play listing rewrite live** | Copy ready in `PLAY_LISTING.md` — paste when shipping |
| P1 | **Run content engine 7 days** | Tracker: `CONTENT_ENGINE_TRACKER.md` |
| P1 | **Talk to 10–20 sharers** | Script: `SHARER_INTERVIEWS.md` |
| P1 | **Flip Remove Ads IAP** after share rate moves | Set `ENABLE_REMOVE_ADS_IAP=true` + Play product `cita_remove_ads` |

---

## Later product (implement when gates pass)

| Item | Gate |
|---|---|
| Push time picker (not just ~9:00) | Reminders retention data |
| Spanish (`es`) locale | After pt-BR / ID usage |
| Catalog sprint III (long-tail 1-quote titles) | After seed titles stay deep |
| Cita Plus subscription paywall | After Remove Ads converts |
| HD / no-watermark exclusive templates | Plus launch |

---

## Later (Phase 3–5 — do not start yet)

- iOS app / web Card Studio  
- Discord bot + server seeding  
- Pack marketplace / creator rev-share  
- Seasonal editorial CMS / episode companion  
- Social feed, likes, follows, public remix  
- Accounts + cloud sync  
- Brand / licensed art partnerships  
- AI chat / generative fake quotes / character likeness  
- Full Canva-style editor  
- Re-enable app-open ads  
- Massive scraped catalog (prefer curated depth)

---

## Known gaps / tech debt

- WSC is counted when save→share **starts**, not when the user completes the system share sheet  
- Double `quote_view` possible (list → detail → sheet)  
- Keep ID / pt-BR in sync when adding English keys  
- Abstract covers only — no licensed character art by design  
- AdMob account approval still blocks live fill (debug uses sample units)  
- versionCode **8** / **1.7** (Billing Library 9.1 + R8 minify)  
- `ENABLE_REMOVE_ADS_IAP` defaults **false** until WSC moves  

---

## Decision checklist (before any new feature)

1. Does it raise **WSC** or **D7 of sharers**?  
2. Does it make a Cita card more recognizable?  
3. Habit (widget/daily) or growth (share/referral)?  
4. Would a fan pay for this without resentment?  
5. Expression — or just another database feature?

If it fails these, park it here and don’t build it.
