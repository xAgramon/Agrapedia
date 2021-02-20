package me.agramon.agrapedia;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String token) {
        return dotenv.get(token);
    }
}
