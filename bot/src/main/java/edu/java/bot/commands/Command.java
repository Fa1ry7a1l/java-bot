package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Command {

    private final String command;
    private final String description;


    public abstract String handle(Update update);

}
