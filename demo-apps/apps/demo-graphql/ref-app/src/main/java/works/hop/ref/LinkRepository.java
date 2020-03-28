package works.hop.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LinkRepository {

    private final List<Link> links;

    public LinkRepository() {
        links = new ArrayList<>();
        //add some links to start off with
        links.add(new Link(nextId(), "http://howtographql.com", "Your favorite GraphQL page"));
        links.add(new Link(nextId(), "http://graphql.org/learn/", "The official docks"));
    }

    public List<Link> getAllLinks() {
        return links;
    }

    public void saveLink(Link link) {
        links.add(link);
    }

    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
