package net.mamestagram.server;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static net.mamestagram.Main.jda;

public class RoleDistribution extends ListenerAdapter {

    private static final long chID = 1012691799350988810L;
    private static final long infoRoleID = 1083976804680863744L;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {

        if(e.getChannel().getIdLong() == chID && e.getReaction().getEmoji().equals(Emoji.fromUnicode("U+2705"))) {
            Guild guild = e.getGuild();
            Member member = e.getMember();
            long messageID = e.getMessageIdLong();
            Role role = guild.getRoleById(infoRoleID);

            guild.addRoleToMember(member, role).queue();
        }
    }
}
