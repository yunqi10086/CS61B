package gitlet;

import spark.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        REMOVED.mkdirs();
        BRANCHES.mkdirs();
        head = "master";
        writeContents(HEAD, head);
        Commit c = new Commit("initial commit", null);
        File f = join(BRANCHES, "master");

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

        //检查add的文件与父commit里面的是否相同 如果相同就不add(直接return)
        Commit cur_com = getHeadCommit();
        String cur_hash = cur_com.map.get(arg);
        String new_hash = sha1(readContents(f));
        if(new_hash.equals(cur_hash)){
            //如果暂存区有就删掉
            File fff = join(STAGED, arg);
            if(fff.exists()){
                fff.delete();
            }
            return;
        }
        //add
        Blobs.add_file(f);
        StagedFile.add_file(arg, f);
    }

    public static void commit(String arg) throws IOException {
        head = readContentsAsString(HEAD);
        Commit com = new Commit(arg, getHeadHash());
        commitCommit(com);
    }
    private static void commitCommit(Commit c) throws IOException {
        byte[] bc = serialize(c);
        File f = join(COMMITTED_DIR, sha1(bc));
        f.createNewFile();
        writeObject(f, c);

        File ff = join(BRANCHES, head);
        writeContents(ff, sha1(bc));
    }

    public static void rm(String arg) throws IOException {
        boolean inStaged = false;
        File ff = join(STAGED, arg);
        if(ff.exists()){                     //find in Staged
            ff.delete();
            inStaged = true;
        }
        head = readContentsAsString(HEAD);
        Commit com = getHeadCommit();
        String hash = com.map.get(arg);
        if(hash == null && inStaged == false){   //不在head commit里 报错
            System.out.println("No reason to remove the file.");
            exit(0);
        }else if(hash == null && inStaged == true){
            exit(0);
        } else {               //在head commit里 rm
            Removed.removed_file(arg, hash);
            //在工作区删除file
            File f = join(CWD, arg);
            restrictedDelete(f);
        }

    }

    public static void log(){
        head = readContentsAsString(HEAD);
        File ff = join(BRANCHES, head);
        String currHash = readContentsAsString(ff);
        while(true){
            File fff = join(COMMITTED_DIR, currHash);
            Commit curr = readObject(fff, Commit.class);

            help_print_log(curr, currHash);
            List<String> par = curr.get_parent();

            //已print首次提交，退出
            if(par == null){
                return;
            }

            //更新currHash
            currHash = par.get(0);
        }
    }
    public static void global_log(){
        List<String> list = plainFilenamesIn(COMMITTED_DIR);
        if(list == null){
            return;
        }
        for(String s : list){
            File f = join(COMMITTED_DIR, s);
            Commit curr = readObject(f, Commit.class);
            help_print_log(curr, s);
        }
    }
    private static void help_print_log(Commit curr, String hash){
        System.out.println("===");
        System.out.println("commit " + hash);
        List<String> par = curr.get_parent();
        if(par != null && par.size()>1){
            System.out.print("Merge:");
            for(String a : par){
                System.out.print(" " + a.substring(0,7));
            }
            System.out.println();
        }

        System.out.println("Date: " + curr.get_time());
        System.out.println(curr.get_message());
        System.out.println();
    }

    public static void find(String message){
        List<String> list = plainFilenamesIn(COMMITTED_DIR);
        if(list == null){
            System.out.println("Found no commit with that message.");
            return;
        }
        boolean have_find = false;
        for(String s : list){
            File f = join(COMMITTED_DIR, s);
            Commit curr = readObject(f, Commit.class);
            if(curr.get_message().equals(message)){
                System.out.println(s);
                have_find = true;
            }
        }
        if(have_find == false){
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status(){
        System.out.println("=== Branches ===");
        help_print_status_branch();
        System.out.println();

        System.out.println("=== Staged Files ===");
        help_print_status_staged();
        System.out.println();

        System.out.println("=== Removed Files ===");
        help_print_status_removed();
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        help_print_status_modifications();
        System.out.println();

        System.out.println("=== Untracked Files ===");
        help_print_status_untracked();
        System.out.println();
    }
    private static void help_print_status_branch(){
        head = readContentsAsString(HEAD);
        List<String> list = plainFilenamesIn(BRANCHES);
        if(list == null){
            return;
        }list.sort(Comparator.naturalOrder());
        for(String s : list){
            if(s.equals(head)){
                System.out.print("*");
            }
            System.out.println(s);
        }
    }
    private static void help_print_status_staged(){
        List<String> list = plainFilenamesIn(STAGED);
        if(list == null){
            return;
        }
        list.sort(Comparator.naturalOrder());
        for(String s : list){
            System.out.println(s);
        }
    }
    private static void help_print_status_removed(){
        List<String> list = plainFilenamesIn(REMOVED);
        if(list == null){
            return;
        }
        list.sort(Comparator.naturalOrder());
        for(String s : list){
            System.out.println(s);
        }
    }
    private static void help_print_status_modifications(){
        //to be continued
    }
    private static void help_print_status_untracked(){
        //to be continued
    }

    public static void checkout(String[] args) throws IOException {
        if(args.length == 3 && args[1].equals("--")){       //java gitlet.Main checkout -- [file name]
            checkout_v1(args[2]);
        }else if(args.length == 4 && args[2].equals("--")){  //java gitlet.Main checkout [commit id] -- [file name]
            checkout_v2(args[1], args[3]);
        }else if(args.length == 2){
            checkout_v3(args[1]);       //java gitlet.Main checkout [branch name]
        }else{
            System.out.println("error");
            return;
        }
    }
    private static void checkout_v1(String filename){
        help_checkout(getHeadHash(), filename);
    }
    private static void checkout_v2(String commit_id, String filename){
        List<String> ls = plainFilenamesIn(COMMITTED_DIR);
        for(String s : ls){
            if(s.startsWith(commit_id)){
                help_checkout(s, filename);
                exit(0);
            }
        }
        System.out.println("No commit with that id exists.");
        exit(0);
    }
    private static void checkout_v3(String branch_name) throws IOException {
        head = readContentsAsString(HEAD);
        File des = join(BRANCHES, branch_name);
        if(!des.exists()){
            System.out.println("No such branch exists.");
            return;
        }
        if(head.equals(branch_name)){
            System.out.println("No need to checkout the current branch.");
            return;
        }
        List<String> list1 = plainFilenamesIn(CWD);     //工作目录下的文件名的list
        Commit curr = getHeadCommit();
        String des_hash = readContentsAsString(des);
        Commit des_com = readObject(join(COMMITTED_DIR, des_hash), Commit.class);
        Set<String> list2 = des_com.map.keySet();     //待跳转的commit目录下的文件名的list
        if (list1 != null) {    //dangerous
            for(String s : list1){
                boolean is_tracked = (curr.map.get(s) != null);
                boolean is_staged = join(STAGED, s).exists();
                boolean is_untracked = !is_tracked && !is_staged;
                if(is_untracked && des_com.map.get(s) != null){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            }
        }
        //开始复制
        for(String s : list2){
            File f = join(CWD, s);
            if(!f.exists()){
                f.createNewFile();
            }
            String des_file_hash = des_com.map.get(s);
            File des_file = join(BLOBS, des_file_hash);
            byte[] by = readContents(des_file);
            writeContents(f, by);
        }
        //任何在当前分支中被追踪（Tracked）、但在目标分支中不存在的文件都将被删除
        if (list1 != null) {
            for(String s : list1){
                if(curr.map.get(s) != null && des_com.map.get(s) == null){
                    File f = join(CWD, s);
                    restrictedDelete(f);
                }
            }
        }

        if(!head.equals(branch_name)){
            //清空缓存区
            List<String> l = plainFilenamesIn(STAGED);
            if (l != null) {
                for(String s : l){
                    File ff = join(STAGED, s);
                    restrictedDelete(ff);
                }
            }
            List<String> ll = plainFilenamesIn(REMOVED);
            if (ll != null) {
                for(String s : ll){
                    File ff = join(REMOVED, s);
                    restrictedDelete(ff);
                }
            }

            //更新head
            head = branch_name;
            writeContents(HEAD, head);
        }

    }
    private static void help_checkout(String commit_id, String filename){
        File f = join(COMMITTED_DIR, commit_id);
        if(!f.exists()){    //commit_id don't exist
            System.out.println("No commit with that id exists");
            exit(0);
        }else{
            Commit com = readObject(f, Commit.class);
            String hash = com.map.get(filename);
            if(hash == null){   //commit_id exist but filename don't exist
                System.out.println("File does not exist in that commit");
                exit(0);
            }else{
                File ff = join(BLOBS, hash);
                File curf = join(CWD, filename);
                byte[] by = readContents(ff);
                writeContents(curf, by);
            }
        }
    }

    public static void branch(String branch_name) throws IOException {
        File f = join(BRANCHES, branch_name);
        if(f.exists()){
            System.out.println("A branch with that name already exists.");
            return;
        }
        f.createNewFile();
        writeContents(f, getHeadHash());
    }

    public static void rm_branch(String branch_name){
        File f = join(BRANCHES, branch_name);
        if(!f.exists()){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        head = readContentsAsString(HEAD);
        if(branch_name.equals(head)){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        restrictedDelete(f);
    }

    public static void reset(String commit_id) throws IOException {
        List<String> ls = plainFilenamesIn(COMMITTED_DIR);
        for(String s : ls){
            if(s.startsWith(commit_id)){
                help_reset(s);
                exit(0);
            }
        }
        System.out.println("No commit with that id exists.");
        return;
    }
    private static void help_reset(String commit_id) throws IOException {
        head = readContentsAsString(HEAD);
        File fff = join(COMMITTED_DIR, commit_id);
        if(!fff.exists()){
            System.out.println("No commit with that id exists.");
            return;
        }
        List<String> list1 = plainFilenamesIn(CWD);     //工作目录下的文件名的list
        Commit curr = getHeadCommit();
        Commit des_com = readObject(fff, Commit.class);
        Set<String> list2 = des_com.map.keySet();     //待跳转的commit目录下的文件名的list
        if (list1 != null) {    //dangerous
            for(String s : list1){
                boolean is_tracked = (curr.map.get(s) != null);
                boolean is_staged = join(STAGED, s).exists();
                boolean is_untracked = !is_tracked && !is_staged;
                if(is_untracked && des_com.map.get(s) != null){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            }
        }
        //开始复制
        for(String s : list2){
            File f = join(CWD, s);
            if(!f.exists()){
                f.createNewFile();
            }
            String des_file_hash = des_com.map.get(s);
            File des_file = join(BLOBS, des_file_hash);
            byte[] by = readContents(des_file);
            writeContents(f, by);
        }
        //任何在当前分支中被追踪（Tracked）、但在目标分支中不存在的文件都将被删除
        if (list1 != null) {
            for(String s : list1){
                if(curr.map.get(s) != null && des_com.map.get(s) == null){
                    File f = join(CWD, s);
                    restrictedDelete(f);
                }
            }
        }

        //清空缓存区
        List<String> l = plainFilenamesIn(STAGED);
        if (l != null) {
            for (String s : l) {
                File ff = join(STAGED, s);
                restrictedDelete(ff);
            }
        }
        List<String> ll = plainFilenamesIn(REMOVED);
        if (ll != null) {
            for (String s : ll) {
                File ff = join(REMOVED, s);
                restrictedDelete(ff);
            }
        }

        //更新：当前分支的指针 指向 目的分支
        String curr_branch = head;
        File fi = join(BRANCHES, curr_branch);
        writeContents(fi, commit_id);
    }

    public static void merge(String branch_name) throws IOException {
        //If there are staged additions or removals present, exit.
        List<String> l1 = plainFilenamesIn(STAGED);
        List<String> l2 = plainFilenamesIn(REMOVED);
        if((l1 != null && !l1.isEmpty()) || (l2 != null && !l2.isEmpty())){
            System.out.println("You have uncommitted changes.");
            exit(0);
        }
        //If a branch with the given name does not exist
        File fe = join(BRANCHES, branch_name);
        if(!fe.exists()){
            System.out.println("A branch with that name does not exist.");
            exit(0);
        }
        //If attempting to merge a branch with itself, print the error message Cannot merge a branch with itself.
        head = readContentsAsString(HEAD);
        if(branch_name.equals(head)){
            System.out.println("Cannot merge a branch with itself.");
            exit(0);
        }
        //If an untracked file in the current commit would be overwritten or deleted by the merge, exit
        // and print error message
        File des = join(BRANCHES, branch_name);
        List<String> list1 = plainFilenamesIn(CWD);     //工作目录下的文件名的list
        Commit curr = getHeadCommit();
        String des_hash = readContentsAsString(des);
        Commit des_com = readObject(join(COMMITTED_DIR, des_hash), Commit.class);
        if (list1 != null) {    //dangerous
            for(String s : list1){
                boolean is_tracked = (curr.map.get(s) != null);
                boolean is_staged = join(STAGED, s).exists();
                boolean is_untracked = !is_tracked && !is_staged;
                if(is_untracked && des_com.map.get(s) != null){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            }
        }


        File ff = join(BRANCHES, head);
        String head_hash = readContentsAsString(ff);
        File f = join(BRANCHES, branch_name);
        String hash_of_giving_branch = readContentsAsString(f);
        String split_point_hash = get_split_point_hash(branch_name);
        if(hash_of_giving_branch.equals(split_point_hash)){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if(head_hash.equals(split_point_hash)){
            help_reset(hash_of_giving_branch);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        File fsp = join(COMMITTED_DIR, split_point_hash);
        Commit split_point = readObject(fsp, Commit.class);
        Commit curr_com = getHeadCommit();
        File fgc = join(COMMITTED_DIR, hash_of_giving_branch);
        Commit giving_com = readObject(fgc, Commit.class);

        //1.since the split point,any files that have been modified in the given branch
        //  but not modified in the current branch
        //  should be changed to their versions in the given branch
        //5.Any files that were not present at the split point
        //  and are present only in the given branch should be checked out and staged.
        //6.Any files present at the split point, unmodified in the current branch,
        // and absent in the given branch should be removed (and untracked).
        HashSet<String> set = new HashSet<>();
        set.addAll(split_point.map.keySet());
        set.addAll(curr_com.map.keySet());
        set.addAll(giving_com.map.keySet());
        boolean has_confilct = false;

        for(String s : set){
            String hash_sp = split_point.map.get(s);
            String hash_cc = curr_com.map.get(s);
            String hash_gc = giving_com.map.get(s);
            if(!Objects.equals(hash_gc, hash_sp) && Objects.equals(hash_cc, hash_sp) && hash_gc != null){ // 1. and 5.
                File file = join(BLOBS, hash_gc);
                File f1 = join(CWD, s);
                writeContents(f1, readContents(file));
                add(s);
                continue;
            }
            if(hash_sp != null && Objects.equals(hash_cc, hash_sp) && hash_gc == null){ //6.
                rm(s);
                continue;
            }


            //8.1 the contents of both are changed and different from other
            //8.2 the contents of one are changed and the other file is deleted
            //8.3 the file was absent at the split point and has different contents in the given and current branches
            boolean is_conflict = !Objects.equals(hash_cc, hash_sp) &&
                                  !Objects.equals(hash_gc, hash_sp) &&
                                  !Objects.equals(hash_cc, hash_gc);
            if(is_conflict){
                has_confilct = true;

                String gc = "";
                if(hash_gc != null){
                    File f_gc = join(BLOBS, hash_gc);
                    if(f_gc.exists()) gc = readContentsAsString(f_gc);
                }
                String cc = "";
                if(hash_cc != null){
                    File f_cc = join(BLOBS, hash_cc);
                    if(f_cc.exists()) cc = readContentsAsString(f_cc);
                }

                StringBuilder ans = new StringBuilder("<<<<<<< HEAD\n");
                ans.append(cc);
                ans.append("=======\n");
                ans.append(gc);
                ans.append(">>>>>>>\n");

                File ffff = join(CWD, s);
                writeContents(ffff, ans.toString());
                add(s);
            }
        }

        //生成并提交commit
        Commit c = new Commit("Merged " + branch_name + " into " + head + ".", head_hash, hash_of_giving_branch);
        commitCommit(c);
        if(has_confilct){
            System.out.println("Encountered a merge conflict.");
        }


    }
    private static String get_split_point_hash(String branch_name){
        Queue<String> q = new LinkedList<>(); //q存放head commit hash and it's parents hash
        Set<String> s = new HashSet<>();
        q.add(getHeadHash());
        Set<String> visited1 = new HashSet<>();
        visited1.add(getHeadHash());
        while(!q.isEmpty()){    //把head的所有父commit的hash都存到Set<String> s里面
            String first = q.poll();
            s.add(first);
            File f = join(COMMITTED_DIR, first);
            Commit com = readObject(f, Commit.class);
            if(com.get_parent() != null){
                for(String st : com.get_parent()){
                    if(!visited1.contains(st)){
                        visited1.add(st);
                        q.add(st);
                    }
                }
            }
        }

        File ff = join(BRANCHES, branch_name);
        String hash_of_giving_branch = readContentsAsString(ff);
        Queue<String> q2 = new LinkedList<>();  //q2存放giving_commit hash and it's parents hash
        q2.add(hash_of_giving_branch);
        Set<String> visited = new HashSet<>();
        visited.add(hash_of_giving_branch);
        while(!q2.isEmpty()){                   //遍历比较父提交
            String first = q2.poll();
            if (s.contains(first)) {
                return first;
            }
            File f = join(COMMITTED_DIR, first);
            Commit com = readObject(f, Commit.class);
            if(com.get_parent() != null){
                for(String st : com.get_parent()){
                    if(!visited.contains(st)){
                        visited.add(st);
                        q2.add(st);
                    }
                }
            }
        }
        return null;
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

    public static final File REMOVED = join(GITLET_DIR, "removed");

    public static final File BRANCHES = join(GITLET_DIR, "branches");

    public static String head;

    private static Commit getHeadCommit(){
        String hash = getHeadHash();
        File f = join(COMMITTED_DIR, hash);
        return readObject(f, Commit.class);
    }

    private static String getHeadHash(){
        head = readContentsAsString(HEAD);
        File ff = join(BRANCHES, head);
        return readContentsAsString(ff);
    }

}
