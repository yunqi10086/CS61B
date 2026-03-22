package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

public class Blobs {
    //以哈希值为文件名 文件的String为内容

    public static File BLOBS = Repository.BLOBS;

    public static void add_file(File f) throws IOException {
        byte[] bb = readContents(f); //读文件为二进制字节
        String hash = sha1(bb);
        File ff = join(BLOBS, hash);
        ff.createNewFile();
        writeContents(ff, bb); //写文件
    }

}
