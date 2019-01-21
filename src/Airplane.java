/*
	CSCI 240 - Electronic Flight Planning System
	**********************************
	File Name: Airplane.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description: 
	The airplane class contains the get and set methods for obtaining all the airplane attributes.
	
	Date Modified: 04.24.18
*/

import java.util.*;
public class Airplane {

		private final static int num=5;
		private String[] stringInfo=new String[2];
		private double[] numberInfo=new double[3];
		/*stringInfo[0]=Made/Model
		 *stringInfo[1]=airplane type
		 *numberInfo[0]=fuel tank size
		 *numberInfo[1]=fuel burn speed
		 *numberInfo[2]=speed
		 */
		private String matchFuelType="N";

    public Airplane (String[] getInfo){
		setAll(getInfo);
    }

    public Airplane(ArrayList<String> fileList){
    	for(int i=0;i<num;++i){
    		if(i<2){
    			stringInfo[i]=fileList.get(i);
    		}
    	    else{
    	    		numberInfo[i-2]=Double.parseDouble(fileList.get(i));
    	    }
    	}
    	setAT();
    }

	public void setAll(String[] getInfo){
    	for(int i=0;i<num;++i){

    		if(i<2){
    			stringInfo[i]=getInfo[i];
    		}
    	    else{
    	    	numberInfo[i-2]=Double.parseDouble(getInfo[i]);
    	    }
    	}
    	setAT();
	}

	private void setAT(){
			switch(stringInfo[1].toUpperCase()){
				case "JET":
				case "TURBOFAN":
					matchFuelType="J";
					break;
				case "PROP PLANE":
					matchFuelType="V";
					break;
				case "P":
					matchFuelType="V";
					stringInfo[1]="Prop Plane";
					break;
				case "J":
					matchFuelType="J";
					stringInfo[1]="Jet";
					break;
				case "T":
					matchFuelType="J";
					stringInfo[1]="Turbofan";
					break;
			}
	}

    public String getModel(){
    	return stringInfo[0];
    }
    public String getMatchType(){
    	return matchFuelType;
    }
    public double getFuelTank(){
    	return numberInfo[0];
    }
    public double getBurn(){
    	return numberInfo[1];
    }
    public double getSpeed(){
    	return numberInfo[2];
	}
    public double getMPH(){
    	return numberInfo[2]*1.15077945;
    }
	public double getMaxRange(){
		return ((numberInfo[0]/numberInfo[1])*(getMPH()));
	}
    public String[] getAll(){
    	String[] all= new String[num];
    	for(int i=0;i<num;++i){
    		if(i<2){
    			all[i]=stringInfo[i];
    		}
    		else all[i]=numberInfo[i-2]+"";
    	}
    	return all;
    }

}