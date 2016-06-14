package pac;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * GUI window
 * @author Grzegorz Stasiak
 *
 * @param <TKey> key type
 */
public class MainFrame<TKey extends Comparable<TKey> & Serializable> extends JFrame 
{
	//String file - path to loaded file
	MainFrame(String file){
		super("Tree file reader");
		setBounds(100,100,500,500);
		
		//drawing panel
		GraphicsPanel p = new GraphicsPanel<TKey>(file,this);
		
		JScrollPane scrollPane = new JScrollPane(p);
		setLayout(new BorderLayout());
		
		//bottom panel
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//reload button
		JButton button = new JButton("Reload");
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
		        p.init(file);
		        p.revalidate();
		        p.repaint();
			}
			
		});
		
		//clear layout checkbox
		JCheckBox cb = new JCheckBox("Clear layout");
		cb.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
			        p.setClearLayout(true);
			        p.revalidate();
			        p.repaint();
			    } else {
			        p.setClearLayout(false);
			        p.revalidate();
			        p.repaint();
			    }
			}
			
		});
		add(scrollPane,BorderLayout.CENTER);
		add(panel,BorderLayout.SOUTH);
		panel.add(cb,BorderLayout.WEST);
		panel.add(button,BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
