package main.java;

/**
 * Created by zakirhyder on 6/23/17.
 */

public class BTree<K extends Comparable, V> {
    private BTNode<K, V> mRoot = null;

    private BTNode<K,V> createNode() {
        BTNode<K, V> btNode = new BTNode();

        return btNode;
    }

    //
    // Insert key and its value into the tree
    //
    public void insert(K key, V value) {
//        System.out.println(key + " " + value);

        if(mRoot==null){
            mRoot = createNode();
        }

        if(mRoot.mCurrentKeyNum == BTNode.UPPER_BOUND_KEYNUM) {
//            System.out.println("!!!!node upper limit reached!!!!");
            BTNode<K, V> btNode = createNode();
            btNode.mIsLeaf = false;
            btNode.mChildren[0] = mRoot;
            mRoot = btNode;
            splitNode(mRoot, 0, btNode.mChildren[0]);
            return;
        }

        insertKeyAtNode(mRoot, key, value);

//        printNodeKeys(mRoot);
    }

    //
    // Insert key and its value to the specified root
    //
    private void printNodeKeys(BTNode node) {
        System.out.println("nodes number of keys = " + node.mCurrentKeyNum);
        int i;
        System.out.println("=========keys====");
        for (i = 0; i < node.mCurrentKeyNum; i++) {
            System.out.println(i + " " + node.mKeys[i].mKey + " " + node.mKeys[i].mValue);
        }

        System.out.println("=========children====");
        for (i = 0; i < node.mChildren.length; i++) {
            if (node.mChildren[i] != null) {
                printNodeKeys(node.mChildren[i]);
            }
        }

    }

    private void insertKeyAtNode(BTNode rootNode, K key, V value) {
        int i;

        if(rootNode.mIsLeaf) {
            if(rootNode.mCurrentKeyNum == 0){
                rootNode.mKeys[0] = new BTKeyValue(key, value);
                ++(rootNode.mCurrentKeyNum);
                return;
            }

            // Verify if the specified key doesn't exist in the node
            for (i = 0; i < rootNode.mCurrentKeyNum; i++) {
                if(key.compareTo(rootNode.mKeys[i].mKey) == 0) {
                    // Find existing key, overwrite its value only
                    rootNode.mKeys[i].mValue = value;
                    return;
                }
            }


            // placing key in correct place by shifting larger keys to right
            i = rootNode.mCurrentKeyNum - 1;
            BTKeyValue<K, V> exitingKeyVal = rootNode.mKeys[i];
            while (i > -1 && key.compareTo(exitingKeyVal.mKey) < 0 ){
                rootNode.mKeys[i + 1] = exitingKeyVal;
                --i;
                if(i > -1) {
                    exitingKeyVal = rootNode.mKeys[i];
                }
            }

            i = i + 1;
            rootNode.mKeys[i] = new BTKeyValue<K, V>(key,value);

            ++(rootNode.mCurrentKeyNum);
            return;
        }

        // This is an internal node (i.e: not a leaf node)
        // So let find the child node where the key is supposed to belong
        i = 0;
        int numberOfKeys = rootNode.mCurrentKeyNum;

        BTKeyValue<K, V> currentKey = rootNode.mKeys[i];

        // figuring out the i and currentkey. In each turn we check for key
        // is larger then currentKey.mkey. If yes then replace the currentKey.
        while ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) > 0)) {
            ++i;
            if (i < numberOfKeys) {
                currentKey = rootNode.mKeys[i];
            }
            else {
                --i;
                break;
            }
        }

        if ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) == 0)) {
            // The key already existed so replace its value and done with it
            currentKey.mValue = value;
            return;
        }


        BTNode<K, V> btNode;
        // if key is bigger than current key then check the right child
        // i is the index of the root mkey that is figured out from
        // previous while loop
        if (key.compareTo(currentKey.mKey) > 0) {
            btNode = BTNode.getRightChildAtIndex(rootNode, i);
            i = i + 1;
        }
        else {
            if ((i - 1 >= 0) && (key.compareTo(rootNode.mKeys[i - 1].mKey) > 0)) {
                btNode = BTNode.getRightChildAtIndex(rootNode, i - 1);
            }
            else {
                btNode = BTNode.getLeftChildAtIndex(rootNode, i);
            }
        }

        if (btNode.mCurrentKeyNum == BTNode.UPPER_BOUND_KEYNUM) {
            // If the child node is a full node then handle it by splitting out
            // then insert key starting at the root node after splitting node
            splitNode(rootNode, i, btNode);
            insertKeyAtNode(rootNode, key, value);
            return;
        }

        insertKeyAtNode(btNode, key, value);

    }


    //
    // Split a child with respect to its parent at a specified node
    //
    private void splitNode(BTNode parentNode, int nodeIdx, BTNode childNode) {
        int i;

        BTNode<K, V> newNode = createNode();

        newNode.mIsLeaf = childNode.mIsLeaf;

        // Since the node is full,
        // new node must share LOWER_BOUND_KEYNUM (aka t - 1) keys from the node
        newNode.mCurrentKeyNum = BTNode.LOWER_BOUND_KEYNUM;

        // Copy right half of the keys from the node to the new node
        // LOWER_BOUND_KEYNUM index is the middle key of array ndex.
        for (i = 0; i < BTNode.LOWER_BOUND_KEYNUM; ++i) {
            // new keys are always MIN_DEGREE away
            // deg = 5
            // lb = 4
            // hb = 9
            // so the first key of new node is 5. we are separating the keys are after 4
            // 4 will become new root
            newNode.mKeys[i] = childNode.mKeys[i + BTNode.MIN_DEGREE];
            childNode.mKeys[i + childNode.MIN_DEGREE] = null;
        }

        // If the node is an internal node (not a leaf),
        // copy the its child pointers at the half right as well
        if(!childNode.mIsLeaf) {
            // for non-leaf the node wil have 1 more children then keys
            // deg = 5
            // lb = 4
            // hb = 9
            // thats why we are using MIN_DEGREE here because MIN_DEGREE = LOWER_BOUND_KEYNUM + 1
            for (i = 0; i < childNode.MIN_DEGREE; i++) {
                newNode.mChildren[i] = childNode.mChildren[i + BTNode.MIN_DEGREE];
                childNode.mChildren[i] = null;

            }
        }

        // The node at this point should have LOWER_BOUND_KEYNUM (aka min degree - 1) keys at this point.
        // We will move its right-most key(4) to its parent node later.
        childNode.mCurrentKeyNum = BTNode.LOWER_BOUND_KEYNUM;

        // Do the right shift for relevant child pointers of the parent node
        // so that we can put the new node as its new child pointer
        for (i = parentNode.mCurrentKeyNum; i > nodeIdx; --i) {
            parentNode.mChildren[i + 1] = parentNode.mChildren[i];
            parentNode.mChildren[i] = null;
        }
        parentNode.mChildren[nodeIdx + 1] = newNode;

        // Do the right shift all the keys of the parent node the right side of the node index as well
        // so that we will have a slot for move a median key from the splitted node
        for (i = parentNode.mCurrentKeyNum - 1; i >= nodeIdx; --i) {
            parentNode.mKeys[i + 1] = parentNode.mKeys[i];
            parentNode.mKeys[i] = null;
        }
        parentNode.mKeys[nodeIdx] = childNode.mKeys[BTNode.LOWER_BOUND_KEYNUM];
        childNode.mKeys[BTNode.LOWER_BOUND_KEYNUM] = null;
        ++(parentNode.mCurrentKeyNum);
    }


    //
    // Search value for a specified key of the tree
    //
    public V search(K key) {
        BTNode<K, V> currentNode = mRoot;
        BTKeyValue<K, V> currentKey;
        int i, numberOfKeys;

        while (currentNode != null) {
            numberOfKeys = currentNode.mCurrentKeyNum;
            i = 0;
            currentKey = currentNode.mKeys[i];
            while ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) > 0)) {
                ++i;
                if (i < numberOfKeys) {
                    currentKey = currentNode.mKeys[i];
                }
                else {
                    --i;
                    break;
                }
            }

            if ((i < numberOfKeys) && (key.compareTo(currentKey.mKey) == 0)) {
                return currentKey.mValue;
            }

            if (key.compareTo(currentKey.mKey) > 0) {
                currentNode = BTNode.getRightChildAtIndex(currentNode, i);
            }
            else {
                currentNode = BTNode.getLeftChildAtIndex(currentNode, i);
            }
        }

        return null;
    }

}
