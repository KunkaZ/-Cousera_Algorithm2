import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.HashSet;

public class BoggleSolver {

    private final TST<Integer> dict;

    private HashSet<String> allValidWords;


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException("Null input of BoggleSolver(String[] dictionary)!");

        dict = new TST<Integer>();
        for (String word : dictionary) {
            dict.put(word, 0);
        }

    }

    private static class RowAndCol {
        int col;
        int row;


        // both 0 <= col < width and 0 <= row < height
        RowAndCol(int row, int col) {
            this.col = col;
            this.row = row;

        }
    }

    private RowAndCol get2DRowCol(int index, int width) {
        // index starting from 0 to width * height -1
        int col = index % width;
        int row = index / width;
        return new RowAndCol(row, col);
    }

    private int get1dIndex(int row, int col, int width) {
        // index starting from 0 to width*height -1
//        StdOut.println("r:" + row + " c:" + col + " width:" + width);
        return row * width + col;
    }


    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null)
            throw new IllegalArgumentException("Null input of getAllValidWords(BoggleBoard board)!");

        allValidWords = new HashSet<String>();

//        StdOut.println(board.toString());
        int width = board.cols();
        int height = board.rows();

        for (int v = 0; v < board.rows() * board.cols(); v++) {
            StringBuilder dfsWord = new StringBuilder();
            dfsWord.append(get1dLetter(v, board));
            boolean[] marked = new boolean[width * height];
//            StdOut.println("Loop V:" + v + " " + dfsWord.toString());
            dfs(v, marked, board, dfsWord);
        }

        return allValidWords;
    }

    private void dfs(int v, boolean[] marked, BoggleBoard board, StringBuilder dfsWord) {
        marked[v] = true;
//        StdOut.println("v:" + v + " dfsWord:" + dfsWord.toString());
        for (int w : getAdjacentVertex(v, board.cols(), board.rows())) {
//            StdOut.println(v + "->" + w);
            if (!marked[w]) {
                dfsWord.append(get1dLetter(w, board));
//                StdOut.println("w:" + w + " dfsWord:" + dfsWord.toString());
                String tempWord = dfsWord.toString();
                Queue<String> lp = (Queue<String>) dict.keysWithPrefix(tempWord);
                if (lp.size() > 0) {
                    // tempWord is a word OR a prefix of a word
                    if ((dict.contains(tempWord)) && (tempWord.length() >= 3)) {

                        allValidWords.add(tempWord);  // remove duplicated word, Should I?

                    }
                    dfs(w, marked, board, dfsWord);
                }

//                    StdOut.println("Return:" + " dfsWord:" + dfsWord);
                // remove new added letters when exist current dfs function call
                dfsWord.setLength(dfsWord.length() - get1dLetter(w, board).length());

            }
        }
        // reset marked to false when returned from current vertex
        marked[v] = false;
    }

    private String get1dLetter(int v, BoggleBoard board) {

        RowAndCol rl = get2DRowCol(v, board.cols());
        char letter = (board.getLetter(rl.row, rl.col));
        if (letter == 'Q') {
            return "QU";
        } else {
            return String.valueOf(letter);
        }
    }

    // all use 1-D array
    // return the set of all adjacent vertex (integer) from a 2D board
    private Iterable<Integer> getAdjacentVertex(int v, int width, int height) {
        RowAndCol rowCol = get2DRowCol(v, width);
        ArrayList<Integer> retVertex = new ArrayList<Integer>();

        for (int r = rowCol.row - 1; r <= rowCol.row + 1; r++) {
            for (int c = rowCol.col - 1; c <= rowCol.col + 1; c++) {
                if (!((r < 0) || (r >= height) || (c < 0) || (c >= width) ||
                        ((r == rowCol.row) && (c == rowCol.col)))) {
                    retVertex.add(get1dIndex(r, c, width));
                }
            }
        }
        return retVertex;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null)
            throw new IllegalArgumentException("Null input of scoreOf(String word)!");

        if (dict.contains(word)) {
            if ((word.length() >= 3) && (word.length() <= 4)) {
                return 1;
            } else if (word.length() == 5) {
                return 2;
            } else if (word.length() == 6) {
                return 3;
            } else if (word.length() == 7) {
                return 5;
            } else if (word.length() >= 8) {
                return 11;
            }
        }
        return 0;
    }

    /**
     * Unit tests the BoggleBoard data type.
     */
    public static void main(String[] args) {

//        In in = new In(args[0]);
        In in = new In("dictionary-algs4.txt");
//        In in = new In("testDict.txt");
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
//        BoggleBoard board = new BoggleBoard(args[1]);
        BoggleBoard board = new BoggleBoard("board4x4.txt");
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
