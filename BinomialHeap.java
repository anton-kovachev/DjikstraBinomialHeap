/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package djikstra;
import java.util.LinkedList;
import java.util.HashMap;

/**
 *
 * @author Anton
 */
public class BinomialHeap implements BinomialHeapInterface {

    private int heapSize;
    private Node heapHead;

    public BinomialHeap()
    {
        heapSize = 0;
        heapHead = null;
    }

    public BinomialHeap(Comparable [] elements)
    {
        BinomialHeap newHeap = new BinomialHeap();

        for(Comparable element : elements)
        {
            newHeap.push(element);
        }
    }

    public Node getRoot()
    {
        return heapHead;
    }

    public void setRoot(Node root)
    {
        this.heapHead = root;
    }

    public int getSize()
    {
        return heapSize;
    }

    private Node binomialLink(Node largerNode, Node smallerNode)
    {
        largerNode.parent = smallerNode;
        largerNode.right = smallerNode.leftmostChild;
        smallerNode.leftmostChild = largerNode;

        return smallerNode;
    }

    public Node consolidate(Node leftRoot, int leftSize, Node rightRoot, int rightSize)
    {
        Node a = (leftRoot);
        Node b = (rightRoot);

        HashMap<Node,Integer> degreeDict = new HashMap<Node,Integer>();
        precomputeDegrees(degreeDict, a, leftSize);
        precomputeDegrees(degreeDict, b, rightSize);

        Node firstNode = getMinDegree(degreeDict, a, b);

        if (firstNode == b)
        {
            b = a;
        }

        a = firstNode;

        while(b != null)
        {
            if(a.right == null)
            {
                moveBRightToA(a, b, degreeDict);
                return firstNode;
            }
            else if(degreeDict.get(a.right) < degreeDict.get(b))
            {
                a = a.right;
            }
            else
            {
                b = switchAandB(a, b, degreeDict);
            }
        }

        return firstNode;
    }

    private void moveBRightToA(Node a,Node b,HashMap<Node,Integer> degreeDict)
    {
        int aDegree = degreeDict.get(a);
        a.right = b;
        degreeDict.put(a, aDegree);

    }

    private Node switchAandB(Node a,Node b,HashMap<Node,Integer> degreeDict)
    {
        Node c = b.right;
        int bDegree = degreeDict.get(b);
        b.right = a.right;
        degreeDict.put(b, bDegree);
        int aDegree = degreeDict.get(a);
        a.right = b;
        degreeDict.put(a, aDegree);
        a = a.right;
        b = c;

        return b;
    }

    private void precomputeDegrees(HashMap<Node,Integer> degreeDict,Node root,int size)
    {
        Node currentTree = root;
        LinkedList<Integer> bits = getSizeBitRepresentation(size);
        int lastDegree = -1;

        while(currentTree != null)
        {
            lastDegree = getTreeDegree(lastDegree,bits);
            degreeDict.put(currentTree, lastDegree);
            currentTree = currentTree.right;
        }
    }

     private int getTreeDegree(int lastTreeDegree, LinkedList<Integer> bits)
    {
        for(int i = lastTreeDegree + 1 ; i < bits.size(); ++i)
        {
            int currentIndex = i;

            if(bits.get(currentIndex) == 1)
            {
                return currentIndex;
            }
        }

        return -1;
    }

    private LinkedList<Integer> getSizeBitRepresentation(int size)
    {
        LinkedList<Integer> bits = new LinkedList<Integer>();

        int currentValue = size;

        while(currentValue != 0)
        {
            int bit = currentValue % 2;
            bits.addLast(bit);
            //System.out.println("NexBit " + bits.get(0));
            currentValue = currentValue / 2;
        }

        return bits;
    }

    private Node getMinDegree(HashMap<Node,Integer> degreeDict,Node lNode,Node rNode)
    {
        if(degreeDict.get(lNode) <= degreeDict.get(rNode))
        {
            return lNode;
        }

        return rNode;
    }

    public void union(BinomialHeapInterface otherHeap)
    {
        if(otherHeap == null || otherHeap.getRoot() == null)
        {
            return;
        }

        if(heapHead == null && heapSize == 0)
        {
            this.heapHead = otherHeap.getRoot();
            this.heapSize = otherHeap.getSize();
            return;
        }
        
        HashMap<Node,Integer> degreeDict = new HashMap<Node,Integer>();
        precomputeDegrees(degreeDict,this.heapHead,this.heapSize);
        precomputeDegrees(degreeDict,otherHeap.getRoot(),otherHeap.getSize());

        BinomialHeap newHeap = new BinomialHeap();
        newHeap.heapHead = consolidate(this.heapHead,this.heapSize,otherHeap.getRoot(),otherHeap.getSize());
        newHeap.heapSize = this.heapSize + otherHeap.getSize();


        if(newHeap.heapHead == null)
        {
            return;
        }

        Node previousNode = null;
        Node currentNode = newHeap.heapHead;
        Node nextNode = newHeap.heapHead.right;

        while(nextNode != null)
        {
            int firstDegree = degreeDict.get(currentNode);
            int secondDegree = degreeDict.get(nextNode);
            int nextDegree;

            if(nextNode.right != null)
            {
                nextDegree = degreeDict.get(nextNode.right);
            }
            else
            {
                nextDegree = -1;
            }

            if((firstDegree != secondDegree) ||
               ( nextDegree == firstDegree))
            {
                previousNode = currentNode;
                currentNode = nextNode;
            }
            else if(currentNode.key.compareTo(nextNode.key) == - 1 ||
                    currentNode.key.compareTo(nextNode.key) == 0)
            {
                currentNode.right = nextNode.right;
                binomialLink(nextNode,currentNode);
                degreeDict.put(currentNode,firstDegree + 1);
            }
            else
            {
                if(previousNode == null)
                {
                    newHeap.heapHead = nextNode;
                }
                else
                {
                    previousNode.right = nextNode;
                }
                    binomialLink(currentNode, nextNode);
                    degreeDict.put(nextNode,secondDegree + 1);
                    currentNode = nextNode;
            }
            nextNode = currentNode.right;
        }

        this.heapHead = newHeap.heapHead;
        this.heapSize = newHeap.heapSize;
    }

    public Comparable getMin()
    {
        Node minNode = getMinNode();
        if(minNode == null)
        {
            return null;
        }
        return minNode.key;
    }

    private Node getMinNode()
    {
        if(heapHead == null)
        {
            return null;
        }

        Node minNode = null;
        Node currentNode = this.heapHead;

        Comparable min = currentNode.key;
        minNode = currentNode;

        while(currentNode != null)
        {
            if(currentNode.key.compareTo(min) == - 1)
            {
                min = currentNode.key;
                minNode = currentNode;
            }

            currentNode = currentNode.right;
        }

        return minNode;
    }

    public Node push(Comparable newKey)
    {

        Node newNode = new Node();
        newNode.key = newKey;

        if(heapSize == 0)
        {
            heapHead = newNode;
            heapSize = 1;
        }
        else
        {
            BinomialHeap newHeap = new BinomialHeap();
            newHeap.heapHead = newNode;
            newHeap.heapSize = 1;

            this.union(newHeap);
        }

        return newNode;
    }

    public void decreaseKey(Node node, Comparable newKey)
    {
        if(node.key.compareTo(newKey) == -1)
        {
            return;
        }

        node.key = newKey;
        Node parentNode = node.parent;

        while(parentNode != null && node.key.compareTo(parentNode.key) == -1)
        {
            Comparable key = node.key;
            node.key = parentNode.key;
            parentNode.key = key;

            node = parentNode;
            parentNode = node.parent;
        }
    }

    public Comparable extractMin()
    {
        HashMap<Node,Integer> degreeDict = new HashMap<Node,Integer>();
        precomputeDegrees(degreeDict, heapHead,heapSize);

        Node minRoot = getMinNode();
        int minRootSize = (int)Math.pow(2,degreeDict.get(minRoot));

        Node currentNode = heapHead;
        Node minRootPrevious = getPreviousNode(currentNode, minRoot);

        if(minRootPrevious != null)
        {
            minRootPrevious.right = minRoot.right;
        }
        else
        {
            this.heapHead = minRoot.right;
        }

        minRoot.right = null;
        heapSize = heapSize - minRootSize;

        Node leftMostChild = minRoot.leftmostChild;
        minRoot.leftmostChild = null;

        Node newRoot = reverse(leftMostChild);

        if(newRoot != null)
        {
            BinomialHeap newHeap = new BinomialHeap();

            newHeap.heapHead = newRoot;
            newHeap.heapSize = minRootSize - 1;

            this.union(newHeap);
        }

        return minRoot.key;

    }

    private Node reverse(Node root)
    {
        LinkedList<Node> nodeList = new LinkedList<Node>();

        Node current = root;

        while(current != null)
        {
            current.parent = null;
            nodeList.addFirst(current);
            current = current.right;

            if(nodeList.size() > 1)
            {
                Node temp = nodeList.get(0);
                temp.right = nodeList.get(1);
            }

            if(nodeList.size() == 1)
            {
                nodeList.get(0).right = null;
            }
        }

        if(nodeList.size() > 0)
        {
            return nodeList.getFirst();
        }

        return null;
    }

     private Node getPreviousNode(Node currentNode, Node minRoot)
     {
         if(currentNode == minRoot)
         {
             return null;
         }

        Node minRootPrevious;
        while (currentNode != null) {
            if (currentNode.right == minRoot) {
                minRootPrevious = currentNode;
                return minRootPrevious;
            }
            currentNode = currentNode.right;
        }

        return null;
    }

     public void Print()
     {
         printHelper(heapHead,"");
     }

     public String PrintTrees()
     {
         Node currentNode = heapHead;
         String str = "";

         while(currentNode != null)
         {
             str = str.concat(" " + currentNode.toString());
             currentNode = currentNode.right;
         }
         str = str.concat("\n Size" + this.heapSize);
         return str;
     }

     private String printHelper(Node root,String str)
     {
         Node currentNode = root;

         while(currentNode != null)
         {
             Node n = new Node();
             if(currentNode.parent == null)
             {
                 n.key = -1;
             }
             else
             {
                 n.key = currentNode.parent.key;
             }
             int count = 0;
             System.out.print(currentNode.key.toString() + "Parent " + n.key + " ( ");
             str =  str.concat(currentNode.key.toString() + " ");
             //System.out.print("Next Level!");
             Node currentChildNode = currentNode.leftmostChild;

             while(currentChildNode != null)
             {
                 count++;
                 str = str.concat(printHelper(currentChildNode,str + " ") + " ");
                 currentChildNode = currentChildNode.right;
             }

             System.out.print(" ) " + count);
             if(currentNode.right != null)
             {
                System.out.println(currentNode.key == currentNode.right.key);
             }
             System.out.println();
             currentNode = currentNode.right;
         }
         return str;
     }

    @Override
     public String toString()
     {
        return PrintTrees();
     }
}
