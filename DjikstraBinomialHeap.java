/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package djikstra;
import java.util.ArrayList;

/**
 *
 * @author Anton
 */
public class DjikstraBinomialHeap extends BinomialHeap {


    public void decreaseKey(Node node,Comparable newKey,ArrayList<Node> helpNodes)
    {
       if(node.key.compareTo(newKey) == -1)
        {
            return;
        }

        node.key = newKey;
        Node parentNode = node.parent;
        int i,j;

        while(parentNode != null && node.key.compareTo(parentNode.key) == -1)
        {
            i = helpNodes.indexOf(node);
            j = helpNodes.indexOf(parentNode);

            Comparable key = node.key;
            node.key = parentNode.key;
            parentNode.key = key;

            helpNodes.set(i,parentNode);
            helpNodes.set(j, node);

            node = parentNode;
            parentNode = node.parent;
        }
    }
}
