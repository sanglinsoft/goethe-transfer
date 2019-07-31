package cn.linkapp.lfs.fte;

import java.nio.channels.FileChannel;
import java.util.HashMap;

public class FileManager {
    private static FileManager ins = new FileManager();
    private FileManager() {}

    private HashMap<Integer, FileChannel> fileMap = new HashMap<>();
    private FileFinder finder;

    public static FileManager getInstance() {
        return ins;
    }

    public void setFileFinder(FileFinder finder) {
        this.finder = finder;
    }

    public synchronized FileChannel findFile(int crc) {
        if (this.finder == null) {
            return null;
        }

        FileChannel ch = this.fileMap.get(crc);

        if (ch != null) {
            return ch;
        }

        // 尝试查找
        FileInfo info = this.finder.findFile(crc);
        if (info == null) {
            return null;
        }

        // 打开文件
        ch = FileChannelCreator.getInstance().open(info.getStorage(), info.getPath());

        if (ch != null) {
            this.fileMap.put(crc, ch);
        }

        return ch;
    }

    private synchronized void RemoveFile(int crc) {
        this.fileMap.remove(crc);
    }
}
