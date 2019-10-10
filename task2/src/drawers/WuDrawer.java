package drawers;

import Interfaces.IPixelDrawer;

import java.awt.*;
import java.util.*;

public class WuDrawer {

    private ArrayList<Ellipse.Point> drawLine(IPixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
        ArrayList<Ellipse.Point> contour = new ArrayList<>();
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
                contour.add(new Ellipse.Point(x, y));
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
            contour.add(new Ellipse.Point(x, y));
        }
        return contour;
    }

    public void drawPie(IPixelDrawer pd, int x0, int y0, int a, int b, int from, int to, Color color, boolean fill) {
        Ellipse ell = new Ellipse(x0, y0, a, b, from, to, color);

        Queue<Ellipse.Point> q2 = new ArrayDeque<>(); // коллекция для каждой четверти
        Queue<Ellipse.Point> q4 = new ArrayDeque<>(); // эллипс начинает рисоваться в 4-й четверти (в координатах экрана)
        Stack<Ellipse.Point> q1 = new Stack<>(); // точки сразу отражаются в остальные 3, но при отрисовке надо рисовать их
        Stack<Ellipse.Point> q3 = new Stack<>(); // в правильном порядке от ОХ по часовой
        int x = 0;
        int y = b;
        int D = (b * b - 2 * b * a * a + a * a); // разность квадратов расстояний от центра и дуги до пиксела в диагональном направлении
        // расстояния до трёх точек: диаогональной, горизонтальной, вертикальной
        double d, h, v;
        int x2 = x;
        int y2 = y;
        int alpha = 255;
        while (y >= 0) {
            q1.push(new Ellipse.Point(ell, x, y, alpha));
            q2.add(new Ellipse.Point(ell, -x, y, alpha));
            q3.push(new Ellipse.Point(ell, -x, -y, alpha));
            q4.add(new Ellipse.Point(ell, x, -y, alpha));
            pd.drawMirrorPixels(ell, x2, y2, color, 255 - alpha);

            d = ell.shortestDistance(x + 1, y - 1);
            // Диагональная точка внутри дуги
            if (D < 0) {
                h = ell.shortestDistance(x + 1, y);
                // если диагональная т. ближе
                if (Math.abs(d) <= Math.abs(h)) {
                    alpha = (int) (Math.abs(h) / (Math.abs(d) + Math.abs(h)) * 255);
                    // распределяем цвет на точку выше
                    y2 = y;
                    x++;
                    y--;
                    x2 = x;
                    // Рекуррентно перерасчитываем расстояние от дуги до диаг. точки
                    D = (D + b * b * (2 * x + 1) - 2 * y * a * a + a * a);
                } else {
                    alpha = (int) (Math.abs(d) / (Math.abs(d) + Math.abs(h)) * 255);
                    // сдвиг в горизонтальном напр.
                    x++;
                    // если наша точка уже вне эллипса, дополнительно распределяем цвет на точку ниже, иначе выше
                    y2 = ell.distanceToByX(x, y) < 0 ? y + 1 : y - 1;
                    x2 = x;
                    D = (D + b * b * (2 * x + 1));
                }
            } else if (D > 0) {
                v = ell.shortestDistance(x, y - 1);
                if (Math.abs(d) <= Math.abs(v)) { // если расстояние от окружности до точки в верт. направлении больше, чем до т. в диаг. напр.
                    alpha = (int) (Math.abs(v) / (Math.abs(d) + Math.abs(v)) * 255);
                    x2 = x;
                    // сдвиг в диагональном направлении
                    x++;
                    y--;
                    D = (D + b * b * (2 * x + 1) - 2 * y * a * a + a * a);
                } else {
                    alpha = (int) (Math.abs(d) / (Math.abs(d) + Math.abs(v)) * 255);
                    // сдвиг в верт. направлении
                    y--;
                    x2 = v > 0 ? x - 1 : x + 1;
                    D = D - 2 * y * a * a + a * a;
                }
                y2 = y;
            } else {  // если диаг. точка прямо на окружности
                x++;
                y--;
                alpha = 255;
                D = (D + b * b * (2 * x + 1) - 2 * y * a * a + a * a);
            }
        }
        pd.drawPixels(q1); // рисуем основные точки в правильном порядке, чтобы верно найти start и end точки
        pd.drawPixels(q2);
        pd.drawPixels(q3);
        pd.drawPixels(q4);
        if (from != 0 || to != 360) {
            ell.addContour(drawLine(pd, ell.getX0(), ell.getY0(), ell.getStartX(), ell.getStartY(), color));
            ell.addContour(drawLine(pd, ell.getX0(), ell.getY0(), ell.getEndX(), ell.getEndY(), color));
        }
        if (fill)
            ell.fill(pd, new DDALineDrawer());
    }

    public void drawOval(IPixelDrawer pl, int x0, int y0, int width, int height, Color color) {
        int a = width / 2;
        int b = height / 2;
        int rx = x0 + a;
        int lx = x0 - a;
        int ly = 0;
        int uy = 0;
        int x = a;
        while (a * a * (2 * uy - 1) < 2 * b * b * (x + 1)) {
            uy = (int) Math.ceil(b / (double) a * Math.sqrt(a * a - x * x + x - 0.25)); // порожек в (y;x-1/2)
            drawLine(pl, x, y0 + uy - 1, x, y0 + ly, color);
            drawLine(pl, x, y0 + uy - 1, x, y0 + ly, color);
            drawLine(pl, x, y0 - uy + 1, x, y0 - ly, color);
            drawLine(pl, x, y0 - uy + 1, x, y0 - ly, color);
            x--;
            ly = uy;
        }
        for (int y = uy; y <= b; y++) {
            double step = ((a / (double) b) * Math.sqrt(b * b - y * y + y - 0.25));
            int newRX = (int) Math.ceil(x0 + step);
            int newLX = (int) (x0 - step);

            drawLine(pl, rx, y0 + y - 1, newRX, y0 + y, color);
            drawLine(pl, lx, y0 + y - 1, newLX, y0 + y, color);
            drawLine(pl, lx, y0 - y + 1, newLX, y0 - y, color);
            drawLine(pl, rx, y0 - y + 1, newRX, y0 - y, color);

            rx = newRX;
            lx = newLX;
        }
    }

    public void drawPie(Graphics2D g, int x0, int y0, int a, int b, int from, int to, Color color) {
        int dx = 0;
        int dy = b;
        //Рассчитываем координаты точки (x+1; y-1/2)
        int delta = 4 * b * b * (dx + 1) * (dx + 1) + a * a * (2 * dy - 1) * (2 * dy - 1) - 4 * a * a * b * b;
        IPixelDrawer pl = new GraphicsPixelDrawer(g);
        float truX;
        float truY = b;
        Double curAlpha = 0.0; // угол в 1-й четверти для текущей точки
        Point first = new Point();
        initPoint(a, b, from, first);
        Point last = new Point();
        initPoint(a, b, to, last);
        //Первая часть дуги
        // При очень маленьком b ( => 0) цикл в "классическом" Брезенхеме останавливается слишком рано
        // и не дорисовываются горизонтальные точки
        // поэтому в условии должна быть реальная координата truY
        while (a * a * truY > b * b * dx) { // Y - сдвиг для доп. точки через OY (над основной)
            curAlpha = Math.toRadians(90) - Math.atan(a * dx / (b * Math.sqrt(a * a - dx * dx)));
            pl.putPixels(x0, y0, dx, truY, from, to, curAlpha, 1, color);

            if (delta < 0) {
                dx++;
                delta += 4 * b * b * (2 * dx + 3);
            } else {//переход по диагонали
                dx++;
                delta = delta - 8 * a * a * (dy - 1) + 4 * b * b * (2 * dx + 3);
                if (dy > 1) // чтобы не пропускать точку при 200;20
                    dy--;
            }
            truY = (float) (b / (double) a * Math.sqrt(a * a - dx * dx));
        }
        //Рассчитываем координаты точки (x+1/2; y-1)
        delta = b * b * (2 * dx + 1) * (2 * dx + 1) + 4 * a * a * (dy + 1) * (dy + 1) - 4 * a * a * b * b;
        dy = (int) Math.max(dy, Math.floor(truY)); // узнаём ординату ближайшей к последней отрисованной точки
        // рисуем утерянный при переходе пиксел
        pl.putPixels(x0, y0, dx, truY, from, to, curAlpha, 2, color);

        truX = (float) ((a / (double) b) * Math.sqrt(b * b - dy * dy));
        //Вторая часть дуги, если не выполянется условие первого цикла, значит Y изменяется быстрее
        while (dy + 1 != 0) { // X - сдвиг для доп. точки через OX
            curAlpha = Math.atan(b * dy / (a * Math.sqrt(b * b - dy * dy)));
            pl.putPixels(x0, y0, truX, dy, from, to, curAlpha, 3, color);

            if (delta < 0) {
                dy--;
                delta += 4 * a * a * (2 * dy + 3);
            } else {//переход по диагонали
                dy--;
                delta = delta - 8 * b * b * (dx + 1) + 4 * a * a * (2 * dy + 3);
                dx++;
            }
            truX = (float) ((a / (double) b) * Math.sqrt(b * b - dy * dy));
        }
        if (from != 0 && to != 360) {
            drawLine(pl, x0, y0, x0 + first.x, y0 + first.y, color);
            drawLine(pl, x0, y0, x0 + last.x, y0 + last.y, color);
        }
    }

    private void initPoint(int a, int b, int alpha, Point p) {
        double k = Math.tan(Math.toRadians(alpha));
        double x = Math.sqrt(b * b / (k * k + b * b / (double) a / a));
        int y = (int) Math.floor(Math.abs(k) * x);
        x = Math.ceil(x);
        switch (getQuarter(alpha)) {
            case 1: {
                p.y = y;
                p.x = (int) x;
                break;
            }
            case 2: {
                p.y = y;
                p.x = (int) -x;
                break;
            }
            case 3: {
                p.y = -y;
                p.x = (int) -x;
                break;
            }
            case 4: {
                p.y = -y;
                p.x = (int) x;
                break;
            }
        }
    }

    private int getQuarter(int angle) {
        if (angle >= 0 && angle <= 90)
            return 1;
        if (angle > 90 && angle <= 180)
            return 2;
        if (angle > 180 && angle <= 270)
            return 3;
        return 4;
    }

}
