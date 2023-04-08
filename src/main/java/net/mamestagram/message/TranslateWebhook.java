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

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getMember() != null) {
            if(!e.getMember().getUser().isBot() && e.getChannel().getIdLong() == 1012666685989339156L && e.getChannel().getIdLong() == 1012876250399907850L && e.getChannel().getIdLong() == 1012963169083334686L) {

            }
        }
    }

}
