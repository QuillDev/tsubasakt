package moe.quill.tsubasa.commands

import dev.minn.jda.ktx.awaitButton
import kotlinx.coroutines.withTimeoutOrNull
import moe.quill.tsubasa.framework.annotations.CommandHandler
import moe.quill.tsubasa.framework.annotations.CommandProcessor
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

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
    suspend fun dev(event: SlashCommandEvent) {
        val clickable = Button.primary("dev_test", "????")

        event.reply(
            MessageBuilder().setContent("Hello World!")
                .setActionRows(ActionRow.of(clickable))
                .build()
        ).setEphemeral(true).queue()

        //TODO: Move this to a helper method of some sort
        withTimeoutOrNull(60000) {
            val pressed = event.user.awaitButton(clickable)
            pressed.deferEdit().queue()
            pressed.editMessage("Clicked it")
        } ?: event.hook.editOriginal("Timed Out.").setActionRows(emptyList()).queue()


    }

}