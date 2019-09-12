import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Picture extends JPanel {

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    private Color backgroundColor;

    private ArrayList<PicObject> objs;

    public Picture() {
        objs = new ArrayList<>();
    }

    public Picture(ArrayList<PicObject> objs) {
        this.objs = objs;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        paintBackground(g2);

        for (PicObject obj : objs)
            obj.draw(g2);

    }

    private void paintBackground(Graphics2D g2) {
        if (backgroundColor != null){
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth() - 1, getHeight() - getHeight()/4);
        }
        g2.setColor(new Color(240, 219, 125));
        g2.fillRect(0, getHeight() - getHeight()/4, getWidth() - 1, getHeight()/4);

    }

    public void addObj(PicObject obj) {
        objs.add(obj);
    }

    public void delObj(PicObject obj) {
        objs.remove(obj);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
