package net.mamestagram.game;

//TODO 家で続きかきます

import com.fasterxml.jackson.databind.JsonNode;
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

import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static net.mamestagram.Main.connection;
import static net.mamestagram.module.OSUModule.getMapData;
import static net.mamestagram.module.ModalModule.*;

public class MapStatusBeta extends ListenerAdapter {

    private static EmbedBuilder eb;
    private static PreparedStatement ps;
    private static ResultSet result;

    private static EmbedBuilder mapRequestMessage() {

        eb = new EmbedBuilder();

        eb.setTitle("**Map Status Change Request**");
        eb.addField("**:notebook_with_decorative_cover: How to Request**", "Click the button for the status you wish to apply for and enter the required data", false);
        eb.addField("**:pencil: Changeable status**", ":white_check_mark: Ranked\n:heart: Loved", false);
        eb.setColor(Color.PINK);
        return eb;
    }

    private static EmbedBuilder mapRankedSuccessMessage(User user, String mapsetID, String comment) throws IOException, SQLException {

        eb = new EmbedBuilder();

        JsonNode root;
        String md5 = null, mapTitle, mapCreator;

        var time = DateTimeFormatter.ofPattern("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        try {
            date = sdf.parse(DateTimeFormatter.ofPattern("MM/dd").format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
        } catch(ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);

        if (Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(LocalDateTime.now(ZoneId.of("Asia/Tokyo")))) >= 4) {
            calendar.add(Calendar.DATE, 1);
        }

        ps = connection.prepareStatement("select md5 from maps where set_id = ? limit 1");
        ps.setInt(1, Integer.parseInt(mapsetID));

        result = ps.executeQuery();

        if(result.next()) {
            md5 = result.getString("md5");
        }

        root = getMapData(md5);

        mapTitle = root.get(0).get("title").asText() + " by " + root.get(0).get("artist").asText();
        mapCreator = root.get(0).get("creator").asText();

        eb.setTitle(":star2: **This map will be Ranked at " + sdf.format(calendar.getTime()) + " 4:00 (JST)!**");
        eb.addField("**Map Data**", "Name: **"+ mapTitle + "**\n" +
                "Map Creator: **" + mapCreator + "**", false);
        eb.addField("**Map Tester**", user.getAsMention(), false);
        eb.addField("**Tester Comment**", "```" + comment + "```", false);
        eb.setImage("https://assets.ppy.sh/beatmaps/" + mapsetID + "/covers/cover.jpg?");
        eb.setFooter("Accepted at " + time.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))), "https://media.discordapp.net/attachments/944984741826932767/1095668104824115230/check.png?width=662&height=662");
        eb.setColor(Color.green);

        return eb;
    }

    private static EmbedBuilder mapRankedNotSuccessMessage(User user, String mapsetID, String comment) throws SQLException, IOException {

        eb = new EmbedBuilder();

        JsonNode root;
        String md5 = null, mapTitle, mapCreator;

        ps = connection.prepareStatement("select md5 from maps where set_id = ? limit 1");
        ps.setInt(1, Integer.parseInt(mapsetID));
        result = ps.executeQuery();

        if(result.next()) {
            md5 = result.getString("md5");
        }

        root = getMapData(md5);

        mapTitle =  root.get(0).get("title").asText() + " by " + root.get(0).get("artist").asText();
        mapCreator = root.get(0).get("creator").asText();

        eb.setTitle("**:x: Ranked request has been canceled!**");
        eb.addField("**Map Data**", "Name: **"+ mapTitle + "**\n" +
                "Map Creator: **" + mapCreator + "**", false);
        eb.addField("**Map Tester**", user.getAsMention(), false);
        eb.addField("**Tester Comment**", "```" + comment + "```", false);
        eb.setColor(Color.RED);

        return eb;
    }

    private static EmbedBuilder mapRequestToDMMessage(User user, String id, int statusid, String mode) {

        EmbedBuilder eb = new EmbedBuilder();

        if(statusid == 1) {
            eb.setTitle(user.getAsMention() + "**よりMap Ranked Requestが届きました!**\n**マップをプレイ後、承認の連絡を送信してください**");
            eb.setColor(Color.green);
        } else if(statusid == 2) {
            eb.setTitle(user.getAsMention() + "**よりMap Loved Requestが届きました!**\n**マップをプレイ後、承認の連絡を送信してください**");
            eb.setColor(Color.PINK);
        }
        eb.addField("**Map Link**", "https://osu.ppy.sh/beatmapsets/" + id, false);
        eb.addField("**Map Mode**", mode, false);

        return eb;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getChannel().getIdLong() == 1093845482247307276L && e.getMessage().getContentRaw().equals("create-req-form")) {
            e.getMessage().replyEmbeds(mapRequestMessage().build())
                    .addActionRow(
                            Button.success("button_ranked", "Ranked"),
                            Button.danger("button_loved", "Loved")
                    ).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if(e.getComponentId().equals("button_ranked")) {
            TextInput mapID = createTextInput("ranked_mapID", "Beatmapset ID", "If url is https://osu.ppy.sh/beatmapsets/1#osu/75, then type 1", true, TextInputStyle.SHORT);
            TextInput mode = createTextInput("ranked_mode", "Map Mode", "Type 'osu', 'taiko', 'catch', 'mania'", true, TextInputStyle.SHORT);
            Modal modal = Modal.create("request-ranked", "Ranked Request Form")
                    .addActionRows(ActionRow.of(mapID), ActionRow.of(mode))
                    .build();

            e.replyModal(modal).queue();
        } else if(e.getComponentId().equals("button_loved")) {
            TextInput mapID = createTextInput("loved_mapID", "BeatmapsetID", "If url is https://osu.ppy.sh/beatmapsets/1#osu/75, then type 1", true, TextInputStyle.SHORT);
            TextInput mode = createTextInput("loved_mode", "Map Mode", "Type 'osu', 'taiko', 'catch', 'mania'", true, TextInputStyle.SHORT);
            Modal modal = Modal.create("request-loved", "Loved Request Form")
                    .addActionRows(ActionRow.of(mapID), ActionRow.of(mode))
                    .build();

            e.replyModal(modal).queue();
        } else if(e.getComponentId().equals("ranked_accept")) {
            TextInput comment = createTextInput("ranked-accept_comment", "コメント", "必ずプレイヤーが役に立つコメントを入力してください！", true, TextInputStyle.SHORT);
            Modal modal = Modal.create("ranked-request-accept", "Rankedリクエスト許可フォーム")
                    .addActionRows(ActionRow.of(comment))
                    .build();

            e.replyModal(modal).queue();
        } else if(e.getComponentId().equals("ranked_deny")) {
            TextInput comment = createTextInput("ranked-deny_comment", "コメント", "何故許可できないのかの理由を入力してください", true, TextInputStyle.SHORT);
            Modal modal = Modal.create("ranked-request-deny", "Rankedリクエスト拒否フォーム")
                    .addActionRows(ActionRow.of(comment))
                    .build();

            e.replyModal(modal).queue();
        } else if(e.getComponentId().equals("loved_accept")) {
            TextInput comment = createTextInput("loved-accept_comment", "コメント", "必ずプレイヤーが役に立つコメントを入力してください！", true, TextInputStyle.SHORT);
            Modal modal = Modal.create("loved-request-accept", "Lovedリクエスト許可フォーム")
                    .addActionRows(ActionRow.of(comment))
                    .build();

            e.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        if(e.getModalId().equals("request-ranked")) {
            Role osuTesterRole = e.getGuild().getRoleById(1093865053448589342L);
            List<Member> osuTester = e.getGuild().getMembersWithRoles(osuTesterRole);

            for(Member user : osuTester) {
                user.getUser().openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(e.getValue("ranked_mapID").getAsString())
                                .addEmbeds(mapRequestToDMMessage(e.getUser(), e.getValue("ranked_mapID").getAsString(), 1, e.getValue("ranked_mode").getAsString()).build())
                                .addActionRow(Button.success("ranked_accept", "承認"),
                                        Button.danger("ranked_deny", "拒否")
                        )).queue();
            }
            e.reply("Thank you for your request. Your request has been sent to the tester.").setEphemeral(true).queue();
        } else if(e.getModalId().equals("request-loved")) {
            Role osuTesterRole = e.getGuild().getRoleById(1093865053448589342L);
            List<Member> osuTester = e.getGuild().getMembersWithRoles(osuTesterRole);

            for(Member user : osuTester) {
                user.getUser().openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(e.getValue("loved_mapID").getAsString())
                                .addEmbeds(mapRequestToDMMessage(e.getUser(), e.getValue("loved_mapID").getAsString(), 2, e.getValue("loved_mode").getAsString()).build())
                                .addActionRow(Button.success("loved_accept", "承認"),
                                        Button.danger("loved_deny", "拒否")
                                )).queue();
            }
            e.reply("Thank you for your request. Your request has been sent to the tester.").setEphemeral(true).queue();
        }
    }
}
