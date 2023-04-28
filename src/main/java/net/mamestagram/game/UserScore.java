package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.mamestagram.message.EmbedMessageData.*;
import static net.mamestagram.module.CommandModule.*;
import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;

//実装後以降予定 (Commandのほうは削除する)

public class UserScore extends ListenerAdapter {

    private final String[] modes = new String[] {"Standard", "Taiko", "Mania", "Relax", "AutoPilot"};
    private static PreparedStatement ps;
    private static ResultSet result;
    private static EmbedBuilder eb;
    static int userID, bUserID, nUserID;
    static boolean isFirstBoot = true;
    static String userName;

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {

        if(e.getName().equals("result") && e.getFocusedOption().getName().equals("mode")) {
            createAutoCompleteInteraction(e, modes);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if(e.getName().equals("result")) {
            try {
                e.replyEmbeds(getUserScoreBoard(e.getMember(), getModeNumber(e.getOption("mode").getAsString()), true).build()).queue();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void autoSendPlayerScoreBoard() throws SQLException {

        final long guildID = 944248031136587796L;
        final long channelID = 1081737936401350717L;

        if(getUserScoreBoard(null, 0, false) != null) {
            jda.getGuildById(guildID).getTextChannelById(channelID).sendMessageEmbeds(getUserScoreBoard(null, 0, false).build()).queue();
        }
    }

    private static EmbedBuilder getUserScoreBoard(Member user, int mode, boolean commandMode) throws SQLException{

        String searchQuery = "select id, name from users where name = ?";

        eb = new EmbedBuilder();

       if(commandMode) {
           ps = connection.prepareStatement(searchQuery);
           ps.setString(1, user.getNickname());
           result = ps.executeQuery();
           if (!result.next()) {
               ps = connection.prepareStatement(searchQuery);
               ps.setString(1, user.getUser().getName());
               result = ps.executeQuery();
               if (!result.next()) {
                   return notUserFoundMessage(user.getUser().getName());
               } else {
                   userID = result.getInt("id");
                   userName = result.getString("name");
               }
           } else {
               userID = result.getInt("id");
               userName = result.getString("name");
           }
       } else {
           ps = connection.prepareStatement("select COUNT(id) from scores where not grade = 'F'");
           result = ps.executeQuery();
           if(isFirstBoot) {
               if(result.next()) {
                   bUserID = result.getInt("COUNT(id)");
                   nUserID = result.getInt("COUNT(id)");
               }
               isFirstBoot = false;
               return null;
           } else {
               bUserID = nUserID;

               if(result.next()) nUserID = result.getInt("COUNT(id)");

               if(bUserID != nUserID) {
                   ps = connection.prepareStatement("select userid, mode from scores where id = ?");
                   ps.setInt(1, nUserID);
                   result = ps.executeQuery();
                   if(result.next()) {
                       mode = result.getInt("mode");
                       userID = result.getInt("userid");
                       ps = connection.prepareStatement("select name from users where id = ?");
                       ps.setInt(1, userID);
                       result = ps.executeQuery();
                       if(result.next()) {
                           userName = result.getString("name");
                       }
                   }
               } else {
                   return null;
               }
           }
       }

        eb.setAuthor(userName + "( " + getMapDataDoubleFromID(userID, mode).get(6) + " :star2: ) " + getMapDataStringFromID(userID, mode).get(1) + " - " +
                getMapDataStringFromID(userID, mode).get(0) + " [" + getMapDataStringFromID(userID, mode).get(2) + "] +" + getModsName(getUserDataIntergerFromID(userID, mode).get(2)) + " " + getUserDataDoubleFromID(userID, mode).get(1) + "% | " + getUserDataIntergerFromID(userID, mode).get(1)
                + "x | " + getUserDataDoubleFromID(userID, mode).get(0) + "pp", getWebsiteLink(mode, getMapDataIntergerFromID(userID, mode).get(1), getMapDataIntergerFromID(userID, mode).get(0)), "https://a.mamesosu.net/" + userID);
        eb.addField( getModeEmoji(getMapDataIntergerFromID(userID, mode).get(4))+ " **Map Info**", "Beatmap: **" + getMapDataStringFromID(userID, mode).get(1) + " - " + getMapDataStringFromID(userID, mode).get(0) + " [" + getMapDataStringFromID(userID, mode).get(2) + "]**\n" +
                "Rating: :star2: **" + roundNumber(getMapDataDoubleFromID(userID, mode).get(6), 2) + "**\n" +
                "AR: **" + getMapDataDoubleFromID(userID, mode).get(2) + "** / CS: **" + getMapDataDoubleFromID(userID, mode).get(1) + "** / OD: **" + getMapDataDoubleFromID(userID, mode).get(3) + "** / BPM: **" + getMapDataDoubleFromID(userID, mode).get(0) + "**\n" +
                "MapStatus: " + getMapStatusEmoji(getMapDataIntergerFromID(userID, mode).get(2)), false);
        eb.addField(getModeEmoji(getMapDataIntergerFromID(userID, mode).get(4)) + " **User Scores**", "Grade: " + getUserRankEmoji(getUserDataStringFromID(userID, mode).get(0)) + " ▸ **" + getCompareScoreData(userID, mode).get(0) + "%**\n" +
                "Score: **" + String.format("%,d", getUserDataIntergerFromID(userID, mode).get(0)) + "** ▸ **" + getCompareScoreData(userID, mode).get(2) + "%**\n" +
                "Accuracy: **" + getUserDataDoubleFromID(userID, mode).get(1) + "%** ▸ **" + getCompareScoreData(userID, mode).get(0) + "%**\n" +
                "Combo: **" + getUserDataIntergerFromID(userID, mode).get(1) + "x** ▸ **" + getCompareScoreData(userID, mode).get(3) + "%**\n" +
                "**[ <:hit300k:1100843483549409280> " + getUserDataIntergerFromID(userID, mode).get(3) + " / <:hit300:1100843418260873286> " + getUserDataIntergerFromID(userID, mode).get(4) + " / <:hit100k:1100843460157779969> " + getUserDataIntergerFromID(userID, mode).get(5) + " / <:hit100:1100843408530096188> " + getUserDataIntergerFromID(userID, mode).get(6) + " / <:hit50:1100843399675912223> " + getUserDataIntergerFromID(userID, mode).get(7) + " / <:hit0:1100843386996543519> " + getUserDataIntergerFromID(userID, mode).get(8) + " ]**\n" +
                "Mods: **" + getModsName(getUserDataIntergerFromID(userID, mode).get(2)) + "**", false);
        return eb;
    }

    private static String getMD5FromID(int userID, int mode) throws SQLException{
        ps = connection.prepareStatement("select map_md5 from scores where userid = ? and mode = ? order by id desc limit 1");
        ps.setInt(1, userID);
        ps.setInt(2, mode);
        result = ps.executeQuery();

        if(result.next()) {
            return result.getString("map_md5");
        } else {
            return null;
        }
    }

    //Artist = 0, Title = 1, Version = 2, Creator = 3
    private static List<String> getMapDataStringFromID(int id, int mode) throws SQLException{

        List<String> userData = new ArrayList<>(Arrays.asList(null, null, null, null));
        String mapMD5;

        if(getMD5FromID(id, mode) != null) {
            mapMD5 = getMD5FromID(id, mode);

            ps = connection.prepareStatement("select artist, title, version, creator from maps where md5 = ?");
            ps.setString(1, mapMD5);
            result = ps.executeQuery();

            if(result.next()) {
                userData.add(0, result.getString("artist"));
                userData.add(1, result.getString("title"));
                userData.add(2, result.getString("version"));
                userData.add(3, result.getString("creator"));
            }
        }

        return userData;
    }

    //0 = id, 1 = set_id, 2 = status, 3 = max_combo, 4 = mode, 5 = mods
    private static List<Integer> getMapDataIntergerFromID(int id, int mode) throws SQLException{

        List<Integer> userData = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));
        String mapMD5;

        if(getMD5FromID(id, mode) != null) {
            mapMD5 = getMD5FromID(id, mode);

            ps = connection.prepareStatement("select id, set_id, status, max_combo, mode from maps where md5 = ?");
            ps.setString(1, mapMD5);
            result = ps.executeQuery();
            if(result.next()) {
                userData.add(0, result.getInt("id"));
                userData.add(1, result.getInt("set_id"));
                userData.add(2, result.getInt("status"));
                userData.add(3, result.getInt("max_combo"));
                userData.add(4, result.getInt("mode"));
            }
        }

        return userData;
    }

    //0 = passedRate, 1 = bpm, 2 = cs, 3 = ar, 4 = od, 5 = hp, 6 = diff

    private static List<Double> getMapDataDoubleFromID(int id, int mode) throws SQLException {

        List<Double> userData = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        String mapMD5;

        if(getMD5FromID(id, mode) != null) {
            mapMD5 = getMD5FromID(id, mode);

            ps = connection.prepareStatement("select plays, passes, bpm, cs, ar, od, hp, diff from maps where md5 = ?");
            ps.setString(1, mapMD5);
            result = ps.executeQuery();
            if(result.next()) {
                userData.add(0, roundNumber((double)result.getInt("passes") / (double) result.getInt("plays"), 2));
                userData.add(1, result.getDouble("bpm"));
                userData.add(2, result.getDouble("cs"));
                userData.add(3, result.getDouble("ar"));
                userData.add(4, result.getDouble("od"));
                userData.add(5, result.getDouble("hp"));
                userData.add(6, result.getDouble("diff"));
            }
        }

        return userData;
    }

    //0 = score, 1 = max_combo, 2 = mods, 3 = ngeki, 4 = n300, 5 = nkatu, 6 = n100
    //7 = n50, 8 = nmiss, 9 = status

    private static List<Integer> getUserDataIntergerFromID(int id, int mode) throws SQLException {

        List<Integer> userData = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        ps = connection.prepareStatement("select score, max_combo, mods, ngeki, n300, nkatu, n100, n50, nmiss, status from scores where mode = ? and userid= ? order by id desc limit 1");
        ps.setInt(1, mode);
        ps.setInt(2, id);
        result = ps.executeQuery();
        if(result.next()) {
            userData.add(result.getInt("score"));
            userData.add(result.getInt("max_combo"));
            userData.add(result.getInt("mods"));
            userData.add(result.getInt("ngeki"));
            userData.add(result.getInt("n300"));
            userData.add(result.getInt("nkatu"));
            userData.add(result.getInt("n100"));
            userData.add(result.getInt("n50"));
            userData.add(result.getInt("nmiss"));
            userData.add(result.getInt("status"));
        }

        return userData;
    }

    //0 = pp, 1 = acc

    private static List<Double> getUserDataDoubleFromID(int id, int mode) throws SQLException {

        List<Double> userData = new ArrayList<>(Arrays.asList(0.0, 0.0));

        ps = connection.prepareStatement("select pp, acc from scores where mode = ? and userid = ? order by id desc limit 1");
        ps.setInt(1, mode);
        ps.setInt(2, id);
        result = ps.executeQuery();
        if(result.next()) {
            userData.add(result.getDouble("pp"));
            userData.add(result.getDouble("acc"));
        }

        return userData;
    }

    //0 = grade, 1 = play_time

    private static List<String> getUserDataStringFromID(int id, int mode) throws SQLException {

        List<String> userData = new ArrayList<>(Arrays.asList(null, null));

        ps = connection.prepareStatement("select grade, play_time from scores where mode = ? and userid = ? order by id desc limit 1");
        ps.setInt(1, mode);
        ps.setInt(2, id);
        result = ps.executeQuery();
        if(result.next()) {
            userData.add(result.getString("grade"));
            userData.add(result.getString("play_time"));
        }

        return userData;
    }

    //0 = pp, 1 = acc, 2 = score, 3 = max_combo

    private static List<Double> getCompareScoreData(int id, int mode) throws SQLException {

        List<Double> compareData = new ArrayList<>(Arrays.asList(100.0, 100.0, 100.0, 100.0));
        List<Double> tempPP = new ArrayList<>(), tempACC = new ArrayList<>();
        List<Integer> tempScore = new ArrayList<>(), tempCombo = new ArrayList<>();

        ps = connection.prepareStatement("select COUNT(id) from scores where userid = ? and mode = ? and not grade = 'F'");
        ps.setInt(1, id);
        ps.setInt(2, mode);
        if(ps.getResultSet().getInt("COUNT(id)") > 1) {

            ps = connection.prepareStatement("select pp, acc, score, max_combo from scores where userid = ? and mode = ? and not grade = 'F' order by id desc limit 2");
            ps.setInt(1, id);
            ps.setInt(2, mode);
            result = ps.executeQuery();
            while (result.next()) {
                tempACC.add(result.getDouble("acc"));
                tempPP.add(result.getDouble("pp"));
                tempScore.add(result.getInt("score"));
                tempCombo.add(result.getInt("max_combo"));
            }
            compareData.add(0, roundNumber((tempPP.get(0) / tempPP.get(1)) * 100, 2));
            compareData.add(1, roundNumber((tempACC.get(0) / tempACC.get(1)) * 100, 2));
            compareData.add(2, roundNumber(((double)tempScore.get(0) / (double)tempScore.get(1)) * 100, 2));
            compareData.add(3, roundNumber(((double)tempCombo.get(0) / (double)tempScore.get(1)) * 100, 2));
        }
        return compareData;
    }
}
