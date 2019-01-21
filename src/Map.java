/*
	CSCI 240 - Electronic Flight Planning System
	**********************************
	File Name: Map.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description:
	The Map displays the flight route between two or more aiports.

	Date Modified: 04.24.18
*/

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class Map extends JPanel implements ActionListener{

	private ArrayList<Airport> apt; //airport list
	private WorldMap map; //map
	private JCheckBox hideAll=new JCheckBox("Hide All Airport"); //check box for show all airport
	private ArrayList<Airport> totalAPT; //total airport in routine
	private ArrayList<Integer> mainAirportIndex; //main destination index
	private boolean showRoutine=false; //show routine boolean

	public Map(ArrayList<Airport> airport){
	   	apt=airport;//set airport list
	   	setLayout(new BorderLayout());
	   	map=new WorldMap();
	   	map.setFocusable(true); //set map focuable
	   	JScrollPane scrollPane = new JScrollPane(map); //set scroll pane
	 	MouseListener ml = new MouseListener();
	 	scrollPane.addMouseListener(ml);
	    scrollPane.addMouseMotionListener(ml);
		WheelListener wl=new WheelListener();
		scrollPane.addMouseWheelListener(wl);
		add(scrollPane,BorderLayout.CENTER);
		add(new JLabel("<html><font face='TimesRoman' size='3' color='gray'>------  Press '1' For Zooming Out  ------  Press '2' For Zooming In  ------  Mouse Drag or Scroll  ------",SwingConstants.CENTER),BorderLayout.NORTH);
	}

	public Map(ArrayList<Airport> ta, ArrayList<Integer> mai, ArrayList<Airport> airport){
		this(airport); //extends contructor for routine map
		totalAPT=ta;
	  	mainAirportIndex=mai;
		showRoutine=true;
		hideAll.setFocusable(false);
	  	hideAll.addActionListener(new ActionListener(){ //show every airport when check box is clicked
	   		public void actionPerformed(ActionEvent e){
	   			map.calculate();
	   			map.repaint();
	  		}
	    });
	    JPanel keyPanel=new JPanel();
		JButton[] buttons=new JButton[4];
		String[] btnName={"RESTART","PAUSE","PLAY","SPEED UP"};
		keyPanel.add(hideAll);
		for(int i=0;i<4;++i){  //set buttons
		   buttons[i]=new JButton(btnName[i],new ImageIcon("image/"+i+".png"));
		   buttons[i].addActionListener(this);
		   buttons[i].setFocusable(false);
		   keyPanel.add(buttons[i]);
		}
		add(keyPanel,BorderLayout.SOUTH);
		map.routineStart();
	}

	public void update(){
		map.setup();
	}

	public void kill(){
		map.pause(); //stop timer when screen is close
	}

	public void actionPerformed(ActionEvent e){
		switch(e.getActionCommand()){
		   	case "PLAY":
		    	map.playSpeed(400); //set timer speed
		    	break;
		    case "PAUSE":
		    	map.pause(); //make timer stop
		    	break;
		    case "SPEED UP":
		    	map.playSpeed(50); //set timer speed
		    	break;
			case "RESTART":
				map.restart(); //map restart
		}
	}

	private class MouseListener extends MouseAdapter{ //listener for mouse drag
		private Point origin;
		public void mousePressed(MouseEvent e) {
			origin = new Point(e.getPoint()); //get point where mouse pointed when press
			}
			public void mouseDragged(MouseEvent e) {
			if (origin != null) {
			JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, map);
				if (viewPort != null) {
					int deltaX = origin.x - e.getX(); //original x and y  -  x y where mouse pointed
					int deltaY = origin.y -	e.getY();
					Rectangle view = viewPort.getViewRect();
					view.x += deltaX;
					view.y += deltaY;
					map.scrollRectToVisible(view);  //set view point
				}
			}
		}
	}

	private class WheelListener implements MouseWheelListener{ //listener for mouse wheel
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0) { //if mouse scroll up, zoom in map
				map.zoomIn();
			}
			else{   //else zoom out
			 	map.zoomOut();
			}
		}
	}

	private class WorldMap extends JPanel{
		private Image img; //image
		private int x[]; //x and y pixel coordinate
		private int y[];
		private int xn[]; //x and y for all airport
		private int yn[];


		private int w,h; //width and heigh of the image
		private double zoom; //zooming size

		private int meX; //x of your location
		private int meY; //y of your location

		private int index;//index of airport
		private int run; //number of pixel move per time
		private Timer timer;

		private int routineSize; //routine airport size
		private int aptSize;

		private WorldMap(){
		  	setLayout(null);
			img =new ImageIcon("image/map.png").getImage();
			zoom=0.6;
			addKeyListener(new MyKeyListener());
			setup();
		}

		private void setup(){   //set up the size of all airports and its x and y
			aptSize=apt.size();//set all airport
			xn=new int[aptSize];//get x and y for all airport lat and lon
	    	yn=new int[aptSize];
			doStuff();
			repaint();
		}

	   	private void routineStart(){ //start routine planning when calling this method
	   		index=0;
	   		run=0;
	   		routineSize=totalAPT.size();
	    	x=new int[routineSize]; //x and y coordinate for all airport
	    	y=new int[routineSize];
	    	doStuff();
	    	timer=new Timer(400,new MyTimer());
	     	timer.start();
	     	repaint();
	   	}

    	private void findLinearLine(){  //find linear line
    		if((x[index]-10)<=x[index+1]&& x[index+1]<=(x[index]+10)){ //if slope is infinity
         		double m=((double)(x[index+1]-x[index])/(double)(y[index+1]-y[index]));
	         	double b=x[index]-(m*y[index]);
				meY=y[index]+run;
				meX=(int)((m*meY)+b);
         	}
    		else{
	    		double m=((double)(y[index+1]-y[index])/(double)(x[index+1]-x[index]));
	         	double b=y[index]-(m*x[index]);
	       		meX=x[index]+run;
	       		meY=(int)((m*meX)+b);
		      	}
	    	}

		private void doStuff(){//get width and height
	       	w=(int)(img.getWidth(null)*zoom);
           	h=(int)(img.getHeight(null)*zoom);
     		setPreferredSize(new Dimension(w,h)); //set zooming size
     		calculate(); //convert lat and lon to x and y pixel
     		if(showRoutine){
     			findLinearLine();//find linear line
     		}
		}

		private void calculate(){
			if(showRoutine){
			   	for(int i=0; i<routineSize;++i){ //get all x y pixel for all airport in routine
			    	x[i] = (int)((w/360.0)*totalAPT.get(i).getLon()+(w/2.0));
					y[i] =  (int)((h/180.0)*-totalAPT.get(i).getLat()+(h/2.0));
			    }
			};

		    if(!hideAll.isSelected()){
				for(int i=0; i<aptSize;++i){ //get all x y pixel for all airport
	     			xn[i] = (int)((w/360.0)*apt.get(i).getLon()+(w/2.0));
					yn[i] =  (int)((h/180.0)*-apt.get(i).getLat()+(h/2.0));
			  	}
		   	}
		}

		private void pause(){
			timer.stop();
		}

		private void restart(){
			index=0; //set index to 0
			run=0; //slope to default
			meX=x[index]; //meX to 0
			meY=y[index];
			repaint();
		}

		private void playSpeed(double time){
			timer.start();
			timer.setDelay((int)(time));
		}

		private void zoomIn(){//zoom in
			if(zoom<2.58){
			   	zoom*=1.2;
		       	doStuff();
		       	revalidate();
	            repaint();
	        }
		}

		private void zoomOut(){ //zoom out
			if(zoom>0.6){
	          	zoom/=1.2;
	            doStuff();
	            revalidate();
	            repaint();
	        }
		}

		private class MyKeyListener extends KeyAdapter{
			public void keyPressed(KeyEvent e) {
		   		super.keyPressed(e);
		        int key = e.getKeyCode();
	            if(key==KeyEvent.VK_2){ //press 2 for zoom in
	            	zoomIn();
	            }
	            else if(key== KeyEvent.VK_1){ //press 1 for zoom out
	            	zoomOut();
	            }
	    	}
		}

		private class MyTimer implements ActionListener{ //calculation
			public void actionPerformed(ActionEvent e){
				boolean reach=false; //running
				{ //test if x1 and x2 if same or less or greater than, decrease and increase linear line slope by this
					if((x[index]+10)<x[index+1]){
						run++;
					reach=meX>=x[index+1];
					}
					else if((x[index]-10)>x[index+1]){
						run--;
						reach=meX<=x[index+1];
					}
					else{
						if(y[index]<y[index+1]){
							run++;
							reach=meY>=y[index+1];
						}
						else{
							run--;
							reach=meY<=y[index+1];
						}
					}
				}

				if(reach){ //when you reach the destination airport index plus one, if index if at max then restart
					index++;
					if(index>=routineSize-1){
						index=0;
					}
					run=0;
					meX=x[index];
				}
			    findLinearLine();
			    repaint();
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
	    	g.drawImage(img,0,0,w,h,null);
			if(!hideAll.isSelected()){ //draw all airport
		    	for(int i=0;i<aptSize;++i){
					g.fillOval(xn[i]-3,yn[i]-3,6,6);
					g.drawString(apt.get(i).getID(),xn[i]+6,yn[i]+6);
		    	}
			}
			if(showRoutine){
		    	g.setColor(Color.BLUE); //draw first airport
		    	g.fillOval(x[0]-4,y[0]-4,8,8);
				g.drawString(totalAPT.get(0).getID()+" (Start)",x[0]+6,y[0]+6);
		    	for(int i=1;i<routineSize;++i){
		    		if(mainAirportIndex.contains(i)){ //blue for all destination
		    			g.setColor(Color.BLUE);
		    			g.fillOval(x[i]-4,y[i]-4,8,8);
						g.drawString(totalAPT.get(i).getID()+" (D."+i+")",x[i]+6,y[i]+6);
		    		}
		    		else{ //red for all stops
		    			g.setColor(Color.RED);
						g.fillOval(x[i]-4,y[i]-4,8,8);
						g.drawString(totalAPT.get(i).getID()+" (S."+i+")",x[i]+6,y[i]+6);
		    		}
		    	}

			   	g.drawPolyline(x,y,routineSize); //connect line
		    	g.setColor(Color.GREEN); //green for you
		    	g.fillOval(meX-3,meY-3,6,6);
		    	g.drawString(">O<",meX-3,meY-6);
			}
		}
	}
}