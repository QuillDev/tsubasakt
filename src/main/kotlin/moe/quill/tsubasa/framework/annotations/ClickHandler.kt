package moe.quill.tsubasa.framework.annotations

@Deprecated("Now should appropriately use Kotlin coroutines.")
@Target(AnnotationTarget.FUNCTION)
annotation class ClickHandler(val commandName: String)