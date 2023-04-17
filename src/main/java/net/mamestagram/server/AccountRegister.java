package net.mamestagram.server;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AccountRegister extends ListenerAdapter {

    //テーブル "userlink" を追加必須

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {

        if(e.getName().equals("link")) {

        }
    }
}
