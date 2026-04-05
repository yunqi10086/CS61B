package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private int size = 0;
    private BSTNode<K, V> root = null;

    public void printInOrder(){
        if(root == null) return;
        root.print_in_order();
    }

    @Override
    public void clear(){
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key){
        if(root == null) return false;
        return root.contain_key(key);
    }

    @Override
    public V get(K key){
        if(root == null) return null;
        return root.get(key);
    }

    @Override
    public int size(){
        return this.size;
    }

    @Override
    public void put(K key, V value){
        if(root == null){
            root = new BSTNode<>(key, value);
            size += 1;
            return;
        }
        if(root.put(key, value)) this.size += 1;
    }

    @Override
    public Set<K> keySet(){
        throw new UnsupportedOperationException();
    }
    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key){
        if (!containsKey(key)) return null;
        if(root == null) return null;
        V ans = this.get(key);
        root = remove_helper(root, key);
        size -= 1;
        return ans;
    }
    @Override
    public V remove(K key, V value){
        if (!containsKey(key)) return null;
        if(root == null) return null;
        if(!this.get(key).equals(value)) return null;
        V ans = this.get(key);
        root = remove_helper(root, key);
        size -= 1;
        return ans;
    }
    private BSTNode<K, V> remove_helper(BSTNode<K, V>node, K key){
        if(node == null) return null;
        int cmp = node.key.compareTo(key);
        if(cmp < 0){
            node.right_map = remove_helper(node.right_map, key);
            return node;
        }else if(cmp > 0){
            node.left_map = remove_helper(node.left_map, key);
            return node;
        }else{
            if(node.left_map == null) return node.right_map;
            if(node.right_map == null) return node.left_map;

            BSTNode<K, V> pre = node.left_map;
            while(pre.right_map != null){
                pre = pre.right_map;
            }

            node.key = pre.key;
            node.value = pre.value;
            node.left_map = remove_helper(node.left_map, pre.key);
            return node;
        }
    }


    private static class BSTNode<K extends Comparable<K>, V> {

        public K key;
        public V value;
        private BSTNode<K, V> left_map;
        private BSTNode<K, V> right_map;

        public BSTNode(K k, V v){
            key = k;
            value = v;
            left_map = null;
            right_map = null;
        }

        public boolean contain_key(K key){
            if(this.key.equals(key)) return true;
            if(this.key.compareTo(key) > 0 && this.left_map != null) return left_map.contain_key(key);
            if(this.key.compareTo(key) < 0 && this.right_map != null) return right_map.contain_key(key);
            return false;
        }

        public V get(K key){
            if(this.key.equals(key)) return this.value;
            if(this.key.compareTo(key) > 0 && this.left_map != null) return left_map.get(key);
            if(this.key.compareTo(key) < 0 && this.right_map != null) return right_map.get(key);
            return null;
        }

        public boolean put(K key, V value){
            if(this.key == null && this.value == null){
                this.key = key;
                this.value = value;
                return true;
            }
            if(this.key.equals(key)) {
                this.value = value;
                return false;
            }
            if(this.key.compareTo(key) > 0){
                if(this.left_map == null){
                    this.left_map = new BSTNode<>(key, value);
                    return true;
                }else{
                    return left_map.put(key, value);
                }
            }
            else if(this.key.compareTo(key) < 0){
                if(this.right_map == null){
                    this.right_map = new BSTNode<>(key, value);
                    return true;
                }else{
                    return right_map.put(key, value);
                }
            }
            return false;
        }

        public void print_in_order(){
            if(this.key == null && this.value == null) return;
            if(this.left_map != null) this.left_map.print_in_order();
            System.out.println("(" + this.key + ", " + this.value + ")" + '\n');
            if(this.right_map != null) this.right_map.print_in_order();
        }



    }

}
