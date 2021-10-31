package moe.quill.tsubasa.framework.commands

import moe.quill.tsubasa.framework.annotations.ClickHandler
import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import moe.quill.tsubasa.framework.listener.ButtonClickListener
import moe.quill.tsubasa.framework.listener.SlashCommandListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import org.kodein.di.DI
import org.kodein.di.instance
import org.reflections.Reflections

class CommandRegistrar(private val client: JDA, private val services: DI) {
    private val reflections = Reflections("moe.quill.tsubasa")

    val slashProcessors = HashMap<String, (SlashCommandEvent) -> Unit>()
    val buttonProcessors = HashMap<String, (ButtonClickEvent) -> Unit>()

    val hooks = ArrayList<InteractionHook>()

    //TODO: Change in prod
    private val testGuildId = 235091663758950403

    init {
        //Register the listeners for usage on this registrar
        client.addEventListener(SlashCommandListener(this), ButtonClickListener(this))
    }

    fun registerCommands() {

        slashProcessors.clear()

        val guild = client.getGuildById(testGuildId) ?: run { println("Couldn't get guild!"); return }

        //Iterate through classes marked with command processor
        reflections.getTypesAnnotatedWith(CommandProcessor::class.java).forEach { clazz ->
            val constructor = clazz.constructors.firstOrNull() ?: return
            val constructorObjects = constructor.parameterTypes.map { services.instance<Any>(it) }

            val instance = constructor.newInstance(*constructorObjects.toTypedArray())

            //Get methods marked with @CommandHandler() and load them
            clazz.declaredMethods.forEach { method ->
                method.getAnnotation(CommandHandler::class.java)?.also { commandHandler ->
                    //Ensure that the return type and params of the method are correct
                    val params = method.parameterTypes ?: return
                    if (params.size != 1 || SlashCommandEvent::class == params[0]) return

                    val commandName = commandHandler.name
                    println("Registered command with name '$commandName'! | description '${commandHandler.description}'")
                    slashProcessors[commandName] = { event -> hooks.add(event.hook);method.invoke(instance, event) }

                    //Register the slash command with discord
                    guild.upsertCommand(commandHandler.name, commandHandler.description).queue()
                }

                method.getAnnotation(ClickHandler::class.java)?.also { clickHandler ->
                    //Ensure that the return type and params of the method are correct
                    val params = method.parameterTypes ?: return
                    if (params.size != 1 || ButtonClickEvent::class == params[0]) return

                    val commandName = clickHandler.commandName
                    println("Registered button processor for command '$commandName'!")
                    buttonProcessors[commandName] = { event -> method.invoke(instance, event) }
                }

            }
        }
    }
}