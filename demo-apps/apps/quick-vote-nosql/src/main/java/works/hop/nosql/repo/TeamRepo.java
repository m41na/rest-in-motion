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
import works.hop.scrum.entity.Team;

import java.util.*;

@Repository
public class TeamRepo {

    private final Logger LOG = LoggerFactory.getLogger(TeamRepo.class);
    private final ArangoDatabase db;
    private final ArangoCollection teams;

    public TeamRepo(@Autowired ArangoDB arangoDB) {
        this.db = arangoDB.db();
        if (!this.db.collection("teams").exists()) {
            db.createCollection("teams");
        }
        this.teams = this.db.collection("teams");
    }

    public AResult<Team> save(Team team) {
        if (!findByName(team.getTitle(), team.getOrganization()).isOk()) {
            try {
                BaseDocument teamDoc = EntityDocuments.fromTeam(team);
                DocumentCreateEntity<BaseDocument> created = teams.insertDocument(teamDoc, new DocumentCreateOptions().returnNew(true));
                LOG.info("new document created. id = {}, key = {}", created.getId(), created.getKey());
                return AResult.of(EntityDocuments.toTeam(created.getNew()));
            } catch (Exception e) {
                return AResult.empty(Tracy.unpackStack(e));
            }
        } else {
            return AResult.empty("A team already exists with the same title and organization");
        }
    }

    public AResult<Team> merge(Team team) {
        if (!findByName(team.getTitle(), team.getOrganization()).isOk()) {
            try {
                BaseDocument teamDoc = EntityDocuments.fromTeam(team);
                DocumentUpdateEntity<BaseDocument> updated = teams.updateDocument(team.getId(), teamDoc, new DocumentUpdateOptions().returnNew(true));
                LOG.info("current document updated. id = {}, key = {}", updated.getId(), updated.getKey());
                return AResult.of(EntityDocuments.toTeam(updated.getNew()));
            } catch (Exception e) {
                return AResult.empty(Tracy.unpackStack(e));
            }
        } else {
            return AResult.empty("A team already exists with the same title and organization");
        }
    }

    public AResult<Team> findById(String id) {
        BaseDocument doc = teams.getDocument(id, BaseDocument.class);
        return doc != null ? AResult.of(EntityDocuments.toTeam(doc)) : AResult.empty("no document was found");
    }

    public AResult<Team> findByName(String title, String organization) {
        String query = "FOR t IN teams FILTER t.title == @title and t.organization == @organization RETURN t";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("title", title);
        params.putIfAbsent("organization", organization);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            return AResult.of(EntityDocuments.toTeam(doc));
        }
        return AResult.empty("No entity found using provided query");
    }

    public AResult<List<Team>> findByOrganizer(String organizer) {
        String query = "FOR t IN teams FILTER t.organizer == @organizer RETURN t";
        Map<String, Object> params = new HashMap<>();
        params.putIfAbsent("organizer", organizer);
        //execute query
        ArangoCursor<BaseDocument> cursor = db.query(query, params, BaseDocument.class);
        Collection<Team> entities = new LinkedList<>();
        if (cursor.hasNext()) {
            BaseDocument doc = cursor.next();
            entities.add(EntityDocuments.toTeam(doc));
        }
        return AResult.of(entities);
    }
}
