package me.agramon.agrapedia.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class Profile extends Command {
    public Profile() {
        super.name = "profile";
        super.help = "Shows a user's profile";
        super.cooldown = 5;
        super.category = new Category("Help/Info");
        super.aliases = new String[] {"points", "p"};
    }

    @Override
    protected void execute(CommandEvent e) {
        EmbedBuilder eb = new EmbedBuilder();

        User user = e.getMember().getUser();
        if (!e.getArgs().isEmpty()) {
            user = e.getMessage().getMentionedMembers().get(0).getUser();
        }

        eb.setThumbnail(user.getAvatarUrl())
                .setAuthor("Profile of " + user.getName(), null, user.getAvatarUrl())
                .addField("Username:", user.getName(), false)
                .addField("Account Created:", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), false);

        e.reply(eb.build());
    }
}
