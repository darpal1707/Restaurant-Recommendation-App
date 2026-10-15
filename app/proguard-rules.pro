# Firebase Realtime Database reads/writes plain maps in this app, so no model classes need keeping.
# Credential Manager (Google Sign-In)
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
