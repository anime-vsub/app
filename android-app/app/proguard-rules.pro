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
-keep,includedescriptorclasses class eu.org.animevsub.**$$serializer { *; }
-keepclassmembers class eu.org.animevsub.** { *** Companion; }
-keepclasseswithmembers class eu.org.animevsub.** { kotlinx.serialization.KSerializer serializer(...); }
