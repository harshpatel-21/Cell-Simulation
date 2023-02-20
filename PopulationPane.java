import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class PopulationPane extends JPanel{
    private JPanel popPanel = new JPanel(new GridBagLayout());

    String bacteriaName;
    int bacteriaNum;
    JLabel bacLabel;
    Color bacteriaColor;
    JPanel thisPanel;

    public PopulationPane(JLabel bacLabel, Color bacteriaColor){
        this.bacLabel = bacLabel;
        this.bacteriaColor = bacteriaColor;
        generatePane();
    }

    private class Rectangle extends JPanel{
        private int width,height;
        Color color;

        public Rectangle(int width, int height, Color color){
            this.width = width;
            this.height = height;
            this.color = color;
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(this.color);
            g.fillRect(0, 0, this.width, this.height);
        }

    }

    public void generatePane(){
        thisPanel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        this.add(this.bacLabel, c);
        c.gridx=1;
        c.gridy=0;
        this.add(new Rectangle(10,10,Color.RED));
    }

    public JPanel getPanel(){
        return thisPanel;
    }

    public void setInfo(JLabel bL, Color color){
        this.bacteriaColor = color;
        bacLabel.setText(bL.getText());
        generatePane();
    }

}
