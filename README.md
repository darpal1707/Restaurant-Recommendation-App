# Food Labrinth

A restaurant discovery and recommendation app for Android, written in **Kotlin** with
**Jetpack Compose** and **Material 3**.

Restaurant data comes from **OpenStreetMap** through the free
[Overpass API](https://wiki.openstreetmap.org/wiki/Overpass_API): no account, server or API key is
needed for it.

## Features

| Tab | What it does |
| --- | --- |
| **Home** | Area picker (“Near me” or a preset city), search, “Help me choose”, cuisine shortcuts, closest places, “Picked for you” and more places around you. Pull to refresh. |
| **Explore** | Search plus a grid of 24 cuisines. |
| **Map** | Every place on a Google Map; tap a pin for a preview card. Optional “my location”. |
| **Saved** | Places you hearted (kept even when you switch area) and the cuisines you like, which drive recommendations. |

Restaurant detail: place type and distance, cuisines, amenities (outdoor seating, takeaway, Wi-Fi…),
directions, call, website, share, embedded map, opening hours, and a link to the entry on
openstreetmap.org.

“Help me choose” asks for meal, cuisines and distance. Cuisine and distance filter; the meal only
*ranks* results, using the OSM `opening_hours` tag where present (many places don't have it).

### What OpenStreetMap does and doesn't provide

OSM has names, coordinates, cuisine, opening hours, address, phone, website and amenities.
It has **no ratings, reviews, prices or photos**, so the app doesn't show them. Coverage varies a
lot by city. The public Overpass server is rate limited, so results are cached on the device for
24 hours (pull to refresh forces a new request).

The app must credit OSM (ODbL licence); this is shown on Home, results, detail and the map.

### Accounts, reviews and photos (switched off)

Sign-in (email / Google), reviews and photo uploads are implemented against Firebase but disabled
with `FeatureFlags.ACCOUNTS_ENABLED = false`: the Profile tab and those sections are hidden and
Firebase is never initialised. Set the flag to `true` once a Firebase project (or another backend)
is available again.

## Tech stack

- Kotlin 2.1, Jetpack Compose (BOM 2024.12), Material 3, light & dark theme
- Navigation Compose, ViewModel + `StateFlow`, coroutines
- OpenStreetMap / Overpass API over plain `HttpURLConnection`, platform `LocationManager`
- Firebase Auth, Realtime Database, Cloud Storage (kept for the disabled accounts feature)
- Credential Manager for Google sign-in
- Maps Compose, Coil, DataStore (Preferences), SplashScreen API
- Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`)

## Project structure

```
app/src/main/java/com/darpal/foodlabrinthnew/
├── data/
│   ├── model/        Restaurant, OsmParser, OpeningHours, Cuisine, Area, RestaurantFilters (pure Kotlin)
│   ├── osm/          Overpass client and on-disk cache
│   ├── *Repository   OSM, DataStore and (disabled) Firebase access
│   └── LocationProvider
├── di/AppContainer   Manual dependency wiring
└── ui/
    ├── theme/        Colours, typography (Montserrat), shapes
    ├── components/   Cards, list items, rating, shimmer, empty/error states
    ├── navigation/   Routes and bottom-bar destinations
    └── home, explore, results, detail, decide, map, saved, profile + auth (disabled)
```

## Building

Requirements: Android Studio (Ladybug or newer) or JDK 17 + Android SDK 35.

```bash
./gradlew testDebugUnitTest   # unit tests for OSM parsing, opening hours and filtering
./gradlew assembleDebug       # app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions (`.github/workflows/android.yml`) runs both on every push.

### Google Maps key

The map uses the key in `app/src/debug|release/res/values/google_maps_api.xml`. It must have the
*Maps SDK for Android* enabled and allow this package + your signing SHA-1
(`./gradlew signingReport`). If that key belonged to the old Firebase/Google Cloud project and is
gone, map tiles will be blank; everything else still works.

> The Maps key and `app/google-services.json` were already committed in the original repository.
> Consider restricting or rotating them.
