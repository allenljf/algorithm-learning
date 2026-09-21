# Android direct-release APK runbook

This repository distributes Android builds as a versioned, directly downloadable
release APK. It does not publish to a Google Play track. The production endpoint
is embedded by the existing release build configuration; users can still choose
and persist another valid endpoint from Settings.

## Operator boundary

The Android keystore, its location, alias, and passwords are operator-only. Do
not commit, paste, print, or send them to an agent. The repository contains only
[`android-release-signing.properties.example`](android-release-signing.properties.example).
Copy it to `android-release-signing.properties` in this directory and fill all
four values locally; that file is ignored by Git. Alternatively, CI may supply
the four Gradle project properties through its secret mechanism:

- `androidReleaseStoreFile`
- `androidReleaseStorePassword`
- `androidReleaseKeyAlias`
- `androidReleaseKeyPassword`

Signing activates only when all four values are present. Supplying any incomplete
set fails the build before an artifact is created. With none present,
`assembleRelease` creates an unsigned release APK and `assembleDebug` always
works without signing material.

## Build a release

Before each external release, choose a monotonic integer `VERSION_CODE` and a
human-readable `VERSION_NAME`. They can be passed as Gradle properties (for
example, `-PandroidVersionCode=<next integer>` and
`-PandroidVersionName=<release name>`); the values become Android's installed
version metadata. Never reuse or decrease a released version code.

From this directory, with the operator signing configuration available:

```sh
./gradlew :composeApp:assembleRelease \
  -PandroidVersionCode=<next integer> \
  -PandroidVersionName=<release name>
```

The signed APK is written to
`composeApp/build/outputs/apk/release/composeApp-release.apk`. Create and publish
a checksum beside the APK:

```sh
shasum -a 256 composeApp/build/outputs/apk/release/composeApp-release.apk
```

Record the artifact filename, version code/name, SHA-256, release date, and the
previous known-good APK in the release notes. The checksum must be obtained from
the exact file that is uploaded.

## External installation and rollback

1. Give the downloader the APK, its version, and its SHA-256 through the chosen
   release channel. They verify the checksum before opening the APK.
2. Android may require enabling **Install unknown apps** for the browser or file
   manager that opens the download. This permission is local to that installer;
   no Play Store enrollment is implied.
3. The downloader installs the APK, opens it, and confirms the production
   endpoint (or an intentionally selected override) is reachable.

For rollback, publish the recorded previous known-good signed APK and checksum.
An Android downgrade may require uninstalling the newer app first, which clears
its local endpoint override and session; users then reinstall and choose their
endpoint again if needed. Do not try to repair a released artifact by changing
the keystore or by reusing a version code.
