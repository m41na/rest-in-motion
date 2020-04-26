package works.hop;

import org.junit.Test;
import works.hop.model.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConverterTest {

    @Test
    public void testParseBooleanValue() {
        Boolean boolValue = (Boolean) Converter.valueParser().apply("false");
        assertFalse(boolValue);
    }

    @Test
    public void testParseCharacterValue() {
        Character charValue = (Character) Converter.valueParser().apply("c");
        assertEquals('c', charValue.charValue());
    }

    @Test
    public void testParseDoubleValue() {
        BigDecimal doubleValue = (BigDecimal) Converter.valueParser().apply("10.94");
        assertEquals(10.94, doubleValue.floatValue(), 0.01);
    }

    @Test
    public void testParseLongValue() {
        BigInteger longValue = (BigInteger) Converter.valueParser().apply("0");
        assertEquals(0, longValue.intValue());
    }

    @Test
    public void testParseUndefinedValue() {
        Object value = Converter.valueParser().apply("undefined");
        assertNull(value);
    }

    @Test
    public void testParseInvalidValue() {
        String value = (String) Converter.valueParser().apply("0y");
        assertEquals("0y", value);
    }

    @Test
    public void testParseProfileJson() {
        Map<String, Object> map = (Map) Converter.parseJson("/schema/profile.json");
        assertNotNull(map);
        assertEquals("Expecting 11", 11, map.size());
    }

    @Test
    public void testParseTodoListJson() {
        List<Object> list = (List) Converter.parseJson("/schema/todos.json");
        assertNotNull(list);
        assertEquals("Expecting 2", 2, list.size());
    }
}
