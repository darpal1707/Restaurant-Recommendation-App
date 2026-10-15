# Food Labrinth

A restaurant discovery and recommendation app for Android, written in **Kotlin** with
**Jetpack Compose** and **Material 3**.

This branch is a full rewrite of the original Java/XML app. It talks to the same Firebase project
and reads/writes the same Realtime Database nodes, so existing data (restaurants, reviews, photos)
keeps working.

## Features

| Tab | What it does |
| --- | --- |
| **Home** | Greeting, search, “Help me choose” questionnaire, cuisine shortcuts, trending, “Picked for you” and top-rated lists. Pull to refresh. |
| **Explore** | Search plus a grid of 24 cuisines. |
| **Map** | Every restaurant with coordinates on a Google Map; tap a pin for a preview card. Optional “my location”. |
| **Saved** | Restaurants you hearted and the cuisines you like (these drive recommendations). |
| **Profile** | Email/password sign-in & sign-up, Google sign-in, password reset, profile and sign-out. |

Restaurant detail: hero image, rating, price, categories, directions, share, embedded map, opening
hours, user photos (camera or gallery upload), reviews and a bottom sheet to write one.

“Help me choose” asks for meal, cuisines, vibe and budget. Cuisine is a hard filter; meal, vibe and
budget only *rank* results using Yelp `attributes` (`GoodForMeal`, `Ambience`,
`RestaurantsPriceRange2`) when a record has them, so sparse data doesn't produce empty results.

## Tech stack

- Kotlin 2.1, Jetpack Compose (BOM 2024.12), Material 3, light & dark theme
- Navigation Compose, ViewModel + `StateFlow`, coroutines
- Firebase Auth, Realtime Database, Cloud Storage (via the Firebase BoM)
- Credential Manager for Google sign-in
- Maps Compose, Coil, DataStore (Preferences), SplashScreen API
- Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`)

## Project structure

```
app/src/main/java/com/darpal/foodlabrinthnew/
├── data/
│   ├── model/        Restaurant, Review, Photo, Cuisine, questionnaire enums, RestaurantFilters (pure Kotlin)
│   ├── *Repository   Firebase + DataStore access
│   └── FirebaseExt   Query → Flow helpers
├── di/AppContainer   Manual dependency wiring
└── ui/
    ├── theme/        Colours, typography (Montserrat), shapes
    ├── components/   Cards, list items, rating, shimmer, empty/error states
    ├── navigation/   Routes and bottom-bar destinations
    └── home, explore, results, detail, decide, map, saved, profile, auth
```

## Building

Requirements: Android Studio (Ladybug or newer) or JDK 17 + Android SDK 35.

```bash
./gradlew testDebugUnitTest   # unit tests for parsing / filtering logic
./gradlew assembleDebug       # app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions (`.github/workflows/android.yml`) runs both on every push.

### Firebase / Google setup

- `app/google-services.json` is the existing Firebase config (package `com.darpal.foodlabrinthnew`).
- **Google sign-in** needs your signing certificate's SHA-1 registered in the Firebase console
  (Project settings → Your apps). Get it with `./gradlew signingReport`.
- **Maps** uses the key in `app/src/debug|release/res/values/google_maps_api.xml`; it must have the
  *Maps SDK for Android* enabled and allow this package + SHA-1.

> These keys were already committed in the original repository. Consider restricting them in the
> Google Cloud console, or moving them out of version control.

## Data model (Realtime Database)

| Node | Fields used |
| --- | --- |
| `business` | `business_id`, `name`, `address`, `city`, `state`, `postal_code`, `latitude`, `longitude`, `stars`, `review_count`, `categories`, `hours`, `is_open`, `attributes` |
| `reviews` | `business_id`, `text`, `stars`, `date`, `userid`, `user_name` (new, optional) |
| `photos` | `imageUrl`, `business_id`, `user_id` |
| `user/{uid}` | `userid`, `email`, `fullname` (the password is no longer stored) |

Reviews and photos are queried with `orderByChild("business_id")`; adding `".indexOn": ["business_id"]`
to those nodes in the database rules avoids downloading the whole node.
