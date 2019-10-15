import java.util.*;

public class SearchRand {
	public final int breadthFirst = 0;
	public final int depthFirst = 1;
	public final int branchAndBound = 2;
	public final int hillClimbing = 3;
	public final int bestFirst = 4;
	public final int aStar = 5;
	public final int STEP = 0;
	public final int COST = 1;
	static Scanner std = new Scanner(System.in);
	Node[] node;
	Node goal;
	Node start;
	// ノード名
	String[] locations = { "L.A.Airport", "UCLA", "Hoolywood", "Anaheim", "GrandCanyon", "SanDiego", "Downtown",
			"Pasadena", "DisneyLand", "Las Vegas" };
	// 分岐元のインデックス
	int[] nodeIndex = { 0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5, 6, 6, 7, 7, 8 };
	// 分岐先のインデックス
	int[] childIndex = { 1, 2, 2, 6, 3, 6, 7, 4, 7, 8, 8, 9, 1, 5, 7, 8, 9, 9 };
	int[] nodeRand;
	int[] costRand;
	int randSize = 9;
	// N回試行の記録(Method,0.step 1.cost)
	int[][] record = new int[2][6];

	public int[][] getRecord() {
		return record;
	}

	SearchRand() {
		// コストとヒューリスティック関数の決定
		Random rand = new Random();
		nodeRand = new int[10];
		costRand = new int[18];
		for (int i = 0; i < nodeRand.length; i++) {
			if (i != 0 && i != nodeRand.length - 1)
				nodeRand[i] = rand.nextInt(randSize) + 1;
			else
				nodeRand[i] = 0;
		}
		for (int i = 0; i < costRand.length; i++)
			costRand[i] = rand.nextInt(randSize) + 1;
		makeStateSpace();
	}

	private void makeStateSpace() {
		// 状態空間の生成
		Node[] nodelist = new Node[10];
		for (int i = 0; i < locations.length; i++)
			nodelist[i] = new Node(locations[i], nodeRand[i]);
		for (int i = 0; i < nodeIndex.length; i++)
			nodelist[nodeIndex[i]].addChild(nodelist[childIndex[i]], costRand[i]);
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
					// 子節点 m が open にも closed にも含まれていなければ，
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
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
			// printSolution(goal, step);
			record[STEP][breadthFirst] = step;
			record[COST][breadthFirst] = goal.getGValue();
		}
	}

	/***
	 * 深さ優先探索
	 */
	public void depthFirst() {
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
					// 子節点 m が open にも closed にも含まれていなければ，
					// 以下を実行．幅優先探索と異なるのはこの部分である．
					// j は複数の子節点を適切にopenの先頭に置くために位置
					// を調整する変数であり，一般には展開したときの子節点
					// の位置は任意でかまわない．
					int j = 0;
					for (int i = 0; i < children.size(); i++) {
						Node m = children.get(i);
						if (!open.contains(m) && !closed.contains(m)) {
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
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
			// printSolution(goal, step);
			record[STEP][depthFirst] = step;
			record[COST][depthFirst] = goal.getGValue();
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
			// printSolution(goal, step);
			record[STEP][branchAndBound] = step;
			record[COST][branchAndBound] = goal.getGValue();
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
					int gmn = node.getGValue() + node.getCost(m);
					m.setGValue(gmn);
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
			// printSolution(goal, step);
			record[STEP][hillClimbing] = step;
			record[COST][hillClimbing] = goal.getGValue();
		} else {
			//System.out.println("faild\nstep:" + step);
			record[STEP][hillClimbing] = step;
			record[COST][hillClimbing] = step * (randSize + 1);
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
							int gmn = node.getGValue() + node.getCost(m);
							m.setGValue(gmn);
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
			// printSolution(goal, step);
			record[STEP][bestFirst] = step;
			record[COST][bestFirst] = goal.getGValue();
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
			// printSolution(goal, step);
			record[STEP][aStar] = step;
			record[COST][aStar] = goal.getGValue();
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

	// ヒューリスティック関数とエッジのコストを表示
	public void printCost() {
		for (int i = 0; i < locations.length; i++)
			System.out.print(i + "." + locations[i] + "(h:" + nodeRand[i] + "),");
		System.out.println();
		for (int i = 0; i < costRand.length; i++) {
			System.out.print(nodeIndex[i] + "->" + childIndex[i] + "(" + costRand[i] + "),");
		}
		System.out.println();
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
		 * System.out.println("java SearchRand [Number]");
		 * System.out.println("[Number] = 1 : Bredth First Search");
		 * System.out.println("[Number] = 2 : Depth  First Search");
		 * System.out.println("[Number] = 3 : Branch and Bound Search");
		 * System.out.println("[Number] = 4 : Hill Climbing Search");
		 * System.out.println("[Number] = 5 : Best First Search");
		 * System.out.println("[Number] = 6 : A star Algorithm"); } else { int which =
		 * Integer.parseInt(args[0]); switch (which) { case 1: // 幅優先探索
		 * System.out.println("\nBreadth First Search"); (new SearchRand()).breadthFirst();
		 * break; case 2: // 深さ優先探索 System.out.println("\nDepth First Search"); (new
		 * SearchRand()).depthFirst(); break; case 3: // 分岐限定法
		 * System.out.println("\nBranch and Bound Search"); (new
		 * SearchRand()).branchAndBound(); break; case 4: // 山登り法
		 * System.out.println("\nHill Climbing Search"); (new SearchRand()).hillClimbing();
		 * break; case 5: // 最良優先探索 System.out.println("\nBest First Search"); (new
		 * SearchRand()).bestFirst(); break; case 6: // A*アルゴリズム
		 * System.out.println("\nA star Algorithm"); (new SearchRand()).aStar(); break;
		 * default: System.out.println("Please input numbers 1 to 6"); } }
		 */
		final int N = 1000000;
		int hillloop =0;
		//0.step 1.cost
		int[][] counts = new int[2][6];
		for (int i = 0; i < N; i++) {
			SearchRand place = new SearchRand();
			//place.printCost();
			//System.out.println("Breadth First Search");
			place.makeStateSpace();
			place.breadthFirst();
			//System.out.println("Depth First Search");
			place.makeStateSpace();
			place.depthFirst();
			//System.out.println("Branch and Bound Search");
			place.makeStateSpace();
			place.branchAndBound();
			//System.out.println("Hill Climbing Search");
			place.makeStateSpace();
			place.hillClimbing();
			//System.out.println("Best First Search");
			place.makeStateSpace();
			place.bestFirst();
			//System.out.println("A star Algorithm");
			place.makeStateSpace();
			place.aStar();
			int[][] record = place.getRecord();
			int minCost = getMin(record[1]);
			for (int j = 0; j < record[1].length; j++) {
				if (minCost == record[1][j]) {
					counts[1][j]++;
					if(record[0][2]<record[0][5]){
						if(record[0][j]<=record[0][2])
							counts[0][j]++;
					}else{
						if(record[0][j]<=record[0][5])
							counts[0][j]++;
					}
				}
			}
			if(record[0][3]==100)hillloop++;
		}
		for(int i=0;i<6;i++){
			System.out.println("counts[0]["+i+"]:"+counts[0][i]+",counts[1]["+i+"]:"+counts[1][i]);
		}
		System.out.println("hillloop:"+hillloop);
	}

	private static int getMin(int[] is) {
		int min = is[0];
		for (int i = 0; i < is.length; i++) {
			if (min > is[i])
				min = is[i];
		}
		return min;
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
			// result = result + "(f:" + fValue + ")";
		}
		return result;
	}
}