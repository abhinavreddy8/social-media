// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Make sure to specify classpath for Android plugin and Firebase plugin here
        classpath("com.android.tools.build:gradle:8.4.1")
        classpath("com.google.gms:google-services:4.4.2")
    }
}
