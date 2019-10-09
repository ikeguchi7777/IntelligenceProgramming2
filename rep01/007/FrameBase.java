import javax.swing.JFrame;

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
}

class ThreadTest implements Runnable{

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		test();
	}
	
	synchronized public void test() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("test");
	}
}