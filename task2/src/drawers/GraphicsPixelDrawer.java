package drawers;

import Interfaces.IPixelDrawer;

import java.awt.*;
import java.util.Queue;
import java.util.Stack;

public class GraphicsPixelDrawer implements IPixelDrawer {

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
    public void drawPixels(Stack<Ellipse.Point> s) {
        Ellipse.Point p;
        while (!s.empty()) { // foreEach почему-то достаёт элементы из стэка по принципу очереди
            p = s.pop();
            if (p.ell.isRange(p.x, p.y, true))
                drawPixel(p.ell.getX0() + p.x, p.ell.getY0() + p.y, p.ell.getColor(), p.alpha);
        }
    }

    @Override
    public void drawPixels(Queue<Ellipse.Point> q) {
        Ellipse.Point p;
        while (!q.isEmpty()) {
            p = q.poll();
            if (p.ell.isRange(p.x, p.y, true))
                drawPixel(p.ell.getX0() + p.x, p.ell.getY0() + p.y, p.ell.getColor(), p.alpha);
        }
    }

    @Override
    public void putPixels(int x0, int y0, float x, float y, int from, int to, double alpha, int part, Color color) {
        alpha = Math.toDegrees(alpha);
        switch (part) {
            //Первая часть дуги
            case 1: {
                //I квадрант
                if (isQuarter(from, to, alpha, 1)) {
                    putPixel(g, color, (int) (x0 + x), y0 + IPart(y), 255 - (int) (FPart(y) * 255));
                    putPixel(g, color, (int) (x0 + x), y0 + IPart(y) + 1, (int) (FPart(y) * 255));
                }//II квадрант, Y
                if (isQuarter(from, to, alpha, 2)) {
                    putPixel(g, color, (int) (x0 - x), y0 + IPart(y), 255 - (int) (FPart(y) * 255));
                    putPixel(g, color, (int) (x0 - x), y0 + IPart(y) + 1, (int) (FPart(y) * 255));
                }//III квадрант, Y
                if (isQuarter(from, to, alpha, 3)) {
                    putPixel(g, color, (int) (x0 - x), y0 - IPart(y), 255 - (int) (FPart(y) * 255));
                    putPixel(g, color, (int) (x0 - x), y0 - IPart(y) - 1, (int) (FPart(y) * 255));
                }//IV квадрант, Y
                if (isQuarter(from, to, alpha, 4)) {
                    putPixel(g, color, (int) (x0 + x), y0 - IPart(y), 255 - (int) (FPart(y) * 255));
                    putPixel(g, color, (int) (x0 + x), y0 - IPart(y) - 1, (int) (FPart(y) * 255));
                }
                break;
            }
            // рисуем утерянный при переходе пиксел
            case 2: {
                if (isQuarter(from, to, alpha, 1))
                    putPixel(g, color, (int) (x0 + x), y0 + IPart(y + 1), (int) (FPart(y) * 255));
                if (isQuarter(from, to, alpha, 2))
                    putPixel(g, color, (int) (x0 - x), y0 + IPart(y + 1), (int) (FPart(y) * 255));
                if (isQuarter(from, to, alpha, 3))
                    putPixel(g, color, (int) (x0 - x), y0 - IPart(y + 1), (int) (FPart(y) * 255));
                if (isQuarter(from, to, alpha, 4))
                    putPixel(g, color, (int) (x0 + x), y0 - IPart(y + 1), (int) (FPart(y) * 255));
                break;
            }
            // 2-я часть дуги
            case 3: { // X - сдвиг для доп. точки через OX
                //I квадрант
                if (isQuarter(from, to, alpha, 1)) {
                    putPixel(g, color, x0 + IPart(x), (int) (y0 + y), 255 - (int) (FPart(x) * 255));
                    putPixel(g, color, x0 + IPart(x) + 1, (int) (y0 + y), (int) (FPart(x) * 255));
                }//II квадрант
                if (isQuarter(from, to, alpha, 2)) {
                    putPixel(g, color, x0 - IPart(x) - 1, (int) (y0 + y), (int) (FPart(x) * 255));  // shade was on the other side
                    putPixel(g, color, x0 - IPart(x), (int) (y0 + y), 255 - (int) (FPart(x) * 255));
                }//III квадрант
                if (isQuarter(from, to, alpha, 3)) {
                    putPixel(g, color, x0 - IPart(x), (int) (y0 - y), 255 - (int) (FPart(x) * 255));
                    putPixel(g, color, x0 - IPart(x) - 1, (int) (y0 - y), (int) (FPart(x) * 255));
                }//IV квадрант
                if (isQuarter(from, to, alpha, 4)) {
                    putPixel(g, color, x0 + IPart(x), (int) (y0 - y), 255 - (int) (FPart(x) * 255));
                    putPixel(g, color, x0 + IPart(x) + 1, (int) (y0 - y), (int) (FPart(x) * 255));
                }
                break;
            }
        }
    }


    private boolean isQuarter(int from, int to, double alpha, int q) {
        switch (q) {
            case 1: {
                return from < to && from <= 90 && alpha >= from && alpha <= to ||
                        to < from && !(alpha > to && alpha < from);
            }
            case 2: {
                return from < to && to >= 90 && from <= 180 && 180 - alpha >= from && 180 - alpha <= to ||
                        to < from && !(180 - alpha > to && 180 - alpha < from);
            }
            case 3: {
                return from < to && to >= 180 && from <= 270 && 180 + alpha >= from && 180 + alpha <= to ||
                        to < from && !(180 + alpha > to && 180 + alpha < from);
            }
            case 4: {
                return from < to && to >= 270 && 360 - alpha >= from && 360 - alpha <= to ||
                        to < from && !(360 - alpha > to && 360 - alpha < from);
            }
        }
        return false;
    }

    private static void putPixel(Graphics g, Color col, int x, int y, int alpha) {
        g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha));
        g.drawLine(x, y, x, y);
    }

    //Целая часть числа
    private static int IPart(float x) {
        return (int) x;
    }

    //дробная часть числа
    private static float FPart(float x) {
        while (x >= 0)
            x--;
        x++;
        return x;
    }

}
