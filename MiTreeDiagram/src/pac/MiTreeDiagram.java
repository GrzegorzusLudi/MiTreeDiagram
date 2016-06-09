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
