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

    private void drawLine(IPixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
        int x = x1;
        int y = y1;
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double tangent = dy / dx;
        float error = 0;   // The offset of the real Y coordinate relative to the rendered
        int directionY = (int) Math.signum(y2 - y1);
        int directionX = (int) Math.signum(x2 - x1);
        int secondY = y + directionY;
        int secondX = x + directionX;
        for (int i = 0; i < (tangent <= 1 ? dx : dy); i++) {
            if (i == 0) {
                pd.drawPixel(x, y, c); // красим первый пиксель
            }
            if (tangent <= 1) {
                x += directionX;
                error += tangent;
                if (error >= 1) {
                    y += directionY;
                    secondY += directionY;
                    error -= 1;
                }
//                System.out.println(" WX " + x);
                pd.drawPixel(x, y, c, (int) (255 * (1 - error))); // основную точку рисуем с прозрачностью 1- error
                pd.drawPixel(x, secondY, c, (int) (255 * error));// вторую с прозрачностью равной error
            } else { // если быстрее растет У
                y += directionY;
                error += 1 / tangent;
                if (error >= 1) {
                    x += directionX;
                    secondX += directionX;
                    error -= 1;
                }
                pd.drawPixel(x, y, c, (int) (255 * (1 - error))); // основную точку рисуем с прозрачностью 1- error
                pd.drawPixel(secondX, y, c, (int) (255 * error));// вторую с прозрачностью равной error
            }
        }
    }

    @Override
    public void putPixels(int x0, int y0, double x, double y, int from, int to, double alpha, int part, Color color) {
        alpha = Math.toDegrees(alpha);
        double x1, x2;
        switch (part) {
            //Первая часть дуги
            case 1: {
                //I квадрант
                if (isQuarter(from, to, alpha, 1)) {
                    putPixel(g, color, (int) (x0 + x), y0 + IPart(y), 255 - (int) (FPart(y) * 255));
                    putPixel(g, color, (int) (x0 + x), y0 + IPart(y) + 1, (int) (FPart(y) * 255));
                }
                //II квадрант, Y
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
                }
                //II квадрант
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

    @Override
    public void fill(int x0, int y0, double x, double y, int from, int to, double alpha, int part, Color color) {
        alpha = Math.toDegrees(alpha);
        double x1 = 0, x2 = 0;
        switch (part) {
            //Первая часть дуги
            case 1: {
                //I квадрант
                if (from < to) {
                    if (isQuarter(from, to, alpha, 1)) {
                        x1 = to >= 90 ? 0 : (IPart(y) / Math.tan(Math.toRadians(to)));
                        drawLine(this, (int) (x0 + x1), y0 + IPart(y), (int) (x0 + x), y0 + IPart(y), color);
                    } else if (from < 90 && alpha < from) {
                        x1 = to >= 90 ? 0 : (IPart(y) / Math.tan(Math.toRadians(to)));
                        x2 = (IPart(y) / Math.tan(Math.toRadians(from)));
                        drawLine(this, (int) (x0 + x1), y0 + IPart(y), (int) (x0 + x2), y0 + IPart(y), color);
                    }
                    //II квадрант
                    if (isQuarter(from, to, alpha, 2)) {
                        putPixel(g, color, (int) (x0 - x), y0 + IPart(y), 255 - (int) (FPart(y) * 255));
                        putPixel(g, color, (int) (x0 - x), y0 + IPart(y) + 1, (int) (FPart(y) * 255));
                    }//III квадрант
                    if (isQuarter(from, to, alpha, 3)) {
                        putPixel(g, color, (int) (x0 - x), y0 - IPart(y), 255 - (int) (FPart(y) * 255));
                        putPixel(g, color, (int) (x0 - x), y0 - IPart(y) - 1, (int) (FPart(y) * 255));
                    }//IV квадрант
                    if (isQuarter(from, to, alpha, 4)) {
                        putPixel(g, color, (int) (x0 + x), y0 - IPart(y), 255 - (int) (FPart(y) * 255));
                        putPixel(g, color, (int) (x0 + x), y0 - IPart(y) - 1, (int) (FPart(y) * 255));
                    }
                    // если угол to > from
                } else {
                    //I квадрант
                    if (isQuarter(from, to, alpha, 1)) { // если точка на контуре пая
                        if (alpha < to) {
                            x1 = (IPart(y) / Math.tan(Math.toRadians(to)));
                            // от контура до линии to
                            drawLine(this, (int) (x0 + x1), y0 + IPart(y), (int) (x0 + x), y0 + IPart(y), color);
                            if (from < 90) {
                                x2 = (IPart(y) / Math.tan(Math.toRadians(from)));
                                // от ОY до from
                                drawLine(this, x0, y0 + IPart(y), (int) (x0 + x2), y0 + IPart(y), color);
                            }
                        } else // когда угол больше to: от OY до контура
                            drawLine(this, x0, y0 + IPart(y), (int) (x0 + x), y0 + IPart(y), color);
                        // если точка не входит в контар
                    } else if (from < 90) { // рисуем от OY до линии from
                        x2 = (IPart(y) / Math.tan(Math.toRadians(from)));
                        drawLine(this, x0, y0 + IPart(y), (int) (x0 + x2), y0 + IPart(y), color);
                    }
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
                if (from < to) {
                    if (isQuarter(from, to, alpha, 1)) {
                        drawLine(this, x0, y0 + IPart(y), x0 + IPart(x), y0 + IPart(y), color);
                    } else if (alpha < from && from < 90) {
                        x1 = to >= 90 ? 0 : (IPart(y) / Math.tan(Math.toRadians(to)));
                        x2 = (IPart(y) / Math.tan(Math.toRadians(from)));
                        drawLine(this, (int) (x0 + x1), y0 + IPart(y), x0 + IPart(x2), y0 + IPart(y), color);
                    }
                    //II квадрант
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
                } else {
                    //I квадрант
                    if (isQuarter(from, to, alpha, 1)) { // если точка на контуре пая
                        if (alpha < to) {
                            x1 = (IPart(y) / Math.tan(Math.toRadians(to)));
                            // от контура до линии to
                            drawLine(this, (int) (x0 + x1), y0 + IPart(y), (int) (x0 + x), y0 + IPart(y), color);
                            if (from < 90) {
                                x2 = (IPart(y) / Math.tan(Math.toRadians(from)));
                                // от ОY до from
                                drawLine(this, x0, y0 + IPart(y), (int) (x0 + x2), y0 + IPart(y), color);
                            }
                        } else // когда угол больше to: от OY до контура
                            drawLine(this, x0, y0 + IPart(y), (int) (x0 + x), y0 + IPart(y), color);
                        // если точка не входит в контар
                    } else if (from < 90) { // рисуем от OY до линии from
                        x2 = (IPart(y) / Math.tan(Math.toRadians(from)));
                        drawLine(this, x0, y0 + IPart(y), (int) (x0 + x2), y0 + IPart(y), color);
                    }
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
                return from < to && to > 270 && 360 - alpha >= from && 360 - alpha <= to ||
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
    private static int IPart(double x) {
        return (int) x;
    }

    //дробная часть числа
    private static double FPart(double x) {
        while (x >= 0)
            x--;
        x++;
        return x;
    }

}
