package cn.linkapp.lfs.fte;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class RedisFileFinderTest {

    @Mock
    private Jedis mockRedis;
    private RedisFileFinder fileFinder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.fileFinder = new RedisFileFinder(mockRedis);
    }

    @Test
    public void testNormal() {
        String json = "{ \"id\":1, \"stored-path\":\"C:\\\\out.txt\", \"size\":10000, \"stored-type\":\"file\"}";

        when(this.mockRedis.hget("lfs-upload", String.valueOf(1))).thenReturn(json);
        FileInfo info = this.fileFinder.findFile(1);

        assertEquals(1, info.getCrc());
        assertEquals("C:\\out.txt", info.getPath());
    }
}
