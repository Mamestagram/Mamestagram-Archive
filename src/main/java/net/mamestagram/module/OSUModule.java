package net.mamestagram.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.mamestagram.Main.connection;
import static net.mamestagram.Main.osuAPIKey;

public class OSUModule {

    public static String getModsName(int n) {

        ArrayList<String> mod = new ArrayList<>();
        final String[] mods = {"NF", "EZ", "TS", "HD", "HR", "SD", "DT", "RX", "HT", "NC", "FL", "", "SO", "AP", "PF", "4K", "5K", "6K", "7K", "8K", "FD", "RD", "CM", "TG", "9K", "KC", "1K", "3K", "2K", "V2", "MR"};
        StringBuilder rMods = new StringBuilder();

        for (int i = 30; i >= 0; i--) {
            if (i != 2 && i != 11 && n >= Math.pow(2, i)) {
                switch (i) {
                    case 14 -> n -= Math.pow(2, 5);
                    case 9 -> n -= Math.pow(2, 6);
                }
                mod.add(mods[i]);
                n -= Math.pow(2, i);
            }
        }

        for (String s : mod) {
            rMods.append(s);
        }

        if(!rMods.toString().equals("")) {
            return rMods.toString();
        } else {
            return "NM";
        }
    }

    public static String getModeName(int mode) {

        switch (mode) {
            case 0 -> {
                return  "Standard";
            }
            case 1 -> {
                return  "Taiko";
            }
            case 2 -> {
                return  "Catch";
            }
            case 3 -> {
                return  "Mania";
            }
            case 4, 5, 6 -> {
                return  "Relax";
            }
            case 8 -> {
                return "AutoPilot";
            }
            default -> {
                return null;
            }
        }
    }

    public static int getModeNumber(String mode) {
        switch (mode) {
            case "Standard" -> {
                return  0;
            }
            case "Taiko" -> {
                return 1;
            }
            case "Catch" -> {
                return 2;
            }
            case "Mania" -> {
                return 3;
            }
            case "Relax" -> {
                return 4;
            }
            case "AutoPilot" -> {
                return 8;
            }
            default -> {
                return 6; //not happen
            }
        }
    }

    public static String isRanked(int approved) {

        switch (approved) {
            case 0 -> {
                return ":skull: Graveyard";
            }
            case 2 -> {
                return ":white_check_mark: Ranked";
            }
            case 5 -> {
                return ":heart: Loved";
            }
            default -> {
                return ":x: Unknown";
            }
        }
    }

    public static JsonNode getMapData(String md5) throws IOException {

        String inputLine;
        String jsonURL = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPIKey + "&h=" + md5;
        URL obj = new URL(jsonURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return mapper.readTree(response.toString());
    }

    public static Color getMessageColor(String grade) {

        switch (grade) {
            case "XH", "SH" -> {
                return Color.WHITE;
            }
            case "X", "S" -> {
                return Color.YELLOW;
            }
            case "A" -> {
                return Color.GREEN;
            }
            case "B" -> {
                return Color.CYAN;
            }
            case "C" -> {
                return Color.PINK;
            }
            default -> {
                return Color.RED;
            }
        }
    }

    public static String getWebsiteLink(int mode, int mapsetID, int mapID) {

        String browserLink = "https://osu.ppy.sh/beatmapsets/" + mapsetID + "#";

        switch (mode) {
            case 0, 4, 8 -> {
                browserLink += "osu/";
            }
            case 1, 5 -> {
                browserLink += "taiko/";
            }
            case 2, 6 -> {
                browserLink += "fruits/";
            }
            case 3 -> {
                browserLink += "mania/";
            }
        }

        return browserLink += mapID;
    }

    public static int getBeatmapRank(ArrayList<Integer> rID, int userID) {


        int countData = 0;

        for (int i = 0; i < rID.size(); i++) {
            for (int j = i + 1; j < rID.size(); j++) {
                if (rID.get(i).equals(rID.get(j)) && !rID.get(j).equals(0)) {
                    rID.set(j, 0);
                }
            }
        }

        for (int i = 0; i < rID.size() - 1; i++) {
            if (rID.get(i).equals(0)) {
                for (int j = i + 1; j < rID.size(); j++) {

                    if (!rID.get(j).equals(0)) {
                        Collections.swap(rID, i, j);
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < rID.size(); i++) {
            if (rID.get(i).equals(userID)) {
                countData = i;
                break;
            }
        }

        return countData + 1;
    }

    public static double roundNumber(double num, int n) {
        return Math.round(num * Math.pow(10, n)) / Math.pow(10, n);
    }

    public static double getAveragePP(int userID, int mode) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        ArrayList<Double> TotalPP = new ArrayList<>();
        double resultPP = 0.0;

        ps = connection.prepareStatement("select pp from scores where userid = ? and mode = ? and not grade = 'F' order by score desc");
        ps.setInt(1, userID);
        ps.setInt(2, mode);
        result = ps.executeQuery();

        while(result.next()) {
            TotalPP.add(result.getDouble("pp"));
        }

        for(double score : TotalPP) {
            resultPP += score;
        }
        
        if(TotalPP.size() != 0) {
            return resultPP / (TotalPP.size());
        } else {
            return 0;
        }
    }

    public static double getAverageStarRate(int userID, int mode) throws SQLException {

        int dataCount = 0;
        double totalStarRate = 0.0;
        PreparedStatement ps;
        ResultSet result;
        ArrayList<String> mapMD5 = new ArrayList<>();

        ps = connection.prepareStatement("select id, map_md5 from scores where userid = ? and mode = " + mode + " and not grade = 'F'");
        ps.setInt(1, userID);
        result = ps.executeQuery();

        while(result.next()) {
            mapMD5.add(result.getString("map_md5"));
            dataCount++;
        }

        switch (mode) {
            case 4 -> mode = 0;
            case 8 -> mode = 0;
        }

        for(int i = 0; i < dataCount; i++) {
            ps = connection.prepareStatement("select diff from maps where md5 = ? and mode = ?");
            ps.setString(1, mapMD5.get(i));
            ps.setInt(2, mode);
            result = ps.executeQuery();

            if(result.next()) {
                if(result.getDouble("diff") <= 13.0) {
                    totalStarRate += result.getDouble("diff");
                }
            }
        }

        if(dataCount != 0) {
            return totalStarRate / (dataCount);
        } else {
            return 0.0;
        }
    }

    public static List<String> getUserRankEmoji(int playMode, int userID) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        String query = ("select grade from scores where userid = ? and mode = " + playMode + " order by id desc limit 1");

        ps = connection.prepareStatement(query);

        ps.setInt(1, userID);
        result = ps.executeQuery();

        if(result.next()) {
            List<String> data = new ArrayList<>(Arrays.asList(null, null));

            switch (result.getString("grade")) {

                case "C" -> {
                    data.add(0, "C");
                    data.add(1, "<:rankC:1100664705997082755>");
                    return data;
                }
                case "B" -> {
                    data.add(0, "B");
                    data.add(1, "<:rankB:1100664703224664125>");
                    return data;
                }
                case "A" -> {
                    data.add(0, "A");
                    data.add(1, "<:rankA:1100664700905205900>");
                    return data;
                }
                case "S" -> {
                    data.add(0, "S");
                    data.add(1, "<:rankS:1100664689588969523>");
                    return data;
                }
                case "SH" -> {
                    data.add(0, "SH");
                    data.add(1, "<:rankSH:1100664691270893678>");
                    return data;
                }
                case "X" -> {
                    data.add(0, "X");
                    data.add(1, "<:rankX:1100664694982836346>");
                    return data;
                }
                case "XH" -> {
                    data.add(0, "XH");
                    data.add(1, "<:rankXH:1100664698237624430>");
                    return data;
                }
                default -> {
                    data.add(0, "D");
                    data.add(1, "<:rankD:1100664707892920340>");
                    return data;
                }
            }
        } else {
            System.out.println("[Error] Can't get Userdata string type data! Please fix this method!");
            return null;
        }
    }
}
