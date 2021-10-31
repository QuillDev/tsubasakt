package moe.quill.tsubasa.framework.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class CommandHandler(
    val name: String,
    val description: String = "needs description"
)