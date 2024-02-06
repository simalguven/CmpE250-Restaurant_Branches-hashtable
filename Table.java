import java.util.LinkedList;

public class Table <E>{
    public static final int DEFAULT_TABLE_SIZE=11;
    private LinkedList<E>[] array;
    private int currentSize;


    @SuppressWarnings("unchecked")
    Table(int size){
        array = new LinkedList[nextPrime(size)];
        for(int i = 0; i < array.length; i++) {
            array[i] = new LinkedList<>();
        }
        currentSize = 0;

    }

    public void insert(E value) {
        LinkedList<E> llToInsert = array[hash(value)];
        if(!llToInsert.contains(value)) {
            llToInsert.add(value);
            currentSize++;
            if(currentSize > array.length) {
                rehash();
            }


        }
    }

    public LinkedList<E>[] getArray() {
        return array;
    }

    public E getelement(E obj, String name){
        int index= hash(obj);

        for(E value: array[index]){
            if(value.equals(obj)){
                return value;
            }

        }
        return null;
    }
    public void remove(E value) {
        LinkedList<E> listtoRemove = array[hash(value)];
        if(listtoRemove.contains(value)) {
            listtoRemove.remove(value);
            currentSize--;
        }
    }

    public boolean contains(E value) {
        LinkedList<E> listtoCheck = array[hash(value)];
        return listtoCheck.contains(value);
    }
    public int hash(E value) {
        int hashValue = value.toString().hashCode();
        hashValue = hashValue % array.length;
        if(hashValue < 0) {
            hashValue += array.length;
        }
        return hashValue;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        LinkedList<E>[] oldarray = array;
        array = (LinkedList<E>[]) new LinkedList[nextPrime(array.length * 2)];
        for(int i = 0; i < array.length; i++) {
            array[i] = new LinkedList<>();
        }
        currentSize = 0;
        for(LinkedList<E> list : oldarray) {
            for(E item: list) {
                insert(item);
            }
        }
    }

    private static int nextPrime(int currentPrime) {
        if(currentPrime % 2 == 0) {
            currentPrime++;
        }
        while(!isPrime(currentPrime)) {
            currentPrime += 2;
        }
        return currentPrime;
    }

    private static boolean isPrime(int n) {
        if(n == 2 || n == 3) {
            return true;
        }
        if (n == 1 || n % 2 == 0) {
            return false;
        }
        for(int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;

    }



}
