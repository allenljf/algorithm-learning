# Android source-build runbook

The public showcase does not publish an Android APK, checksum, GitHub Release,
or Play Store track. To run Android, download this public repository and build
the debug APK locally. The app's Settings screen can select and persist a valid
API endpoint after installation.

## Prerequisites

- Git
- JDK 17 or newer
- Android Studio with Android SDK Platform 37 and its command-line tools, or an
  equivalent Android SDK installation
- An Android emulator or a USB-connected device with USB debugging enabled

If Gradle cannot find the SDK, create the ignored
`apps/multiplatform/local.properties` file with the local SDK path, for example:

```properties
sdk.dir=/absolute/path/to/Android/sdk
```

## Download and build

```sh
git clone https://github.com/allenljf/algorithm-learning.git
cd algorithm-learning/apps/multiplatform
./gradlew :composeApp:assembleDebug
```

The debug APK is written to
`composeApp/build/outputs/apk/debug/composeApp-debug.apk`. It is a local build,
not a project-published release artifact; no signing configuration or checksum
is needed for this path.

## Install and confirm the showcase

With an emulator or USB-debuggable device selected, install the locally built
APK using Android Debug Bridge:

```sh
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

Open **Algorithm Learning**. Confirm that the default endpoint can reach the
public showcase, or use **Settings** to save a reachable absolute HTTP(S) API
origin. A failed connection should remain editable and show its localized error;
do not reuse a session after changing the endpoint.

## Update or roll back source

To update, pull a newer revision and rerun `assembleDebug`, then reinstall with
`adb install -r`. To return to an earlier source revision, check out the chosen
known-good revision, rebuild, and reinstall it. If Android rejects a downgrade,
uninstall the newer locally built app first; this clears its locally persisted
endpoint override and session.

## Optional operator-only signing

The existing release-signing wiring is intentionally retained for operator use
outside this showcase. Its keystore, location, alias, and passwords must never
be committed, pasted, printed, or sent to an agent. The public source-build path
above does not require `android-release-signing.properties`, CI signing secrets,
or a signed APK.
