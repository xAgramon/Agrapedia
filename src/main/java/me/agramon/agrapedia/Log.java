package me.agramon.agrapedia;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class Log extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log.class);

    @Override
    public void onReady(@Nonnull ReadyEvent e) {
        LOGGER.info("Agrapedia is ready...");
    }
}
