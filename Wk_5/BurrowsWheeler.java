import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
// reading from standard input and writing to standard output
    public static void transform() {

//        String input = "ABRACADABRA!";
        String input = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(input);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
            }
        }
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(input.charAt(input.length() - 1));
            } else {
                BinaryStdOut.write(input.charAt(csa.index(i) - 1));
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int R = 256;
        // input is first and t, we can sort t to get the first column of sorted suffixes
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();

//        int first = 3;
//        String t = "ARD!RCAAAABB";

        char[] inputSortedChar = t.toCharArray();

        int[] next = new int[t.length()];

        int[] duplicatedCnts = new int[R + 1];

        // use key indexed string sorting
        for (int i = 0; i < t.length(); i++) {
            duplicatedCnts[t.charAt(i) + 1]++;
        }

        for (int r = 0; r < R; r++) {
            duplicatedCnts[r + 1] += duplicatedCnts[r];
        }

        for (int i = 0; i < t.length(); i++) {
            int pos = duplicatedCnts[t.charAt(i)]++;
            inputSortedChar[pos] = t.charAt(i);
            next[pos] = i;  // i is the original index of sorted char
        }
//
//        StdOut.println(Arrays.toString(next));
//        StdOut.println("inputSortedChar" + Arrays.toString(inputSortedChar));


        int lastId = first;
        for (int i = 0; i < t.length(); i++) {
            BinaryStdOut.write(inputSortedChar[lastId]);
//            StdOut.println(next[first]);
//            StdOut.println(next[first] + " " + inputSortedChar[lastId]);
            lastId = next[lastId];
        }


        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
// if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {

        if (args[0].equals("-")) {

            BurrowsWheeler.transform();
        }
        if (args[0].equals("+")) {
            BurrowsWheeler.inverseTransform();
        }
    }
}
