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
	
		static double calcPaceImpact(double elevation) {
			if (elevation > 25) return 0.4;
			if (elevation > 0) return 0.5;
			if (elevation < -20) return 0.15;
			if (elevation < 0) return 0.3;
			return 0;
		}
		
		static LocalTime AddTime(LocalTime FirstTime,LocalTime SecondTime) {
			FirstTime=FirstTime.plusHours(SecondTime.getHour());
			FirstTime=FirstTime.plusMinutes(SecondTime.getMinute());
			FirstTime=FirstTime.plusSeconds(SecondTime.getSecond());
			FirstTime=FirstTime.plusNanos(SecondTime.getNano());
			return FirstTime;
		}
		
		static LocalTime SubtractTime(LocalTime FirstTime,LocalTime SecondTime) {
			FirstTime=FirstTime.minusHours(SecondTime.getHour());
			FirstTime=FirstTime.minusMinutes(SecondTime.getMinute());
			FirstTime=FirstTime.minusSeconds(SecondTime.getSecond());
			FirstTime=FirstTime.minusNanos(SecondTime.getNano());
			return FirstTime;
		}

		
}
