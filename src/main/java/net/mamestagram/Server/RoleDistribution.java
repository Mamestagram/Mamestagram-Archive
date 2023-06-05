package net.mamestagram.Server;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class RoleDistribution extends ListenerAdapter {

    private static final long CHANNELID = 1012691799350988810L;
    private static final long INFOROLEID = 1083976804680863744L;
    private static final long OSUCATCHID = 1115259320012111913L;
    private static final long OSUTAIKOID = 1115259058816041101L;
    private static final long OSUMANIAID = 1115258910123773992L;
    private static final long OSUSTDID = 1115258608557494333L;
    private static final long MULTIID = 1115260158830985246L;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {

        Guild guild = e.getGuild();
        Member member = e.getMember();
        Role infoRole = guild.getRoleById(INFOROLEID);
        Role catchRole = guild.getRoleById(OSUCATCHID);
        Role taikoRole = guild.getRoleById(OSUTAIKOID);
        Role maniaRole = guild.getRoleById(OSUMANIAID);
        Role stdRole = guild.getRoleById(OSUSTDID);
        Role multiRole = guild.getRoleById(MULTIID);

        if(e.getChannel().getIdLong() == CHANNELID) {

            if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("U+2705"))){
                guild.addRoleToMember(member, infoRole).queue();
            } else if(e.getReaction().getEmoji().equals(Emoji.fromFormatted("<:osu:1100702517119168562>"))) {
                guild.addRoleToMember(member, stdRole).queue();
            } else if(e.getReaction().getEmoji().equals(Emoji.fromFormatted("<:catch:992621083985457202>"))) {
                guild.addRoleToMember(member, catchRole).queue();
            } else if(e.getReaction().getEmoji().equals(Emoji.fromFormatted("<:mania:1100702514501910630>"))) {
                guild.addRoleToMember(member, maniaRole).queue();
            } else if(e.getReaction().getEmoji().equals(Emoji.fromFormatted("<:taiko:1100702510152429588>"))) {
                guild.addRoleToMember(member, taikoRole).queue();
            } else if(e.getReaction().getEmoji().equals(Emoji.fromUnicode("U+1F525"))) {
                guild.addRoleToMember(member, multiRole).queue();
            }
        }
    }
}
