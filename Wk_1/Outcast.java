import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    // constructor takes a WordNet object
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
//        this.wordnet = new WordNet(wordnet);
        // need deep copy here?
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int outcast = 0;
        int preDistance = 0;
        int distance = 0;

        for (int i = 0; i < nouns.length; i++) {
            distance = 0;
            for (int j = 0; j < nouns.length; j++) {
                distance += wordnet.distance(nouns[i], nouns[j]);
            }

//            StdOut.println("nounI: " + nouns[i]
//                    + "     Distance: " + Integer.toString(distance));
            if (distance > preDistance) {
                preDistance = distance;
                outcast = i;
            }
        }
        return nouns[outcast];
    }

    // see test client below
    public static void main(String[] args) {

        WordNet wordnet = new WordNet(args[0], args[1]);

        Outcast outcast = new Outcast(wordnet);

        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);

            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
