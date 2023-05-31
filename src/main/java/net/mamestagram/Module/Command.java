package net.mamestagram.Module;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Command {

    public static void createAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e, String[] option) {

        List<net.dv8tion.jda.api.interactions.commands.Command.Choice> options = Stream.of(option)
                .filter(Array->Array.startsWith(e.getFocusedOption().getValue()))
                .map(Array-> new net.dv8tion.jda.api.interactions.commands.Command.Choice(Array, Array))
                .collect(Collectors.toList());
        e.replyChoices(options).queue();
    }
}
