# Share validation checklist (Pixel)

P0 after FileProvider. Protects Weekly Shared Cards.

## Automated (CI / local)

```bash
./gradlew testDebugUnitTest
```

- `ShareLinksTest` — UTM / invite URLs  
- `CatalogDepthTest` — thin popular titles have enough lines  
- `DailyLineSchedulerTest` — next-fire delay math  

## Manual on device (do once per release)

Device: Pixel (or equivalent API 33+). Build: debug APK installed.

1. **Save & share → Files / Photos**  
   Open any quote → Card Studio → Save & share → pick Files or Photos.  
   Expect: JPEG opens; toast “Card ready — share it”; no crash; file under app Pictures/Cita.

2. **WhatsApp**  
   Same flow → WhatsApp → send to Saved Messages / self.  
   Expect: image preview, no “unable to attach” / permission error.

3. **Instagram**  
   Same flow → Instagram Stories or Feed (if installed).  
   Expect: image loads into composer (Stories format preferred).

4. **WSC hint**  
   After a successful Save & share, Home / Favorites should show shares-this-week increment (counted when share Intent starts).

5. **Widget deep-link**  
   Tap Daily Drop widget → lands on Home Daily Drop.

6. **Remove Ads gate (debug)**  
   Menu → Remove ads → expect “coming soon” while `ENABLE_REMOVE_ADS_IAP=false`.

## Failures to watch

| Symptom | Likely cause |
|---|---|
| Share crashes / FileUriExposed | FileProvider path / authority mismatch |
| Blank attach in WhatsApp | Missing `FLAG_GRANT_READ_URI_PERMISSION` |
| No toast / no WSC bump | Save path failed before Intent |

Log tag hints: `QuoteSheetController`, `ViewToImage`.
