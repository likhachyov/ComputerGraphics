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
    public void drawPixel(int x, int y, int transparency, Color c) {
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
                drawPixel(p.ell.getX0() + p.x, p.ell.getY0() + p.y, p.alpha, p.ell.getColor());
        }
    }

    @Override
    public void drawPixels(Queue<Ellipse.Point> q) {
        Ellipse.Point p;
        while (!q.isEmpty()) {
            p = q.poll();
            if (p.ell.isRange(p.x, p.y, true))
                drawPixel(p.ell.getX0() + p.x, p.ell.getY0() + p.y, p.alpha, p.ell.getColor());
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
                pd.drawPixel(x, y, (int) (255 * (1 - error)), c); // основную точку рисуем с прозрачностью 1- error
                pd.drawPixel(x, secondY, (int) (255 * error), c);// вторую с прозрачностью равной error
            } else { // если быстрее растет У
                y += directionY;
                error += 1 / tangent;
                if (error >= 1) {
                    x += directionX;
                    secondX += directionX;
                    error -= 1;
                }
                pd.drawPixel(x, y, (int) (255 * (1 - error)), c); // основную точку рисуем с прозрачностью 1- error
                pd.drawPixel(secondX, y, (int) (255 * error), c);// вторую с прозрачностью равной error
            }
        }
    }


    //   рисует контур пая
    @Override
    public void putPixels(int x0, int y0, double x, double y, int from, int to, double alpha, int part, Color color) {
        alpha = Math.toDegrees(alpha);
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
    public void fill(int x0, int y0, double x, double y, int from, int to, double alpha, Color color) {
        alpha = Math.toDegrees(alpha);
        double x1 = 0, x2 = 0; // координаты линии
        int iy = IPart(y);
        //I квадрант
        if (from < to) {
            if (isQuarter(from, to, alpha, 1)) {
                x1 = to >= 90 ? 0 : getX(iy, to);
                // от from  до контура
                drawLine(this, (int) (x0 + x1), y0 + iy, (int) (x0 + x), y0 + iy, color);
                // если линия не доходит до контура
            } else if (from < 90 && alpha < from) {
                x1 = to >= 90 ? 0 : getX(iy, to);
                x2 = getX(iy, from);
                // от OY/to до from
                drawLine(this, (int) (x0 + x1), y0 + iy, x0 + IPart(x2), y0 + iy, color);
            }
            //II квадрант
            if (isQuarter(from, to, alpha, 2)) { // точка лежит на контуре
                x1 = from > 90 && from < 180 ? getX(iy, 180 - from) : 0; // если линия from в этой четверти, рисуем к ней, иначе к OY
                drawLine(this, (int) (x0 - x1), y0 + iy, (int) (x0 - x), y0 + iy, color);
            } else if (to < 180 && alpha < 180 - from) { //линия не доходит до контура
                x1 = getX(iy, 180 - to); // point on на прямой to
                x2 = from > 90 ? getX(iy, 180 - from) : 0; // точка на прямой from
                drawLine(this, (int) (x0 - x1), y0 + iy, x0 - IPart(x2), y0 + iy, color);
            }
            //III квадрант
            if (isQuarter(from, to, alpha, 3)) {
                x1 = to >= 270 ? 0 : getX(iy, to - 180);
                // от  to  до контура
                drawLine(this, (int) (x0 - x1), y0 - iy, (int) (x0 - x), y0 - iy, color);
//                 если линия не доходит до контура (упирается в from)
            } else if (from < 270 && alpha < from - 180) {
                x1 = to >= 270 ? 0 : getX(iy, to - 180);
                x2 = getX(iy, from - 180);
                // от OY/to до from
                drawLine(this, (int) (x0 - x1), y0 - iy, x0 - IPart(x2), y0 - iy, color);
            }
            //IV квадрант
            if (isQuarter(from, to, alpha, 4)) {
                x1 = from > 270 ? getX(iy, 360 - from) : 0; // если линия from в этой четверти, рисуем к ней, иначе к OY
                drawLine(this, (int) (x0 + x1), y0 - iy, (int) (x0 + x), y0 - iy, color);
            } else if (to < 360 && alpha < 360 - from) { //линия не доходит до контура
                x1 = getX(iy, 360 - to); // point on на прямой to
                x2 = from > 270 ? getX(iy, 360 - from) : 0; // точка на прямой from
                drawLine(this, (int) (x0 + x1), y0 - iy, x0 + IPart(x2), y0 - iy, color);
            }
            // если угол to > from
        } else {
            //I квадрант
            if (isQuarter(from, to, alpha, 1)) { // если точка на контуре пая
                if (alpha < to) {
                    x1 = to < 90 ? getX(iy, to) : 0;
                    // от контура до линии to
                    drawLine(this, (int) (x0 + x1), y0 + iy, (int) (x0 + x), y0 + iy, color);
                    if (from < 90) {
                        x2 = getX(iy, from);
                        // от ОY до from
                        drawLine(this, x0, y0 + iy, (int) (x0 + x2), y0 + iy, color);
                    }
                } else // когда угол больше to: от OY до контура
                    drawLine(this, x0, y0 + iy, (int) (x0 + x), y0 + iy, color);
                // если точка не входит в контар
            } else if (from < 90) { // рисуем от OY до линии from
                x2 = getX(iy, from);
                drawLine(this, x0, y0 + iy, (int) (x0 + x2), y0 + iy, color);
            }
            //II квадрант
            if (isQuarter(from, to, alpha, 2)) {
                if (to >= 180) { // вся четверть входит
                    drawLine(this, x0, y0 + iy, (int) (x0 - x), y0 + iy, color);
                } else if (from < 180 && alpha < 180 - from) {
                    x2 = getX(iy, 180 - from); // рисуем от from до OY
                    drawLine(this, (int) (x0 - x2), y0 + iy, (int) (x0 - x), y0 + iy, color);
                    if (to > 90) { // рисуем от to до контура
                        x2 = getX(iy, 180 - to);
                        drawLine(this, x0, y0 + iy, (int) (x0 - x2), y0 + iy, color);
                    }
                } else if (to > 90) { // от OY до контура
                    drawLine(this, x0, y0 + iy, (int) (x0 - x), y0 + iy, color);
                }

            } else {
                if (to > 90) {
                    x2 = getX(iy, 180 - to);
                    drawLine(this, x0, y0 + iy, (int) (x0 - x2), y0 + iy, color);
                }
            }
            //III квадрант
            if (isQuarter(from, to, alpha, 3)) {
                if (alpha < to-180) {
                    x1 = to < 270 ? getX(iy, to-180) : 0;
                    // от контура до линии to
                    drawLine(this, (int) (x0 - x1), y0 - iy, (int) (x0 - x), y0 - iy, color);
                    if (from < 270) {
                        x2 = getX(iy, from-180);
                        // от ОY до from
                        drawLine(this, x0, y0 - iy, (int) (x0 - x2), y0 - iy, color);
                    }
                } else // когда угол больше to: от OY до контура
                    drawLine(this, x0, y0 - iy, (int) (x0 - x), y0 - iy, color);
                // если точка не входит в контар
            } else if (from < 270) { // рисуем от OY до линии from
                x2 = getX(iy, from-180);
                drawLine(this, x0, y0 - iy, (int) (x0 - x2), y0 - iy, color);
            }
            //IV квадрант
            if (isQuarter(from, to, alpha, 4)) {
                if (alpha < 360-from) {
                    x1 = from > 270 && from < 360 ? getX(iy, 360-from) : 0;
                    // от контура до линии from
                    drawLine(this, (int) (x0 + x1), y0 - iy, (int) (x0 + x), y0 - iy, color);
                    if (to > 270) {
                        x2 = getX(iy, 360-to);
                        // от ОY до to
                        drawLine(this, x0, y0 - iy, (int) (x0 + x2), y0 - iy, color);
                    }
                }
                else // когда угол больше to: от OY до контура
                    drawLine(this, x0, y0 - iy, (int) (x0 + x), y0 - iy, color);
                // если точка не входит в контар
            } else if (to > 270) { // рисуем от OY до линии to
                x2 = getX(iy, 360-to);
                drawLine(this, x0, y0 - iy, (int) (x0 + x2), y0 - iy, color);
            }
        }
    }

    // Находим Х на контуре пая через тангенс
    private double getX(int y, int alpha) {
        return y / Math.tan(Math.toRadians(alpha));
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
