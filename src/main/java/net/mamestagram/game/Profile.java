package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;
import static net.mamestagram.message.EmbedMessageData.*;

public class Profile {

    public static EmbedBuilder getProfileData(String sName, Member pName, int mode) throws SQLException, IOException {

        int userID = 0, userRank = 0, userCountryRank = 0, userWeightedPP = 0, user1stRank = 0;
        double userAveragePP = 0.0, userAverageRate = 0.0;
        String userName, userCountry = "";

        PreparedStatement ps;
        ResultSet result;

        EmbedBuilder eb = new EmbedBuilder();

        ps = connection.prepareStatement("select * from users where name = ?");

        if(sName == null) {
            ps.setString(1, pName.getNickname());
            result = ps.executeQuery();

            if(!result.next()) {
                ps = connection.prepareStatement("select * from users where name = ?");
                ps.setString(1, pName.getUser().getName());
                result = ps.executeQuery();

                if(!result.next()) {
                    return notUserFoundMessage(pName.getUser().getName());
                } else {
                    userID = result.getInt("id");
                    userName = pName.getUser().getName();
                }
            } else {
                userID = result.getInt("id");
                userName = pName.getNickname();
            }
        } else {
            ps.setString(1, sName);
            result = ps.executeQuery();

            if(!result.next()) {
                return notUserFoundMessage(sName);
            } else {
                userID = result.getInt("id");
                userName = sName;
            }
        }

        ps = connection.prepareStatement("select country from users where id = ?");
        ps.setInt(1, userID);
        result = ps.executeQuery();

        while(result.next()) {
            userCountry = result.getString("country");
        }

        ps = connection.prepareStatement("SELECT COUNT(*) + 1 AS 'cranking' " +
                "FROM stats " +
                "JOIN users " +
                "ON stats.id = users.id " +
                "WHERE pp > ( " +
                "    SELECT pp " +
                "    FROM stats " +
                "    WHERE id = ? " +
                "    AND mode = ? " +
                "    AND country = ( " +
                "        SELECT country " +
                "        FROM users " +
                "        WHERE id = ? " +
                "        AND mode = ? " +
                "    ) " +
                ") " +
                "AND mode = ?");

        ps.setInt(1, userID);
        ps.setInt(2, mode);
        ps.setInt(3, userID);
        ps.setInt(4, mode);
        ps.setInt(5, mode);

        result = ps.executeQuery();

        if(result.next()) {
            userCountryRank = result.getInt("cranking");
        }

        ps = connection.prepareStatement("SELECT COUNT(*) + 1 AS 'ranking' " +
                "FROM stats " +
                "WHERE pp > (" +
                "SELECT pp " +
                "FROM stats " +
                "WHERE id = ? " +
                "AND mode = ? ) " +
                "AND mode = ?");

        ps.setInt(1, userID);
        ps.setInt(2, mode);
        ps.setInt(3, mode);

        result = ps.executeQuery();

        if(result.next()) {
            userRank = result.getInt("ranking");
        }

        ps = connection.prepareStatement("select pp from stats where id = " + userID + " and mode = " + mode);
        result = ps.executeQuery();

        if(result.next()) {
            userWeightedPP = result.getInt("pp");
        }

        user1stRank = getUserRank(userID, mode);
        userAveragePP = getAveragePP(userID, mode);
        userAverageRate = getAverageStarRate(userID, mode);

        eb.setAuthor("osu! " + getModeName(mode) + " Profile for " + userName, "https://web.mamesosu.net/profile/id=" + userID + "/mode=std/special=none", "https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.addField("**Performance of " + userName + "**" , "Player: **" + userName + "**\n" +
                "Rank: **#" + String.format("%,d",userRank) + "** (" + userCountry + ": **#" + String.format("%,d",userCountryRank) + "**)\n" +
                "Weighted PP: **" + String.format("%,d",userWeightedPP) + "pp**\n" +
                "#1 Count: **" + String.format("%,d",user1stRank) + "**\n" +
                "Average PP: **" + roundNumber(userAveragePP, 2) + "pp**\n" +
                "Average Rate: **" + roundNumber(userAverageRate, 2) + "**", false);
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.setColor(Color.CYAN);

        return eb;
    }
}
