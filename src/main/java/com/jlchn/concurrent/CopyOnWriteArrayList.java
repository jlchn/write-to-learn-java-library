package com.jlchn.concurrent;


import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class CopyOnWriteArrayList<E> {

    private transient volatile Object[] array; // 1. volatile

    private final transient ReentrantLock lock = new ReentrantLock();

    private Object[] getArray(){
        return this.array;
    }

    private void setArray(Object [] arr){
        this.array = arr;
    }

    public CopyOnWriteArrayList(){
        this.setArray(new Object[0]);
    }

    public int size() {
        return this.getArray().length;
    }

    public boolean isEmpty() {
        return this.getArray().length == 0;
    }


    public E get(int index) {
        return (E) this.getArray()[index];
    }

    public int indexOf(Object o){
        return this.indexOf(this.getArray(), o);
    }

    public int indexOf(Object[] arr, Object o){
        return indexOf(arr, o, 0, arr.length);
    }

    public int indexOf(Object[] arr, Object o, int start, int end){
        if (o == null){
            for (int i = start; i < end; i++){
                if (arr[i] == null){
                    return i;
                }
            }
        }else{
            for (int i = start; i < end; i++){
                if (o.equals(arr[i])){
                    return i;
                }
            }
        }

        return -1;
    }


    public boolean contains(Object o) {
        Object[] elements = this.getArray();
        return indexOf(elements,o, 0, elements.length) >= 0;
    }

    public boolean add(E e) {

        this.lock.lock();
        try {
            int len = this.getArray().length + 1;
            Object[] copy = Arrays.copyOf(this.getArray(), len);
            copy[len - 1] = e;
            this.setArray(copy);
            return true;
        }finally {
            this.lock.unlock();
        }
    }



    public E set(int index, E newValue) {

        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException("Index: " + index +
                    ",Size: " + this.size());
        }
        Object oldValue = this.getArray()[index];
        this.lock.lock();
        try {
             if (oldValue != newValue) {
                int len = this.getArray().length;
                Object[] copy = Arrays.copyOf(this.getArray(), len);
                copy[index] = newValue;
                this.setArray(copy);
             }

             return (E) oldValue;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean remove(Object o) {

        Object[] copy = this.getArray();
        int index = this.indexOf(copy, o, 0, copy.length);

        if (index < 0 ) {
            return false;
        }

        int stepMoved = copy.length - 1 - index;

        this.lock.lock();
        try {
            Object[] newArray;
            if (stepMoved == 0){// rm the last one
               newArray = Arrays.copyOf(copy,copy.length -1);

            } else {
                newArray = new Object[copy.length  - 1];
                System.arraycopy(copy,0, newArray, 0, index);
                System.arraycopy(copy,index + 1, newArray, index, stepMoved);
            }
            this.setArray(newArray);
            return true;
        } finally {
            this.lock.unlock();
        }

    }

    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            setArray(new Object[0]);
        } finally {
            lock.unlock();
        }
    }





}
