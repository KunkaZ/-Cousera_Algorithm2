import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;

public class WordNet {

    //    private final ArrayList<String> synSets;
    private final ST<String, SET<Integer>> synSets;
    private final ST<Integer, String> idNouns;
    private final SAP wordSap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {

        if ((synsets == null) || (hypernyms == null)) {
            throw new IllegalArgumentException("null inputs of WordNet");
        }


        // init synsets
        In synsetsIn = new In(synsets);
        // init words of synsets into a Array

        this.synSets = new ST<String, SET<Integer>>();
        this.idNouns = new ST<Integer, String>();

        while (!synsetsIn.isEmpty()) {

            String[] words = synsetsIn.readLine().split(",");
            int index = Integer.parseInt(words[0]);
            idNouns.put(index, words[1]);

            String[] nouns = words[1].split(" ");
            for (int i = 0; i < nouns.length; i++) {

                if (synSets.contains(nouns[i])) {
                    synSets.get(nouns[i]).add(index);
                } else {
                    SET<Integer> ids = new SET<Integer>();
                    ids.add(index);
                    synSets.put(nouns[i], ids);
                }
            }
        }

        // init Digraph of wordnet
        In hypernymsIn = new In(hypernyms);
        Digraph wordNetG = new Digraph(idNouns.size());

        while (!hypernymsIn.isEmpty()) {

            String[] hyperNums = hypernymsIn.readLine().split(",");

            for (int i = 1; i < hyperNums.length; i++) {
                wordNetG.addEdge(Integer.parseInt(hyperNums[0]), Integer.parseInt(hyperNums[i]));
            }
        }

        // check if the graph is a rooted DAG
        DirectedCycle cycCheck = new DirectedCycle(wordNetG);
        if (cycCheck.hasCycle())
            throw new IllegalArgumentException("Graph has cycle");
        else {
            int rootNum = 0;
            for (int v = 0; v < wordNetG.V(); v++) {
                if (wordNetG.outdegree(v) == 0) rootNum++;
                if (rootNum > 1) {
//                    System.out.println("rootNum:" + Integer.toString(rootNum));
                    throw new IllegalArgumentException("Input WordNet is not rooted");
                }
            }

        }

        wordSap = new SAP(wordNetG);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synSets.keys();
    }

    // Question is a string has multipe word like "ASL American_sign_language"
    // is the word a WordNet noun?
    public boolean isNoun(String word) {

        if (word == null) {
            throw new IllegalArgumentException("NULL argument");
        }

//        System.out.println("word: " + word
//                + " " + synSets.contains(word));
        return synSets.contains(word);
    }

    private Iterable<Integer> getNounInt(String noun) {

        return synSets.get(noun);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {

        if ((nounA == null) || (nounB == null)) {
            throw new IllegalArgumentException("null inputs of distance");
        }

        if ((!isNoun(nounA)) || (!isNoun(nounB))) {
            throw new IllegalArgumentException(nounA + " or " + nounB + " NOT A WordNet NOUN");
        }
        Iterable<Integer> v1 = getNounInt(nounA);
        Iterable<Integer> v2 = getNounInt(nounB);
        return wordSap.length(v1, v2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {

        if ((nounA == null) || (nounB == null)) {
            throw new IllegalArgumentException("null inputs of sap");
        }

        if ((!isNoun(nounA)) || (!isNoun(nounB))) {
            throw new IllegalArgumentException(nounA + " or " + nounB + " " + "NOT A WordNet NOUN");
        }

        Iterable<Integer> v1 = getNounInt(nounA);
        Iterable<Integer> v2 = getNounInt(nounB);

        int an = wordSap.ancestor(v1, v2);
        if (an < 0) return null;
        else return idNouns.get(an);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // test command:
        // java-algs4 WordNet docs/wordnet/synsets6.txt  docs/wordnet/hypernyms3InvalidTwoRoots.txt
        // java-algs4 WordNet docs/wordnet/synsets6.txt  docs/wordnet/hypernyms6InvalidCycle+Path.txt
//        synsets6.txt, hypernyms6InvalidCycle+Path.txt
        // nounA: zero
        // nounB: 6
        // sap: two 2
        // distance: 4
        // isNoun: event false
        // isNoun: seven 7 true
//        if (true) {
//            System.out.println("------Unit Test------");
//            WordNet testWN = new WordNet(args[0], args[1]);
//            Iterable<String> Words = testWN.nouns();
//            System.out.println(Words.toString());
//
//            System.out.println("test function distance();");
//            String nounA = "6";
//            String nounB = "zero";
//            String nounC = "event";
//            String nounD = "seven 7";
//            System.out.println("nounA: " + nounA
//                    + "\nnounB: " + nounB
//                    + "\nsap: " + testWN.sap(nounA, nounB)
//                    + "\ndistance: " + Integer.toString(testWN.distance(nounA, nounB))
//                    + "\nisNoun: " + nounC + " " + Boolean.toString(testWN.isNoun(nounC))
//                    + "\nisNoun: " + nounD + " " + Boolean.toString(testWN.isNoun(nounD))
//            );
//        }
//
//        if (true) {
//            System.out.println("------Unit Test------");
//            WordNet testWN = new WordNet("docs/wordnet/synsets15.txt", "docs/wordnet/hypernyms15Tree2.txt");
//            Iterable<String> Words = testWN.nouns();
//            System.out.println(Words.toString());
//
//            System.out.println("test function distance();");
//            String nounA = "six";
//            String nounB = "eleven";
//            String nounC = "event";
//            String nounD = "seven 7";
//            System.out.println("nounA: " + nounA
//                    + "\nnounB: " + nounB
//                    + "\nsap: " + testWN.sap(nounA, nounB)
//                    + "\ndistance: " + Integer.toString(testWN.distance(nounA, nounB))
//                    + "\nisNoun: " + nounC + " " + Boolean.toString(testWN.isNoun(nounC))
//                    + "\nisNoun: " + nounD + " " + Boolean.toString(testWN.isNoun(nounD))
//            );
//        }
    }
}
