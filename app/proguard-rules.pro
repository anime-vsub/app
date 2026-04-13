# Jsoup
-keeppackagenames org.jsoup.nodes

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Supabase / Ktor
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class git.shin.animevsub.**$$serializer { *; }
-keepclassmembers class git.shin.animevsub.** { *** Companion; }
-keepclasseswithmembers class git.shin.animevsub.** { kotlinx.serialization.KSerializer serializer(...); }

-dontwarn com.google.re2j.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.**

# Fix Ktor debug
-dontwarn io.ktor.util.debug.**
