public class BST {
    private String key;
    private BST left;
    private BST right;

    public BST(String key){
        this.key=key;
        left=null;
        right=null;
    }

    //返回以key为根的树
    public static BST find(BST T, String key){
        if(T==null){
            return null;
        }
        if(T.key.equals(key)){
            return T;
        }
        if(T.key.compareTo(key) > 0){
            return find(T.left, key);
        }
        if(T.key.compareTo(key) < 0){
            return find(T.right, key);
        }
        return null;
    }

    public static BST insert(BST T, String key){
        if(T==null){
            return new BST(key);
        }
        if(T.key.equals(key)){
            return T;
        }
        if(T.key.compareTo(key) > 0){
            T.left = insert(T.left, key);
            return T;
        }
        if(T.key.compareTo(key) < 0){
            T.right = insert(T.right, key);
            return T;
        }
        return T;
    }

    public static BST delete(BST T, String key){
        if(T == null){
            System.out.println("don't involve key!");
            return T;
        }
        if(T.key.compareTo(key) > 0){
            T.left = delete(T.left, key);
            return T;
        }
        if(T.key.compareTo(key) < 0){
            T.right = delete(T.right, key);
            return T;
        }
        else{
                if(T.left == null) {
                    return T.right;
                } else if(T.right == null) {
                    return  T.left;
                }
                BST m = T.left;
                while(m.right != null){
                    m = m.right;
                }
                T.key = m.key;
                T.left = delete(T.left, m.key);
                return T;
            }
        }
    }

}
