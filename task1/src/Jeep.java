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
    }

}
