package drawers;

import Interfaces.IPixelDrawer;

import java.awt.*;

public class SvetaPie {

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


    private void initPoint(int a, int b, int alpha, Point p) {
        double k = Math.tan(Math.toRadians(alpha));
        double x = Math.sqrt(b * b / (k * k + b * b / (double) a / (double) a));
        int y = (int) Math.floor(Math.abs(k) * x);
        x = Math.floor(x);
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

    public void drawPie(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color, boolean fill) {
        int a = width / 2;
        int b = height / 2;
        int dx = 0;
        int dy = b;
        //Рассчитываем координаты точки (x+1; y-1/2)
        int d = 4 * b * b * (dx + 1) * (dx + 1) + a * a * (2 * dy - 1) * (2 * dy - 1) - 4 * a * a * b * b;
        float stepX;
        float stepY = b;
        Double currentAlpha = 1 / 6.0;
        Point first = new Point();
        initPoint(a, b, from, first);
        Point last = new Point();
        initPoint(a, b, to, last);
        while (a * a * stepY > b * b * dx) { // Y - сдвиг для доп. точки через OY (над основной)
            currentAlpha = Math.toRadians(90) - Math.atan(a * dx / (b * Math.sqrt(a * a - dx * dx)));
            if (fill)
                fillRow(pl, x0, y0, dx, (int) stepY, from, to, currentAlpha, 1, color);
            if (check(from, to, Math.toDegrees(currentAlpha), 1)) {
                //I квадрант, Y
                pl.drawPixel(x0 + dx, y0 + (int) (stepY), 255 - (int) (floatPart(stepY) * 255), color);
                pl.drawPixel(x0 + dx, y0 + (int) (stepY) + 1, (int) (floatPart(stepY) * 255), color);
            }
            if (check(from, to, Math.toDegrees(currentAlpha), 2)) {
                // II квадрант, Y
                pl.drawPixel(x0 - dx, y0 + (int) (stepY), 255 - (int) (floatPart(stepY) * 255), color);
                pl.drawPixel(x0 - dx, y0 + (int) (stepY) + 1, (int) (floatPart(stepY) * 255), color);
            }
            if (check(from, to, Math.toDegrees(currentAlpha), 3)) {
                //III квадрант, Y
                pl.drawPixel(x0 - dx, y0 - (int) (stepY), 255 - (int) (floatPart(stepY) * 255), color);
                pl.drawPixel(x0 - dx, y0 - (int) (stepY) - 1, (int) (floatPart(stepY) * 255), color);
            }
            if (check(from, to, Math.toDegrees(currentAlpha), 4)) {
                //IV квадрант, Y
                pl.drawPixel(x0 + dx, y0 - (int) (stepY), 255 - (int) (floatPart(stepY) * 255), color);
                pl.drawPixel(x0 + dx, y0 - (int) (stepY) - 1, (int) (floatPart(stepY) * 255), color);
            }
            if (d < 0) {
                dx++;
                d += 4 * b * b * (2 * dx + 3);
            } else {//переход по диагонали
                dx++;
                d = d - 8 * a * a * (dy - 1) + 4 * b * b * (2 * dx + 3);
                if (dy > 1) // чтобы не пропускать точку при 200;20
                    dy--;
            }
            stepY = (float) (b / (double) a * Math.sqrt(a * a - dx * dx));
        }
        //Рассчитываем координаты точки (x+1/2; y-1)
        d = b * b * (2 * dx + 1) * (2 * dx + 1) + 4 * a * a * (dy + 1) * (dy + 1) - 4 * a * a * b * b;
        dy = (int) Math.max(dy, Math.floor(stepY)); // узнаём ординату ближайшей к последней отрисованной точки
        if (check(from, to, Math.toDegrees(currentAlpha), 1))
            pl.drawPixel(x0 + dx, (int) (y0 + Math.floor(stepY + 1)), (int) (floatPart(stepY) * 255), color);
        if (check(from, to, Math.toDegrees(currentAlpha), 2))
            pl.drawPixel(x0 - dx, (int) (y0 + Math.floor(stepY + 1)), (int) (floatPart(stepY) * 255), color);
        if (check(from, to, Math.toDegrees(currentAlpha), 3))
            pl.drawPixel(x0 - dx, (int) (y0 - Math.floor(stepY + 1)), (int) (floatPart(stepY) * 255), color);
        if (check(from, to, Math.toDegrees(currentAlpha), 4))
            pl.drawPixel(x0 + dx, (int) (y0 - Math.floor(stepY + 1)), (int) (floatPart(stepY) * 255), color);
        stepX = (float) ((a / (double) b) * Math.sqrt(b * b - dy * dy));

        //Вторая часть дуги, если не выполянется условие первого цикла, значит Y изменяется быстрее
        while (dy + 1 != 0) { // X - сдвиг для доп. точки через OX
            currentAlpha = Math.atan(b * dy / (a * Math.sqrt(b * b - dy * dy)));
            if (fill)
                fillRow(pl, x0, y0, (int) (stepX), dy, from, to, currentAlpha, 1, color);
            if (check(from, to, Math.toDegrees(currentAlpha), 1)) {
                //I квадрант, X
                pl.drawPixel(x0 + (int) (stepX), y0 + dy, 255 - (int) (floatPart(stepX) * 255), color);
                pl.drawPixel(x0 + (int) (stepX) + 1, y0 + dy, (int) (floatPart(stepX) * 255), color);
            }
            if (check(from, to, Math.toDegrees(currentAlpha), 2)) {
                //II квадрант, X
                pl.drawPixel(x0 - (int) (stepX) - 1, y0 + dy, (int) (floatPart(stepX) * 255), color);
                pl.drawPixel(x0 - (int) (stepX), y0 + dy, 255 - (int) (floatPart(stepX) * 255), color);
            }
            if (check(from, to, Math.toDegrees(currentAlpha), 3)) {
                //III квадрант, X
                pl.drawPixel(x0 - (int) (stepX), y0 - dy, 255 - (int) (floatPart(stepX) * 255), color);
                pl.drawPixel(x0 - (int) (stepX) - 1, y0 - dy, (int) (floatPart(stepX) * 255), color);
            }
            if (check(from, to, Math.toDegrees(currentAlpha), 4)) {
                //IV квадрант, X

                pl.drawPixel(x0 + (int) (stepX), y0 - dy, 255 - (int) (floatPart(stepX) * 255), color);
                pl.drawPixel(x0 + (int) (stepX) + 1, y0 - dy, (int) (floatPart(stepX) * 255), color);
            }
            if (d < 0) {
                dy--;
                d += 4 * a * a * (2 * dy + 3);
            } else {//переход по диагонали
                dy--;
                d = d - 8 * b * b * (dx + 1) + 4 * a * a * (2 * dy + 3);
                dx++;
            }
            stepX = (float) ((a / (double) b) * Math.sqrt(b * b - dy * dy));
        }
        if (from != 0 || to != 360) {
            drawLine(pl, x0, y0, x0 + first.x, y0 + first.y, color);
            drawLine(pl, x0, y0, x0 + last.x, y0 + last.y, color);
        }
    }

    // Находим Х на контуре пая через тангенс
    private int getX(int y, int alpha) {
        return (int) ((double) y / Math.tan(Math.toRadians(alpha)));
    }

    private void fillRow(IPixelDrawer pl, int x0, int y0, int x, int y, int from, int to, double alpha, int q, Color color) {
        alpha = Math.toDegrees(alpha);
        int x1 = 0, x2 = 0; // координаты линии
        // 4 четверти
        switch (q) {
            case 1: {
                if (from < to) {
                    if (check(from, to, alpha, 1)) {
                        x1 = to >= 90 ? 0 : getX(y, to);
                        // от from  до контура
                        drawLine(pl, x0 + x1, y0 + y, x0 + x, y0 + y, color);
                        // если линия не доходит до контура
                    } else if (from < 90 && alpha < from) {
                        x1 = to >= 90 ? 0 : getX(y, to);
                        x2 = getX(y, from);
                        // от OY/to до from
                        drawLine(pl, x0 + x1, y0 + y, x0 + x2, y0 + y, color);
                    }
                } // to < from
                else {
                    if (check(from, to, alpha, 1)) { // если точка на контуре пая
                        if (alpha < to) {
                            x1 = to < 90 ? getX(y, to) : 0;
                            // от контура до линии to
                            drawLine(pl, x0 + x1, y0 + y, x0 + x, y0 + y, color);
                            if (from < 90) {
                                x2 = getX(y, from);
                                // от ОY до from
                                drawLine(pl, x0, y0 + y, x0 + x2, y0 + y, color);
                            }
                        } else // когда угол больше to: от OY до контура
                            drawLine(pl, x0, y0 + y, x0 + x, y0 + y, color);
                        // если точка не входит в контар
                    } else if (from < 90) { // рисуем от OY до линии from
                        x2 = getX(y, from);
                        drawLine(pl, x0, y0 + y, x0 + x2, y0 + y, color);
                    }
                }
            }
            case 2: {
                if (from < to) {
                    if (check(from, to, alpha, 2)) { // точка лежит на контуре
                        x1 = from > 90 && from < 180 ? getX(y, 180 - from) : 0; // если линия from в этой четверти, рисуем к ней, иначе к OY
                        drawLine(pl, x0 - x1, y0 + y, x0 - x, y0 + y, color);
                    } else if (to < 180 && alpha < 180 - from) { //линия не доходит до контура
                        x1 = getX(y, 180 - to); // point on на прямой to
                        x2 = from > 90 ? getX(y, 180 - from) : 0; // точка на прямой from
                        drawLine(pl, x0 - x1, y0 + y, x0 - x2, y0 + y, color);
                    }
                } else if (from > to) {
                    if (check(from, to, alpha, 2)) {
                        if (to >= 180 || to < 90 && from < 90) { // вся четверть входит
                            drawLine(pl, x0, y0 + y, x0 - x, y0 + y, color);
                        } else if (from < 180 && from > 90 && alpha < 180 - from) {
                            x2 = getX(y, 180 - from); // рисуем от from до OY
                            drawLine(pl, x0 - x2, y0 + y, x0 - x, y0 + y, color);
                            if (to > 90) { // рисуем от to до контура
                                x2 = getX(y, 180 - to);
                                drawLine(pl, x0, y0 + y, x0 - x2, y0 + y, color);
                            }
                        } else if (to > 90) { // от OY до контура
                            drawLine(pl, x0, y0 + y, x0 - x, y0 + y, color);
                        }
                    }
                }
            }
            case 3: {
                if (from < to) {
                    if (check(from, to, alpha, 3)) {
                        x1 = to >= 270 ? 0 : getX(y, to - 180);
                        // от  to  до контура
                        drawLine(pl, x0 - x1, y0 - y, x0 - x, y0 - y, color);
//                 если линия не доходит до контура (упирается в from)
                    } else if (from < 270 && alpha < from - 180) {
                        x1 = to >= 270 ? 0 : getX(y, to - 180);
                        x2 = getX(y, from - 180);
                        // от OY/to до from
                        drawLine(pl, x0 - x1, y0 - y, x0 - x2, y0 - y, color);
                    }
                }
                else {
                    if (check(from, to, alpha, 3)) {
                        if (alpha < to-180) {
                            x1 = to < 270 ? getX(y, to-180) : 0;
                            // от контура до линии to
                            drawLine(pl,x0 - x1, y0 - y, x0 - x, y0 - y, color);
                            if (from < 270) {
                                x2 = getX(y, from-180);
                                // от ОY до from
                                drawLine(pl, x0, y0 - y, x0 - x2, y0 - y, color);
                            }
                        } else // когда угол больше to: от OY до контура
                            drawLine(pl, x0, y0 - y, x0 - x, y0 - y, color);
                        // если точка не входит в контар
                    } else if (from < 270) { // рисуем от OY до линии from
                        x2 = getX(y, from-180);
                        drawLine(pl, x0, y0 - y, x0 - x2, y0 - y, color);
                    }
                }
            }
            case 4: {
                if (from < to) {
                    if (check(from, to, alpha, 4)) {
                        x1 = from > 270 ? getX(y, 360 - from) : 0; // если линия from в этой четверти, рисуем к ней, иначе к OY
                        drawLine(pl, x0 + x1, y0 - y, x0 + x, y0 - y, color);
                    } else if (to < 360 && to > 270 && alpha < 360 - from) { //линия не доходит до контура
                        x1 = getX(y, 360 - to); // point on на прямой to
                        x2 = from > 270 ? getX(y, 360 - from) : 0; // точка на прямой from
                        drawLine(pl, x0 + x1, y0 - y, x0 + x2, y0 - y, color);
                    }
                }
                else {
                    if (check(from, to, alpha, 4)) {
                        if (alpha < 360-from) {
                            x1 = from > 270 && from < 360 ? getX(y, 360-from) : 0;
                            // от контура до линии from
                            drawLine(pl, x0 + x1, y0 - y, x0 + x, y0 - y, color);
                            if (to > 270) {
                                x2 = getX(y, 360-to);
                                // от ОY до to
                                drawLine(pl, x0, y0 - y, (int) (x0 + x2), y0 - y, color);
                            }
                        }
                        // если точка не входит в контар
                    } else if (to > 270) { // рисуем от OY до линии to
                        x2 = getX(y, 360-to);
                        drawLine(pl, x0, y0 - y, x0 + x2, y0 - y, color);
                    }
                }
            }
        }
    }

    //	проверяем олежит ли точка с углом alpha в четверти q в указанном диапазоне
    private boolean check(int from, int to, double alpha, int q) {
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

    //дробная часть числа
    private static double floatPart(double x) {
        while (x >= 0)
            x--;
        x++;
        return x;
    }
}
