package cn.linkapp.lfs.fte;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileChannelCreator {
    private static FileChannelCreator ins = new FileChannelCreator();
    private FileChannelCreator() {}

    public static FileChannelCreator getInstance() {
        return ins;
    }

    public FileChannel open(String type, String path) {
        return this.open(type, path, null);
    }

    public FileChannel open(String type, String path, String token) {
        if ("FILE".equals(type)) {
            try {
                File file = new File("path");
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                if (file.exists()) {
                    file.delete();
                }

                file.createNewFile();
                FileOutputStream os = new FileOutputStream(file);

                return os.getChannel();
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}
