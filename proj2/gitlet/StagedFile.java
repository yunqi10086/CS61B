package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.*;

public class StagedFile {

     public static final File STAGED = Repository.STAGED;

     //add file f to Staged 以f为文件名， f的内容的哈希值 为 文件内容
     public static void add_file(String filename, File f) throws IOException {
         File ff = join(STAGED, filename);
         if(!ff.exists()){
             ff.createNewFile();
         }
         String hash = sha1(readContents(f));
         writeContents(ff, hash);
     }

}
