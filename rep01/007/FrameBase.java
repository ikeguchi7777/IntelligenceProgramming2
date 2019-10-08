import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class FrameBase extends JFrame{
	public FrameBase(String title,int x,int y,int width,int height) {
		setTitle(title);
	    setBounds(x, y, width, height);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public FrameBase(String title,int width,int height) {
		setTitle(title);
	    setSize(width, height);
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String args[]){
	    JFrame frame = new JFrame("タイトル");
	    frame.setBounds(100, 100, 400, 300);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    JPanel p = new JPanel();
	    JButton btn1 = new JButton("Save");
	    JButton btn2 = new JButton("Cancel");
	    JButton btn3 = new JButton("Help");

	    p.add(btn1);
	    p.add(btn2);
	    p.add(btn3);
	    
	    p.setLayout(new GridLayout(5,1));
	    
	    frame.getContentPane().add(p);
	    frame.setVisible(true);
	  }
}