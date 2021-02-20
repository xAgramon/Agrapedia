package me.agramon.agrapedia;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import me.agramon.agrapedia.fun.Baka;
import me.agramon.agrapedia.fun.Pat;
import me.agramon.agrapedia.fun.Slap;
import me.agramon.agrapedia.fun.Smug;
import me.agramon.agrapedia.info.Avatar;
import me.agramon.agrapedia.info.Help;
import me.agramon.agrapedia.info.Info;
import me.agramon.agrapedia.info.Profile;
import me.agramon.agrapedia.web.KakaoPage;
import me.agramon.agrapedia.web.MangaDex;
import me.agramon.agrapedia.web.NovelUpdates;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import javax.security.auth.login.LoginException;


public class Agrapedia {

    public static void main(String [] args) throws LoginException {
        CommandClientBuilder ccb = new CommandClientBuilder();

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(Config.get("TOKEN"));
        builder.addEventListeners(new Log());

        //builder.addEventListeners(new AgraPinged());
        builder.addEventListeners(new TemperatureConverter());
        builder.addEventListeners(new LengthConverter());
        builder.addEventListeners(new WeightConverter());

        ccb.setPrefix(Config.get("PREFIX"));
        ccb.setOwnerId("305410443059396608");
        ccb.setHelpWord("dmhelp");
        ccb.setActivity(Activity.watching("Hentai (͡° ͜ʖ ͡°)"));

        // Fun Category
        ccb.addCommand(new Baka());
        ccb.addCommand(new Pat());
        ccb.addCommand(new Slap());
        ccb.addCommand(new Smug());

        // Help/Info Category
        ccb.addCommand(new Avatar());
        ccb.addCommand(new Help());
        ccb.addCommand(new Info());
        ccb.addCommand(new Profile());

        // Sources Category
        ccb.addCommand(new KakaoPage());
        ccb.addCommand(new MangaDex());
        ccb.addCommand(new NovelUpdates());

        CommandClient client = ccb.build();
        builder.addEventListeners(client);
        builder.build();

        //ShardManager sm = builder.build();
        //System.out.println(sm.getGuildCache().asList());
    }
}
