plugins {
    id ("application")
    id("java")
}
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
application {
    mainClass = "org.example.Main" // Thay bằng tên class chứa hàm main của bạn
}
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Thiết lập JVM sử dụng UTF-8 khi chạy
tasks.named<JavaExec>("run") {
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}