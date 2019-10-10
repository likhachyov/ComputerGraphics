package drawers;

import Interfaces.LineDrawer;
import Interfaces.OvalDrawer;
import Interfaces.IPixelDrawer;

import java.awt.*;

public class BresenhamDrawer implements LineDrawer, OvalDrawer {

    public void quadrant(IPixelDrawer pd, int x0, int y0, int r, Color color) {
        // координаты данной фигуры при ее построении с центром в начале координат
        int x = 0;
        int y = r;  // Y - монотонно убывающая функций при возрастании х, т.е. не можем при изменении х его уменшить
        int D = 2 * (1 - r); // разность квадратов расстояний от центра и окружности до пиксела в диагональном направлении
        // Иницилизируем для диаг. т. (1; r-1)
        int d1;  // разность квадратов расстояний от окружности до пикселов в горизонтальном и диагональном направлениях
        int d2;  // то же, но пикселов в диагональном и вертикальном направлении
        while (y >= 0) {
            pd.drawPixel(x0 + x, y0 + y, color);
            if (D < 0) { // Диагональная точка внутри круга
                d1 = 2 * (D + y) - 1;
                if (d1 > 0) { // если диагональная т. ближе
                    x++;
                    y--;
                    D = D - 2 * y + 2 * x + 2; // Рекуррентно перерасчитываем расстояние от окр. до диаг. точки
                } else {
                    // сдвиг в горизонтальном напр.
                    x++;
                    D = D + 2 * x + 1;
                }
            } else if (D > 0) {
                d2 = 2 * (D - x) - 1;
                if (d2 <= 0) { // если расстояние от окружности до точки в верт. направлении больше, чем до т. в диаг. напр.
                    // сдвиг в диагональном направлении
                    x++;
                    y--;
                    D = D - 2 * y + 2 * x + 2;
                } else {
                    // сдвиг в верт. направлении
                    y--;
                    D = D - 2 * y + 1;
                }
            } else {  // если диаг. точка прямо на окружности
                x++;
                y--;
                D = D - 2 * y + 2 * x + 2;
            }
        }
    }

    public void drawEllipse(int x, int y, int a, int b, Graphics g) {
        int dx = 0; //Компонента x
        int dy = b; //Компонента y
        //Рассчитываем координаты точки (x+1; y-1/2)
        int delta = 4 * b * b * (dx + 1) * (dx + 1) + a * a * (2 * dy - 1) * (2 * dy - 1) - 4 * a * a * b * b;
        //Первая часть дуги
        while (a * a * (2 * dy - 1) > 2 * b * b * (dx + 1)) {
            fill(x, y, dx, dy, g);
            //переход по диагонали
            if (delta < 0) {
                dx++;
                delta += 4 * b * b * (2 * dx + 3);
            } else {
                dx++;
                delta = delta - 8 * a * a * (dy - 1) + 4 * b * b * (2 * dx + 3);
                dy--;
            }
        }
        //Рассчитываем координаты точки (x+1/2; y-1)
        delta = b * b * (2 * dx + 1) * (2 * dx + 1) + 4 * a * a * (dy + 1) * (dy + 1) - 4 * a * a * b * b;

        //Вторая часть дуги, если не выполянется условие первого цикла, значит a*a*(2*y - 1) <= 2*b*b*(x + 1)
        while (dy + 1 != 0) {
            fill(x, y, dx, dy, g);
//            переход по диагонали
            if (delta < 0) {
                dy--;
                delta += 4 * a * a * (2 * dy + 3);
            } else {
                dy--;
                delta = delta - 8 * b * b * (dx + 1) + 4 * a * a * (2 * dy + 3);
                dx++;
            }
        }
    }

    private void fill(int x, int y, int dx, int dy, Graphics g) {
        //ставим точки в первом квадранте и симметрично в остальных
        IPixelDrawer pd = new GraphicsPixelDrawer((Graphics2D) g);
        pd.drawPixel(x + dx, y + dy, Color.RED);
        pd.drawPixel(x - dx, y + dy, Color.RED);
        pd.drawPixel(x + dx, y - dy, Color.RED);
        pd.drawPixel(x - dx, y - dy, Color.RED);
//        g.fillRect(x, y - dy, dx, dy);
//        g.fillRect(x, y, dx, dy);
//        g.fillRect(x - dx, y, dx, dy);
//        g.fillRect(x - dx, y - dy, dx, dy);
    }

    @Override
    public void drawLine(IPixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
        int x = x1;
        int y = y1;
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double tangent = dy / dx;
        double error = 0;   // The offset of the real Y coordinate relative to the rendered
        double directionY = Math.signum(y2 - y1);
        double directionX = Math.signum(x2 - x1);
        for (int i = 0; i <= (dx == 0 ? dy : dx); i++) {
            x += directionX;
            error += tangent;
            if (error >= 0.5) {
                y += directionY;
                error -= 1;
            }
            pd.drawPixel(x, y, c);
        }
    }

    // Закрашиваемый пай включает в себя построение незакрашиваемого.
    public void fillPie(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
        if (from > to || from < 0 || to > 360)
            throw new IllegalArgumentException();
        if (from <= 90)
            fillPieMod(pl, x0, y0, width, height, from, Math.min(90, to), color);
        if (from <= 180 && to > 90) {
            int trueTo = from > 90 ? 180 - from : 90;
            fillPieMod2(pl, x0, y0, width, height, to >= 180 ? 0 : trueTo - to % 90, trueTo, color);
        }
        if (from <= 270 && to > 180)
            fillPieMod3(pl, x0, y0, width, height, from > 180 ? from - 180 : 0, to < 270 ? to - 180 : 90, color);
        if (to > 270)
            fillPieMod4(pl, x0, y0, width, height, to < 360 ? 90 - to % 90 : 0, from <= 270 ? 90 : 90 - from % 90, color);
    }

    /**
     * Для первой четверти
     * Метод последовательно смещается по OY и рисует горизонтальные отрезки, пока угол - trueAlpha между OX и
     * линией, проведённой к концу очередного отрезка не равен значению угла 'to'.
     */
    private void fillPieMod(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
        int a = width / 2;
        int b = height / 2;
        Double alpha = Math.toRadians(from);  // переводим градусы в радианы
        Double betta = Math.toRadians(to);  // переводим градусы в радианы
        int stepToSegmentStartX = 0;
        int stepToSegmentEndX = 0;
        int stepByY = 0;
        Double trueAlpha = 0.0;
        while (Math.toDegrees(trueAlpha) < to) {
            drawLine(pl, x0 + stepToSegmentStartX, y0 + stepByY, x0 + stepToSegmentEndX, y0 + stepByY, color);
            stepByY++;
            stepToSegmentStartX = (int) (stepByY / Math.tan(betta));
            // после каждого сдвига по Y, перерасчитываем угол линии, до которой доходит отрезок
            trueAlpha = Math.atan((stepByY * b) / (a * Math.sqrt(b * b - stepByY * stepByY)));
// пока угол линии из центра к точке на контуре эллипса меньше заданного from, конец отрезка рассчитываем до линии проведённой под углом from из центра
            stepToSegmentEndX = (int) (trueAlpha <= alpha ? stepByY / Math.tan(alpha) : stepByY / Math.tan(trueAlpha));
        }
        if (to == 90)
            drawLine(pl, x0, y0, x0, y0 + b - 2, color);
    }

    /**
     * Для второй четверти. Отзеркаливает рисунок с данными значениями в 1-й четверти
     */
    private void fillPieMod2(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
        int a = width / 2;
        int b = height / 2;
        Double alpha = Math.toRadians(from);  // переводим градусы в радианы
        Double betta = Math.toRadians(to);  // переводим градусы в радианы
        int stepToSegmentStartX = 0;
        int stepToSegmentEndX = 0;
        int stepByY = 0;
        Double trueAlpha = 0.0;
        while (Math.toDegrees(trueAlpha) <= to) {
            drawLine(pl, x0 - stepToSegmentStartX, y0 + stepByY, x0 - stepToSegmentEndX, y0 + stepByY, color);
            stepByY++;
            stepToSegmentStartX = (int) (stepByY / Math.tan(betta));
            trueAlpha = Math.atan((stepByY * b) / (a * Math.sqrt(b * b - stepByY * stepByY)));
            stepToSegmentEndX = (int) (trueAlpha <= alpha ? stepByY / Math.tan(alpha) : stepByY / Math.tan(trueAlpha));
        }
        if (from == 0)
            drawLine(pl, x0 - a, y0, x0, y0, color);
    }

    /**
     * Для третьей четверти. Отзеркаливает рисунок с данными значениями во 2-й четверти
     */
    private void fillPieMod3(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
        int a = width / 2;
        int b = height / 2;
        Double alpha = Math.toRadians(from);  // переводим градусы в радианы
        Double betta = Math.toRadians(to);  // переводим градусы в радианы
        int stepToSegmentStartX = 0;
        int stepToSegmentEndX = 0;
        int stepByY = 0;
        Double trueAlpha = 0.0;
        while (Math.toDegrees(trueAlpha) < to) {
            drawLine(pl, x0 - stepToSegmentStartX, y0 - stepByY, x0 - stepToSegmentEndX, y0 - stepByY, color);
            stepByY++;
            stepToSegmentStartX = (int) (stepByY / Math.tan(betta));
            trueAlpha = Math.atan((stepByY * b) / (a * Math.sqrt(b * b - stepByY * stepByY)));
            stepToSegmentEndX = (int) (trueAlpha <= alpha ? stepByY / Math.tan(alpha) : stepByY / Math.tan(trueAlpha));
        }
        if (to == 90)
            drawLine(pl, x0, y0 - b, x0, y0, color);
    }

    /**
     * Для 4-й четверти. Отзеркаливает рисунок с данными значениями в 3-й четверти
     */
    private void fillPieMod4(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
        int a = width / 2;
        int b = height / 2;
        Double alpha = Math.toRadians(from);  // переводим градусы в радианы
        Double betta = Math.toRadians(to);  // переводим градусы в радианы
        int stepToSegmentStartX = 0;
        int stepToSegmentEndX = 0;
        int stepByY = 0;
        Double trueAlpha = 0.0;
        while (Math.toDegrees(trueAlpha) < to) {
            drawLine(pl, x0 + stepToSegmentStartX, y0 - stepByY, x0 + stepToSegmentEndX, y0 - stepByY, color);
            stepByY++;
            stepToSegmentStartX = (int) (stepByY / Math.tan(betta));
            trueAlpha = Math.atan((stepByY * b) / (a * Math.sqrt(b * b - stepByY * stepByY)));
            stepToSegmentEndX = (int) (trueAlpha <= alpha ? stepByY / Math.tan(alpha) : stepByY / Math.tan(trueAlpha));
        }
        if (from == 0)
            drawLine(pl, x0, y0, x0 + a, y0, color);
    }

    /**
     * Рисуем от ОХ
     */
    @Override
    public void drawOval(IPixelDrawer pd, int x0, int y0, int a, int b, Color c) {
        int y1 = 0;
        int y2 = 0;
        int x = a;
        //Первая часть дуги
        while (a * a * (2 * y1 - 1) < 2 * b * b * (x + 1)) {

            y2 = (int) Math.ceil(b / (double) a * Math.sqrt(a * a - x * x + x - 0.25)); // порожек в (y;x-1/2)
            drawLines(pd, x0, y0, x, y1, x, y2, c);
            x--;
            y1 = y2;
        }
        int y = y2;
        int lx = 0;
        int rx = x;
        while (y < b) {
            y++;
            lx = (int) Math.ceil(a / (double) b * Math.sqrt(b * b - y * y - y - 0.25)); // порожек в (y+1/2;x)
            drawLines(pd, x0, y0, rx, y, lx, y, c);
            rx = lx - 1;
        }
    }

    private void drawLines(IPixelDrawer pd, int x0, int y0, int x1, int y1, int x2, int y2, Color c) {
        drawLine(pd, x0 + x1, y0 - y1, x0 + x2, y0 - y2, c);
        drawLine(pd, x0 - x1, y0 - y1, x0 - x2, y0 - y2, c);
        drawLine(pd, x0 + x1, y0 + y1, x0 + x2, y0 + y2, c);
        drawLine(pd, x0 - x1, y0 + y1, x0 - x2, y0 + y2, c);
    }

    private void drawCircle(IPixelDrawer pd, int x, int y, int horizontalR, int verticalR, Color c) {
        int xOffset = 0;  // The offset of coords relative to the center
        int yOffset = verticalR;
        while (xOffset <= yOffset) {
            pd.drawPixel(x - xOffset, y - yOffset, c);
            pd.drawPixel(x - xOffset, y + yOffset, c);
            pd.drawPixel(x + xOffset, y - yOffset, c);
            pd.drawPixel(x + xOffset, y + yOffset, c);
            pd.drawPixel(x - yOffset, y - xOffset, c);
            pd.drawPixel(x - yOffset, y + xOffset, c);
            pd.drawPixel(x + yOffset, y - xOffset, c);
            pd.drawPixel(x + yOffset, y + xOffset, c);
            xOffset++;
            if (!isCircle(xOffset, yOffset - 0.5, horizontalR, verticalR))
                yOffset--;
        }
    }

    private boolean isCircle(double x, double y, int a, int b) {
        return (x * x / a / a) + (y * y / b / b) <= 1;
    }
}
