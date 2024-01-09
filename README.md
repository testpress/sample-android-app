# Sample android app
Sample android app using tesptress android native sdk to play videos

### Add testpress maven repo to app module's `build.gradle`

```
repositories {
    maven {
        url "https://github.com/testpress/maven/raw/main/repo"
    }
}
```

### Add player dependency to app project `build.gradle`

```
// Use the latest version available for integration (this sample app is integrated with Testpress).

// To integrate the Testpress player
implementation 'com.tpstreams.player:player:3.0.13b'
```

### Enable Java 8 support

You also need to ensure Java 8 support is enabled by adding the following block to each of your app module's `build.gradle` file inside the `android` block:

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

### Using ProGuard

```
-keep class com.tpstream.player.** { *; }
```

## Documentation
* The developer guides for both [Testpress] and [TPStreams] provide a wealth of information.

[Testpress]: https://developer.testpress.in/docs/video-embedding/player-sdk/android-native-sdk/getting-started
[TpStreams]: https://developer.tpstreams.com/docs/mobile-sdk/android-native-sdk/getting-started