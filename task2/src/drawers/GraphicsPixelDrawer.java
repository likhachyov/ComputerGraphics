package drawers;

import Interfaces.PixelDrawer;

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

    @Override
    public void drawPixel(int x, int y, Color c, int transparency) {
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), transparency));
        g.drawLine(x, y, x, y);
    }
}
