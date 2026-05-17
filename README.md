# IFT — Tamil Quran (MVC Rebuild)

Production-quality Android app for **IFT Chennai** Tamil Quran translation, rebuilt with strict MVC architecture, AndroidX, Material 3, Room, and Retrofit.

## Features

- Offline Quran database (Tamil + Arabic) with 114 suras
- Sura index, ayah reader, quick goto, last-read bookmark
- Tamil search with vowel normalization and paginated results
- Favorites (liked ayahs)
- Display settings (Arabic/Tamil toggles, font sizes)
- IFT books and Samarasam magazine catalogs with PDF download
- Navigation drawer with website, contact, and rate app links

## Architecture

```
view/        → Activities, Fragments, Adapters (UI only)
controller/  → Business logic, validation, navigation coordination
model/       → Room DB, Repositories, Retrofit API, Preferences
utils/       → TamilNormalizer, ArabicTextHelper, FontManager
```

Manual DI via `AppContainer` provides `QuranRepository`, `BookCatalogRepository`, and executors.

## Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35

## Setup

1. Clone this repository.
2. Ensure assets are present (copied from legacy project):
   - `app/src/main/assets/alquran.zip`
   - `app/src/main/assets/font/*.ttf`
3. Open the project in Android Studio and sync Gradle.
4. Run on API 26+ device or emulator.

## Build

Create `local.properties` with your SDK path (Android Studio usually does this automatically):

```properties
sdk.dir=/path/to/Android/sdk
```

```bash
./gradlew assembleDebug
./gradlew test
```

Build verified: debug APK and unit tests pass.

## Application ID

`com.tamilquran.ift`

## API Endpoints (HTTP)

- Books manifest: `http://apps.ift-chennai.org/apps/books/android_iftbooks.txt`
- Samarasam manifest: `http://apps.ift-chennai.org/apps/samarasam/android_samarasam.txt`

Cleartext traffic is allowed only for `apps.ift-chennai.org` via network security config.

## License

Content © IFT Chennai. App structure rebuilt for maintainability.
