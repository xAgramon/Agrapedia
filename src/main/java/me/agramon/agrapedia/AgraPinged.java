package me.agramon.agrapedia;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class AgraPinged extends ListenerAdapter {
    private final String[] responses = new String[] {"bruh stop pingin me", "what do you want??", "am i not working hard enough already?!", "stop poking me", "i do what i want", "baka!!", "pls can you not"};
    @Override
    public void onMessageReceived(MessageReceivedEvent e)
    {
        if (e.getMessage().getMentionedMembers().contains(e.getGuild().getSelfMember())) {
            e.getMessage().getChannel().sendMessage(new EmbedBuilder().setColor(Color.MAGENTA).setDescription(responses[(int)(Math.random() * (responses.length))]).build()).queue();
        }
    }
}