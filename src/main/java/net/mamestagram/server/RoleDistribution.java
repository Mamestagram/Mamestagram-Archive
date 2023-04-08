package net.mamestagram.server;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class RoleDistribution extends ListenerAdapter {

    private static final long CHANNELID = 1012691799350988810L;
    private static final long INFOROLEID = 1083976804680863744L;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {

        Guild guild = e.getGuild();
        Member member = e.getMember();
        Role role = guild.getRoleById(INFOROLEID);

        if(e.getChannel().getIdLong() == CHANNELID && e.getReaction().getEmoji().equals(Emoji.fromUnicode("U+2705"))) {

            guild.addRoleToMember(member, role).queue();
        }
    }
}