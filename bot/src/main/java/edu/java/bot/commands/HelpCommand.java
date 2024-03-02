package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand extends Command {

    private static final String PATTERN = "%s - %s\n";
    private final List<Command> allCommands;

    public HelpCommand(List<Command> allCommands) {
        super("/help", "Выводит список команд");
        this.allCommands = allCommands;
    }

    @Override
    public String handle(Update update) {

        StringBuilder sb = new StringBuilder();
        allCommands.forEach(command1 -> sb.append(PATTERN.formatted(command1.getCommand(), command1.getDescription())));

        sb.append(PATTERN.formatted(getCommand(), getDescription()));
        return sb.toString();
    }
}
