package drawers;

import Interfaces.LineDrawer;
import Interfaces.OvalDrawer;
import Interfaces.PixelDrawer;

import java.awt.*;

public class BresenhamDrawer implements LineDrawer, OvalDrawer {

    @Override
    public void drawLine(PixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
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
    public void fillPie(PixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
        if (from > to || from < 0 || to > 360)
            throw new IllegalArgumentException();
        if (from <= 90)
            fillPieMod(pl, x0, y0, width, height, from, Math.min(90, to), color);
        if (from <= 180 && to > 90){
            int trueTo =from > 90 ? 180 - from : 90;
            fillPieMod2(pl, x0, y0, width, height, to >= 180 ? 0 : trueTo-to%90, trueTo, color);
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
    private void fillPieMod(PixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
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
    private void fillPieMod2(PixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
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
    private void fillPieMod3(PixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
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
    private void fillPieMod4(PixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color) {
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

    @Override
    public void drawOval(PixelDrawer pd, int x0, int y0, int a, int b, Color c) {
        int y = 0;
        int x = 0;
        int prevX = a;

        while (y < b) {
            y++;
            x = (int) Math.ceil(a / (double) b * Math.sqrt(b * b - y * y));
            drawLine(pd, x0 + prevX, y0 - y, x0 + x, y0 - y, c);
            drawLine(pd, x0 - prevX, y0 - y, x0 - x, y0 - y, c);
            drawLine(pd, x0 + prevX, y0 + y, x0 + x, y0 + y, c);
            drawLine(pd, x0 - prevX, y0 + y, x0 - x, y0 + y, c);
            prevX = x;
        }
    }


    private void drawCircle(PixelDrawer pd, int x, int y, int horizontalR, int verticalR, Color c) {
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
