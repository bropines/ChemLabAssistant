plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    // Добавляем KSP плагин, версия должна совпадать с версией Kotlin (2.0.0)
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}