package net.mamestagram.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static net.mamestagram.data.EmbedMessageData.*;

public class SlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        switch (e.getName()) {
            case "help":
                e.replyEmbeds(helpCommand().build()).setEphemeral(true).queue();
                break;
            case "profile":

                break;
        }
    }
}
