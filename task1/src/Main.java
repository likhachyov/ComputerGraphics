import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Task 1");
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Picture p = initPicture(960, 720);

        frame.add(p);
        frame.pack();
        frame.setLocationRelativeTo(null);

    }

    private static Picture initPicture(int width, int height) {
        Picture p = new Picture();
        p.setPreferredSize(new Dimension(width, height));
        p.setBackgroundColor(Color.black);
        p.addObj(new Sun(200, 200, 50, 20, 25, Color.orange, Color.orange));
        p.addObj(new Jeep(350, 430, 500, 240, Color.green));
        return p;
    }
}
