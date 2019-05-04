# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Softwares\Android\sdk/tools/proguard/proguard-android.txt
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

-dontwarn org.jaudiotagger.fix.**
-dontwarn org.jaudiotagger.test.**
-dontwarn org.jaudiotagger.audio.**
-dontwarn org.jaudiotagger.logging.**
-dontwarn org.jaudiotagger.tag.**

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

#renderscript
-keep class android.support.v8.renderscript.** { *; }

#searchview
-keep class android.support.v7.widget.SearchView { *; }

-keep class com.simplecityapps.** { *; }

-keep class org.jaudiotagger.** { *; }

-keep class com.google.code.gson.** { *; }

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn org.conscrypt.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

