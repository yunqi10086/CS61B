package deque;

public class ArrayDeque<T> {
    public T[] array;
    public int nextfirst;
    public int nextlast;
    public int size;

    public ArrayDeque(){
        array=(T[]) new Object[8];
        nextfirst=4;
        nextlast=5;
        size=0;
    }

    private void resize(int cap){
        if(size==array.length || nextlast<=nextfirst) {  //因为塞满了所以扩容 or shrink when 两端都有东西
            T[] arr = (T[]) new Object[cap];
            int n_front = nextlast;
            int n_rear = size - n_front;
            System.arraycopy(array, 0, arr, 0, n_front);
            System.arraycopy(array, array.length - n_rear, arr, cap - n_rear, n_rear);
            nextfirst = cap - n_rear - 1;
            array = arr;
        }
        else{ // shrink when 两端空，中间有东西
            T[] arr = (T[]) new Object[cap];
            System.arraycopy(array,nextfirst+1,arr,(cap-size)/2,nextlast-nextfirst+1);
            nextfirst=((cap-size)/2-1)%cap;
            nextlast=nextfirst+size+1;
            array=arr;
        }
    }

    public void addFirst(T item){
        if(size==array.length){
            this.resize(size*2);
        }
        array[nextfirst]=item;
        size++;
        nextfirst--;
        if(nextfirst<0){
            nextfirst+=array.length;
        }
    }

    public void addLast(T item){
        if(size==array.length){
            this.resize(size*2);
        }
        array[nextlast]=item;
        size++;
        nextlast++;
        if(nextlast>=array.length){
            nextlast-=array.length;
        }
    }

    public boolean isEmpty(){
        return size==0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int curr=nextfirst+1;
        for(int i=0;i<size;i++){
            System.out.print(array[curr]);
            curr=(curr+1)%array.length;
        }
        System.out.println();
    }

    public T removeFirst(){
        if(size==0){
            return null;
        }
        nextfirst=(nextfirst+1)%array.length;
        T fir=array[nextfirst];
        array[nextfirst]=null;
        double a=(double)size/(double)array.length;
        if(a<0.25){
            resize((int)(0.25*array.length));
        }
        return fir;
    }

    public T removeLast(){
        if(size==0){
            return null;
        }
        nextlast=((nextlast-1)+array.length)%array.length;
        T las=array[nextlast];
        array[nextlast]=null;
        double a=(double)size/(double)array.length;
        if(a<0.25){
            resize((int)(0.25*array.length));
        }
        return las;
    }

    public T get(int index){
        if(index>=size){
            return null;
        }
        int a=(index+nextfirst+1)%array.length;
        return array[a];
    }





}
