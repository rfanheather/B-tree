import java.util.ArrayList;
import java.util.List;

public class Node {
    //Is the node a leaf?
    public boolean isLeaf;
    //Is the node a root?
    public boolean isRoot;
    //Number of keys stored in the node
    public int n;
    //Is there a duplicate in the node?
    public boolean duplicate;

    public Node parent;
    public List<Node> children;

    public Node prev;
    public Node next;

    //Pairs stored in the node
    public List<MyPair> pairs;

    //Initialize a node with isLeaf information
    public Node(boolean isLeaf) {
        this.isLeaf = isLeaf;
        pairs = new ArrayList<>();
        n = 0;
        duplicate = false;
        parent = null;

        if (!isLeaf) {
            children = new ArrayList<>();
        } else {
            prev = null;
            next = null;
        }
    }

    //Initialize a node with isLeaf and isRoot information
    public Node(boolean isLeaf, boolean isRoot) {
        this.isLeaf = isLeaf;
        this.isRoot = isRoot;
        pairs = new ArrayList<>();
        n = 0;
        duplicate = false;
        parent = null;

        if (!isLeaf) {
            children = new ArrayList<>();
        } else {
            prev = null;
            next = null;
        }
    }
}