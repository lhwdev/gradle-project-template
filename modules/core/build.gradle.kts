plugins {
	kotlin("jvm")
	
	id("common-plugin") // must be applied after Kotlin/Android plugins
}

commonConfig {
	kotlin {
		
	}
}

// kotlin {
// 	setup()
// }
//
//
// dependencies {
// 	// implementation(projects.abc) // this is available thanks to https://docs.gradle.org/7.4/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
// }
