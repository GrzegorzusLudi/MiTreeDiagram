package pac;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Application which shows structure of the tree (shows tree file in a graphical form).
 * After start, it's needed to choose file which contains the tree.
 * Every line represents one page. The application shows only used nodes of the tree.
 * In each node, there are three types of showed values:
 * -green rectangles represent pointers (in inner node they have also index of page where it points)
 * -yellow rectangles represent keys (with their value)
 * -lightblue rectangles represent additional values of inner nodes of the tree
 * 
 * In the bottom of the window there are:
 * -checkbox which turns the clear layout mode (root is showed last, doesn't show unused pages),
 * -reload button

 * @author Grzegorz Stasiak
 *
 */
public class MiTreeDiagram {
	/**
	 * Default path to tree file
	 */
	static String path = "C:\\Users\\KaczorDonald"; //currently name of my commputer account
	public static void main(String [] args){
		//choose the source of tree file
		
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setCurrentDirectory(new File(path));
		
		int result = jFileChooser.showOpenDialog(new JFrame());
	
		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
		    selectedFile = jFileChooser.getSelectedFile();
		}
		//GUI window execution
		try {
			MainFrame<Integer> window = new MainFrame<Integer>(selectedFile.getAbsolutePath());
		} catch(OutOfMemoryError e) {
			//if the file is invalid
			System.exit(0);
		}
	}
}