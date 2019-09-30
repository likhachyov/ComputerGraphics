package drawers;

import Interfaces.PixelDrawer;

import java.awt.*;
import java.util.*;

public class WuDrawer {


    private ArrayList<Ellipse.Point> drawLine(PixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
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
            if (i == 0){
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



    public void drawPie(PixelDrawer pd, int x0, int y0, int a, int b, int from, int to, Color color, boolean fill) {
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
            ell.fill(pd,new DDALineDrawer());
    }
}
