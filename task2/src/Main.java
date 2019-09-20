import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Task 1");
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DrawPanel p = initPicture(500, 500);

        frame.add(p);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private static DrawPanel initPicture(int width, int height) {
        DrawPanel p = new DrawPanel();
        p.setPreferredSize(new Dimension(width, height));
        return p;
    }
}
