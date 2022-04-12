import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;


public class MoveToFront {


    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        String R = buildR();
        String input = BinaryStdIn.readString();

        for (int i = 0; i < input.length(); i++) {

            int id = R.indexOf(input.charAt(i));
            if (id >= 0) {
                BinaryStdOut.write((char) id);
                R = move(R, id);
            } else {
                throw new IllegalArgumentException("String:" + input.charAt(i) + " not in ASCII");
            }
        }
        BinaryStdOut.close();
    }


    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        String R = buildR();


        while (!BinaryStdIn.isEmpty()) {
            int id = BinaryStdIn.readChar();
//            StdOut.printf("id = %x ", id);
            if ((id < 0) || (id > 255)) {
                throw new IllegalArgumentException(" id:" + id);
            }

            BinaryStdOut.write(R.charAt(id));
            R = move(R, id);
        }
//        StdOut.println(" ");
        BinaryStdOut.close();
    }

    private static String move(String R, int id) {

        // Check this output is correct, when id == 256
        if (id == 0) {
            R = R.substring(1) + R.charAt(0);
        } else if (id == (R.length() - 1)) {
            R = R.charAt(id) + R.substring(0, id - 1);
        } else
            R = R.charAt(id) + R.substring(0, id) + R.substring(id + 1);

        return R;
    }

    private static String buildR() {
        StringBuilder strBld = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            strBld.append((char) i);
        }
        return strBld.toString();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {

            MoveToFront.encode();
        }
        if (args[0].equals("+")) {
            MoveToFront.decode();
        }
//        StdOut.println(" ");
//        encode();

    }
}
