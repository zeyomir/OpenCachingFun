OpenCachingFun
==============
This is an Android app that helps in geocaching. It is using opencaching.pl site.

What's up with that rewrite thing?
----------------------------------
Yup, I've decided to dump the old codebase and start from scratch. Don't even look at the older commits, it's garbage (well, you know, I was young...). Right now all efforts are focused on bringing 100% of the old functionality into the new version and releasing it.

How to contribute
=================
* if you are not a developer, you can contribute by suggesting/discussing new features/improvements or reporting bugs (with detailed description ;) )
* if you are familiar with Android SDK, feel free to fork me, pick some issue, implement necessary changes and create a pull request

This repository is using [git flow](http://nvie.com/posts/a-successful-git-branching-model/). Long story short: 
* *master branch* contains only stable versions that were published
* *develop branch* has the newest code base, on release the code from this branch is merged to master; **you will probably want to fork from this branch**
* *feature branches* contain commits associated with single feature/issue; after the work is finished, they get merged into develop branch; **you should create one for each issue you want to take on, and when the work is done open a pull request to your feature branch**

Working copy
============
You can download a working copy from [Google Play](https://play.google.com/store/apps/details?id=com.zeyomir.ocfun). It's free! You can also get the latest beta release from [here](http://mzawisza.pl/wp-content/uploads/ocFun.apk) or (even better) register as beta tester [here](https://betas.to/wX5x3Rj6).

License
=======
Source code is available to anyone under GPLv3.
