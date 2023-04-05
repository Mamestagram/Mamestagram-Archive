package net.mamestagram.module;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandModule {

    public static void createAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e, String[] option) {

        List<Command.Choice> options = Stream.of(option)
                .filter(Array->Array.startsWith(e.getFocusedOption().getValue()))
                .map(Array-> new Command.Choice(Array, Array))
                .collect(Collectors.toList());
        e.replyChoices(options).queue();
    }
}
