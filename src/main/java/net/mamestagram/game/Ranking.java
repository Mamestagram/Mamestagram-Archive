package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;

public class Ranking {

    private static int count = 0;
    public static int rowCount;
    private static ArrayList<Integer> rPP = new ArrayList<>(), rSS = new ArrayList<>(), rS = new ArrayList<>(), rA = new ArrayList<>(), rPlays = new ArrayList<>();
    private static ArrayList<String> rCountry = new ArrayList<>(), rName = new ArrayList<>();
    private static ArrayList<Double> rAcc = new ArrayList<>();
    public static String rankView = "";

    public static EmbedBuilder getRankingData(int mode, int row) throws SQLException {

        EmbedBuilder eb = new EmbedBuilder();
        PreparedStatement ps;
        ResultSet result;

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
            rCountry.add(count, result.getString("country"));
            rName.add(count, result.getString("name"));
            rAcc.add(count, result.getDouble("acc"));
            rPlays.add(count, result.getInt("plays"));
            rPP.add(count, result.getInt("pp"));
            rSS.add(count, result.getInt("SS"));
            rS.add(count, result.getInt("S"));
            rA.add(count, result.getInt("A"));
            count++;
        }

        for(int i = row; i < row + 5; i++) {
            if(i > rowCount - 1) {
                break;
            }
            rankView += "#" + (i + 1) + ": **__" + rName.get(i) + "__** (" + rCountry.get(i) + ")\n" + "Acc: **" + rAcc.get(i) + "%**\nPP: **" + rPP.get(i) + "pp**\nSS: ``" + rSS.get(i) + "`` / S: ``" + rS.get(i) + "`` / A: ``" + rA.get(i) + "``\n\n";
        }

        eb.addField("**:pushpin: Player Ranking**", rankView, false);
        eb.setColor(Color.WHITE);

        return eb;
    }
}
