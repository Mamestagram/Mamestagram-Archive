package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.data.EmbedMessageData.*;

public class Profile {
    public static EmbedBuilder profileData(Member pName, int mode) throws SQLException { //引数にはdiscordのnicknameを取得
        double UserACC = 0.00;
        EmbedBuilder eb = new EmbedBuilder();
        int userRank = 0, userCountryRank = 0, userID = 0, userReplay = 0, userTotalScore = 0, userCombo = 0, UserPP = 0, UserPlayCount = 0, A_Count = 0, S_Count = 0, SS_Count = 0;
        String modeName = "", Country = "", userName = "";
        PreparedStatement ps = null;
        ResultSet result = null;

        ps = connection.prepareStatement("select * from users where name = ?");
        ps.setString(1, pName.getNickname());

        result = ps.executeQuery();

        /*data exist?*/

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

        /*playcount*/

        ps = connection.prepareStatement("select plays from stats where id = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            UserPlayCount = result.getInt("plays");
        }

        /*pp*/

        ps = connection.prepareStatement("select pp from stats where id = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            UserPP = result.getInt("pp");
        }

        /*replay*/

        ps = connection.prepareStatement("select replay_views from stats where id = ? and mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userReplay = result.getInt("replay_views");
        }

        /*Country*/

        ps = connection.prepareStatement("select country from users where id = ?");
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            Country = result.getString("country");
        }

        /*ACount*/

        ps = connection.prepareStatement("select a_count from stats where id = ? AND mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            A_Count = result.getInt("a_count");
        }

        /*SCount*/

        ps = connection.prepareStatement("select s_count+sh_count from stats where id = ? AND mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            S_Count = result.getInt("s_count+sh_count");
        }

        /*SSCount*/

        ps = connection.prepareStatement("select xh_count+x_count from stats where id = ? AND mode = " + mode);
        ps.setInt(1, userID);
        result = ps.executeQuery();
        while(result.next()) {
            SS_Count = result.getInt("xh_count+x_count");
        }

        /*CountryRank*/

        ps = connection.prepareStatement("SELECT COUNT(*) + 1 AS 'cranking' " +
                        "FROM stats " +
                        "JOIN users " +
                        "ON stats.id = users.id " +
                        "WHERE pp > ( " +
                        "    SELECT pp " +
                        "    FROM stats " +
                        "    WHERE id = ? " +
                        "    AND mode = " + mode +
                        "    AND country = ( " +
                        "        SELECT country " +
                        "        FROM users " +
                        "        WHERE id = ? " +
                        "        AND mode = " + mode +
                        "    ) " +
                        ") " +
                        "AND mode = " + mode);
        ps.setInt(1, userID);
        ps.setInt(2, userID);
        result = ps.executeQuery();
        while(result.next()) {
            userCountryRank = result.getInt("cranking");
        }

        /*max combo*/

        ps = connection.prepareStatement("select max_combo from stats where id = ? and mode = " + mode);
        ps.setInt(1,userID);
        result = ps.executeQuery();
        while(result.next()) {
            userCombo = result.getInt("max_combo");
        }

        /*ACC*/

        ps = connection.prepareStatement("select acc from stats where id = ? AND mode = " + mode);
        ps.setDouble(1,userID);
        result = ps.executeQuery();
        while(result.next()) {
            UserACC = result.getDouble("acc");
        }

        /*TotalScore*/

        ps = connection.prepareStatement("select tscore from stats where id = ? AND mode = " + mode);
        ps.setDouble(1,userID);
        result = ps.executeQuery();
        while(result.next()) {
            userTotalScore = result.getInt("tscore");
        }


        /*rank*/

        ps = connection.prepareStatement("select COUNT(*) + 1 AS 'ranking' " +
                "FROM stats " +
                "WHERE pp > (" +
                "SELECT pp " +
                "FROM stats " +
                "WHERE id = ? " +
                "AND mode = " + mode + ") " +
                "AND mode = " + mode );
        ps.setInt(1,userID);
        result = ps.executeQuery();
        while(result.next()) {
            userRank = result.getInt("ranking");
        }

        switch (mode) {
            case 0:
                modeName = "standard";
                break;
            case 1:
                modeName = "taiko";
                break;
            case 2:
                modeName = "catch";
                break;
            case 3:
                modeName = "mania";
                break;
            case 4:
                modeName = "relax";
                break;
        }

        eb.setAuthor("osu! " + modeName + " Profile for " + userName, "https://osu.ppy.sh/images/layout/avatar-guest.png","https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.setThumbnail("https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.addField("**Global Ranking**", "▸ #" + userRank + " (" + Country + ": #" + userCountryRank + ")", false);
        eb.addField("**Total Score**", "▸ " + userTotalScore, false);
        eb.addField("**Accuracy**", "▸ "+ UserACC + "%", false);
        eb.addField("**PP**", "▸ "+ UserPP + "pp", false);
        eb.addField("**Play Count**", "▸ "+ UserPlayCount + " plays", false);
        eb.addField("**Maximum Combo**", "▸ " + userCombo + "x", false);
        eb.addField("**Replays Watch**", "▸ " + userReplay + " views", false);
        eb.addField("**Grade**", "SS:``" + SS_Count + "`` S:``" + S_Count + "`` A:``" + A_Count + "``", false);
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.setColor(Color.GREEN);

        return eb;
    }
}
