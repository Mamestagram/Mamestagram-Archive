package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.swing.text.DateFormatter;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.mamestagram.Main.*;

public class LoginStatus {

    private static final long GUILDID = 944248031136587796L;
    private static final long CHANNELID = 1081737936401350717L;
    private static int currentLoginID = 0;
    private static int userID = 0;
    private static String userName;
    private static boolean isFirstLogin = true;

    public static void getLoginStatus() throws SQLException {

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        var date = DateTimeFormatter.ofPattern("HH:mm");

        EmbedBuilder embedBuilder = new EmbedBuilder();

        int previousLoginID = currentLoginID;

        preparedStatement = connection.prepareStatement("SELECT id FROM ingame_logins ORDER BY id DESC LIMIT 1");
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            currentLoginID = resultSet.getInt("id");
        }

        if (previousLoginID != currentLoginID && !isFirstLogin) {

            preparedStatement = connection.prepareStatement("SELECT userid FROM ingame_logins WHERE id = ?");
            preparedStatement.setInt(1, currentLoginID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userID = resultSet.getInt("userid");
            }

            preparedStatement = connection.prepareStatement("SELECT name FROM users WHERE id = ?");
            preparedStatement.setInt(1, userID);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userName = resultSet.getString("name");
            }

            embedBuilder.setTitle(":inbox_tray: **" + userName + " has logged in**");
            embedBuilder.setFooter("Connected at " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
            embedBuilder.setColor(Color.GREEN);

            jda.getGuildById(GUILDID).getTextChannelById(CHANNELID).sendMessageEmbeds(embedBuilder.build()).addActionRow(
                    Button.link("https://web.mamesosu.net/profile/id=" + userID + "/mode=std/special=none/bestpp=1&mostplays=1&recentplays=1", "Go to Profile!")
            ).queue();
        } else {
            isFirstLogin = false;
        }
    }
}
