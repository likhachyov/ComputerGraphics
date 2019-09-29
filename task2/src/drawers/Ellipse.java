package drawers;

import java.awt.*;

public class Ellipse {

    private int aa, bb;
    private int from, to;
    private int startX, startY, endX, endY;
    private Color color;

    public Color getColor() {
        return color;
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }

    private int x0, y0;

    public Ellipse(int x0, int y0, int a, int b, int from, int to, Color color) {
        if (from < 0 || to > 360)
            throw new IllegalArgumentException();
        this.from = from;
        this.to = to;
        this.x0 = x0;
        this.y0 = y0;
        this.color = color;
        aa = a * a;
        bb = b * b;
        pastRes = from < to;
//        calcPoints();
    }

    private void calcPoints() {
        double k = Math.tan(Math.toRadians(from));
        double x = Math.sqrt(bb / (k * k + bb / (double) aa));
        int y = (int) Math.ceil(Math.abs(k) * x);
        x = Math.ceil(x);
        switch (getQuarter(from)) {
            case 1: {
                startY = y;
                startX = (int) x;
                break;
            }
            case 2: {
                startY = y;
                startX = (int) -x;
                break;
            }
            case 3: {
                startY = -y;
                startX = (int) -x;
                break;
            }
            case 4: {
                startY = -y;
                startX = (int) x;
                break;
            }
        }
    }

    /**
     * Если результат > 0, значит точка вне эллипса, (работает почему-то наоборот)
     * если < 0, значит точка внутри эллипса (для 1-й четверти)
     *
     * @return квадрат расстояния до данной точки от точки на эллипсе в той же ординате
     */
    public double distanceToByY(int x, int y) {
        double exx = aa * (bb - y * y) / (double) bb;
        return x * x - exx;
    }

    /**
     * Если результат > 0, значит точка вне эллипса,
     * если < 0, значит точка внутри эллипса (для 1-й четверти)
     *
     * @return квадрат расстояния до данной точки от точки на эллипсе в той же абциссе
     */
    public double distanceToByX(int x, int y) {
        double eyy = bb * (aa - x * x) / (double) aa;
        return y * y - eyy;
    }

    public double shortestDistance(int x, int y) {
        return Math.min(distanceToByX(x, y), distanceToByY(x, y));
    }

    private int count = 0; // сколько раз начинали/переставали рисовать точки.
    private boolean pastRes;

    public boolean isRange(int x, int y, boolean main) {
        int angle = getAngle(x, y);
        boolean res;
        if (from > to)
            res = !(angle >= to && angle <= from);
        else
            res = angle >= from && angle <= to;
        if (main && res != pastRes) { // не учитываем неосновные точки, когда считаем изменения
            count++;
            System.out.println("A " + angle);
            System.out.println("x " + x);
            System.out.println("y " + y);
        }
        if (count == 2) {
            if (!pastRes) {
                startX = x;
                startY = y;
            } else {
                endX = x;
                endY = y;
            }
        }
        if (main)
            pastRes = res;
        return res;
    }

    private int getAngle(int x, int y) {
        if (x > 0 && y < 0) // 4 q.
            return (int) Math.toDegrees(Math.atan(y / (double) x)) + 360;
        if (x < 0 && y > 0 || x < 0 && y < 0) // 2 and 3 q.
            return (int) Math.toDegrees(Math.atan(y / (double) x)) + 180;
        if (x > 0 && y > 0) // 1 quarter
            return (int) Math.toDegrees(Math.atan(y / (double) x));
        // special cases
        if (y == 0 && x > 0)
            return 0;
        if (x == 0 && y > 0)
            return 90;
        if (y == 0 && x < 0)
            return 180;
        return 270;
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

    // Не вызывать до конца отрисовки пая!
    public int getStartX() {
        System.out.println("sx " + startX);
        return x0 + startX;
    }

    public int getStartY() {
        System.out.println("sy " + startY);
        return y0 + startY;
    }

    public int getEndX() {
        System.out.println("ex " + endX);
        return x0 + endX;
    }

    public int getEndY() {
        System.out.println("ey " + endY);
        return y0 + endY;
    }
}
