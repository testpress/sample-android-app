# Sample android app
Sample android app using tesptress android native sdk to play videos

## Adding dependency
### Add testpress maven repo to app module's `build.gradle`

```
repositories {
    maven {
        url "https://github.com/testpress/maven/raw/main/repo"
    }
}
```

### Add testpress player dependency to app project `build.gradle`

```
// use the latest available version
implementation 'com.testpress.player:player:1.0.12b'
```

### Enable Java 8 support

You also need to ensure Java 8 support is enabled by adding the following block to each of your app module's `build.gradle` file inside the `android` block:

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

## Using ProGuard

```
-keep class com.tpstream.player.models.* { *; }
```

## Documentation
* The [developer guide][https://developer.testpress.in/docs/video-embedding/player-sdk/android-native-sdk/getting-started] provides a wealth of information.
