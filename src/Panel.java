
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.*;

public class Panel extends JPanel {
    private int nodeNo = 1;
    private int node_diam = 30;
    private Vector<Node> nodesList = new Vector();
    private Vector<Edge> edgesList = new Vector();

    private boolean directedUndirectedGraph = false; // false == undirected
    private List<List<Integer>> adjacencyMatrix = new ArrayList<>();
    private int currentAdjacencyRow = 0;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;

    boolean isDraggingNode = false;
    Node whichNodeIsMoving;
    private void writeMatrix(List<List<Integer>> matrix){

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("AdjacencyMatrix.txt"));
            bw.write("There are " + nodesList.size() + " nodes.\n");
            for(int i = 0; i < this.currentAdjacencyRow; ++i) {
                for (int j = 0; j < this.currentAdjacencyRow; ++j)
                    bw.write(this.adjacencyMatrix.get(i).get(j) + " ");
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Panel() {
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        JButton modeButton = new JButton("Currently being in undirected graph mode");
        modeButton.setBounds(50,100,95,30);
        modeButton.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(modeButton);

        modeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(!directedUndirectedGraph) {
                    modeButton.setText("Currently being in oriented graph mode");
                    directedUndirectedGraph = true;
                }
                else {
                    modeButton.setText("Currently being in undirected graph mode");
                    directedUndirectedGraph = false;
                }
            }
        });

        JButton resetButton = new JButton("Reset graph.");
        resetButton.setBounds(50,40,40,30);
        resetButton.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(resetButton);

        resetButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Panel.this.nodeNo = 1;
                Panel.this.nodesList.clear();
                Panel.this.edgesList.clear();
                Panel.this.currentAdjacencyRow = 0;
                Panel.this.pointStart = null;
                Panel.this.pointEnd = null;
                Panel.this.isDragging = false;
                Panel.this.isDraggingNode = false;
                Panel.this.adjacencyMatrix.clear();
                Panel.this.repaint();
                Panel.this.writeMatrix(Panel.this.adjacencyMatrix);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Panel.this.pointStart = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                if (!Panel.this.isDragging) {
                        Panel.this.addNode(e.getX(), e.getY());
                    }
                 else {
                     if(isDraggingNode) {
                         isDraggingNode = false;
                     }
                     else {
                         boolean fromNode = false, toNode = false;
                         int node1No = 0, node2No = 0;
                         Point start = new Point(), end = new Point();

                         for (Node node : Panel.this.nodesList) {
                             if (Point2D.distance(Panel.this.pointStart.getX(), Panel.this.pointStart.getY(), node.getCoordX(), node.getCoordY()) < 30) {
                                 fromNode = true;
                                 start.setLocation(node.getCoordX() + 15, node.getCoordY() + 15);
                                 node1No = node.getNumber() - 1;
                             }
                         }

                         for (Node node : Panel.this.nodesList) {
                             if (node.getNumber() != node1No + 1 && Point2D.distance(Panel.this.pointEnd.getX(), Panel.this.pointEnd.getY(), node.getCoordX(), node.getCoordY()) < 30) {
                                 toNode = true;
                                 end.setLocation(node.getCoordX() + 15, node.getCoordY() + 15);
                                 node2No = node.getNumber() - 1;
                             }
                         }
                         if (fromNode && toNode && start.getX() != end.getX() && start.getY() != end.getY()) {
                             Edge edge = new Edge(start, end, directedUndirectedGraph);
                             Panel.this.edgesList.add(edge);
                             //verificare daca este orientat sau nu
                             //if(orientat...)
                             if (!directedUndirectedGraph) {
                                 Panel.this.adjacencyMatrix.get(node1No).set(node2No, 1);
                                 Panel.this.adjacencyMatrix.get(node2No).set(node1No, 1);
                             } else {
                                 Panel.this.adjacencyMatrix.get(node1No).set(node2No, 1);
                             }
                         }
                         Panel.this.writeMatrix(Panel.this.adjacencyMatrix);
                     }
                 }
                Panel.this.repaint();
                Panel.this.pointStart = null;
                Panel.this.isDragging = false;
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Panel.this.pointEnd = e.getPoint();

                for(var node : Panel.this.nodesList)
                    if(Point2D.distance(e.getPoint().getX(), e.getPoint().getY(), node.getCoordX(), node.getCoordY()) < 8)
                    {
                        Panel.this.isDraggingNode = true;
                        Panel.this.whichNodeIsMoving = node;
                        break;
                    }

                Panel.this.isDragging = true;
                Panel.this.repaint();
            }
        });
    }

    private void addNode(int x, int y) {

        boolean canAddNode = true;
        for(Node node : this.nodesList)
            if(Point2D.distance(x-10, y-10, node.getCoordX(), node.getCoordY()) < 60)
                canAddNode = false;
        if(canAddNode) {
            Node node = new Node(x - 10, y - 10, this.nodeNo);
            this.nodesList.add(node);
            ++this.nodeNo;
            this.repaint();

            //updatare matrice adiacenta
            List<Integer> row = new ArrayList<>();
            Panel.this.adjacencyMatrix.add(currentAdjacencyRow, row);
            for(int j = 0; j <= currentAdjacencyRow; ++j)
                row.add(j, 0);
            for(int i = 0; i < currentAdjacencyRow; ++i)
                Panel.this.adjacencyMatrix.get(i).add(currentAdjacencyRow, 0);

            ++this.currentAdjacencyRow;
            this.writeMatrix(Panel.this.adjacencyMatrix);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("This is my Graph!", 10, 20);

        Iterator var3 = this.edgesList.iterator();

        while(var3.hasNext()) {
            Edge a = (Edge)var3.next();
            a.drawEdge(g);
        }

        if (this.pointStart != null && !isDraggingNode) {
            if(directedUndirectedGraph)
                g.setColor(Color.BLUE);
            else
                g.setColor(Color.BLACK);
            g.drawLine(this.pointStart.x, this.pointStart.y, this.pointEnd.x, this.pointEnd.y);
        }

        if(isDraggingNode) {


            boolean movingIntoANode = false;

            for(Node node : this.nodesList)
                if(node.getNumber() != Panel.this.whichNodeIsMoving.getNumber() && Point2D.distance(this.pointEnd.x, this.pointEnd.y, node.getCoordX(), node.getCoordY()) < 60)
                    movingIntoANode = true;

            if(!movingIntoANode) {

                //update muchii
                for (var edge : Panel.this.edgesList) {
                    Point start = edge.getStart();
                    Point end = edge.getEnd();
                    if (Point2D.distance(start.getX() - 15, start.getY() - 15, whichNodeIsMoving.getCoordX(), whichNodeIsMoving.getCoordY()) <= 30) {
                        edge.setStartX(this.pointEnd.x + 15);
                        edge.setStartY(this.pointEnd.y + 15);
                    } else if (Point2D.distance(end.getX() - 15, end.getY() - 15, whichNodeIsMoving.getCoordX(), whichNodeIsMoving.getCoordY()) <= 30) {
                        edge.setEndX(this.pointEnd.x + 15);
                        edge.setEndY(this.pointEnd.y + 15);
                    }

                }

                Panel.this.whichNodeIsMoving.setCoordX(this.pointEnd.x);
                Panel.this.whichNodeIsMoving.setCoordY(this.pointEnd.y);
            }
        }
        for(int i = 0; i < this.nodesList.size(); ++i) {
            ((Node)this.nodesList.elementAt(i)).drawNode(g, this.node_diam);

        }

    }
}
