package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c){
        comparator=c;
    }

    public T max(){
        return max(comparator);
    }

    public T max(Comparator<T> c){
        if(size()==0){
            return null;
        }
        Iterator<T> it=iterator();
        T m=get(0);
        while(it.hasNext()){
            T i=it.next();
            if(comparator.compare(m,i)<0){
                m=i;
            }
        }
        return m;
    }





}
