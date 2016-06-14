<<<<<<< HEAD
package pac;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Main class of the application
 * @author Grzegorz Stasiak
 *
 */
public class MiTreeDiagram {
	static String path = "C:\\Users\\KaczorDonald";
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
=======
package pac;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Main class of the application
 * @author Grzegorz Stasiak
 *
 */
public class MiTreeDiagram {
	static String path = "C:\\Users\\KaczorDonald";
	public static void main(String [] args){
		//source of data fileJFileChooser jFileChooser = new JFileChooser();
		
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setCurrentDirectory(new File(path));
		
		int result = jFileChooser.showOpenDialog(new JFrame());
	
		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
		    selectedFile = jFileChooser.getSelectedFile();
		}
		MainFrame<Integer, Integer> window = new MainFrame<Integer,Integer>(selectedFile.getAbsolutePath());
	}
}
>>>>>>> 480e63b875a3a2d7a16e2c35a6bb584cf9793a04
