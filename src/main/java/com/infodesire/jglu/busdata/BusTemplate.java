package com.infodesire.jglu.busdata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Defines the fields and field types of a BusObject
 *
 */
public class BusTemplate implements Iterable<String> {

    private Map<String, BusType> fields = new HashMap<>();

    public boolean hasField( String fieldName ) {
        return fields.containsKey( fieldName );
    }

    public void setType( String fieldName, BusType type ) {
        fields.put( fieldName, type );
    }

    public BusType getType( String fieldName ) {
        return fields.get( fieldName );
    }

    @Override
    public Iterator<String> iterator() {
        return fields.keySet().iterator();
    }
}
