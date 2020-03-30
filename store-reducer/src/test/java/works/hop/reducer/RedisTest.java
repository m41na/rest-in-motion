package works.hop.reducer;

import redis.clients.jedis.Jedis;

public class RedisTest {

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.setex("events/city/rome", 5, "32,15,223,828");
        String cachedResponse = jedis.get("events/city/rome");
        System.out.println(cachedResponse);
    }
}