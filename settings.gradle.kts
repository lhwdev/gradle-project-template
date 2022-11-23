@file:Suppress("UnstableApiUsage")

// Type-safe Project Dependency Accessor
// https://docs.gradle.org/7.4/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// All single-source-of-truth for **settings.gradle.kts** + build.gradle.kts + includeBuild
val androidGradleVersion = "7.2.0"

pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
	}
	
}

// Putting this in pluginManagement {} block makes referencing outer variable impossible
pluginManagement.resolutionStrategy.eachPlugin {
	val id = requested.id.id
	
	// Android
	if(id.startsWith("com.android")) {
		useModule("com.android.tools.build:gradle:$androidGradleVersion")
	}
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			// If you are to change this path, make sure to change includeBuild/settings.gradle.kts
			from(files("libs.versions.toml"))
			
			version("androidGradle", androidGradleVersion)
		}
	}
}


/// Utility Scripts
class IncludesAllBase(
	val settings: Settings,
	val rootPath: List<String>,
)

class IncludesAllDsl(
	private val base: IncludesAllBase,
	private val cwd: List<String>,
) {
	private fun filePathFor(path: List<String>) = (base.rootPath + path).joinToString("/")
	private fun gradleModuleNameFor(path: List<String>) = path.joinToString("") { ":$it" }
	
	fun module(vararg modules: String, block: ((ProjectDescriptor) -> Unit)? = null) {
		for(module in modules) {
			val path = cwd + module
			val gradleModuleName = gradleModuleNameFor(path)
			val filePath = filePathFor(path)
			base.settings.include(gradleModuleName)
			val project = base.settings.project(gradleModuleName)
			project.projectDir = File(filePath)
			block?.invoke(project)
		}
	}
	
	fun group(path: String, block: IncludesAllDsl.() -> Unit) {
		module(path)
		IncludesAllDsl(
			base = base,
			cwd = cwd + path,
		).block()
	}
}

fun includesAll(rootPath: String, block: IncludesAllDsl.() -> Unit) {
	val base = IncludesAllBase(
		settings = settings,
		rootPath = listOf(rootPath)
	)
	IncludesAllDsl(base, emptyList()).block()
}


/// Projects
includeBuild("includeBuild")

includesAll(rootPath = "modules") {
	module("core")
}
