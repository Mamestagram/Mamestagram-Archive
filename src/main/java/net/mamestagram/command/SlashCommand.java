package net.mamestagram.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.sql.SQLException;

import static net.mamestagram.message.EmbedMessageData.*;

import static net.mamestagram.game.Profile.*;
import static net.mamestagram.game.RecentPlay.*;
import static net.mamestagram.game.Ranking.*;
import static net.mamestagram.module.OSUModule.*;
import static net.mamestagram.module.CommandModule.*;

public class SlashCommand extends ListenerAdapter {

    private final String[] modes = new String[] {"Standard", "Taiko", "Catch", "Mania", "Relax", "AutoPilot"};

    private double mode, row = 0;

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {

        if(e.getName().equals("osuprofile") && e.getFocusedOption().getName().equals("mode")) {
            createAutoCompleteInteraction(e, modes);
        } else if(e.getName().equals("result") && e.getFocusedOption().getName().equals("mode")) {
            createAutoCompleteInteraction(e, modes);
        } else if(e.getName().equals("ranking") && e.getFocusedOption().getName().equals("mode")) {
            createAutoCompleteInteraction(e, modes);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {

        if(e.getOption("mode") != null) {
            mode = getModeNumber(e.getOption("mode").getAsString());
        }

        switch (e.getName()) {
            case "help" -> e.replyEmbeds(helpCommand().build()).setEphemeral(true).queue();
            case "osuprofile" -> {
                try {
                    e.replyEmbeds(profileData(e.getMember(), (int) mode).build()).queue();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (NullPointerException ex) {
                    e.replyEmbeds(notArgumentMessage().build()).queue();
                }
            }
            case "result" -> {
                if (!e.isAcknowledged()) {
                    try {
                        e.reply("**This is the result of your " + e.getOption("mode").getAsString() + " play!**").setEmbeds(recentData(e.getMember(), (int) mode).build()).queue();
                    } catch (SQLException | IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (NullPointerException ex) {
                        e.replyEmbeds(notArgumentMessage().build()).queue();
                    }
                }
            }
            case "server" -> e.replyEmbeds(connectGuideMessage().build()).queue();
            case "ranking" -> {
                rankView = "";
                row = 0;
                try {
                    e.reply("**This is the rank of " + e.getOption("mode").getAsString() + "!**").setEmbeds(rankingViewerMessage((int) mode, (int) row).build()).addActionRow(
                            Button.success("next", "Next")
                    ).setEphemeral(true).queue();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if(e.getComponentId().equals("next")) {

            rankView = "";

            try {
                int divRow = rowCount / 5;
                int modRow = rowCount % 5;

                if(modRow != 0) divRow++;
                row += 5;
                if(divRow * 5 > row) {
                    e.editMessageEmbeds(rankingViewerMessage((int)mode, (int)row).build()).queue();
                } else {
                    row = 0;
                    e.editMessageEmbeds(rankingViewerMessage((int)mode, 0).build()).queue();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
