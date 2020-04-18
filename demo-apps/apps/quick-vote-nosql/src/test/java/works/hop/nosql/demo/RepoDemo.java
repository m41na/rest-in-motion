package works.hop.nosql.demo;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import works.hop.nosql.command.EntityDocuments;
import works.hop.nosql.config.RepoConfig;
import works.hop.scrum.entity.User;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

public class RepoDemo {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RepoConfig.class);
        ArangoDB arangoDB = ctx.getBean(ArangoDB.class);

        ArangoCollection scrum = Optional.ofNullable(arangoDB.db().collection("scrums")
        ).orElseGet(() -> {
            arangoDB.db().createCollection("scrums");
            return arangoDB.db().collection("scrums");
        });
        User user = User.builder().emailAddress("email@rock.com").dateCreated(new Date()).firstName("steve").build();

        BaseDocument userDoc = EntityDocuments.fromUser(user);
        DocumentCreateEntity created = scrum.insertDocument(userDoc);
        System.out.printf("new document created. id = %s, key = %s%n", created.getId(), created.getKey());

        BaseDocument retrieved = scrum.getDocument(userDoc.getKey(), BaseDocument.class);
        System.out.printf("document retrieved. id = %s, key = %s%n", retrieved.getId(), retrieved.getKey());

        String query = "FOR t IN scrums RETURN t";
        ArangoCursor<BaseDocument> cursor = arangoDB.db().query(query, Collections.emptyMap(), null,
                BaseDocument.class);
        cursor.forEachRemaining(aDocument -> {
            System.out.println("Key: " + aDocument.getKey());
        });

        arangoDB.shutdown();
    }
}
