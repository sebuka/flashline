package com.example.myapplication;

import java.util.*;

class LevelValidator {
    private static final long INF = Long.MAX_VALUE / 2;
    private int n, s, t;
    private int[][] grid;
    private MCMF mcmf;

    public LevelValidator(int[][] grid) {
        n = grid.length;
        int sz = n * n;
        s = 0;
        t = 4 * sz + 1;
        mcmf = new MCMF(4 * sz + 2, s, t);
        this.grid = grid;

    }

    private boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < n && y < n;
    }

    public long validate() {
        Map<Integer, Boolean> colors = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int type = grid[i][j];
                if (type > 0) {
                    if (!colors.containsKey(type)) {
                        mcmf.addEdge(s, j * n + i + 1, 1, 0);
                    } else {
                        mcmf.addEdge(j * n + i + 1 + n * n, t, 1, 0);
                    }
                    colors.put(type, true);
                }
            }
        }

        int[][] h = new int[n][n];
        int[][] v = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(h[i], -1);
            Arrays.fill(v[i], -1);
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == -1) {
                    h[i][j] = i * n + j + 1;
                    mcmf.addEdge(h[i][j], h[i][j] + n * n, 1, 0);
                    v[i][j] = i * n + j + 1 + 2 * n * n;
                    mcmf.addEdge(v[i][j], v[i][j] + n * n, 1, 0);
                } else if (grid[i][j] != -2) {
                    h[i][j] = v[i][j] = i * n + j + 1;
                    mcmf.addEdge(h[i][j], h[i][j] + n * n, 1, 0);
                }
            }
        }

        int[] dx = {-1, 0, 0, 1};
        int[] dy = {0, -1, 1, 0};
        boolean[] check = {false, true, true, false};

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == -2) continue;
                for (int k = 0; k < 4; k++) {
                    int x = i + dx[k], y = j + dy[k];
                    if (isValid(x, y) && grid[x][y] != -2) {
                        if (check[k]) {
                            mcmf.addEdge(h[i][j] + n * n, h[x][y], 1, 1);
                        } else {
                            mcmf.addEdge(v[i][j] + n * n, v[x][y], 1, 1);
                        }
                    }
                }
            }
        }

        int need = colors.size();
        Pair<Long, Long> result = mcmf.maxFlow();
        if (result.getFirst() != need) {
            return -1;
        } else {
            return result.getSecond() / 2;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] grid = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = scanner.nextInt();
            }
        }
        new LevelValidator(grid).validate();
    }

    static class MCMF {
        class Edge {
            int v;
            long c, f, cost;

            Edge(int v, long c, long f, long cost) {
                this.v = v;
                this.c = c;
                this.f = f;
                this.cost = cost;
            }
        }

        private int n, s, t;
        private List<Edge> edge;
        private List<List<Integer>> g;
        private long[] d, p;
        private int[] par, ft;

        MCMF(int n, int s, int t) {
            this.n = n;
            this.s = s;
            this.t = t;
            edge = new ArrayList<>();
            g = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                g.add(new ArrayList<>());
            }
            d = new long[n];
            p = new long[n];
            par = new int[n];
        }

        void addEdge(int u, int v, long c, long cost) {
            g.get(u).add(edge.size());
            edge.add(new Edge(v, c, 0, cost));
            g.get(v).add(edge.size());
            edge.add(new Edge(u, 0, 0, -cost));
        }

        private boolean flex() {
            Arrays.fill(d, INF);
            Arrays.fill(par, -1);
            TreeSet<Pair<Long, Integer>> queue = new TreeSet<>(Comparator.comparingLong(Pair::getFirst));
            d[s] = 0;
            queue.add(new Pair<>(d[s], s));
            while (!queue.isEmpty()) {
                int u = queue.pollFirst().getSecond();
                for (int i : g.get(u)) {
                    Edge e = edge.get(i);
                    long w = e.cost + p[u] - p[e.v];
                    if (e.c - e.f > 0 && d[u] + w < d[e.v]) {
                        queue.remove(new Pair<>(d[e.v], e.v));
                        d[e.v] = d[u] + w;
                        par[e.v] = i;
                        queue.add(new Pair<>(d[e.v], e.v));
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                if (d[i] < INF) {
                    d[i] += p[i] - p[s];
                }
            }
            for (int i = 0; i < n; i++) {
                if (d[i] < INF) {
                    p[i] = d[i];
                }
            }
            return d[t] != INF;
        }

        private long sendFlow(int u, long cur, long[] cost) {
            if (par[u] == -1) return cur;
            int i = par[u];
            Edge e = edge.get(i);
            Edge bck = edge.get(i ^ 1);
            long f = sendFlow(bck.v, Math.min(cur, e.c - e.f), cost);
            e.f += f;
            bck.f -= f;
            cost[0] += f * e.cost;
            return f;
        }

        Pair<Long, Long> maxFlow() {
            Arrays.fill(d, INF);
            Arrays.fill(p, 0);
            d[s] = 0;
            boolean relax = true;
            for (int flex = 0; flex < n && relax; flex++) {
                relax = false;
                for (int u = 0; u < n; u++) {
                    for (int i : g.get(u)) {
                        Edge e = edge.get(i);
                        if (d[u] + e.cost < d[e.v]) {
                            d[e.v] = d[u] + e.cost;
                            relax = true;
                        }
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                if (d[i] < INF) {
                    p[i] = d[i];
                }
            }
            long flow = 0, cost = 0;
            while (flex()) flow += sendFlow(t, INF, new long[]{cost});
            ft = new int[edge.size()];
            for (int i = 0; i < edge.size(); i++) {
                ft[i] = (int) edge.get(i).f;
            }
            return new Pair<>(flow, cost);
        }
    }

    static class Pair<K, V> {
        private final K first;
        private final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public K getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }
    }
}
