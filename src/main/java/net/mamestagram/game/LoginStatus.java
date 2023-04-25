package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.swing.text.DateFormatter;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.List.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static net.mamestagram.Main.*;

public class LoginStatus {

    private static final long guildID = 944248031136587796L;
    private static final long channelID = 1081737936401350717L;
    private static int currentLoginID = 0;
    private static int userID = 0;
    private static String userName;
    private static boolean isFirstLogin = true;

    public static void getLoginStatus() throws SQLException {

        ArrayList<Integer> modePP = new ArrayList<>();
        List<Integer> tempData = new ArrayList<>(Arrays.asList(0, 0)); //0 = pp, 1 = mode
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

            for(int i = 0; i <= 8; i++) {
                preparedStatement = connection.prepareStatement("select pp from stats where id = ? and mode = ?");
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, i);
                resultSet = preparedStatement.executeQuery();

                if(resultSet.next()) {
                    modePP.add(resultSet.getInt("pp"));
                }
            }

            int currentCount = 0;

            for(int i : modePP) {
                if(tempData.get(0) <= i) {
                    tempData.add(0, i);
                    tempData.add(1, currentCount);
                }
                currentCount++;
            }

            preparedStatement = connection.prepareStatement("select pp, plays, acc, tscore, max_combo, playtime from stats where id = ? and mode = ?");
            preparedStatement.setInt(1, tempData.get(0));
            preparedStatement.setInt(2, tempData.get(1));
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                //TODO
            }

            embedBuilder.setTitle(":inbox_tray: **" + userName + " has logged in**");
            embedBuilder.setFooter("Connected at " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
            embedBuilder.setColor(Color.GREEN);

            jda.getGuildById(guildID).getTextChannelById(channelID).sendMessageEmbeds(embedBuilder.build()).addActionRow(
                    Button.link("https://web.mamesosu.net/profile/id=" + userID + "/mode=std/special=none/bestpp=1&mostplays=1&recentplays=1", "Go to Profile!")
            ).queue();
        } else {
            isFirstLogin = false;
        }
    }
}
