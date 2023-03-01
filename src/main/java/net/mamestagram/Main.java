package net.mamestagram;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.mamestagram.command.SlashCommand;

import java.sql.*;

public class Main {

    public static JDA jda;
    public static Connection connection;

    public static void main(String[] args) {
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

        /*Discord API*/

        jda = JDABuilder.createDefault(TOKEN, GatewayIntent.GUILD_MESSAGES)
                .setRawEventsEnabled(true)
                .addEventListeners(new SlashCommand())
                .setActivity(Activity.playing("mamestagram"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        jda.updateCommands().queue();
        jda.upsertCommand("help", "Mamestagram Botのヘルプコマンドです").queue();
        jda.upsertCommand("profile", "mamesosu.netのプロフィールを表示します").addOption(OptionType.INTEGER, "mode", "モードを指定してください").queue();

        /*Launch Check*/

        System.out.println("ready");
    }

}