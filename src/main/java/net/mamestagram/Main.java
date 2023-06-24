package net.mamestagram;

import net.dv8tion.jda.api.JDA;

import static net.mamestagram.Server.LoginActivity.*;
import static net.mamestagram.System.JDABuilder.*;

import static net.mamestagram.Game.ConvertScore.*;
import static net.mamestagram.Game.LoginStatus.*;
import static net.mamestagram.DataBase.SQLConnector.*;
import static net.mamestagram.DataBase.StatusAdjuster.*;
import static net.mamestagram.Game.RecentPlay.*;
import static net.mamestagram.System.CacheRemover.*;
import static net.mamestagram.DataBase.WebDBUpdater.*;
import static net.mamestagram.Game.StatusBoard.*;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static JDA jda;
    public static Connection connection;
    public static boolean isRestarting = false;
    public static final String osuAPIKey = "";
    public static final String TOKEN = "";
    public static final String deeplAPIKEY = "";

    public static void main(String[] args) throws SQLClientInfoException {

        try {
            connection = connectToServer();
            System.out.println("Connection is Successful!");
        } catch (SQLException e) {
            System.out.println("Connection is failed! Error log is below.\n\n");
            e.printStackTrace();
        }

        connection.setClientInfo("mysql-connection-timeout", "300");

        jda = createJDA(TOKEN);
        Timer systemTimer = new Timer();
        Timer messageUpdateTimer = new Timer();

        TimerTask systemTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    getLoginStatus();
                    deleteConvertScore();
                    setUpdateDatabase();
                    checkMapStatus();
                    autoSendPlayerScoreBoard();
                    updateLoginStatus();
                    executeRestart();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        systemTimer.schedule(systemTask,1000L,2000L);

        TimerTask messageUpdateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    updatePPRecordMessage();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        messageUpdateTimer.schedule(messageUpdateTask, 2000L, 20000L);

        System.out.println("ready");
    }
}