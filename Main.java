/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package djikstra;
import java.io.FileNotFoundException;

/**
 *
 * @author Anton
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        Djikstra d = new Djikstra();
        d.ReadGraphFromFile("./graph.txt");
        d.findShortestPathFrom(2);
        d.findShortestPathFrom(4);
    }

}
