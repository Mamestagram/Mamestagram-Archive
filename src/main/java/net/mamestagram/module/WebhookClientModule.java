package net.mamestagram.module;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.WebhookCluster;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static net.mamestagram.Main.TOKEN;
import static net.mamestagram.module.TranslateModule.*;

public class WebhookClientModule {

    public static WebhookClient createWebhookClient(String url, long id) {

        //create new webhook thread
        WebhookClientBuilder builder = new WebhookClientBuilder(url);
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("mamesosu");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        WebhookClient client = builder.build();

        //create webhook cluster
        WebhookCluster cluster = new WebhookCluster(5);
        cluster.setDefaultHttpClient(new OkHttpClient());
        cluster.setDefaultDaemon(true);

        cluster.buildWebhook(id, TOKEN);
        cluster.addWebhooks(client);

        return client;
    }

    public static void sendWebhookMessage(@NotNull User user, WebhookClient client , String text) throws IOException, InterruptedException {

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(user.getName());
        builder.setAvatarUrl(user.getAvatarUrl());
        builder.setContent(text);
        client.send(builder.build());
        client.close();
    }
}
