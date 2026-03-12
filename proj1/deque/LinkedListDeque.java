package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private class Node{
        public T value;
        public Node last;
        public Node next;
        public Node(Node l,T a,Node n){
            last=l;
            value=a;
            next=n;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel=new Node(null,null,null);
        sentinel.last=sentinel;
        sentinel.next=sentinel;
        size=0;
    }

    @Override
    public void addFirst(T item){
        Node qwe=new Node(sentinel,item,sentinel.next);
        sentinel.next.last=qwe;
        sentinel.next=qwe;
        size+=1;
    }

    @Override
    public void addLast(T item){
        Node qwe=new Node(sentinel.last,item,sentinel);
        sentinel.last.next=qwe;
        sentinel.last=qwe;
        size+=1;
    }

    @Override
    public int size(){
        return this.size;
    }

    @Override
    public void printDeque(){
        Node p=sentinel;
        while(p.next != sentinel){
            System.out.print(p.next.value + " ");
            p=p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst(){
        if(size>0) {
            Node p = sentinel.next;
            sentinel.next = p.next;
            p.next.last = sentinel;
            size--;
            return p.value;
        }
        return null;
    }

    @Override
    public T removeLast(){
        if(size>0) {
            Node p = sentinel.last;
            sentinel.last = p.last;
            p.last.next = sentinel;
            size--;
            return p.value;
        }
        return null;

    }

    @Override
    public T get(int index){
        if(index>=size){
            return null;
        }
        Node curr=sentinel.next;
        int now=0;
        while(now<index){
            curr=curr.next;
            now+=1;
        }
        return curr.value;
    }

    private T helpget(int curr,int des,Node p){
        if(curr==des){
            return p.value;
        }
        else{
            return helpget(curr+1,des,p.next);
        }
    }

    public T getRecursive(int index){
        if(index>=size){
            return null;
        }
        Node p=sentinel.next;
        return helpget(0,index,p);
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Deque)){
            return false;
        }
        else {
            Deque<?> oo= ((Deque<?>) o);
            int aa = oo.size();
            if (aa != size) {
                return false;
            }
            Node p=sentinel.next;
            for(int i=0;i<size;i++){
                if(!(p.value.equals(oo.get(i)))){
                    return false;
                }
                p=p.next;
            }
            return true;
        }
    }

    public Iterator<T> iterator(){
        return new it();
    }

    private class it implements Iterator<T>{
        int pos;

        public it(){
            pos=0;
        }

        public boolean hasNext(){
            return pos<size();
        }

        public T next(){
            if(!hasNext()) {
                return null;
            }
            T i=get(pos);
            pos+=1;
            return i;
        }
    }

}
