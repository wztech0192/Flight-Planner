/*
	CSCI 240 - Electronic Flight Planning System
	**********************************
	File Name: AirplaneManager.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description:
	A class that manages the information of airplanes inside a specific airport. The class has the options of
	viewing, adding, editing, and deleting specific airplanes.

	Date Modified: 04.24.18
*/

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class AirplaneManager extends JDialog implements ActionListener{ //inner class manager

	private final static Font titleFont = new Font("TimesRoman", Font.BOLD + Font.ITALIC, 15);
	private final static int num=5;    //number of information type of airplane
	private final static String[] infoType ={	"Make and Model:",
										"Type:",
										"Fuel Tank Size:",
										"Fuel Burn at CP in l/h:",
										"Airspeed at CP in Knots:"
									}; //information type,
	private ArrayList<Airplane> apn;
	private String owner;
	private JTextField searchInput;
	private ManagerPanel mp;

	public AirplaneManager(ArrayList<Airplane> ap, String o){
		apn=ap;
		owner=o;
		mp= new ManagerPanel();
		setLayout(new BorderLayout(10,10));
		JButton btnSearch=new JButton("Search");
		JButton btnAdd=new JButton("Add Airplane");
		JButton btnReturn=new JButton("Exit");
		JPanel btnPanel=new JPanel(new GridLayout(0,3));
		btnSearch.addActionListener(this);
		btnAdd.addActionListener(this);
		btnReturn.addActionListener(this);
		btnPanel.add(btnSearch);
		btnPanel.add(btnAdd);
		btnPanel.add(btnReturn);
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		add(mp,BorderLayout.CENTER);
		add(btnPanel,BorderLayout.SOUTH);
	}

	public void getScreen(){
		setTitle("Airplane Manager of Airplanes own by: "+owner);
    	setSize(380,300);
    	setModal(true);
    	setLocationRelativeTo(null);
    	setVisible(true);
	}


	private void getInfo(int row){  //get specifi airplane information
 		if(row != -1){
 			InfoDialog g=new InfoDialog(row);
 			g.setModal(true);
			g.setTitle("Airplane Infomation");
			g.pack();
    		g.setLocationRelativeTo(AirplaneManager.this);
    		g.setVisible(true);
 		}
 	}


 	private int search(String input){    //search specific airplane1;
  		for(int i=0;i<apn.size();++i){
   			if(input.equalsIgnoreCase(apn.get(i).getModel())){
    			return i;
   			}
  		}
  		JOptionPane.showMessageDialog(AirplaneManager.this,"No Data Match");
  		return -1;
 	}


	public void actionPerformed(ActionEvent e){
    		String str = e.getActionCommand();
     		switch(str){
       			case "Search":     	getInfo(search(searchInput.getText()));
       								break;
       			case "Add Airplane": new ModifyInfoScreen().display();
       								break;
       			case "Exit": 		dispose();
       								break;
       		}
			mp.updateList();
			
    	}


	private class ManagerPanel extends JPanel{    //panel for search screen

			private DefaultListModel<String> listModel;
			private JList<String> list;
			private JScrollPane scroll;
			private JPanel panel2;
			private final Font infoFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

			private ManagerPanel(){
				list = new JList<>();
				updateList();
        		scroll = new JScrollPane(list);
 				setLayout(new BorderLayout(3,3));
 				panel2= new JPanel(new GridLayout(2,1));
 				searchInput=new JTextField();
 				list.addMouseListener(new MouseAdapter() {   //list listen to the click
  					public void mouseClicked(MouseEvent evt) {
        				@SuppressWarnings("unchecked")
						JList<String> list = (JList<String>)evt.getSource();
       					if (evt.getClickCount() == 2) {
           	 				int index = list.locationToIndex(evt.getPoint());
          					getInfo(index);
							updateList();
							
        				}
        			}
        		});
				list.setFont(infoFont);
 				JLabel label1=new JLabel("Model-Made / Tank Size / Speed at CP");
 				label1.setFont(infoFont);
 				label1.setForeground(Color.GRAY);
 				add(label1, BorderLayout.NORTH);
 				add(scroll, BorderLayout.CENTER);
 				panel2.add(new JLabel("Enter Airplane Model-Make"));
 				panel2.add(searchInput);
 				add(panel2, BorderLayout.SOUTH);
 				setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
			}

			private void updateList(){
				listModel = new DefaultListModel<>(); //list
   	 			for(int i=0;i<apn.size();++i){
     				listModel.addElement(apn.get(i).getModel()+" / "+apn.get(i).getFuelTank()+"  liters / "+apn.get(i).getSpeed()+" knots");
   				}
   				list.setModel(listModel);
			}
		}


	private class InfoDialog extends JDialog{   // infomation screen

		private int row;
		private GetPanel gp;
		private InfoDialog(int r){
			row=r;
			gp=new GetPanel();
			setLayout(new BorderLayout());
			add(gp,BorderLayout.CENTER);
			add(new BtnPanel(), BorderLayout.SOUTH);
		}

		private class BtnPanel extends JPanel implements ActionListener{  //button panel
			private BtnPanel(){
				JButton btnEdit=new JButton("EDIT");
				JButton btnDelete=new JButton("DELETE");
				JButton btnReturn=new JButton("RETURN");
				btnEdit.addActionListener(this);
				btnDelete.addActionListener(this);
				btnReturn.addActionListener(this);
				setLayout(new BorderLayout(10,10));
				add(btnEdit,BorderLayout.CENTER);
				add(btnDelete,BorderLayout.LINE_START);
				add(btnReturn,BorderLayout.LINE_END);
				setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			}


			public void actionPerformed(ActionEvent e){  //button reaction
    			String str = e.getActionCommand();
     			switch(str){
       				case "EDIT":    new ModifyInfoScreen(row).display();
       				       			gp.removeAll();
       								gp.updateInfo();
       								revalidate();
      								repaint();
       								break;
       				case "DELETE": 	int confirm = JOptionPane.showConfirmDialog(InfoDialog.this,"ARE YOU CERTAIN YOU WANT TO DELETE?" ,"CONFIRM" ,JOptionPane.YES_NO_OPTION);
   									if (confirm == JOptionPane.YES_OPTION){
       								apn.remove(row);
       								dispose();
       								JOptionPane.showMessageDialog(AirplaneManager.this,"Airplane Delete Compelete");
   									}
       				case "RETURN": 	dispose();
       								break;
				}
			}
		}

		private class GetPanel extends JPanel{  //panel for get info
			private GetPanel(){
				setLayout(new GridLayout(num,2,10,10));
				TitledBorder title= new TitledBorder("AIRPLANE #"+(row+1));
				updateInfo();
  				title.setTitleColor(Color.GRAY);
  				title.setTitleFont(titleFont);
  				setBorder(title);
			}
			private void updateInfo(){
					for(int i=0;i<num;++i){
  					add(new JLabel(infoType[i]));
  					add(new JLabel(apn.get(row).getAll()[i]));
  				}
			}
		}
	}


	private class ModifyInfoScreen extends JDialog implements ActionListener{
		private int index=-1;
		private JTextField[] editAddInput; //for edit and adding purpose
		private JButton btnOK;
		private JButton btnCancel=new JButton("Cancel");
		private JPanel btnPanel=new JPanel(new GridLayout(1,2,5,5));
		private String errors;

		private ModifyInfoScreen(){
			setTitle("Add New Airplane");
			btnOK=new JButton("Add");
			setup();
		}
		private ModifyInfoScreen(int row){
			setTitle("Edit Airplane Information");
			index=row;
			btnOK=new JButton("Submit");
			setup();
		}

		private void setup(){
			setLayout(new BorderLayout());
			btnOK.addActionListener(this);
			btnCancel.addActionListener(this);
			btnPanel.add(btnOK);
			btnPanel.add(btnCancel);
			btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(new AEPanel(), BorderLayout.CENTER);
			add(btnPanel, BorderLayout.SOUTH);
		}

		private void display(){
			setModal(true);
    		pack();
    		setLocationRelativeTo(AirplaneManager.this);
    		setVisible(true);
		}

		public void actionPerformed(ActionEvent e){
			String str = e.getActionCommand();
			String[] info=new String[num];
     		if(str.equals("Add")){
     			for(int i=0;i<num;++i){
   					info[i]=editAddInput[i].getText();
   				}
   				checkInfo(info);
   				if(errors.isEmpty()){
   					apn.add(new Airplane(info));
   					dispose();
   					JOptionPane.showMessageDialog(AirplaneManager.this,"Add Airplane Compelete");
   				}
   				else JOptionPane.showMessageDialog(ModifyInfoScreen.this,"<html>Errors Found:<br>"+errors+"</html>");
     		}
     		else if(str.equals("Submit")){
     			for(int i=0;i<num;++i){
   					info[i]=editAddInput[i].getText();
   				}
   				checkInfo(info);
   				if(errors.isEmpty()){
   					apn.get(index).setAll(info);
   					dispose();
   				}
   				else JOptionPane.showMessageDialog(ModifyInfoScreen.this,"<html>Errors Found:<br>"+errors+"</html>");
     		}
    		else dispose();
    		
		}

		private void checkInfo(String[] info){
			errors="";

			if(info[0].isEmpty()){
    			errors+="<br>*Airplane Model and Made is Empty";
    		}
    		else{
    			for(int i=0; i<apn.size();++i){
   					if(info[0].equalsIgnoreCase(apn.get(i).getModel())&& i!=index){
						errors+="<br>*This Model/Made Already Existed";
   						break;
   					}
    			}
    		}

    		switch(info[1].toUpperCase()){
				case "JET":
				case "TURBOFAN":
				case "PROP PLANE":
				case "P":
				case "J":
				case "T":
					break;
				default:
					errors+="<br>*Invalid Airplane Type";
					break;
			}

			for(int i=2;i<5;++i){
				try{
    	    		Double.parseDouble(info[i]);
    	    	}catch(NumberFormatException e){
    	    		errors+="<br>*Text Field "+(i+1)+" Must Be Numbers";
				}
			}
		}
		private class AEPanel extends JPanel{   //panel for add/edit
			private final String[] toolTipInfo={
												"Enter Producer and Model of Airplane",
												"Enter Either Fullname or P, J, or T [Ex:J/JET]",
												"Enter the Input as a Number Only",
												"Enter the Input as a Number Only",
												"Enter the Input as a Number Only",
												};
			private AEPanel(){
				editAddInput=new JTextField[num];
				setLayout(new GridLayout(num,1,5,5));
   				for(int i=0;i<num;++i){
   					add(new JLabel("Enter Airplane "+infoType[i]));
   					if(index!=-1){
   						editAddInput[i]=new JTextField(apn.get(index).getAll()[i]);
   					}
   					else editAddInput[i]=new JTextField();
   					editAddInput[i].setToolTipText(toolTipInfo[i]);
  					add(editAddInput[i]);
   				}
			}
		}
	}
}