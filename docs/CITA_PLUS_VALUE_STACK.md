# Cita Plus — value stack (paper)

Draft monetization before flipping `BuildConfig.ENABLE_REMOVE_ADS_IAP`.  
North star still: **Weekly Shared Cards (WSC)**. Do not turn on paywall until share habit moves.

## Free forever

- Browse quotes offline  
- Daily Drop + home widget  
- Card Studio (core templates)  
- Favorites & collections  
- Invite → Aurora unlock  
- Starter ad-free saves + rewarded ads for more  

## One-time: Remove Ads (`cita_remove_ads`)

**Price intuition:** $2.99–$4.99 one-time (localize later).

**Unlocks**
- No banners / support interstitial / save-gate rewarded ads  
- Unlimited saves without watching ads  
- Watermark stays (Plus owns watermark) — keep Remove Ads cheap & honest  

**Ship gate**
1. WSC or share rate trending up for ≥2 weeks  
2. Play product `cita_remove_ads` created (unpublished until flag)  
3. Set `ENABLE_REMOVE_ADS_IAP` to `true` in `app/build.gradle`  
4. Soft-launch to 10–20% of users if possible  

Code is already wired behind the flag (`RemoveAdsBilling`, `CitaPlus.adsRemoved()`, `AdsPolicy`).

## Subscription: Cita Plus (later — do not build paywall UI yet)

**Price intuition:** $2.99–$4.99/mo or $19.99–$29.99/yr.

**Stack**
| Benefit | Notes |
|---|---|
| Everything in Remove Ads | Bundle or credit purchase |
| Hide watermark | Already in `CitaPlus.hideWatermark()` |
| Pro / seasonal templates | Beyond Aurora |
| Widget packs / HD export | Phase 4 |
| Exclusive editorial packs | When CMS exists |

## What we will not sell early

- Extra scraped quotes as “premium catalog”  
- Fake character art / likeness  
- Aggressive interstitial walls that punish sharers  

## Validation questions (talk to sharers)

1. Would you pay once to never see ads while making cards?  
2. Would you pay monthly for no watermark + exclusive styles?  
3. What would feel unfair?

See also: `REMAINING_WORK.md`, `CITA_PRODUCT_STRATEGY.md`.
