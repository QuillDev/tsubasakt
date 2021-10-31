package moe.quill.tsubasa.commands

import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

@CommandProcessor
class GeneralCommands {

    @CommandHandler(name = "ping", description = "Gets the current gateway ping")
    fun ping(event: SlashCommandEvent) {
        return event.reply("Current gateway ping: ${event.jda.gatewayPing}ms!").setEphemeral(true).queue()
    }

    @CommandHandler(name = "help", description = "Gets the bots help website")
    fun help(event: SlashCommandEvent) {
        return event.reply("Help Website: https://quill.moe/tsubasa").setEphemeral(true).queue()
    }
}