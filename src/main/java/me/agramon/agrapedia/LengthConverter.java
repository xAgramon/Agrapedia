package me.agramon.agrapedia;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DecimalFormat;

public class LengthConverter extends ListenerAdapter {
    private static final String s = ".*\\s[0-9]+[incmINCM]{2}\\s.*";
    private static final String s2 = "[0-9]+[incmINCM]{2}";
    private static final String s3 = "[0-9]+[incmINCM]{2}\\s.*";
    private static final String s4 = ".*\\s[0-9]+[incmINCM]{2}";
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e)
    {
        if (e.getMessage().getContentRaw().matches(s) || e.getMessage().getContentRaw().matches(s2) || e.getMessage().getContentRaw().matches(s3) || e.getMessage().getContentRaw().matches(s4)) {
            e.getMessage().addReaction("U+1F4CF").queue();
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        DecimalFormat df = new DecimalFormat("#.##");
        if (e.getReactionEmote().getAsReactionCode().equals("\uD83D\uDCCF") && e.getUser() != e.getJDA().getSelfUser()) {
            e.getChannel().retrieveMessageById(e.getMessageId()).queue(message -> {
                String m = message.getContentRaw();
                String x = m.replaceAll("^\\D*(\\d+).*", "$1");
                String unit = m.substring(m.indexOf(x) + x.length(), m.indexOf(x) + x.length() + 2);
                if (unit.equalsIgnoreCase("in")) {
                    double cm = Double.parseDouble(x) * 2.54;
                    e.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("**" + x + "in** is equivalent to **" + df.format(cm) + "cm**").queue());
                } else if (unit.equalsIgnoreCase("cm")) {
                    double in = Double.parseDouble(x) / 2.54;
                    e.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("**" + x + "cm** is equivalent to **" + df.format(in) + "in**\n" + "It is also equivalent to **" + (int)(in / 12) + " ft " + df.format((in)%12) + " in**").queue());
                } else {
                    System.out.println(unit);
                }
            });
        }
    }
}
