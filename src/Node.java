import java.awt.*;

public class Node {
    private int coordX;
    private int coordY;
    private int number;

    public Node(int coordX, int coordY, int number) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.number = number;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCoordX() {
        return this.coordX;
    }

    public int getCoordY() {
        return this.coordY;
    }

    public int getNumber() {
        return this.number;
    }

    public Point getPoint() {
        return new Point(this.coordX, this.coordY);
    }

    public void drawNode(Graphics g, int node_diam) {
        g.setColor(Color.ORANGE);
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);
        g.fillOval(coordX, coordY, node_diam, node_diam);
        g.setColor(Color.black);
        g.drawOval(coordX, coordY, node_diam, node_diam);
        g.setColor(Color.black);
        if(this.number < 10)
            g.drawString(((Integer)this.number).toString(), coordX+12, coordY + 20);
        else
            g.drawString(((Integer)this.number).toString(), coordX+8, coordY + 20);
    }
}
