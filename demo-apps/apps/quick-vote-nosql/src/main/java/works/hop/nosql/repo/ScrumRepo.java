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
import works.hop.scrum.entity.Scrum;

import java.util.stream.Collectors;

@Repository
public class ScrumRepo {

    private final Logger LOG = LoggerFactory.getLogger(ScrumRepo.class);
    private final ArangoDatabase db;
    private final ArangoCollection scrums;

    public ScrumRepo(@Autowired ArangoDB arangoDB) {
        this.db = arangoDB.db();
        if (!this.db.collection("scrums").exists()) {
            db.createCollection("scrums");
        }
        this.scrums = this.db.collection("scrums");
    }

    public AResult<Scrum> save(Scrum scrum) {
        if (!findById(scrum.getTeamId(), scrum.getName()).isOk()) {
            try {
                BaseDocument scrumDoc = EntityDocuments.fromScrum(scrum);
                DocumentCreateEntity<BaseDocument> created = scrums.insertDocument(scrumDoc, new DocumentCreateOptions().returnNew(true));
                LOG.info("new document created. id = {}, key = {}", created.getId(), created.getKey());
                return AResult.of(EntityDocuments.toScrum(created.getNew()));
            } catch (Exception e) {
                return AResult.empty(Tracy.unpackStack(e));
            }
        } else {
            return AResult.empty("another document already exists with the same team id and name");
        }
    }

    public AResult<Scrum> findById(String teamId, String name) {
        BaseDocument doc = scrums.getDocument(String.format("%s:%s", teamId, name), BaseDocument.class);
        return doc != null ? AResult.of(EntityDocuments.toScrum(doc)) :
                AResult.empty("no document was found with matching team id and name");
    }

    public AResult<List<Scrum>> findByTeamId(String teamId) {
        String query = "FOR s IN scrums FILTER s.teamId == @teamId RETURN s";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("teamId", teamId);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        Collection<Scrum> entities = new LinkedList<>();
        while (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            entities.add(EntityDocuments.toScrum(doc));
        }
        return AResult.of(entities);
    }

    public AResult<Scrum> updateTopic(String teamId, String name, String topic) {
        String query = "FOR s IN scrums FILTER s.teamId == @teamId and s.name == @name RETURN s";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("teamId", teamId);
        params.putIfAbsent("name", name);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            doc.addAttribute("topic", topic);
            DocumentUpdateEntity<BaseDocument> updated = scrums.updateDocument(doc.getKey(), doc, new DocumentUpdateOptions().returnNew(true));
            return AResult.of(EntityDocuments.toScrum(updated.getNew()));
        }
        return AResult.empty("No entities found to update");
    }

    public AResult<Scrum> updateChoices(String teamId, String name, String[] choices) {
        String query = "FOR s IN scrums FILTER s.teamId == @teamId and s.name == @name RETURN s";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("teamId", teamId);
        params.putIfAbsent("name", name);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            doc.addAttribute("choices", String.join(",", choices));
            DocumentUpdateEntity<BaseDocument> updated = scrums.updateDocument(doc.getKey(), doc, new DocumentUpdateOptions().returnNew(true));
            return AResult.of(EntityDocuments.toScrum(updated.getNew()));
        }
        return AResult.empty("No entities found to update");
    }

    public AResult<Scrum> joinScrum(String teamId, String name, String participant) {
        String query = "FOR s IN scrums FILTER s.teamId == @teamId and s.name == @name RETURN s";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("teamId", teamId);
        params.putIfAbsent("name", name);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            String participants = doc.getAttribute("participants").toString();
            doc.addAttribute("participants", String.join(",", participants.trim().length() > 0 ? participant.concat(",").concat(participant) : participant));
            DocumentUpdateEntity<BaseDocument> updated = scrums.updateDocument(doc.getKey(), doc, new DocumentUpdateOptions().returnNew(true));
            return AResult.of(EntityDocuments.toScrum(updated.getNew()));
        }
        return AResult.empty("No entities found to update");
    }

    public AResult<Scrum> exitScrum(String teamId, String name, String participant) {
        String query = "FOR s IN scrums FILTER s.teamId == @teamId and s.name == @name RETURN s";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("teamId", teamId);
        params.putIfAbsent("name", name);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            String[] participants = doc.getAttribute("participants").toString().split(",");
            List<String> updatedList = Arrays.asList(participants).stream().filter(p -> !p.equals(participant)).collect(Collectors.toList());
            doc.addAttribute("participants", String.join(",", updatedList.toArray(new String[]{})));
            DocumentUpdateEntity<BaseDocument> updated = scrums.updateDocument(doc.getKey(), doc, new DocumentUpdateOptions().returnNew(true));
            return AResult.of(EntityDocuments.toScrum(updated.getNew()));
        }
        return AResult.empty("No entities found to update");
    }
}
