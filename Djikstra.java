/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package djikstra;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import djikstra.BinomialHeapInterface.Node;

/**
 *
 * @author Anton
 */
public class Djikstra {

    private static class Map implements Comparable
    {
        int vertex;
        int parent;
        float cost;

        public void Copy(Map map)
        {
            vertex = map.vertex;
            parent = map.parent;
            cost = map.cost;
        }

        public int compareTo(Object o) {

            Map otherMap = (Map)o;

            if(cost < otherMap.cost )
            {
                return -1;
            }
            else if(cost > otherMap.cost)
            {
                return 1;
            }

            return 0;
        }

        @Override
        public String toString()
        {
            return "( " + vertex + " " + parent + " " + cost + " )";
        }
    }

    private ArrayList<LinkedList<Map>> graphList;
    private ArrayList<Map> shortestPath;

    public Djikstra()
    {
        graphList = new ArrayList<LinkedList<Map>>();
    }

    public void ReadGraphFromFile(String fileName) throws FileNotFoundException
    {
        Scanner reader = new Scanner(new FileInputStream(fileName));
        int number;

        while(reader.hasNextLine())
        {
            String nextLine = reader.nextLine();
            String [] neightbourVertexList = nextLine.split(" ");
            LinkedList<Map> edgeList  =  new LinkedList<Map>();

            number = Integer.parseInt(neightbourVertexList[0]);

            for(int i = 1; i < neightbourVertexList.length; i += 2)
            {
                Map newEdge =  new Map();
                newEdge.vertex = Integer.parseInt(neightbourVertexList[i]);
                newEdge.cost = Float.parseFloat(neightbourVertexList[i + 1]);
                newEdge.parent = number;

                edgeList.add(newEdge);
            }

            graphList.add(number,edgeList);
        }
    }

    public void findShortestPathFrom(int vertex)
    {
        if(vertex <= 0 || vertex >= graphList.size())
        {
            return;
        }

       shortestPath = new ArrayList<Map>();
       DjikstraBinomialHeap distanceHeap = new DjikstraBinomialHeap();

       ArrayList<Node> distanceNodes = fillBinomialHeap(vertex, distanceHeap);

       processALgorithm(distanceNodes, distanceHeap);
       PrintShortestPath();

    }

    private ArrayList<Node> fillBinomialHeap(int vertex,DjikstraBinomialHeap heap)
    {
        ArrayList<Node> heapNodes = new ArrayList<Node>();
        float infinity = Float.MAX_VALUE;

        for(int i = 0; i < graphList.size(); i++)
        {
            Node heapNode;
            Map distMap = new Map();
            Map nodeDistMap = new Map();

            if(i == vertex)
            {
                distMap.vertex =  i;
                distMap.parent = -1;
                distMap.cost = 0;

                shortestPath.add(distMap);

                nodeDistMap.Copy(distMap);
                heapNode = heap.push(nodeDistMap);
                
            }
            else
            {
                distMap.vertex =  i;
                distMap.parent = -1;
                distMap.cost = infinity;

                shortestPath.add(distMap);

                nodeDistMap.Copy(distMap);
                heapNode = heap.push(nodeDistMap);
                
            }

            heapNodes.add(heapNode);
        }

        return heapNodes;
    }

    private void processALgorithm(ArrayList<Node> heapNodes,DjikstraBinomialHeap distHeap)
    {
        ArrayList<Node> currentHeapNodes = heapNodes;
        Map minMap = (Map)distHeap.getMin();

        while(distHeap.getSize() != 0 && minMap != null && minMap.cost != Float.MAX_VALUE)
        {
            Map currentVertex = (Map)distHeap.extractMin();

            for(int i = 0; i < graphList.get(currentVertex.vertex).size(); i++)
            {
                Map edge = graphList.get(currentVertex.vertex).get(i);
                Map neighbourVertex = shortestPath.get(edge.vertex);

                if(currentVertex.cost + edge.cost < neighbourVertex.cost)
                {
                    float shorterCost = currentVertex.cost + edge.cost;
                    int newParent = currentVertex.vertex;

                    neighbourVertex.cost = shorterCost;
                    neighbourVertex.parent = newParent;

                    Node node = currentHeapNodes.get(neighbourVertex.vertex);
                    distHeap.decreaseKey(node, neighbourVertex,currentHeapNodes);
                }
            }

            minMap = (Map)distHeap.getMin();
        }
    }

    public void PrintShortestPath()
    {
        for(int i = 0 ; i < graphList.size() ; i++)
        {
            printHelper(i);
            System.out.println();
        }
        System.out.println();
    }

    private void printHelper(int vertex)
    {
        Map v = shortestPath.get(vertex);
        String path;

        if(v.parent == -1)
        {
            path = " ( " + v.vertex + " " + v.cost + " ) ";
            System.out.print(path);
            return;
        }
        else
        {
            printHelper(v.parent);
            path = " ( " + v.vertex + " " + v.cost + " ) ";
            System.out.print(path);
        }
    }

    @Override
    public String toString()
    {
        String output = "";
        for(int i = 0 ; i < graphList.size(); ++i)
        {
            for(int j = 0; j < graphList.get(i).size(); i++)
            {
                output = output.concat(graphList.get(i).get(j).toString());
                output =  output.concat("\n");
            }
        }

        return output;
    }
}
