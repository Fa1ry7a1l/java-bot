package edu.java.bot;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.CommandHandler;
import edu.java.bot.entity.User;
import edu.java.bot.entity.repository.UserRepository;

public class ResponseService {
    private final UserRepository userRepository;
    private final CommandHandler commandHandler;

    private static final String ERROR_MESSAGE = "Команда не распознана, список команд можно увидеть при помощи /help\n";

    public ResponseService(UserRepository userRepository, CommandHandler commandHandler) {
        this.userRepository = userRepository;
        this.commandHandler = commandHandler;

    }

    public String getAnswer(Update update) {
        var userId = update.message().chat().id();

        if (userRepository.getUser(userId).isEmpty()) {
            userRepository.put(userId, new User(userId));
        }

        var messageArray = update.message().text().trim().split("\s+");
        if (!commandHandler.getCommands().containsKey(messageArray[0])) {
            return ERROR_MESSAGE;
        }
        return commandHandler.getCommands().get(messageArray[0]).handle(update);

    }

}
