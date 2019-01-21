/*
	CSCI 240 - Electronic Flight Planning System
	**********************************
	File Name: AirportManager.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description:
	A class that manages the information of airports. The class has the options of viewing, adding, editing, and deleting specific airports.

	Date Modified: 04.24.18
*/


import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class AirportManager extends JPanel implements ActionListener{
	private ArrayList<Airport> apt = new ArrayList<Airport>(); //list of airports
	private int aptSize; //airport size
 	private final static Font titleFont = new Font("TimesRoman", Font.BOLD + Font.ITALIC, 15);
	private final static int num=7;    //number of information type of airport
	private final static String[] infoType ={	"Identifier:",
										"Location:",
										"Name:",
										"Fuel Type Avaliable: ",
										"Latitude:",
										"Longitude:",
										"Frequency:",
										"Airplanes Owned:"
									}; //information type,
	private JTextField searchInput;  //user's input for searching airport
	private ManagerPanel mp; //airports list interface
	private Map map;
	private Planner owner;

	public AirportManager(Planner o){//Constuctor of the class, use for adding components to the screen which allow users to access multiple functions
		owner=o;
		readData("Data");  //read the airport from database file.
		mp= new ManagerPanel();
		setLayout(new BorderLayout(10,10));
		JButton btnSearch=new JButton("Search");
		JButton btnAdd=new JButton("Add Airport");
		JButton btnReset=new JButton("Reset Data");
		JButton btnSave=new JButton("Save Data");
		JPanel btnPanel=new JPanel(new GridLayout(2,2));
		btnSearch.addActionListener(this);
		btnAdd.addActionListener(this);
		btnSave.addActionListener(this);
		btnReset.addActionListener(this);
		btnPanel.add(btnSearch);
		btnPanel.add(btnAdd);
		btnPanel.add(btnSave);
		btnPanel.add(btnReset);
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		add(mp,BorderLayout.CENTER);
		add(btnPanel,BorderLayout.SOUTH);
	}

	public void setMap(Map m){
		map=m;
	}

 	//Write information into data file
	public void saveData(){
  		try{
  			BufferedWriter bwAPT=new BufferedWriter(new FileWriter("data/Airport_Data"));
  			BufferedWriter bwAPN=new BufferedWriter(new FileWriter("data/Airplane_Data"));
  			for(Airport list : apt){
   				for (String line : list.getAll()) { //write all information of airport into airport data file
    				bwAPT.write (line + "\n");   //seperate each information by line
   				}
   				for (Airplane plane : list.apn()) { //write all information of airplane inside this airport into airplane data file
    				for(String line : plane.getAll()){
    					bwAPN.write (line + "\n");   //seperate each information by line
   					}
   				}
  			}
    		bwAPT.close();
    		bwAPN.close();
  		}catch(IOException e){
  	 	e.printStackTrace ();
  		}
 	}

	//Read information from data file
 	private void readData(String dataFile){   //Read data from file
 		try{
   			BufferedReader brAPT= new BufferedReader(new FileReader("data/Airport_"+dataFile));
   			BufferedReader brAPN= new BufferedReader(new FileReader("data/Airplane_"+dataFile));
 			ArrayList<String> aptInfoList= new ArrayList<String>(); //list of a airport information
 			ArrayList<String> apnInfoList= new ArrayList<String>(); //list of a airplane information
  			String dataAPT;
  			String dataAPN;
			Airport temp;   //temperory airport
  			do{
  				dataAPT=brAPT.readLine(); //start reading information from airport file
  				aptInfoList.add(dataAPT);	//add information to airport information list
  				if(aptInfoList.size()==num+1){  //creating airport base on information list when information list is equal to max number of information types
  					temp=new Airport(aptInfoList);
  					apt.add(temp);
  						for(int i=0;i<temp.getOwns();++i){//create airplanes base on number of airplanes this airport owns
  							for(int c=0;c<5;++c){
  								dataAPN=brAPN.readLine(); //add information from airplane data to airplane information list
  								apnInfoList.add(dataAPN);
  							}
  							temp.apn().add(new Airplane(apnInfoList));
  							apnInfoList.clear(); //clear airplane information list and restart adding-airplane cycle
  						}
  					aptInfoList.clear(); //clear airport information list and restart adding-airport cycle
  				}
  			}while(dataAPT!=null);  //when data in data file is empty the reading will stop
  			aptSize=apt.size(); //set airport size
  			brAPT.close();
    		brAPN.close();
  		}catch(IOException e){
   			e.printStackTrace ();
  		}
 	}

	//return all airports information
	public ArrayList<Airport> getAirport(){
		return apt;
	}

	//set data back to default data
	private void setDefault(){
			apt.clear(); //clear all airports
       		readData("Data_Default"); //add default airpots
       		mp.updateData(); //refresh the screen
       		map.update();
	}

	//take index and display airport information screen base on this index in apt list
	public void getInfo(int row){ //editable = allow user to modify airplane, edit, and delete this airport
 		if(row != -1){
 			InfoDialog g=new InfoDialog(row);
 			g.setModal(true);
			g.setTitle("Airport Infomation");
			g.pack();
    		g.setLocationRelativeTo(this);
    		g.setVisible(true);
 		}
 		else  JOptionPane.showMessageDialog(AirportManager.this,"No Airport Found");
 	}

	//Take user input and return the index of specific airport
 	public int search(String input){
 		if(input.length()==3 || input.length()==4){ //search airport ICAO if input length is 3 or 4. Return the matching Index
 			for(int i=0;i<aptSize;++i){
   				if(input.equalsIgnoreCase(apt.get(i).getID())){
    				return i;
   				}
  			}
 		}
 		else{  //search matching airport location, return the index
 			ArrayList<Integer> listNum = new ArrayList<>(); //number of target found with same location
  			for(int i=0;i<aptSize;++i){
   				if(input.equalsIgnoreCase(apt.get(i).getLoc())){
    				listNum.add(i);
   				}
  			}
  			if(listNum.size()==1){  //return the first value in the list if only one airport match
   				return listNum.get(0);
  			}
  			else if(listNum.size()>1){ //display choice screen when multiple airport location match
  				ChoiceDialog choice= new ChoiceDialog(listNum);
  				choice.setTitle("Multiple Results Found on "+input);
				choice.setModal(true);
				choice.pack();
				choice.setLocationRelativeTo(AirportManager.this);
  				choice.setVisible(true);
  				return choice.getResult(); //return the chosen airport
  			}
 		}
 		// When none airport matches the information enter
  		return -1;
 	}

	//Buttons on Airport Manager Screen
	public void actionPerformed(ActionEvent e){
    	String str = e.getActionCommand();
     	switch(str){
       		case "Search":     	getInfo(search(searchInput.getText()));
       							break;
       		case "Add Airport": new ModifyInfoScreen().display();
       							break;
       		case "Save Data": 		saveData();;
       							JOptionPane.showMessageDialog(this,"Data Save Compelete");
       							break;
       		case "Reset Data": //Opens a confirmation screen, verifying the user wishes to restore all pre-existing airport and airplane information.
       		   					int confirm = JOptionPane.showConfirmDialog(null,"ARE YOU CERTAIN YOU WANT TO RESTORE THE DEFAULT DATA?" ,"CONFIRM" ,JOptionPane.YES_NO_OPTION);

  								if (confirm == JOptionPane.YES_OPTION){
       								setDefault();
       								JOptionPane.showMessageDialog(this,"Data Reset Complete");
								}
     							break;
      	}
		
    }

	//Panels that display a table of airports ICAO & Location and search input field
	private class ManagerPanel extends JPanel{    //panel for search screen
		private TableModel model;
		private JTable table;
		private JScrollPane scroll;
		private	String[][] data;
		private final Font infoFont = new Font("TimesRoman", Font.PLAIN, 12);
		private	final String[] columns = new String[] {"Airport ##", "ICAO ID", "Location"};

		private ManagerPanel(){
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		       table = new JTable(){
				public boolean isCellEditable(int row, int column){
        			return false;
   				}
   			};
   			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
   			updateData(); //load information
   			table.setShowVerticalLines(false);
        	scroll = new JScrollPane(table);
 			searchInput=new JTextField();
 			table.addMouseListener(new MouseAdapter() {   //listen to the click
  				public void mouseClicked(MouseEvent evt) {
       				if (evt.getClickCount() == 2) {
           	 			int index = table.rowAtPoint(evt.getPoint());
          				getInfo(index); //display info screen base on the index return from click
          				
        			}
        		}
        	});
			table.setFont(infoFont);
			table.setRowHeight(table.getRowHeight() + 3);
 			add(scroll);
 			add(Box.createRigidArea(new Dimension(10,10)));
 			add(new JLabel("            Enter Airport Identifier(ICAO) or Location"));
 			add(searchInput);
 			searchInput.addActionListener(new ActionListener(){
 				public void actionPerformed(ActionEvent e){
 					getInfo(search(searchInput.getText()));
 				}
 			});
 			setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		}

		private void updateData(){  //information of table
			data = new String[aptSize][3];
   	 		for(int i=0;i<aptSize;++i){
   	 			data[i][0]="Airport #"+(i+1);
   	 			data[i][1]=apt.get(i).getID();
   	 			data[i][2]=apt.get(i).getLoc();
   			}
   			model = new DefaultTableModel(data, columns);
   			table.setModel(model);
   			TableColumnModel columnModel = table.getColumnModel();
			columnModel.getColumn(2).setPreferredWidth(180); //set length of Location cell
		}
	}

	// Add/Edit Airport information
	private class ModifyInfoScreen extends JDialog implements ActionListener{
		private Airport airport; //selected airport
		private int index=-1; //selected airport index
		private JTextField[] editAddInput; //for edit and adding purpose
		private JButton btnOK;
		private double longitude;
		private double latitude;
		private String errors; //errors found
		private double freq;

		private ModifyInfoScreen(){  //Constuctor for adding new airport
			setTitle("Add New Airport");
			btnOK=new JButton("Add");
			setUp();  //set actionlisnter
		}
		private ModifyInfoScreen(int i){ //constuctor for editing existed airport
			setTitle("Edit Airport Information");
			index=i; //set selected airport index
			airport=apt.get(i);  //set selected airport
			btnOK=new JButton("Submit");
			setUp();  //set actionlisnter
		}

		private void setUp(){
			JPanel btnPanel=new JPanel(new GridLayout(1,2,5,5));
			JButton btnCancel=new JButton("Cancel");
			btnOK.addActionListener(this);
			btnCancel.addActionListener(this);
			setLayout(new BorderLayout());
			btnPanel.add(btnOK);
			btnPanel.add(btnCancel);
			btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(new AEPanel(), BorderLayout.CENTER); //add input panel of this airport
			add(btnPanel, BorderLayout.SOUTH); //add button panel
		}

		private void display(){ //display screen
			setModal(true);
    		setSize(350,350);
    		setLocationRelativeTo(AirportManager.this);
    		setVisible(true);
		}

		public void actionPerformed(ActionEvent e){
			String str = e.getActionCommand();
			String[] info=new String[num];
     		if(str.equals("Add")){   //add new airport
     			for(int i=0;i<num;++i){
   					info[i]=editAddInput[i].getText(); //take user inputs
   				}
   				checkInfo(info);  //check inputs is valid
   				if(errors.isEmpty()){
   					apt.add(new Airport(info,latitude,longitude, freq)); //add new airport to airport list
   					aptSize++; //increase apt size
   					map.update();
   					mp.updateData(); //update screen of the table
   					dispose(); //close screen
   					JOptionPane.showMessageDialog(AirportManager.this,"Add Airport Compelete");
   				}
   				else JOptionPane.showMessageDialog(ModifyInfoScreen.this,"<html>Errors Found:<br>"+errors+"</html>");
     		}
     		else if(str.equals("Submit")){ //edit airport
     			for(int i=0;i<num;++i){
   					info[i]=editAddInput[i].getText(); //user inputs
   				}
   				checkInfo(info); //check inputs is valid
   				if(errors.isEmpty()){
   					airport.setAll(info,latitude,longitude, freq); //set information
   					mp.updateData(); //update screen of the table
   					map.update();
   					dispose();
   				}
   				else JOptionPane.showMessageDialog(ModifyInfoScreen.this,"<html>Errors Found:<br>"+errors+"</html>");
     		}
    		else dispose(); //Cancel button
    		
		}

		private void checkInfo(String[] info){  //check user inputs
			/*
			 *info[0]=ICAO
			 *info[1]=LOCATION
			 *info[2]=NAME
			 *info[3]=AVALIABLE FUEL TYPE
			 *info[4]=LATITUDE
			 *info[5]=LONGITUDE
			 *info[6]=Frequency
			 */

			errors=""; //empty the error

    		if(info[0].length()==3 || info[0].length()==4){ //test input icao length
    			for(int i=0; i<aptSize;++i){  //test icao is existed
   					if(info[0].equalsIgnoreCase(apt.get(i).getID())&& i!=index){
						errors+="<br>*This ICAO Identifier Already Existed";
   						break;
   					}
    			}
    			for(int i=0;i<info[0].length();++i){ //test icao is letter
    				if(!Character.isLetter(info[0].charAt(i))){
    					errors+="<br>*ICAO Identifier Must Be Letters";
    					break;
    				}
    			}
    		}
   		 	else{
    			errors+="<br>*ICAO Identifier Length Must Be 3 or 4 Letters";
    		}
			if(info[1].length()<6){ //test location input, must greater than 6 character
				errors+="<br>*Location cannot be less than six character";
			}
    		switch(info[3].toUpperCase()){ //test and convert fuel type
				case "AVGAS & JA-A":
				case "AVGAS":
				case "JA-A":
					break;
				case "AJ":
					info[3]="AVGAS & JA-a";
					break;
				case "A":
					info[3]="AVGAS";
					break;
				case "J":
					info[3]="JA-a";
					break;
				default:
					errors+="<br>*Unable To Identify Fuel Type";
					break;
			}
    		info[4]=setCoordinate(info[4],'N','S'); //test coordinate input
    		info[5]=setCoordinate(info[5],'E','W');
    		try{ //test if input frequency is a number
    			freq=Double.parseDouble(info[num-1]);
    		}catch(NumberFormatException e){
    			errors+="<br>*Frequency Must Be Numbers";
    		}
		}

		private String setCoordinate(String input, char direction1, char direction2){
			char finalDirection='?';
			double codn=999; //initial value
			if(!input.isEmpty()){ // test input is not empty
				try{
					codn=Double.parseDouble(input); //test if input is number
					if(codn>0){
						finalDirection=direction1; //give direction base on positive and negative
					}
					else finalDirection=direction2;
				}
				catch(NumberFormatException e){ //if input is not number
					int length=input.length()-1;
					finalDirection=Character.toUpperCase(input.charAt(length)); //direction==input
					try{
						if(finalDirection==direction1){ //give positive or negative coordinate base on correct direction
							codn=Double.parseDouble(input.substring(0,length));
						}
						else if(finalDirection==direction2){
							codn=-1*(Double.parseDouble(input.substring(0,length)));
						}
					}
					catch(IndexOutOfBoundsException | NumberFormatException ex){ //error if input is in wrong format
					}
				}
			}
			if(direction1=='N'){ //set latitude
				if(codn<-90 || codn>90){
					errors+="<br>*Invalid Latitude Format";
					return "";
				}
				else{
					latitude=codn;
					return Math.abs(latitude)+""+finalDirection;
				}
			}
			else{  //set longitude
				if(codn<-180 || codn>180){
					errors+="<br>*Invalid Longitude Format";
					return "";
				}
				else{
					longitude=codn;
					return Math.abs(longitude)+""+finalDirection;
				}
			}
		}

		private class AEPanel extends JPanel{   //input panel
			private final String[] toolTipInfo={
												"Enter the 3 or 4 Letter ICAO Code [Ex: CAE]",
												"Enter the Location of the Airport in Format No Less Than SIX Characters: City, State [Ex: Columbia, SC]",
												"Enter the Name of the Airport",
												"Enter A, J, AJ, or Full Name of Available Fuel Types [Ex: avgas]",
												"Enter the Latitude Number or ### N/S [Ex: 34 N]",
												"Enter the Longitude Number or ### W/E [Ex: 81 W]",
												"Enter the Frequency as a Number Only"
												};

			private AEPanel(){ //constructor for input
				setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				editAddInput=new JTextField[num];
				setLayout(new GridLayout(num,1,5,5));
   				for(int i=0;i<num;++i){
   					add(new JLabel("Set "+infoType[i]));
   					if(airport!=null){  //if theres airport, set textfield text to airport info for editing
  						editAddInput[i]=new JTextField(airport.getAll()[i]); //text field display existed info
   					}
   					else editAddInput[i]=new JTextField(); //set empty textfield
  				    editAddInput[i].setToolTipText(toolTipInfo[i]);
  					add(editAddInput[i]);
   				}
			}
		}
	}

	// screen of airports selection when multiple airports has same location
	private class ChoiceDialog extends JDialog{  //screen for choice option

		private ButtonGroup btnGroup=new ButtonGroup(); //radio button group
		private int result=-1;
		private ArrayList<Integer> listNum;

		private ChoiceDialog(ArrayList<Integer> get){
			listNum=get;
			JButton btnOK=new JButton("Proceed");
			setLayout(new BorderLayout(20,20));
			btnOK.addActionListener(new ActionListener() {   //check Button for starting
  				public void actionPerformed(ActionEvent event) {
    				ButtonModel	btnModel = btnGroup.getSelection();
    				if(btnModel==null){
    					JOptionPane.showMessageDialog(ChoiceDialog.this,"No Selection");
    				}
    				else{
    					dispose();
    					result=Integer.parseInt(btnModel.getActionCommand()); //selected button
    				}
  				}
			});
			add(new ChoicePanel(),BorderLayout.PAGE_START);
			add(new JLabel("     Proceed By Select One Option     "),BorderLayout.CENTER);
			add(btnOK,BorderLayout.PAGE_END);
		}

		public int getResult(){   //Return the user's selection
			return result;
		}

		private class ChoicePanel extends JPanel{     //Panel for multiple objects found

			private ChoicePanel(){
  				setLayout(new GridLayout(listNum.size(),1));
  				TitledBorder title= new TitledBorder(listNum.size()+" Airports Found");
  				title.setTitleColor(Color.GRAY);
  				title.setTitleFont(titleFont);
  				setBorder(title);
  				for(int i=0;i<listNum.size(); ++i){  //add radio button
    				addOption(apt.get(listNum.get(i)).getName()+" ("+ apt.get(listNum.get(i)).getID()+")",i);
				}
			}

			private void addOption(String t, int i) {
      			JRadioButton b = new JRadioButton(t); //set radio button title
      			b.setActionCommand(""+listNum.get(i));   //radio button result
      			add(b); //add radio button to the panel
      			btnGroup.add(b); //add radio button to the button group
   			}
		}
	}

	//Information screen of specific airport
	private class InfoDialog extends JDialog{   // infomation screen

		private int index; //the index of airport
		private Airport airport;
		private InfoPanel gp;

		private InfoDialog(int r){
			index=r; //take index
			airport=apt.get(index);
			gp=new InfoPanel();
			setLayout(new BorderLayout());
			add(gp,BorderLayout.CENTER);
			add(new BtnPanel(),BorderLayout.SOUTH);
		}


		private class BtnPanel extends JPanel implements ActionListener{  //button panel
			private BtnPanel(){
				final JButton btnEdit=new JButton("EDIT");
				final JButton btnDelete=new JButton("DELETE");
				final JButton btnReturn=new JButton("RETURN");
				final JButton btnView=new JButton("View / Edit Airplane Own by Airport");
				btnEdit.addActionListener(this);
				btnDelete.addActionListener(this);
				btnReturn.addActionListener(this);
				btnView.addActionListener(this);
				setLayout(new BorderLayout(10,10));
				add(btnView,BorderLayout.NORTH);
				add(btnEdit,BorderLayout.CENTER);
				add(btnDelete,BorderLayout.LINE_START);
				add(btnReturn,BorderLayout.LINE_END);
				setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			}


			public void actionPerformed(ActionEvent e){  //button reaction
    			String str = e.getActionCommand();
     			switch(str){
       				case "EDIT":    new ModifyInfoScreen(index).display(); //access edit screen
       								gp.updateInfo(); //update new information
       								revalidate();
      								repaint();
       								break;
       				case "DELETE":  int confirm = JOptionPane.showConfirmDialog(InfoDialog.this,"ARE YOU CERTAIN YOU WANT TO DELETE?" ,"CONFIRM" ,JOptionPane.YES_NO_OPTION);
   									if (confirm == JOptionPane.YES_OPTION){
       								apt.remove(index); //delete airport
       								aptSize--; //decrease apt size
       								mp.updateData(); //update screen of table
       								map.update();
       								owner.clearAll(); //clear planner's planning input when a airport is delete
       								dispose();
       								JOptionPane.showMessageDialog(AirportManager.this,"Airport Deleted");
   									}
       								break;
       				case "RETURN": 	dispose();
       								break;
       				case "View / Edit Airplane Own by Airport" :
       					airport.airplaneManager().getScreen(); //access airplane manager screen
       					airport.setOwns(); //update this airport's airplane number when airplane manager is close
       					gp.updateInfo();
       					revalidate();
      					repaint();
       			}
       			
			}
		}

		private class InfoPanel extends JPanel{  //panel display the information of airport

			private JLabel[] lbl;

			private InfoPanel(){
				setLayout(new GridLayout(num+1,2));
				TitledBorder title= new TitledBorder("AIRPORT #"+(index+1));
				lbl= new JLabel[num+1];
				for(int i=0;i<num+1;++i){
					add(new JLabel(infoType[i]));
					add(lbl[i]=new JLabel(airport.getAll()[i]));
				}
				updateInfo();
  				title.setTitleColor(Color.GRAY);
  				title.setTitleFont(titleFont);
  				setBorder(title);
			}
			private void updateInfo(){ //update information

				for(int i=0;i<num+1;++i){
  					lbl[i].setText(airport.getAll()[i]);
  				}
			}
		}
	}
}