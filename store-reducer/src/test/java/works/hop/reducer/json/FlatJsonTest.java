package works.hop.reducer.json;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class FlatJsonTest {

    private FlatJson flat = new FlatJson(new Companion(CrazyClass.class, new CrazyClass()));

    @Test
    public void testGetName() throws IOException {
        Map<String, Object> map = new HashMap<>();
        Companion fields = flat.flatten(map);
        assertEquals("works/hop/reducer/json/CrazyClass", fields.getName());
    }
}