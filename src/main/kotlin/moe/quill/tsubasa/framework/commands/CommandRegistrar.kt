package moe.quill.tsubasa.framework.commands

import dev.minn.jda.ktx.onCommand
import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import org.kodein.di.DI
import org.kodein.di.instance
import org.reflections.Reflections
import kotlin.jvm.internal.Reflection
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

class CommandRegistrar(private val client: JDA, private val services: DI) {

    //TODO: See if there's an easy way to remove this library, this is legit all it does
    private val reflections = Reflections("moe.quill.tsubasa")

    //TODO: Change in prod
    private val testGuildId = 235091663758950403

    fun registerCommands() {
        client.awaitReady()

        val guild = client.getGuildById(testGuildId) ?: run { println("Couldn't get guild!"); return }

        //Iterate through classes marked with command processor
        reflections.getTypesAnnotatedWith(CommandProcessor::class.java).forEach { clazz ->

            Reflection.getOrCreateKotlinClass(clazz).declaredFunctions.forEach { function ->

                val constructor = clazz.constructors.firstOrNull() ?: return

                val constructorObjects = constructor.parameterTypes.map { services.instance<Any>(it) }

                val instance = constructor.newInstance(*constructorObjects.toTypedArray())

                val params = function.valueParameters
                if (params.isEmpty() || params[0].type.jvmErasure != SlashCommandEvent::class) return


                function.findAnnotation<CommandHandler>()?.also { commandHandler ->

                    val commandName = commandHandler.name
                    println("Registered command with name '$commandName'! | description '${commandHandler.description}'")

                    client.onCommand(commandName) {
                        if (function.isSuspend) function.callSuspend(instance, it)
                        else function.call(instance, it)
                    }

                    //Register the slash command with discord
                    guild.upsertCommand(commandName, commandHandler.description).queue()
                }
            }
        }
    }
}