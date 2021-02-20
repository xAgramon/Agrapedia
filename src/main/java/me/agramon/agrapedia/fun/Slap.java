package me.agramon.agrapedia.fun;

import com.jagrosh.jdautilities.command.CommandEvent;

public class Slap extends PurrBotAPI {
    public Slap() {
        super("slap", "*Wa-pow!*", "/sfw/slap/gif", "slaps you");
        super.category = new Category("Fun");
        super.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent e) {
        super.execute(e);
    }
}
