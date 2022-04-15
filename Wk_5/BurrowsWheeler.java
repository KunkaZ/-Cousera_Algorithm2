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
        // input is first and t, we can sort t to get the first column of sorted suffixes
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
// if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        transform();
    }
}
