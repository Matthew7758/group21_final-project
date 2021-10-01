**Final Project Submission**
Title: MP
Group 21
Github: https://github.com/Matthew7758/group21_final_project
A simple music player for Android.
This app provides a basic audio player for Android with support for almost any audio format placed on the internal storage of Android devices.
Supported audio formats include: mp3, m4a, flac, aac, wav, and ogg files.
The player allows a user to select a song from the list of songs on their device then play it.
The player has controls for play/pause, skipping, and rewinding and fast forwarding by 10 seconds.

How we accomplished each task:
1. Design pattern: We implemented the MVC controller view for the PlayActivity class to keep device song available. To assess this, look at the PlayViewModel.kt file and the PlayActivity file.
2. Persistence: For persistent storage we made a file called save.txt in the files directory of the app and wrote to it the last path of the song that was played. This is used to resume a song if the player is exited without going back to the main song list screen. To check this, open the player once you have some audio files on the device in the Music directory, select a song, then close the app in the tasks view. When you re-poen the app, the song will start playing.
3. Mobile sensor: We harnessed the device microphone to capture internal audio in the device to display the wave visualizer. In order to fulfill a more concrete example we captured data from the accelerometer to shuffle songs if the device is shook in the play screen. To check the visualizer, simply click on a song. To check the shuffle feature, either shake your mobile device or open the dev tools in the virtual device and shake it through there. Note that it may not work as well in the virtual device, and you may have to be more aggressive with the shaking.
4. Network: We used android's DownloadManager to download a test file to the device. This uses the network and writes a file called test.mp3 to the Music directory. To test this, open the app for the first time, click the download button, then close the app. Relaunch the app and check to see if the song Impact Moderato is there.

NOTES ON IMPLEMENTATION:
If the song Impact Moderato does not show up, it may be possible that Android's MediaStorage hasn't indexed it yet. Either give the device time to index, or try rebooting the device.
Currently the application only supports files on LOCAL storage. Any files on the SD card will display, but will not be able to play because of how scoped storage works on Android 10+

Design Achievements:
In terms of a design achievement we made a button that disables itself if test files currently exist. We also implemented dynamically loading images from files into views which we haven’t done in previous assignments. This should be considered a design achievement as it makes the app layout nicer by allowing users to download a file once, then never again.
The second design achievement we did was creating a functional status bar. This element wasn’t used in previous assignments and contributes to this application by providing finer control over the song being played back. This should count as a design achievement as it was a completely new function that was nonexistent in prior projects and required research to implement.
A third design achievement we would argue for is the use of CardViews in the recycler view to make nicer views for each song. This neatly groups all the information per song into a single box with borders. This is new as we have not used CardViews in other projects.

Technical Achievements:
The first technical achievement we used was using permissions and a third party library to visualize music waveforms. We edited the Android manifest xml file to use the microphone permission. Then we added an implementation line in the build gradle file and created a WaveVisualizer view and coded it in the play activity. This should be considered a technical achievement as it uses a third party library we didn’t use prior, and it adds an interesting and useful component to the application.
The second technical achievement we did was using android legacy and existing permissions to manage the stored files on a device. We learned about scoped storage on android versions 10+ and used it to download files and read them. As previously stated, our networking component involves using Android’s DownloadManager to access URLs and save resources to the device. This would not have been possible without using storage permissions and therefore should be counted as an achievement. It also involved using database SQL like commands to only isolate audio files.
That third achievement involves using the DownloadManager android API to download files. This API was not one we used in previous projects.

