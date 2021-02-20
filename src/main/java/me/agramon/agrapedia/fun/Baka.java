package me.agramon.agrapedia.fun;

import com.jagrosh.jdautilities.command.CommandEvent;

public class Baka extends NekosLifeAPI {
    public Baka() {
        super("baka", "B-baka!", "baka");
        super.category = new Category("Fun");
        super.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent e) {
        super.execute(e);
    }
}