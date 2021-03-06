package moe.quill.tsubasa

import dev.minn.jda.ktx.light
import moe.quill.tsubasa.framework.commands.CommandRegistrar
import net.dv8tion.jda.api.JDA
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class Tsubasa {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val di = DI {
                bindSingleton { light(System.getenv("DISCORD_TOKEN"), enableCoroutines = true) }
                bindSingleton { CommandRegistrar(instance(), di) }
            }

            val jda: JDA by di.instance()
            jda.awaitReady()
            val registrar: CommandRegistrar by di.instance()
            registrar.registerCommands()
        }
    }
}