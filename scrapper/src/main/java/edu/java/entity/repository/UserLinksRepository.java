package edu.java.entity.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

@Repository
public class UserLinksRepository {

    private final HashMap<Long, ArrayList<URI>> links = new HashMap<>();

    public boolean add(Long id, URI link) {
        ArrayList<URI> currentUsersLinks;
        currentUsersLinks = links.get(id);
        if (currentUsersLinks.contains(link)) {
            return false;
        }
        currentUsersLinks.add(link);
        return true;

    }

    public void createUser(Long id) {
        links.put(id, new ArrayList<>());
    }

    public boolean remove(Long id, URI url) {
        return links.get(id).remove(url);
    }

    public ArrayList<URI> getLinks(Long id) {
        if (!links.containsKey(id)) {
            links.put(id, new ArrayList<>());

        }
        return links.get(id);
    }
}
