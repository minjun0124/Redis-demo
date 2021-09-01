package demo.redis;

import demo.redis.config.AppConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = AppConfig.class)
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void redisConnectionTest() {
        final String key = "a";
        final String data = "1";

        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data);

        final String result = valueOperations.get(key);
        assertEquals(data, result);
    }

    @Test
    void redisSetOperationTest() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        String setKey = "setKey";
        String str1 = "str1";
        String str2 = "str2";
        String str3 = "str3";

        List<String> list = new LinkedList<>();

        list.add(str1);
        list.add(str2);
        list.add(str3);

        setOperations.add(setKey, list.get(0));
        setOperations.add(setKey, list.get(1));
        setOperations.add(setKey, list.get(2));

        assertEquals(true, checkAllInside(setKey, setOperations, list));
    }

    private boolean checkAllInside(String setKey, SetOperations<String, String> setOperations, List<String> list) {
        boolean flag = true;
        for (String s : list) {
            System.out.println(s);
            if (!setOperations.isMember(setKey, s)) {
                flag = false;
            }
        }
        return flag;
    }
}
