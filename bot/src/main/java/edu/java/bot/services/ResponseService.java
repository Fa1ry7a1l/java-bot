package edu.java.bot.services;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import io.micrometer.core.instrument.Counter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ResponseService {

    private final Map<String, Command> allCommands;
    private final Counter messageCounter;

    private static final String ERROR_MESSAGE = "Команда не распознана, список команд можно увидеть при помощи /help\n";

    @Autowired
    public ResponseService(List<Command> allAvailableCommandsCommands, Counter messageCounter) {
        allCommands = new HashMap<>();
        for (Command command : allAvailableCommandsCommands) {
            allCommands.put(command.getCommand(), command);
        }
        this.messageCounter = messageCounter;
    }

    public String getAnswer(Update update) {
        var messageArray = update.message().text().trim().split("\s+");
        if (messageArray.length == 0 || !allCommands.containsKey(messageArray[0])) {
            return ERROR_MESSAGE;
        }
       log.info("ответили на сообщение {}", messageArray[0]);
        messageCounter.increment();
        return allCommands.get(messageArray[0]).handle(update);

    }

}
