package me.agramon.agrapedia.info;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class Help extends Command {
    public Help() {
        super.name = "help";
        super.help = "List of commands";
        super.cooldown = 5;
        super.category = new Category("Help/Info");
    }

    @Override
    protected void execute(CommandEvent e) {
        EmbedBuilder eb = new EmbedBuilder()
                .setThumbnail(e.getGuild().getIconUrl())
                .setTitle("Available Commands for Agrapedia")

                .addField(":question: Help/Info :question:",
                        "• avatar \n" +
                                "• dmhelp \n" +
                                "• help \n" +
                                "• info \n" +
                                "• profile (user) \n" +
                                "\n"
                        , true)
                .addField(":video_game: Fun :video_game:",
                        "• baka \n" +
                                "• pat (user) \n" +
                                "• slap (user) \n" +
                                "• smug \n" +
                                "\n"
                        , true)
                .addField(":mag_right: Sources :mag_right:",
                        "• kkp \n" +
                                "• kkp get [seriesID] \n" +
                                "• kkp search [name] \n" +
                                "\n" +
                                "• md [name/seriesID]\n" +
                                "• md group [name/groupID]\n" +
                                "• md searchgroup [name]\n" +
                                "• md search [name]\n" +
                                "• md searchall [name]\n" +
                                "\n" +
                                "*Currently In Development*\n" +
                                "• nu [name]\n" +
                                "• nu search [name]\n" +
                                "• nu searchall [name]\n" +
                                "\n"
                        , true);

        e.reply(eb.build());
    }
}
