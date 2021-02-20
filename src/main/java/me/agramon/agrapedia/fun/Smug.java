package me.agramon.agrapedia.fun;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Smug extends NekosLifeAPI {
    public Smug() {
        super("smug", "¬‿¬", "smug");
        super.category = new Category("Fun");
        super.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent e) {
        super.execute(e);
    }
}