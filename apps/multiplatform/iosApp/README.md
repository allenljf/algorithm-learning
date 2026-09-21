# iOS app shell

`iosApp` is the Xcode entry point for the shared Compose UI. It contains no
product logic, credentials, certificates, provisioning profiles, or signing
keys. `composeApp` produces the `ComposeApp` framework and this shell invokes
its `MainViewController`.

## Local simulator build

On macOS with Xcode installed, from `apps/multiplatform` run:

```sh
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphonesimulator -configuration Debug build CODE_SIGNING_ALLOWED=NO
```

Xcode's framework build phase calls `:composeApp:embedAndSignAppleFrameworkForXcode`.
Open `iosApp/iosApp.xcodeproj` to select a simulator and run it from Xcode.

## Demonstration scope

The checked-in `com.algorithmlearning.iosApp` identifier is a non-secret
development placeholder. This showcase supports opening the checked-in project
and running the shared UI in the iOS Simulator. It does not require an Apple
Developer account, device signing, archive, upload, review, external tester, or
public iOS artifact.

`Info.plist` supplies the production HTTPS API default. An operator may change
`API_BASE_URL` to an absolute HTTP(S) origin for a development build; the shared
Settings UI validates and persists a user override locally. Production builds
must retain an HTTPS default.

## Run in Xcode

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Choose an iOS Simulator as the run destination.
3. Run the `iosApp` scheme. The shell builds the shared `ComposeApp` framework
   and opens the same Compose UI used by the other client targets.

This is intentionally the complete iOS showcase path. TestFlight, App Store
submission, and public IPA distribution are out of scope.
