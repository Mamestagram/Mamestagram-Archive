package net.mamestagram.Game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.Module.DataBase.*;
import static net.mamestagram.Message.EmbedMessage.*;
import static net.mamestagram.Module.OSU.*;

public class Profile {

    public static EmbedBuilder getProfileData(String sName, Member pName, int mode) throws SQLException, IOException {

        int userID;
        String userName;

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

        eb.setAuthor("osu! " + getModeName(mode) + " Profile for " + userName, "https://web.mamesosu.net/profile/id=" + userID + "/mode=std/special=none/bestpp=1&mostplays=1&recentplays=1", "https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.setThumbnail("https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.addField("**:notepad_spiral: Performance" + "**" , "Player: **" + userName + "**\n" +
                "Rank: **#" + String.format("%,d",getGlobalRank(userID, mode)) + "** (" + getUserCountry(userID) + ": **#" + String.format("%,d",getCountryRank(userID, mode)) + "**)\n" +
                "Weighted PP: **" + String.format("%,d",getWeightedPP(mode, userID)) + "pp**", false);
        eb.addField("**:bar_chart: Play Analysis**", "Average PP: **" + roundNumber(getAveragePP(userID, mode), 2) + "**\n" +
        "Average Rate: **" + roundNumber(getAverageStarRate(userID, mode), 2) + "**\n" +
                "Map #1: **" + String.format("%,d",getNumberOfMapsWonByUser(userID, mode)) + "**", false);
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.setColor(Color.CYAN);

        return eb;
    }

    private static String getUserCountry(int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        String query = ("select country from users where id = ?");

        ps = connection.prepareStatement(query);
        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            return result.getString("country");
        } else {
            return null;
        }
    }

    private static int getWeightedPP(int playMode, int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        String query = ("select pp from stats where id = " + userID + " and mode = " + playMode);

        ps = connection.prepareStatement(query);

        result = ps.executeQuery();

        if(result.next()) {
            return result.getInt("pp");
        } else {
            return 0;
        }
    }
}
