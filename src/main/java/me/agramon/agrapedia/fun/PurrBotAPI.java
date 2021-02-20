package me.agramon.agrapedia.fun;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PurrBotAPI extends Command {

    public String name;
    public String api;
    public String title;

    public PurrBotAPI (String name, String help, String api, String title) {
        super.name = name;
        super.help = help;
        super.category = new Category("Roleplay");
        this.api = api;
        this.title = title;
    }

    @Override
    protected void execute(CommandEvent e) {
        WebUtils.ins.getJSONObject("https://purrbot.site/api/img" + api).async((json) -> {
            String url = json.get("link").asText();
            MessageEmbed embed;
            if (!e.getArgs().isEmpty() && e.getMessage().getMentionedMembers().get(0).getUser() == e.getSelfUser()) {
                embed = EmbedUtils.embedImage(url)
                        .setTitle(e.getMessage().getMentionedMembers().get(0).getUser().getName() + " loves you too " + e.getMessage().getAuthor().getName() + "!")
                        .build();
            } else if (!e.getArgs().isEmpty() && e.getMessage().getMentionedMembers().get(0) != null) {
                embed = EmbedUtils.embedImage(url)
                        .setTitle(e.getMessage().getAuthor().getName() + " " + title + " " + e.getMessage().getMentionedMembers().get(0).getUser().getName())
                        .build();
            } else {
                embed = EmbedUtils.embedImage(url)
                        .build();
            }
            e.reply(embed);
        });
    }
}