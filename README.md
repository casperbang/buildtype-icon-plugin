# Buildtype icon plugin - for Android
This plugin for Android projects aims at providing a mechanism for generating launcher icons for non-release builds based on a master release icon.

## Introduction
All too often, intermediate build types (test, beta, preview, staging, develop etc.) are given a very poor launcher icon or even none at all. This likely happens because it isn't important enough for the developer to order graphics work or because the developer just can't be bothered. This has some annoying ramifications however, in that it either reflects poorly on the intermediate build (if a crap icon is supplied) or (if none are supplied at all) it becomes impossible to tell different build types apart on test devices.

The buildtype-icon-plugin can come to the rescue here! It will go through all variants (flavors and build types) and generate a dedicated icon based on the master release icon. The generated icon will have the build type name overlaid as a label to the lower 3'rd part of the icon.

![Amazon](../gh-pages/icon-samples/rejsekortscanner-buildtypes.png)

##Icon examples
The examples below are purely for illustrative purposes, please don't sue me because I overlaid text onto your companys logo. ;)

![Amazon](../gh-pages/icon-samples/amazon.png) 
![Angry birds](../gh-pages/icon-samples/angrybirds.png) 
![Avast](../gh-pages/icon-samples/avastantivirus.png) 
![BBC news](../gh-pages/icon-samples/bbcnewz.png) 
![Google Chrome](../gh-pages/icon-samples/chrome.png) 
![CNN news](../gh-pages/icon-samples/cnnnewz.png) 
![Dispickable](../gh-pages/icon-samples/dispicakleme.png) 
![Google Drive](../gh-pages/icon-samples/drive.png) 
![Dropbox](../gh-pages/icon-samples/dropbox.png) 
![Endomondo](../gh-pages/icon-samples/endomondo.png) 
![Evernote](../gh-pages/icon-samples/evernote.png)
![Excel](../gh-pages/icon-samples/excel.png) 
![Facebook](../gh-pages/icon-samples/facebook.png) 
![Firefox](../gh-pages/icon-samples/firefox.png) 
![Flipboard](../gh-pages/icon-samples/flipboard.png) 
![Google Earth](../gh-pages/icon-samples/googleearth.png) 
![Hulu](../gh-pages/icon-samples/hulu.png) 
![IMDB](../gh-pages/icon-samples/imdb.png) 
![Runtastic](../gh-pages/icon-samples/runtastic.png) 
![Rejsekortscanner](../gh-pages/icon-samples/rejsekortscanner.png) 
![Shazam](../gh-pages/icon-samples/shazam.png) 
![The Sims](../gh-pages/icon-samples/sims.png) 
![Skype](../gh-pages/icon-samples/skype.png) 
![Snapchat](../gh-pages/icon-samples/snapchat.png) 
![Spotify](../gh-pages/icon-samples/spotify.png) 
![Tinder](../gh-pages/icon-samples/tinder.png) 
![Twitter](../gh-pages/icon-samples/twitter.png) 
![Wimp](../gh-pages/icon-samples/wimp.png) 
![Word](../gh-pages/icon-samples/word.png) 
![Runtastic](../gh-pages/icon-samples/runtastic.png) 
![Wordfeud](../gh-pages/icon-samples/wordfeud.png) 
![Youtube](../gh-pages/icon-samples/youtube.png) 

##How does it work?
As should be somewhat apparent on the icon samples above, the plugin does the following:
- Designates the lower 3'rd of an icon for the label overlay region
- Applies a blur on this lower 3'rd and makes it 30% darker
- Tries to determine the transparent padding in the overlay section, to avoid writing onto transparent pixels
- Writes the label in the center of the available space with as large a font as possible

The above algorithm isn't bullet proof, in particular there are issues with non-regular icons and icons with transparency at the center (Opera, I'm looking at you). However, for rectangular and round icons (90-95% of the icons out there?), the result should be quite decent. For a more precise description of what goes on, [read the source code](https://github.com/casperbang/buildtype-icon-plugin/blob/master/src/main/java/com/bangbits/android/gradle/ImageStamper.java). :) Have a better approach, submit a patch. :)

##How can I use it?
First, make sure you can pull down the binary plugin by adding to your repository - I got bintray to add the plugin so as long as you have jcenter as a reposatory then you should be able to see the plugin. 

```
buildscript {
    dependencies {
        classpath 'buildtype-icon-plugin:1.0.4'
    }
}

Alternatively, you can also include it by adding my Maven reposatory, but it shouldn't be necessary:


```
buildscript {
    repositories {
        maven { url "https://dl.bintray.com/casperbang/maven/" }
    }
    dependencies {
        classpath 'buildtype-icon-plugin:1.0.4'
    }
}


```

Now apply the plugin in your build.gradle, so that it gets to install the task it needs:

```
apply plugin: 'buildtypeIcon'

```

You're ready to go. To see that the plugin is installed and active, invoke the 'gradle task' command:

```

Other tasks
-----------
...
generateBuildtypeIcons - Generates overlaid icons for non-release builds
...


```

You will see a bunch of tasks, but under "Other tasks" one of these should be called 'generateBuildtypeIcons'.

Invoking this task, will go through your project (flavorized or not) and generate new icons for these variants based on your master (release) icon:

```

~/$ gradle generateBuildtypeIcons
app:generateBuildtypeIcons
+-Generated app/src/debug/res/mipmap-hdpi/ic_launcher.png
+-Generated app/src/develop/res/mipmap-hdpi/ic_launcher.png
+-Generated app/src/preview/res/mipmap-hdpi/ic_launcher.png
+-Generated app/src/debug/res/mipmap-mdpi/ic_launcher.png
+-Generated app/src/develop/res/mipmap-mdpi/ic_launcher.png
+-Generated app/src/preview/res/mipmap-mdpi/ic_launcher.png
+-Generated app/src/debug/res/mipmap-xhdpi/ic_launcher.png
+-Generated app/src/develop/res/mipmap-xhdpi/ic_launcher.png
+-Generated app/src/preview/res/mipmap-xhdpi/ic_launcher.png
+-Generated app/src/debug/res/mipmap-xxhdpi/ic_launcher.png
+-Generated app/src/develop/res/mipmap-xxhdpi/ic_launcher.png
+-Generated app/src/preview/res/mipmap-xxhdpi/ic_launcher.png

```

You can then commit these icons and you're done. The plugin can then be removed, although it incurs no significant overhead to your build process and this way you can always update your icons when there is new release icon or a new build type.

##TODO
- Use a Gausian rather than kernel 3x3 avg blur, it would be better looking around the edges
- Start rendering largest text placed within lower 3'rd, and check if lower left and right pixel fall on a transparent pixel - if so, go one size down... continue doing this until it fits. For the above to work, box should be defined by left/right padding at sample 0.66 and lower padding defined by sample at 0.5.
- Inversion when background brightness is too bright?

##License
Copyright (c) 2015 Casper Bang <<casper@bangbits.com>>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.