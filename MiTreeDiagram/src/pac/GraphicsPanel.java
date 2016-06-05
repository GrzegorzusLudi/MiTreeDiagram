package pac;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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


public class GraphicsPanel<TKey extends Comparable<TKey> & Serializable, TValue extends Serializable> extends JPanel {

	File file;
	public RandomAccessFile raFile;
	int pointerSize = Long.BYTES;
	
	long rootPage;
	int pageSize;
	public int keySize;
	int height;
	
	long headerSize;      //size of header of the file
	int keyPointerCount;
	int pageCount = 0;
	public boolean clearLayout = false;
	List<gPage> pageList;
	List<gPage> clearLayoutPageList;
	JFrame frame;

	//painting
    public void paint(Graphics g) {   

		if(!clearLayout){
			setPreferredSize(new Dimension(frame.getWidth(),pageCount*30));
		} else {
			setPreferredSize(new Dimension(frame.getWidth(),clearLayoutPageList.size()*30));
		}
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setColor(new Color(245,245,245));
		g2d.fillRect(0, 0, getWidth(), getHeight());
    	g2d.translate(30,0);
    	int a = 0;
    	while((a<pageCount && !clearLayout) || (clearLayout && a<clearLayoutPageList.size())){
    		gPage uPage;
    		if(!clearLayout){
    			uPage = pageList.get(a);
    		} else {
    			uPage = clearLayoutPageList.get(a);
    		}
        	g2d.setStroke(new BasicStroke(2));
        	g2d.setColor(new Color(200,200,200));
    		g2d.fillRect(0, 30*a, getWidth(), 30);
        	g2d.setColor(new Color(0,0,0));
    		g2d.drawRect(0, 30*a, getWidth(), 30);
    		
        	g2d.setColor(new Color(255,255,255));
    		g2d.fillRect(-30, 30*a, 30, 30);
        	g2d.setColor(new Color(0,0,0));
    		g2d.drawRect(-30, 30*a, 30, 30);

        	g2d.setStroke(new BasicStroke(1));

    		if(!clearLayout){
    			g2d.drawString(Integer.toString(a), 3-30, 30*(a+1));
    		} else {
    			g2d.drawString(Integer.toString(uPage.number), 3-30, 30*(a+1));
    		}
			int validNodeCount = 0;
    		int b = 0;
    		while(b<height){
    			if(uPage.node[b]!=null){
    				validNodeCount++;
    	        	int blockWidth = getWidth()-30;
    	        	int leftPos = blockWidth-blockWidth/powerOf2(b);
    	        	int rightPos;
    	        	if(b == height-1)
    	        		rightPos = blockWidth;
    	        	else
    	        		rightPos = blockWidth-blockWidth/powerOf2(b+1);
    	        	g2d.setColor(new Color(240,240,240));
    	    		g2d.fillRect(leftPos, 30*a, rightPos-leftPos, 30);
    	        	g2d.setColor(new Color(0,0,0));
    	    		gNode uNode = uPage.node[b];
    	    		int elementWidth = (rightPos-leftPos)/(uNode.maxKeyNumber*2+1);
    	    		int c = 0;
    	    		while(c<uNode.maxKeyNumber+1){
    	    			if(uNode.pointer[c]>0){
    	    				g2d.setColor(new Color(120,240,120));
    	    				g2d.fillRect(leftPos+elementWidth*c*2, 30*a, elementWidth, 30);
    	    				g2d.setColor(new Color(0,0,0));
    	    				g2d.drawRect(leftPos+elementWidth*c*2, 30*a, elementWidth, 30);
    	    				if(uNode.level>1)
    	    				g2d.drawString(Integer.toString(getPageNumberFromAddres(uNode.pointer[c])), leftPos+elementWidth*(c*2)+3, 30*a+25);
    	    			}
    	    			if(c<uNode.maxKeyNumber && uNode.key[c]!=null){
    	    				g2d.setColor(new Color(240,240,120));
    	    				g2d.fillRect(leftPos+elementWidth*(c*2+1), 30*a, elementWidth, 30);
    	    				g2d.setColor(new Color(0,0,0));
    	    				g2d.drawRect(leftPos+elementWidth*(c*2+1), 30*a, elementWidth, 30);
    	    				
    	    				g2d.drawString(uNode.key[c].toString(), leftPos+elementWidth*(c*2+1)+3, 30*a+20);
    	    			}
    	    			
    	    			c++;
    	    		}
    	        	g2d.setStroke(new BasicStroke(2));
    	    		g2d.drawRect(leftPos, 30*a, rightPos-leftPos, 30);
    	        	g2d.setStroke(new BasicStroke(1));
    			}
    			if(validNodeCount==0){
    				g2d.drawString("Unused page", 3, 30*a+20);
    			}
    			b++;
    		}
    		a++;
    	}
    }
	
	GraphicsPanel(String name,JFrame jf){
		frame = jf;
		init(name);
	}
	public void init(String name){
		
		try {
			file = new File(name);
			raFile = new RandomAccessFile(file, "r");
			headerSize = (long)(Long.BYTES+Integer.BYTES*3);
			//writing header
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
			pageList = new ArrayList<gPage>();
			clearLayoutPageList = new ArrayList<gPage>();
			int b = 0;
			while(b<pageCount){
				long pageAddr = headerSize+b*pageSize;
				pageList.add(new gPage(height,pageAddr));
				System.out.println("hehe "+pageAddr);
				b++;
			}
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
	//auxiliary methods
	private long getAddresFromPageNumber(int arg){
		return ((long)arg)*pageSize+headerSize;
	}
	private int getPageNumberFromAddres(long arg){
		return (int)((arg-headerSize)/pageSize);
	}
	
	/*Code below is copied from TreeFile from the mu-Tree project*/
	
    //Starts from root node, then searches all valid nodes recursively
	public void getNodeFromPage(long pAddr,int level) throws IOException
	{
		gNode<TKey,TValue> node;
		long writePlace;
		int elementCount = 0; //checks if node isn't empty
		
		int maxKeyNumber = maxKeyNumber(level);
		if(level==this.height){
			writePlace = pAddr;
		} else {
			writePlace = pAddr+nodeSize(level);
		}

		node = new gNode<TKey,TValue>(pAddr,level,maxKeyNumber);
		raFile.seek(writePlace);
		int a = 0;
		while(a<maxKeyNumber+1){
			try {
				elementCount = 1;
				raFile.seek(writePlace+a*Long.BYTES);
				
				node.setPointer(a, raFile.readLong());
			} catch(IOException e){
			}
			a++;
		}
		a = 0;
		int keyCount = 0;
		while(a<maxKeyNumber){
			try {
				elementCount = 1;
				long pos = writePlace+Long.BYTES*(maxKeyNumber+1)+a*keySize;
				node.setKey(a, readObj(pos));

			} catch(ClassNotFoundException e){
				e.printStackTrace();
			} catch(EOFException e)
			{
				e.printStackTrace();
			}
			a++;
		}
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
	public int maxKeyNumber(int level){ 
		int n = 0;
		int size = nodeSize(level);
		while(pointerSize*(n+1)+keySize*n<=size){
			n++;
		}
		return n-1;
	}

	public int nodeSize(int level){ 
		if(level == height)
			level--;
		return pageSize/powerOf2(level);
	}
	
	private int powerOf2(int n){
		int a = 0;
		int b = 1;
		while(a<n){
			b*=2;
			a++;
		}
		return b;
	}
	@SuppressWarnings("unchecked")
	private TKey readObj(long pos) throws IOException, ClassNotFoundException, EOFException{
		TKey readObject;
		try {
	    byte[] buf = new byte[keySize];
	    raFile.seek(pos);
	    raFile.readFully(buf, 0, keySize);
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(buf));
		
		readObject = (TKey)is.readObject();
		} catch(EOFException e){
			readObject = null;
		}
		return readObject;
	}
    
    public boolean isClearLayout() {
		return clearLayout;
	}

	public void setClearLayout(boolean clearLayout) {
		this.clearLayout = clearLayout;
	}

	//representation of page in RAM
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
	//representation of node in RAM
    class gNode<TKey,TValue>{
    	long[] pointer;
    	gNode<TKey,TValue>[] pointedNode;
    	Object[] key;
    	long pageAddr;
    	int level;
    	int maxKeyNumber;
    	gNode(long pAddr,int lvl,int mKNumber){
    		pageAddr = pAddr;
    		level = lvl;
    		maxKeyNumber = mKNumber;
    		pointer = new long[mKNumber+1];
    		for(int i = 0;i<=mKNumber;i++){
    			pointer[i] = -1;
    		}
    		key = new Object[mKNumber];
    	}
    	public void setPointer(int pos,long addr) throws IOException{
    		pointer[pos] = addr;
    		if(level>1){
    			getNodeFromPage(addr,level-1);
    		}
    		
    	}
    	public void setKey(int pos,Object obj){
    		key[pos] = obj;
    	}
    }
}
