package spbetu.prim.model.graph;

import java.util.HashMap;
import java.util.Set;

public class Vertex {  // класс ребра
    private int number;
    private HashMap<Vertex, Edge<Double>> edges;
    private boolean visited;

    Vertex(int number) {
        this.number = number;
        edges = new HashMap<>();
        visited = false;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean flag) {
        visited = flag;
    }

    public void vertexAddEdge(Vertex nextVertex, Edge edge) { // функция добавления ребра
        edges.put(nextVertex, edge);
    }

    public Edge<Double> getMinimum() {  // возвращает ребро
        Edge<Double> nextMinimumEdge = new Edge(Double.MAX_VALUE, null, null);  /// ???? generic

        Set<HashMap.Entry<Vertex, Edge<Double>>> set = edges.entrySet();

        for (HashMap.Entry<Vertex, Edge<Double>> me : set) {

            if (!me.getKey().isVisited() && (!me.getValue().isIncluded())) {
                if (me.getValue().getWeight() < nextMinimumEdge.getWeight()) {
                    nextMinimumEdge = me.getValue();  // возвращает ребро
                }
            }
        }

        return nextMinimumEdge;
    }

    public void forPreviousStep(Vertex to) {  // для отката назад, отметить, что ребро не посещали
        Set<HashMap.Entry<Vertex, Edge<Double>>> set = edges.entrySet(); // словарь вершины
        for (HashMap.Entry<Vertex, Edge<Double>> me : set) {
            if (me.getKey().equals(to)) {
                me.getValue().setIncluded(false);  // возвращает ребро
            }
        }
    }

    public void deleteEdgeFromDictionary(int indexVertexTo) {
        Set<HashMap.Entry<Vertex, Edge<Double>>> set = edges.entrySet(); // словарь ребер вершины
        for (HashMap.Entry<Vertex, Edge<Double>> edge : set) {
            if (edge.getKey().number == indexVertexTo) { // нашли в словаре ребро ко второй вершине
                edges.remove(edge.getKey());  // удаляем из словаря
                break;
            }
        }
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public HashMap<Vertex, Edge<Double>> getEdges() {
        return edges;
    }

    public void setEdges(HashMap<Vertex, Edge<Double>> edges) {
        this.edges = edges;
    }
}
