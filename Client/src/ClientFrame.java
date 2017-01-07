import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;

public final class ClientFrame extends JFrame {

	private final RSApplet applet;
	public Toolkit toolkit = Toolkit.getDefaultToolkit();
	public Dimension screenSize = toolkit.getScreenSize();
	public int screenWidth = (int) screenSize.getWidth();
	public int screenHeight = (int) screenSize.getHeight();
	protected final Insets insets;
	private static final long serialVersionUID = 1L;

	public ClientFrame(RSApplet applet, int width, int height, boolean resizable, boolean fullscreen) throws IOException {
		this.applet = applet;
		setTitle("Dodian.net - The Revival");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(resizable);
		setUndecorated(fullscreen);
		setVisible(true);
		insets = getInsets();
		if (resizable) {
			setMinimumSize(new Dimension(766 + insets.left + insets.right, 536 + insets.top + insets.bottom));
		}
		setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		setLocationRelativeTo(null);
		setBackground(Color.BLACK);
		requestFocus();
		toFront();
		setIconImage(Toolkit.getDefaultToolkit().getImage(sign.signlink.findcachedir() + "icon.png"));
		Client.readSettings();
	}

	public Graphics getGraphics() {
		final Graphics graphics = super.getGraphics();
		Insets insets = this.getInsets();
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.translate(insets != null ? insets.left : 0, insets != null ? insets.top : 0);
		return graphics;
	}

	public int getFrameWidth() {
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}

	public int getFrameHeight() {
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}

	public void update(Graphics graphics) {
		applet.update(graphics);
	}

	public void paint(Graphics graphics) {
		applet.paint(graphics);
	}
}