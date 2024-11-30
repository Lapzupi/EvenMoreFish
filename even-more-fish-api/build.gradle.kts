plugins {
    id("com.oheers.evenmorefish.java-conventions")
    alias(libs.plugins.error.prone)
}

version = 1.7

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.annotations)
    compileOnly(libs.commons.lang3)
    compileOnly(libs.universalscheduler)
    compileOnly(libs.boostedyaml)

    errorprone(libs.error.prone.core)
    errorprone(project(":even-more-fish-custom-errors"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}
