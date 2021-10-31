package moe.quill.tsubasa.framework.listener

import moe.quill.tsubasa.framework.commands.CommandRegistrar
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ButtonClickListener(private val registrar: CommandRegistrar) : ListenerAdapter() {

    @Override
    override fun onButtonClick(event: ButtonClickEvent) {
        registrar.buttonProcessors["dev"]?.also { it.invoke(event) }
    }
}