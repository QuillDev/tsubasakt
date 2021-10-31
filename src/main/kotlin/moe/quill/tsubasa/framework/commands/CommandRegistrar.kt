package moe.quill.tsubasa.framework.commands

import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import moe.quill.tsubasa.framework.listener.SlashCommandListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.kodein.di.DI
import org.kodein.di.instance
import org.reflections.Reflections

class CommandRegistrar(private val client: JDA, private val services: DI) {
    private val reflections = Reflections("moe.quill.tsubasa")

    val commands = HashMap<String, (SlashCommandEvent) -> Unit>()

    //TODO: Change in prod
    private val testGuildId = 235091663758950403

    init {
        client.addEventListener(SlashCommandListener(this))
    }

    fun registerCommands() {

        commands.clear()

        val guild = client.getGuildById(testGuildId) ?: run { println("Couldn't get guild!"); return }

        //Iterate through classes marked with command processor
        reflections.getTypesAnnotatedWith(CommandProcessor::class.java).forEach { clazz ->
            val constructor = clazz.constructors.firstOrNull() ?: return
            val constructorObjects = constructor.parameterTypes.map { services.instance<Any>(it) }

            val instance = constructor.newInstance(*constructorObjects.toTypedArray())

            //Get methods marked with @CommandHandler() and load them
            clazz.declaredMethods.forEach { method ->
                val annotation = method.getAnnotation(CommandHandler::class.java) ?: return

                //Ensure that the return type and params of the method are correct
                val params = method.parameterTypes ?: return
                if (params.size != 1 || SlashCommandEvent::class == params[0]) return

                val commandName = annotation.name
                println("Registered command with name '$commandName'! | description '${annotation.description}'")
                commands[commandName] = { event -> method.invoke(instance, event) }

                //Register the slash command with discord
                guild.upsertCommand(annotation.name, annotation.description).queue()
            }
        }
    }
}