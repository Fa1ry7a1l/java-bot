package edu.java.bot.commands;

import edu.java.bot.LinkChecker;
import edu.java.bot.entity.repository.UserLinksRepository;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandHandler {

    private final HashMap<String, Command> commands;

    public CommandHandler(UserLinksRepository userLinksRepository) {
        commands = new HashMap<>();
        ArrayList<Command> tempCommands = new ArrayList<>();
        tempCommands.add(new StartCommand());
        tempCommands.add(new TrackCommand(userLinksRepository, new LinkChecker()));
        tempCommands.add(new UntrackCommand(userLinksRepository));
        tempCommands.add(new ListCommand(userLinksRepository));
        for (var c : tempCommands) {
            commands.put(c.command, c);
        }
        commands.put("/help", new HelpCommand(tempCommands));

    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
