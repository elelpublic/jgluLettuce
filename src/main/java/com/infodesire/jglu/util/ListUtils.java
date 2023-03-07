package com.infodesire.jglu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils {

    public static List<String> l( String... words ) {
        List<String> result = new ArrayList<>();
        Collections.addAll( result, words );
        return result.size() == 0 ? null : result;
    }

}
