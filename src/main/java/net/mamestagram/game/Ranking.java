package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;

public class Ranking {

    private static int count = 0;
    private static int[] rPP = new int[20000], rSS = new int[20000], rS = new int[20000], rA = new int[20000], rPlays = new int[20000];
    private static String[] rCountry = new String[20000], rName = new String[20000];
    private static double[] rAcc = new double[20000];
    public static String rankView = "";
    public static int rowCount;

    public static EmbedBuilder rankingViewerMessage(int mode, int row) throws SQLException {
        EmbedBuilder eb = new EmbedBuilder();
        PreparedStatement ps = null;
        ResultSet result = null;

        ps = connection.prepareStatement("SELECT COUNT(*) FROM users join stats on users.id = stats.id where mode = ? and not users.id = 1 and not acc = 0");
        ps.setInt(1, mode);
        result = ps.executeQuery();

        while(result.next()) {
            rowCount = result.getInt("COUNT(*)");
        }

        ps = connection.prepareStatement("SELECT RANK() OVER(ORDER BY pp DESC) ranking, country, name, acc, plays, pp, xh_count + x_count AS 'SS', sh_count + s_count AS 'S', a_count AS 'A'" +
                "FROM users " +
                "JOIN stats " +
                "ON users.id = stats.id " +
                "WHERE mode = ? " +
                "AND NOT users.id = 1 AND NOT acc = 0 " +
                "ORDER BY pp DESC ");
        ps.setInt(1, mode);
        result = ps.executeQuery();

        count = 0;

        while(result.next()) {
            rCountry[count] = result.getString("country");
            rName[count] = result.getString("name");
            rAcc[count] = result.getDouble("acc");
            rPlays[count] = result.getInt("plays");
            rPP[count] = result.getInt("pp");
            rSS[count] = result.getInt("SS");
            rS[count] = result.getInt("S");
            rA[count] = result.getInt("A");
            count++;
        }

        for(int i = row; i < row + 10; i++) {
            if(i > rowCount-1) {
                break;
            }
            rankView += "#" + (i + 1) + ": **__" + rName[i] + "__** (" + rCountry[i] + ")\n" + "Acc: **" + rAcc[i] + "%**\nPP: **" + rPP[i] + "pp**\nSS: ``" + rSS[i] + "`` / S: ``" + rS[i] + "`` / A: ``" + rA[i] + "``\n\n";
        }

        eb.addField("**Ranking (mamesosu.net)\n **", rankView, false);
        eb.setColor(Color.CYAN);

        return eb;
    }
}
