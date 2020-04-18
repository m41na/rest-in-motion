package works.hop.nosql.repo;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.*;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentUpdateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import works.hop.nosql.command.AResult;
import works.hop.nosql.command.EntityDocuments;
import works.hop.scrum.entity.Vote;

import java.util.*;

@Repository
public class VoteRepo {

    private final Logger LOG = LoggerFactory.getLogger(VoteRepo.class);
    private final ArangoDatabase db;
    private final ArangoCollection votes;

    public VoteRepo(@Autowired ArangoDB arangoDB) {
        this.db = arangoDB.db();
        if (!this.db.collection("votes").exists()) {
            db.createCollection("votes");
        }
        this.votes = this.db.collection("votes");
    }

    public AResult<Vote> save(Vote vote) {
        if (!findByParticipant(vote.getScrumKey(), vote.getParticipant()).isOk()) {
            try {
                BaseDocument voteDoc = EntityDocuments.fromVote(vote);
                DocumentCreateEntity<BaseDocument> created = votes.insertDocument(voteDoc, new DocumentCreateOptions().returnNew(true));
                LOG.info("new document created. id = {}, key = {}", created.getId(), created.getKey());
                return AResult.of(EntityDocuments.toVote(created.getNew()));
            } catch (Exception e) {
                return AResult.empty(Tracy.unpackStack(e));
            }
        } else {
            return updateVote(vote.getScrumKey(), vote.getParticipant(), vote.getTopic(), vote.getValue());
        }
    }

    public AResult<Vote> updateVote(String scrumKey, String participant, String topic, String value) {
        String query = "FOR v IN votes FILTER v.scrumKey == @scrumKey and v.participant == @participant and v.topic == @topic RETURN v";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("scrumKey", scrumKey);
        params.putIfAbsent("participant", participant);
        params.putIfAbsent("topic", topic);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            doc.addAttribute("value", value);
            DocumentUpdateEntity<BaseDocument> updated = votes.updateDocument(doc.getKey(), doc, new DocumentUpdateOptions().returnNew(true));
            return AResult.of(EntityDocuments.toVote(updated.getNew()));
        }
        return AResult.empty("No entities found to update");
    }

    public AResult<Vote> findByParticipant(String scrumKey, String participant) {
        BaseDocument doc = votes.getDocument(String.format("%s:%s", scrumKey, participant), BaseDocument.class);
        return doc != null ? AResult.of(EntityDocuments.toVote(doc)) : AResult.empty("No document found");
    }

    public AResult<Integer> toggleVotesLock(String scrumKey, String topic, Boolean locked) {
        String query = "FOR v IN votes FILTER v.scrumKey == @scrumKey and v.topic == @topic RETURN v";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("scrumKey", scrumKey);
        params.putIfAbsent("topic", topic);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        Collection<BaseDocument> entities = new LinkedList<>();
        while (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            doc.addAttribute("locked", locked);
            entities.add(doc);
        }
        //batch update
        MultiDocumentEntity<DocumentUpdateEntity<BaseDocument>> updated = votes.updateDocuments(entities);
        return AResult.of(updated.getDocuments().size());
    }

    public AResult<Integer> clearVotes(String scrumKey, String topic) {
        String query = "FOR v IN votes FILTER v.scrumKey == @scrumKey and v.topic == @topic RETURN v";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("scrumKey", scrumKey);
        params.putIfAbsent("topic", topic);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        Collection<BaseDocument> entities = new LinkedList<>();
        while (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            entities.add(doc);
        }
        //batch update
        MultiDocumentEntity<DocumentDeleteEntity<Void>> cleared = votes.deleteDocuments(entities);
        return AResult.of(cleared.getDocuments().size());
    }

    public AResult<List<Vote>> fetchCurrentVotes(String scrumKey, String topic) {
        String query = "FOR v IN votes FILTER v.scrumKey == @scrumKey and v.topic == @topic and v.locked == @locked RETURN v";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("scrumKey", scrumKey);
        params.putIfAbsent("topic", topic);
        params.putIfAbsent("locked", false);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        List<Vote> entities = new LinkedList<>();
        while (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            entities.add(EntityDocuments.toVote(doc));
        }
        return AResult.of(entities);
    }
}
