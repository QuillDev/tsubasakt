package moe.quill.tsubasa.framework.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class ClickHandler(val commandName: String)