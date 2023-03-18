package net.mamestagram.game;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import static net.mamestagram.Main.*;

public class LoginStatus {

    private static int LOGINID = 0;
    private static int BLOGINID = 0;
    private static int USERID = 0;
    private static String USERNAME;
    private static boolean isFirstLogin = true;

    public static void getLoginStatus() throws SQLException {

        PreparedStatement ps;
        ResultSet result;

        EmbedBuilder eb = new EmbedBuilder();

        BLOGINID = LOGINID;

        ps = connection.prepareStatement("select id from ingame_logins order by id desc limit 1");
        result = ps.executeQuery();

        while(result.next()) {
            LOGINID = (result.getInt("id")); //bloginID= 86, loginID = 87
        }

        if(BLOGINID != LOGINID && isFirstLogin == false) {

            var date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

            ps = connection.prepareStatement("select userid from ingame_logins where id = ?");
            ps.setInt(1 ,LOGINID);
            result = ps.executeQuery();

            while (result.next()) {
                USERID = result.getInt("userid");
            }

            ps = connection.prepareStatement("select name from users where id = ?");
            ps.setInt(1, USERID);
            result = ps.executeQuery();

            while(result.next()) {
                USERNAME = result.getString("name");
            }

            eb.setAuthor(USERNAME + " has logged in", "https://web.mamesosu.net/profile/id=" + USERID + "/mode=std/special=none", "https://osu.ppy.sh/images/layout/avatar-guest.png");
            eb.setColor(Color.GREEN);

            jda.getGuildById(944248031136587796L).getTextChannelById(1081737936401350717L).sendMessageEmbeds(eb.build()).queue();
        } else {
            isFirstLogin = false;
        }

    }
}
