package net.mamestagram.message;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static net.mamestagram.Main.*;
import static net.mamestagram.module.TranslateModule.*;

public class TranslateWebhook extends ListenerAdapter {

    //TODO 画像 / 動画のdetectと不適切ワード指摘APIの導入、コード成型、idとurlを別のクラスに分けてまとめる
    //TODO ボタン関連追加できそうなら追加

    String messageRaw;
    Message message;
    long messageID;
    static boolean isProcess = false;


    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getMember() != null) {
            if (!e.getMember().getUser().isBot() && e.getChannel().getIdLong() == 1012678670252523530L && !isProcess) {
                e.getMessage().addReaction(Emoji.fromUnicode("U+1F310")).queue();
                messageRaw = e.getMessage().getContentRaw();
                message = e.getMessage();
            } else if(e.getChannel().getIdLong() == 1012678670252523530L) {
                messageID = e.getMessageIdLong();
                startTimer();
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {

        if(e.getEmoji().equals(Emoji.fromUnicode("U+1F310")) && !isProcess) {
            try {
                message.reply("JP: " + getTranslateSentence(messageRaw, "JA") + "\n" +
                        "EN: " + getTranslateSentence(messageRaw, "EN")).queue();
                jda.getGuildById(e.getGuild().getIdLong()).getTextChannelById(e.getChannel().getIdLong()).deleteMessageById(messageID).queueAfter(30, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.getReaction().clearReactions().queue();
        } else {
            e.getReaction().clearReactions().queue();
        }
    }

    private static void startTimer() {

        long startTime, elapsedTime;

        startTime = System.currentTimeMillis();
        elapsedTime = 0;
        while (elapsedTime < 30000) {
            elapsedTime = System.currentTimeMillis() - startTime;
            isProcess = true;
        }
        isProcess = false;
    }
}
