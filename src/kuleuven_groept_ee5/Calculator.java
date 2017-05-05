package kuleuven_groept_ee5;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Calculator {

	private Range range;

	public void setRange(Range range) {
		this.range = range;
	}

	public Calculator(){
	}
	
	public void calculate(ArrayList<Integer> ADsamples){
		
		double[] doubleSamples = new double[ADsamples.size()]; 
		double voltage = 0;
		
		for(int i = 0; i < ADsamples.size(); i++){
			
			int intValue = ADsamples.get(i);
			
			switch(range){
			case EIGHT:
				
				voltage = (intValue / 255) * 8;
				if(voltage>=4.016){
					voltage = ((voltage-4.016)/3.2)*8;
				}
				else{
					voltage = - (1-((voltage-0.314)/3.7))*8;
				}
				break;
			
			case FIVE:
				voltage = (intValue / 255) * 5;
				if(voltage >= 2.509){
					voltage = ((voltage-2.509)/2)*5;
				} else{
					voltage = - (1-((voltage-1.431)/1.078))*5;
				}
				break;
				
			case THIRTY:
				voltage = (intValue / 255) * 25;
				if(voltage >= 12.549){
					voltage = ((voltage-12.549)/9.706)*25;
				}
				else{
					voltage = - (1-((voltage-0.588)/11.961))*25;
				}
				break;
				
			case SEVENFIFTY:
				voltage = ( intValue / 255) * 750;
				if(voltage>=350){
					voltage = ((voltage-350)/158.8)*750;
				}
				else{
					voltage = - (1-((voltage-82.4)/267.6))*750;
				}
				break;
				
			default:
				System.out.println("No range selected --> in default of calculate() " );
				break;
			
			}
			doubleSamples[i] = voltage;	
		}
		toUserInterface(doubleSamples);
		
		
	}
	
	
	public void toUserInterface(double[] doubleSamples){
		for(int i = 0; i < doubleSamples.length; i++){
			switch(range){
			case FIVE:
			case EIGHT:
				Main.getUi().getOscilloscoop().changeDataset(doubleSamples, 10);
			case THIRTY:
				Main.getUi().getOutputArea().append(round(doubleSamples[i],3) + "V"  + "\n");
				break;
			case SEVENFIFTY:
				Main.getUi().getOutputArea().append(round(doubleSamples[i],1) + "mV"  + "\n");
				break;
			default: 
				System.out.println("No range selected --> in default of toUserInteface() " );
				break;		
			}
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());
		}
	}
	
	public double round(double value, int places) {
		if (places < 0){
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}


}
