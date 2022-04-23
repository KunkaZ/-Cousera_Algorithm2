import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {

    private final Integer[] index;
    private final char[] suffixes;
    private final int length;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null input");
//        Throw an IllegalArgumentException in the constructor if the argument is null. Throw an
//        IllegalArgumentException in the method index() if i is outside its prescribed range (between 0 and n − 1)
//  constructor must take time proportional to n log n
        length = s.length();
        index = new Integer[s.length()];
        suffixes = new char[length];
        for (int i = 0; i < length; i++) {
            suffixes[i] = s.charAt(i);
            index[i] = i;
        }
        Arrays.sort(index, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {

                for (int j = 0; j < length; j++) {
                    char a1 = suffixes[(a + j) % length];
                    char b1 = suffixes[(b + j) % length];
                    if (Character.compare(a1, b1) != 0)
                        return Character.compare(a1, b1);
                }
                return 0;
            }
        });
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if ((i < 0) || (i >= length))
            throw new IllegalArgumentException("i out of range");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        // call each public method for unit testing
        String s = "ABRACADABRA!";
        CircularSuffixArray csa = new CircularSuffixArray(s);
        StdOut.println("length:" + csa.length());
        for (int i = 0; i < csa.length(); i++) {
            StdOut.print(csa.index(i) + " ");
        }
    }

}
