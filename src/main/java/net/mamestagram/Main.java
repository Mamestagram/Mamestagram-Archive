package net.mamestagram;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.mamestagram.command.SlashCommand;
import net.mamestagram.server.PatchAnnounce;
import net.mamestagram.server.RoleDistribution;

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

        jda = JDABuilder.createDefault(TOKEN, GatewayIntent.GUILD_MESSAGES)
                .setRawEventsEnabled(true)
                .addEventListeners(new SlashCommand())
                .addEventListeners(new PatchAnnounce())
                .addEventListeners(new RoleDistribution())
                .setActivity(Activity.playing("mamesosu.net"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                .build();
        jda.updateCommands().queue();
        jda.upsertCommand("help", "Help command for Mamestagram Bot").queue();
        jda.upsertCommand("osuprofile", "View mamesosu.net's Profile").addOption(OptionType.STRING, "mode", "GameMode", true, true).queue();
        jda.upsertCommand("result", "Submit your play at mamesosu.net").addOption(OptionType.STRING, "mode", "GameMode", true, true).queue();
        jda.upsertCommand("server", "Send method of connection to mamesosu.net").queue();
        jda.upsertCommand("ranking", "View mamesosu.net's ranking").addOption(OptionType.STRING, "mode", "GameMode", true, true).queue();

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

        System.out.println("ready");
    }
}