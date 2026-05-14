
# Namma-Vastra

Namma-Vastra is an Android app for artisan discovery, saree listing, inquiry management, and moderated weaver storytelling. It is built with Kotlin, Jetpack Compose, Firebase Authentication, and Supabase.

## Stack

- Kotlin
- Jetpack Compose
- Firebase Authentication
- Supabase REST + Storage
- Ktor
- Coil

## Project setup

1. Copy `local.properties.example` to `local.properties`.
2. Fill in your Android SDK path and backend values.
3. Open the project in Android Studio.

Required local properties:

- `GEMINI_API_KEY`
- `SUPABASE_URL`
- `SUPABASE_PUBLISHABLE_KEY`
- `GOOGLE_WEB_CLIENT_ID`
- `FIREBASE_PROJECT_ID`
- `FIREBASE_APPLICATION_ID`
- `FIREBASE_API_KEY`
- `FIREBASE_STORAGE_BUCKET`
- `FIREBASE_SENDER_ID`
- `ADMIN_EMAIL`

## Backend resources

Supabase resources expected by the app:

- bucket: `sarees`
- tables:
  - `trends`
  - `sarees`
  - `stories`
  - `weavers`
  - `story_submissions`

Use `supabase_admin_setup.sql` to create the moderation-related tables and policies.

## App flow

- Firebase Authentication handles email/password and Google sign-in.
- Supabase stores loom gallery rows, story submissions, published stories, weaver profiles, and uploaded images.
- Weaver stories are submitted for review.
- The admin account defined by `ADMIN_EMAIL` can approve or remove submitted content inside the app.

## Notes

- `local.properties` is intentionally ignored and should not be committed.
- The local `gradle-8.7` folder and `gradle-8.7-bin.zip` are ignored for GitHub cleanup.
- If you want a standard Gradle wrapper in the repo, generate it locally in Android Studio or with a working Gradle install.
=======
# Namma-Vastra
>>>>>>> e21d9a47f4e0e0ca5cd17e6b9b975cced699832d
