package Clustering.overlapCoefficient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Mubin
 */
public class DocVector {

    HashMap<String, HashMap<String, Integer>> terms;
    HashMap<String, HashSet<Integer>> vector;

    public DocVector(HashMap<String, HashMap<String, Integer>> terms) {
        this.terms = terms;
        for(Map.Entry<String, HashMap<String, Integer>> e : terms.entrySet()) {
            vector.put(e.getKey(), new HashSet<>());
        }
    }

    public void setEntry(String field, String term) {
        if(terms.containsKey(field)) {
            if(terms.get(field).containsKey(term)) {
                vector.get(field).add(terms.get(field).get(term));
            }
        }
    }
}