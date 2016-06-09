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
 * @param <TKey>
 * @param <TValue>
 */
public class MainFrame<TKey extends Comparable<TKey> & Serializable, TValue extends Serializable> extends JFrame 
{
	
	MainFrame(String file){
		super("Tree file reader");
		setBounds(100,100,500,500);
		
		GraphicsPanel p = new GraphicsPanel<TKey, TValue>(file,this);
		JScrollPane scrollPane = new JScrollPane(p);
		setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JButton button = new JButton("Reload");
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
		        p.init(file);
		        p.revalidate();
		        p.repaint();
			}
			
		});
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
