package gitlet;

import net.sf.saxon.expr.instruct.Block;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Utils.*;
import static java.lang.System.exit;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    private Repository() throws IOException {
        GITLET_DIR.mkdirs();
        COMMITTED_DIR.mkdirs();
        BLOBS.mkdirs();
        STAGED.mkdirs();
        HEAD.createNewFile();
        MASTER.createNewFile();
        REMOVED.mkdirs();
        Commit c = new Commit("initial commit", null);

        //commit
        commitCommit(c);
    }

    public static void init() throws IOException {
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            exit(0);
        }
        else{
            new Repository();
        }
    }

    public static void add(String arg) throws IOException {
        File f = join(CWD, arg);
        if(!f.exists()){
            System.out.println("File does not exist.");
            exit(0);
        }
        //从removed文件夹中删除该文件
        File ff = join(REMOVED, arg);
        if(ff.exists()){
            ff.delete();
        }

        Blobs.add_file(f);
        StagedFile.add_file(arg, f);
    }

    public static void commit(String arg) throws IOException {
        head = readContentsAsString(HEAD);
        Commit com = new Commit(arg, head);

        commitCommit(com);
    }

    public static void rm(String arg) throws IOException {
        List<String> l = plainFilenamesIn(STAGED);
        boolean inStaged = false;
        if(l != null){
            for (String s : l) {            //find in Staged
                if (s.equals(arg)) {
                    File f = join(STAGED, arg);
                    f.delete();
                    inStaged = true;
                    break;
                }
            }
        }
        head = readContentsAsString(HEAD);
        Commit com = getHeadCommit();
        String hash = com.map.get(arg);
        if(hash == null && inStaged == false){   //不在head commit里 报错
            System.out.println("No reason to remove the file.");
            exit(0);
        } else{               //在head commit里 rm
            Removed.removed_file(arg, hash);
            //在工作区删除file
            File f = join(CWD, arg);
            restrictedDelete(f);
        }

    }

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public static final File COMMITTED_DIR = join(GITLET_DIR, "committed");

    public static final File STAGED = join(GITLET_DIR, "staged");

    public static final File BLOBS = join(GITLET_DIR, "blobs");

    public static final File HEAD = join(GITLET_DIR, "head");

    public static final File MASTER = join(GITLET_DIR, "master");

    public static final File REMOVED = join(GITLET_DIR, "removed");

    public static String head;

    public static String master;

    private static Commit getHeadCommit(){
        head = readContentsAsString(HEAD);
        File ff = join(COMMITTED_DIR, head);
        Commit com = readObject(ff, Commit.class);
        return com;
    }
    private static Commit getMasterCommit(){
        master = readContentsAsString(MASTER);
        File ff = join(COMMITTED_DIR, master);
        Commit com = readObject(ff, Commit.class);
        return com;
    }
    private static void commitCommit(Commit c) throws IOException {
        byte[] bc = serialize(c);
        File f = join(COMMITTED_DIR, sha1(bc));
        f.createNewFile();
        writeObject(f, c);

        head = sha1(bc);
        writeContents(HEAD, head);
        master = sha1(bc);
        writeContents(MASTER, master);
    }
}
