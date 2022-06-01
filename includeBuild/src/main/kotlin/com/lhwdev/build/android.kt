// Android dependencies and toolchains are large.
// If you need android in your project, rename this file to .kt and uncomment `initializeAndroid` line from CommonConfig.kt.
// And uncomment `compileOnly("com.android.tools.build:gradle:...")` from /includeBuild/build.gradle.kts.
package com.lhwdev.build

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget


internal fun initializeAndroid(extensions: ExtensionContainer, project: Project) {
	val android = project.extensions.findByName("android") as? BaseExtension ?: return
	
	extensions.create("android", android::class.java)
}


fun KotlinMultiplatformScope.android(
	name: String = "android",
	setupBlock: KotlinAndroidItem.() -> Unit
): KotlinAndroidItem {
	val android = commonConfig.project.extensions.findByName("android") as? BaseExtension
		?: error("android plugin was not configured")
	
	val target = kotlin.android(name)
	val item = KotlinAndroidItem(
		target = target,
		sourceSet = KotlinTargetSourceSet(kotlin.sourceSets, name = "android")
	)
	
	android.sourceSets.all {
		val directory = "src/${sourceSetNameFor(name, this.name)}"
		setRoot(directory)
	}
	
	item.setupBlock()
	
	return item
}


// mpp only
class KotlinAndroidItem(
	val target: KotlinAndroidTarget,
	sourceSet: KotlinTargetSourceSet
) : KotlinPlatformItem(sourceSet = sourceSet)
