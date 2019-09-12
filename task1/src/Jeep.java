import java.awt.*;

public class Jeep implements PicObject {

    private int x, y, // The coordinates of the lower left corner of the machine
            width, height;
    private double hullWidth, hullHeight;
    private double doorWidth;
    private double doorHeight;
    private double wheelDiameter;

    private int xD, yD;

    private Color color;

    public Jeep(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        hullWidth = 0.9 * width;
        wheelDiameter = 0.2 * hullWidth;
        hullHeight = height - (wheelDiameter / 2);
        doorWidth = 0.25 * hullWidth;
        doorHeight = 0.8 * hullHeight;
    }

    @Override
    public void draw(Graphics2D g2) {
        drawHull(g2, x, y, (int) hullWidth, (int) (hullHeight), color);
        drawWheel(g2, x + (int) (0.365 * hullWidth / 2 - wheelDiameter / 2), y + (int) (0.55 * hullHeight / 2 - wheelDiameter / 2), (int) wheelDiameter, Color.GRAY);
        drawWheel(g2, x + (int) (hullWidth - 0.365 * hullWidth / 2 - wheelDiameter / 2), y + (int) (0.55 * hullHeight / 2 - wheelDiameter / 2), (int) wheelDiameter, Color.GRAY);
        drawDoor(g2, xD, yD, doorWidth, doorHeight, color);
    }

    private void drawDoor(Graphics2D g2, int x, int y, double width, double height, Color color) {
        int xW, yW;

        int nPoints = 8;
        int n = 0;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        xPoints[nPoints-1] = xPoints[n] = x;
        yPoints[nPoints-1] = yPoints[n] = y;
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.7 * width);
        yPoints[n] = yPoints[n - 1];
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.1 * width);
        yPoints[n] = yPoints[n - 1] - (int) (0.125 * height);
        ++n;
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] - (int) (0.375 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.25 * width);
        yPoints[n] = yPoints[n - 1] - (int) (0.5 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.75 * width);
        yPoints[n] = yPoints[n - 1];
        xW = (int) (xPoints[n] + 0.1*width);
        yW = (int) (yPoints[n] + 0.1*height);
        ++n;
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] + (int) (0.75 * height);
        ++n;
        g2.setColor(color);
        g2.fillPolygon(xPoints, yPoints, nPoints);
        g2.setColor(Color.RED);
        g2.drawPolygon(xPoints, yPoints, nPoints);

        g2.setColor(Color.BLACK);
        n = 0;
        nPoints = 5;
        xPoints = new int[nPoints];
        yPoints = new int[nPoints];
        xPoints[nPoints-1] = xPoints[n] = xW;
        yPoints[nPoints-1] = yPoints[n] = yW;
        ++n;
        xPoints[n] = (int) (xPoints[n-1] + 0.6*width);
        yPoints[n] = yPoints[n-1];
        ++n;
        xPoints[n] = (int) (xPoints[n-1] + 0.15*width);
        yPoints[n] = yPoints[n-1] + (int) (0.3 * height);
        ++n;
        xPoints[n] = (int) (xPoints[n-1] - 0.75*width);
        yPoints[n] = yPoints[n-1];
        g2.fillPolygon(xPoints, yPoints, nPoints);
    }

    private void drawWheel(Graphics2D g2, int x, int y, int diameter, Color color) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval((int) (x - 0.1 * diameter), (int) (y - 0.1 * diameter), (int) (diameter + 0.2 * diameter), (int) (diameter + 0.2 * diameter));
        g2.setColor(color);
        g2.fillOval(x, y, diameter, diameter);
        g2.setColor(Color.BLACK);
        Polygon p = new Polygon();
        int indent = diameter / 8; // отступ от центра
        int side = diameter / 4;   // длина стороны треугольника
        int cX = x + diameter / 2; // центр колеса
        int cY = y + diameter / 2;
        double angle = 2 * Math.PI / 5;  // угол между треугольниками
        double trAngle = 2 * Math.PI / 10; // половина угла вершины треугольника
        p.npoints = 4;
        p.xpoints = new int[4];
        for (int i = 0; i < 5; i++) {
            p.xpoints[0] = (int) (cX + indent * Math.cos(angle * i));
            p.xpoints[1] = (int) (p.xpoints[0] + side * Math.cos(trAngle + angle * i));
            p.xpoints[2] = p.xpoints[3] = (int) (p.xpoints[0] + side * Math.cos(-trAngle + angle * i));
            p.ypoints[0] = (int) (cY + indent * Math.sin(angle * i));
            p.ypoints[1] = (int) (p.ypoints[0] + side * Math.sin(trAngle + angle * i));
            p.ypoints[2] = p.ypoints[3] = (int) (p.ypoints[0] + side * Math.sin(-trAngle + angle * i));
            g2.fillPolygon(p);
        }
    }

    /**
     * Draws the frame of the car from the lower left corner
     *
     * @param x     - coordinate of the lower left corner along the abscissa
     * @param y     - coordinate of the lower left corner along the ordinate
     * @param color - colour of the hull
     */
    private void drawHull(Graphics2D g2, int x, int y, int width, int height, Color color) {
        g2.setColor(color);
        int nPoints = 24;
        int n = 0;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        xPoints[n] = x;
        yPoints[n] = y;
        ++n;
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] - (int) (0.25 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.365 * width);
        yPoints[n] = yPoints[n - 1];
        ++n;//3
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] + (int) (0.25 * doorHeight);
        ++n;
        xD = xPoints[n] = xPoints[n - 1] + (int) (0.2 * doorWidth);
        yD = yPoints[n] = yPoints[n - 1] + (int) (0.25 * doorHeight);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.7 * doorWidth);
        yPoints[n] = yPoints[n - 1];
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.1 * doorWidth);
        yPoints[n] = yPoints[n - 1] - (int) (0.125 * doorHeight);
        ++n;
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] - (int) (0.375 * doorHeight);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.25 * doorWidth);
        yPoints[n] = yPoints[n - 1] - (int) (0.5 * doorHeight);
        ++n;
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] - (int) (0.05 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.025 * width);
        yPoints[n] = yPoints[n - 1];
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.05 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.4 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.365 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.05 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.0075 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.1 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.0025 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.1 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.05 * width);
        yPoints[n] = yPoints[n - 1];
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.03 * width);
        yPoints[n] = yPoints[n - 1] - (int) (0.07 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (wheelDiameter);
        yPoints[n] = yPoints[n - 1];
        n++;
        xPoints[n] = xPoints[n - 1] - (int) (0.1 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.425 * height);
        n++;
        xPoints[n] = xPoints[n - 1] - (int) (0.259 * width);
        yPoints[n] = yPoints[n - 1];
        n++;
        xPoints[n] = xPoints[n - 1] - (int) (0.07 * width);
        yPoints[n] = yPoints[n - 1] - (int) (0.4 * height);
        n++;
        xPoints[n] = xPoints[n - 1] - (int) (wheelDiameter);
        yPoints[n] = yPoints[n - 1];
        n++;
        xPoints[n] = xPoints[n - 1] - (int) (0.03 * width);
        yPoints[n] = yPoints[0];
        n++;
        xPoints[n] = xPoints[0];
        yPoints[n] = yPoints[0];
        g2.fillPolygon(xPoints, yPoints, nPoints);
    }

}
