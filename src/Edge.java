import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.lang.Object;
public class Edge {
    private Point start;
    private Point end;

    private Node startNode;
    private Node endNode;


    private boolean directed = false;
    public Edge(Point start, Point end, boolean directed, Node startNode, Node endNode) {
        this.start = start;
        this.end = end;
        this.directed = directed;

        this.startNode = startNode;
        this.endNode = endNode;
    }

    public Node getStartNode() {
        return this.startNode;
    }

    public Node getEndNode() {
        return this.endNode;
    }
    public Point getStart() {
        return start;
    }

    public void setDirectedUndirected(boolean x) {
        this.directed = x;
    }

    public Point getEnd() {
        return end;
    }

    public void setStartX(int coord) {
        this.start.x = coord;
    }

    public void setStartY(int coord) {
        this.start.y = coord;
    }
    public void setEndX(int coord) {
        this.end.x = coord;
    }

    public void setEndY(int coord) {
        this.end.y = coord;
    }

    public boolean isDirected() {
        return this.directed;
    }
    public void drawEdge(Graphics g) {
        if(this.start != null) {
            if(directed) {
                g.setColor(Color.BLUE);
                g.drawLine(this.start.x, this.start.y, this.end.x, this.end.y);
                g.setColor(Color.BLUE);
                AffineTransform tx = new AffineTransform();
                Polygon arrowHead = new Polygon();
                arrowHead.addPoint( 0, 5 - 20);
                arrowHead.addPoint( -7,  -17 - 20);
                arrowHead.addPoint( 0,  -7- 20);
                arrowHead.addPoint(  7, -17 - 20);

                double angle = Math.atan2(this.end.y-this.start.y, this.end.x-this.start.x);
                tx.translate(this.end.x, this.end.y);
                tx.rotate((angle-Math.PI/2d));

                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setTransform(tx);
                g2d.fill(arrowHead);

            }
            else {
                g.setColor(Color.RED);
                g.drawLine(this.start.x, this.start.y, this.end.x, this.end.y);
            }
        }
    }
}
