package edu.java.bot.entity.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class UserLinksRepository {

    public UserLinksRepository() {
    }

    HashMap<Long, ArrayList<URI>> links = new HashMap<>();

    public HashMap<Long, ArrayList<URI>> getLinks() {
        return links;
    }


    public void addLink(Long id, URI link) {
        ArrayList<URI> currentUsersLinks;
        if (!links.containsKey(id)) {
            links.put(id, new ArrayList<>());

        }
        currentUsersLinks = links.get(id);
        currentUsersLinks.add(link);
    }

    public ArrayList<URI> getLinks(Long id) {
        if (!links.containsKey(id)) {
            links.put(id, new ArrayList<>());

        }
        return links.get(id);
    }
}
