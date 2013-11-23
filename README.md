OpenCachingFun
==============
This is an Android app that helps in geocaching. It is using opencaching.pl site.

How to contribute
=================
* if you are not a developer, you can contribute by suggesting/discussing new features/improvements or reporting bugs (with detailed description ;) )
* if you are familiar with Android SDK, feel free to fork me, pick some issue, implement necessary changes and create a pull request

This repository is using [git flow](http://nvie.com/posts/a-successful-git-branching-model/). Long story short: 
* *master branch* contains only stable versions that were published
* *develop branch* has the newest code base, on release the code from this branch is merged to master; **you will probably want to fork from this branch**
* *feature branches* contain commits associated with single feature/issue; after the work is finished, they get merged into develop branch; **you should create one for each issue you want to take on, and when the work is done open a pull request to your feature branch**

To make work with git and git flow easy, I'm using great (and free) app called [Source Tree](http://www.sourcetreeapp.com/)- it has versions for Windows and Mac. On Linux you would probably go with [git flow extension](https://github.com/nvie/gitflow).

How to run/open in IDE
======================
If you haven't been using maven for android projects before (or maven at all):
* make sure you have maven installed (so you can invoke mvn from console)
* make sure all google/android-related libs are available in your local maven repository (use this nice [project](https://github.com/mosabua/maven-android-sdk-deployer#readme) to do so)

Now all you have to do is import project to your favorite IDE. I'm using intelliJ IDEA (and I recommend you to use it too at least for all your Android work) and there you just have to click _File_ -> _Import project_ and point it to the 'pom.xml' file in this repo.

If you are using Eclipse, it shouldn't be much different, look for something like 'import' and then 'existing maven project'.

And there, you are all set! 

If you wish to build and use this app, you will have to fill OKAPI and GoogleMaps api keys in `pom.xml` file (actually all project's configuration takes place here, changes in `AndroidManifest.xml` will be overwritten). 

**to run**

To compile and install the app on any connected device or running symulator just issue this command in main project dir:
`mvn clean install android:deploy`

**to debug**

Run the app with the above command, open Android Monitor, search for this app's process name and check the corresponding port.
Now, in your IDE of choice, configure remote debug on localhost and provide port number from Android Monitor.

Working copy
============
You can download a working copy from [Google Play](https://play.google.com/store/apps/details?id=com.zeyomir.ocfun). It's free!

License
=======
Source code is available to anyone under GPLv3.
