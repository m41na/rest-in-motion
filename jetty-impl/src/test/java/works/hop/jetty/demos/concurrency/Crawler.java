package works.hop.jetty.demos.concurrency;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

public class Crawler {

    private static Logger LOG = LoggerFactory.getLogger(Crawler.class);
    private static String[] startUrls = {"https://en.wikipedia.org/", "https://www.cnn.com", "https://www.msn.com" };
    private static LongAdder visitedCount = new LongAdder();

    public static Boolean isHttpUrl(String url) {
        return url != null &&
                url.trim().length() > 0 &&
                url.startsWith("http");
    }

    public static Document load(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        visitedCount.increment();
        return doc;
    }

    public static Set<String> linksFound(Document doc) {
        Set<String> links = new HashSet<>();
        Elements linkNodes = doc.select("a");
        for (Element headline : linkNodes) {
            String href = headline.absUrl("href");
            //remove page anchors
            href = href.replaceAll("#.*$", "");
            links.add(href);
        }
        System.out.printf("retrieve %d links from %s\n", links.size(), doc.location());
        return links;
    }

    public static CompletableFuture<Set<String>> visitLinks(int depth, int maxDepth, Set<String> links, Set<String> visited) {
        if (depth == maxDepth) {
            return CompletableFuture.completedFuture(visited);
        } else {
            List<CompletableFuture<Set<String>>> tasks = new LinkedList<>();
            for (String link : links) {
                Supplier<CompletableFuture<Set<String>>> supplier = () -> {
                    try {
                        Set<String> newLinks = !visited.contains(link) && isHttpUrl(link) ?
                                linksFound(load(link)) : emptySet();
                        return visitLinks(depth + 1, maxDepth, newLinks, visited);
                    } catch (IOException e) {
                        System.out.println("Could not load url -> " + link);
                        return CompletableFuture.completedFuture(emptySet());
                    }
                };
                tasks.add(supplier.get());
                visited.add(link);
            }
            return CompletableFuture.completedFuture(tasks.stream().map(fut -> fut.join()).flatMap(x -> x.stream()).parallel().collect(Collectors.toSet()));
        }
    }

    public static void main(String[] args) throws IOException {
        //System.out.println(linksFound(load(startUrls[0])));
        Set<String> source = new HashSet<>();
        source.add(startUrls[2]);
        visitLinks(0, 2, source, new HashSet<>());
        LOG.info("Total urls visited: " + visitedCount.sum());
    }
}
