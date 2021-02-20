package me.agramon.agrapedia.fun;

import com.jagrosh.jdautilities.command.CommandEvent;

public class Pat extends PurrBotAPI {
    public Pat() {
        super("pat", "*Pat pat*", "/sfw/pat/gif", "pats");
        super.category = new Category("Fun");
        super.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent e) {
        super.execute(e);
    }
}
