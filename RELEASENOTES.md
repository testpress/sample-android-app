# Release notes

### 1.0.14b [Jul 25, 2023]

* Add `TpStreamPlayer.load()` as a replacement for `TpStreamPlayerFragment.load()`. The load method in fragment will be deprecated in the next release.
* Added play and pause APIs in the TpStreamsPlayer to programmatically control the playback.
* Add new `TPStreamPlayerView`.
* Add support to Enable and Disable seekBar in `TPStreamPlayerView`.
* Add support to show and hide fast forward button and rewind button
* Fix Night theme in Resolution selection sheet and Download selection sheet.
* Fix duplicate resolution names in Resolution selection sheet.
* Upgraded media3 dependency to 1.0.2
* Add Marker support in `TPStreamPlayerView`.
* Move `com.tpstream.player.TpStreamPlayerFragment` to `com.tpstream.player.ui.TpStreamPlayerFragment`
* Move `com.tpstream.player.models.Video` to `com.tpstream.player.data.Video`
* Move `com.tpstream.player.TpStreamDownloadManager` to `com.tpstream.player.offline.TpStreamDownloadManager`
* Move `com.tpstream.player.models.DownloadStatus` to `com.tpstream.player.data.source.local.DownloadStatus`
* Move `com.tpstream.player.InitializationListener` to `com.tpstream.player.ui.InitializationListener`
* Code optimisation and refactoring

### 1.0.13b [Mar 14, 2023]

*   Remove playerFragment.playbackStateListener. Use player.setListener(TPStreamPlayerListener) instead
*   Rename TPPlayerListener to TPStreamPlayerListener
*   Rename OfflineVideoState to DownloadStatus
*   Rename OfflineVideoInfo to Video
*   Code optimisation and refactoring

### 1.0.12b [Feb 10, 2023]

*   Fix stop background music when the video starts playing
*   Fix app crash when turning off the internet while downloading video
*   Refactor

### 1.0.11b [Feb 4, 2023]

*   Add sentry error logs
*   Misc bug fixes
*   Refactor

### 1.0.10b [Jan 23, 2023]

*   Fix not able to replace expired DRM license key
*   Fix not able to download single track in non-DRM videos
*   Fix AES encrypted video not downloading

### 1.0.9b [Dec 29, 2022]

*   Fix not able to download AES encrypted video

### 1.0.8b [Dec 23, 2022]

*   Fix video not playing after minimize and reopening the app
*   Fix non-DRM video is not getting downloaded
*   Refactor

### 1.0.7b [Dec 20, 2022]

*   Misc bug fixes

### 1.0.5b [Dec 17, 2022]

*   Added Event Listener to player

### 1.0.4b [Dec 12, 2022]

*   Misc bug fixes

### 1.0.2b [Dec 7, 2022]

*   Added support for AES encrypted video

### 1.0.1b [Dec 6, 2022]

*   Added support to change resolution for video
*   Added support to full screen

### 1.0.0b

*   No notes provided.