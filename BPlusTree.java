import java.util.ArrayList;

public class BPlusTree {
    private Node root = null;
    private int degree;

    //The first node of the leafs
    private Node head = null;
    //The last node of the leafs
    private Node tail = null;

    /**
     * Initialize(m)
     * Initialize the tree with degree of m, set the root as a leaf node.
     *
     * @param m
     *
     */
    public void Initialize(int m) {
        degree = m;
        root = new Node(true, true);
        head = root;
    }

    /**
     * Search(key)
     * returns all values associated with a specific key.
     *
     * @param key
     * @return value
     */
    public ArrayList<String> Search(Double key) {
        //return null if key is empty or tree is empty
        if (key == null || root == null) {
            return null;
        }

        //Search for the leaf node which contains the key
        Node leaf = searchLeafNode(root, key);
        ArrayList<String> result = new ArrayList<>();

        //find the index and return
        // Look for value in the leaf
        for (int i = 0; i < leaf.pairs.size(); i++) {
            if (key.equals(leaf.pairs.get(i).key())) {
                result.add(leaf.pairs.get(i).value());
            }
        }
        return result;
    }


    /**
     * Search(key1, key2)
     * returns all key value pairs such that key1 <= key <= key2.
     *
     * @param key1, key2
     * @return value
     */
    public ArrayList<MyPair> Search(Double key1, Double key2) {
        if (key1 == null || key2 == null || root == null) {
            return null;
        }
        //Search for the leaf node which contains key1
        Node leaf = searchLeafNode(root, key1);
        ArrayList<MyPair> result = new ArrayList<>();
        traverseLeaf(leaf, result, key1, key2);
        return result;
    }

    /**
     * traverse all the leaf nodes which contain keys in range of (key1, key2),
     *      stops until the first key of next leaf is larger than key2, or it is the last leaf of the tree.
     *
     * @param leaf, result, key1, key2
     */
    private void traverseLeaf(Node leaf, ArrayList<MyPair> result, Double key1, Double key2) {
        if (key2.compareTo(leaf.pairs.get(0).key()) >= 0) {
            for (int i = 0; i < leaf.pairs.size(); i++) {
                if (key1.compareTo(leaf.pairs.get(i).key()) <= 0 && key2.compareTo(leaf.pairs.get(i).key()) >= 0) {
                    result.add(leaf.pairs.get(i));
                }
            }
            if(leaf != tail) {
                leaf = leaf.next;
                traverseLeaf(leaf, result, key1, key2);
            }
        }
    }

    /**
     * returns the leaf node which might contain value associated with key
     *
     * @param key
     * @return node
     */
    private Node searchLeafNode(Node root, Double key) {
        //If the node is a leaf, return
        if (root.isLeaf) {
            return root;
        }
        //If the key is smaller than the first key in the node, follow its first child to continue
        if (key.compareTo(root.pairs.get(0).key()) < 0) {
            return searchLeafNode(root.children.get(0), key);
            //If the key is bigger than the last key in the node, follow its last child to continue
        }else if (key.compareTo(root.pairs.get(root.pairs.size() - 1).key()) >= 0) {
            return searchLeafNode(root.children.get(root.children.size() - 1), key);
        } else {
            //Use binary search to find the child to follow
            int left = 0, right = root.pairs.size() - 1, mid;
            int comp;
            while (left <= right) {
                mid = (left + right) / 2;
                comp = root.pairs.get(mid).key().compareTo(key);
                if (comp == 0) {
                    return searchLeafNode(root.children.get(mid + 1), key);
                } else if (comp < 0) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
            return searchLeafNode(root.children.get(left), key);
        }
    }

    /**
     * Insert a pair of (key, value) into the tree
     *
     * @param key, value
     */
    public void Insert (Double key, String value) {
        if (key == null || value == null || root == null) {
            return;
        }
        //1. Search for the leaf node to add the pair
        Node leaf = searchLeafNode(root, key);
        //2. Insert the new pair into the leaf node
        insertIntoLeafNode(leaf, key, value);
        //3. Update n: number of pairs in the leaf, check if splitting is necessary
        if (!leaf.duplicate) {
            leaf.n++;
        }
        //If n < degree, done.
        if (leaf.n < degree) {
            return;
        }
        //4.Split
        //<1>.Create two new nodes
        Node left = new Node(true);
        Node right = new Node(true);
        //<2>.Update doubly linked list
        if (leaf.prev == null) {
            head = left;
        } else {
            left.prev = leaf.prev;
            leaf.prev.next = left;
        }
        if (leaf.next != null) {
            right.next = leaf.next;
            leaf.next.prev = right;
        } else {
            tail = right;
        }
        left.next = right;
        right.prev = left;
        leaf.next = null;
        leaf.prev = null;
        //<3>.Copy the pairs from former leaf to new nodes
        copy2Nodes(left, right, leaf);

        //5. Update parent node
        //<1> If the leaf is a root
        if (leaf.isRoot) {
            leaf.isRoot = false;
            Node parent = new Node (false, true);
            root = parent;
            left.parent = parent;
            right.parent = parent;
            parent.children.add(left);
            parent.children.add(right);
            parent.pairs.add(right.pairs.get(0));
            leaf.pairs = null;
            return;
        }
        //<2> If the leaf is not a root
        //Update parent node
        int index = leaf.parent.children.indexOf(leaf);
        leaf.parent.children.remove(leaf);
        left.parent = leaf.parent;
        right.parent = leaf.parent;
        leaf.parent.children.add(index, left);
        leaf.parent.children.add(index + 1, right);
        leaf.parent.pairs.add(index,right.pairs.get(0));
        leaf.pairs = null;

        //Split parent node if necessary
        updateInsert(leaf.parent);
        leaf.parent = null;
    }

    /**
     * copy the pairs of a leaf node into two new leaf nodes
     *
     * @param left, right, leaf
     */
    private void copy2Nodes (Node left, Node right, Node leaf) {
        //The split point is ceil(m / 2)
        int splitPoint = (degree + 1) / 2;
        //Split the node into two nodes
        //<1> No duplicate
        if (!leaf.duplicate) {
            for (int i = 0; i < leaf.pairs.size(); i++) {
                if (i < splitPoint - 1) {
                    left.pairs.add(leaf.pairs.get(i));
                } else {
                    right.pairs.add(leaf.pairs.get(i));
                }
            }
        } else {
            //<2> Duplicate
            int j = 0;
            for (int i = 0; i < leaf.pairs.size(); i++) {
                if (j < splitPoint && leaf.pairs.get(i).key().compareTo(leaf.pairs.get(i + 1).key()) != 0) {
                    j++;
                }
                if (j < splitPoint) {
                    left.pairs.add(leaf.pairs.get(i));
                } else {
                    right.pairs.add(leaf.pairs.get(i));
                }
            }
        }
    }

    /**
     * Update the parent node of a node after insertion
     *
     * @param parent
     */
    private void updateInsert(Node parent) {
        if (parent.children.size() > degree) {
            //Divide into two nodes
            Node left = new Node(false);
            Node right = new Node(false);
            int splitPoint = (degree + 1) / 2;
            //<1> Split children
            for (int i = 0; i < parent.children.size(); i++) {
                if (i < splitPoint) {
                    left.children.add(parent.children.get(i));
                    parent.children.get(i).parent = left;
                } else {
                    right.children.add(parent.children.get(i));
                    parent.children.get(i).parent = right;
                }
            }
            parent.children = null;
            //<2> Split pairs
            for (int i = 0; i < parent.pairs.size(); i++) {
                if (i < splitPoint - 1) {
                    left.pairs.add(parent.pairs.get(i));
                } else if (i > splitPoint - 1)  {
                    right.pairs.add(parent.pairs.get(i));
                }
            }
            //<3>.Update grandparent node
            //(1) if parent is not a root
            if (parent.parent != null) {
                //Update gradparent node
                int index = parent.parent.children.indexOf(parent);
                parent.parent.children.remove(parent);
                left.parent = parent.parent;
                right.parent = parent.parent;
                parent.parent.children.add(index, left);
                parent.parent.children.add(index + 1, right);
                parent.parent.pairs.add(index, parent.pairs.get(splitPoint - 1));
                parent.pairs = null;

                updateInsert(parent.parent);
                parent.parent = null;

            }else {
                //(2) If parent is a root
                parent.isRoot = false;
                Node gradparent = new Node(false, true);
                root = gradparent;
                left.parent = gradparent;
                right.parent = gradparent;
                gradparent.children.add(left);
                gradparent.children.add(right);
                gradparent.pairs.add(parent.pairs.get(splitPoint - 1));
                parent.pairs = null;
            }
        }
    }

    /**
     * insert a new pair(key, value) into a leaf node
     *
     * @param leaf, key, value
     */
    private void insertIntoLeafNode (Node leaf, Double key, String value) {
        //Use binary search
        int low = 0, high = leaf.pairs.size() - 1, mid;
        int comp ;
        while (low <= high) {
            mid = (low + high) / 2;
            comp = leaf.pairs.get(mid).key().compareTo(key);
            if (comp == 0) {
                leaf.pairs.add(mid, new MyPair(key, value));
                leaf.duplicate = true;
                break;
            } else if (comp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        if (low > high){
            leaf.pairs.add(low, new MyPair(key, value));
        }
    }
}
