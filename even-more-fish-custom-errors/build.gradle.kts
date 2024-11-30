plugins {
    id("java")
}

group = "com.oheers.fish.errors"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.error.prone.core)
    annotationProcessor(libs.error.prone.annotations)

    testImplementation(libs.error.prone.test.helpers)

    testImplementation(platform(libs.junit.platform))
    testImplementation(libs.junit.jupiter)
}

val sunToolsExports: MutableList<String> = mutableListOf(
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
)

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(sunToolsExports)
}



tasks.jar {
    from("src/main/resources/META-INF/services/") {
        into("META-INF/services/")
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED"
    )
}