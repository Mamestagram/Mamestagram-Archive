package net.mamestagram.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.io.EOFException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.mamestagram.data.EmbedMessageData.*;

import static net.mamestagram.game.Profile.*;
import static net.mamestagram.game.RecentPlay.*;

public class SlashCommand extends ListenerAdapter {

    private String[] modes = new String[] {"osu", "taiko", "catch", "mania", "relax"};
    private int mode;

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        if(e.getName().equals("profile") && e.getFocusedOption().getName().equals("mode")) {
            List<Command.Choice> options = Stream.of(modes)
                    .filter(modes->modes.startsWith(e.getFocusedOption().getValue()))
                    .map(modes-> new net.dv8tion.jda.api.interactions.commands.Command.Choice(modes,modes))
                    .collect(Collectors.toList());
            e.replyChoices(options).queue();
        } else if(e.getName().equals("play") && e.getFocusedOption().getName().equals("mode")) {
            List<net.dv8tion.jda.api.interactions.commands.Command.Choice> options = Stream.of(modes)
                    .filter(modes->modes.startsWith(e.getFocusedOption().getValue()))
                    .map(modes-> new net.dv8tion.jda.api.interactions.commands.Command.Choice(modes,modes))
                    .collect(Collectors.toList());
            e.replyChoices(options).queue();
        }
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if(e.getOption("mode") != null) {
            switch (e.getOption("mode").getAsString()) {
                case "osu":
                    mode = 0;
                    break;
                case "taiko":
                    mode = 1;
                    break;
                case "catch":
                    mode = 2;
                    break;
                case "mania":
                    mode = 3;
                    break;
                case "relax":
                    mode = 4;
                    break;
            }
        }

        switch (e.getName()) {
            case "help":
                e.replyEmbeds(helpCommand().build()).setEphemeral(true).queue();
                break;
            case "profile":
                try {
                    e.replyEmbeds(profileData(e.getMember(), mode).build()).queue();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (NullPointerException ex) {
                    e.replyEmbeds(notArgumentMessage().build()).queue();
                }
                break;
            case "play":
                try {
                    e.replyEmbeds(recentData(e.getMember(),mode).build()).queue();
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                } catch (NullPointerException ex) {
                    e.replyEmbeds(notArgumentMessage().build()).queue();
                }
        }
    }
}
