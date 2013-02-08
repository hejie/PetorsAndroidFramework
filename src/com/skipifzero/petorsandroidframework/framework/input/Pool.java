package com.skipifzero.petorsandroidframework.framework.input;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {
	public interface PoolObjectFactory<T> {
	    public T createObject();
	}
	
    private final List<T> freeObjects;
    private final PoolObjectFactory<T> factory;
    private final int maxSize;

    public Pool(PoolObjectFactory<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<T>(maxSize);
    }

    public T getRecycledObject() {
    	//If there aren't any free objects to reuse, make a new one.
    	if(freeObjects.size() <= 0){
    		return factory.createObject();
    	}
    	//Return an old object to reuse.
    	else{
    		return freeObjects.remove(freeObjects.size() -1);
    	}
    }

    public void free(T object) {
    	//Adds object to list of free objects. If the list is larger than the max size the object will be consumed by the Garbage Collector.
        if (freeObjects.size() < maxSize){
            freeObjects.add(object);
        }
    }
}

