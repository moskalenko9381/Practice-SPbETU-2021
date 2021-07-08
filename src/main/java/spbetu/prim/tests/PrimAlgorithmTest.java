package spbetu.prim.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spbetu.prim.model.algorithm.PrimAlgorithm;
import spbetu.prim.model.graph.Edge;
import spbetu.prim.model.graph.Graph;
import spbetu.prim.model.loader.FileLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.Stack;
import java.util.ArrayList;

public class PrimAlgorithmTest {
    PrimAlgorithm actualPrimAlgorithm;
    PrimAlgorithm expectPrimAlgorithm;
    Stack<Edge> expectSpanningTree;

    FileLoader runAlgorithmResultFile;
    FileLoader runAlgorithmTestFile;
    FileLoader addEdgeToSpanningTreeResult;
    FileLoader addEdgeToSpanningTreeTest;

    public PrimAlgorithmTest() throws FileNotFoundException {
        runAlgorithmResultFile = new FileLoader("./Test_files/PrimAlgorithmTests/RunAlgorithmResult.txt");
        runAlgorithmTestFile = new FileLoader("./Test_files/PrimAlgorithmTests/RunAlgorithmTest.txt");
        addEdgeToSpanningTreeResult = new FileLoader("./Test_files/PrimAlgorithmTests/AddEdgeToSpanningTreeResult.txt");
        addEdgeToSpanningTreeTest = new FileLoader("./Test_files/PrimAlgorithmTests/AddEdgeToSpanningTreeTest.txt");
    }

    @Before
    public void setUp() {
        Graph testGraph = new Graph();
        runAlgorithmTestFile.loadGraph(testGraph);
        actualPrimAlgorithm = new PrimAlgorithm(testGraph);

        Graph resultGraph = new Graph();
        runAlgorithmResultFile.loadGraph(resultGraph);
        expectPrimAlgorithm = new PrimAlgorithm(resultGraph);
        expectPrimAlgorithm.runAlgorithm();
        expectSpanningTree = expectPrimAlgorithm.getSpanningTree();
    }

    @After
    public void endTest(){
        actualPrimAlgorithm.clearGraph();
        expectSpanningTree.clear();
        expectPrimAlgorithm.clearGraph();
    }

    public void isEqualEdges(Edge expectEdge, Edge actualEdge){
        Assert.assertEquals(expectEdge.getWeight(), actualEdge.getWeight());
        int actualFrom = actualEdge.getVertexFrom().getNumber();
        int actualTo = actualEdge.getVertexTo().getNumber();
        int expectFrom = expectEdge.getVertexFrom().getNumber();
        int expectTo = expectEdge.getVertexTo().getNumber();
        boolean equal = false;
        if((actualFrom == expectFrom && actualTo == expectTo) ||
                (actualFrom == expectTo && actualTo == expectFrom))
            equal = true;
        Assert.assertTrue(equal);
    }

    @Test
    public void runAlgorithm(){
        actualPrimAlgorithm.runAlgorithm();
        expectPrimAlgorithm.runAlgorithm();
        Stack<Edge> actualSpanningTree = actualPrimAlgorithm.getSpanningTree();
        Assert.assertEquals(expectSpanningTree.size(), actualSpanningTree.size());
        for(int i = 0; i < actualSpanningTree.size(); i++){
            isEqualEdges(expectSpanningTree.get(i), actualSpanningTree.get(i));
        }
    }

    @Test
    public void runAlgorithmByStep() {
        for(Edge edge : expectSpanningTree){
            isEqualEdges(edge, actualPrimAlgorithm.runAlgorithmByStep());
        }
    }

    @Test
    public void addEdgeToSpanningTree() {
        Stack<Edge> actualSpanningTree = new Stack<Edge>();

        for(int i = 0; i < expectSpanningTree.size(); i++){
            actualSpanningTree.add(expectSpanningTree.get(i));
            actualPrimAlgorithm.addEdgeToSpanningTree(expectSpanningTree.get(i).getVertexFrom(),
                                                        expectSpanningTree.get(i).getVertexTo(),
                                                        expectSpanningTree.get(i).getWeight());
            isEqualEdges(actualSpanningTree.get(i), actualPrimAlgorithm.getSpanningTree().get(i));
        }
    }

    @Test
    public void previousStep() {
        Edge actualEdge = actualPrimAlgorithm.runAlgorithmByStep();
        Edge expectEdge = actualPrimAlgorithm.previousStep();
        isEqualEdges(expectEdge, actualEdge);
    }

    @Test
    public void clearGraph() {
        actualPrimAlgorithm.clearGraph();
        Assert.assertEquals(0, actualPrimAlgorithm.getGraph().size());
        Assert.assertEquals(0, actualPrimAlgorithm.getSpanningTree().size());
    }

    @Test
    public void graphStartAgain() {
        int size = actualPrimAlgorithm.getGraph().size();
        actualPrimAlgorithm.restart();
        Assert.assertEquals(size, actualPrimAlgorithm.getGraph().size());
        Assert.assertEquals(0, actualPrimAlgorithm.getSpanningTree().size());
    }
}