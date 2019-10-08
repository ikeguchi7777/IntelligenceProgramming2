import java.util.ArrayList;

public class Search4 {
	Node[] node;
	Node goal;
	Node start;

	Search4() {
		makeStateSpace();
	}

	private void makeStateSpace() {
		node = new Node[4];
		// 状態空間の生成
		node[0] = new Node("Start", 0);
		node[1] = new Node("Goal", 0);
		node[2] = new Node("Good", 3);
		node[3] = new BadNode("Bad", 1);
		start = node[0];
		goal = node[1];

		node[0].addChild(node[3], 1);
		node[0].addChild(node[2], 1);
		node[2].addChild(node[1], 4);
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

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
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
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 深さ優先探索
	 */
	public void depthFirst() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		ArrayList<Node> newOpen = new ArrayList<>();
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;
		int step = 0;

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("NEWOPEN:" + newOpen.toString());
			System.out.println("CLOSED:" + closed.toString());
			// openは空か？
			if (open.size() == 0) {
				if (newOpen.size() == 0) {
					success = false;
					break;
				}
				open = newOpen;
				newOpen = new ArrayList<>();
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
						if (!open.contains(m) && !newOpen.contains(m) && !closed.contains(m)) {
							// m から node へのポインタを付ける
							m.setPointer(node);
							if (m == goal) {
								newOpen.add(0, m);
							} else {
								newOpen.add(j, m);
							}
							j++;
						}
					}
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
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

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
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
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	/***
	 * 山登り法
	 */
	public void hillClimbing() {
		ArrayList<Node> open = new ArrayList<Node>();
		open.add(start);
		start.setGValue(0);
		ArrayList<Node> closed = new ArrayList<Node>();
		boolean success = false;

		// Start を node とする．
		Node node = start;
		for (;;) {
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
					node = min;
				}
			}
		}
		if (success) {
			System.out.println("*** Solution ***");
			printSolution(goal);
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

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
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
			System.out.println("*** Solution ***");
			printSolution(goal);
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

		for (;;) {
			System.out.println("STEP:" + (step++));
			System.out.println("OPEN:" + open.toString());
			System.out.println("CLOSED:" + closed.toString());
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
			System.out.println("*** Solution ***");
			printSolution(goal);
		}
	}

	
	
	/***
	 * 解の表示
	 */
	public void printSolution(Node theNode) {
		if (theNode == start) {
			System.out.println(theNode.toString());
		} else {
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

	public static void main(String[] args) {
		if (args.length != 1) {
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
				(new Search4()).breadthFirst();
				break;
			case 2:
				// 深さ優先探索
				System.out.println("\nDepth First Search");
				(new Search4()).depthFirst();
				break;
			case 3:
				// 分岐限定法
				System.out.println("\nBranch and Bound Search");
				(new Search4()).branchAndBound();
				break;
			case 4:
				// 山登り法
				System.out.println("\nHill Climbing Search");
				(new Search4()).hillClimbing();
				break;
			case 5:
				// 最良優先探索
				System.out.println("\nBest First Search");
				(new Search4()).bestFirst();
				break;
			case 6:
				// A*アルゴリズム
				System.out.println("\nA star Algorithm");
				(new Search4()).aStar();
				break;
			default:
				System.out.println("Please input numbers 1 to 6");
			}
		}
	}
}
