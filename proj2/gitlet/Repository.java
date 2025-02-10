package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import static gitlet.Utils.*;

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

    /** the hashset of tracking the boldID in the bolds folder*/
    public static HashSet<String> blobSet = new HashSet<>();

    /** current commit for comparing staging area */
    public static Commit currCommit;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** the heads directory */
    public static final File HEADS_DIR = join(GITLET_DIR, "refs", "heads");

    /** the Head file path, record the path of head refs for each branch */
    public static final File HEAD = join(GITLET_DIR, "HEAD");



    /* TODO: fill in the rest of this class. */

    /** init the repository */
    public static void init() {

        if (!checkStructure()) {
            GITLET_DIR.mkdir();
            Blob.BLOBS_DIR.mkdirs();
            Commit.COMMIT_DIR.mkdirs();
            HEADS_DIR.mkdirs();

        }




    }

    /** check the structure of the repository
     * if the structure is right return true
     * else return false*/
    public static boolean checkStructure() {
        return GITLET_DIR.exists() && Commit.COMMIT_DIR.exists() &&
                Blob.BLOBS_DIR.exists() && HEADS_DIR.exists();
    }

    /**
     * 1. sha(file) - blobID use untils methods
     * 2. save blobID in blobs folder if it is new
     *      use a hashset to track the blobID in the runtime time lgN
     * 3. save new blobID(create or modify) and file name in the staging area
     *      and check if it is identical to the version in the current commit
     */
    public static void add(String file) {
        // use util to create a list of file and check
        if (file not exist) {
            System.out.println("File does not exist");
            System.exit(0);
        }

        // clear the staging area

        String blobID = sha1();
        if (!blobSet.contains(blobID)) {
            blobSet.add(blobID);
            // blobs folder add file named the first two bit of hash and
            // store 40 bits
        }

        // check if is identical, use hashmap in the commit
        if (commit.tree.contain(blodID)) {
            clear staging folders
        }


    }


}
