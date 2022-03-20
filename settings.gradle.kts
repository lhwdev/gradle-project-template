// Type-safe Project Dependency Accessor
// https://docs.gradle.org/7.4/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
	}
	
	resolutionStrategy.eachPlugin {
		val id = requested.id.id
		
		// Android
		if(id.startsWith("com.android")) {
			useModule("com.android.tools.build:gradle:7.1.2")
		}
	}
}

fun includeAll(filePath: String, path: String, vararg items: String) {
	for(item in items) {
		val realPath = "$filePath/$item"
		val projectPath = "$path:$item"
		include(projectPath)
		
		project(projectPath).projectDir = file(realPath)
	}
}


/// Projects
includeBuild("includeBuild")

includeAll(
	filePath = "modules",
	path = "",
	"core"
)

