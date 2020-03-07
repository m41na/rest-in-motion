package works.hop.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class AResponseEntityTest {

    @Test
    public void ok() {
        AResponseEntity res = AResponseEntity.ok("YES");
        assertEquals("YES", res.data);
        assertTrue(res.errors.size() == 0);
    }

    @Test
    public void error() {
        AResponseEntity res = AResponseEntity.error(new RuntimeException("Oops!"));
        assertNull(res.data);
        assertEquals("Oops!", res.errors.get("error"));
        assertTrue(res.errors.size() == 1);
    }
}