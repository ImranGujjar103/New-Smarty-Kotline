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

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn android.telephony.HwTelephonyManager
-dontwarn com.android.org.conscrypt.OpenSSLSocketImpl
-dontwarn com.huawei.android.os.BuildEx$VERSION
-dontwarn com.huawei.hianalytics.process.HiAnalyticsConfig$Builder
-dontwarn com.huawei.hianalytics.process.HiAnalyticsConfig
-dontwarn com.huawei.hianalytics.process.HiAnalyticsInstance$Builder
-dontwarn com.huawei.hianalytics.process.HiAnalyticsInstance
-dontwarn com.huawei.hianalytics.process.HiAnalyticsManager
-dontwarn com.huawei.hianalytics.util.HiAnalyticTools
-dontwarn com.huawei.hms.framework.network.restclient.dnkeeper.DNKeeperManager
-dontwarn com.huawei.hms.framework.network.restclient.dnkeeper.RequestHost
-dontwarn com.huawei.hms.framework.network.restclient.hwhttp.ClientConfiguration
-dontwarn com.huawei.hms.framework.network.restclient.hwhttp.dns.DnsResult
-dontwarn com.huawei.hms.framework.qoes.HmsQoEApiManager
-dontwarn com.huawei.hms.hquic.HQUICManager$HQUICInitCallback
-dontwarn com.huawei.hms.hquic.HQUICManager
-dontwarn com.huawei.hms.hquic.HQUICProvider
-dontwarn com.huawei.hms.network.AdvanceNetworkKitProvider
-dontwarn com.huawei.hms.network.api.advance.AdvanceNetworkKit
-dontwarn com.huawei.hms.network.api.advance.ReportCallBack
-dontwarn com.huawei.hms.network.api.advance.WrapperLogger
-dontwarn com.huawei.hms.network.httpclient.hianalytics.EditableMetrics
-dontwarn com.huawei.libcore.io.ExternalStorageFile
-dontwarn com.huawei.libcore.io.ExternalStorageFileInputStream
-dontwarn com.huawei.libcore.io.ExternalStorageFileOutputStream
-dontwarn com.huawei.libcore.io.ExternalStorageRandomAccessFile
-dontwarn org.chromium.net.BidirectionalStream$Callback
-dontwarn org.chromium.net.CronetEngine
-dontwarn org.chromium.net.ExperimentalBidirectionalStream$Builder
-dontwarn org.chromium.net.ExperimentalBidirectionalStream
-dontwarn org.chromium.net.ExperimentalCronetEngine$Builder
-dontwarn org.chromium.net.ExperimentalCronetEngine
-dontwarn org.chromium.net.ExperimentalUrlRequest$Builder
-dontwarn org.chromium.net.ExperimentalUrlRequest
-dontwarn org.chromium.net.NetworkException
-dontwarn org.chromium.net.RequestFinishedInfo$Listener
-dontwarn org.chromium.net.UploadDataProvider
-dontwarn org.chromium.net.UrlRequest$Builder
-dontwarn org.chromium.net.UrlRequest$Callback
-dontwarn org.chromium.net.UrlRequest
-dontwarn org.chromium.net.UrlResponseInfo
-dontwarn org.chromium.net.impl.ImplVersion

# Huawei HMS and ML Kit Rules
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.agconnect.**{*;}
-keep class com.huawei.hiai.**{*;}
-keep class com.huawei.hiai.computecapability.**{*;}
-keep class com.huawei.** {*;}
# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault,Signature,InnerClasses,EnclosingMethod

# Google Mobile Ads SDK
-keep class com.google.android.libraries.ads.mobile.sdk.** { *; }
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }

# The stack trace mentioned ads_mobile_sdk package
-keep class ads_mobile_sdk.** { *; }

# Keep all members of classes that might be accessed via reflection in the Ads SDK
-keepclassmembers class * {
    * flags_;
}
