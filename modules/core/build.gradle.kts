import com.lhwdev.build.*


plugins {
	kotlin("jvm")
	
	id("common-plugin") // must be applied after Kotlin/Android plugins
}

kotlin {
	setup()
}


dependencies {
	// implementation(project.abc) // this is available thanks to https://docs.gradle.org/7.4/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
}
