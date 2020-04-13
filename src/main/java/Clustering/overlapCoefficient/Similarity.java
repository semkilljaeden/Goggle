package Clustering.overlapCoefficient;

import java.util.HashSet;
import java.util.Set;

public class Similarity {
    public static double similarity(DocVector d1, DocVector d2) {
        double result = 0;
        for(String s : ClusterBuilder.businessDocumentFieldNames) {
            result += overlapCoefficient(d1.vector.get(s), d2.vector.get(s));
        }
        return result;
    }

    public static double overlapCoefficient(HashSet<Integer> a, HashSet<Integer> b) {
        Set<Integer> result = new HashSet<>(a);
        result.retainAll(b);
        return  result.size() / (a.size() < b.size() ? a.size() : b.size());
    }

}