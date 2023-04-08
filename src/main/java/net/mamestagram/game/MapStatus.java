package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.w3c.dom.Text;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static net.mamestagram.module.ModalModule.*;
import static net.mamestagram.Main.*;

public class MapStatus extends ListenerAdapter {

    private static EmbedBuilder mapRequestMessage() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**Map Ranked Request**");
        eb.addField("**How do I make a request?**", "Click the button below and enter the required data", false);
        eb.setColor(Color.CYAN);

        return eb;
    }

    private static EmbedBuilder mapRequestToDMMessage(String url, String mode) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Map Request**");
        eb.addField("**Map Link**", url, false);
        eb.addField("**Map Mode**", mode, false);
        eb.setColor(Color.green);

        return eb;
    }

    private static EmbedBuilder mapRankedSuccess(User user, String mapTitle, String comment) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**New Ranked Map is now available!**");
        eb.addField("**Map Name**", mapTitle, false);
        eb.addField("**Map Tester**", user.getAsMention(), false);
        eb.addField("**Tester Comment**", "```" + comment + "```", false);
        eb.setColor(Color.green);

        return eb;
    }

    private static EmbedBuilder mapRankedNotSuccess(User user, String mapTitle, String comment) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**Ranked request has been canceled!**");
        eb.addField("**Map Name**", mapTitle, false);
        eb.addField("**Map Tester**", user.getAsMention(), false);
        eb.addField("**Tester Comment**", "```" + comment + "```", false);
        eb.setColor(Color.RED);

        return eb;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getChannel().getIdLong() == 1093845482247307276L && e.getMessage().getContentRaw().equals("create-map-request")) {
            e.getMessage().replyEmbeds(mapRequestMessage().build()).addActionRow(
                    Button.primary("request", "Map Request")
            ).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if(e.getComponentId().equals("request")) {
            TextInput mapURL = createTextInput("map_url", "Map URL", "Please enter the URL of the map you wish to request", true, TextInputStyle.SHORT);
            TextInput mapPlayMode = createTextInput("map_mode", "Map's Mode", "Type 'osu', 'mania', 'catch', 'taiko'", true, TextInputStyle.SHORT);

            Modal modal = Modal.create("request-maps", "Map request for Ranked")
                    .addActionRows(ActionRow.of(mapURL), ActionRow.of(mapPlayMode))
                    .build();

            e.replyModal(modal).queue();
        } else if(e.getComponentId().equals("accept")) {
            TextInput mapTitle = createTextInput("map_title", "マップのタイトル", "マップのタイトルを入力してください", true, TextInputStyle.SHORT);
            TextInput mapsetID = createTextInput("mapset_id", "BeatmapsetID", "BeatmapsetIDを入力してください", true, TextInputStyle.SHORT);
            TextInput testerComment = createTextInput("tester_comment", "コメント", "コメントを入力", true, TextInputStyle.PARAGRAPH);

            Modal modal = Modal.create("accept-request", "マップの承認通知作成")
                    .addActionRows(ActionRow.of(mapTitle), ActionRow.of(mapsetID) ,ActionRow.of(testerComment))
                    .build();

            e.replyModal(modal).queue();
        } else if(e.getComponentId().equals("deny")) {
            TextInput mapTitle = createTextInput("map_title", "マップのタイトル", "マップのタイトルを入力してください", true, TextInputStyle.SHORT);
            TextInput testerComment = createTextInput("tester_comment", "コメント", "コメントを入力", true, TextInputStyle.PARAGRAPH);

            Modal modal = Modal.create("deny-request", "マップの非承認通知作成")
                    .addActionRows(ActionRow.of(mapTitle), ActionRow.of(testerComment))
                    .build();

            e.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        if(e.getModalId().equals("request-maps")) {

            Role osuTesterRole = e.getGuild().getRoleById(1093865053448589342L);

            List<Member> osuTester = e.getGuild().getMembersWithRoles(osuTesterRole);

            String mapMode = e.getValue("map_mode").getAsString();

                for(Member user : osuTester) {
                    user.getUser().openPrivateChannel()
                            .flatMap(channel -> channel.sendMessage(e.getUser().getAsMention() + "からマップリクエストがありました。\n必ず**マップをプレイ後**、下の通知を送信してください")
                                    .addEmbeds(mapRequestToDMMessage(e.getValue("map_url").getAsString(), mapMode).build())
                                    .addActionRow(Button.success("accept", "承認"), Button.primary("deny", "拒否")))
                            .queue();
                }
                e.reply("Request has been sent!").setEphemeral(true).queue();

        } else if(e.getModalId().equals("accept-request")) {
            try {
                PreparedStatement ps = connection.prepareStatement("UPDATE maps SET status = 2 WHERE set_id = ? AND status = 0");
                ps.setString(1, e.getValue("mapset_id").getAsString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            jda.getGuildById(944248031136587796L).getTextChannelById(1081737936401350717L).sendMessageEmbeds(mapRankedSuccess(e.getUser(), e.getValue("map_title").getAsString(), e.getValue("tester_comment").getAsString()).build())
                    .queue();
            e.reply("送信しました").queue();
        } else if(e.getModalId().equals("deny-request")) {
            jda.getGuildById(944248031136587796L).getTextChannelById(1081737936401350717L).sendMessageEmbeds(mapRankedNotSuccess(e.getUser(), e.getValue("map_title").getAsString(), e.getValue("tester_comment").getAsString()).build())
                    .queue();
            e.reply("送信しました").queue();
        }
    }

}
