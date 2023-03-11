package net.mamestagram.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;

import static net.mamestagram.Main.*;

public class PatchAnnounce extends ListenerAdapter {

    private static final long guildID = 944248031136587796L;
    private static final long toCHID = 1084068468036472852L;

    private static EmbedBuilder announceBuilder(String project, String version, String text) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("***:white_check_mark: Patch [" + project + " #" + version + "]***\n\n**" + text + "**");
        eb.setColor(Color.GREEN);

        return eb;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getMessage().getContentRaw().equals("dev-create-form")) {
            e.getMessage().reply("ここに書いた内容はannounceへ通知されます\nご注意ください")
                    .addActionRow(
                            Button.primary("create","Create")
                    ).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if(e.getComponentId().equals("create")) {
            TextInput projectName = TextInput.create("project-name", "Project", TextInputStyle.SHORT)
                    .setPlaceholder("Please Enter Project Name")
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            TextInput projectVersion = TextInput.create("version", "Version", TextInputStyle.SHORT)
                    .setPlaceholder("Please Enter Project Version")
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            TextInput projectText = TextInput.create("text", "Text", TextInputStyle.SHORT)
                    .setPlaceholder("Please Write Update Contents")
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("announce-create", "Announce Generator")
                    .addActionRows(ActionRow.of(projectName), ActionRow.of(projectVersion), ActionRow.of(projectText))
                    .build();

            e.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        String name = e.getValue("project-name").getAsString();
        String version = e.getValue("version").getAsString();
        String text = e.getValue("text").getAsString();

        e.reply("send").queue();
        jda.getGuildById(guildID).getTextChannelById(toCHID).sendMessageEmbeds(announceBuilder(name,version,text).build()).queue();
    }
}
