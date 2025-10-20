// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Plugins comunes para todos los módulos, aplicados false aquí
    id("com.android.application") version "8.12.3" apply false
    id("com.android.library") version "8.12.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
