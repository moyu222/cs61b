package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.join;

/** Represents a gitlet commit object.

 *  does at a high level.
 *  commits has parent commit (at most two), and load them in the runtime
 *  in the commit file:
 *  file name is the hash value of the commit object
 *
 *  commit object
 *  List<Commit></Commit> parentCommit
 *  List<String></String> parentHash
 *  String hash - file name
 *  String message - commmit description
 *  String timestamp - commit time
 *  HashMap blobRef (the name of work file, the name of corresponding blob name)
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /** List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The commits directory */
    static final File COMMIT_DIR = join(Repository.GITLET_DIR, "objects", "commits");

    /** The message of this Commit. */
    private final String message;

    /** The timestamp of commit */
    private final String timestamp;

    /** The parents (max two)  commit file name (hash)
     * use List<String> */
    private final List<String> parentHashes;

    /** the parent commit
     * use List<Commit></Commit>*/
    private transient List<Commit> parentCommits;

    /** The hashmap for the tracked file*/
    private final TreeMap<String, String> blobsRef;

    /** the hash of commit */
    private transient String hash;

    /** The content of commit:
     * String message
     * Date timestamp
     * List<String></String> parent hash
     * blobsRef from parent same
     */

    /** the constructor of commit */
    public Commit(String message, List<String> parentHashes) {
        this.message = message;
        this.parentHashes = parentHashes != null ? parentHashes : Collections.emptyList();
        if (this.parentHashes.isEmpty()) {
            this.timestamp = "Thu Jan 1 00:00:00 1970 +0000";
            this.blobsRef = new TreeMap<>();
        } else {
            this.timestamp = getCurrentTimestamp();
            this.blobsRef = loadBlobsFromParent(this.parentHashes);
        }
    }

    public Commit(String message, String parentHash) {
        this.message = message;
        // an empty set that can not modify collections.emptyList() keep invariant
        List<String> parentHashesX = new ArrayList<>();
        parentHashesX.add(parentHash);
        this.parentHashes = parentHashesX;
        this.timestamp = getCurrentTimestamp();
        this.blobsRef = loadBlobsFromParent(parentHashes);
    }


    /** get the current timestamp */
    public static String getCurrentTimestamp() {
        Date now = new Date();
        Formatter formatter = new Formatter(Locale.ENGLISH);
        formatter.format("%ta %tb %td %tT %tY %tz", now, now, now, now, now, now);
        return formatter.toString();
    }


    /** compute the file name use this after compare to the index*/
    public void computeHash() {
        if (this.hash != null) {
            return;
        }
        this.hash = Utils.sha1(Utils.serialize(this));
    }

    /** get the hash of the commit */
    public String getHash() {
        if (this.hash == null) {
            computeHash();
        }
        return this.hash;
    }

    /** load the blobsRef from parent hash
     *
     * hash -> object (commit) -> treemap
     */
    private TreeMap<String, String> loadBlobsFromParent(List<String> yParentHashes) {
        String firstParentHash = yParentHashes.get(0);
        Commit firstParent = loadCommitFromHash(firstParentHash);
        return new TreeMap<>(firstParent.getBlobsRef());
    }

    /** get list of parent commits hash */
    public List<String> getParentHashes() {
        return parentHashes;
    }

    /** load parent commit in run time */
    public void loadParentCommits() {
        parentCommits = new ArrayList<>();

        if (parentHashes.isEmpty()) {
            return;
        }

        for (String xHash : parentHashes) {
            Commit parent = loadCommitFromHash(xHash);
            if (parent != null) {
                parentCommits.add(parent);
            }
        }
    }

    /** after load, get the first parent of the commit */
    public Commit getFirstParentCommit() {
        return (parentCommits != null && !parentCommits.isEmpty()) ? parentCommits.get(0) : null;
    }

    /** compare with index to determine the blobsRef and return if blobsRef
     * been changed
     * Assume there is index file */
    public boolean deterBlobsRef(HashMap<String, String> stagingMap) {

        TreeMap<String, String> oldBlobsRef = new TreeMap<>(blobsRef);

        for (Map.Entry<String, String> entry : stagingMap.entrySet()) {
            String filePath = entry.getKey();
            String blobHash = entry.getValue();
            blobsRef.put(filePath, blobHash);
        }
        return !oldBlobsRef.equals(blobsRef);
    }

    /** remove file path and blob hash in the blobsRef
     *  Assume the file already in the commit
     *  return if changed */
    public boolean removeBlobsRef(HashSet<String> removedFiles) {

        HashMap<String, String> oldBlobsRef = new HashMap<>(blobsRef);
        for (String file : removedFiles) {
            blobsRef.remove(file);
        }
        return !oldBlobsRef.equals(blobsRef);
    }

    /** load commit from hash */
    public static Commit loadCommitFromHash(String hash) {
        File commitFile = join(COMMIT_DIR, hash);
        Commit returnCommit = Utils.readObject(commitFile, Commit.class);
        returnCommit.hash = hash;
        return returnCommit;
    }

    /** get blobsRef for next commit */
    public TreeMap<String, String> getBlobsRef() {
        return blobsRef;
    }

    /** get String format when gitlet log
     * Assume that the commit has already computed the hash */
    public void printLog() {
        StringBuilder log = new StringBuilder();
        log.append("===\n");
        log.append("commit ").append(getHash()).append("\n");

        if (parentHashes.size() > 1) {
            log.append("Merge: ")
                    .append(parentHashes.get(0), 0, 7).append(" ")
                    .append(parentHashes.get(1), 0, 7).append("\n");
        }

        log.append("Date: ").append(timestamp).append("\n");
        log.append(message).append("\n");
        System.out.println(log);
    }


    /** save the commit file
     * Assume the commit has determined the blobsRef
     * after deterBlobsRef and get hash */
    public void saveCommit(String yHash) {
        File filePath = join(COMMIT_DIR, yHash);
        Utils.writeObject(filePath, this);
    }

    /** get message */
    public String getMessage() {
        return this.message;
    }

    /** Finds the full commit ID that starts with the given prefix */
    public static String findFullCommitId(String prefix) {
        List<String> commitList = Utils.plainFilenamesIn(Commit.COMMIT_DIR);
        if (!(commitList == null || commitList.isEmpty())) {
            for (String commitId: commitList) {
                if (commitId.startsWith(prefix)) {
                    return commitId;
                }
            }
        }
        return null;
    }

}
