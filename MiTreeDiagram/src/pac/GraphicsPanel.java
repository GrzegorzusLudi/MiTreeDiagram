//<<<<<<< HEAD
package pac;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Drawing in a graphical component
 * @see MiTreeDiagram
 * @author Grzegorz Stasiak
 *
 * @param <TKey>
 * @param <TValue>
 */
public class GraphicsPanel<TKey extends Comparable<TKey> & Serializable> extends JPanel {

	File file;
	/**
	 * Object which operates on the file
	 */
	public RandomAccessFile raFile;
	/*
	 * Variables below are similar to these in TreeFile of MiTree project
	 */
	int pointerSize = Long.BYTES;
	long rootPage;
	int pageSize;
	public int keySize;
	int height;
	int innerValueSize = Integer.BYTES;
	long headerSize;      //size of header of the file
	int keyPointerCount;
	int pageCount = 0;
	/**
	 * Variable stating if the pages are shown in clear layout mode
	 */
	public boolean clearLayout = false;
	/**
	 * Representation of pages in RAM
	 */
	List<gPage> pageList;
	/**
	 * Position of pages in clear layout
	 */
	List<gPage> clearLayoutPageList;
	JFrame frame;

	/*
	 * Dimensions of drawn elements 
	 */
	int pageHeight = 40;
	int leftColumnWidth = 50;
	int xx = 0;
	int yy = -10;
	/** Painting
	 * 
	 */
    public void paint(Graphics g) {   

		if(!clearLayout){
			setPreferredSize(new Dimension(frame.getWidth(),pageCount*pageHeight));
		} else {
			setPreferredSize(new Dimension(frame.getWidth(),clearLayoutPageList.size()*pageHeight));
		}
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setColor(new Color(245,245,245));
		g2d.fillRect(0, 0, getWidth(), getHeight());
    	g2d.translate(leftColumnWidth,0);
    	int a = 0;
    	while((a<pageCount && !clearLayout) || (clearLayout && a<clearLayoutPageList.size())){
    		//draw certain pages
    		gPage uPage;
    		if(!clearLayout){
    			uPage = pageList.get(a);
    		} else {
    			uPage = clearLayoutPageList.get(a);
    		}
        	g2d.setStroke(new BasicStroke(2));
        	g2d.setColor(new Color(200,200,200));
    		g2d.fillRect(0, pageHeight*a, getWidth(), pageHeight);
        	g2d.setColor(new Color(0,0,0));
    		g2d.drawRect(0, pageHeight*a, getWidth(), pageHeight);
    		
        	g2d.setColor(new Color(255,255,255));
    		g2d.fillRect(-leftColumnWidth, pageHeight*a, leftColumnWidth, pageHeight);
        	g2d.setColor(new Color(0,0,0));
    		g2d.drawRect(-leftColumnWidth, pageHeight*a, leftColumnWidth, pageHeight);

        	g2d.setStroke(new BasicStroke(1));

			int validNodeCount = 0;  //number of valid nodes in page
    		int b = 0;
    		while(b<height){
    			//drawing nodes in page
    			if(uPage.node[b]!=null){
    				validNodeCount++;
    	        	int blockWidth = getWidth()-leftColumnWidth;
    	        	int leftPos = blockWidth-blockWidth/powerOf2(b);
    	        	int rightPos;
    	        	if(b == height-1)
    	        		rightPos = blockWidth;
    	        	else
    	        		rightPos = blockWidth-blockWidth/powerOf2(b+1);
    	        	g2d.setColor(new Color(240,240,240));
    	    		g2d.fillRect(leftPos, pageHeight*a, rightPos-leftPos, pageHeight);
    	        	g2d.setColor(new Color(0,0,0));
    	    		gNode uNode = uPage.node[b];
    	    		int elementWidth = (rightPos-leftPos)/(uNode.maxKeyNumber*2+1);
    	    		uNode.pointerWidth = elementWidth;
    	    		int c = 0;
    	    		while(c<uNode.maxKeyNumber+1){
    	    			//pointers (green rectangles)
    	    			if(uNode.pointer[c]>0){
    	    				uNode.pointerPos[c] = leftPos+elementWidth*c*2;
    	    				g2d.setColor(new Color(120,240,120));
    	    				g2d.fillRect(leftPos+elementWidth*c*2, pageHeight*a, elementWidth, pageHeight);
    	    				g2d.setColor(new Color(0,0,0));
    	    				g2d.drawRect(leftPos+elementWidth*c*2, pageHeight*a, elementWidth, pageHeight);
    	    			}
    	    			//keys (yellow rectangles)
    	    			if(c<uNode.maxKeyNumber && uNode.key[c]!=null){
    	    				g2d.setColor(new Color(240,240,120));
    	    				g2d.fillRect(leftPos+elementWidth*(c*2+1), pageHeight*a, elementWidth, pageHeight);
    	    				g2d.setColor(new Color(0,0,0));
    	    				g2d.drawRect(leftPos+elementWidth*(c*2+1), pageHeight*a, elementWidth, pageHeight);
    	    				
    	    			}
    	    			//inner node value
    	    			if(c<uNode.maxKeyNumber && uNode.level>1){
    	    				g2d.setColor(new Color(200,200,240));
    	    				g2d.fillRect(leftPos+elementWidth*c*2, pageHeight*a, elementWidth*2, 10);
    	    				g2d.setColor(new Color(0,0,0));
    	    				g2d.drawRect(leftPos+elementWidth*c*2, pageHeight*a, elementWidth*2, 10);
    	    			}
    	    			
    	    			c++;
    	    		}
    	    		c = 0;
    	    		while(c<uNode.maxKeyNumber+1){
    	    			//number of a page where the pointer points
    	    			if(uNode.pointer[c]>0){
    	    				if(uNode.level>1)
    	    				g2d.drawString(Integer.toString(getPageNumberFromAddres(uNode.pointer[c])), leftPos+elementWidth*(c*2)+3, pageHeight*a+(pageHeight-5));
    	    			}
    	    			//key value
    	    			if(c<uNode.maxKeyNumber && uNode.key[c]!=null){
    	    				g2d.drawString(uNode.key[c].toString(), leftPos+elementWidth*(c*2+1)+3, pageHeight*a+(pageHeight-18));
    	    			}
    	    			//inner node value
    	    			if(c<uNode.maxKeyNumber && uNode.level>1){
    	    				System.out.println("Numer: "+uNode.values[c]);
    	    				g2d.drawString(Integer.toString(uNode.values[c]), leftPos+elementWidth*(c*2)+3, pageHeight*a+10);
    	    			}
    	    			
    	    			c++;
    	    		}
    	        	g2d.setStroke(new BasicStroke(2));
    	    		g2d.drawRect(leftPos, pageHeight*a, rightPos-leftPos, pageHeight);
    	        	g2d.setStroke(new BasicStroke(1));
    			}
    			b++;
    		}
    		if(validNodeCount==0){
    			g2d.drawString("Unused page", 3, pageHeight*a+(pageHeight-5));
    		}
    		//index number of page
    		if(!clearLayout){
    			g2d.drawString(Integer.toString(a), 3-leftColumnWidth, pageHeight*(a+1));
    		} else {
    			g2d.drawString(Integer.toString(uPage.number), 3-leftColumnWidth, pageHeight*(a+1));
    		}
    		a++;
    	}
    }
	
	GraphicsPanel(String name,JFrame jf){
		frame = jf;
		init(name);
		//reloading during mouse move
		addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent arg0) {
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {

	    		xx=e.getX();
	    		yy=e.getY()+leftColumnWidth;
	    		revalidate();
	    		repaint();
			}
			
		});
	}
	/**
	 * loading data from chosen file
	 * @param Name of default path.
	 */
	public void init(String name){
		
		try {
			//opens tree file
			file = new File(name);
			raFile = new RandomAccessFile(file, "r");
			headerSize = (long)(Long.BYTES+Integer.BYTES*3);
			
			//reads header
			rootPage = raFile.readLong();
			pageSize = raFile.readInt();
			keySize = raFile.readInt();
			height = raFile.readInt();
			
			long fileLength = raFile.length();
			pageCount = 0;
			long a = headerSize;
			while(a<fileLength){
				pageCount++;
				a+=pageSize;
			}
			//Makes arrays of pages represented in RAM
			pageList = new ArrayList<gPage>();
			clearLayoutPageList = new ArrayList<gPage>();
			int b = 0;
			while(b<pageCount){
				long pageAddr = headerSize+b*pageSize;
				pageList.add(new gPage(height,pageAddr));
				b++;
			}
			//checks for valid nodes (starting from root)
			getNodeFromPage(rootPage,height);
			System.out.println(rootPage+" "+pageSize+" "+keySize+" "+height);
			revalidate();
			repaint();
		} catch(EOFException e){
			System.out.println("EOFException");
		} catch(IOException e){
			System.out.println("IOException");
		}
	}
	/**Auxiliary method. Used to find index of page
	 * 
	 * @param arg address of page
	 * @return index of page
	 */
	private int getPageNumberFromAddres(long arg){
		return (int)((arg-headerSize)/pageSize);
	}
	
	/*Code below is copied from TreeFile from the mu-Tree project*/
	
    //Starts from root node, then searches all valid nodes recursively
	/**
	 * Method copied from TreeFile from the mu-Tree project
	 * gets root node and then gets all used nodes recursively
	 * @param pAddr adders of page containing node
	 * @param level level of desired node
	 * @throws IOException
	 */
	public void getNodeFromPage(long pAddr,int level) throws IOException
	{
		gNode<TKey> node;
		long readPlace;
		int elementCount = 0; //checks if node isn't empty
		
		int maxKeyNumber = maxKeyCount(level);
		if(level==this.height){
			readPlace = pAddr;
		} else {
			readPlace = pAddr+nodeSize(level);
		}

		node = new gNode<TKey>(pAddr,level,maxKeyNumber);
		
		//get pointers
		raFile.seek(readPlace);
		int a = 0;
		while(a<maxKeyNumber+1){
			try {
				elementCount = 1;
				raFile.seek(readPlace+a*Long.BYTES);
				
				node.setPointer(a, raFile.readLong());
			} catch(IOException e){
			}
			a++;
		}
		//get keys
		a = 0;
		int keyCount = 0;
		while(a<maxKeyNumber){
			try {
				elementCount = 1;
				long pos = readPlace+Long.BYTES*(maxKeyNumber+1)+a*keySize;
				node.setKey(a, readObj(pos));

			} catch(ClassNotFoundException e){
				e.printStackTrace();
			} catch(EOFException e)
			{
				e.printStackTrace();
			}
			a++;
		}
		//get inner node values
		if(level > 1){

			raFile.seek(readPlace + Long.BYTES * (maxKeyNumber + 1) + maxKeyNumber * keySize);
			for(a=0; a < maxKeyNumber; a++)
			{
				int b = raFile.readInt();
				node.setValue(a,b);
				if(b>0)
				System.out.println(b);
			}
		}
		//puts node into one of pages
		int pagePlace = getPageNumberFromAddres(pAddr);
		if(elementCount>0 && pagePlace>-1 && pagePlace<pageCount){
			pageList.get(pagePlace).setNode(node,level);
			gPage usedPage = pageList.get(pagePlace);
			if(!clearLayoutPageList.contains(usedPage)){
				clearLayoutPageList.add(pageList.get(pagePlace));
			} else {
				clearLayoutPageList.remove(pageList.get(pagePlace));
				clearLayoutPageList.add(pageList.get(pagePlace));
			}
			System.out.println("Node "+pAddr+" of level "+level+" read.");
		}
		
	}
	/**
	 * Method copied from TreeFile from the mu-Tree project
	 * Number of keys able to write to a node of certain level
	 * 
	 * @param level Level of the node
	 * @return
	 */public int maxKeyCount(int level)
		{ // level decreased by 1 for root
			int n = 0;
			int size = nodeSize(level);
			
			// For n keys leaf node has n values
			if(level == 1)
				while ( (pointerSize + keySize) * n<= size)
				{
					n++;
				}
			// For n keys inner node has n+1 children and n values
			else
				while (pointerSize * (n + 1) + (keySize + innerValueSize) * n<= size)
				{
					n++;
				}
			
			return n - 1;
		}

	/**
	 * Size of node of a certain level in memory
	 * @param level Level of the node
	 * @return
	 */
	public int nodeSize(int level){ 
		if(level == height)
			level--;
		return pageSize/powerOf2(level);
	}
	
	//auxiliary method
	private int powerOf2(int n){
		int a = 0;
		int b = 1;
		while(a<n){
			b*=2;
			a++;
		}
		return b;
	}
	private TKey readObj(long pos) throws IOException, ClassNotFoundException, EOFException{
		byte[] buf = new byte[keySize];
		raFile.seek(pos);
		raFile.readFully(buf, 0, keySize);
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(buf));

		@SuppressWarnings("unchecked")
		TKey readObject = (TKey) is.readObject();
		return readObject;
	}
	
	/**representation of page in RAM
	 * 
	 * @author Grzegorz Stasiak
	 *
	 */
    class gPage{
    	long addr;
    	gNode[] node;
    	int number;
    	gPage(int height,long pos){
    		node = new gNode[height];
    		addr = pos;
    		number = getPageNumberFromAddres(pos);
    	}
    	public void setNode(gNode insertedNode,int level){
    		node[level-1] = insertedNode;
    	}
    }
	/**representation of node in RAM
	 * 
	 * @author Grzegorz Stasiak
	 *
	 * @param <TKey>
	 */
    class gNode<TKey>{
    	long[] pointer;
    	gNode<TKey>[] pointedNode;
    	Object[] key;
    	long pageAddr;
    	int level;
    	int maxKeyNumber;
    	int pointerWidth;
    	int[] pointerPos;
    	int[] values;
    	gNode(long pAddr,int lvl,int mKNumber){
    		pageAddr = pAddr;
    		level = lvl;
    		maxKeyNumber = mKNumber;
    		pointer = new long[mKNumber+1];
    		pointerPos = new int[mKNumber+1];
    		pointerWidth = 0;
        	values = new int[mKNumber];
    		for(int i = 0;i<=mKNumber;i++){
    			pointer[i] = -1;
    			pointerPos[i] = -100;
    		}
    		key = new Object[mKNumber];
    	}
    	public void setPointer(int pos,long addr) throws IOException{
    		pointer[pos] = addr;
    		if(level>1){
    			getNodeFromPage(addr,level-1);
    		}
    		
    	}
    	public void setValue(int pos,int val) throws IOException{
    		values[pos] = val;
    	}
    	public void setKey(int pos,Object obj){
    		key[pos] = obj;
    	}
    }
}