package net.mamestagram.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.mamestagram.game.Profile;

import java.io.IOException;
import java.sql.SQLException;

import static net.mamestagram.data.EmbedMessageData.*;

import static net.mamestagram.game.Profile.*;

public class SlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        switch (e.getName()) {
            case "help":
                e.replyEmbeds(helpCommand().build()).setEphemeral(true).queue();
                break;
            case "profile":
                try {
                    e.replyEmbeds(profileData(e.getMember().getNickname(), e.getOption("mode").getAsInt()).build()).queue();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                break;
        }
    }
}
