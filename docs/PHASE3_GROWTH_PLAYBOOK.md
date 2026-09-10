# Cita Phase 3 — Growth Playbook

Companion to `CITA_PRODUCT_STRATEGY.md`. Use this weekly.

## Owned content engine (non-negotiable)

**Goal:** Every day, publish 1–3 Cita cards that look native on TikTok / Reels / Shorts.

### Cadence
| Day | Content |
|---|---|
| Mon | Resolve mood — shonen motivation |
| Tue | Heartbreak — quiet/romance |
| Wed | Chaos — villains / dark lines |
| Thu | Comfort — found family |
| Fri | Seasonal / trending anime |
| Sat | Template flex (show Studio styles) |
| Sun | “Make yours” CTA + invite code |

### Production checklist (15–20 min/day)
1. Open Cita → Daily Drop or mood lane  
2. Pick **Stories** export + a strong template  
3. Save & share → download the JPEG  
4. Post with caption: line + character + soft CTA (“Made in Cita”)  
5. Pin comment with Play Store link + your invite code  

### Creative rules
- Face text readable in 1 second on mute  
- Prefer short quotes (<120 chars) for vertical  
- Always leave the Cita mark unless testing Plus/watermark  
- Don’t use official key art you don’t have rights to  

## ASO / Play Store listing (update when you ship v1.5+)

**Title (30):** `Cita — Anime Quote Cards`

**Short description (80):**  
`Make & share anime quote cards. Daily Drop, moods, offline quotes.`

**Long description (draft):**
```
Cita turns iconic anime lines into shareable cards.

• Offline quotes from popular series
• Card Studio styles (Classic, Midnight, Resolve, Heartbreak, Chaos)
• Stories & Feed export formats
• Daily Drop + home screen widget
• Mood lanes and favorites
• Invite friends to unlock Aurora

Search: anime quote maker, anime story quotes, anime quotes wallpaper cards
```

**Keywords to test:** anime quote maker, anime quotes, anime story quotes, otaku quotes, anime cards

## Referral loop (in-app)

1. Menu → **Invite friends** — shares code + Play link  
2. Friend installs → Menu → **Enter invite code**  
3. Friend unlocks **Aurora** template + 2 save credits  

Track in Firebase: `referral_shared`, `referral_redeemed`.

## Watermark / Plus

- Free cards include subtle **Cita** mark (growth billboard)  
- `CitaPlus.hideWatermark()` removes it (flag ready for Billing later)  
- Creator seeding: call `new CitaPlus(ctx).setPlus(true)` for gifted accounts  

## Localization

- Indonesian (`values-in`) shipped for core UI  
- Next candidates: Portuguese (BR), Spanish  

## Weekly metrics review

1. Weekly Shared Cards (WSC)  
2. % new users who share in 24h  
3. Referral redeem count  
4. Organic install %  
5. D7 of sharers vs non-sharers  
