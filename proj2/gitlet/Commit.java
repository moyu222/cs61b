package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.join;

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

    /** The commits directory */
    static final File COMMIT_DIR = join(Repository.GITLET_DIR, "objects", "commits");

    /** The  sha-1. name of commit file */
    private String commitName;

    /** The message of this Commit. */
    private String message;

    /** The timestamp of commit */
    private final String timestamp = DateTimeFormatter
            .ofPattern("EEE MMM d HH:mm:ss yyyy Z")
            .withZone(ZoneId.of("UTC"))
            .format(Instant.ofEpochSecond(0));


    /** The parents (max two)  commit file name (hash)
     * use List<String> */
    private List<String> parentHashes;

    /** the parent commit
     * use List<Commit></Commit>*/
    private transient List<Commit> parentCommits;

    /** The hashmap for the tracked file*/
    private HashMap<String, String> blobsRef;



    /** The content of commit:
     * String message
     * Date timestamp
     * Blobs hashmap(file name: reference) if there is a change to
     * one file overwrite the reference. if there is new file add
     * node to hashmap the tree structure
     */

    /** the constructor of commit */
    public Commit() {

    }

    /** get the file name */
    public String getCommitName() {
        return commitName;
    }

    /** get list of parent commits hash */
    public List<String> getParentHashes() {
        return parentHashes;
    }

    /** load parent commit in run time */
    public void loadParentCommits() {
        for (String hash : parentHashes) {
            Commit parent = loadCommitFromHash(hash);
            if (parent != null) {
                parentCommits.add(parent);
            }
        }
    }

    /** load commit from hash */
    private Commit loadCommitFromHash(String hash) {
        return
    }

    /* TODO: fill in the rest of this class. */
}
