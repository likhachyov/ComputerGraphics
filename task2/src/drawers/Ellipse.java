package drawers;

import Interfaces.LineDrawer;
import Interfaces.PixelDrawer;

import java.awt.*;
import java.util.*;

public class Ellipse {

    public static class Point implements Comparable {
        int x, y, alpha;
        Ellipse ell;

        public Point(Ellipse ell, int x, int y, int alpha) {
            this.x = x;
            this.y = y;
            this.alpha = alpha;
            this.ell = ell;
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Object o) {
            return Integer.compare(x, ((Point) o).x);
        }
    }

    private int x0, y0;
    private int aa, bb;
    private int from, to;
    private int startX, startY, endX, endY;
    private Color color;
    private ArrayList<Point> contour = new ArrayList<>();

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
        pastRes = from < to && from != 0;
    }
//      для каждого У берём две крайние точки и соединяем линией
    public void fill(PixelDrawer pd, LineDrawer ld) {
        HashMap<Integer, TreeSet<Point>> hm = map();
        hm.forEach((y, tr) -> {
//            System.out.println(y);
            int x1 = tr.first().x;
            int x2 = tr.last().x;
//            System.out.println("x1 " + x1);
//            System.out.println("x2 " + x2);
            ld.drawLine(pd, x1, y, x2, y, color);
        });

    }

    private HashMap<Integer, TreeSet<Point>> map() {
        HashMap<Integer, TreeSet<Point>> hm = new HashMap<>();
        contour.forEach(p -> {
            if (hm.containsKey(p.y)) {
                hm.get(p.y).add(p);
            } else {
                TreeSet<Point> deq = new TreeSet<>();
                hm.put(p.y, deq);
                deq.add(p);
            }
        });
        return hm;
    }

    void addContour(ArrayList<Point> line) {
        contour.addAll(line);
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

    // Если main = true, метод считает, что рисуются точки с бОльшей прозрачностью
    // count первый раз меняется всегда при начале проверке точек (alpha = 0), второй - когда начали рисовать пай
    public boolean isRange(int x, int y, boolean main) {
        int angle = getAngle(x, y);
        boolean res;
        if (from > to)
            res = !(angle >= to && angle <= from);
        else
            res = angle >= from && angle <= to;
        if (main && res != pastRes) { // не учитываем неосновные точки, когда считаем изменения
            count++;
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
        if (main) {
//            System.out.println("A " + angle);
//            System.out.println("x " + x);
//            System.out.println("y " + y);
            pastRes = res;
            if (res) // Основные точки заданного диапазона сохраянем
                contour.add(new Point(x0 + x, y0 + y));
        }
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
        return x0 + startX;
    }

    public int getStartY() {
        return y0 + startY;
    }

    public int getEndX() {
        return x0 + endX;
    }

    public int getEndY() {
        return y0 + endY;
    }

    public Color getColor() {
        return color;
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }
}
