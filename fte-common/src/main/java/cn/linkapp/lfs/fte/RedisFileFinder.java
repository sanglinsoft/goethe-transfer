package cn.linkapp.lfs.fte;

import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

/**
 * 基于redis的文件信息查找
 */
public class RedisFileFinder implements FileFinder{
    private Jedis jedis;

    public RedisFileFinder(String host, int port) {
        this(new Jedis(host, port));
    }

    public RedisFileFinder(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public FileInfo findFile(int crc) {
        if (this.jedis == null) {
            return null;
        }

        String json = this.jedis.hget("lfs-upload", String.valueOf(crc));
        return (json == null) ? null : getFileInfo(json);
    }

    private FileInfo getFileInfo(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return new FileInfo(
                    object.getInt("id"),
                    object.getString("stored-path"),
                    object.getLong("size"),
                    object.getString("stored-type"));
        } catch (JSONException e){
            return null;
        }
    }
}
