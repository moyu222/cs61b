package gitlet;

import java.io.File;

import static gitlet.Utils.join;

/** Represent blob object
 * each blob represent a file and plain file
 * the name of blob file is the hash value of work file content
 * and the body of blob file is the  work file content
 *
 * use a hashset to track all the blobs for distinguishing in the runtime
 * achieve hashset in the repository class
 */
public class Blob {

    /** The blobs directory */
    static final File BLOBS_DIR = join(Repository.GITLET_DIR, "objects", "blobs");

    /** The sha-1 value and name of blob */
    private final String hash;

    /** save content of new file as byte[] as content of blob */
    private final byte[] content;

    /** the constructor of blob */
    public Blob(File file) {
        this.content = Utils.readContents(file);
        this.hash = Utils.sha1((Object) content);
    }

    public Blob(String file) {
        File filePath = new File(file);
        this.content = Utils.readContents(filePath);
        this.hash = Utils.sha1((Object) content);
    }

    /** save blob object to 'objects/blobs/' if not exist*/
    public void saveBlob() {
        File blobfile = join(BLOBS_DIR,hash);
        if (!blobfile.exists()) {
            Utils.writeContents(blobfile, (Object) content);
        }
    }

    /** get hash */
    public String getHash() {
        return hash;
    }

    /** get the content of work file */
    public String getFileContent() {
        File blobfile = join(BLOBS_DIR,hash);
        return Utils.readContentsAsString(blobfile);
    }

    /** get the byte[] the blob content */
    public byte[] getBlobContent() {
        return content;
    }
}
