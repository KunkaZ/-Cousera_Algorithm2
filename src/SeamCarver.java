import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    // do not construct explicit DirectedEdge and EdgeWeightedDigraph objects.
    private Picture pic;
    private int w;
    private int h;
    private int curW;
    private int curH;
    private double[] energy;

    private int s;
    private int d;

    private Digraph horiGraph;
    private Digraph vertGraph;

    private class ColAndRow {
        int col;
        int row;
        int index;

        // both 0 <= col < width and 0 <= row < height
        ColAndRow(int col, int row) {
            this.col = col;
            this.row = row;
            this.index = row * w + col + 1;
        }

        ColAndRow(int index) {
            if (index <= 0) {
                throw new IllegalArgumentException("ColAndRow: index is zero:" + index);
            }
            this.index = index;
            this.col = (index - 1) % w;
            this.row = (index - 1) / w;
        }
    }

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Null picture");
        }
        updateSeamCarver(picture);
    }

    private double getGradientSq(int color1, int color2) {

        double deltaR = ((color1 >> 16) & 0xFF) - ((color2 >> 16) & 0xFF);
        double deltaG = ((color1 >> 8) & 0xFF) - ((color2 >> 8) & 0xFF);
        double deltaB = (color1 & 0xFF) - (color2 & 0xFF);

        return (deltaR * deltaR + deltaB * deltaB + deltaG * deltaG);
    }

    // y = col; x = row
    private double getPixEnergy(ColAndRow xy) {
        int col = xy.col;
        int row = xy.row;

        if ((col == 0) || (row == 0) || (col == (w - 1)) || (row == (h - 1))) {
            return 1000.0;
        } else {
            double xG = getGradientSq(pic.getRGB(col - 1, row), pic.getRGB(col + 1, row));
            double yG = getGradientSq(pic.getRGB(col, row - 1), pic.getRGB(col, row + 1));
            return Math.sqrt(xG + yG);
        }
    }


    private void updateSeamCarver(Picture picture) {
        this.pic = new Picture(picture);
        w = picture.width();
        h = picture.height();
        curW = w;
        curH = h;
        energy = new double[w * h + 2];

        // vertex 0 and w*h+1 is added vertex
        // vertex 1 to w*h are pixels
        this.s = 0;
        this.d = w * h + 1;
        energy[s] = 0;
        energy[d] = 0;

        vertGraph = new Digraph(w * h + 2);
        horiGraph = new Digraph(w * h + 2);
        ColAndRow colRow;

        for (int i = 1; i <= w; i++) {
            colRow = new ColAndRow(i);  // first row
            // add add edge from s to first row
            vertGraph.addEdge(s, colRow.index);

            colRow = new ColAndRow(w * (h - 1) + i); // last row
            // add add edge from last row to d
            vertGraph.addEdge(colRow.index, d);
        }

        for (int i = 1; i <= h; i++) {
            colRow = new ColAndRow((i - 1) * w + 1); // first column
            // add add edge from s to first column
            horiGraph.addEdge(s, colRow.index);

            colRow = new ColAndRow(i * w);  // last column
            // add add edge from last column to d
            horiGraph.addEdge(colRow.index, d);
        }

        ColAndRow colRowOrig;
        for (int i = 1; i <= w * h; i++) {
            colRowOrig = new ColAndRow(i);
            energy[i] = getPixEnergy(colRowOrig);

            if (colRowOrig.row < (h - 1)) {
                if (colRowOrig.col > 0) {
                    colRow = new ColAndRow(colRowOrig.col - 1, colRowOrig.row + 1);  // (col-1, row+1)
                    vertGraph.addEdge(i, colRow.index);
                }

                if (colRowOrig.col < w - 1) {
                    colRow = new ColAndRow(colRowOrig.col + 1, colRowOrig.row + 1); // (col+1, row+1)
                    vertGraph.addEdge(i, colRow.index);
                }

                colRow = new ColAndRow(colRowOrig.col, colRowOrig.row + 1); // (col, row+1)
                vertGraph.addEdge(i, colRow.index);
            }

            if (colRowOrig.col < (w - 1)) {

                if (colRowOrig.row > 0) {
                    colRow = new ColAndRow(colRowOrig.col + 1, colRowOrig.row - 1);  // (col+1, row-1)
                    horiGraph.addEdge(i, colRow.index);
                }

                if (colRowOrig.row < h - 1) {
                    colRow = new ColAndRow(colRowOrig.col + 1, colRowOrig.row + 1);  // (col+1, row+1)
                    horiGraph.addEdge(i, colRow.index);
                }

                colRow = new ColAndRow(colRowOrig.col + 1, colRowOrig.row);  // (col+1, row)
                horiGraph.addEdge(i, colRow.index);
            }
        }
    }

    private static int[] getSpt(Digraph g, double[] energy, int s) {

        double[] energyTo = new double[g.V()]; // energyTo[v] = length of smallest energy s->d path
        boolean[] marked = new boolean[g.V()]; // marked[v] = is there an s->v path?
        int[] edgeTo = new int[g.V()];  // edgeTo[v] = last edge on smallest energy s->d path
        IndexMinPQ<Double> pq;

        energyTo[s] = energy[s];

        pq = new IndexMinPQ<Double>(g.V());
        pq.insert(s, 0.0);
        marked[s] = true;
        while (!pq.isEmpty()) {
            int v = pq.delMin();
//            StdOut.printf("\n V %d  ", v);
            for (int w : g.adj(v)) {
                if (!marked[w]) {
                    edgeTo[w] = v;
                    energyTo[w] = energyTo[v] + energy[w];
                    marked[w] = true;
                    pq.insert(w, energyTo[w]);
                } else {
                    if (energyTo[w] > energyTo[v] + energy[w]) {
//                        StdOut.printf("\n Revisit Vertex %d - > %d, energyTo:%d ", edgeTo[w], w, energyTo[w]);
                        energyTo[w] = energyTo[v] + energy[w];
                        edgeTo[w] = v; // test this code
                        pq.insert(w, energyTo[w]);
                    }
                }
            }
        }

        return edgeTo;
    }

//    private void printEnergy() {
//        StdOut.print("\nEnergy Matrix:");
//        for (int r = 0; r < h; r++) {
//            StdOut.printf("\n row [%d]:", r);
//            for (int c = 0; c < w; c++) {
//                StdOut.printf("%6.0f ", energy(c, r));
//            }
//        }
//        StdOut.print("\n");
//    }

//    private void printPic() {
//        StdOut.print("\nPic:");
//        for (int r = 0; r < h; r++) {
//            StdOut.printf("\n row [%d]:", r);
//            for (int c = 0; c < w; c++) {
//                StdOut.printf("%8x ", pic.getRGB(c, r));
//            }
//        }
//        StdOut.print("\n");
//    }

    // current picture
    public Picture picture() {
        Picture newPic = new Picture(pic);
        return newPic;
    }

    // width of current picture
    public int width() {
        return curW;
    }

    // height of current picture
    public int height() {
        return curH;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        // both 0 <= col < width and 0 <= row < height
        if ((x < 0) || (x >= w) || (y < 0) || (y >= h)) {
            throw new IllegalArgumentException("x y not in range!" + Integer.toString(x) + " " + Integer.toString(y));
        }
        ColAndRow xy = new ColAndRow(x, y);
        return energy[xy.index];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] spt = getSpt(horiGraph, energy, s);
        int[] seams = new int[w];

        ColAndRow xy;
        for (int i = d; spt[i] != s; i = spt[i]) {
            xy = new ColAndRow(spt[i]);
            seams[xy.col] = xy.row;
        }
        return seams;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] spt = getSpt(vertGraph, energy, s);
        int[] seams = new int[h];
        ColAndRow xy;
        for (int i = d; spt[i] != s; i = spt[i]) {
            xy = new ColAndRow(spt[i]);
            seams[xy.row] = xy.col;
        }
        return seams;
    }

    // make sure column index is between 0 and width - 1
    private void validateCol(int col) {
        if (col < 0 || col > w - 1) {
            throw new IllegalArgumentException("colmun index is out of range\n");
        }
    }

    // make sure row index is between 0 and height - 1
    private void validateRow(int row) {
        if (row < 0 || row > h - 1) {
            throw new IllegalArgumentException("row index is out of range\n");
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (h <= 1) {
            throw new IllegalArgumentException("Pic height is les than 2");
        }
        if (seam == null) {
            throw new IllegalArgumentException("Null seam");
        }

        if (seam.length != w) {
            throw new IllegalArgumentException("Seam length not equal to width.");
        }

        int preSeam = seam[0];
        for (int i = 1; i < seam.length; i++) {

            if (Math.abs(seam[i] - preSeam) > 1)
                throw new IllegalArgumentException("Difference of adjacent Seams larger than 1");

            preSeam = seam[i];
        }

        curH--;
        Picture tmpPicture = new Picture(w, h - 1);
        for (int col = 0; col < w; col++) {
            for (int row = 0; row < h - 1; row++) {
                validateRow(seam[col]);
                if (row < seam[col]) {
                    tmpPicture.setRGB(col, row, pic.getRGB(col, row));
                } else {
                    tmpPicture.setRGB(col, row, pic.getRGB(col, row + 1));
                }
            }
        }
        updateSeamCarver(tmpPicture);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

        if (w <= 1) {
            throw new IllegalArgumentException("Pic width is les than 2");
        }
        if (seam == null) {
            throw new IllegalArgumentException("Null seam");
        }

        if (seam.length != h) {
            throw new IllegalArgumentException("Seam length not equal to height.");
        }

        int preSeam = seam[0];
        for (int i = 1; i < seam.length; i++) {

            if (Math.abs(seam[i] - preSeam) > 1)
                throw new IllegalArgumentException("Difference of adjacent Seams larger than 1");

            preSeam = seam[i];
        }


//        StdOut.println("removeVerticalSeam:");
//        System.out.println(Arrays.toString(seam));

        curW--;
        Picture tmpPicture = new Picture(w - 1, h);
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w - 1; col++) {
                validateCol(seam[row]);
                if (col < seam[row]) {
                    tmpPicture.setRGB(col, row, pic.getRGB(col, row));
                } else {
                    tmpPicture.setRGB(col, row, pic.getRGB(col + 1, row));
                }
            }
        }
        updateSeamCarver(tmpPicture);
    }

    // unit testing (optional)
    public static void main(String[] args) {
        // java-algs4 SAP docs/wordnet/digraph1.txt
//        File in = new File(args[0]);
//        Picture pic = new Picture(in);
//        SeamCarver seam = new SeamCarver(pic);

        // Unite test getSpt()
        // java-algs4  SeamCarver graph1.txt energy1.txt
        // spt = 1->3->5->6->7

//        System.out.println(">>>>>>Unit test of getSpt:");
//
//        In in = new In("graph1.txt");
//        Digraph g = new Digraph(in);
//
//        In in2 = new In("energy1.txt");
//        int[] energy = new int[g.V()];
//        for (int i = 0; i < g.V(); i++) {
//            int id = in2.readInt();
//            energy[id] = in2.readInt();
//        }
//        int s = 1;
//        int d = 7;
//        StdOut.printf("\n Source %d, Destination:%d ", s, d);
//        int[] spt = SeamCarver.getSpt(g, energy, s);
//        for (int i = d; i != s; i = spt[i]) {
//            StdOut.printf("\n %d < -  %d", i, spt[i]);
//        }
//        StdOut.printf("\n ");

        System.out.println(">>>>>>Unit test of removeHorizontalSeam:");
        Picture pic = new Picture("testImage.png");
//        Picture pic = new Picture("3x4.png");
//        Picture pic = new Picture("6x5.png");
//        Picture pic = SCUtility.randomPicture(4, 4);
//        pic.show();
        SeamCarver seam = new SeamCarver(pic);
        SCUtility.showEnergy(seam);

        Picture picPlusSeam = new Picture(pic);

//        int[] vvSeam = seam.findVerticalSeam();
//        seam.removeVerticalSeam(vvSeam);
//        picPlusSeam = SCUtility.seamOverlay(picPlusSeam, true, vvSeam);


        for (int i = 1; i <= 100; i++) {
            int[] horiSeam = seam.findHorizontalSeam();
//            System.out.print("\nhoriSeam");
//            System.out.println(Arrays.toString(horiSeam));
            seam.removeHorizontalSeam(horiSeam);
//            picPlusSeam = SCUtility.seamOverlay(picPlusSeam, true, horiSeam);
//
            int[] vertSeam = seam.findVerticalSeam();
            seam.removeVerticalSeam(vertSeam);
//            System.out.print("\nvertSeam");
//            System.out.println(Arrays.toString(vertSeam));
//            picPlusSeam = SCUtility.seamOverlay(picPlusSeam, false, vertSeam);
            if (i % 20 == 0) {
                Picture finalPic = seam.picture();
                finalPic.show();
            }
        }
        picPlusSeam.show();
        Picture finalPic = seam.picture();
        finalPic.show();
    }
}
