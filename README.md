# gradle-project-template
A project template(boilerplate) for me to use. Includes most best-practices to me.  
Configured with Kotlin and Android(which is quite heavy; excluded by default but can be enabled by uncommenting some lines).


## Overall structure
- [`libs.versions.toml`](libs.versions.toml): where all dependencies are defined
- [`includeBuild`](includeBuild): some wrappers lie here(
  see [utils.kt](includeBuild/src/main/kotlin/com/lhwdev/build/utils.kt))
- [`core`](modules/core): example module with Kotlin JVM.  
  When renaming / duplicating this module, see `includesAll()` in [settings.gradle.kts](settings.gradle.kts).


## License
Virtual permissive license `*_*`  
You can do just anything with this template repository.  
