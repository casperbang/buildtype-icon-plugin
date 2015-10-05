# Buildtype icon plugin - for Android

## Introduction
This plugin for Android projects aims at providing a mechanism for generating launcher icons for non-release builds - as an alternative to having to provide these manually.

## Configuration
In order to use the plugin, all that's needed is inclusion of the dependency and the Maven
repository where it can be downloaded from. Modify your existing buildscript content as so:

```
buildscript {
    repositories {
        ...
        maven { url "http://nexus.ci82.trifork.com/content/repositories/releases" }
    }
    dependencies {
        ...
        classpath 'com.trifork.tpa:tpa-gradle-plugin:1.0.60'
    }
}


```


To actually use the plugin, it must be applied and a TPA DSL must configure how
the variants (flavors and build types) map to a TPA deployment. The example below
configures two distinct flavors 'alka' and 'falck' with each their TPA uploadUUID.
However, only the 'develop' build type will be published (pushed out actively), 
while other variants such as i.e. alkaRelease will have to be published manually
by using the user-interface of the TPA server application.

```
apply plugin: 'tpa'

tpa {
    server = 'tpa.trifork.com'
    defaultPublish = false
    productFlavors{
        falck {
            uploadUUID = '35771f0d-202c-47e9-9904-8aae8f4115bf'
        }
        alka {
            uploadUUID = 'baf3750c-efeb-430b-b523-0781a6b700a3'
        }
    }
    buildTypes{
        develop{
            publish = true
        }
    }
}


```



## Task examples
The plugin will analyze the android and TPA configuration, and generate tasks
accordingly. To see which tasks will be generated, execute a "gradle task".

```
The Perfect App tasks
---------------------
tpaInfo - Fetches info about current deploy of all variants
tpaInfoAlkaDebug - Fetches info about latest deploy of alkaDebug variant
tpaInfoAlkaDevelop - Fetches info about latest deploy of alkaDevelop variant
tpaInfoAlkaRelease - Fetches info about latest deploy of alkaRelease variant
tpaInfoFalckDebug - Fetches info about latest deploy of falckDebug variant
tpaInfoFalckDevelop - Fetches info about latest deploy of falckDevelop variant
tpaInfoFalckRelease - Fetches info about latest deploy of falckRelease variant
tpaDeployAlkaDebug - Deploys alkaDebug variant
tpaDeployAlkaDevelop - Deploys alkaDevelop variant
tpaDeployAlkaRelease - Deploys alkaRelease variant
tpaDeployFalckDebug - Deploys falckDebug variant
tpaDeployFalckDevelop - Deploys falckDevelop variant
tpaDeployFalckRelease - Deploys falckRelease variant


```


You can use the tpaInfo (or any variant version of it) to learn about the current
situation of TPA deployments. An example of running tpaInfo:

```
:app:tpaInfoAlkaDebug
No previous deployment of com.falck.fga.alka.debug found on server tpa.trifork.com
:app:tpaInfoAlkaDevelop
Current deploy information for variant alkaDevelop:
* Package name: com.falck.fga.alka.develop
* Size: 3,1 MB
* Published: true
* Uploaded on: Sep 23, 2015 3:01 PM
* VersionNo: 102
* VersionString: 0.1-51-DEVELOP
* Release notes: 
:app:tpaInfoAlkaRelease
No previous deployment of com.falck.fga.alka found on server tpa.trifork.com
:app:tpaInfoFalckDebug
No previous deployment of com.falck.fga.falck.debug found on server tpa.trifork.com
:app:tpaInfoFalckDevelop
Current deploy information for variant falckDevelop:
* Package name: com.falck.fga.falck.develop
* Size: 3,1 MB
* Published: true
* Uploaded on: Sep 24, 2015 6:01 AM
* VersionNo: 102
* VersionString: 0.1-51-DEVELOP
* Release notes: 
:app:tpaInfoFalckRelease
No previous deployment of com.falck.fga.falck found on server tpa.trifork.com


```


To actually deploy a variant to TPA, you would use the specific tpaDeploy task.
An example of deploying a new version of the flavor 'alka' and build type 'develop'
would look like this:

```
:app:assembleAlkaDevelop
:app:tpaInfoAlkaDevelop
Current deploy information for variant alkaDevelop:
* Package name: com.falck.fga.alka.develop
* Size: 3,1 MB
* Published: true
* Uploaded on: Sep 23, 2015 3:01 PM
* VersionNo: 102
* VersionString: 0.1-51-DEVELOP
* Release notes: 
:app:tpaDeployAlkaDevelop
Uploading VersionNo 103 of alkaDevelop variant
* Deploying alkaDevelop
* APK: /Users/clb-trifork/Development/FGA-Android/app/build/outputs/apk/app-alka-develop.apk
* Proguard: 
* Publish: true
* Uploading to: https://tpa.trifork.com/bff3750c-efeb-430b-b523-0781a6b700a3/upload
OK

BUILD SUCCESSFUL


```

Notice that the tpaDeployAlkaDevelop triggers the task assembleAlkaDevelop and 
tpaInfoAlkaDevelop to run first. This is done in order to avoid deploying an 
artifact with the *same* versionNo as the currently deployed version on the TPA server 
(which would generate an error after upload anyway). So if we were to execute the 
tpaDeployAlkaDevelop again with no change in versionNo, the following would happen:

```
:app:tpaInfoAlkaDevelop
Current deploy information for variant alkaDevelop:
* Package name: com.falck.fga.alka.develop
* Size: 3,1 MB
* Published: true
* Uploaded on: Sep 24, 2015 8:13 AM
* VersionNo: 103
* VersionString: 0.1-52-DEVELOP
* Release notes: 
:app:tpaDeployAlkaDevelop
VersionNo 103 of alkaDevelop variant already uploaded
:app:tpaDeployAlkaDevelop SKIPPED

BUILD SUCCESSFUL


```

As you can see, the tpaDeployDevelop task is now completely skipped, since it would
only fail anyway. Avoiding this failure scenario is paramount in a continuous integration 
setup where you wouldn't want Jenkins to fail a job just because it's up-to-date.


##TODO:
- Test non-flavor distribution
- Handle TpaLib config? (tpa.crashreporting.* properties)
- Handle library format (AAR)
- Test on a Windows box
- Upload progress monitoring
- Support for -Tpublish=true override?

