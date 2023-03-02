package net.mamestagram.data;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class EmbedMessageData extends ListenerAdapter {
    public static EmbedBuilder helpCommand() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Command list for Mamestagram Bot", "https://media.discordapp.net/attachments/944984741826932767/1080466807338573824/MS1B_logo.png", "https://media.discordapp.net/attachments/944984741826932767/1080466807338573824/MS1B_logo.png");
        eb.addField("General", "``/help`` - Botのヘルプを表示します\n" +
                        "``/invite`` - Mamestagramの招待リンクを表示します\n" +
                        "``/setting`` - ボットの設定のヘルプを表示します\n",
                false);
        eb.addField("osu!", "``/profile`` ``<mode>`` - アカウント情報を表示します\n" +
                "``/ranking`` ``<mode>`` - mamestagramでのランクを表示します\n" +
                "``/recent`` ``<mode>`` - mamestagramでのプレイを送信します\n",false);
        eb.setColor(Color.PINK);

        return eb;
    }

    public static EmbedBuilder notUserFoundMessage(String dName) { //TODO
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**実行エラー**");
        eb.setTitle("``" + dName + "`` というプレイヤーは見つかりませんでした!");
        eb.setColor(Color.RED);
        return eb;
    }
}
