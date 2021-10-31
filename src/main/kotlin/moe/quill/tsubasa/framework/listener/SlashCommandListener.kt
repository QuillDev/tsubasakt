package moe.quill.tsubasa.framework.listener

import moe.quill.tsubasa.framework.commands.CommandRegistrar
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SlashCommandListener(private val commandRegistrar: CommandRegistrar) : ListenerAdapter() {

    override fun onSlashCommand(event: SlashCommandEvent) {
        println(event.responseNumber)

        val command = commandRegistrar.slashProcessors[event.name] ?: {
            it.reply("There is no command with the name '$event.name'").setEphemeral(true).queue()
        }
        command.invoke(event)
    }
}