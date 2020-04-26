package works.hop.nosql.repo;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentDeleteEntity;
import com.arangodb.entity.MultiDocumentEntity;
import com.arangodb.model.DocumentCreateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import works.hop.nosql.command.AResult;
import works.hop.nosql.command.EntityDocuments;
import works.hop.scrum.entity.Chat;

@Repository
public class ChatRepo {

    private final Logger LOG = LoggerFactory.getLogger(ChatRepo.class);
    private final ArangoDatabase db;
    private final ArangoCollection chatter;

    public ChatRepo(@Autowired ArangoDB arangoDB) {
        this.db = arangoDB.db();
        if (!this.db.collection("chatter").exists()) {
            db.createCollection("chatter");
        }
        this.chatter = this.db.collection("chatter");
    }

    public AResult<Chat> save(Chat chat) {
        try {
            BaseDocument chatDoc = EntityDocuments.fromChat(chat);
            DocumentCreateEntity<BaseDocument> created = chatter.insertDocument(chatDoc, new DocumentCreateOptions().returnNew(true));
            LOG.info("new document created. id = {}, key = {}", created.getId(), created.getKey());
            return AResult.of(EntityDocuments.toChat(created.getNew()));
        } catch (Exception e) {
            return AResult.empty(Tracy.unpackStack(e));
        }
    }

    public AResult<List<Chat>> retrieveConversation(String scrumKey) {
        String query = "FOR c IN chatter FILTER c.scrumKey == @scrumKey RETURN c";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("scrumKey", scrumKey);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        List<Chat> entities = new LinkedList<>();
        while (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            entities.add(EntityDocuments.toChat(doc));
        }
        return AResult.of(entities);
    }

    public AResult<Integer> clearChat(String scrumKey) {
        String query = "FOR c IN chatter FILTER c.scrumKey == @scrumKey RETURN c";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("scrumKey", scrumKey);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        Collection<BaseDocument> entities = new LinkedList<>();
        while (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            entities.add(doc);
        }
        //batch update
        MultiDocumentEntity<DocumentDeleteEntity<Void>> cleared = chatter.deleteDocuments(entities);
        return AResult.of(cleared.getDocuments().size());
    }
}
