# Flutter application

This directory is reserved for the shared Flutter Web, Android, and iOS client.
Its SDK is pinned to Flutter 3.47.0 in [`.fvmrc`](.fvmrc).

After ALG-003 creates the application root, install the pinned SDK and use the
FVM entry point from this directory:

```sh
fvm install
fvm flutter run -d chrome
```

No Flutter project or product source is created by ALG-001.
