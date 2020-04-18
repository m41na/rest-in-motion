package works.hop.nosql.repo;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentUpdateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import works.hop.nosql.command.AResult;
import works.hop.nosql.command.EntityDocuments;
import works.hop.scrum.entity.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepo {

    private final Logger LOG = LoggerFactory.getLogger(UserRepo.class);
    private final ArangoDatabase db;
    private final ArangoCollection users;

    public UserRepo(@Autowired ArangoDB arangoDB) {
        this.db = arangoDB.db();
        if (!this.db.collection("users").exists()) {
            db.createCollection("users");
        }
        this.users = db.collection("users");
    }

    public AResult<User> save(User user) {
        try {
            BaseDocument userDoc = EntityDocuments.fromUser(user);
            DocumentCreateEntity<BaseDocument> created = users.insertDocument(userDoc, new DocumentCreateOptions().returnNew(true));
            LOG.info("new document created. id = {}, key = {}", created.getId(), created.getKey());
            return AResult.of(EntityDocuments.toUser(created.getNew()));
        } catch (Exception e) {
            return AResult.empty(Tracy.unpackStack(e));
        }
    }

    public AResult<User> merge(User user) {
        try {
            BaseDocument userDoc = EntityDocuments.fromUser(user);
            DocumentUpdateEntity<BaseDocument> updated = users.updateDocument(user.getId(), userDoc, new DocumentUpdateOptions().returnNew(true));
            LOG.info("current document updated. id = {}, key = {}", updated.getId(), updated.getKey());
            return AResult.of(EntityDocuments.toUser(updated.getNew()));
        } catch (Exception e) {
            return AResult.empty(Tracy.unpackStack(e));
        }
    }

    public AResult<User> findById(String id) {
        BaseDocument doc = users.getDocument(id, BaseDocument.class);
        return AResult.of(EntityDocuments.toUser(doc));
    }

    public AResult<User> findByEmail(String emailAddress) {
        String query = "FOR u IN users FILTER u.emailAddress == @emailAddress RETURN u";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("emailAddress", emailAddress);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            return AResult.of(EntityDocuments.toUser(doc));
        }
        return AResult.empty("No entity found using provided query");
    }
}
