package net.mamestagram.Message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

import static net.mamestagram.Main.*;

public class WelcomeMessage extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**" + e.getMember().getUser().getName() + "**さん、Mamestagramへようこそ！以下はサーバーのガイドです。");
        eb.addField("**サーバーのルール**", "ルールは" + e.getGuild().getTextChannelById(1012666778003976243L).getAsMention() + "にて閲覧できます", false);
        eb.addField("**お知らせロール**", "このサーバーは" + e.getGuild().getRoleById(1083976804680863744L).getAsMention() + "にてお知らせを通知します。" + e.getGuild().getTextChannelById(1012691799350988810L).getAsMention() + "にて入手できます。", false);
        eb.addField("**プライベートサーバー**", "**Mamestagram**が運営するプライベートサーバーの情報は" + e.getGuild().getTextChannelById(1073989576462438420L).getAsMention() + "や" + e.getGuild().getTextChannelById(1087987989600280686L).getAsMention() + "、" + e.getGuild().getTextChannelById(1104675564763222016L).getAsMention()+ "、" +
                e.getGuild().getTextChannelById(1081737936401350717L).getAsMention() + "にて閲覧できます。サーバー内で一位を目指しましょう。" +
                "アップデートは" + e.getGuild().getTextChannelById(1084068468036472852L).getAsMention() + "に通知します。", false);
        eb.addField("**チャット等**", "チャットはルールさえ守っていれば何を話していただいても結構です！" + e.getGuild().getTextChannelById(1012666685989339156L).getAsMention()+ "や" + e.getGuild().getTextChannelById(1012876250399907850L).getAsMention() + "、" + e.getGuild().getTextChannelById(1012963169083334686L).getAsMention() + "で仲間達と楽しくチャットをしましょう！", false);

        eb.setColor(Color.green);

        jda.getGuildById(944248031136587796L).getTextChannelById(1012964779838685205L).sendMessageEmbeds(eb.build()).queue();
    }
}
