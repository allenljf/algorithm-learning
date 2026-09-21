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

## Operator settings

The checked-in `com.algorithmlearning.iosApp` identifier is a non-secret
development placeholder. Before a device, archive, or TestFlight build, the
operator must replace it with their registered bundle identifier and select the
matching Apple Developer team, certificate, and provisioning profile in Xcode.
Those materials remain outside this repository.

`Info.plist` supplies the production HTTPS API default. An operator may change
`API_BASE_URL` to an absolute HTTP(S) origin for a development build; the shared
Settings UI validates and persists a user override locally. Production builds
must retain an HTTPS default.
