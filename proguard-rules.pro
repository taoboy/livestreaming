# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/macbook/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**
-dontwarn "com/google/ads/**"
-dontwarn "com/google/android/gms/analytics/**"
-dontwarn "com/google/android/gms/games/**"
-dontwarn "com/google/android/gms/maps/**"
-dontwarn "com/google/android/gms/panorama/**"
-dontwarn "com/google/android/gms/plus/**"
-dontwarn "com/google/android/gms/drive/**"
-dontwarn "com/google/android/gms/ads/**"
-dontwarn "com/google/android/gms/wallet/**"
-dontwarn "com/google/android/gms/wearable/**"
