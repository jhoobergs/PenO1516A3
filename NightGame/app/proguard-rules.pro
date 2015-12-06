# Keep a fixed source file attribute and all line number tables to get line
# numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-keepattributes SourceFile,LineNumberTable
-dontskipnonpubliclibraryclasses

########################
#####              #####
#####     LIBS     #####
#####              #####
########################

-dontwarn okio.**
-dontwarn org.joda.time.**

###########################
###   Square's OkHttp   ###
###########################

#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.okhttp.**

#############################
###   Square's Retrofit   ###
#############################
-keep class be.cwa3.nightgame.Data.** { *; }

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

################################
###   Google Play Services   ###
################################
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-dontwarn com.google.android.gms.**