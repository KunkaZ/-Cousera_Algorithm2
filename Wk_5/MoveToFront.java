import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;


public class MoveToFront {


    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        char[] letters = buildR();
        String input = BinaryStdIn.readString();

        for (int i = 0; i < input.length(); i++) {

//            StdOut.println("R: " + alphabet);
//            StdOut.println("input:" + input.charAt(i));
            char id = input.charAt(i);
            if (id >= 0) {
                move(letters, id, true);
            } else {
                throw new IllegalArgumentException("String:" + input.charAt(i) + " not in ASCII");
            }
        }
        BinaryStdOut.close();
    }


    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] letters = buildR();

        while (!BinaryStdIn.isEmpty()) {
            char id = BinaryStdIn.readChar();
//            StdOut.printf("id = %x ", id);
            if ((id < 0) || (id > 255)) {
                throw new IllegalArgumentException(" id:" + id);
            }
            BinaryStdOut.write(letters[id]);
            move(letters, letters[id], false);
        }
//        StdOut.println(" ");
        BinaryStdOut.close();
    }

    private static void move(char[] letters, char id, boolean writeChar) {

        // Check this output is correct, when id == 256
        char pre = letters[0];
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == id) {
                letters[0] = id;
                letters[i] = pre;
                if (writeChar) {
                    BinaryStdOut.write((char) i);
                }
                break;
            }
            char temp = letters[i];
            letters[i] = pre;
            pre = temp;
        }
    }

    private static char[] buildR() {
        char[] retChar = new char[256];
        for (int i = 0; i < 256; i++) {
            retChar[i] = (char) i;
        }
        return retChar;
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
