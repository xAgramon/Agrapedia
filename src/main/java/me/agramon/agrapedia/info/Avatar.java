package me.agramon.agrapedia.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Objects;

public class Avatar extends Command {
    public Avatar() {
        super.name = "avatar";
        super.help = "Shows a user's avatar";
        super.cooldown = 5;
        super.category = new Category("Help/Info");
    }

    @Override
    protected void execute(CommandEvent e) {
        EmbedBuilder eb = new EmbedBuilder();

        if (e.getMessage().getMentionedMembers().size() != 0) {
            eb.setTitle(e.getMessage().getMentionedMembers().get(0).getUser().getName() + "'s Avatar");
            eb.setImage(e.getMessage().getMentionedMembers().get(0).getUser().getAvatarUrl());
        } else if (!e.getArgs().isEmpty() && e.getJDA().getUserById(e.getArgs()) != null) {
            eb.setTitle(Objects.requireNonNull(e.getJDA().getUserById(e.getArgs())).getName() + "'s Avatar");
            eb.setImage(Objects.requireNonNull(e.getJDA().getUserById(e.getArgs())).getAvatarUrl());
        } else {
            eb.setTitle(Objects.requireNonNull(e.getMessage().getMember()).getUser().getName() + "'s Avatar");
            eb.setImage(e.getMessage().getMember().getUser().getAvatarUrl());
        }
        e.reply(eb.build());
    }
}