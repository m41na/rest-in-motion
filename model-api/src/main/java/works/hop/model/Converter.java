package works.hop.model;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Converter {
    private static Function<String, Object> converter = valueParser();

    public static <T> Function<String, T> valueParser() {
        Pattern booleanPattern = Pattern.compile("^(true|false)$", Pattern.CASE_INSENSITIVE);
        Pattern characterPattern = Pattern.compile("^[a-zA-Z]$");
        Pattern bigDecimalPattern = Pattern.compile("^[0-9]+?(\\.[0-9]+)$");
        Pattern bigIntegerPattern = Pattern.compile("^[0-9]+$");
        Pattern undefinedPattern = Pattern.compile("^(null|undefined)$");
        return (String input) -> {
            if (booleanPattern.matcher(input).matches()) {
                return (T) Boolean.valueOf(input);
            }
            if (characterPattern.matcher(input).matches()) {
                return (T) Character.valueOf(input.charAt(0));
            }
            if (bigDecimalPattern.matcher(input).matches()) {
                return (T) new BigDecimal(input);
            }
            if (bigIntegerPattern.matcher(input).matches()) {
                return (T) new BigInteger(input);
            }
            if (undefinedPattern.matcher(input).matches()) {
                return null;
            }
            return (T) input;
        };
    }

    public static Object parseJson(String source) {
        JsonParser parser = Json.createParser(Converter.class.getResourceAsStream(source));
        while (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            switch (e) {
                case START_OBJECT:
                    //System.out.println("starting object");
                    return parseObject(parser);
                case START_ARRAY:
                    //System.out.println("starting array");
                    return parseList(parser);
                default:
                    break;
            }
        }
        return null;
    }

    public static Map<String, Object> parseObject(JsonParser parser) {
        Map<String, Object> result = new HashMap<>();
        String key = null, value;
        while (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            switch (e) {
                case START_OBJECT:
                    //System.out.println("starting object");
                    Map<String, Object> object = parseObject(parser);
                    result.put(key, object);
                    break;
                case END_OBJECT:
                    //System.out.println("ending object");
                    return result;
                case KEY_NAME:
                    //System.out.println("key name");
                    key = parser.getString();
                    //System.out.println(key);
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    //System.out.println("key value");
                    value = parser.getString();
                    //System.out.println(value);
                    if (result != null) {
                        result.put(key, converter.apply(value));
                        break;
                    }
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_FALSE:
                    //System.out.println("key value");
                    value = parser.getValue().toString();
                    //System.out.println(value);
                    result.put(key, converter.apply(value));
                    break;
                case START_ARRAY:
                    //System.out.println("starting array");
                    List<Object> list = parseList(parser);
                    result.put(key, list);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public static List<Object> parseList(JsonParser parser) {
        List<Object> result = new ArrayList<>();
        String key, value;
        while (parser.hasNext()) {
            JsonParser.Event e = parser.next();
            switch (e) {
                case START_OBJECT:
                    //System.out.println("starting object");
                    Map<String, Object> object = parseObject(parser);
                    result.add(object);
                    break;
                case KEY_NAME:
                    //System.out.println("key name");
                    key = parser.getString();
                    //System.out.println(key);
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    //System.out.println("key value");
                    value = parser.getString();
                    //System.out.println(value);
                    if (result != null) {
                        result.add(converter.apply(value));
                        break;
                    }
                    //System.out.println(value);
                    break;
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_FALSE:
                    //System.out.println("key value");
                    value = parser.getValue().toString();
                    //System.out.println(value);
                    if (result != null) {
                        result.add(converter.apply(value));
                        break;
                    }
                case START_ARRAY:
                    //System.out.println("starting array");
                    List<Object> list = parseList(parser);
                    result.add(list);
                    break;
                case END_ARRAY:
                    //System.out.println("ending array");
                    return result;
                default:
                    break;
            }
        }
        return result;
    }
}
