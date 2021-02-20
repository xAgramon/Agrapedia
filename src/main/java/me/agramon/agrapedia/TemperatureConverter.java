package me.agramon.agrapedia;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DecimalFormat;

public class TemperatureConverter extends ListenerAdapter {
    private static final String s = ".*\\s-?[0-9]+[cfCF]\\s.*";
    private static final String s2 = "-?[0-9]+[cfCF]";
    private static final String s3 = "-?[0-9]+[cfCF]\\s.*";
    private static final String s4 = ".*\\s-?[0-9]+[cfCF]";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e)
    {
        if (e.getMessage().getContentRaw().matches(s) || e.getMessage().getContentRaw().matches(s2) || e.getMessage().getContentRaw().matches(s3) || e.getMessage().getContentRaw().matches(s4)) {
            e.getMessage().addReaction("U+1F321").queue();
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        DecimalFormat df = new DecimalFormat("#.#");
        if (e.getReactionEmote().getAsReactionCode().equals("\uD83C\uDF21") && e.getUser() != e.getJDA().getSelfUser()) {
            e.getChannel().retrieveMessageById(e.getMessageId()).queue(message -> {
                String m = message.getContentRaw();
                String x = m.replaceAll("^\\D*(\\d+).*", "$1");
                String unit = m.charAt(m.indexOf(x) + x.length()) + "";
                boolean b = false;
                try {
                    b = m.charAt(m.indexOf(x) - 1) == '-';
                } catch (Exception ex) { System.out.println("No negative sign!"); }
                if (unit.equalsIgnoreCase("f")) {
                    double c = 0;
                    if (b) {
                        x = "-" + x;
                        c = ((Double.parseDouble(x) - 32)*5)/9;
                    }
                    double finalC = c;
                    String finalX = x;
                    e.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("**" + finalX + "°F** is equivalent to **" + df.format(finalC) + "°C**").queue());
                } else if (unit.equalsIgnoreCase("c")) {
                    double f = 0;
                    if (b) {
                        x = "-" + x;
                        f = ((Double.parseDouble(x)*9)/5)+32;
                    }
                    double finalF = f;
                    String finalX1 = x;
                    e.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("**" + finalX1 + "°C** is equivalent to **" + df.format(finalF) + "°F**").queue());
                } else {
                    System.out.println(unit);
                }
            });
        }
    }
}
