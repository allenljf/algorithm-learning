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

## TestFlight external-test release

TestFlight is the supported external iOS distribution channel for this milestone;
App Store submission is not part of it. The following actions are operator-only:
Apple Developer membership, App Store Connect access, bundle-ID registration,
certificate and provisioning-profile selection, archive signing, upload, and
tester invitations. Do not place any of those credentials or private keys in the
repository or provide them to an agent.

1. In Xcode, select the registered production bundle identifier, Apple Developer
   team, and matching distribution signing configuration. Confirm
   `API_BASE_URL` remains the production HTTPS origin.
2. Increase both Release build settings before every upload: `MARKETING_VERSION`
   for the user-visible version and `CURRENT_PROJECT_VERSION` for the monotonic
   build number. The checked-in `Info.plist` reads those values directly.
3. Create a signed archive from `apps/multiplatform`:

   ```sh
   xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
     -configuration Release archive -archivePath build/iosApp.xcarchive
   ```

4. Validate the archive in Xcode Organizer, then upload it to App Store Connect.
   Wait for processing; submit it to TestFlight external testing and provide the
   required beta review information. Do not invite an external tester until any
   required beta review is approved.
5. Invite at least one external tester. They install the build from TestFlight,
   launch it on a physical device, and confirm it reaches the production endpoint
   (or explicitly select a validated override in Settings). Record the archive
   version/build, upload date, App Store Connect processing result, beta-review
   result, invitation, and installation outcome outside this repository.

TestFlight builds expire after Apple's current TestFlight validity period. Before
expiry, increase the build number, upload a replacement, complete any required
external-test review, and invite or notify testers again. To roll back, stop
testing the faulty build in App Store Connect and distribute the most recent
known-good TestFlight build; do not reuse a build number or alter a shipped
archive's signing identity.
