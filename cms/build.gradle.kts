plugins {
    // Apply any necessary plugins for your project
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Add other configurations as needed
