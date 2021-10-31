package moe.quill.tsubasa.framework.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandHandler(val name: String, val description: String = "needs description", val type: Int = -1)