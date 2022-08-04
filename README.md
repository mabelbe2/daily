# Project Summary
This project is to test the video call connection started between two dailyDemo Android apps. 
## Appium
It uses two Appium server, each host a mobile driver, one as host, and one as guest.(Host is the first one opening the room, the guest is the second one, the naming is only for easy identification).
 
## Video
When both host and guest join the room, both drivers compare the current screenshot to previous successful video connection screenshot. Based on the similarity score produced by opencv, the drivers can tell when the connection started (both host and guest video are showed).
## Audio
Audio uses virtual audio devices like sunflower and Blackhole, mac's output points to host output audio, and mac's input points to guest input audio.
During the project run, a sound file is feed to the mac's/guest's input, once video is connected, we can check if the guest input sound is coming out of host's output by recording host's output channel. The recording is done by ffmpeg.

A silent sound file is used to compared to the record host output. The comparison is by compare audio fingerprint. If two silent files are compared, a comparison score would be 100. As long as the score is less than 100 (both files are identical), it means host has sound output, thus audio connection has started.
Audio fingerprint by Chromaprint and comparison by [JavaWuzzy](https://github.com/xdrop/fuzzywuzzy) (a port of the useful Python library [FuzzyWuzzy](https://github.com/seatgeek/fuzzywuzzy))
## Out of  Scope
For Audio, the project only test if host can receive guest input, but does not test the opposite way. In the current setup, it only works if mac's output and input are set to target channels. Since mac's output and input can only be set one at time, only one pair of input and output can be tested a time. To be able to test both way, might need a different setup or programmatically change mac input and output at real time if that's possible.

# Development environment
## Project framework
Java maven project using cucumber, JUnit and appium. All dependencies are under `pom.xml`
## Platforms
Running two Android 12 android emulators on mac Catalina.
# Project setup
## Java
Install openJDK 11 (for the project itself and SoundBoard)  
https://docs.microsoft.com/en-us/java/openjdk/older-releases#openjdk-11  
after installation, add `JAVA_HOME` environment variable to your shell startup script ~/.bashrc or ~/.zshrc depending on your shell
## Maven
Install the latest Maven  
https://maven.apache.org/download.cgi  
after installation, add `M2_HOME`environment variable to your shell startup script
## Node
Install node version 14+ (I am using v14.16.0)
## Appium
`npm install -g appium`
### Appium-doctor
This is helpful to check appium related installation  
`npm install -g appium-doctor`
### Appium Server
To build appium server programmatically, you need `APPIUM_PATH` and `NODE_PATH` in the shell startup script
#### ports
By default, the two appium server will run on port 23861 (host) and 20452 (guest). If you want to use different ports, you can change project's `config.properties`'s `hostAppiumServerPort` and `guestAppiumServerPort`

Sometimes if the project end before finish running everything, the appium server might still be running on the port and when you try to run the project again, it failed and mentioned port is already used in the appium server log file. In this case, you need to kill the process
```
lsof -n -i :<port in used> | grep LISTEN
kill <PID return from last command>
```
## IDE
Intellij https://www.jetbrains.com/idea/
### Preference
If you have more than one version of JDK, make sure JDK 11 is selected in  
Preferences | Build, Execution, Deployment | Build Tools | Maven | Runner For JRE field  
and  
File -> Project Structure -> SDK
### Plugins
`Cucumber for Java` and `Gherkin` are helpful for cucumber syntax highlight
## Android Studio
Install Android Studio.
### SDK Manager
Open SDK Manager -> SDK platform tab, click "show Package Details" checkbox on bottom right, under Android 12.0(S) select "Android SDK Platform 31" (first one), sources for Android 31(second one), Intel x86 Atom_64 System Image(5th one), and then click "SDK tools" tab, select "31.0.0", click "ok" to install if any of these are not yet installed.

# Video testing setup
## opencv
`npm i -g opencv4nodejs`  
It require cmake to install, if you don't have cmake, run  
`brew install cmake`  
This will take a while to finish the installation  
(I forgot if it is for opencv or ffmpeg, one of the long installation might end up in long error with many function body printed, and the fix might be reinstall xcode command line)

To verify this is installed properly, run appium-doctor, it should say something like
```  
info AppiumDoctor ### Diagnostic for optional dependencies starting ###  
info AppiumDoctor ✔ opencv4nodejs is installed at: /Users/mabelbe/.nvm/versions/node/v14.16.0/lib. Installed version is: 5.6.0
```  
If not, check the fix message below
```  
info AppiumDoctor ### Optional Manual Fixes ###  
info AppiumDoctor The configuration can install optionally. Please do the following manually:  
...  
```  

# Audio testing setup
Most of the setup are based on the tutorial https://www.headspin.io/blog/capturing-audio-output-during-testing-part-1 and https://www.headspin.io/blog/capturing-audio-output-during-testing-part-2, which covered Audio capture with appium.
## Virtual Audio devices setup
Install [Sunflower](https://github.com/mattingalls/Soundflower) and [Blackhole](https://github.com/ExistentialAudio/BlackHole)  
You might need to restart.  
Go to System Preferences, and you should see the devices under the "Output" and "Input" tabs. Sound Effects and Output should use the same channel. On Sound Effects, choose Sunflower(2ch) under play sound effects through dropdown, and on Output tab, choose Sunflower(2ch) also. On Input tab, choose BlackHole 2ch.

The channels does not need to be the exactly same like this, as long as sound effects and output are the same channel, and input use a different channel, and input needs to be blackhole channel as it is required by SoundBoard.
## ffmpeg  (for recording)
Install  
`brew install ffmpeg`
Also will take a while to install


Check the available devices  
`ffmpeg -f avfoundation -list_devices true -i ""`  
You should be able to all the sunflower and blackhole channel showed up under AVFoundation audio devices.  
Note down the number of the channel you used for host output, in the last section mentioned, I used sunflower(2ch) for host output. If the console output looks likes
```  
[AVFoundation input device @ 0x7f9427501240] [4] Soundflower (2ch)  
```  
Then the hostOutputDeviceNum is 4, go to project's `config.properties` and change `hostOutputDeviceNum` value to be 4.

## SoundBoard setup (passing sound to mic)
Using the open source [UniversalSoundBoard project](https://github.com/sethmachine/universal-sound-board), its usage in this project is to feed a sound file to the virtual microphone.   

Following the instruction [building instructions](https://github.com/sethmachine/universal-sound-board#Building) and [Running the server](https://github.com/sethmachine/universal-sound-board#running-the-server).   
I believe this project does not need to setup sink and source together.  (Though I did follow all the steps, so if you run into a problem, maybe you also need to do [setting up a sink](https://github.com/sethmachine/universal-sound-board#setting-up-a-sink) and [wiring the sink to the source](https://github.com/sethmachine/universal-sound-board#wiring-the-sink-to-the-source) in the given order.

Next follow [Setting up a source](https://github.com/sethmachine/universal-sound-board#setting-up-a-source).  For source, use the same channel setup for the guest input in "Virtual Audio devices setup" section in this README.md, in this example, BlackHole 2ch. Note the returned audioMixerId, set this audioMixerId value in the project `config.properties`'s `guestMicInputSourceId` field.

Test setup is success, open System Preferences ->sounds ->Input, make sure the source input channel is selected (Blackhole 2ch in this example), there should be fluctuation in input level while playing audio file by following  [Play an audio file through the microphone](https://github.com/sethmachine/universal-sound-board#play-an-audio-file-through-the-microphone)
# Emulator Setup
Both devices should be use API 31 (S, Android 12) as per the demo app requirement.
## Host Device
Before setting up emulator, set the System Preferences -> sound -> set Both Sound Effects and Output to sunflower 2ch(should match channel for  `hostOutputDeviceNum`) , Input to blackhole 16ch (this is an unused channel, just to avoid two device using same channel). If you want to change these channel setup for the emulator, you need to shut down and change the channels and then cold reboot, as Android emulator only set the audio and camera setting at the beginning, any changes you need will require cold reboot.

At Virtual Device Configuration, click Advanced Setting on the bottom left, and Camera setting: front camera -> emulated, back camera -> anything but emulated.
Create or cold reboot if existing devices.
### UDID
get UDID for the device after booted by checking `adb devices`, UDID should looks like "emulator-xxxx", xxxx part should match the port number on the top bar on the emulator. Note this UDID, and set it in project's `config.properties`'s `hostDeviceUDID`

## Guest Device
Before setting up emulator, set the System Preferences -> sound -> set Both Sound Effects and Output to blackhole 64ch(unused channel), mic blackhole 2ch (should match the channel for `guestMicInputSourceId`)

At Virtual Device Configuration, click Advanced Setting on the bottom left, and Camera setting: front camera -> emulated, back camera -> anything but emulated.

Create or cold reboot if existing devices, when the device started, go to the sidebar menu, click the 3 dot to bring up the menu, go to microphone tab, turn on the toggle named "virtual microphone uses host audio input". (When you shut down the emulator and boot it again, you may need to turn on this toggle again)

### UDID
Similar to Host Device, set guest's UDID in project's `config.properties`'s `guestDeviceUDID`

## Install/Build app
I have some storage issue on the emulator when trying to install the app programatically, so the project does not install the app to the emulator. An apk file is included in the project under `src/test/appFiles`, you can drag the file to emulator. If there is any issue, you may still need to run the app from android studio to install on emulators.

## Environment Variable
You should have these variables set in your startup script.
```
export NODE_PATH="/Users/mabelbe/.nvm/versions/node/v14.16.0/lib/node_modules"
export JAVA_HOME="/Library/Java/JavaVirtualMachines/microsoft-11.jdk/Contents/Home"
export M2_HOME="/Users/mabelbe/apache-maven-3.8.2/"
export PATH="$PATH:/Users/mabelbe/apache-maven-3.8.2/bin"
export ANDROID_HOME="/Users/mabelbe/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export PATH="$ANDROID_HOME/build-tools:$PATH"
export APPIUM_PATH="/Users/mabelbe/.nvm/versions/node/v14.16.0/bin/appium"
export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk
```
## Appium doctor diagnostic
run  `appium-doctor --android` to check all the necessary dependencies has been checked, and under optional dependencies opencv4nodejs and ffmpeg has been checked

## Running the project
During test run, make sure the universal sound board server is running.
```
git clone https://github.com/mabelbe2/daily.git
cd daily
mvn test
```
While running, keep both emulators on the front end, I find sometimes when they are running in the background, I would get errors like "Sytem UI is not responding" and stop the test from running

### Report
After the project finish running, there should be a cucumber report link that starts with  that looks like `https://reports.cucumber.io/reports/<some unique id>`, click on it will open up a html report in the browser that show the run tests with steps, results, screenshots if failure, stacktrace if failure.

### Logs
Aside report, you can also check the logs file saved in the project after run, under `src/test/logs`, which has the appium server logs and logcat logs for host and guest.

### Structures
test description file: `src/test/features/test.feature`
There are four test cases, first one is only for recording success screenshot, second one is a positive test case where both side video are connected and host can hear guest, third one is negative test case where video are not connected if one of them does not click join button, the fourth one is a negative test case where host cannot hear guest when guest turn off audio

appium server and driver building:`src/test/java/environment/AppiumSession.java`

audios files: `src/test/audios`
- host.wav : recording host output
- noMusic.mp4: silent sound file for silence check
- mic_sample_sound.wav: sound file to play to guest input

all the sounds plugin usage: `src/test/java/plugins`

screenshots and screenshot comparison: `src/test/screenshots`
