package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
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

    /** Constructor */
    public Index() {
        stagedFiles = new HashMap<>();
    }

    /** Add a file in to the index (staging area) in gitlet one file once */
    public void add(String filePath, String blobHash) {
        stagedFiles.put(filePath, blobHash);
    }

    /** Remove a file from the index */
    public void remove(String filePath) {
        stagedFiles.remove(filePath);
    }

    /** Save the index to a file */
    public void save() {
        Utils.writeObject(INDEX_FILE, stagedFiles);
    }

    /** Load the index from file */
    public static List<String> Load() {
        return
    }

    /** get stagedFile object */
    public HashMap<String, String> getStagedFiles() {
        return stagedFiles;
    }

}
