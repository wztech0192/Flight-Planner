/*
	CSCI 240 - Our Flight Planning System
	**********************************
	File Name: Starter.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description:
	At the start of the program, the user is prompted to login using default information provided below:

		User ID: group2
		Password: 1234

	If the input is incorrect, an error message is displayed and the user will be reprompted to enter the information until it is correct, or they may choose to exit the system.
	Following a successful login, the user will be prompted to heed the warning message: THIS SOFTWARE IS NOT TO BE USED FOR FLIGHT PLANNING OR NAVIGATIONAL PURPOSES.

	Date Modified: 04.24.18
*/


import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.UIManager.*;

//The Starter class with a default constructor, action listener, and main, which enables the formation of the login screen, warning message, and Starter Menu screen with buttons that perform various actions.

//An outer class constructing the Login frame for the Flight Planning System.

@SuppressWarnings("serial")
public class Starter extends JFrame implements ActionListener
{
	//Defining global variables of the outer class.
	private JPasswordField password;	//The text field used to obtain the password.
	private JTextField userName;		//The text field used to obtain the user name.

	//The method for setting up the Login specifications and UI.
	public Starter()
	{
		JPanel panel1=new JPanel();
		panel1.setLayout(new GridLayout(3,2,5,5));	//A grid layout with 3 rows and 2 columns to form the login screen.
		panel1.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		//Add username and password labels, text fields, and tool tips onto the panel.
		userName = new JTextField();
		userName.setToolTipText("Enter the User Name here.");
		password = new JPasswordField();
		password.setToolTipText("Enter the Password here.");
		panel1.add(new JLabel("User Name:"));
		panel1.add(userName);
		panel1.add(new JLabel("Password:"));
		panel1.add(password);

		//Add a login and exit button with associated tool tips.
		final JButton btnLogin=new JButton("Login");
		btnLogin.setToolTipText("Select to Login the System.");
		final JButton btnExit= new JButton("Exit");
		btnExit.setToolTipText("Select to Exit the System.");
		btnLogin.addActionListener(this);
		btnExit.addActionListener(this);
		panel1.add(btnLogin);
		panel1.add(btnExit);


		//Add a title to the login screen.
		JLabel title=new JLabel("Electronic Flight Planning System");
		Font font1 = new Font("Serif", Font.BOLD+Font.ITALIC, 18);
		title.setFont(font1);
		title.setForeground(Color.BLUE);
		title.setHorizontalAlignment(JTextField.CENTER);

		//Add a brief instructional description to the login screen.
		JLabel description=new JLabel("Please login.");
		Font font2 = new Font("TimesRoman", Font.ITALIC, 14);
		description.setFont(font2);
		description.setForeground(Color.BLACK);
		description.setHorizontalAlignment(JTextField.CENTER);

		//Adding all the components appropriately using Borderlayout.
		add(title,BorderLayout.NORTH);	//The Title is located at the top of the screen.
		add(description,BorderLayout.CENTER);	//The description is located in the middle of the screen.
		add(panel1,BorderLayout.SOUTH);	//The login panel is located at the lower half of the screen.

		getRootPane().setDefaultButton(btnLogin);  //make enter key connect to ok button
		}

	//The Action Listener for when input is entered into the User Name and Password text fields.
	public void actionPerformed(ActionEvent e)
	{
    	String str = e.getActionCommand();
     	switch(str){
       		case "Login":	//If the information is correct, the login is successful.
       			if(Arrays.equals(password.getPassword(), new char[]{'1','2','3','4'}) && userName.getText().equals("group2"))	//Assigned login information.
       			{
       				JOptionPane successful = new JOptionPane("<html><font face='Calibri' size='5' color='blue'>SUCCESS");
		    		JDialog successDialog = successful.createDialog(this, "Successful Login");
		    		successDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		    		successDialog.setVisible(true);
       				dispose();
       				//The Warning Screen is displayed. The user must agree to the terms by selecting "Ok".
		    		//The user may not exit upon clicking the X in the upper right-hand corner of the screen.
       				JOptionPane.showMessageDialog(null, "<html><font face='Calibri' size='5' color='red'>THIS SOFTWARE IS NOT TO BE USED FOR FLIGHT <br>PLANNING OR NAVIGATIONAL PURPOSES.","WARNING",  JOptionPane.WARNING_MESSAGE);
		    		new Planner().getScreen();


       			}
       			else
       			{	//If the information is incorrect, an error message is displayed.


       				JOptionPane unsuccessful = new JOptionPane("<html><font face='Calibri' size='4' color='red'>Incorrect User Name or Password.\n<html><font face='Calibri' size='4' color='red'>Please try again.");
		    		JDialog unsuccessfulDialog = unsuccessful.createDialog(this, "Unsuccessful Login");
		    		unsuccessfulDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		    		unsuccessfulDialog.setVisible(true);

       				password.setText("");
       			}
       			break;
       		case "Exit":
       		 System.exit(0);
       		break;
       }
    }

	//The main method sets a L&F theme for all the frames and defines the frame specs for the Login, Warning, and Starter Screens.
    public static void main(String[] args)
    {
   		SwingUtilities.invokeLater(new Runnable() {
            public void run(){
		    	//Sets the L&F theme.
				try
				{
		    		for (LookAndFeelInfo info:UIManager.getInstalledLookAndFeels()){
		        	if ("Nimbus".equals(info.getName())) {
		        	    UIManager.setLookAndFeel(info.getClassName());
		        	    break;
		        		}
		    		}
				} catch (Exception e) {}

				//The Login Screen is displayed. The user must type in the correct login information. If the input is incorrect, an error message is displayed and they will be reprompted to enter the correct information.
				//The user may choose to exit by clicking the "Exit" button or X on top.
		   		Starter loginScreen= new Starter();
		    	loginScreen.setTitle("Login Screen");
		 	  	loginScreen.setSize(400,200);
		   		loginScreen.setLocationRelativeTo(null);
		   		loginScreen.setResizable(false);
				loginScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //allow x to exit the application
		    	loginScreen.setAlwaysOnTop(true);
		    	loginScreen.setVisible(true);
        	}
        });
	}
}
