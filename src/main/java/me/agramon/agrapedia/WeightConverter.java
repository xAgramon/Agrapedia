package me.agramon.agrapedia;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DecimalFormat;

public class WeightConverter extends ListenerAdapter {
    private static final String s = ".*\\s[0-9]+[kglbKGLB]{2}\\s.*";
    private static final String s2 = "[0-9]+[kglbKGLB]{2}";
    private static final String s3 = "[0-9]+[kglbKGLB]{2}\\s.*";
    private static final String s4 = ".*\\s[0-9]+[kglbKGLB]{2}";
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e)
    {
        if (e.getMessage().getContentRaw().matches(s) || e.getMessage().getContentRaw().matches(s2) || e.getMessage().getContentRaw().matches(s3) || e.getMessage().getContentRaw().matches(s4)) {
            e.getMessage().addReaction("U+2696").queue();
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        DecimalFormat df = new DecimalFormat("#.##");
        if (e.getReactionEmote().getAsReactionCode().equals("\u2696") && e.getUser() != e.getJDA().getSelfUser()) {
            e.getChannel().retrieveMessageById(e.getMessageId()).queue(message -> {
                String m = message.getContentRaw();
                String x = m.replaceAll("^\\D*(\\d+).*", "$1");
                String unit = m.substring(m.indexOf(x) + x.length(), m.indexOf(x) + x.length() + 2);
                if (unit.equalsIgnoreCase("kg")) {
                    double lb = Double.parseDouble(x) * 2.205;
                    e.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("**" + x + "kg** is equivalent to **" + df.format(lb) + "lb**").queue());
                } else if (unit.equalsIgnoreCase("lb")) {
                    double kg = Double.parseDouble(x) / 2.205;
                    e.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("**" + x + "lb** is equivalent to **" + df.format(kg) + "kg**").queue());
                } else {
                    System.out.println(unit);
                }
            });
        }
    }
}
