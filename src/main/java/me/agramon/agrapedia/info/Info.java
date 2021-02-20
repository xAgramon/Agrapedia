package me.agramon.agrapedia.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class Info extends Command {
    public Info() {
        super.name = "info";
        super.help = "Shows the server info";
        super.cooldown = 5;
        super.category = new Category("Help/Info");
        super.aliases = new String[]{"info"};
    }

    @Override
    protected void execute(CommandEvent e) {
        Guild g = e.getGuild();

        String generalInfo = "**Region**: " + g.getRegion().getName() + "\n**Birthday** :birthday:: " + g.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        String memberInfo = "**Total Members**: " + g.getMemberCount();

        EmbedBuilder eb = new EmbedBuilder()
                .setThumbnail(g.getIconUrl())
                .setTitle("Server Info for " + g.getName())
                .addField("", generalInfo, false)
                .addField("", memberInfo, false);

        e.reply(eb.build());
    }
}