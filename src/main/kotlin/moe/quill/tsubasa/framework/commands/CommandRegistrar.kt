package moe.quill.tsubasa.framework.commands

import dev.minn.jda.ktx.listener
import dev.minn.jda.ktx.onCommand
import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.kodein.di.DI
import org.kodein.di.instance
import org.reflections.Reflections
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.Reflection
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class CommandRegistrar(private val client: JDA, private val services: DI) {
    private val reflections = Reflections("moe.quill.tsubasa")

    val slashProcessors = HashMap<String, Method>()
    val buttonProcessors = HashMap<String, (ButtonClickEvent) -> Unit>()

    //TODO: Change in prod
    private val testGuildId = 235091663758950403

    init {
        client.listener<SlashCommandEvent> { event ->

        }
    }

    fun registerCommands() {
        client.awaitReady()

        slashProcessors.clear()

        val guild = client.getGuildById(testGuildId) ?: run { println("Couldn't get guild!"); return }

        //Iterate through classes marked with command processor
        reflections.getTypesAnnotatedWith(CommandProcessor::class.java).forEach { clazz ->


            Reflection.getOrCreateKotlinClass(clazz).declaredFunctions.forEach { function ->

                val constructor = clazz.constructors.firstOrNull() ?: return
                val constructorObjects = constructor.parameterTypes.map { services.instance<Any>(it) }

                val instance = constructor.newInstance(*constructorObjects.toTypedArray())

                function.findAnnotation<CommandHandler>()?.also { commandHandler ->
                    //Ensure that the return type and params of the method are correct
                    val params = function.typeParameters
                    if (params.isNotEmpty() && SlashCommandEvent::class.java == params[0]) return

                    val commandName = commandHandler.name
                    println("Registered command with name '$commandName'! | description '${commandHandler.description}'")

                    client.onCommand(commandName) {
                        if (function.isSuspend) function.callSuspend(instance, it)
                        else function.call(instance, it)
                    }

                    //Register the slash command with discord
                    guild.upsertCommand(commandHandler.name, commandHandler.description).queue()
                }

            }

        }
    }
}