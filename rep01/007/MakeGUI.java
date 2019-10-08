import static java.awt.RenderingHints.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MakeGUI {

	public static void MakeChooseSearchGUI(ActionListener listener, String[] searchNames) {
		FrameBase frame = new FrameBase("test", 500, 500);
		JPanel panel = new JPanel();
		for (int i = 0; i < searchNames.length; i++) {
			JButton but = new JButton(searchNames[i]);
			but.addActionListener(listener);
			but.setActionCommand(searchNames[i]);
			panel.add(but);
		}
		panel.setLayout(new GridLayout(searchNames.length, 1));
		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}

	public static void MakeSearchGUI(ActionListener listener) {
		FrameBase frame = new FrameBase("test", 1000, 500);
		DrawArrow arrow = new DrawArrow(new Point(15, 15), new Point(15, 175));
		GraphPanel gPanel = new GraphPanel(1000, 1000);
		gPanel.addShape(arrow);

		JButton bclear = new JButton("clear");
		bclear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gPanel.clear();
			}
		});
		JButton bnext = new JButton("next");
		bnext.addActionListener(listener);
		gPanel.add(bnext, BorderLayout.NORTH);
		frame.getContentPane().add(gPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	static class GraphPanel extends JPanel {
		ArrayList<Shape> shapes = new ArrayList<>();
		RenderingHints rh;

		GraphPanel(int width, int height) {
			setPreferredSize(new Dimension(width, height));
			setBackground(Color.white);
			rh = new RenderingHints(null);
			rh.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
			rh.put(KEY_STROKE_CONTROL, VALUE_STROKE_PURE);
		}

		public void addShape(Shape shape) {
			shapes.add(shape);
		}

		public void clear() {
			shapes.clear();
			this.repaint();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHints(rh);
			g2.setStroke(new BasicStroke(2.0f));
			g2.setPaint(Color.red);
			for (Shape shape : shapes) {
				g2.draw(shape);
			}
		}
	}
}