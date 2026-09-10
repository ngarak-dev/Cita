# Cita Content Engine (manual)

Lightweight playbook for organic growth. **Not** an auto-poster bot — you make the card in Cita, then post.

Companion to `PHASE3_GROWTH_PLAYBOOK.md` (referral, ASO, metrics). This doc focuses on the daily create→post loop.

## North star

**Weekly Shared Cards (WSC):** distinct users who share ≥1 card per week.  
Your own posts should model that habit: one beautiful card that travels beats ten flat screenshots.

## Cadence (15–20 min/day)

| Day | Mood / angle |
|---|---|
| Mon | Resolve — shonen motivation |
| Tue | Heartbreak — quiet / romance |
| Wed | Chaos — villains / dark lines |
| Thu | Comfort — found family |
| Fri | Seasonal / trending title |
| Sat | Template flex (show Studio styles) |
| Sun | “Make yours” CTA + invite code |

Aim **1–3 posts/day** on TikTok / Reels / Shorts. Quality over volume.

## Workflow: make card → post

1. Open **Cita** → Daily Drop or a mood lane (or search a title you deepened).  
2. Open Card Studio → pick **Stories** export + a strong template (Ember / Midnight / Resolve / Neon…).  
3. **Save & share** → save the JPEG to camera roll.  
4. Post to Stories/Reels/TikTok.  
5. Caption = line + character · anime + soft CTA.  
6. Pin comment: Play Store link + your invite code (Menu → Invite friends).

### Caption templates

```
“{quote}”
— {character} · {anime}

Made in Cita ✨
```

```
The line that hits different today 👇
{quote}

Craft yours offline in Cita — anime quote cards.
```

```
POV: this line is your whole personality
{quote}

Card Studio → Stories export → post.
Link in bio / comments.
```

## Creative rules

- Text readable in **1 second** on mute.  
- Prefer quotes **&lt;120 characters** for vertical.  
- Keep the **Cita** watermark unless testing Plus.  
- Use **abstract covers / lettermarks** only — never unlicensed character key art.  
- One idea per post; don’t stack stats or schedule clutter.

## Links & UTM (in-app helper: `ShareLinks`)

Use tagged Play URLs so installs from posts are attributable:

```
ShareLinks.contentPlayUrl("wsc_mon_resolve")
→ …/details?id=me.ngarak.cita&utm_source=tiktok&utm_medium=organic&utm_campaign=wsc_mon_resolve
```

Invite shares use `ShareLinks.invitePlayUrl(code)` (referrer + UTM). Swap `utm_campaign` per series (e.g. `wsc_mon_resolve`, `invite_sunday`).

Example raw URL:

`https://play.google.com/store/apps/details?id=me.ngarak.cita&utm_source=tiktok&utm_medium=organic&utm_campaign=content_engine`

## Weekly review (with WSC)

1. How many cards did **you** post?  
2. Firebase / in-app: shares this week (WSC hint on Home / Favorites).  
3. Which template + mood got saves/comments? Double down next week.  
4. Referral redeems from pin comments.

## Don’t

- Scrape or claim official studio art.  
- Spam the same card across 10 accounts with no caption craft.  
- Position Cita as “600+ anime database” — position as **quote studio**.

See also: `PLAY_LISTING.md` for store copy when you ship.
