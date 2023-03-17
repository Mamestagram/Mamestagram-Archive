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

        ArrayList<Integer> rID = new ArrayList<>();
        int mapRanked, mode = 0, userID;
        String md5 = "";

        PreparedStatement ps;
        ResultSet result;
        JsonNode root;

        EmbedBuilder eb = new EmbedBuilder();

        BPLAYID = NPLAYID;

        ps = connection.prepareStatement("select id, userid from scores where not grade = 'F' order by score desc limit 1");
        result = ps.executeQuery();

        while(result.next()) {
            NPLAYID = result.getInt("id");
            userID = result.getInt("userid");
        }

        if(BPLAYID != NPLAYID && !isFirstLogin) {
            ps = connection.prepareStatement("select map_md5, mode from scores where id = ?");
            ps.setInt(1, NPLAYID);
            result = ps.executeQuery();

            while (result.next()) {
                md5 = result.getString("map_md5");
                mode = result.getInt("mode");
            }

            root = getMapData(md5);
            mapRanked = root.get(0).get("approved").asInt();

            ps = connection.prepareStatement("select userid  from scores where mode = ? and map_md5 = ? and not grade = 'F' order by score desc");
            ps.setInt(1, mode);
            ps.setString(2, md5);
            result = ps.executeQuery();

            while(result.next()) {
                rID.add(result.getInt("userid"));
            }



        } else {
            isFirstLogin = false;
        }

        return eb;
    }
}
