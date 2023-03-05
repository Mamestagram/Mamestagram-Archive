package net.mamestagram.game;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.mamestagram.Main.*;

public class LoginAlert {

    private static int loginID = 0;
    private static int bloginID = 0;
    private static int userID = 0;
    private static String userName;

    public static void loginStatusUpdate() throws SQLException {
        PreparedStatement ps = null;
        ResultSet result = null;

        bloginID = loginID;

        ps = connection.prepareStatement("select id from ingame_logins order by id desc limit 1");
        result = ps.executeQuery();

        while(result.next()) {
            loginID = (result.getInt("id"));
        }

        if(bloginID != loginID) {
            var date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

            ps = connection.prepareStatement("select userid from ingame_logins where id = ?");
            ps.setInt(1 ,loginID);
            result = ps.executeQuery();
            while (result.next()) {
                userID = result.getInt("userid");
            }

            ps = connection.prepareStatement("select name from users where id = ?");
            ps.setInt(1, userID);
            result = ps.executeQuery();
            while(result.next()) {
                userName = result.getString("name");
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("**" + userName + " has logged in**");
            eb.setFooter("Login at " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
            eb.setColor(Color.GREEN);

            jda.getGuildById(944248031136587796L).getTextChannelById(1081737936401350717L).sendMessageEmbeds(eb.build()).queue();
        } else {
            return;
        }

    }
}
