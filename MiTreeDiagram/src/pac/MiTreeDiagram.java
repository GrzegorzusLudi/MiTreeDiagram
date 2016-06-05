package pac;
import java.io.File;
import javax.swing.JFileChooser;

import javax.swing.JFrame;
public class MiTreeDiagram {
	static String path = "C:\\Users\\KaczorDonald\\BTreeTest";
	public static void main(String [] args){
		//source of data fileJFileChooser jFileChooser = new JFileChooser();
		
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setCurrentDirectory(new File(path));
		
		int result = jFileChooser.showOpenDialog(new JFrame());
	
		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
		    selectedFile = jFileChooser.getSelectedFile();
		}
		MemoryCheck<Integer, Integer> window = new MemoryCheck<Integer,Integer>(selectedFile.getAbsolutePath());
	}
}
