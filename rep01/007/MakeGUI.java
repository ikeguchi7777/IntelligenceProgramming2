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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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

	public static GraphPanel MakeSearchGUI(ActionListener listener) {
		FrameBase frame = new FrameBase("test", 1000, 500);
		GraphPanel gPanel = new GraphPanel(1000, 500);
		JPanel p = new JPanel();
		JButton btn1 = new JButton("閉じる");
		btn1.addActionListener(listener);
		btn1.setActionCommand("close");
		JButton btn2 = new JButton("１ステップ");
		btn2.addActionListener(listener);
		btn2.setActionCommand("step");
		JButton btn3 = new JButton("最後まで");
		btn3.addActionListener(listener);
		btn3.setActionCommand("last");

		p.add(btn1);
		p.add(btn2);
		p.add(btn3);

		JPanel p2 = new JPanel();
		JLabel label1 = new JLabel("Step：");
		JLabel label2 = new JLabel();
		p2.add(label1);
		p2.add(label2);
		gPanel.SetStepLabel(label2);

		frame.getContentPane().add(p2, BorderLayout.NORTH);
		frame.getContentPane().add(p, BorderLayout.SOUTH);
		frame.getContentPane().add(gPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		return gPanel;
	}
}

class GraphPanel extends JPanel {
	private ArrayList<Shape> shapes = new ArrayList<>();
	private HashMap<String, DefaultMutableTreeNode> nodeMap;
	private RenderingHints rh;
	private JTree tree;
	private JLabel step;
	String msg = "";

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
	
	public void ShowDialog() {
		JLabel label = new JLabel("探索したルート:"+msg);
	    JOptionPane.showMessageDialog(this.getParent(), label);
	}

	public void SetStepLabel(JLabel label) {
		step = label;
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

	public void makeLocalGraph(Node root) {
		setLayout(null);
		removeAll();
		JLabel rootlab = makeNodeLabel(root,Color.RED, 150, 200);
		int num = root.getChildren().size();
		if (num == 1) {
			JLabel child = makeNodeLabel(root.getChildren().get(0),Color.BLUE, 700, 200);
			add(child);
		} else {
			int t = 400/(num-1);
			for (int i = 0; i < num; i++) {
				JLabel child = makeNodeLabel(root.getChildren().get(i),Color.BLUE, 700, t*i);
				add(child);
			}
		}
		add(rootlab);
		setVisible(false);
		setVisible(true);
	}

	public JLabel makeNodeLabel(Node node,Color border, int x, int y) {
		JLabel label = new JLabel(node.toString());
		label.setBorder(new LineBorder(border, 8, true));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBounds(x, y, 200, 100);
		return label;
	}
	
	public void makeArrow(Node p,Node c) {
		clear();
		int num = p.getChildren().size();
		int i = p.getChildren().indexOf(c);
		if(num==1) {
			DrawArrow arrow = new DrawArrow(new Point(370, 250), new Point(680, 250));
			addShape(arrow);
		}
		else {
			DrawArrow arrow = new DrawArrow(new Point(370, 250), new Point(680, 400/(num-1)*i+50));
			addShape(arrow);
		}
		repaint();
	}

	public void makeTreeList(Node root) {
		nodeMap = new HashMap<>();
		//clear();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
		nodeMap.put(root.name, rootNode);
		tree = new JTree(rootNode);
		add(tree);
		setVisible(false);
		setVisible(true);
	}

	public void addTreeList(Node p, Node c) {
		DefaultMutableTreeNode parent = nodeMap.get(p.name);
		if (parent == null) {
			System.out.println("error");
			return;
		}
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(c);
		parent.add(child);
		nodeMap.put(c.name, child);
		DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
		m.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public void removeNodeFromTree(Node c) {
		DefaultMutableTreeNode child = nodeMap.get(c.name);
		if (child != null) {
			child.removeFromParent();
		}
	}

	public void addLeaf(Node p) {
		DefaultMutableTreeNode parent = nodeMap.get(p.name);
		if (parent == null) {
			System.out.println("error");
			return;
		}
		parent.add(new DefaultMutableTreeNode("leaf"));
		DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
		m.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public void addGoal(Node p) {
		DefaultMutableTreeNode parent = nodeMap.get(p.name);
		if (parent == null) {
			System.out.println("error");
			return;
		}
		parent.add(new DefaultMutableTreeNode("Goal"));
		DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
		m.reload();
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public void ChangeStep(int step) {
		this.step.setText("" + step);
	}
}