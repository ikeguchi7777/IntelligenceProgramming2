import java.util.*;

public class Search {
	Node[] node;
	Node goal;
	Node start;
	String[] locations = { "L.A.Airport", "UCLA", "Hoolywood", "Anaheim", "GrandCanyon", "SanDiego", "Downtown",
			"Pasadena", "DisneyLand", "Las Vegas" };
	int[] nodeRand;
	int[] costRand;

	Search() {
		Random rand = new Random();
		nodeRand = new int[10];
		costRand = new int[18];
		for (int i = 0; i < nodeRand.length; i++)
			nodeRand[i] = rand.nextInt(9) + 1;
		for (int i = 0; i < costRand.length; i++)
			costRand[i] = rand.nextInt(9) + 1;
		makeStateSpace();
	}

	private void makeStateSpace() {
		// 状態空間の生成
		Node[] nodelist = new Node[10];
		for (int i = 0; i < locations.length; i++)
			nodelist[i] = new Node(locations[i], nodeRand[i]);
		nodelist[0].addChild(nodelist[1], costRand[0]);
		nodelist[0].addChild(nodelist[2], costRand[1]);
		nodelist[1].addChild(nodelist[2], costRand[2]);
		nodelist[1].addChild(nodelist[6], costRand[3]);
		nodelist[2].addChild(nodelist[3], costRand[4]);
		nodelist[2].addChild(nodelist[6], costRand[5]);
		nodelist[2].addChild(nodelist[7], costRand[6]);
		nodelist[3].addChild(nodelist[4], costRand[7]);
		nodelist[3].addChild(nodelist[7], costRand[8]);
		nodelist[3].addChild(nodelist[8], costRand[9]);
		nodelist[4].addChild(nodelist[8], costRand[10]);
		nodelist[4].addChild(nodelist[9], costRand[11]);
		nodelist[5].addChild(nodelist[1], costRand[12]);
		nodelist[6].addChild(nodelist[5], costRand[13]);
		nodelist[6].addChild(nodelist[7], costRand[14]);
		nodelist[7].addChild(nodelist[8], costRand[15]);
		nodelist[7].addChild(nodelist[9], costRand[16]);
		nodelist[8].addChild(nodelist[9], costRand[17]);
		node = nodelist.clone();
		start = node[0];
		goal = node[9];
	}

	/***
	 * 幅優先探索
	 */
	public void breadthFirst() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		while (step < 100) {
			step++;// System.out.println("STEP:" + (step++));
			// System.out.println("OPEN:" + open.toString());
			// System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					// 子節点 m が open にも closed にも含まれていなければ，
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							if (m == goal) {
								open.add(0, m);
							} else {
								open.add(m);
							}
						}
					}
				}
			}
		}
		if (success) {
			// System.out.println("*** Solution ***");
			printSolution(goal, step);
		}
	}

	/***
	 * 深さ優先探索
	 */
	public void depthFirst() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		while (step < 100) {
			step++;// System.out.println("STEP:" + (step++));
			// System.out.println("OPEN:" + open.toString());
			// System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
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
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける
							m.setPointer(node);
							if (m == goal) {
								open.add(0, m);
							} else {
								open.add(j, m);
							}
							j++;
						}
					}
				}
			}
		}
		if (success) {
			// System.out.println("*** Solution ***");
			printSolution(goal, step);
		}
	}

	/***
	 * 分岐限定法
	 */
	public void branchAndBound() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		while (step < 100) {
			step++;// System.out.println("STEP:" + (step++));
			// System.out.println("OPEN:" + open.toString());
			// System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							// nodeまでの評価値とnode->mのコストを
							// 足したものをmの評価値とする
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
							open.add(m);
						}
						// 子節点mがopenに含まれているならば，
						if (open.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							if (gmn < m.getGValue()) {
								m.setGValue(gmn);
								m.setPointer(node);
							}
						}
					}
				}
			}
			open = sortUpperByGValue(open);
		}
		if (success) {
			// System.out.println("*** Solution ***");
			printSolution(goal, step);
		}
	}

	/***
	 * 山登り法
	 */
	public void hillClimbing() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		boolean success = false;
		int step = 0;

		// Start を node とする．
		Node node = start;
		while (step < 100) {
			step++;
			// node は ゴールか？
			if (node == goal) {
				success = true;
				break;
			} else {
				// node を展開して子節点をすべて求める．
				ArrayList<Node> children = node.getChildren();
				// System.out.println(children.toString());
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
					node = min;
				}
			}
		}
		if (success) {
			// System.out.println("*** Solution ***");
			printSolution(goal, step);
		}
	}

	/***
	 * 最良優先探索
	 */
	public void bestFirst() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		while (step < 100) {
			step++;// System.out.println("STEP:" + (step++));
			// System.out.println("OPEN:" + open.toString());
			// System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						// 子節点mがopenにもclosedにも含まれていなければ，
						if (!open.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける．
							m.setPointer(node);
							open.add(m);
						}
					}
				}
			}
			open = sortUpperByHValue(open);
		}
		if (success) {
			// System.out.println("*** Solution ***");
			printSolution(goal, step);
		}
	}

	/***
	 * A* アルゴリズム
	 */
	public void aStar() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		start.setFValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		while (step < 100) {
			step++;// System.out.println("STEP:" + (step++));
			// System.out.println("OPEN:" + open.toString());
			// System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				success = false;
				break;
			} else {
				// openの先頭を取り出し node とする．
				Node node = open.get(0);
				open.remove(0);
				// node は ゴールか？
				if (node == goal) {
					success = true;
					break;
				} else {
					// node を展開して子節点をすべて求める．
					ArrayList<Node> children = node.getChildren();
					// node を closed に入れる．
					closed.add(node);
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
						} else if (open.contains(m)) {
							// 子節点mがopenに含まれている場合
							if (gmn < m.getGValue()) {
								// 評価値を更新し，m から node へのポインタを付け替える
								m.setGValue(gmn);
								m.setFValue(fmn);
								m.setPointer(node);
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
							}
						}
					}
				}
			}
			open = sortUpperByFValue(open);
		}
		if (success) {
			// System.out.println("*** Solution ***");
			printSolution(goal, step);
		}
	}

	/***
	 * 解の表示
	 */
	public void printSolution(Node theNode, int step) {
		// int gValue = 0;
		if (theNode == start) {
			System.out.println(theNode.toString());
			System.out.println("step:" + step);
			// System.out.println("gvalue:"+gValue+",step:"+step);
		} else {
			System.out.print(theNode.toString() + " <- ");
			// gValue += theNode.getPointer().getCost(theNode);
			printSolution(theNode.getPointer(), step);
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

	public static void main(String[] args) {
		/*
		 * if (args.length != 1) { System.out.println("USAGE:");
		 * System.out.println("java Search [Number]");
		 * System.out.println("[Number] = 1 : Bredth First Search");
		 * System.out.println("[Number] = 2 : Depth  First Search");
		 * System.out.println("[Number] = 3 : Branch and Bound Search");
		 * System.out.println("[Number] = 4 : Hill Climbing Search");
		 * System.out.println("[Number] = 5 : Best First Search");
		 * System.out.println("[Number] = 6 : A star Algorithm"); } else { int which =
		 * Integer.parseInt(args[0]); switch (which) { case 1: // 幅優先探索
		 * System.out.println("\nBreadth First Search"); (new Search()).breadthFirst();
		 * break; case 2: // 深さ優先探索 System.out.println("\nDepth First Search"); (new
		 * Search()).depthFirst(); break; case 3: // 分岐限定法
		 * System.out.println("\nBranch and Bound Search"); (new
		 * Search()).branchAndBound(); break; case 4: // 山登り法
		 * System.out.println("\nHill Climbing Search"); (new Search()).hillClimbing();
		 * break; case 5: // 最良優先探索 System.out.println("\nBest First Search"); (new
		 * Search()).bestFirst(); break; case 6: // A*アルゴリズム
		 * System.out.println("\nA star Algorithm"); (new Search()).aStar(); break;
		 * default: System.out.println("Please input numbers 1 to 6"); } }
		 */
		Search place = new Search();
		System.out.println("Breadth First Search");
		place.makeStateSpace();
		place.breadthFirst();
		System.out.println("Depth First Search");
		place.makeStateSpace();
		place.depthFirst();
		System.out.println("Branch and Bound Search");
		place.makeStateSpace();
		place.branchAndBound();
		System.out.println("Hill Climbing Search");
		place.makeStateSpace();
		place.hillClimbing();
		System.out.println("Best First Search");
		place.makeStateSpace();
		place.bestFirst();
		System.out.println("A star Algorithm");
		place.makeStateSpace();
		place.aStar();
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