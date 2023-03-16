package net.mamestagram.game;

import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.OSUModule.*;

public class MapNotice {

    private static int BPLAYID;
    private static int NPLAYID;
    private static boolean isFirstLogin = true;

    public static EmbedBuilder getMapNotice() throws SQLException, IOException {

        int[] rScore = new int[2147483647], rID = new int[2147483647];
        int mapRanked, mode = 0, count = 0;
        String md5 = "";

        PreparedStatement ps;
        ResultSet result;
        JsonNode root;

        EmbedBuilder eb = new EmbedBuilder();

        BPLAYID = NPLAYID;

        ps = connection.prepareStatement("select id from scores where not grade = 'F' order by score desc limit 1");
        result = ps.executeQuery();

        while(result.next()) {
            NPLAYID = result.getInt("id");
        }

        if(BPLAYID != NPLAYID && isFirstLogin == false) {
            ps = connection.prepareStatement("select map_md5, mode from scores where id = ?");
            ps.setInt(1, NPLAYID);
            result = ps.executeQuery();

            while (result.next()) {
                md5 = result.getString("map_md5");
                mode = result.getInt("mode");
            }

            root = getMapData(md5);
            mapRanked = root.get(0).get("approved").asInt();

            ps = connection.prepareStatement("select score, userid  from scores where mode = ? and map_md5 = ? and not grade = 'F' order by score desc");
            ps.setInt(1, mode);
            ps.setString(2, md5);
            result = ps.executeQuery();

            while(result.next()) {
                rScore[count] = result.getInt("score");
                rID[count] = result.getInt("userid");
                count++;
            }

            //sort algorithm

        } else {
            isFirstLogin = false;
        }
    }
}
