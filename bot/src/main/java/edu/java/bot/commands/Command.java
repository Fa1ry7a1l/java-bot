package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;

public abstract class Command {

    public final String command;
    public final String description;

    public Command(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public abstract String handle(Update update);

    //default boolean supports(Update update) {  }
}
