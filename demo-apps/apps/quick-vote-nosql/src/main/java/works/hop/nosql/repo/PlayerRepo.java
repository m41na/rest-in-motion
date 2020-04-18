package works.hop.nosql.repo;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentDeleteEntity;
import com.arangodb.model.DocumentCreateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import works.hop.nosql.command.AResult;
import works.hop.nosql.command.EntityDocuments;
import works.hop.scrum.entity.Player;

@Repository
public class PlayerRepo {

    private final Logger LOG = LoggerFactory.getLogger(PlayerRepo.class);
    private final ArangoDatabase db;
    private final ArangoCollection players;

    public PlayerRepo(@Autowired ArangoDB arangoDB) {
        this.db = arangoDB.db();
        if (!this.db.collection("players").exists()) {
            db.createCollection("players");
        }
        this.players = this.db.collection("players");
    }

    public AResult<Player> save(Player player) {
        if (!findById(player.getTeamId(), player.getName()).isOk()) {
            try {
                BaseDocument playerDoc = EntityDocuments.fromPlayer(player);
                DocumentCreateEntity<BaseDocument> created = players.insertDocument(playerDoc, new DocumentCreateOptions().returnNew(true));
                LOG.info("new document created. id = {}, key = {}", created.getId(), created.getKey());
                return AResult.of(EntityDocuments.toPlayer(created.getNew()));
            } catch (Exception e) {
                return AResult.empty(Tracy.unpackStack(e));
            }
        } else {
            return AResult.empty("There already exists a document with the same team id and player name");
        }
    }

    public AResult<Player> findById(String teamId, String name) {
        BaseDocument doc = players.getDocument(String.format("%s:%s", teamId, name), BaseDocument.class);
        return doc != null ? AResult.of(EntityDocuments.toPlayer(doc)) : AResult.empty("No document found");
    }

    public AResult<Integer> deletePlayer(String teamId, String name) {
        DocumentDeleteEntity<Void> deleted = players.deleteDocument(String.format("%s:%s", teamId, name));
        if (deleted != null) {
            return AResult.of(1);
        }
        return AResult.empty("Could not delete specified document");
    }
}
