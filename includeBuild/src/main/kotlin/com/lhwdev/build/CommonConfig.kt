@file:Suppress("LeakingThis")

package com.lhwdev.build

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import javax.inject.Inject


/**
 * Not expected to be exhaustive kotlin/etc. configuration utility; created on demand.
 */
open class CommonConfig @Inject constructor(private val project: Project) : ExtensionAware {
	init {
		val extensions = (this as ExtensionAware).extensions
		
		val kotlin = project.extensions.findByName("kotlin")
		when(kotlin) {
			null -> null
			is KotlinMultiplatformExtension -> KotlinMultiplatformScope::class.java
			is KotlinJvmProjectExtension -> KotlinJvmScope::class.java
			else -> error("not supported: $kotlin")
		}?.let {
			val scope = extensions.create("kotlin", it, this, kotlin)
			scope.setup()
		}
	}
	
	override fun getExtensions(): ExtensionContainer = error("stub!")
}

abstract class KotlinScope(protected val commonConfig: CommonConfig) {
	protected abstract val kotlin: KotlinProjectExtension
	
	internal open fun setup() {
		kotlin.sourceSets {
			all {
				languageSettings.apply {
					enableLanguageFeature("InlineClasses")
					optIn("kotlin.RequiresOptIn")
					optIn("kotlin.ExperimentalUnsignedTypes")
				}
			}
			
			val testSourceSet = if(kotlin is KotlinMultiplatformExtension) "commonTest" else "test"
			
			named(testSourceSet) {
				dependencies {
					implementation(kotlin("test-common"))
					implementation(kotlin("test-annotations-common"))
				}
			}
		}
	}
}

open class KotlinMultiplatformScope(commonConfig: CommonConfig, override val kotlin: KotlinMultiplatformExtension) :
	KotlinScope(commonConfig) {
	
	val common = KotlinCommonItem(sourceSet = KotlinTargetSourceSet(kotlin.sourceSets, "common"))
	
	
	fun intermediate(name: String, setupBlock: KotlinIntermediateItem.() -> Unit = {}): KotlinIntermediateItem {
		val item = KotlinIntermediateItem(
			name = name,
			sourceSet = kotlin.sourceSets.create(sourceSetNameFor(name, "main"))
		)
		
		item.setupBlock()
		
		return item
	}
	
	fun jvm(name: String, setupBlock: KotlinJvmItem.() -> Unit = {}): KotlinJvmItem {
		val item = KotlinJvmItem(
			sourceSet = KotlinTargetSourceSet(kotlin.sourceSets, name = name)
		)
		
		item.setupBlock()
		
		return item
	}
}

open class KotlinJvmScope(commonConfig: CommonConfig, override val kotlin: KotlinJvmProjectExtension) :
	KotlinScope(commonConfig) {
	
	override fun setup() {
		kotlin.sourceSets {
			named("test") {
				dependencies {
					implementation(kotlin("test-junit"))
				}
			}
		}
	}
}


class KotlinTargetSourceSet(val main: KotlinSourceSet, val test: KotlinSourceSet) {
	constructor(sourceSets: NamedDomainObjectContainer<KotlinSourceSet>, name: String) : this(
		main = sourceSets[sourceSetNameFor(name, "main")],
		test = sourceSets[sourceSetNameFor(name, "test")]
	)
}


abstract class KotlinItem

abstract class KotlinTargetItem(internal val sourceSet: KotlinTargetSourceSet) : KotlinItem()

class KotlinCommonItem(sourceSet: KotlinTargetSourceSet) : KotlinTargetItem(sourceSet = sourceSet) {
	
}

class KotlinJvmItem(sourceSet: KotlinTargetSourceSet) : KotlinTargetItem(sourceSet = sourceSet) {
	
}


class KotlinIntermediateItem(private val name: String, internal val sourceSet: KotlinSourceSet) : KotlinItem()



private fun String.firstToUpperCase() = replaceRange(0, 1, first().toUpperCase().toString())

private fun sourceSetNameFor(name: String?, type: String) =
	if(name == null) type else "$name${type.firstToUpperCase()}"
