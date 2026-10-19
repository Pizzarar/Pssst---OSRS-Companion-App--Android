# Gson generic type tokens & annotations
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

# Keep Gson TypeToken subclasses
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Keep network/sync payload DTOs serialized/deserialized by Gson
-keep class com.pssst.data.network.model.** { *; }

# Keep local models serialized via Gson in SharedPreferences / Room
-keep class com.pssst.data.local.ItemManifestEntity { *; }
-keep class com.pssst.data.local.PlayerStateEntity { *; }
-keep class com.pssst.data.local.GoalConfig { *; }
-keep class com.pssst.data.local.ListLayouts$Stored { *; }
-keep enum com.pssst.util.GoalPriceMode { *; }
