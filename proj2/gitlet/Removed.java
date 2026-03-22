package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.join;
import static gitlet.Utils.writeContents;

public class Removed {
    public static File REMOVED = Repository.REMOVED;

    // 以f为文件名， f的内容的哈希值 为 文件内容
    public static void removed_file(String filename, String hash) throws IOException {
        File f = join(REMOVED, filename);
        if(!f.exists()){
            f.createNewFile();
        }
        writeContents(f, hash);
    }
}
