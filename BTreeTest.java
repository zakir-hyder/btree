package main.java;

/**
 * Created by zakirhyder on 6/24/17.
 */
public class BTreeTest {
    private final BTree<String, String> mBTree;

    public BTreeTest() {
        mBTree = new BTree<String, String>();
    }

    private void insert() {
        mBTree.insert("a", "hello");
        mBTree.insert("b", "hello!!");
        mBTree.insert("c", "world");
        mBTree.insert("d", "hey");
        mBTree.insert("x", "dude");
        mBTree.insert("y", "extra9");
        mBTree.insert("k", "extra4");
        mBTree.insert("l", "extra100");
        mBTree.insert("aa", "extra45");
        mBTree.insert("ab", "hello80");
        mBTree.insert("m", "world!81");
    }


    public static void main(String []args) {
        BTreeTest test = new BTreeTest();

        test.insert();

        if(test.mBTree.search("m") != null) {
            System.out.println("BTree validation is successful.");

        } else {
            System.out.println("BTree validation is not successful.");

        }
    }

}
