package net.mamestagram.data;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class EmbedMessageData extends ListenerAdapter {
    public static EmbedBuilder helpCommand() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Command list for Mamestagram Bot", "https://media.discordapp.net/attachments/944984741826932767/1080466807338573824/MS1B_logo.png", "https://media.discordapp.net/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.addField("General", "``/help`` - Displays help for the bot\n" +
                        "``/server`` - Displays a video on how to connect to the server\n",
                false);
        eb.addField("osu!", "``/osuprofile`` ``<mode>`` - Displays account information\n" +
                "``/ranking`` ``<mode>`` - Showing the ranking of mamestagram\n" +
                "``/result`` ``<mode>`` - Submit your play on mamestagram\n",false);
        eb.setColor(Color.PINK);

        return eb;
    }

    public static EmbedBuilder notUserFoundMessage(String dName) { //TODO
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":x:  **Can't found player** ``" + dName + "``**.** \n\n**Please use the same nickname as mamesosu.net**");
        eb.setColor(Color.RED);
        return eb;
    }

    public static EmbedBuilder connectGuideMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Connection Guide**");
        eb.addField("URL", "https://youtu.be/CAfovJYSEvw", false);
        eb.setImage("https://i9.ytimg.com/vi/CAfovJYSEvw/mqdefault.jpg?sqp=CNCkkqAG-oaymwEmCMACELQB8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGH8gMChaMA8=&rs=AOn4CLDKFS4f-tIu-5JBOCqdWr7P8E1AwQ");
        eb.setColor(Color.CYAN);

        return eb;
    }

    public static EmbedBuilder notArgumentMessage() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**:x:  Acquisition error occurred.**");
        eb.setColor(Color.RED);

        return eb;
    }

}
