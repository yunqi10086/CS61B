package gitlet;

// TODO: any imports you need here

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private List<String> parent = new ArrayList<>();
    private String timestamp;
    public TreeMap<String, String> map;     //from filename to its hash
    public static File COMMITTED_DIR = Repository.COMMITTED_DIR;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, String parent) throws IOException {
        this(message, parent, null);
    }
    public Commit(String message, String parent1, String parent2){
        this.message = message;
        this.map = new TreeMap<>();
        if(parent1 == null){
            this.parent = null;
            this.timestamp = "Thu Jan 1 00:00:00 1970 -0800";
            return;
        }
        List<String> l = plainFilenamesIn(STAGED);
        List<String> ll =plainFilenamesIn(REMOVED);
        if( (l == null || l.isEmpty() )&&( ll == null || ll.isEmpty()) ) { //判断是否有change
            System.out.println("No changes added to the commit.");
            exit(0);
        }
        this.parent = new ArrayList<>();
        if(parent1 != null) this.parent.add(parent1);
        if(parent2 != null) this.parent.add(parent2);
        //init timestamp
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-08:00")); // 强行锁死加州时间
        this.timestamp = formatter.format(new Date());

        //clone map (not just simply '=')
        if(this.parent != null && !this.parent.isEmpty()){
            File f = join(COMMITTED_DIR, this.parent.get(0));
            Commit par = readObject(f, Commit.class);
            this.map.putAll(par.map);
        }
        //replace map( add )
        //List<String> l = plainFilenamesIn(STAGED);
        if(l != null){
            for(String s: l){
                File ff = join(STAGED, s);
                this.map.put(s, readContentsAsString(ff)); //s->filename; readContentsAsString(ff)->hash of file
                ff.delete();
            }
        }
        //replace map( remove )
        //List<String> ll =plainFilenamesIn(REMOVED);
        if(ll != null){
            for(String s: ll){
                File fff = join(REMOVED, s);
                this.map.remove(s);
                fff.delete();
            }
        }
    }

    public List<String> get_parent(){
        return this.parent;
    }

    public String get_time(){
        return this.timestamp;
    }

    public String get_message(){
        return this.message;
    }
}
