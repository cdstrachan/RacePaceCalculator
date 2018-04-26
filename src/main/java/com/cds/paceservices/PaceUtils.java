package com.cds.paceservices;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PaceUtils {
	
		static double TimeToDouble(LocalTime theTime) {
			double result = theTime.getSecond();
			result = result / 60;
			result = result + (double) theTime.getMinute();
			result = result + theTime.getHour() * 60;		
			return result;
		}

		static LocalTime DoubleToTime(Double theTimeDec) {
			int hours;
			int minutes;
			int seconds;
			
			seconds = getSeconds(theTimeDec);
			minutes = (int) (theTimeDec % 60);
			hours = (int) (theTimeDec / 60);
			if (seconds== 60)
			{
				seconds = 0;
				minutes ++;
			}
			if (minutes== 60)
			{
				minutes = 0;
				hours ++;
			}
			return LocalTime.of(hours,minutes,seconds);
		}
		
		static int getSeconds(double n) {
			double result;
			int seconds;
		    if (n > 0) {
		        result =  n - Math.floor(n);
		    } else {
		    	result =  ((n - Math.ceil(n)) * -1);
		    }
		    
		    seconds = (int) Math.round((result * 60));
		    return seconds;
		}
		
		static String formatTime(LocalTime theTime){
			DateTimeFormatter timeFormatHH = DateTimeFormatter.ofPattern("hh:mm:ss");
			DateTimeFormatter timeFormatH = DateTimeFormatter.ofPattern("h:mm:ss");
			DateTimeFormatter timeFormatMM = DateTimeFormatter.ofPattern("mm:ss");
			DateTimeFormatter timeFormatM = DateTimeFormatter.ofPattern("m:ss");
			if (theTime.getHour() > 9) return theTime.format(timeFormatHH);
			if (theTime.getHour() > 0) return theTime.format(timeFormatH);
			if (theTime.getMinute() > 9) return theTime.format(timeFormatMM);
			return theTime.format(timeFormatM);
			
		}
		
		// max ZZ
		static String getCharForNumber(int i) {
		    char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		    
		    int small = i % 26;
		    int large = i / 26 -1;
		    if (large >25) return null;
		    
		    //string base;
		    if (large >=0)
		    	return Character.toString(alphabet[large]) + Character.toString(alphabet[small]);
		    else 
		    	return Character.toString(alphabet[small]);
		}
		
		static double calcPaceImpact(double elevation) {
			if (elevation > 25) return 0.4;
			if (elevation > 0) return 0.5;
			if (elevation < -20) return 0.15;
			if (elevation < 0) return 0.3;
			return 0;
		}
		

		

}
