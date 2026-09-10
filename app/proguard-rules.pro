# Cita release R8 rules — keep reflection/Gson surfaces only.
# Prefer narrow keeps so Play obfuscation/shrinking scores stay high.

# Crashlytics / readable stacks
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile
-keep public class * extends java.lang.Exception

# Gson — TypeToken + models (quotes.json / packs.json field names)
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class me.ngarak.cita.models.** { <fields>; <init>(...); }
-keep class me.ngarak.cita.CollectionsStore$Collection { <fields>; <init>(...); }

# Enums persisted via name() / valueOf in SharedPreferences
-keepclassmembers enum me.ngarak.cita.Mood {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers enum me.ngarak.cita.cards.CardTemplate {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Retrofit API (unused for shipping catalog, still in APK)
-keep,allowobfuscation,allowshrinking interface * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# WorkManager worker constructed by class name
-keep class me.ngarak.cita.notify.DailyLineWorker { <init>(...); }

# Optional: suppress noisy notes
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
