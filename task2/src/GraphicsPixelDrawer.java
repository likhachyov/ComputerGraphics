import java.awt.*;

public class GraphicsPixelDrawer implements PixelDrawer {

    private Graphics2D g;

    public GraphicsPixelDrawer(Graphics2D g) {
        this.g = g;
    }

    @Override
    public void drawPixel(int x, int y, Color c) {
        g.setColor(c);
        g.drawLine(x, y, x, y);
    }
}
