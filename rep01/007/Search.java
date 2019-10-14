import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class Search implements Runnable {
	Node[] node;
	Node goal;
	Node start;

	GraphPanel gPanel;

	int type;
	boolean isStop = true;

	Search() {
		makeStateSpace();
	}

	private void makeStateSpace() {
		node = new Node[10];
		// 状態空間の生成
		node[0] = new Node("L.A.Airport", 0);
		node[1] = new Node("UCLA", 7);
		node[2] = new Node("Hoolywood", 4);
		node[3] = new Node("Anaheim", 6);
		node[4] = new Node("GrandCanyon", 1);
		node[5] = new Node("SanDiego", 2);
		node[6] = new Node("Downtown", 3);
		node[7] = new Node("Pasadena", 4);
		node[8] = new Node("DisneyLand", 2);
		node[9] = new Node("Las Vegas", 0);
		start = node[0];
		goal = node[9];

		node[0].addChild(node[1], 1);
		node[0].addChild(node[2], 3);
		node[1].addChild(node[2], 1);
		node[1].addChild(node[6], 6);
		node[2].addChild(node[3], 6);
		node[2].addChild(node[6], 6);
		node[2].addChild(node[7], 3);
		node[3].addChild(node[4], 5);
		node[3].addChild(node[7], 2);
		node[3].addChild(node[8], 4);
		node[4].addChild(node[8], 2);
		node[4].addChild(node[9], 1);
		node[5].addChild(node[1], 1);
		node[6].addChild(node[5], 7);
		node[6].addChild(node[7], 2);
		node[7].addChild(node[8], 1);
		node[7].addChild(node[9], 7);
		node[8].addChild(node[9], 5);
	}

	/***
	 * 幅優先探索
	 */
	synchronized public void breadthFirst() {
		gPanel.makeTreeList(start);
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;
		long time,start_time,end_time,total;
		time=0;
		total=0;
		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			if (isStop) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			start_time=System.currentTimeMillis();
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				gPanel.selectTreeNode(node);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					gPanel.addGoal(node);
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					boolean isleaf = true;
					// 子節点 m が open にも closed にも含まれていなければ，
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							isleaf = false;
							gPanel.addTreeList(node, m);
							m.setPointer(node);
							if (m == goal) {
								open.add(0, m);
							} else {
								open.add(m);
							}
						}
					}
					if (isleaf)
						gPanel.addLeaf(node);
				}
			}
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
		}
		if (success) {
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 深さ優先探索
	 */
	synchronized public void depthFirst() {
		gPanel.makeTreeList(start);
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;
		long time,start_time,end_time,total;
		time=0;
		total=0;
		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			if (isStop) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			start_time=System.currentTimeMillis();
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				gPanel.selectTreeNode(node);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					gPanel.addGoal(node);
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					// 子節点 m が open にも closed にも含まれていなければ，
					// 以下を実行．幅優先探索と異なるのはこの部分である．
					// j は複数の子節点を適切にopenの先頭に置くために位置
					// を調整する変数であり，一般には展開したときの子節点
					// の位置は任意でかまわない．
					int j = 0;
					boolean isleaf = true;
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける
							isleaf=false;
							gPanel.addTreeList(node, m);
							m.setPointer(node);
							if (m == goal) {
								open.add(0, m);
							} else {
								open.add(j, m);
							}
							j++;
						}
					}
					if(isleaf)
						gPanel.addLeaf(node);
				}
			}
			end_time=System.currentTimeMillis();
			time=end_time-start_time;
		}
		if (success) {
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 分岐限定法
	 */
	synchronized public void branchAndBound() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		gPanel.makeTreeList(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;
		long time,start_time,end_time,total;
		time=0;
		total=0;
		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			if (isStop) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			start_time=System.currentTimeMillis();
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				gPanel.selectTreeNode(node);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					gPanel.addGoal(node);
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					boolean isleaf = true;
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							isleaf=false;
							m.setPointer(node);
							// nodeまでの評価値とnode->mのコストを
							// 足したものをmの評価値とする
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
							open.add(m);
							gPanel.addTreeList(node, m);
						}
						// 子節点mがopenに含まれているならば，
						if (open.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							if (gmn < m.getGValue()) {
								m.setGValue(gmn);
								m.setPointer(node);
								gPanel.removeNodeFromTree(m);
								gPanel.addTreeList(node, m);
								isleaf=false;
							}
						}
					}
					if(isleaf)
						gPanel.addLeaf(node);
				}
			}
			open = sortUpperByGValue(open);
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
		}
		if (success) {
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 山登り法
	 */
	synchronized public void hillClimbing() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;
		// Start を node とする．
		Node node = start;
		long time,start_time,end_time,total;
		time=0;
		total=0;
		for (;;) {
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			if (isStop) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			start_time=System.currentTimeMillis();
			step++;
			gPanel.makeLocalGraph(node);
			// node は ゴールか？
			if (node == goal) {
				success = true;
				break;
			} else {
				// node を展開して子節点をすべて求める．
				ArrayList<Node> children = node.getChildren();
				System.out.println(children.toString());
				for (int i = 0; i < children.size(); i++) {
					Node m = children.get(i);
					// m から node へのポインタを付ける．
					m.setPointer(node);
				}
				// 子節点の中に goal があれば goal を node とする．
				// なければ，最小の hValue を持つ子節点 m を node
				// とする．
				boolean goalp = false;
				Node min = children.get(0);
				for (int i = 0; i < children.size(); i++) {
					Node a = children.get(i);
					if (a == goal) {
						goalp = true;
					} else if (min.getHValue() > a.getHValue()) {
						min = a;
					}
				}
				if (goalp) {
					node = goal;
				} else {
					gPanel.makeArrow(node, min);
					node = min;
				}
			}
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
		}
		if (success) {
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 最良優先探索
	 */
	synchronized public void bestFirst() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		gPanel.makeTreeList(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;
		long time,start_time,end_time,total;
		time=0;
		total=0;
		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			if (isStop) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			start_time=System.currentTimeMillis();
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				gPanel.selectTreeNode(node);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					gPanel.addGoal(node);
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					boolean isleaf = true;
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							open.add(m);
							isleaf=false;
							gPanel.addTreeList(node, m);
						}
					}
					if(isleaf)
						gPanel.addLeaf(node);
				}
			}
			open = sortUpperByHValue(open);
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
		}
		if (success) {
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * A* アルゴリズム
	 */
	synchronized public void aStar() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		start.setFValue(0);
		gPanel.makeTreeList(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;
		long time,start_time,end_time,total;
		time=0;
		total=0;
		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			if (isStop) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			start_time=System.currentTimeMillis();
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				gPanel.selectTreeNode(node);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					gPanel.addGoal(node);
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					boolean isleaf = true;
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						int gmn = node.getGValue() + node.getCost(m);
						int fmn = gmn + m.getHValue();

						// 各子節点mの評価値とポインタを設定する
						if (!open.contains(m) && !closed.contains(m)) {
							// 子節点mがopenにもclosedにも含まれていない場合
							// m から node へのポインタを付ける．
							m.setGValue(gmn);
							m.setFValue(fmn);
							m.setPointer(node);
							// mをopenに追加
							open.add(m);
							isleaf=false;
							gPanel.addTreeList(node, m);
						} else if (open.contains(m)) {
							// 子節点mがopenに含まれている場合
							if (gmn < m.getGValue()) {
								// 評価値を更新し，m から node へのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
								isleaf=false;
								gPanel.removeNodeFromTree(m);
								gPanel.addTreeList(node, m);
							}
						} else if (closed.contains(m)) {
							if (gmn < m.getGValue()) {
								// 子節点mがclosedに含まれていて fmn < fm となる場合
								// 評価値を更新し，mからnodeへのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
								// 子節点mをclosedからopenに移動
								closed.remove(m);
								open.add(m);
								isleaf=false;
								gPanel.removeNodeFromTree(m);
								gPanel.addTreeList(node, m);
							}
						}
					}
				}
			}
			open = sortUpperByFValue(open);
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
		}
		if (success) {
			end_time=System.currentTimeMillis();
			time=end_time - start_time;
			total+=time;
			gPanel.ChangeLabel(step,time,open.size(),closed.size(),total);
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 解の表示
	 */
	public void printSolution(Node theNode) {
		if (theNode == start) {
			gPanel.msg+=theNode.toString();
			System.out.println(theNode.toString());
			gPanel.ShowDialog();
		} else {
			gPanel.msg+=theNode.toString() + " <- ";
			System.out.print(theNode.toString() + " <- ");
			printSolution(theNode.getPointer());
		}
	}

	/***
	 * ArrayList を Node の fValue の昇順（小さい順）に列べ換える．
	 */
	public ArrayList<Node> sortUpperByFValue(ArrayList<Node> theOpen) {
		ArrayList<Node> newOpen = new ArrayList<Node>();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.get(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.get(i);
				if (min.getFValue() > tmp.getFValue()) {
					min = tmp;
				}
			}
			newOpen.add(min);
			theOpen.remove(min);
		}
		return newOpen;
	}

	/***
	 * ArrayList を Node の gValue の昇順（小さい順）に列べ換える．
	 */
	public ArrayList<Node> sortUpperByGValue(ArrayList<Node> theOpen) {
		ArrayList<Node> newOpen = new ArrayList<Node>();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.get(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.get(i);
				if (min.getGValue() > tmp.getGValue()) {
					min = tmp;
				}
			}
			newOpen.add(min);
			theOpen.remove(min);
		}
		return newOpen;
	}

	/***
	 * ArrayList を Node の hValue の昇順（小さい順）に列べ換える．
	 */
	public ArrayList<Node> sortUpperByHValue(ArrayList<Node> theOpen) {
		ArrayList<Node> newOpen = new ArrayList<Node>();
		Node min, tmp = null;
		while (theOpen.size() > 0) {
			min = (Node) theOpen.get(0);
			for (int i = 1; i < theOpen.size(); i++) {
				tmp = (Node) theOpen.get(i);
				if (min.getHValue() > tmp.getHValue()) {
					min = tmp;
				}
			}
			newOpen.add(min);
			theOpen.remove(min);
		}
		return newOpen;
	}

	public static void actionByCommand(String cmd) {
		Thread th;
		Search sh = new Search();

		switch (cmd) {
		case "Bredth First Search":
			// 幅優先探索
			System.out.println("\nBreadth First Search");
			sh.type = 1;
			break;
		case "Depth  First Search":
			// 深さ優先探索
			System.out.println("\nDepth First Search");
			sh.type = 2;
			break;
		case "Branch and Bound Search":
			// 分岐限定法
			System.out.println("\nBranch and Bound Search");
			sh.type = 3;
			break;
		case "Hill Climbing Search":
			// 山登り法
			System.out.println("\nHill Climbing Search");
			sh.type = 4;
			break;
		case "Best First Search":
			// 最良優先探索
			System.out.println("\nBest First Search");
			sh.type = 5;
			break;
		case "A star Algorithm":
			// A*アルゴリズム
			System.out.println("\nA star Algorithm");
			sh.type = 6;
			break;
		default:
			System.out.println("存在しない探索");
			return;
		}
		th = new Thread(sh);
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd1 = e.getActionCommand();
				switch (cmd1) {
				case "close":
					System.exit(0);
					break;
				case "step":
					synchronized (sh) {
						sh.notifyAll();
					}
					break;
				case "last":
					sh.isStop = false;
					synchronized (sh) {
						sh.notifyAll();
					}
					break;
				default:
					break;
				}

			}
		};
		sh.gPanel = MakeGUI.MakeSearchGUI(listener);
		th.start();

	}

	public static void main(String[] args) {
		String[] searchNames = {
				"Bredth First Search",
				"Depth  First Search",
				"Branch and Bound Search",
				"Hill Climbing Search",
				"Best First Search",
				"A star Algorithm" };
		MakeGUI.MakeChooseSearchGUI(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自動生成されたメソッド・スタブ
				actionByCommand(e.getActionCommand());
			}
		}, searchNames);

		/*if (args.length != 1) {
			System.out.println("USAGE:");
			System.out.println("java Search [Number]");
			System.out.println("[Number] = 1 : Bredth First Search");
			System.out.println("[Number] = 2 : Depth  First Search");
			System.out.println("[Number] = 3 : Branch and Bound Search");
			System.out.println("[Number] = 4 : Hill Climbing Search");
			System.out.println("[Number] = 5 : Best First Search");
			System.out.println("[Number] = 6 : A star Algorithm");
		} else {
			int which = Integer.parseInt(args[0]);
			switch (which) {
			case 1:
				// 幅優先探索
				System.out.println("\nBreadth First Search");
				(new Search()).breadthFirst();
				break;
			case 2:
				// 深さ優先探索
				System.out.println("\nDepth First Search");
				(new Search()).depthFirst();
				break;
			case 3:
				// 分岐限定法
				System.out.println("\nBranch and Bound Search");
				(new Search()).branchAndBound();
				break;
			case 4:
				// 山登り法
				System.out.println("\nHill Climbing Search");
				(new Search()).hillClimbing();
				break;
			case 5:
				// 最良優先探索
				System.out.println("\nBest First Search");
				(new Search()).bestFirst();
				break;
			case 6:
				// A*アルゴリズム
				System.out.println("\nA star Algorithm");
				(new Search()).aStar();
				break;
			default:
				System.out.println("Please input numbers 1 to 6");
			}
		}*/
	}

	@Override
	public void run() {
		switch (type) {
		case 1:
			// 幅優先探索
			breadthFirst();
			break;
		case 2:
			// 深さ優先探索
			depthFirst();
			break;
		case 3:
			// 分岐限定法
			branchAndBound();
			break;
		case 4:
			// 山登り法
			hillClimbing();
			break;
		case 5:
			// 最良優先探索
			bestFirst();
			break;
		case 6:
			// A*アルゴリズム
			aStar();
			break;
		default:
			return;
		}
	}

}

class Node {
	String name;
	ArrayList<Node> children;
	HashMap<Node, Integer> childrenCosts;
	Node pointer; // 解表示のためのポインタ
	int gValue; // コスト
	int hValue; // ヒューリスティック値
	int fValue; // 評価値
	boolean hasGValue = false;
	boolean hasFValue = false;

	Node(String theName, int theHValue) {
		name = theName;
		children = new ArrayList<Node>();
		childrenCosts = new HashMap<Node, Integer>();
		hValue = theHValue;
	}

	public String getName() {
		return name;
	}

	public void setPointer(Node theNode) {
		this.pointer = theNode;
	}

	public Node getPointer() {
		return this.pointer;
	}

	public int getGValue() {
		return gValue;
	}

	public void setGValue(int theGValue) {
		hasGValue = true;
		this.gValue = theGValue;
	}

	public int getHValue() {
		return hValue;
	}

	public int getFValue() {
		return fValue;
	}

	public void setFValue(int theFValue) {
		hasFValue = true;
		this.fValue = theFValue;
	}

	/***
	 * theChild この節点の子節点 theCost その子節点までのコスト
	 */
	public void addChild(Node theChild, int theCost) {
		children.add(theChild);
		childrenCosts.put(theChild, new Integer(theCost));
	}

	public ArrayList<Node> getChildren() {
		return children;
	}

	public int getCost(Node theChild) {
		return childrenCosts.get(theChild).intValue();
	}

	public String toString() {
		String result = name + "(h:" + hValue + ")";
		if (hasGValue) {
			result = result + "(g:" + gValue + ")";
		}
		if (hasFValue) {
			result = result + "(f:" + fValue + ")";
		}
		return result;
	}
}
