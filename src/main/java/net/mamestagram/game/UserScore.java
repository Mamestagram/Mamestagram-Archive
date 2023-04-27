package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
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

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {

        if(e.getName().equals("result") && e.getFocusedOption().getName().equals("mode")) {
            createAutoCompleteInteraction(e, modes);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if(e.getName().equals("result")) {

        }
    }

    private static EmbedBuilder getUserScoreBoard(Member user, int mode) throws SQLException, IOException {

        int userID;
        String userName;
        String searchQuery = "select id, name from users where name = ?";

        eb = new EmbedBuilder();

        ps = connection.prepareStatement(searchQuery);
        ps.setString(1, user.getNickname());
        result = ps.executeQuery();
        if(!result.next()) {
            ps = connection.prepareStatement(searchQuery);
            ps.setString(1, user.getUser().getName());
            result = ps.executeQuery();
            if(!result.next()) {
                return notUserFoundMessage(user.getUser().getName());
            } else {
                userID = result.getInt("id");
                userName = result.getString("name");
            }
        } else {
            userID = result.getInt("id");
            userName = result.getString("name");
        }

        eb.setAuthor(userName + "( " + getMapDataDoubleFromID(userID, mode).get(6) + " :star2: ) " + getMapDataStringFromID(userID, mode).get(1) + " - " +
                getMapDataStringFromID(userID, mode).get(0) + " [" + getMapDataStringFromID(userID, mode).get(2) + "] +" + getModsName(getUserDataStringFromID(userID, mode).get(2)) + " ", "https://web.mamesosu.net/profile/id=" + userID + "/mode=std/special=none/bestpp=1&mostplays=1&recentplays=1", "https://a.mamesosu.net/" + userID);


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

    //0 =
    private static ArrayList<Integer> getUserDataStringFromID(int id, int mode) throws SQLException {

        ArrayList<Integer> userData = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

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
}
