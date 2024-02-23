package edu.java.bot.commands;

import edu.java.bot.LinkChecker;
import edu.java.bot.entity.repository.UserLinksRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandHandler {

    private final HashMap<String, Command> commands;

    @Autowired
    public CommandHandler(UserLinksRepository userLinksRepository) {
        commands = new HashMap<>();
        List<Command> allCommands = new ArrayList<>();
        allCommands.add(new StartCommand());
        allCommands.add(new TrackCommand(userLinksRepository, new LinkChecker()));
        allCommands.add(new UntrackCommand(userLinksRepository));
        allCommands.add(new ListCommand(userLinksRepository));
        for (var c : allCommands) {
            commands.put(c.command, c);
        }
        commands.put("/help", new HelpCommand(allCommands));

    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
