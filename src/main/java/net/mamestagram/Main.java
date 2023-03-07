package net.mamestagram;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.mamestagram.command.SlashCommand;

import static net.mamestagram.game.LoginAlert.*;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static JDA jda;
    public static Connection connection;
    public static final String osuAPIKey = "";

    public static void main(String[] args) throws SQLClientInfoException {
        final String TOKEN = "";

        /*Database Connect*/

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost/private?useSSL=false",
                    "",
                    ""
            );
            System.out.println("Connection is Successful!");
        } catch (SQLException e) {
            System.out.println("Connection is failed! Error log is below.");
            e.printStackTrace();
        }

        connection.setClientInfo("mysql-connection-timeout", "300");

        /*Discord API*/

        jda = JDABuilder.createDefault(TOKEN, GatewayIntent.GUILD_MESSAGES)
                .setRawEventsEnabled(true)
                .addEventListeners(new SlashCommand())
                .setActivity(Activity.playing("mamesosu.net"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        jda.updateCommands().queue();
        jda.upsertCommand("help", "Mamestagram Botのヘルプコマンドです").queue();
        jda.upsertCommand("osuprofile", "mamesosu.netのプロフィールを表示します").addOption(OptionType.STRING, "mode", "取得したいモード", true, true).queue();
        jda.upsertCommand("result", "mamesosu.netでの直近プレイを送信します").addOption(OptionType.STRING, "mode", "取得したいモード", true, true).queue();
        jda.upsertCommand("server", "mamesosu.netへの接続方法を送信します").queue();
        jda.upsertCommand("ranking", "mamesosu.netのランキングを表示します").addOption(OptionType.STRING, "mode", "取得したいモード", true, true).queue();

        /*Scheduler*/

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    loginStatusUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        timer.schedule(task,1000L,2000L);

        /*Launch Check*/

        System.out.println("ready");
    }

}