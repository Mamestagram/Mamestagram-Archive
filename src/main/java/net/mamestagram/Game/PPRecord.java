package net.mamestagram.Game;

import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.mamestagram.Main.*;

public class PPRecord {

    static boolean isFirstBoot = true;

    public static void updatePPRecordMessage() {

        final long guildID = 944248031136587796L;
        final long channelID = 1104675564763222016L;


        if(isFirstBoot) {
            long lastMessageID = Long.parseLong(jda.getGuildById(guildID).getTextChannelById(channelID).getLatestMessageId());
            isFirstBoot = false;
            jda.getGuildById(guildID).getTextChannelById(channelID).deleteMessageById(lastMessageID);


            return;
        }

        long forUpdateMessageID = Long.parseLong(jda.getGuildById(guildID).getTextChannelById(channelID).getLatestMessageId());
    }

    private static EmbedBuilder getPPRecordMessage() throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        EmbedBuilder eb = new EmbedBuilder();
        List<Double> topPP = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        List<Integer> topUser = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0));
        int usersCount = 0;

        for(int i = 0; i <= 8; i++) {
            ps = connection.prepareStatement("SELECT s.pp, s.userid, s.map_md5, s.max_combo, s.mods, s.n300, s.n100, s.n50, s.nmiss, s.ngeki, s.nkatu, s. " +
                    "FROM scores s " +
                    "JOIN maps m ON s.map_md5 = m.md5 " +
                    "WHERE s.mode = ? AND NOT s.grade = 'F' AND s.status NOT IN (-1, 0, 1, 5) AND m.status = 2 " +
                    "ORDER BY s.pp DESC limit 1");
            ps.setInt(1, i);
            result = ps.executeQuery();
            if(result.next()) {
                topUser.add(i, result.getInt("s.userid"));
                topPP.add(i, result.getDouble("s.pp"));
            }
        }

        return eb;
    }
}
