pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://dl.google.com/dl/android/maven2/") }
    maven {
      url = uri("https://maven.google.com")
      name = "google"
    }
    //-----------国内通用-----------
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots") }
    maven { url = uri("https://maven.aliyun.com/repository/spring") }
    maven { url = uri("https://maven.aliyun.com/repository/releases") }
    maven { url = uri("https://maven.aliyun.com/repository/snapshots") }
    maven { url = uri("https://maven.aliyun.com/repository/grails-core") }
    maven { url = uri("https://maven.aliyun.com/repository/mapr-public") }
  }
}

rootProject.name = "ABase"
include(":app")
include(":ablibrary")
include(":silicompressor")