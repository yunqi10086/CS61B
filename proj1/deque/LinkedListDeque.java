package deque;

public class LinkedListDeque<T> {
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
    public void addFirst(T item){
        Node qwe=new Node(sentinel,item,sentinel.next);
        sentinel.next.last=qwe;
        sentinel.next=qwe;
        size+=1;
    }

    public void addLast(T item){
        Node qwe=new Node(sentinel.last,item,sentinel);
        sentinel.last.next=qwe;
        sentinel.last=qwe;
        size+=1;
    }

    public boolean isEmpty(){
        return sentinel.last == sentinel && sentinel.next == sentinel;
    }

    public int size(){
        return this.size;
    }

    public void printDeque(){
        Node p=sentinel;
        while(p.next != sentinel){
            System.out.print(p.next.value + " ");
            p=p.next;
        }
        System.out.println();
    }

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


}
