package demo.redis;

import demo.redis.config.AppConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ContextConfiguration(classes = AppConfig.class)
public class TokenTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void blackListTest() {
        /* Redis set 사용 */
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        /* key 값으로 blacklist 설정 */
        String redisKey = "blacklist";
        String token1 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eA";
        String token2 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eB";
        String token3 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eC";

        List<String> list = new LinkedList<>();

        list.add(token1);
        list.add(token2);
//        list.add(token3);

        setOperations.add(redisKey, list.get(0), list.get(1));

        assertEquals(true, checkAllInside(redisKey, setOperations, list));
    }

    @Test
    void refreshTokenTest() {
        /* Redis hash 사용 */
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        /* key 값으로 blacklist 설정 */
        String redisKey = "refreshTokens";
        String username1 = "test1";
        String username2 = "test2";
        String username3 = "test3";
        String token1 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eA";
        String token2 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eB";
        String token3 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eC";

        Map<String, String> map = new HashMap<>();

        map.put(username1, token1);
        map.put(username2, token2);
        map.put(username3, token3);

        hashOperations.putAll(redisKey, map);


        assertEquals(map.get(username3), checkInside(redisKey, hashOperations, username3));
    }

    @Test
    void expireTest() throws InterruptedException {
        /* Redis set 사용 */
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        /* key 값으로 blacklist 설정 */
        String redisKey = "blacklist";
        String token1 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eA";
        String token2 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eB";
        String token3 = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtamxlZSIsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2MzAzNDMzMTd9" +
                ".LxWl65Vg_qRng5Q7h9BYnmgxefqwHhzPaFS5OJlytsiYL2HqDUUwefGd5dsFTAX0vBV3Mi2iUO2agFB5AZh0eC";

        valueOperations.set("token1", token1, 1L, TimeUnit.SECONDS);
        valueOperations.set("token2", token2, 3L, TimeUnit.SECONDS);
        valueOperations.set("token3", token3, 5L, TimeUnit.SECONDS);

        final Long sec = 1000L;

        Thread.sleep(2L * sec);

        Optional<String> token = Optional.ofNullable(valueOperations.get("token2"));
        if (token.isEmpty()) {
            fail();
        } else {
            assertEquals(token2, token.get());
        }

    }

    private boolean checkAllInside(String redisKey, SetOperations<String, String> setOperations, List<String> list) {
        boolean flag = true;
        for (String s : list) {
            System.out.println(s);
            if (!setOperations.isMember(redisKey, s)) {
                flag = false;
            }
        }
        return flag;
    }


    private String checkInside(String key, HashOperations<String, String, String> hashOperations, String hashKey) {
        Optional<String> str = Optional.ofNullable(hashOperations.get(key, hashKey));
        if (str.isEmpty()) {
            return "";
        }
        return str.get();
    }


}
