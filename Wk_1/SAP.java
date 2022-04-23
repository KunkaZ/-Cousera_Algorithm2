import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class SAP {
    // constructor takes a digraph (not necessarily a DAG)
    private final Digraph G;

    public SAP(Digraph G) {

        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {

        validateVertex(v);
        validateVertex(w);

        int[] ret = getAnLen(v, w);
        int an = ret[0];
        int len = ret[1];
        if (an >= 0) return len;
        else return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {

        validateVertex(v);
        validateVertex(w);

        int[] ret = getAnLen(v, w);

        return ret[0];
    }

    private int[] getAnLen(int v, int w) {

        BreadthFirstDirectedPaths bfsPathV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsPathW = new BreadthFirstDirectedPaths(G, w);

        int len = Integer.MAX_VALUE;
        int an = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfsPathV.hasPathTo(i) && bfsPathW.hasPathTo(i)) {
                if ((bfsPathV.distTo(i) + bfsPathW.distTo(i)) < len) {
                    len = bfsPathV.distTo(i) + bfsPathW.distTo(i);
                    an = i;
                }
            }
        }


//        Iterable<Integer> pathV = bfsPathV.pathTo(an);
//        Iterable<Integer> pathW = bfsPathW.pathTo(an);
//        StdOut.println("---get Ancestor and length: ");
//        StdOut.printf("\nv = %d, w = %d", v, w);
//        StdOut.printf("\nlength = %d, ancestor = %d\n", len, an);
//
//        StdOut.println("PathV:" + pathV.toString());
//        StdOut.println("PathW:" + pathW.toString());

        int[] ret = new int[2];
        ret[0] = an;
        ret[1] = len;
        return ret;
    }

    // Fix Problem below:
    // the distTo is not minimal distance with Iterable v,
    // such as distance(v1, d) = 10, distance(v2, d) = 1, but v1 is check first, so
    // marked[d] = true, the distTo[d] = 10, doesn't have chance to update to 1;
    // need add code to change bfs

    private static class BfsSource {

        private final boolean[] marked;  // marked[v] = is there an s->v path?
        private final int[] edgeTo;      // edgeTo[v] = last edge on shortest s->v path
        private final int[] distTo;      // distTo[v] = length of shortest s->v path

        public BfsSource(Digraph G, Iterable<Integer> sources) {
            marked = new boolean[G.V()];
            distTo = new int[G.V()];
            edgeTo = new int[G.V()];
            for (int v = 0; v < G.V(); v++)
                distTo[v] = Integer.MAX_VALUE;

            bfs(G, sources);
        }

        // BFS from multiple sources
        private void bfs(Digraph G, Iterable<Integer> sources) {
            Queue<Integer> q = new Queue<Integer>();

            for (int s : sources) {
                marked[s] = true;
                distTo[s] = 0;
                q.enqueue(s);
            }
            while (!q.isEmpty()) {
                int v = q.dequeue();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                        q.enqueue(w);
                    } else {
                        // Update distTo to the closest vertex
                        // enqueue current vertex for update following vertex
                        if (distTo[w] > (distTo[v] + 1)) {
                            distTo[w] = distTo[v] + 1;
                            q.enqueue(w);
                            edgeTo[w] = v; // should also update edgeTo here ????
                        }
                    }
                }
            }
        }

        public Iterable<Integer> pathTo(int v) {

            if (!marked[v]) return null;
            Stack<Integer> path = new Stack<Integer>();
            int x;
            for (x = v; distTo[x] != 0; x = edgeTo[x])
                path.push(x);
            path.push(x);
            return path;
        }
    }

    private int[] getAnLen(Iterable<Integer> v, Iterable<Integer> w) {

        if (v == null || w == null) throw new IllegalArgumentException("NULL Iterable.");
        validateVertex(v);
        validateVertex(w);

        BfsSource bfsPathV = new BfsSource(G, v);
        BfsSource bfsPathW = new BfsSource(G, w);

        int len = Integer.MAX_VALUE;
        int an = -1;
        for (int i = 0; i < G.V(); i++) {

            if (bfsPathV.marked[i] && bfsPathW.marked[i]) {
                if ((bfsPathV.distTo[i] + bfsPathW.distTo[i]) < len) {
                    len = (bfsPathV.distTo[i] + bfsPathW.distTo[i]);
                    an = i;
                }
            }
        }

//        Iterable<Integer> pathV = bfsPathV.pathTo(an);
//        Iterable<Integer> pathW = bfsPathW.pathTo(an);
//        StdOut.println("---get Ancestor and length: ");
////        StdOut.printf("\nv = %d, w = %d", v, w);
//        StdOut.printf("length = %d, ancestor = %d\n", len, an);
//
//        StdOut.println("PathV:" + pathV.toString());
//        StdOut.println("PathW:" + pathW.toString());

        int[] ret = new int[2];
        ret[0] = an;
        ret[1] = len;
        return ret;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {

        if (v == null || w == null) throw new IllegalArgumentException("NULL Iterable.");
        validateVertex(v);
        validateVertex(w);

        int[] ret = getAnLen(v, w);
        int an = ret[0];
        if (an >= 0) return ret[1];
        else return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {

        if (v == null || w == null) throw new IllegalArgumentException("NULL Iterable.");
        validateVertex(v);
        validateVertex(w);
        // Mark all nodes with v color,

        int[] ret = getAnLen(v, w);

        return ret[0];
    }

    private void validateVertex(Integer v) {
        int size = G.V();
        if ((v == null) || (v < 0 || v >= size))
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (size - 1));
    }

    private void validateVertex(Iterable<Integer> v) {

        int size = G.V();
        for (Integer i : v)
            if ((i == null) || (i < 0 || i >= size))
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (size - 1));
    }


    // do unit testing of this class
    public static void main(String[] args) {


        //
        // java-algs4 SAP docs/wordnet/digraph1.txt
//        In in = new In(args[0]);
//        Digraph G = new Digraph(in);
//        SAP sap = new SAP(G);
//        while (!StdIn.isEmpty()) {
//            int v = StdIn.readInt();
//            int w = StdIn.readInt();
//            int length = sap.length(v, w);
//            int ancestor = sap.ancestor(v, w);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        }

//         java-algs4 SAP docs/wordnet/digraph25.txt
        // test Group1:length = 4, ancestor = 3
//        Integer v_[] = {13, 23, 24};
//        Integer w_[] = {6, 16, 17};

        // test Group1:length = 2, ancestor = 1
//        Integer v_[] = {8, 15, 19};
//        Integer w_[] = {1, 17, 14};
//        Iterable<Integer> v = Arrays.asList(v_);
////
//        List<Integer> w = Arrays.asList(w_);
//        int length = sap.length(v, w);
//        int ancestor = sap.ancestor(v, w);
//        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);


        // test set distance
        //
//        In in = new In("test3_digraph25_new.txt");
//        Digraph G = new Digraph(in);
//        SAP sap = new SAP(G);
//        Integer v1_[] = {21, null};
//        Integer v2_[] = {5, 28};
//        Iterable<Integer> v = Arrays.asList(v1_);
//        Iterable<Integer> w = Arrays.asList(v2_);
//        int[] anLen = sap.getAnLen(v, w);
//
//        StdOut.printf("length = %d, ancestor = %d\n", anLen[1], anLen[0]);


        // test :
        // Integer v1_[] = {13, 15, 22, 18};
//        for (int i = 0; i < v1_.length; i++) {
//            distance = 0;
//            for (int j = 0; j < v1_.length; j++) {
//                distance += sap.length(v_[i], v_[j]);
//            }
//            StdOut.println("****** vertex: " + v_[i]
//                    + "     Distance: " + Integer.toString(distance));
//            if (distance > preDistance) {
//                preDistance = distance;
//                outcast = i;
//            }
//        }
//
//        StdOut.println("outcast: " + v_[outcast]
//                + "     Distance: " + Integer.toString(distance));
    }

}
