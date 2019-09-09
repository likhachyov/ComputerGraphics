import java.awt.*;

public class Jeep implements PicObject {

    private int x, y, // The coordinates of the lower left corner of the machine
            width, height;

    private Color color;

    public Jeep(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2) {
        drawHull(g2, x, y, (int) (0.9 * width), (int) (0.65 * height), color);
    }

    /**
     * Draws the frame of the car from the lower left corner
     *
     * @param x     - coordinate of the lower left corner along the abscissa
     * @param y     - coordinate of the lower left corner along the ordinate
     * @param color - colour of the hull
     */
    private void drawHull(Graphics2D g2, int x, int y, int width, int height, Color color) {
        Font font = g2.getFont();
        g2.setColor(color);
        int nPoints = 15;
        int n = 0;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        double doorWidth = 0.25 * width;
        double doorHeight = 0.8 * height;
        xPoints[n] = /*xPoints[nPoints - 1] = */x;
        yPoints[n] = /*yPoints[nPoints - 1] =*/ y;
        ++n;
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] - (int) (0.25 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.3 * width);
        yPoints[n] = yPoints[n - 1];
        ++n;//3
        xPoints[n] = xPoints[n - 1];
        yPoints[n] = yPoints[n - 1] + (int) (0.25 * doorHeight);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.2 * doorWidth);
        // 60° rad
        yPoints[n] = yPoints[n - 1] + (int) (0.25 * doorHeight);
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
        xPoints[n] = xPoints[n - 1] + (int) (0.43 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.05 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] + (int) (0.0075 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.1 * height);
        ++n;
        xPoints[n] = xPoints[n - 1] - (int) (0.0025 * width);
        yPoints[n] = yPoints[n - 1] + (int) (0.1 * height);
        g2.drawPolyline(xPoints, yPoints, nPoints);
    }

}
