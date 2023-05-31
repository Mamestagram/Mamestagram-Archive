package net.mamestagram.Game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;

import static net.mamestagram.Module.Modal.*;
import static net.mamestagram.Main.*;

public class ReportScore extends ListenerAdapter {

    long messageID;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if(e.getComponentId().equals("report")) {
            TextInput reportReason = createTextInput("reason", "Comments", "Please provide any information you believe could be useful (Write JA or EN)", true, TextInputStyle.PARAGRAPH);
            Modal modal = Modal.create("report-score", "Report this score?")
                    .addActionRows(ActionRow.of(reportReason))
                    .build();
            e.replyModal(modal).queue();
            messageID = e.getMessageIdLong();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        if(e.getModalId().equals("report-score")) {

            long guildID = e.getGuild().getIdLong();
            long channelID = e.getChannel().getIdLong();
            User user = jda.getUserById(629882176695042049L);

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.RED);
            eb.setTitle("**Received Report!**");
            eb.addField("**Reason**", "```" + e.getValue("reason").getAsString() + "```", false);
            eb.addField("**Score link**", "https://discord.com/channels/" + guildID + "/" + channelID + "/" + messageID, false);
            eb.addField("**Reporter**", e.getUser().getAsMention(), false);

            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                    .queue();

            e.reply("Thank you for your cooperation!").setEphemeral(true).queue();
        }
    }
}
