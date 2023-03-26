package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;

public class LoginStatus {

    private static int currentLoginID = 0;
    private static int userID = 0;
    private static String userName;
    private static boolean isFirstLogin = true;

    public static void getLoginStatus() throws SQLException {

        PreparedStatement preparedStatement;
        ResultSet resultSet;

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

            embedBuilder.setAuthor(userName + " has logged in", "https://web.mamesosu.net/profile/id=" + userID + "/mode=std/special=none", "https://osu.ppy.sh/images/layout/avatar-guest.png");
            embedBuilder.setColor(Color.GREEN);

            jda.getGuildById(944248031136587796L).getTextChannelById(1081737936401350717L).sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            isFirstLogin = false;
        }
    }
}
