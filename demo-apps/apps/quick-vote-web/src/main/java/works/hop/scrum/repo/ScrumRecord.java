package works.hop.scrum.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import works.hop.scrum.domain.Scrum;
import works.hop.reducer.persist.RecordEntity;
import works.hop.reducer.persist.RecordValue;

import java.io.IOException;

@Data
@AllArgsConstructor
@Builder
public class ScrumRecord implements RecordValue<Scrum> {

    private final ObjectMapper mapper = new ObjectMapper();
    private RecordEntity entity;

    @Override
    public Scrum getValue() {
        try {
            return mapper.readValue(entity.getValue(), Scrum.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(Scrum scrum) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(scrum);
            entity.setValue(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
