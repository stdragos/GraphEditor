
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
    private int currentAdjacencySize = 0;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;

    boolean isDraggingNode = false;
    Node whichNodeIsMoving;

    private double calculateDistance(Point a, Point b) {
        return Point2D.distance(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private void writeMatrix(List<List<Integer>> matrix){

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("AdjacencyMatrix.txt"));
            bw.write("There are " + nodesList.size() + " nodes.\n");
            for(var row : matrix){
                for(var element : row)
                    bw.write(element + " ");
                bw.write('\n');
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
        Panel.this.setFocusable(true);
        Panel.this.requestFocusInWindow();
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
                Panel.this.setFocusable(true);
                Panel.this.requestFocus();
            }
        });

        JButton resetButton = new JButton("Reset graph");
        resetButton.setBounds(50,40,40,30);
        resetButton.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(resetButton);

        resetButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Panel.this.nodeNo = 1;
                Panel.this.nodesList.clear();
                Panel.this.edgesList.clear();
                Panel.this.currentAdjacencySize = 0;
                Panel.this.pointStart = null;
                Panel.this.pointEnd = null;
                Panel.this.isDragging = false;
                Panel.this.isDraggingNode = false;
                Panel.this.adjacencyMatrix.clear();
                Panel.this.repaint();
                Panel.this.writeMatrix(Panel.this.adjacencyMatrix);
                Panel.this.setFocusable(true);
                Panel.this.requestFocusInWindow();
            }
        });



        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Panel.this.setFocusable(true);
                Panel.this.requestFocusInWindow();
                Panel.this.pointStart = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                Panel.this.setFocusable(true);
                Panel.this.requestFocusInWindow();
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
                             if (Point2D.distance(Panel.this.pointStart.getX(), Panel.this.pointStart.getY(), node.getCoordX(), node.getCoordY()) < 40) {
                                 fromNode = true;
                                 start.setLocation(node.getCoordX() + 15, node.getCoordY() + 15);
                                 node1No = node.getNumber() - 1;
                             }
                         }

                         for (Node node : Panel.this.nodesList) {
                             if (node.getNumber() != node1No + 1 && Point2D.distance(Panel.this.pointEnd.getX(), Panel.this.pointEnd.getY(), node.getCoordX(), node.getCoordY()) < 40) {
                                 toNode = true;
                                 end.setLocation(node.getCoordX() + 15, node.getCoordY() + 15);
                                 node2No = node.getNumber() - 1;
                             }
                         }
                         if (fromNode && toNode && start.getX() != end.getX() && start.getY() != end.getY()) {
                            //verificare daca exista muchie deja
                             Edge whichEdgeToRemove = null;

                             for(var edg : edgesList) {
                                 if(start.x == edg.getStart().x && start.y == edg.getStart().y &&
                                 end.x == edg.getEnd().x && end.y == edg.getEnd().y) {
                                     whichEdgeToRemove = edg;
                                     break;
                                 }
                                 if(!edg.isDirected() && start.x == edg.getEnd().x && start.y == edg.getEnd().y &&
                                         end.x == edg.getStart().x && end.y == edg.getStart().y) {
                                     whichEdgeToRemove = edg;
                                     break;
                                 }
                             }

                             if(whichEdgeToRemove == null)
                             {
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
                             else {
                                 edgesList.remove(whichEdgeToRemove);
                                 if (!directedUndirectedGraph) {
                                     Panel.this.adjacencyMatrix.get(node1No).set(node2No, 0);
                                     Panel.this.adjacencyMatrix.get(node2No).set(node1No, 0);
                                 } else {
                                     Panel.this.adjacencyMatrix.get(node1No).set(node2No, 0);
                                 }
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
                Panel.this.setFocusable(true);
                Panel.this.requestFocusInWindow();

                if(!Panel.this.isDraggingNode) {
                    //check first if not drawing an edge from a node
                    boolean drawingGoodEdge = false;
                    for (Node node : Panel.this.nodesList) {
                        if (calculateDistance(Panel.this.pointStart, new Point(node.getCoordX() + 15, node.getCoordY()+15)) < 20) {
                            drawingGoodEdge = true;
                        }
                    }

                    if(!drawingGoodEdge)
                    for (var node : Panel.this.nodesList)
                        if (calculateDistance(e.getPoint(), node.getPoint()) < 10) {
                            Panel.this.isDraggingNode = true;
                            Panel.this.whichNodeIsMoving = node;
                            break;
                        }
                }

                Panel.this.isDragging = true;
                Panel.this.repaint();
            }
        });

        //delete a node
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == 'e' || e.getKeyChar() == 'E')
                {
                    Point mouse = MouseInfo.getPointerInfo().getLocation();
                    Node nodeToDelete = null;
                    //verify if mouse pointer is on a node
                    for(var node : Panel.this.nodesList) {

                        if(calculateDistance(new Point(node.getPoint().x + 25, node.getPoint().y + 25), mouse) <= 30) {
                            nodeToDelete = node;
                            break;
                        }
                    }
                    if(nodeToDelete != null) {
                        List<List<Integer>> newMatrix = new ArrayList<>();
                        List<Integer> row = new ArrayList<>();
                        for(int i = 0; i < Panel.this.currentAdjacencySize - 1; ++i) {
                            row = new ArrayList<>();
                            for (int j = 0; j < Panel.this.currentAdjacencySize - 1; ++j)
                                row.add(j, 0);
                            newMatrix.add(i, row);
                        }

                        //adjacency matrix update
                        int currentI = 0, currentJ = 0;
                        for(int i = 0; i < Panel.this.currentAdjacencySize; ++i) {
                            if(i != nodeToDelete.getNumber() - 1) {
                                for (int j = 0; j < Panel.this.currentAdjacencySize; ++j)
                                    if (j != nodeToDelete.getNumber() - 1) {
                                        newMatrix.get(currentI).set(currentJ, Panel.this.adjacencyMatrix.get(i).get(j));
                                        ++currentJ;
                                    }
                                ++currentI;
                                currentJ = 0;
                            }
                        }

                        Panel.this.adjacencyMatrix.clear();
                        Panel.this.adjacencyMatrix = newMatrix;


                        --Panel.this.nodeNo;
                        --Panel.this.currentAdjacencySize;


                        //remove edges from this node
                        for(int i = 0; i < Panel.this.edgesList.size(); ++i) {
                            Edge edge = Panel.this.edgesList.get(i);
                            if(calculateDistance(new Point(edge.getStart().x - 15, edge.getStart().y - 15), nodeToDelete.getPoint()) < 30) {
                                Panel.this.edgesList.remove(edge);
                                --i;
                            }
                            else if (calculateDistance(new Point(edge.getEnd().x - 15, edge.getEnd().y - 15), nodeToDelete.getPoint()) < 30) {
                                Panel.this.edgesList.remove(edge);
                                --i;
                            }
                        }
                        Panel.this.nodesList.remove(nodeToDelete);
                        for(int i = 0;i < Panel.this.nodesList.size();++i) {
                            if(Panel.this.nodesList.get(i).getNumber() == i + 2)
                                Panel.this.nodesList.get(i).setNumber(i + 1);
                        }

                        writeMatrix(adjacencyMatrix);
                        Panel.this.repaint();
                    }

                }
            }
        });
    }

    private void addNode(int x, int y) {

        boolean canAddNode = true;
        for(Node node : this.nodesList)
            if(calculateDistance(new Point(x-10, y-10), node.getPoint()) < 60)
                canAddNode = false;

        if(canAddNode) {
            Node node = new Node(x - 10, y - 10, this.nodeNo);
            this.nodesList.add(node);
            ++this.nodeNo;
            this.repaint();

            //updatare matrice adiacenta
            List<Integer> row = new ArrayList<>();
            Panel.this.adjacencyMatrix.add(currentAdjacencySize, row);
            for(int j = 0; j <= currentAdjacencySize; ++j)
                row.add(j, 0);
            for(int i = 0; i < currentAdjacencySize; ++i)
                Panel.this.adjacencyMatrix.get(i).add(currentAdjacencySize, 0);

            ++this.currentAdjacencySize;
            this.writeMatrix(Panel.this.adjacencyMatrix);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Panel.this.setFocusable(true);
        Panel.this.requestFocusInWindow();
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
                if(node.getNumber() != Panel.this.whichNodeIsMoving.getNumber() && calculateDistance(this.pointEnd, node.getPoint()) < 60)
                    movingIntoANode = true;

            if(!movingIntoANode) {

                //update muchii
                for (var edge : Panel.this.edgesList) {
                    Point start = edge.getStart();
                    Point end = edge.getEnd();
                    if (calculateDistance(new Point(start.x - 15, start.y - 15), whichNodeIsMoving.getPoint()) <= 30) {
                        edge.setStartX(this.pointEnd.x + 15);
                        edge.setStartY(this.pointEnd.y + 15);
                    } else if (calculateDistance(new Point(end.x - 15, end.y - 15), whichNodeIsMoving.getPoint()) <= 30) {
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
