package com.example.myapplication;

import java.util.*;

public class LevelValidator {
    private static final long INF = Long.MAX_VALUE / 2;
    private int[][] grid;
    private int gridSize;

    public LevelValidator(int[][] grid) {
        this.grid = grid;
        this.gridSize = grid.length;
    }

    public int validate() {
        int numCells = gridSize * gridSize; // Общее количество ячеек в сетке
        int source = 0; // Источник в графе потока
        int sink = 4 * numCells + 1; // Сток в графе потока
        MinCostMaxFlow mcmf = new MinCostMaxFlow(4 * numCells + 2, source, sink);
        Map<Integer, Boolean> usedColors = new HashMap<>();
        processGridInput(mcmf, usedColors); // Обработка входных данных
        initializeFlowGraph(mcmf, usedColors); // Инициализация графа потока
        createFlowGraphEdges(mcmf); // Создание ребер графа потока

        int requiredFlow = usedColors.size(); // Необходимый поток
        Pair<Long, Long> result = mcmf.computeMaxFlow(); // Вычисление максимального потока и минимальной стоимости
        long flow = result.getFirst(), cost = result.getSecond(); // Извлечение результата

        if (flow != requiredFlow) {
            return -1; // Если поток не равен требуемому, возвращаем -1
        }
        return (int) cost; // Возвращаем стоимость
    }

    // Метод для обработки входных данных
    private void processGridInput(MinCostMaxFlow mcmf, Map<Integer, Boolean> usedColors) {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int type = grid[y][x];
                if (type > 0) {
                    if (!usedColors.containsKey(type)) {
                        mcmf.addEdge(0, x * gridSize + y + 1, 1, 0); // Добавление ребра от источника
                    } else {
                        mcmf.addEdge(x * gridSize + y + 1 + gridSize * gridSize, 4 * gridSize * gridSize + 1, 1, 0); // Добавление ребра к стоку
                    }
                    usedColors.put(type, true); // Пометка использованного цвета
                }
            }
        }
    }

    // Метод для инициализации графа потока
    private void initializeFlowGraph(MinCostMaxFlow mcmf, Map<Integer, Boolean> usedColors) {
        int[][] horizontalEdges = new int[gridSize][gridSize], verticalEdges = new int[gridSize][gridSize];
        for (int[] row : horizontalEdges) Arrays.fill(row, -1);
        for (int[] row : verticalEdges) Arrays.fill(row, -1);

        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                if (grid[i][j] == -1) {
                    horizontalEdges[i][j] = i * gridSize + j + 1; // Горизонтальные ребра
                    mcmf.addEdge(horizontalEdges[i][j], horizontalEdges[i][j] + gridSize * gridSize, 1, 0);
                    verticalEdges[i][j] = i * gridSize + j + 1 + 2 * gridSize * gridSize; // Вертикальные ребра
                    mcmf.addEdge(verticalEdges[i][j], verticalEdges[i][j] + gridSize * gridSize, 1, 0);
                } else if (grid[i][j] != -2) {
                    horizontalEdges[i][j] = verticalEdges[i][j] = i * gridSize + j + 1;
                    mcmf.addEdge(horizontalEdges[i][j], horizontalEdges[i][j] + gridSize * gridSize, 1, 0);
                }
            }
        }
    }

    // Метод для создания ребер графа потока
    private void createFlowGraphEdges(MinCostMaxFlow mcmf) {
        int[] dx = {-1, 0, 0, 1};
        int[] dy = {0, -1, 1, 0};
        boolean[] isHorizontal = {false, true, true, false};

        for (int i = 0; i < gridSize; ++i) {
            for (int j = 0; j < gridSize; ++j) {
                if (grid[i][j] == -2) continue;
                for (int direction = 0; direction < 4; ++direction) {
                    int x = i + dx[direction], y = j + dy[direction];
                    if (isValidCoordinate(x, y) && grid[x][y] != -2) {
                        if (isHorizontal[direction]) {
                            mcmf.addEdge(i * gridSize + j + 1 + gridSize * gridSize, x * gridSize + y + 1, 1, 1);
                        } else {
                            mcmf.addEdge(i * gridSize + j + 1 + 2 * gridSize * gridSize, x * gridSize + y + 1, 1, 1);
                        }
                    }
                }
            }
        }
    }

    // Метод для проверки корректности координат
    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && y >= 0 && x < gridSize && y < gridSize;
    }

    // Вложенный класс Edge для представления ребра графа
    static class Edge {
        int destination;
        long capacity, flow, cost;

        Edge(int destination, long capacity, long flow, long cost) {
            this.destination = destination;
            this.capacity = capacity;
            this.flow = flow;
            this.cost = cost;
        }
    }

    // Вложенный класс MinCostMaxFlow для реализации алгоритма минимальной стоимости максимального потока
    static class MinCostMaxFlow {
        List<Edge> edges;
        List<List<Integer>> graph;
        long[] distance, potential;
        int[] parent;
        int numNodes, source, sink;

        MinCostMaxFlow(int numNodes, int source, int sink) {
            this.numNodes = numNodes;
            this.source = source;
            this.sink = sink;
            edges = new ArrayList<>();
            graph = new ArrayList<>();
            for (int i = 0; i < numNodes; i++) graph.add(new ArrayList<>());
            distance = new long[numNodes];
            potential = new long[numNodes];
            parent = new int[numNodes];
        }

        void addEdge(int from, int to, long capacity, long cost) {
            graph.get(from).add(edges.size());
            edges.add(new Edge(to, capacity, 0, cost));
            graph.get(to).add(edges.size());
            edges.add(new Edge(from, 0, 0, -cost));
        }

        boolean relaxEdges() {
            Arrays.fill(distance, INF);
            Arrays.fill(parent, -1);
            PriorityQueue<Pair<Long, Integer>> queue = new PriorityQueue<>();
            distance[source] = 0;
            queue.add(new Pair<>(distance[source], source));
            while (!queue.isEmpty()) {
                int node = queue.poll().getSecond();
                for (int edgeIndex : graph.get(node)) {
                    Edge edge = edges.get(edgeIndex);
                    long weight = edge.cost + potential[node] - potential[edge.destination];
                    if (edge.capacity - edge.flow > 0 && distance[node] + weight < distance[edge.destination]) {
                        queue.remove(new Pair<>(distance[edge.destination], edge.destination));
                        distance[edge.destination] = distance[node] + weight;
                        parent[edge.destination] = edgeIndex;
                        queue.add(new Pair<>(distance[edge.destination], edge.destination));
                    }
                }
            }
            for (int i = 0; i < numNodes; ++i) {
                if (distance[i] < INF) {
                    distance[i] += potential[i] - potential[source];
                }
            }
            for (int i = 0; i < numNodes; ++i) {
                if (distance[i] < INF) {
                    potential[i] = distance[i];
                }
            }
            return distance[sink] != INF;
        }

        long sendFlow(int node, long flowAmount, long[] totalCost) {
            if (parent[node] == -1) return flowAmount;
            int edgeIndex = parent[node];
            Edge edge = edges.get(edgeIndex);
            Edge reverseEdge = edges.get(edgeIndex ^ 1);
            long currentFlow = Math.min(flowAmount, edge.capacity - edge.flow);
            long bottleneckFlow = sendFlow(edge.destination, currentFlow, totalCost);
            edge.flow += bottleneckFlow;
            reverseEdge.flow -= bottleneckFlow;
            totalCost[0] += bottleneckFlow * edge.cost;
            return bottleneckFlow;
        }

        Pair<Long, Long> computeMaxFlow() {
            long maxFlow = 0, minCost = 0;
            while (relaxEdges()) {
                long[] cost = {0};
                long flow = sendFlow(sink, INF, cost);
                if (flow == 0) break;
                maxFlow += flow;
                minCost += cost[0];
            }
            return new Pair<>(maxFlow, minCost);
        }
    }

    // Вложенный класс Pair для представления пары значений
    static class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {
        T1 first;
        T2 second;

        Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        public T1 getFirst() {
            return first;
        }

        public T2 getSecond() {
            return second;
        }

        @Override
        public int compareTo(Pair<T1, T2> other) {
            int cmp = first.compareTo(other.first);
            if (cmp == 0) {
                cmp = second.compareTo(other.second);
            }
            return cmp;
        }
    }
}
