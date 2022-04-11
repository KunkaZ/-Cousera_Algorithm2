import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;


public class MoveToFront {


    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        String R = buildR();


        StdOut.println(R);

//        R = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String input = "ABRACADABRA";
        Queue<Integer> output = new Queue<Integer>();

        for (int i = 0; i < input.length(); i++) {

            int id = R.indexOf(input.charAt(i));

            StdOut.println("input char:" + input.charAt(i) + " id:" + id);
            StdOut.println("Seq: " + R);

            if (id >= 0) {
                output.enqueue(id);
                // Check this output is correct, when id == 256
                R = move(R, id);

            } else {
                throw new IllegalArgumentException("String:" + input.charAt(i) + " not in ASCII");
            }
            StdOut.println("Seq: " + R);
        }

        StdOut.println(output.toString());
    }


    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        Integer[] input = {65, 66, 82, 2, 68, 1, 69, 1, 4, 4, 2, 38};
//        Integer[] input = {0, 0, 15, 24, 2, 1, 3, 1, 25, 4, 2};

        String R = buildR();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length; i++) {
            int id = input[i];

            if ((id < 0) || (id > 255)) {
                throw new IllegalArgumentException(" id:" + id);
            }
            StdOut.println("id:" + id);
            StdOut.println("Seq:" + R.substring(0, 20));
            StdOut.println("id of R:" + R.indexOf(id));

            StdOut.println("id012:" + R.charAt(0) + " " + R.charAt(1) + " " + R.charAt(2));


            output.append(R.charAt(id));
            R = move(R, id);
            StdOut.println(output.toString());
        }
        StdOut.println(output.toString());
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
//        for (int i = 65; i < 91; i++) {
            strBld.append(Character.toString(i));
        }
        return strBld.toString();

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
//        if (args[0] == "-") {
//        encode();
//        } else if (args[0] == "+") {
        decode();
//        }


    }
}
