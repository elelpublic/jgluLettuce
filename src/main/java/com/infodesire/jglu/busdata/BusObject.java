package com.infodesire.jglu.busdata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A business data object containing a set of named value fields (BusValue).
 *
 * To get all field names use iterator() or use the object as an Iterable<String>.
 *
 */
public class BusObject implements Iterable<String> {

    private final String key;
    private Map<String, BusValue> data = new HashMap<>();

    public BusObject( String key ) {
        this.key = key;
    }

    /**
     * @return Primary key identifier of business object
     *
     */
    public String getKey() {
        return key;
    }

    public void set( String name, BusValue value ) {
        data.put( name, value );
    }

    public BusValue get( String name ) {
        return data.get( name );
    }

    public void remove( String name ) {
        data.remove( name );
    }

    /**
     * @return All field names
     *
     */
    @Override
    public Iterator<String> iterator() {
        return data.keySet().iterator();
    }

}
