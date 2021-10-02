# gradle-project-template
A project template(boilerplate) for me to use.  
Configured with Kotlin and Android(which is quite heavy; excluded by default but can be enabled by uncommenting some lines).


## Overall structure
- [`gradle/libs.versions.toml`](gradle/libs.versions.toml): where all dependencies are defined
- [`includeBuild`](includeBuild): some wrappers lie here(see [utils.kt](includeBuild/src/main/kotlin/com/lhwdev/build/utils.kt))
- [`module`](module): example module with Kotlin JVM  
  When renaming / duplicating this module, add `include()` in [settings.gradle.kts](settings.gradle.kts).
