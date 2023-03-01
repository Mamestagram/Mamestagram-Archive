package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;
import static net.mamestagram.data.EmbedMessageData.*;

public class Profile {
    public static EmbedBuilder profileData(String pName, int mode) throws SQLException { //引数にはdiscordのnicknameを取得
        double UserACC = 0.00;
        EmbedBuilder eb = new EmbedBuilder();
        int userRank = 0, userID = 0, UserPP = 0, UserPlayCount = 0, A_Count = 0, S_Count = 0, SS_Count = 0;
        String modeName = "";
        PreparedStatement ps = null;
        ResultSet result = null;

        ps = connection.prepareStatement("select * from users where name = ?");
        ps.setString(1, pName);

        result = ps.executeQuery();

        /*data exist?*/

        if(!result.next()) {
            return notUserFoundMessage(pName);
        } else {
            userID = result.getInt("id");
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

        /*ACC*/

        ps = connection.prepareStatement("select acc from stats where id = ? AND mode = " + mode);
        ps.setDouble(1,userID);
        result = ps.executeQuery();
        while(result.next()) {
            UserACC = result.getDouble("acc");
        }

        /*rank*/

        ps = connection.prepareStatement("SELECT RANK() OVER(ORDER BY pp DESC) ranking WHERE id = ? AND mode = " + mode);
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

        eb.setAuthor("mamesosu.net " + modeName + " Profile for " + pName, "https://osu.ppy.sh/images/layout/avatar-guest.png","https://osu.ppy.sh/images/layout/avatar-guest.png");
        eb.addField("**Ranking**", "#" + userRank, false);
        eb.addField("**PP**", UserPP + "pp", false);
        eb.addField("**PlayCount**", UserPlayCount + " counts", false);
        eb.addField("**Ranks**", ":rankX: ``" + SS_Count + "`` :rankS: ``" + S_Count + "`` :rankA: ``" + A_Count + "``", false);
        eb.setColor(Color.GREEN);

        return eb;
    }
}
