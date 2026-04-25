# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# 1. Preservar suas Models (Essencial para o Firestore não dar erro de mapeamento)
# Isso garante que campos como 'createdAt' e 'userName' não sejam renomeados
-keep class com.crlsribeiro.reelcine.domain.model.** { *; }

# 2. Regras para o Firebase Firestore e Auth
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }

# 3. Se você usa Kotlin Serialization ou Gson
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# 4. Hilt / Dependency Injection (Para evitar erro no roteamento das ViewModels)
-keep class com.crlsribeiro.reelcine.**_HiltModules* { *; }
-keep class dagger.hilt.android.internal.** { *; }

# 5. Preservar números de linha para você conseguir ler os erros no Logcat (Opcional, mas ajuda muito)
-keepattributes SourceFile,LineNumberTable