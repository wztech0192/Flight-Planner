/*
	CSCI 240 - Electronic Flight Planning System
	**********************************
	File Name: Planner.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description:
	The Planner class calculates and displays the flight routes between two or more airports.

	Date Modified: 04.24.18
*/


import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.text.*;

@SuppressWarnings("serial")
public class Planner extends JFrame implements ActionListener{

	private JDialog mapScreen; //viewing map
	private final static DecimalFormat df = new DecimalFormat("0.00"); //round to hundredth place
   	private final static int num=2;
   	private final AirportManager aptManager;
   	private ArrayList<Airport> apt;
	private int start=-1;   //index of starting airport from airport list
	private int destn=-1; //index of destination airport
	private ArrayList<Integer> multiDestn=new ArrayList<Integer>(); //indexes of multiple destinations
	private JTextField[] input=new JTextField[3];//starting, destination, and destination+ input field
	private final static JLabel des=new JLabel("<html><div style='text-align: center'><font face='Serif' size='9' color='gray'>Electronic Flight Planning System<br><font face='Serif' size='5' color='gray'>A System That Contains An Airport Manager To Edit, Add, and Delete Any Airport Data And Its Belongings And <br> A Routine Planner That Calculates And Displays The Detailed Information Of The Trip Within A Map</div></html>");
	private final static Font font = new Font("TimesRoman", Font.BOLD+Font.ITALIC, 10);
	private final static Font btnFont=new Font("Arial", Font.BOLD, 16);
	private final static String[] lblAPT={"Starting Airport","Destination Airport",""};
	private MultiDestnPanel mdnScreen;

	//constructor that add components and receive airport data
	public Planner(){
		setLayout(new BorderLayout());

		aptManager=new AirportManager(this);
		apt=aptManager.getAirport();


        JPanel btnPanel= new JPanel();
        final JButton btnClear=new JButton("Clear All");
        final JButton btnCal=new JButton("Make Plan");
		final JButton btnMap=new JButton("View Map");
        final JButton btnExit=new JButton("Save/Exit");
        Color color = Color.RED.darker();
        btnCal.setToolTipText("Select 'Make Plan' to start the flight plan, after entering a valid starting airport and destination(s).");
        btnCal.setFont(btnFont);
		btnCal.setBackground(color);
		btnCal.setForeground(Color.white);
        btnCal.addActionListener(this);
        btnClear.setToolTipText("Select 'Clear All' to clear all of the currently entered airport data within the Routine Planner.");
       	btnClear.addActionListener(this);
       	btnClear.setFont(btnFont);
       	btnMap.setToolTipText("Select 'View Map' to view a map of the world and current airports.");
       	btnMap.setFont(btnFont);
       	btnMap.addActionListener(this);
       	color = Color.GREEN.darker();
       	btnExit.setToolTipText("Select 'Save/Exit' to save and exit the flight plan.");
       	btnExit.setFont(btnFont);
		btnExit.setBackground(color);
		btnExit.setForeground(Color.white);
       	btnExit.addActionListener(this);

       	btnPanel.setLayout(new GridLayout(4,1,10,10));
       	btnCal.setPreferredSize(new Dimension(120,70));
       	btnPanel.add(btnCal);
       	btnPanel.add(btnClear);
       	btnPanel.add(btnMap);
       	btnPanel.add(btnExit);

    	JPanel show=new JPanel();
    	show.setLayout(new BoxLayout(show,BoxLayout.Y_AXIS));
    	show.add(new InputPanel(0));
    	show.add(new JLabel("TO"));
    	show.add(new InputPanel(1));
    	mdnScreen=new MultiDestnPanel();
    	show.add(mdnScreen);

    	JScrollPane js=new JScrollPane(show);
    	js.setPreferredSize(new Dimension(300,310));


    	JPanel description=new JPanel(new GridLayout(2,1));
    	 MatteBorder matte = new MatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY);
        des.setBorder(matte);
        des.setHorizontalTextPosition(JLabel.CENTER);
       	des.setVerticalTextPosition(JLabel.TOP);
        des.setFont(font);
        des.setOpaque(true);
        des.setBackground(Color.CYAN);
		description.add(des);
    	description.add(new JLabel(new ImageIcon("image/airplane.png")));
    	description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    	JPanel routinePlanner=new JPanel();
    	routinePlanner.add(js,BorderLayout.CENTER);
    	routinePlanner.add(btnPanel,BorderLayout.EAST);
    	routinePlanner.setBorder(new TitledBorder("Routine Planner"));

    	aptManager.setPreferredSize(new Dimension(380,300));
    	aptManager.setBorder(new TitledBorder("Airport Manager"));

    	add(description,BorderLayout.NORTH);
    	add(aptManager,BorderLayout.WEST);
    	add(routinePlanner,BorderLayout.CENTER);

		Map m=new Map(apt);
		aptManager.setMap(m);
    	mapScreen=new JDialog(); //map dialog set up
    	mapScreen.add(m);
    	mapScreen.setTitle("World Map");
    	mapScreen.pack();
    	mapScreen.setLocationRelativeTo(null);
    	mapScreen.setResizable(false);
    	mapScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

	//display
    public void getScreen(){

    	//A Window Listener opens a confirmation screen when the user selects X in the upper right-hand corner of the screen, verifying the user wishes to exit the System without saving any data.
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				int confirm = JOptionPane.showConfirmDialog(null,"ARE YOU CERTAIN YOU WANT TO QUIT WITHOUT SAVING THE DATA?","CONFIRM" ,JOptionPane.YES_NO_OPTION);
			 		if (confirm == JOptionPane.YES_OPTION){
	      			System.exit(0);
				}
			}
		});
		setTitle("Electronic Flight Planner System");
    	pack();
    	setResizable(false);
    	setLocationRelativeTo(null);
    	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	setVisible(true);
    }

	//Screen for adding multiple destination
	private class MultiDestnPanel extends JPanel{
		private JPanel infoPanel= new JPanel();
		private ArrayList<JLabel> lblInfo=new ArrayList<JLabel>(); //list of airports added in destination
		private MultiDestnPanel(){
			JPanel btnPanel=new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
			final JButton btnAddDestn=new JButton("Add Destination");
			final JButton btnDeleteDestn=new JButton("Delete Destination");
			btnAddDestn.addActionListener(Planner.this);
			btnAddDestn.setToolTipText("Select 'Add Destination' after entering a valid additional airport in the text panel above. Multiple airports may be added.");
			btnDeleteDestn.addActionListener(Planner.this);
			btnDeleteDestn.setToolTipText("Select 'Delete Destination' if you wish to delete the most recently added additional airports.");
			setLayout(new BorderLayout());
			btnPanel.add(btnAddDestn);
			btnPanel.add(btnDeleteDestn);
			infoPanel.setBorder(new TitledBorder("Destination +"));
			btnPanel.setBackground(Color.LIGHT_GRAY);
			InputPanel in=new InputPanel(num);


			in.add(btnPanel);
			input[num].setText("Ignore If Only One Destination");
        	input[num].addMouseListener(new MouseAdapter(){ //delete text when click

           	public void mouseClicked(MouseEvent e){
            	    input[num].setText("");
            	}
        	});

			add(in, BorderLayout.CENTER);
			add(infoPanel, BorderLayout.NORTH);
		}

		private void clearAll(){ //remove everything
			infoPanel.removeAll();
			lblInfo.clear();
			revalidate();
			repaint();
		}

		private void deleteInfo(){
			if(!multiDestn.isEmpty()){ //remove last destination
   				multiDestn.remove(multiDestn.size()-1);
				infoPanel.remove(lblInfo.get(lblInfo.size()-1));
				lblInfo.remove(lblInfo.size()-1);
				revalidate();
				repaint();
			}
   			else JOptionPane.showMessageDialog(this, "Already Empty");
		}

		private void addInfo(){ //add new destination
			int index=aptManager.search(input[num].getText()); //convert input into index
   			if(index>=0){ // index = -1 means no matching airport
				boolean pass=true;
				int multiDestnSize=multiDestn.size();
				if(multiDestnSize>0){
					if(index==multiDestn.get(multiDestnSize-1)){ //if last destination is same as its departing airport
						pass=false;
					}
				}
				else{ //if first destination+ is same as its departing
					if(index==aptManager.search(input[1].getText())){
						pass=false;
					}
				}
				if(pass){
					multiDestn.add(index); //add index to multiple destination list
					lblInfo.add(new JLabel("Destination #"+(multiDestnSize+1)+": "+apt.get(index).getID()+" - "+apt.get(index).getLoc()));
					infoPanel.add(lblInfo.get(lblInfo.size()-1));
					revalidate();
					repaint();
					}
				else 	JOptionPane.showMessageDialog(this,"The Destination Airport cannot be the same as the Departing Airport.");
   			}
   			else  JOptionPane.showMessageDialog(this,"No Matching Airports.");
		}
	}

	//input field and view button for selecting airport
	private class InputPanel extends JPanel{
    	private	InputPanel(int i){
    		input[i]=new JTextField();
    		input[i].setToolTipText("Enter Airport Identifier or Location [Ex: 'CAE' or 'Columbia, SC']");
    		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    		setBorder(new TitledBorder(lblAPT[i]));
    		add(input[i]);
    		setBackground(Color.LIGHT_GRAY);
    	}
    }

	//check start and destination 1 and destination 2 if exists
	private boolean checkBoth(String input1, String input2){
		boolean pass=true;
		start=aptManager.search(input1); //convert input into index
		destn=aptManager.search(input2); //^^^
		if(start<0 && destn<0){//check if start and destination is valid
			JOptionPane.showMessageDialog(this,"CANNOT IDENTIFY THE STARTING AIRPORT AND DESTINATION AIRPORT");
			return false;
		}
		else if(destn<0){
			JOptionPane.showMessageDialog(this,"CANNOT IDENTIFY THE DESTINATION AIRPORT");
			return false;
		}
		else if(start<0){
			JOptionPane.showMessageDialog(this,"CANNOT IDENTIFY THE STARTING AIRPORT");
			return false;
		}
  		else if(start==destn){ //check if start and destination is same
  			JOptionPane.showMessageDialog(this,"THE STARTING AIRPORT AND DESTINATION AIRPORT CANNOT BE THE SAME");
  			return false;
  		}
  		else if(!multiDestn.isEmpty()){
  			if(destn==multiDestn.get(0)){ //if multiple destination exist, check if destination 1 and destination 2 is same
  					JOptionPane.showMessageDialog(this,"The Second Destination Airport cannot be the same as its Departing Airport");
  				return false;
  			}
  		}
  		return pass;
    }

	public void clearAll(){
		start=-1;
		destn=-1;
		input[0].setText("");
		input[1].setText("");
		input[2].setText("");
		multiDestn.clear();
		mdnScreen.clearAll();
	}

	//all button function
   	public void actionPerformed(ActionEvent e){
   	 	switch(e.getActionCommand()){
   	 		case "View Map":
				mapScreen.setVisible(true);
   	 			break;
    		case "Add Destination":
    			mdnScreen.addInfo(); //add destination+
    			input[2].setText("");
    			break;
    		case "Delete Destination": //delete destination
    			mdnScreen.deleteInfo();
    			break;
    		case "Make Plan": //start planning
				if(checkBoth(input[0].getText(),input[1].getText())){ //check inputs
					if(apt.get(start).getOwns()>0){ //check starting airport's airplane number
						new Calculator();
					}
					else JOptionPane.showMessageDialog(Planner.this,"No Airplane Found Within the Departing Airport");
				 }
		   	 	start=-1; //refresh data
				destn=-1;
 				break;
 			case "Save/Exit": 	aptManager.saveData();
 								System.exit(0);
				break;
			case "Clear All": //clear all data
   				int confirm = JOptionPane.showConfirmDialog(this,"ARE YOU CERTAIN YOU WANT TO CLEAR ALL THE INPUT?" ,"CONFIRM" ,JOptionPane.YES_NO_OPTION);
  					if (confirm == JOptionPane.YES_OPTION){
       					clearAll();
					}
   				break;
   	 	}
   	 	   
  	}

	//Calculating results base on all input airports
  	private class Calculator{
  		private double dist; //distance
  		private double header;	//Header

		private Airport startAPT, destnAPT;
		private Airplane usePlane;
		private double fuelTank;

		private JPanel collections;//multiple screen of collections
		private ArrayList<SelectionPanel> selected = new ArrayList<SelectionPanel>(); //List of panel of airplane selection between each airport
		private ArrayList<Integer> apnChoices=new ArrayList<Integer>(); //list of chosen planes
		private	TitledBorder title;
		private DisplayPanel display=new DisplayPanel(); //display screen

		private ArrayList<Airport> totalAPT=new ArrayList<Airport>(); //airports included start, destn, and refuel stops, for mapping
		private ArrayList<Integer> mainAirportIndex=new ArrayList<Integer>(); //destination airport index, for mapping

		private ArrayList<Airport> stopAPT=new ArrayList<Airport>(); //refuel stop airprots
		private ArrayList<Double> stopAPTDist=new ArrayList<Double>(); //distance between depart and stop airport
		private ArrayList<String> deadEnd=new ArrayList<String>(); //when refuel stopping routine reach a dead end, find other path and ignore this dead end

		private ArrayList<JPanel> stopInfoPanel=new ArrayList<JPanel>(); //stop info panel
		private double firstHeading;//first heading direction
		private double totalRefuel;//total refuel
		private double lastTank;//tank when reaching destination

  		private Calculator(){
  			collections=new JPanel(new CardLayout()); //use cardlayout for airplane selection screen and summary screen
  			startAPT=apt.get(start);
  			destnAPT=apt.get(destn);
  			addDepartPlane(startAPT,destnAPT.getID(),1); //first plane selection, the first selection screen will not have "continues previous plane" option
  			if(!multiDestn.isEmpty()){ // do this if there are more destination
  				addDepartPlane(destnAPT,apt.get(multiDestn.get(0)).getID(), 2); //plane selection from destination 1 to destination 2
  				for(int i=1;i<multiDestn.size();++i){
  					addDepartPlane(apt.get(multiDestn.get(i-1)),apt.get(multiDestn.get(i)).getID(), i+2); //plane selection from destination n to destination n+1
  				}
  			}
			display.add(collections,BorderLayout.CENTER); //add collections to display panel
  			int showWindow;
  			boolean pass=false;
  			while(!pass){
  				apnChoices.clear(); //clear airplane choice
 		 		showWindow=JOptionPane.showConfirmDialog(Planner.this, display, "Airplane Selection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
 		  		if (showWindow == JOptionPane.OK_OPTION){
   					for(SelectionPanel i: selected){
   						apnChoices.add(i.getResult()); //get selected plane and add into airplane choice list
   					}
   					if (!apnChoices.contains(-10)){ //-10 means a selection is empty, without -10 means all selection is selected
    					pass=true;
					}
					else
					 JOptionPane.showMessageDialog(Planner.this,"Please fill all the selection");
   				}
   				else pass=true; //cancel selection will make apnchoice list empty
  			}
  			selected.clear();
  			if(!apnChoices.isEmpty()){ //if apnchoice is not empty start planning
  				makePlan();
  			}
  			else JOptionPane.showMessageDialog(Planner.this,"Plan Canceled");
  		}

  		private void makePlan(){
  			collections.removeAll(); //remove all old selection screen in cardlayout panel
			fuelTank=0;
			totalAPT.add(startAPT); //add starting airport for mapping
			mainAirportIndex.add(0);
			for(int round=0; round<apnChoices.size(); ++round){ //round is base on number of airplanes selected

  				dist=findDistance(startAPT.getLat(),startAPT.getLon(),destnAPT.getLat(),destnAPT.getLon());
  				header=findHeading(startAPT.getLat(),startAPT.getLon(),destnAPT.getLat(),destnAPT.getLon());
  				if(apnChoices.get(round)!=-1){ //-1 means continues use previous plane, reset usePlane if choice is not -1, and reset tank
  					usePlane=startAPT.apn().get(apnChoices.get(round));
  					fuelTank=usePlane.getFuelTank();
  				}

  				double time=dist/usePlane.getMPH();
  				double beforeTank=fuelTank;
  				double fuelUse=usePlane.getBurn()*time;
  				double refuel=0;
				if(usePlane.getMaxRange()<dist){ //check if plane can reach in one stop
					if(startAPT.getFuelType().contains(usePlane.getMatchType())){
						findStop(startAPT,dist,usePlane.getMaxRange()); //if airport can refuel, always max travel range
					}
					else findStop(startAPT,dist,((fuelTank/usePlane.getFuelTank())*usePlane.getMaxRange())); //set max range travel base on current tank if not supporting refuel

	  				if(stopAPT.isEmpty()){   //no refuel stop avaliable in between, stop entire planning
	  					JOptionPane.showMessageDialog(Planner.this, "No Refuel Stop Airport Found Between"+startAPT.getID()+" to "+destnAPT.getID()+" Because No Refuel Stop is Avaliable in Between");
	  					return; //break this method if cannot reach
	  				}
				}
				else{ //if no stop needed because of max travel range, check if need to refuel and check if needed stop because of fuel type difference
	  				if(beforeTank<fuelUse){ //check if needed refuel
	  					if(startAPT.getFuelType().contains(usePlane.getMatchType())){ //check if airport contains plane's fuel type
	  						refuel=usePlane.getFuelTank()-fuelTank;
	  						fuelTank+=refuel;
	  					}
	  					else{  //need refuel stop because of different fuel type
	  						findStop(startAPT,dist,((fuelTank/usePlane.getFuelTank())*usePlane.getMaxRange())); //set max range travel base on current tank if not supporting refuel
			  				if(stopAPT.isEmpty()){
			  					JOptionPane.showMessageDialog(Planner.this, "No Refuel Stop Airport Found Between"+startAPT.getID()+" to "+destnAPT.getID()+" Because Of Fuel Type");
  								return;
			  				}
	  					}
	  				}
				}

				if(stopAPT.isEmpty()){
					fuelTank-=fuelUse;  //if no stop needed, fuel tank minus consume fuel
					mainAirportIndex.add((mainAirportIndex.get(mainAirportIndex.size()-1))+1); //main airport will be previous number +1
					stopInfoPanel.add(null); //if no stop needed, stop panel will be empty
				}
				else {
					mainAirportIndex.add((mainAirportIndex.get(mainAirportIndex.size()-1))+stopAPT.size()); //main airport will be previous number plus # of Stop
					for(int i=0; i<stopAPT.size()-1;++i){ //add all stop to total airport for mapping
			  			totalAPT.add(stopAPT.get(i));
			  		}
			  		dist=0;
					for(double i:stopAPTDist){  //total distance travel
  						dist+=i;
	 				}
				 	time=dist/usePlane.getMPH();
  					fuelUse=usePlane.getBurn()*time;
					stopInfoPanel.add(new StopInfoPane(fuelTank)); //get stop info and pass the current tank
			  		refuel=totalRefuel;  //total fuel
			  		fuelTank=lastTank;  //size of tank when reach destination
					header=firstHeading; //heading direction to first stop
				}

			  	//give info to summary panel and add each summary panel to collection
  				collections.add(new SummaryPane(startAPT.getID(),destnAPT.getID(), dist, header,time,fuelUse, fuelTank, usePlane.getFuelTank(), refuel, beforeTank, usePlane.getModel(), stopAPT.size(), round+1));
				totalAPT.add(destnAPT);
				stopAPT.clear(); //clear stop info for new round
				stopAPTDist.clear();

				try{ //set new depart airport and destination and start a new round
				startAPT=destnAPT;
				destnAPT=apt.get(multiDestn.get(round));
				}
				catch(IndexOutOfBoundsException ex){ //stop in last round
				}
  			}
  			//display all infomation included map
  			mapScreen.dispose();//dispose the old map
  			new ResultDialog().display();
  		}

 	 	private void addDepartPlane(Airport depart,String destination, int round){
  			selected.add(new SelectionPanel(depart, destination, round));//add selectionpanel to selected panel list to get the selected result
		  	collections.add(selected.get(selected.size()-1)); //add selected panel to collections(card layout)
		}

  		private double findDistance(double lat1, double lon1, double lat2, double lon2) {
			double difference = lon1 - lon2;
			return (Math.acos(Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(difference)))*180.0/Math.PI)*60 * 1.1515;
  		}

  		private double findHeading(double lat1, double lon1, double lat2, double lon2){
			double difference= lon2-lon1;
			double y = Math.sin(Math.toRadians(difference))*Math.cos(Math.toRadians(lat2));
			double x = Math.cos(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2))-Math.sin(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.cos(Math.toRadians(difference));

			return ( Math.toDegrees(Math.atan2(y, x)) + 360 ) % 360;
  		}

  		private void findStop(Airport departAPT, double distanceNeed, double maxRange){ //a recursion method to find all stops in between
			int stopIndex=-1;
			double stopDist=0;
			double stopToDestnDistance=999999999; //distance from depart to destination
			double startToStopDistance; //depart to stop distance
			double testDistance;
			String id;

			for(int i=0; i<apt.size();++i){ //test every airport
				//cannot be destination or departing airport or dead end airport;
				id=apt.get(i).getID();
				if(id!=departAPT.getID() && id!=destnAPT.getID() && !deadEnd.contains(id)){
					//test avaliable fuel for this airplane type
					if(apt.get(i).getFuelType().contains(usePlane.getMatchType())){
						startToStopDistance=findDistance(departAPT.getLat(),departAPT.getLon(),apt.get(i).getLat(),apt.get(i).getLon());
						//test if distance between depart and stop is in airplane's max travel range and if not same range as previous stop
						if(startToStopDistance<=maxRange && !stopAPTDist.contains(startToStopDistance)){
							testDistance=findDistance(apt.get(i).getLat(),apt.get(i).getLon(),destnAPT.getLat(),destnAPT.getLon());
							if(testDistance<stopToDestnDistance){
								stopDist=startToStopDistance;
								stopIndex=i;
								stopToDestnDistance=testDistance;
							}
						}
					}
				}
			}
			if(stopIndex!=-1){ //when stop is avaliable add this stop to info list
				stopAPT.add(apt.get(stopIndex));
				stopAPTDist.add(stopDist);
				if(stopToDestnDistance<=usePlane.getMaxRange()){ //if stop can reach destination, add final round and return to plan method
					stopAPT.add(destnAPT);
					stopAPTDist.add(findDistance(apt.get(stopIndex).getLat(),apt.get(stopIndex).getLon(),destnAPT.getLat(),destnAPT.getLon()));
					deadEnd.clear();
					return;
				}
				else findStop(apt.get(stopIndex),stopToDestnDistance, usePlane.getMaxRange());
			}
			else { //when no stop is find, break the plan and clear everything
				if(stopAPT.isEmpty() || deadEnd.size()>5){
					stopAPT.clear();
					stopAPTDist.clear();
					deadEnd.clear();
					return;
				}
				else{ //when stops routine cannot reach destination, clear all and start another path
				deadEnd.add(departAPT.getID());
				stopAPT.clear();
				stopAPTDist.clear();
				if(startAPT.getFuelType().contains(usePlane.getMatchType())){
					findStop(startAPT,dist,usePlane.getMaxRange());
				}else findStop(startAPT,dist,((fuelTank/usePlane.getFuelTank())*usePlane.getMaxRange()));
				}
			}
  		}

		private class StopInfoPane extends JPanel{
			private double currentTank;

			private StopInfoPane(double tank){
					currentTank=tank; //tank when leaving starting airport
					int round=stopAPT.size(); //number of round equals to number of stop needed
					setLayout(new GridLayout(round,1));
					addInfo(startAPT,stopAPT.get(0),stopAPTDist.get(0),1); //add first stop info
					for(int i=1; i<round-1;++i){ //add rest stop info except last round
						addInfo(stopAPT.get(i-1),stopAPT.get(i),stopAPTDist.get(i),i+1);
					}
					addInfo(stopAPT.get(round-2),stopAPT.get(round-1),stopAPTDist.get(round-1),-1);  //add last round info, -1 round means final
					lastTank=currentTank; //return last tank when reach destination
				}

			private void addInfo(Airport depart, Airport destn, double distance, int round){
				double time=distance/usePlane.getMPH();
				double fuelUse=usePlane.getBurn()*time;
				double refuel=0;
				double hd=findHeading(depart.getLat(),depart.getLon(),destn.getLat(),destn.getLon());
				if(round==1){ //check starting airport dont have matching fuel and set first heading
					firstHeading=hd;
					if(depart.getFuelType().contains(usePlane.getMatchType())){
						refuel+=usePlane.getFuelTank()-currentTank; //refuel to full tank
						totalRefuel+=refuel; //total refuel
					}
				}else{
					refuel+=usePlane.getFuelTank()-currentTank; // always refuel to full tank
					totalRefuel+=refuel; //total refuel
				}
				JPanel p=new JPanel();
				p.setToolTipText(" (Current Tank +- Changes) / Max Tank" );
				p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
				p.add(new JLabel("Routine: "+depart.getID()+" >>> "+destn.getID() +" / "+df.format(time)+" hours"));
				p.add(new JLabel("Distance: "+df.format(distance)+" miles / "+"Heading: "+df.format(hd)));
				p.add(new JLabel("After Refuel Perform: ("+df.format(currentTank)+"+"+df.format(refuel)+")/"+usePlane.getFuelTank()));

				currentTank+=refuel; //add refuel needed to current tank
				p.add(new JLabel("Fuel When Reach: ("+ df.format(currentTank)+"-"+df.format(fuelUse)+")/"+usePlane.getFuelTank()));
				currentTank-=fuelUse; //current tank minus fuel used

				String r=(round==-1?"Final":"Round "+round); //final round or  round #
				title= new TitledBorder(r);
				title.setTitleColor(Color.GRAY);
				p.setBorder(title);
				add(p);
			}

		}

		private class SummaryPane extends JPanel{
			private SummaryPane(String begin, String end, double distance , double heading, double time, double fuelUsage,double remainTank, double maxTank, double refuel, double beforeTank, String airplane, int stop, int round){
				setLayout(new GridLayout(10,num,5,5));
				add(new JLabel("Flight routine:"));
				add(new JLabel("From "+ begin+" To "+end));
				add(new JLabel("Airplane Selected:"));
				add(new JLabel(airplane));
				add(new JLabel("Round Needed for Refuel:"));
				JButton btn=new JButton(stop+" Rounds / View Detail");
				btn.setHorizontalAlignment(SwingConstants.LEFT);
				btn.setFocusable(false);
				btn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						if(stop!=0){
							JScrollPane p=new JScrollPane(stopInfoPanel.get(round-1));
							p.setPreferredSize(new Dimension(300,400));
						//	p.setSize(500,100);
							JOptionPane.showMessageDialog(SummaryPane.this,p,"Stop Information",JOptionPane.PLAIN_MESSAGE);
						}
						else JOptionPane.showMessageDialog(SummaryPane.this,"No Stop Needed");
					}
				});
				add(btn);
				add(new JLabel("First Heading Direction:"));
				add(new JLabel(df.format(heading)));
				add(new JLabel("Time to Destination:"));
				add(new JLabel(df.format(time)+" hours"));
				add(new JLabel("Total Distance in Between:"));
				add(new JLabel(df.format(distance)+" miles"));
				add(new JLabel("Fuel Tank Beforehand:"));
				add(new JLabel(df.format(beforeTank)+"/"+df.format(maxTank)+" liters"));
				add(new JLabel("Total Fuel Needed:"));
				add(new JLabel("-"+df.format(fuelUsage)+" liters"));
				add(new JLabel("Total Perform Refuel:"));
				add(new JLabel("+"+df.format(refuel)+" liters"));
				add(new JLabel("Fuel Tank Afterward:"));
				add(new JLabel(df.format(remainTank)+"/"+df.format(maxTank)+" liters"));
				title= new TitledBorder("Round "+round+": "+begin+" >>> "+end);
				title.setTitleColor(Color.GRAY);
				setBorder(title);
			}
		}

  		private class SelectionPanel extends JPanel{     //Panel for plane selection
			private ButtonGroup btnGroup=new ButtonGroup();

			private SelectionPanel(Airport depart, String destination, int round){
  				setLayout(new GridLayout(depart.getOwns()+1,1));
  				title= new TitledBorder("Round "+round+": From "+depart.getID()+" To "+destination);
  				title.setTitleColor(Color.GRAY);
  				setBorder(title);
  				for(int i=0;i<depart.getOwns(); ++i){  //add radio button
    				addOption(depart.apn().get(i).getModel()+"  (Tank Size: "+ depart.apn().get(i).getFuelTank()+" liters)",i);
				}
				if(round!=1){ //show continues previous plane option if it is not the first stop
					JRadioButton b = new JRadioButton("Continues Previous Plane");
    	  			b.setActionCommand("-1");
      				add(b);
      				btnGroup.add(b);
				}
			}

			private int getResult(){   //Return the user's selection
				ButtonModel	btnModel = btnGroup.getSelection();
    			if(btnModel!=null){
    				return Integer.parseInt(btnModel.getActionCommand());
				}
				return -10; //return -10 if user did not fill selection
			}

			private void addOption(String t, int i) {
      			JRadioButton b = new JRadioButton(t); //set radio button title
      			b.setActionCommand(""+i);   //radio button result
      			add(b);
      			btnGroup.add(b);
   			}
		}

		private class DisplayPanel extends JPanel{ //panel of buttons to managing cardlayout panel
			private DisplayPanel(){
				ControlActionListenter cal = new ControlActionListenter();
				setLayout(new BorderLayout());
				final JButton btn1 = new JButton("First");
				btn1.setFocusable(false);
    	    	btn1.addActionListener(cal);
     			final JButton btn2 = new JButton("Next");
    			btn2.addActionListener(cal);
   				final JButton btn3 = new JButton("Previous");
   		    	btn3.addActionListener(cal);
    		    final JButton btn4 = new JButton("Last");
        		btn4.addActionListener(cal);
        		btn2.setFocusable(false);
        		btn3.setFocusable(false);
        		btn4.setFocusable(false);
       		 	JPanel controlButtons = new JPanel(new GridLayout(2,2,5,5));
       		 	controlButtons.add(btn3);
       		 	controlButtons.add(btn2);
       		 	controlButtons.add(btn1);
        		controlButtons.add(btn4);
        		controlButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        		add(controlButtons, BorderLayout.PAGE_END);
			}
			private class ControlActionListenter implements ActionListener {
	            public void actionPerformed(ActionEvent e) {
	                CardLayout cl = (CardLayout) (collections.getLayout());
	                switch(e.getActionCommand()) {
	                case "First":
	                    cl.first(collections);
	                    break;
	                case "Next":
	                    cl.next(collections);
	                 	break;
	                case "Previous":
	                    cl.previous(collections);
	                    break;
	                case "Last":
	                    cl.last(collections);
	                    break;
	                }
	            }
	        }

		}

		private class ResultDialog extends JDialog{ //show result with map and info
			private Map map;

			private ResultDialog(){
				map=new Map(totalAPT,mainAirportIndex, apt); //set routine for map
				add(map,BorderLayout.CENTER);
				add(display,BorderLayout.WEST);
			}
			private void display(){
				addWindowListener(new WindowAdapter(){  //pause the timer and dispose the screen
					public void windowClosing(WindowEvent e){
						map.kill();
						dispose();
					}
			});
				setTitle("Flight Routine Summary");
		 	  	pack();
		 	  	setModal(true);
		   		setLocationRelativeTo(this);
		   		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    	setVisible(true);
			}
		}
  	}
}