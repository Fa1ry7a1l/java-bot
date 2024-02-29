package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;

public class StartCommand extends Command {

    public StartCommand() {
        super("/start", "Запоминание пользователя");
    }

    @Override
    public String handle(Update update) {
        return "Я вас запомнил\n";
    }
}
