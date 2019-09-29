package drawers;

import Interfaces.PixelDrawer;

import java.awt.*;
import java.util.Queue;
import java.util.Stack;

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

    @Override
    public void drawMirrorPixels(Ellipse ell, int x, int y, Color color, int transparency) {
        int x0 = ell.getX0();
        int y0 = ell.getY0();
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), transparency);
        if (ell.isRange(x, y, false))
            drawPixel(x0 + x, y0 + y, color);
        if (ell.isRange(x, -y, false))
            drawPixel(x0 + x, y0 - y, color);
        if (ell.isRange(-x, y, false))
            drawPixel(x0 - x, y0 + y, color);
        if (ell.isRange(-x, -y, false))
            drawPixel(x0 - x, y0 - y, color);
    }

    @Override
    public void drawPixels(Stack<WuDrawer.Point> s) {
        WuDrawer.Point p;
        while (!s.empty()) {
            p = s.pop();
            if (p.ell.isRange(p.x, p.y, true))
                drawPixel(p.ell.getX0() + p.x, p.ell.getY0() + p.y, p.ell.getColor(), p.alpha);
        }
    }

    @Override
    public void drawPixels(Queue<WuDrawer.Point> q) {
        q.forEach(p -> {
            if (p.ell.isRange(p.x, p.y, true)) drawPixel(p.ell.getX0() + p.x, p.ell.getY0() + p.y, p.ell.getColor(), p.alpha);
        });
    }

}
