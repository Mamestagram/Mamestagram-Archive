package net.mamestagram.Game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.mamestagram.Main.*;
import static net.mamestagram.Module.DataBase.*;
import static net.mamestagram.Module.OSU.*;

public class StatusBoard {

    static boolean isFirstBoot = true;

    public static void updatePPRecordMessage() throws SQLException {

        final long guildID = 944248031136587796L;
        final long channelID = 1104675564763222016L;

        if(isRestarting) {
            return;
        }

        if(isFirstBoot) {
            //過去のメッセージすべてを削除

            TextChannel channel = jda.getGuildById(944248031136587796L).getTextChannelById(1104675564763222016L);
            MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
            List<Message> message = history.getRetrievedHistory();
            for(Message m : message) {
                m.delete().queue();
            }
            jda.getGuildById(guildID).getTextChannelById(channelID).sendMessageEmbeds(getPPRecordMessage().build()).queue();
            isFirstBoot = false;
            return;
        }
        long forUpdateMessageID = Long.parseLong(jda.getGuildById(guildID).getTextChannelById(channelID).getLatestMessageId());
        jda.getGuildById(guildID).getTextChannelById(channelID).editMessageEmbedsById(forUpdateMessageID, getPPRecordMessage().build()).queue();
    }

    private static EmbedBuilder getPPRecordMessage() throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        EmbedBuilder eb = new EmbedBuilder();
        double pp = 0, acc = 0.0;
        int userID = 0, maxCombo = 0, mods = 0, n300 = 0, n100 = 0, n50 = 0, nmiss = 0, ngeki = 0, nkatu = 0;
        String grade = null, md5Data = "", userName = null, country = null;
        var date = DateTimeFormatter.ofPattern("HH:mm");

        eb.setTitle("**Mamestagram PP Record**", "https://web.mamesosu.net/home");
        eb.appendDescription("Showing PP Record of Mamestagram!");

        for(int i = 0; i <= 8; i++) {
            if(i != 5 && i != 6 && i != 7) {
                ps = connection.prepareStatement("SELECT s.pp, s.userid, s.map_md5, s.max_combo, s.mods, s.n300, s.n100, s.n50, s.nmiss, s.ngeki, s.nkatu, s.grade, s.acc " +
                        "FROM scores s " +
                        "JOIN maps m ON s.map_md5 = m.md5 " +
                        "WHERE s.mode = ? AND NOT s.grade = 'F' AND s.status NOT IN (-1, 0, 1, 5) AND m.status = 2 " +
                        "ORDER BY s.pp DESC limit 1");
                ps.setInt(1, i);
                result = ps.executeQuery();
                while (result.next()) {
                    userID = result.getInt("s.userid");
                    pp = result.getDouble("s.pp");
                    md5Data = result.getString("s.map_md5");
                    maxCombo = result.getInt("s.max_combo");
                    mods = result.getInt("s.mods");
                    n300 = result.getInt("s.n300");
                    n100 = result.getInt("s.n100");
                    n50 = result.getInt("s.n50");
                    nmiss = result.getInt("s.nmiss");
                    ngeki = result.getInt("s.ngeki");
                    nkatu = result.getInt("s.nkatu");
                    grade = result.getString("s.grade");
                    acc = result.getDouble("s.acc");
                }
                ps = connection.prepareStatement("select name, country from users where id = ?");
                ps.setInt(1, userID);
                result = ps.executeQuery();
                if (result.next()) {
                    userName = result.getString("name");
                    country = result.getString("country");
                }
                ps = connection.prepareStatement("select artist, title, version, max_combo, diff, id, set_id from maps where md5 = ?");
                ps.setString(1, md5Data);
                result = ps.executeQuery();
                if (result.next()) {
                    eb.addField(getModeNameFromNumber(i) + " (**" + (int)pp + "pp**)", "**" + userName + "** (:flag_" + country + ":** #" + getCountryRank(userID, i) + "**) | " +
                            "<:ranked:1100846082998669333> **" + "[" + result.getString("title") + " - " + result.getString("artist") + " [" + result.getString("version") + "] " + "](" + getWebsiteLink(i, result.getInt("set_id"), result.getInt("id")) + ")" +
                            "+" + getModsName(mods) + "** [:star2:" + "**" + roundNumber(result.getDouble("diff"), 2) + "**] with **" + roundNumber(acc, 2) + "%**\n" +
                            (getUserRankEmoji(grade) + " ▸ **" + maxCombo + "x** / " + result.getInt("max_combo") + "x [<:hit300k:1100843483549409280>**" + ngeki + "** / " + "<:hit300:1100843418260873286>**" + n300 + "** / " +
                            "<:hit100k:1100843460157779969>**" + nkatu + "** / " + "<:hit100:1100843408530096188>**" + n100 + "** / " + "<:hit50:1100843399675912223>**" + n50 + "** / " + "<:hit0:1100843386996543519>**" + nmiss + "**]") + "\n" +
                            "__[[Download!]](https://web.mamesosu.net/direct/" + result.getInt("set_id") + ")__", false);
                }
            }
        }
        eb.setColor(Color.RED);
        eb.setFooter("Last updated at " + date.format(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))));
        return eb;
    }

    private static String getModeNameFromNumber(int mode) {
        switch (mode) {
            case 0 -> {
                return "<:osu:1100702517119168562> **osu!std**";
            }
            case 1 -> {
                return "<:taiko:1100702510152429588> **osu!taiko**";
            }
            case 2 -> {
                return "<:fruits:1100702512681599089> **osu!ctb**";
            }
            case 3 -> {
                return "<:mania:1100702514501910630> **osu!mania**";
            }
            case 4 -> {
                return "<:osu:1100702517119168562> **osu!rx**";
            }
            case 8 -> {
                return "<:osu:1100702517119168562> **osu!ap**";
            }
            default -> {
                return "Error";
            }
        }
    }
}
