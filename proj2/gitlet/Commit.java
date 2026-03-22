package gitlet;

// TODO: any imports you need here

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static gitlet.Repository.REMOVED;
import static gitlet.Repository.STAGED;
import static gitlet.Utils.*;
import static gitlet.Utils.readContentsAsString;
import static gitlet.Utils.sha1;
import static java.lang.System.exit;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String parent;
    private String timestamp;
    public HashMap<String, String> map;     //from filename to its hash
    public static File COMMITTED_DIR = Repository.COMMITTED_DIR;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, String parent) throws IOException {
        this.message = message;
        this.parent = parent;
        if(parent == null){
            this.map = new HashMap<>();
           this.timestamp = "00:00:00 UTC, Thursday, 1 January 1970";
        }else{
            //init timestamp ----------------------------------format error
            Date date = new Date();
            this.timestamp = date.toString();

            //clone map (not just simply '=')
            File f = join(COMMITTED_DIR, this.parent);
            Commit par = readObject(f, Commit.class);
            this.map = new HashMap<>(par.map);

            //replace map( add )
            List<String> l = plainFilenamesIn(STAGED);
            if(l != null){
                for(String s: l){
                    File ff = join(STAGED, s);
                    this.map.put(s, readContentsAsString(ff)); //s->filename; readContentsAsString(ff)->hash of file
                    ff.delete();
                }
            }
            //replace map( remove )
            List<String> ll =plainFilenamesIn(REMOVED);
            if(ll != null){
                for(String s: ll){
                    File fff = join(REMOVED, s);
                    this.map.remove(s);
                    fff.delete();
                }
            }

            if( (l == null || l.isEmpty() )&&( ll == null || ll.isEmpty()) ) {
                System.out.println("No changes added to the commit");
                exit(0);
            }
        }
    }


}
