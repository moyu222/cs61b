package gitlet;

import java.io.File;
import java.io.IOException;
import java.lang.module.FindException;
import java.util.*;

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

    /**
     * the hashset of tracking the boldID in the bolds folder
     */
    public static HashSet<String> blobSet = new HashSet<>();

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * the heads directory
     */
    public static final File HEADS_DIR = join(GITLET_DIR, "refs", "heads");

    /**
     * the Head file path, record the path of head refs for each branch
     */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");



    /* TODO: fill in the rest of this class. */

    /** a function to write heads/master file: the hash value of commit */


    /**
     * init the repository
     * inital commit
     * ref/heads/master
     * HEAD
     */
    public static void init() {

        if (GITLET_DIR.exists()) {
            System.out.println("`A Gitlet version-control system already exists in the current directory.`");
            return;
        }
        if (!checkStructure()) {
            GITLET_DIR.mkdir();
            Blob.BLOBS_DIR.mkdirs();
            Commit.COMMIT_DIR.mkdirs();
            HEADS_DIR.mkdirs();
        }

        Commit initialCommit = new Commit("initial commit", (List<String>) null);
        initialCommit.computeHash();
        String commitHash = initialCommit.getHash();
        initialCommit.saveCommit(commitHash);

        File masterBranch = join(HEADS_DIR, "master");
        Utils.writeContents(masterBranch, commitHash);
        Utils.writeContents(HEAD_FILE, "refs/heads/master");

    }


    /**
     * 1. file - blob
     * 2. blob - index
     * 3. load the lastest commit and create new commit
     *      deterBlobsRefs and getHash and save
     *      if deterBlobsRefs return false clear the index
     *
     */
    public static void add(String file) {
        File addFile = new File(file);
        if (!addFile.exists()) {
            System.out.println("File does not exist");
            System.exit(0);
        }

        Blob newBlob = new Blob(file);
        newBlob.saveBlob();
        String blobHash = newBlob.getHash();

        Index staging;
        if (Index.INDEX_FILE.exists()) {
            staging = Index.getIndex();
        } else {
            staging = new Index();
        }
        staging.add(file, blobHash);

        String currentCommitHash = getHeadCommitHash();
        Commit currentCommit = Commit.loadCommitFromHash(currentCommitHash);
        boolean change = currentCommit.deterBlobsRef(staging.getStagedMap());
        if (!change) {
            staging.removeStaged(file);
        }

        staging.save();
    }

    /** commit
     *
     */
    public static void commit(String message) {

        if (message == null || message.trim().isEmpty()) {
            System.out.println("Please enter a commit message");
            System.exit(0);
        }

        if (!Index.INDEX_FILE.exists()) {
            System.out.println("No changes added to the commit");
            return;
        }

        Index staging = Index.getIndex();
        if (staging.getStagedMap().isEmpty() && staging.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        }

        String parentCommitHash = getHeadCommitHash();
        Commit newCommit = new Commit(message, parentCommitHash);

        boolean changed = newCommit.deterBlobsRef(staging.getStagedMap()) ||
                newCommit.removeBlobsRef(staging.getRemovedFiles());
        if (!changed) {
            System.out.println("No changes added to the commit");
            return;
        }

        newCommit.computeHash();
        String hash = newCommit.getHash();
        newCommit.saveCommit(hash);
        updateBranchHead(hash);
        Index.clear();
    }

    /** rm command
     * if file in the staging, remove it in the stagedFile
     * if file has been commited, markForRemoval and remove the file in work dir
     * else "No reason to remove the file
     */
    public static void rm(String fileName) {
        Index staging;
        if (!Index.INDEX_FILE.exists()) {
            staging = new Index();
        } else {
            staging = Index.getIndex();
        }

        boolean removed = false;

        if (staging.getStagedMap().containsKey(fileName)) {
            staging.removeStaged(fileName);
            removed = true;
            staging.save();
        }

        String headCommitHash = getHeadCommitHash();
        Commit headCommit = Commit.loadCommitFromHash(headCommitHash);
        if (headCommit.getBlobsRef().containsKey(fileName)) {
            staging.markForRemoval(fileName);
            removed = true;
            staging.save();

            File fileToRemove = new File(fileName);
            if (fileToRemove.exists()) {
                Utils.restrictedDelete(fileToRemove);
            }
        }

        if (!removed) {
            System.out.println("No reason to remove the file.");
        }

    }

    /** log command current backwards along the commit tree until
     * the initial commit, following the first parent commit
     */
    public static void log() {
        String currCommitHash = getHeadCommitHash();
        Commit currCommit = Commit.loadCommitFromHash(currCommitHash);

        while (currCommit != null) {
            currCommit.printLog();

            List<String> parentHashes = currCommit.getParentHashes();
            if (parentHashes.isEmpty()) {
                break;
            }

            String firstParentHash = parentHashes.get(0);
            currCommit = Commit.loadCommitFromHash(firstParentHash);
        }

    }

    /** the global-log command
     * displays information about all commits
     * order does not matter
     */
    public static void globalLog() {
        List<String> commitList = Utils.plainFilenamesIn(Commit.COMMIT_DIR);
        if (commitList == null || commitList.isEmpty()) {
            return;
        }
        for (String commitHash : commitList) {
            Commit commit = Commit.loadCommitFromHash(commitHash);
            commit.printLog();
        }
    }

    /** the find command
     *  prints out all the ids of all commits given commit message,
     *  one per line. If there are multiple commits, print ids on sparate lines
     */
    public static void find(String message) {
        List<String> commitList = Utils.plainFilenamesIn(Commit.COMMIT_DIR);
        boolean found = false;
        if (commitList == null || commitList.isEmpty()) {
            return;
        }
        for (String commitHash : commitList) {
            Commit commit = Commit.loadCommitFromHash(commitHash);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getHash()+"\n");
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }

    }

    /** the status command
     * display what branches currently exist, and marks the current branch
     * with a *. Also displays what files have been staged for addition or
     * removal
     *
     * There is an empty line between sections, and the entire status ends
     * in an empty line as well. Entries should be listed in lexicographic order,
     * using the Java string-comparison order (the asterisk doesn’t count).
     */
    public static void status() {
        StringBuilder status = new StringBuilder();

        status.append("=== Branches ===").append("\n");
        String currentBranch = getCurrentBranch();
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);

        if (!(branchList == null || branchList.isEmpty())) {
            Collections.sort(branchList);
            for (String branch : branchList) {
                if (branch.equals(currentBranch)) {
                    status.append("*");
                }
                status.append(branch).append("\n");
            }
        }
        status.append("\n");

        if (Index.INDEX_FILE.exists()) {
            status.append("=== Staged Files ===").append("\n");

            Index staging = Index.getIndex();
            List<String> stagedFiles = new ArrayList<>(staging.getStagedMap().keySet());
            if (!stagedFiles.isEmpty()) {
                Collections.sort(stagedFiles);
                for (String file : stagedFiles) {
                    status.append(file).append("\n");
                }
            }
            status.append("\n");

            status.append("=== Removed Files ===").append("\n");
            List<String> markRemovedFiles = new ArrayList<>(staging.getRemovedFiles());
            if (!markRemovedFiles.isEmpty()) {
                Collections.sort(markRemovedFiles);
                for (String file : markRemovedFiles) {
                    status.append(file).append("\n");
                }
            }
            status.append("\n");
        } else {
            status.append("=== Staged Files ===").append("\n");
            status.append("\n");
            status.append("=== Removed Files ===").append("\n");
            status.append("\n");
        }

        System.out.println(status);
    }

    /** the checkout command one part of checkout
     * checkout -- [file name]
     * take the version in the head commit and puts it in the work directory
     * overwriting the version of the file.
     *
     * The new version of the file is not staged
     */
    public static void checkoutLatestFile(String fileName) {
        String headCommitHash = getHeadCommitHash();
        Commit headCommit = Commit.loadCommitFromHash(headCommitHash);

        TreeMap<String, String> blobsRef = headCommit.getBlobsRef();
        String blobHash = blobsRef.get(fileName);
        if (blobHash == null) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            File blobPath = join(Blob.BLOBS_DIR, blobHash);
            File filePath = new File(fileName);
            String content = Utils.readContentsAsString(blobPath);
            Utils.writeContents(filePath, content);
        }
    }

    /** another part of checkout command
     *  checkout [commit id] -- [file name]
     *  takes the version of the file as it exists in the commit with given id
     *  put it in the working directory. overwrite if existed.
     *
     *  new version is not staged
     */
    public static void checkoutIdFile(String prefix, String fileName) {
        String commitId = Commit.findFullCommitId(prefix);
        if (commitId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit specifiedCommit = Commit.loadCommitFromHash(commitId);

        TreeMap<String, String> blobsRef = specifiedCommit.getBlobsRef();
        String blobHash = blobsRef.get(fileName);
        if (blobHash == null) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            File blobPath = join(Blob.BLOBS_DIR, blobHash);
            File filePath = new File(fileName);
            String content = Utils.readContentsAsString(blobPath);
            Utils.writeContents(filePath, content);
        }
    }

    /** the last part of checkout command
     * checkout [branch name]
     * 1. Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions
     * of the files that are already there if they exist.
     * 2. the given branch will now be considered the current branch (HEAD).
     * 3. Any files that are tracked in the current branch
     * but are not present in the checked-out branch are deleted
     * 4. The staging area is cleared, unless the checked-out branch is the current branch
     */
    public static void checkoutBranch(String branchName) {
        String currBranch = getCurrentBranch();
        if (branchName.equals(currBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        String currCommitHash = getHeadCommitHash();
        Commit currCommit = Commit.loadCommitFromHash(currCommitHash);
        TreeMap<String, String> currCommitBlobRefs = currCommit.getBlobsRef();
        List<String> workFileList = Utils.plainFilenamesIn(CWD);

        for (String file : workFileList) {
            if (!(currCommitBlobRefs.containsKey(file))) {
                System.out.println("There is an untracked file in the way; delete it, " +
                        "or add and commit it first.");
                System.exit(0);
            }
        }


        File branchPath = join(HEADS_DIR, branchName);
        if (!branchPath.exists()) {
            System.out.println("No such branch exists.");
            return;
        }

        String specifiedCommitHash = Utils.readContentsAsString(branchPath);
        Commit specifiedCommit = Commit.loadCommitFromHash(specifiedCommitHash);

        TreeMap<String, String> blobsRef = specifiedCommit.getBlobsRef();
        Set<String> workFilesToRemove = new HashSet<>(workFileList);

        for (Map.Entry<String, String> entry: blobsRef.entrySet()) {
            String fileName = entry.getKey();
            String blobHash = entry.getValue();
            workFilesToRemove.remove(fileName);

            File blobPath = join(Blob.BLOBS_DIR, blobHash);
            File filePath = new File(fileName);
            String content = Utils.readContentsAsString(blobPath);
            Utils.writeContents(filePath, content);
        }

        for (String fileToRemove : workFilesToRemove) {
            Utils.restrictedDelete(fileToRemove);
        }

        changeBranch(branchName);
        if (Index.INDEX_FILE.exists()){
           Index.clear();
        }

    }

    /** the branch command
     * creates a new branch with the given name, and points it at the current head
     * commit. A branch is a name for reference to a commit node
     * not immediately switch to the newly created branch
     */
    public static void branch(String branchName) {
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);
        if (branchList.contains(branchName)) {
            System.out.println("A branch with that name already exist.");
            return;
        }

        String headCommitHash = getHeadCommitHash();
        File newBranch = join(HEADS_DIR, branchName);
        Utils.writeContents(newBranch, headCommitHash);
    }

    /** the rm-branch command
     * Deletes the branch with the given name. This only means to delete the pointer
     * associated with the branch; it does not mean to delete all commits that were
     * created
     */
    public static void rmBranch(String branchName) {
        List<String> branchList = Utils.plainFilenamesIn(HEADS_DIR);
        if (!(branchList.contains(branchName))) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String currBranch = getCurrentBranch();
        if (currBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File rmBranchPath = join(HEADS_DIR, branchName);
        rmBranchPath.delete();
    }

    /** the reset command
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Removes the current branch's head to that commit node
     * The staging area is cleared.
     * use commit id prefix as checkout
     */
    public static void reset(String prefix) {
        String commitId = Commit.findFullCommitId(prefix);
        if (commitId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        File tempBranch = join(HEADS_DIR, "temp");
        Utils.writeContents(tempBranch, commitId);
        checkoutBranch("temp");
        String currHead = getCurrentBranch();
        changeBranch(currHead);
        tempBranch.delete();
        File headFile = join(HEADS_DIR, currHead);
        Utils.writeContents(headFile,commitId);
    }

    /** the merge command
     *
     */
    public static void merge(String branchName) {
        if (Index.INDEX_FILE.exists()) {
            Index staging = Index.getIndex();
            if (!staging.getStagedMap().isEmpty() || !staging.getRemovedFiles().isEmpty()) {
                System.out.println("You have uncommitted changes.");
                System.exit(0);
            }
        }

        File givenBranchPath = join(HEADS_DIR, branchName);
        if (!givenBranchPath.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }

        String currBranch = getCurrentBranch();
        if (currBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
        }

        String currBCHash = getHeadCommitHash();
        String givenBCHash = Utils.readContentsAsString(givenBranchPath);
        Map<String, Integer> commitCount = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(currBCHash);
        queue.offer(givenBCHash);
        String splitCommitHash = null;

        while (!queue.isEmpty()) {
            String commitHash = queue.poll();
            commitCount.put(commitHash, commitCount.getOrDefault(commitHash, 0) + 1);
            if (commitCount.get(commitHash) == 2) {
                 splitCommitHash = commitHash;
                 break;
            }

            Commit commit = Commit.loadCommitFromHash(commitHash);
            for (String parentHash : commit.getParentHashes()) {
                if (parentHash != null && !commitCount.containsKey(parentHash)) {
                    queue.offer(parentHash);
                }
            }
        }

        if (splitCommitHash == null) {
            System.out.println("Error: No split point found.");
            return;
        }

        if (splitCommitHash.equals(givenBCHash)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitCommitHash.equals(currBCHash)) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Commit currBC = Commit.loadCommitFromHash(currBCHash);
        Commit givenBC = Commit.loadCommitFromHash(givenBCHash);
        Commit splitC = Commit.loadCommitFromHash(splitCommitHash);
        TreeMap<String, String> currBlobRef = currBC.getBlobsRef();
        TreeMap<String, String> givenBlobRef = givenBC.getBlobsRef();
        TreeMap<String, String> splitBlobRef = splitC.getBlobsRef();

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(currBlobRef.keySet());
        allFiles.addAll(givenBlobRef.keySet());



        Index staging;
        if (Index.INDEX_FILE.exists()) {
            staging = Index.getIndex();
        } else {
            staging = new Index();
        }

        for (String fileName : allFiles) {
            File workFilePath = new File(fileName);
            String currBlobId = currBlobRef.get(fileName);
            String splitBlobId = splitBlobRef.get(fileName);
            String givenBlobId = givenBlobRef.get(fileName);

            List<Integer> caseList = mergeHelper(currBlobId, splitBlobId, givenBlobId);
            int currCaseNum = caseList.get(0);
            int givenCaseNum = caseList.get(1);

            if (givenCaseNum == 5 && currCaseNum == 4) {
                staging.add(fileName, givenBlobId);
            } else if (givenCaseNum == 4 && currCaseNum == 5) {
                staging.add(fileName, currBlobId);
            } else if (givenCaseNum == 1 && currCaseNum == 3) {
                staging.add(fileName, givenBlobId);
            } else if (givenCaseNum == 2 && currCaseNum == 4) {
                staging.markForRemoval(fileName);
                File fileToRemove = new File(fileName);
                Utils.restrictedDelete(fileToRemove);
            } else if (givenCaseNum == 4 && currCaseNum == 2) {

            } else if (!Objects.equals(currBlobId, givenBlobId)) {

                StringBuilder newContent = new StringBuilder();
                if (currBlobId != null) {
                    newContent.append("<<<<<<< HEAD").append("\n");
                    String currContent = Utils.readContentsAsString(join(Blob.BLOBS_DIR, currBlobId));
                    newContent.append(currContent).append("\n");
                    newContent.append("=======").append("\n");
                }
                if (givenBlobId != null) {
                    newContent.append(Utils.readContentsAsString(join(Blob.BLOBS_DIR, givenBlobId)));
                    newContent.append("\n");
                }
                newContent.append(">>>>>>>");
                Utils.writeContents(workFilePath, newContent.toString());
                add(fileName);
                System.out.println("Encountered a merge conflict.");
            }

        }
        String message = "Merged " + branchName + " into " + currBranch + ".";

        List<String> parentHashes = new ArrayList<>();
        parentHashes.add(currBCHash);
        parentHashes.add(givenBCHash);
        Commit newCommit = new Commit(message, parentHashes);

        newCommit.deterBlobsRef(staging.getStagedMap());
        newCommit.removeBlobsRef(staging.getRemovedFiles());

        newCommit.computeHash();
        String hash = newCommit.getHash();
        newCommit.saveCommit(hash);
        updateBranchHead(hash);
        Index.clear();

    }

    private static List<Integer> mergeHelper(String currBlobId, String splitBlobId, String givenBlobId) {
        int currCaseNum = 0;
        int givenCaseNum = 0;

        // file in curr is new
        if (splitBlobId == null && currBlobId != null) {
            currCaseNum = 1;
        }
        // file in curr is deleted
        else if (splitBlobId != null && currBlobId == null) {
            currCaseNum = 2;
        }
        // null both in curr and split
        else if (splitBlobId == null && currBlobId == null) {
            currCaseNum = 3;
        }
        // file in curr does not been modified
        else if (currBlobId.equals(splitBlobId)) {
            currCaseNum = 4;
        }
        // file in curr has been modified
        else {
            currCaseNum = 5;
        }

        // file in given is new
        if (splitBlobId == null && givenBlobId != null) {
            givenCaseNum = 1;
        }
        // file in given is deleted
        else if (splitBlobId != null && givenBlobId == null) {
            givenCaseNum = 2;
        }
        // null both in given and split
        else if (splitBlobId == null) {
            givenCaseNum = 3;
        }
        // file in given does not been modified
        else if (givenBlobId.equals(splitBlobId)) {
            givenCaseNum = 4;
        }
        // file in given has been modified
        else {
            givenCaseNum = 5;
        }
        List<Integer> result = new ArrayList<>();
        result.add(currCaseNum);
        result.add(givenCaseNum);
        return result;
    }






    /** get the hash value of current commit
     *  assume the current branch file (master, other) exist */
    public static String getHeadCommitHash() {
        File branchFile = join(HEADS_DIR, getCurrentBranch());
        return Utils.readContentsAsString(branchFile);
    }

    /**
     * check the structure of the repository
     * if the structure is right return true
     * else return false
     */
    public static boolean checkStructure() {
        return GITLET_DIR.exists() && Commit.COMMIT_DIR.exists() &&
                Blob.BLOBS_DIR.exists() && HEADS_DIR.exists();
    }

    /** get the current branch */
    public static String getCurrentBranch() {
        String headContents = Utils.readContentsAsString(HEAD_FILE).trim();
        return headContents.replace("refs/heads/","");
    }

    /** update current branch head content */
    public static void updateBranchHead(String newCommitHash) {
        File branchFile = join(HEADS_DIR, getCurrentBranch());
        Utils.writeContents(branchFile, newCommitHash);
    }

    /** change current branch and change HEAD file */
    public static void changeBranch(String branchName) {
        Utils.writeContents(HEAD_FILE, "refs/heads/" + branchName);
    }
}