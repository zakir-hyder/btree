package main.java;

/**
 * Created by zakirhyder on 6/23/17.
 */

public class BTNode<K extends Comparable, V>
{

    public final static int MIN_DEGREE          =   5; // t
    public final static int LOWER_BOUND_KEYNUM  =   MIN_DEGREE - 1; // t-1
    public final static int UPPER_BOUND_KEYNUM  =   (MIN_DEGREE * 2) - 1; // 2t - 1


    protected int mCurrentKeyNum;
    protected BTNode mChildren[];
    protected boolean mIsLeaf;
    protected BTKeyValue<K, V> mKeys[];



    public BTNode[] getmChildren() {
        return mChildren;
    }

    public int getmCurrentKeyNum() {
        return mCurrentKeyNum;
    }


    public BTNode() {
        mCurrentKeyNum = 0;
        mChildren = new BTNode[UPPER_BOUND_KEYNUM + 1];
        mIsLeaf = true;
        mKeys = new BTKeyValue[UPPER_BOUND_KEYNUM];
    }


    protected static BTNode getChildNodeAtIndex(BTNode btNode, int keyIdx, int nDirection) {
        if (btNode.mIsLeaf) {
            return null;
        }

        keyIdx += nDirection;
        if ((keyIdx < 0) || (keyIdx > btNode.mCurrentKeyNum)) {
            return null;
        }
        return btNode.mChildren[keyIdx];
    }

    protected static BTNode getLeftChildAtIndex(BTNode btNode, int keyIdx) {
        return getChildNodeAtIndex(btNode, keyIdx, 0);
    }


    protected static BTNode getRightChildAtIndex(BTNode btNode, int keyIdx) {
        return getChildNodeAtIndex(btNode, keyIdx, 1);
    }
}
