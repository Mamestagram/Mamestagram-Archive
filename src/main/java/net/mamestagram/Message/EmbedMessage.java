package net.mamestagram.Message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class EmbedMessage extends ListenerAdapter {

    public static EmbedBuilder helpCommandMessage() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(":book: Command list for Mamestagram Bot");
        eb.addField(":pushpin: **General**", "``/help`` - Send help message", false);
        eb.addField(":key: **Private Server**", "``/osuprofile`` ``<mode>`` - Send profile information\n" +
                "``/ranking`` ``<mode>`` - Send ranking information\n" +
                "``/result`` ``<mode>`` - Send the most recent play information\n",false);
        eb.setColor(Color.PINK);

        return eb;
    }

    public static EmbedBuilder notUserFoundMessage(String dName) { //TODO

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(":x:  **Can't found player ``" + dName + "``**");
        eb.setColor(Color.RED);

        return eb;
    }

    public static EmbedBuilder notArgumentMessage() {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**:x:  Acquisition error occurred**");
        eb.setColor(Color.RED);

        return eb;
    }

}
