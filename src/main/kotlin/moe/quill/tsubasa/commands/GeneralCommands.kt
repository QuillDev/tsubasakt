package moe.quill.tsubasa.commands

import moe.quill.tsubasa.framework.annotations.ClickHandler
import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.Component

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

    @CommandHandler(name = "dev", description = "Just a dev testing command")
    fun dev(event: SlashCommandEvent) {
        event.reply(
            MessageBuilder().setContent("Hello World!")
                .setActionRows(ActionRow.of(Button.primary("dev_test", "????")))
                .build()
        ).setEphemeral(true).queue()
    }

    @ClickHandler(commandName = "dev")
    fun devClick(event: ButtonClickEvent) {
        println(event.componentId)
        if (event.componentId != "dev_test") return
        event.editMessage("Ye it works, poggers").queue()

    }

}