# The views are instantiated from layout XML by name; keep their inflation constructors.
-keep class com.pinata.ui.** { <init>(android.content.Context, android.util.AttributeSet); }
