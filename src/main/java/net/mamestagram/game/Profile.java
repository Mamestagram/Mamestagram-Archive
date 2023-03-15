package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;
import static net.mamestagram.message.EmbedMessageData.*;

public class Profile {

    public static EmbedBuilder profileData(Member pName, int mode) throws SQLException {

        double UserACC = 0.00;
        int userRank = 0, userCountryRank = 0, userID, userReplay = 0, userRankedScore = 0, userTotalScore = 0, userCombo = 0, UserPP = 0, UserPlayCount = 0, A_Count = 0, S_Count = 0, SS_Count = 0;
        String modeName = "", Country = "", userName;
        PreparedStatement ps;
        ResultSet result;

        EmbedBuilder eb = new EmbedBuilder();

        ps = connection.prepareStatement("select * from users where name = ?");
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

        ps = connection.prepareStatement("select country from users where id = ?");
        ps.setInt(1, userID);
        result = ps.executeQuery();

        while(result.next()) {
            Country = result.getString("country");
        }

        ps = connection.prepareStatement("select plays, pp, replay_views, a_count, s_count+sh_count, xh_count+x_count, max_combo, acc, rscore, tscore from stats where id = ? and mode = " + mode);

        ps.setInt(1, userID);
        result = ps.executeQuery();

        while(result.next()) {
            UserPlayCount = result.getInt("plays");
            UserPP = result.getInt("pp");
            userReplay = result.getInt("replay_views");
            A_Count = result.getInt("a_count");
            S_Count = result.getInt("s_count+sh_count");
            SS_Count = result.getInt("xh_count+x_count");
            userCombo = result.getInt("max_combo");
            UserACC = result.getDouble("acc");
            userRankedScore = result.getInt("rscore");
            userTotalScore = result.getInt("tscore");
        }

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

        modeName = getModeName(mode);

        eb.setAuthor("osu! " + modeName + " Profile for " + userName, "https://osu.ppy.sh/images/layout/avatar-guest.png","https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.setThumbnail("https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.addField("**Performance**", "Ranking: **#" + String.format("%,d",userRank) + "** (" + Country + ": **#" + String.format("%,d",userCountryRank) + "**)\n" +
                "Total PP: **" + String.format("%,d", UserPP) + "pp**\n" +
                "Ranked Score: **" + String.format("%,d",userRankedScore) + "**\n" +
                "Total Score: **" + String.format("%,d",userTotalScore) + "**\n" +
                "Accuracy: **" + UserACC + " %**\n" +
                "Play Count: **" + String.format("%,d",UserPlayCount) + "**\n" +
                "Maximum Combo: **" + String.format("%,d",userCombo) + "**\n" +
                "Replay Views: **" + String.format("%,d", userReplay) + "**\n  ", false);
        eb.addField("**Grade**", "**SS:** ``" + String.format("%,d",SS_Count) + "`` **S:** ``" + String.format("%,d",S_Count) + "`` **A:** ``" + String.format("%,d",A_Count) + "``", false);
        eb.setFooter("mamesosu.net", "https://cdn.discordapp.com/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.setColor(Color.PINK);

        return eb;
    }
}
