import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val artifact_id = "bilibili-dynamic-image-downloader"
//val ktor_version: String by project
val logback_version: String by project
val org_gradle_jvmargs: String by project
group = "com.github.masx200"
version = "1.1.0"
tasks.named<ShadowJar>("shadowJar") {
    manifest {
        attributes["Main-Class"] = "com.github.masx200.bilibili_dynamic_image_downloader.IMAGEDOWNLOADERApplicationKt"

    }
}
plugins {
    id("application")
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
//    id("io.ktor.plugin") version "3.0.1"
    id("org.graalvm.buildtools.native") version "0.10.4"
    id("maven-publish")
}
apply {
    plugin("maven-publish")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("plugin") {
                from(components["java"])
                groupId = project.group.toString()
                artifactId = artifact_id
                version = project.version.toString()
            }
            repositories {
                maven {
                    url = uri(System.getenv("MAVEN_REPOSITORY") ?: "")
                    credentials {
                        username = System.getenv("MAVEN_USERNAME") ?: ""
                        password = System.getenv("MAVEN_PASSWORD") ?: ""
                    }
                }
            }
        }
    }
}
//distributions{
//    applicationDefaultJvmArgs
//}
application {
    mainClass.set("com.github.masx200.bilibili_dynamic_image_downloader.IMAGEDOWNLOADERApplicationKt")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://maven.pkg.github.com/masx200/bilibiliclient")

        credentials {
            username = System.getenv("MAVEN_USERNAME") ?: ""
            password = System.getenv("MAVEN_PASSWORD") ?: ""
        }
    }
}

dependencies {

    implementation("com.alibaba.fastjson2:fastjson2:2.0.53")
    implementation("com.alibaba.fastjson2:fastjson2-codegen:2.0.53") {
        //exclude group: "com.alibaba.fastjson2", module: "fastjson2"
    }
    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.53") {
        //exclude group: "com.alibaba.fastjson2", module: "fastjson2"
    }
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.alibaba:fastjson:2.0.53")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16")

    // https://mvnrepository.com/artifact/cn.hll520.linclient/bilibiliclient
    implementation("com.github.masx200:bilibiliclient:1.3.4")

    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
//    implementation("io.ktor:ktor-server-cio-jvm")
//    implementation("io.ktor:ktor-server-compression:$ktor_version")
//    implementation("io.ktor:ktor-server-html-builder")
    implementation("ch.qos.logback:logback-classic:$logback_version")
//    implementation("io.ktor:ktor-server-netty-jvm")
    // https://mvnrepository.com/artifact/io.netty/netty-common
    implementation("io.netty:netty-common:4.1.115.Final")

//    implementation("io.ktor:ktor-client-cio-jvm")

    implementation("org.jsoup:jsoup:1.18.3")
    implementation("com.googlecode.juniversalchardet:juniversalchardet:1.0.3")
//    implementation("io.ktor:ktor-client-encoding:$ktor_version")
//    implementation("io.ktor:ktor-client-core:$ktor_version")
//    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/com.xenomachina/kotlin-argparser
    implementation("com.xenomachina:kotlin-argparser:2.0.7")

}
tasks.named<JavaExec>("run") {
//    mainClass.set("com.example.Main")
//    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = (org_gradle_jvmargs.split(" "))
}

graalvmNative {
    binaries {

        named("main") {
            fallback.set(false)
            verbose.set(true)
            buildArgs.add("--initialize-at-build-time=kotlin")
            buildArgs.add("--initialize-at-build-time=java.lang.Thread")
            buildArgs.add("--initialize-at-build-time=<culprit>")
            buildArgs.add("""--initialize-at-build-time=kotlinx.coroutines.internal.LockFreeTaskQueueCore,kotlinx.coroutines.DefaultExecutorKt,kotlinx.coroutines.DefaultExecutor,kotlinx.coroutines.internal.LimitedDispatcher,kotlinx.coroutines.DebugKt,kotlinx.coroutines.scheduling.CoroutineScheduler,kotlinx.coroutines.CoroutineStart${"$"}WhenMappings""")
            buildArgs.add("--initialize-at-build-time=ch.qos.logback")
//            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin")
            buildArgs.add("--initialize-at-build-time=kotlinx.coroutines.scheduling.TasksKt")
            buildArgs.add("--initialize-at-build-time=org.slf4j.LoggerFactory")
            buildArgs.add("--initialize-at-build-time=kotlinx.coroutines.CoroutineStart,kotlinx.coroutines.internal.LockFreeTaskQueue,kotlinx.coroutines.EventLoopImplPlatform,kotlinx.coroutines.Unconfined,kotlinx.coroutines.LazyStandaloneCoroutine,kotlinx.coroutines.channels.BufferedChannel,kotlinx.coroutines.scheduling.DefaultScheduler,kotlinx.coroutines.internal.ConcurrentLinkedListNode,kotlinx.coroutines.scheduling.SchedulerCoroutineDispatcher,kotlinx.coroutines.channels.ChannelSegment,kotlinx.coroutines.channels.BufferOverflow,kotlinx.coroutines.channels.BufferedChannelKt,kotlinx.coroutines.scheduling.UnlimitedIoScheduler,kotlinx.coroutines.Job,kotlinx.coroutines.scheduling.GlobalQueue,kotlinx.coroutines.StandaloneCoroutine,kotlinx.coroutines.internal.SystemPropsKt__SystemPropsKt,kotlinx.io.SegmentPool,org.slf4j.helpers.Reporter,kotlinx.coroutines.scheduling.DefaultIoScheduler,kotlinx.io.bytestring.ByteString,kotlinx.coroutines.EventLoopImplBase,kotlinx.coroutines.Dispatchers")

            buildArgs.add("-H:+InstallExitHandlers")
            buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
            buildArgs.add("-H:+ReportExceptionStackTraces")

            imageName.set(artifact_id)
        }
    }
}

