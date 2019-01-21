/*
	CSCI 240 - Electronic Flight Planning System
	**********************************
	File Name: Airport.java

	Programmers:
		Wei Zheng
		Jacob Barr
		Elizabeth Rustad (Leader)
	**********************************

	Description: 
	The airport class contains the get and set methods for obtaining all the airport attributes.
	
	Date Modified: 04.24.18
*/

import java.util.*;

public class Airport{
		private final static int num=7;
		private String[] stringInfo=new String[7];
		/*
		 *info[0]=ICAO
		 *info[1]=LOCATION
		 *info[2]=NAME
		 *info[3]=AVALIABLE FUEL TYPE
		 *info[4]=LATITUDE
		 *info[5]=LONGITUDE
		 *info[6]=Frequency
		 */
		private double longitude;
		private double latitude;
		private double freq;
		private int owns=0;
		private ArrayList<Airplane> airplane = new ArrayList<Airplane>();

    public Airport(String[] getInfo, double lat, double lon, double f){
    	setAll(getInfo,lat,lon,f);
    }
    public Airport(ArrayList<String> fileList){
    	for(int i=0;i<num;++i){
    		stringInfo[i]=fileList.get(i);
    	}
    	if(!fileList.get(num).isEmpty()){
    		owns=Integer.parseInt(fileList.get(num));
    	}
    	freq=Double.parseDouble(stringInfo[num-1]);
    	if(stringInfo[4].charAt(stringInfo[4].length()-1)=='N'){
    		latitude=Double.parseDouble(stringInfo[4].substring(0,stringInfo[4].length()-1));
    	}else latitude=-Double.parseDouble(stringInfo[4].substring(0,stringInfo[4].length()-1));
		if(stringInfo[5].charAt(stringInfo[5].length()-1)=='E'){
    		longitude=Double.parseDouble(stringInfo[5].substring(0,stringInfo[5].length()-1));
    	}else longitude=-Double.parseDouble(stringInfo[5].substring(0,stringInfo[5].length()-1));

    }

	public void setAll(String[] getInfo, double lat, double lon, double f){
		freq=f;
		latitude=lat;
		longitude=lon;
		stringInfo=getInfo;
    	stringInfo[0]=stringInfo[0].toUpperCase();
	}

    public void setOwns(){
    	owns=airplane.size();
    }

    public String getID(){
    	return stringInfo[0];
    }
	public String getLoc(){
    	return stringInfo[1];
    }
    public String getName(){
    	return stringInfo[2];
    }
    public String getFuelType(){
    	return stringInfo[3];
    }
    public double getFreq(){
    	return freq;
    }
    public double getLat(){
    	return latitude;
    	}
    public double getLon(){
    	return longitude;
    }
    public int getOwns(){
    	return owns;
    }

    public String[] getAll(){
    	String[] all= new String[num+1];
    	for(int i=0;i<num;++i){
    			all[i]=stringInfo[i];
    	}
    	all[num]=owns+"";
    	return all;
    }

	public ArrayList<Airplane> apn(){  //return airplane information
		return airplane;
	}

	public AirplaneManager airplaneManager(){  //return manager
		return  new AirplaneManager(airplane, stringInfo[0]);
	}
}