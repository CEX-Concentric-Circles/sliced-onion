pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.hibernate.orm") {
                useModule("org.hibernate:hibernate-gradle-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "sliced-onion"
