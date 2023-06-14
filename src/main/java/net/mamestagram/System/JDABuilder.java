package net.mamestagram.System;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.mamestagram.Command.SlashCommand;
import net.mamestagram.Game.MapStatus;
import net.mamestagram.Game.ReportScore;
import net.mamestagram.Game.RecentPlay;
import net.mamestagram.Message.WelcomeMessage;
import net.mamestagram.Server.PatchAnnounce;
import net.mamestagram.Server.RoleDistribution;

public class JDABuilder {

    public static JDA createJDA(String token) {

        JDA jda = net.dv8tion.jda.api.JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                .enableCache(CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI)
                .disableCache(CacheFlag.VOICE_STATE,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS)
                .disableCache(CacheFlag.FORUM_TAGS)
                .setRawEventsEnabled(true)
                .addEventListeners(new PatchAnnounce())
                .addEventListeners(new RoleDistribution())
                .addEventListeners(new ReportScore())
                .addEventListeners(new MapStatus())
                .addEventListeners(new WelcomeMessage())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.streaming("mamesosu.net", "https://web.mamesosu.net/leaderboard/mode=std/special=none"))
                .build();

        return jda;
    }
}
