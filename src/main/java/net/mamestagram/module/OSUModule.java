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
import java.util.Collections;

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

        boolean isApproved = approved == 1;

        if(isApproved) {
            return "Yes";
        } else {
            return "No";
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

    public static int getUserRank(int userid, int mode) throws SQLException {

        PreparedStatement ps;
        ResultSet result;
        ArrayList<Integer> mapID = new ArrayList<>();
        ArrayList<String> mapMD5 = new ArrayList<>();
        int mapCount = 0, count = 0;
        int rankCount = 0;

        ps = connection.prepareStatement("select id, map_md5 from scores where userid = " + userid + " and not grade = 'F' and mode = " + mode);
        result = ps.executeQuery();

        while(result.next()) {
            mapID.add(result.getInt("id"));
            mapMD5.add(result.getString("map_md5"));
        }

        ps = connection.prepareStatement("select COUNT(*) from scores");
        result = ps.executeQuery();

        if (result.next()) {
            mapCount = result.getInt("COUNT(*)");
        }

        for(int i = 1; i <= mapCount; i++) {

            ArrayList<Integer> id = new ArrayList<>();

            try {
                if (i == mapID.get(count)) {
                    ps = connection.prepareStatement("select userid from scores where map_md5 = ? and not grade = 'F' and mode = ? order by score desc");
                    ps.setString(1, mapMD5.get(count));
                    ps.setInt(2, mode);
                    result = ps.executeQuery();

                    while (result.next()) {
                        id.add(result.getInt("userid"));
                    }

                    if (getBeatmapRank(id, userid) == 1) {
                        rankCount++;
                    }
                    count++;
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        System.out.println("[Profile Log] GetUser - OK");
        return rankCount;
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

        System.out.println("[Profile Log] GetPP - OK");

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
        JsonNode root;
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

        System.out.println("[Profile Log] GetAverage - OK");

        if(dataCount != 0) {
            return totalStarRate / (dataCount);
        } else {
            return 0.0;
        }
    }
}
