package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static gitlet.Utils.join;

/** the class Index is a helper to build staging area index file
 * in the staging area:
 * file name, blob.hash as hashmap? since gitlet consider plain files
 *      use file path instead of name since in the commit, we use path: hash\
 *      since plain file fileName is relative path
 *
 * function to be achieved
 * write a index file
 * read from index file
 *
 * Index work as an object to store many blobs map in hashmap so we
 * serialize it
 *
 * @author zhangyangzonghan
 */
public class Index implements Serializable {

    /** the index file path staging area */
    public static final File INDEX_FILE = join(Repository.GITLET_DIR, "index");

    /** the hashmap for path and hash */
    private final HashMap<String, String> stagedFiles;

    /** the hashset for rm command */
    private final HashSet<String> removedFiles;

    /** Constructor */
    public Index() {
        this.stagedFiles = new HashMap<>();
        this.removedFiles = new HashSet<>();
    }

    /** Add a file in to the index (staging area) in gitlet one file once */
    public void add(String filePath, String blobHash) {
        stagedFiles.put(filePath, blobHash);
    }

    /** Remove element in the stagedFile from the index
     *  not marked to remove*/
    public void removeStaged(String filePath) {
        stagedFiles.remove(filePath);
    }

    /** mark the file need to be removed
     */
    public void markForRemoval (String filePath) {
        removedFiles.add(filePath);
    }


    /** Save the index to a file */
    public void save() {
        Utils.writeObject(INDEX_FILE, this);
    }

    /** Read the index file and get the index object */
    public static Index getIndex() {
        return Utils.readObject(INDEX_FILE, Index.class);
    }

    /** From the index from file get String information */
    public static List<String> Load() {
        List<String> s = new ArrayList<>();
        s.add("1");
        return s;
    }

    /** get stagedFile object */
    public HashMap<String, String> getStagedMap() {
        return this.stagedFiles;
    }

    /** get the removedFile object */
    public HashSet<String> getRemovedFiles() {
        return this.removedFiles;
    }

    /** clear the index */
    public static void clear() {
        Index staging = Utils.readObject(Index.INDEX_FILE, Index.class);
        staging.stagedFiles.clear();
        staging.removedFiles.clear();
        staging.save();
    }


}
