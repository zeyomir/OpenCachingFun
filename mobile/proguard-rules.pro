# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/zeyomir/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# butterknife
-keep public class * implements butterknife.internal.ViewBinder { public <init>(); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# retrofit
-dontnote okhttp3.**, okio.**, retrofit2.**
-dontwarn okio.**, okhttp3.**, retrofit2.**
-keep class retrofit2.** { *; }

# otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# picasso
-dontwarn com.squareup.okhttp.**

# gson
-keep class sun.misc.Unsafe { *; }
-keep class com.zeyomir.ocfun.network.request.** { *; }
-keep class com.zeyomir.ocfun.network.response.** { *; }

# active android
-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keepclassmembers class * extends com.activeandroid.Model {
    <fields>;
}
-keep class * extends com.activeandroid.serializer.TypeSerializer

#leakCanary
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }
# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification

# android iconics / material drawer
-keep class .R
-keep class **.R$* {
    <fields>;
}

-keep class android.support.v7.widget.SearchView { *; }
