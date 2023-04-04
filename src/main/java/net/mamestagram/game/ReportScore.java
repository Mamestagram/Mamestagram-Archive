package net.mamestagram.game;

//TODO

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import static net.mamestagram.module.ModalModule.*;

public class ReportScore extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if(e.getComponentId().equals("report")) {
            TextInput reportReason = createTextInput("reason", "Reason", "Why do you report this user?", true, TextInputStyle.PARAGRAPH);
            Modal modal = Modal.create("report-score", "Score Report Form")
                    .addActionRows(ActionRow.of(reportReason))
                    .build();
            e.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        String reportUser = e.getUser().getAsTag();
    }
}
