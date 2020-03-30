package works.hop.reducer.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrazyClass implements Comparable<CrazyClass> {

    private Long id;
    private int[] scores;
    private int[][] settings;
    private String name;
    private Boolean awesome;
    private List<String> stringList = new ArrayList<>();
    private List<List<Double>> doubleTrouble = new LinkedList<>();
    private Map<String, Object> stringObjectMap = new HashMap<>();
    private Map<String, Map<String, Object>> nestedSpiderMap = new TreeMap<>();

    @Override
    public int compareTo(CrazyClass crazyClass) {
        if(this == crazyClass) return 0;
        return this.id.compareTo(crazyClass.id);
    }
}
