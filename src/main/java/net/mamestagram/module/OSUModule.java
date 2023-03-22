package net.mamestagram.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import static net.mamestagram.Main.osuAPIKey;

public class OSUModule {

    public static String[] getMinSecond(int number) {

        String[] time = new String[] {"0", "0"};

        if(number > 59) {
            time[0] = (String.valueOf(number / 60));
            if(number % 60 < 10) {
                time[1] = ("0" + (number % 60));
            } else {
                time[1] = (String.valueOf(number % 60));
            }
        } else {
            if(number < 10) {
                time[1] = ("0" + (number));
            } else {
                time[1] = (String.valueOf(number));
            }
        }
        return time;
    }

    public static String getModsName(int n) {

        ArrayList<String> mod = new ArrayList<>();
        final String[] mods = {"NF", "EZ", "TS", "HD", "HR", "SD", "DT", "RX", "HT", "NC", "FL", "", "SO", "AP", "PF"};
        StringBuilder rMods = new StringBuilder();

        if (n >= 536870912) {
            mod.add("V2");
            n -= 536870912;
        }

        for (int i = 14; i >= 0; i--) {
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
            case 4 -> {
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

    public static int getBeatmapRank(ArrayList<Integer> rID, int userID) {

        int countData = 0;

        for(int i = 0; i < rID.size(); i++) {
            for(int j = i; j < rID.size(); j++) {
                if(i != j && rID.get(i) == rID.get(j) && rID.get(j) != 0) {
                    rID.set(j, 0);
                }
            }
        }

        for(int i = 0; i < rID.size(); i++) {
            try{
                if(rID.get(i) == 0) {
                    rID.set(i, rID.get(i + 1));
                    rID.set(i + 1, 0);
                }
            } catch (IndexOutOfBoundsException ex) {
                break;
            }
        }

        for (Integer integer : rID) {
            if (integer == userID) {
                break;
            } else {
                countData++;
            }
        }

        return countData + 1;
    }
}
