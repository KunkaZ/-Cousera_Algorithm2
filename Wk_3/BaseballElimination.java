import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private final ST<String, Integer> teamId;
    private final int numOfTeam;
    private final int[] w; // wins
    private final int[] losses; // losses
    private final int[] r; // remaining
    private final int[][] g; // against


    public BaseballElimination(String filename) // create a baseball division from given filename in format specified below
    {
        In teamInfo = new In(filename);
        numOfTeam = Integer.parseInt(teamInfo.readLine());
        w = new int[numOfTeam];
        losses = new int[numOfTeam];
        r = new int[numOfTeam];
        g = new int[numOfTeam][numOfTeam];
        teamId = new ST<>();

//            String input1 = teamInfo.readLine();
        for (int i = 0; i < numOfTeam; i++) {
            if (!teamInfo.isEmpty()) {

                String teamName = teamInfo.readString();
                teamId.put(teamName, i);
                StdOut.println();
                StdOut.print(teamName);


                w[i] = teamInfo.readInt();
                StdOut.print(" " + w[i]);
                losses[i] = teamInfo.readInt();
                StdOut.print(" " + losses[i]);
                r[i] = teamInfo.readInt();
                StdOut.print(" " + r[i] + "   ");
                for (int j = 0; j < numOfTeam; j++) {
                    g[i][j] = teamInfo.readInt();
                    StdOut.print(" " + g[i][j]);
                }
            }
        }
    }

    private FlowNetwork buildFlowNetwork(String team) {
        validateTeamName(team);
        int id = teamId.get(team);

        int gameVerticeNum = (numOfTeam - 1) * numOfTeam / 2;
        FlowNetwork flowNet = new FlowNetwork(numOfTeam + gameVerticeNum + 2);

        // vertex 0 ~ numOfTeam-1 is team 0 ~ numOfTeam-1
        // vertex numOfTeam ~ gameVerticeNum-1 is matches
        // vertex s = V-2
        // vertex t = V-1

        // build flow edges connecting team vertex to t
        int t = flowNet.V() - 1;
//        StdOut.println("---build flow edges connecting team vertex to t");
//        StdOut.println("team:" + team + " target team id:" + id + "  w[id]:" + w[id] + " t:" + t);

        for (int teamVertex = 0; teamVertex < numOfTeam; teamVertex++) {
            if (teamVertex != id) {

                FlowEdge e = new FlowEdge(teamVertex, t, w[id] + r[id] - w[teamVertex]);
//                int wr = w[id] + r[id];
//                int weight = w[id] + r[id] - w[teamVertex];
//                StdOut.println("team:" + teamVertex + " ->" + t + "  w + r:" + wr + " w+r-wid:" + weight);
                flowNet.addEdge(e);

            }
        }

        int gameVertex = numOfTeam, s = flowNet.V() - 2;
        for (int teamI = 0; teamI < numOfTeam; teamI++) {
            for (int teamJ = teamI + 1; teamJ < numOfTeam; teamJ++) {
                if ((teamI != id) && (teamJ != id)) {
                    FlowEdge e = new FlowEdge(s, gameVertex, g[teamI][teamJ]);
                    flowNet.addEdge(e);
//                    StdOut.println(s + "->" + "game:" + gameVertex + " g " + g[teamI][teamJ]);
//                    StdOut.println("      " + teamI + " and " + teamJ);
                    e = new FlowEdge(gameVertex, teamI, Integer.MAX_VALUE); //
                    flowNet.addEdge(e);

                    e = new FlowEdge(gameVertex, teamJ, Integer.MAX_VALUE);
                    flowNet.addEdge(e);

                    gameVertex++;
                }
            }
        }

        return flowNet;
    }

    public int numberOfTeams() // number of teams
    {
        return numOfTeam;
    }


    private void validateTeamName(String team) {
        if (team == null)
            throw new IllegalArgumentException("Null inputs of team.");

        if (!teamId.contains(team))
            throw new IllegalArgumentException("Invalid team input.");
    }

    // all teams
    public Iterable<String> teams() {
        return teamId.keys();
    }

    // number of wins for given team
    public int wins(String team) {

        validateTeamName(team);
        int id = teamId.get(team);
        return w[id];
    }

    public int losses(String team) // number of losses for given team
    {
        validateTeamName(team);
        int id = teamId.get(team);
        return losses[id];
    }

    public int remaining(String team) // number of remaining games for given team
    {
        validateTeamName(team);
        int id = teamId.get(team);
        return r[id];
    }

    public int against(String team1, String team2) // number of remaining games between team1 and team2
    {
        validateTeamName(team1);
        validateTeamName(team2);
        int id1 = teamId.get(team1);
        int id2 = teamId.get(team2);
        return g[id1][id2];
    }

    public boolean isEliminated(String team) // is given team eliminated?
    {
        validateTeamName(team);

        return certificateOfElimination(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {

        Queue<String> setR = new Queue<String>();
        // check trivial elimination
        int id = teamId.get(team);
        int possibleWins = w[id] + r[id];
        for (String t : teamId) {
            int v = teamId.get(t);
            if (possibleWins < w[v]) {
                setR.enqueue(t);
            }
        }

        if (setR.isEmpty()) {
            // check nontrivial elimination
            FlowNetwork flowNet = buildFlowNetwork(team);

            FordFulkerson maxflow = new FordFulkerson(flowNet, flowNet.V() - 2, flowNet.V() - 1);

            for (String t : teamId.keys()) {
                int v = teamId.get(t);
                if (maxflow.inCut(v)) {
                    setR.enqueue(t);
                }
            }
        }

        if (setR.isEmpty()) return null;
        else return setR;

    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("teams5.txt");
        for (String team : division.teams()) {
//            String team = "Detroit";
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
