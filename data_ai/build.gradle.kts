import com.example.util.simpletimetracker.Base
import com.example.util.simpletimetracker.applyAndroidLibrary

plugins {
    alias(libs.plugins.gradleLibrary)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

applyAndroidLibrary()

android {
    namespace = "${Base.namespace}.data_ai"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))

    implementation(libs.okhttp)
    implementation(libs.google.dagger)
    implementation(libs.coroutines)

    ksp(libs.kapt.dagger)

    testImplementation(libs.test.junit)
    testImplementation("org.json:json:20231013")
}
