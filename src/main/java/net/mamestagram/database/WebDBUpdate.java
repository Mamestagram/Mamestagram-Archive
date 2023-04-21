package net.mamestagram.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.mamestagram.Main.*;

public class WebDBUpdate {

    private static int UPDATE_USER_ID;
    private static int BEFORE_USER_ID;
    private static PreparedStatement ps;
    private static ResultSet result;
    private static boolean isFirstCheck = true;

    public static void setUpdateDatabase() throws SQLException {

        ps = connection.prepareStatement("select id from users order by id desc limit 1");

        if(isFirstCheck) {
            result = ps.executeQuery();
            if(result.next()) {
                UPDATE_USER_ID = result.getInt("id");
                BEFORE_USER_ID = result.getInt("id");
            }
            isFirstCheck = false;

            return;
        }

        BEFORE_USER_ID = UPDATE_USER_ID;

        result = ps.executeQuery();
        if(result.next()) {
            UPDATE_USER_ID = result.getInt("id");
        }

        if(BEFORE_USER_ID != UPDATE_USER_ID) {
            int dataAmount = 0;
            ps = connection.prepareStatement("select count(id) as count from users");
            result = ps.executeQuery();
            if(result.next()) {
                dataAmount = result.getInt("count");
            }
            ps = connection.prepareStatement("INSERT INTO `private`.`web_userdata` (id, userid, `banner_url`, `description`) VALUES (?, ?, 'https://assets.ppy.sh/beatmaps/1905994/covers/cover.jpg?', 'sample')");
            ps.setInt(1, dataAmount);
            ps.setInt(2, UPDATE_USER_ID);
            ps.executeUpdate();
        }
    }
}
