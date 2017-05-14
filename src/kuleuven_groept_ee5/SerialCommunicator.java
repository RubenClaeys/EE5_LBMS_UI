package kuleuven_groept_ee5;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

public class SerialCommunicator {
	
	private Enumeration<CommPortIdentifier> ports = null;
	private HashMap<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>();
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort serialPort = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private Range range = null;
	private Mode mode = null;
	private SampleFrequency sampleFr = null;
	private BlockingQueue<Integer> queue = null;
	private ArrayList<Integer> samplesAD = new ArrayList<Integer>();  
	private int capacity = 100;
	private int i = 0;

	private volatile boolean shutdown = false;
	
	public SerialCommunicator(){
		searchForPorts();
		mode =(Mode) Main.getUi().getSelectedMode().getSelectedItem();
		range = (Range) Main.getUi().getSelectedRange().getSelectedItem();
		sampleFr = (SampleFrequency) Main.getUi().getSampleFrequency().getSelectedItem();
	}
	
	// Searches for ports and displays them in combobox
	public void searchForPorts() {
		ports = CommPortIdentifier.getPortIdentifiers();
		
		while (ports.hasMoreElements()) {
			CommPortIdentifier currentPort = (CommPortIdentifier) ports.nextElement();

			if (currentPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				Main.getUi().getSelectedPort().addItem(currentPort.getName());
				portMap.put(currentPort.getName(), currentPort);
				
			}
		}
	}

	// Opens the selected serial port and converts it to a serialPort object
	// Creates an inputStream and OutputStream object for the serialPort
	public void connect() {
		String selectedPort = (String) Main.getUi().getSelectedPort().getSelectedItem();
		selectedPortIdentifier = portMap.get(selectedPort);

		CommPort commPort = null;

		try {
			commPort = selectedPortIdentifier.open("PIC", 1000);
			serialPort = (SerialPort) commPort;

			Main.getUi().getOutputArea().setForeground(Color.black);
			Main.getUi().getOutputArea().append(selectedPort + " opened successfully" + "\n");
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());

			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			
			startThreads();
			
			
		} catch (PortInUseException e) {
			Main.getUi().getOutputArea().setForeground(Color.red);
			Main.getUi().getOutputArea().append("Port is in use (" + e.toString() + ")" + "\n");
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());
		} catch (Exception e) {
			Main.getUi().getOutputArea().setForeground(Color.red);
			Main.getUi().getOutputArea().append("Failed to open " + selectedPort + " (" + e.toString() + ")" + "\n");
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());
		}
	}
	
	//Disconnects from the serialPort and closes the in and output stream
	//A volatile boolean is set to stop the threads
	public void disconnect() {
		try {
			shutdown = true;
			
			serialPort.close();
			inputStream.close();
			outputStream.close();

			Main.getUi().getOutputArea().setForeground(Color.black);
			Main.getUi().getOutputArea().append("Disconnected " + "\n");
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());

		} catch (Exception e) {
			Main.getUi().getOutputArea().append("Failed to close " + serialPort.getName() + "(" + e.toString() + ")" + "\n");
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());
		}
	}
	
	
	// Creates two threads which synchronious fill/empty a queue
	public void startThreads() throws InterruptedException{
			shutdown = false;
			queue = new ArrayBlockingQueue<Integer>(capacity);
			
			Thread producerThread = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(10);
						producer();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});

			Thread consumerThread = new Thread(new Runnable(){
				@Override
				public void run() {

					try {
						consumer();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			});
			
			consumerThread.start();
			producerThread.start();
			
	}
	
	// reads data from the serialPort and adds it to the queue
	public void producer() throws InterruptedException{
		while(!shutdown){
//			System.out.println("Thread producer: " + Thread.currentThread().getName());
				try {
					
					int data;
					long begin =0;
					long end = 0;
					
					while((data = inputStream.read()) > -1  ) {
						if(i == 0){begin = System.currentTimeMillis();}
						i++;
						if(i == 10){
							end = System.currentTimeMillis();
							long time = (end - begin)/10;
							long freq = 1/time;	
							Main.getUi().getOutputArea().append(freq + "\n");
						}
						System.out.println(data);
						if(queue.size() < capacity)
							queue.add(data);
							break;
						}
					
					
				}
				catch (IOException e) {
					Main.getUi().getOutputArea().setForeground(Color.red);
					Main.getUi().getOutputArea().append("There was an error while reading data.. (" + e.toString() + ")" + "\n");
					Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());
				}
			}
	}
	
	//takes data out of the queue in a FIFO order
	//in single mode just one sample is taken and send to calculate
	//in continu mode the data is send in bursts of 10 samples
	public void consumer() throws InterruptedException{
		
		while(!shutdown){
			int sample = queue.take();
			Main.getCalc().addSample(sample);
		}
	}
	
	//Writes data to the pic when send button is clicked
	//This is done in the event-dispatch thread
	public void write(String data) {
		try {
			if (data.equals("A")) {	
				byte[] buffer = data.getBytes();
				outputStream.write(buffer, 0, buffer.length);
			}

			else if (data.equals("H")) {
				byte[] buffer = new byte[3];
				buffer[0] = 0x48;
				buffer[1] = generateHeader1();
				buffer[2] = generateHeader2();
				outputStream.write(buffer, 0, buffer.length);
			} else {
				byte[] buffer = data.getBytes();
				outputStream.write(buffer, 0, buffer.length);
			}

		} catch (IOException e) {
			Main.getUi().getOutputArea().setForeground(Color.red);
			Main.getUi().getOutputArea().append("There was an error while writing data.. (" + e.toString() + ")" + "\n");
			Main.getUi().getOutputArea().setCaretPosition(Main.getUi().getOutputArea().getDocument().getLength());
		}
	}
	
	//Generates header byte when a different mode selected and the save button is clicked
	public byte generateHeader1() {
		byte b = 0x00;
		
		switch (mode) {
		case SINGLE:
			switch (range) {
			case FIVE:
				b = 0x01;
				break;
			case THIRTY:
				b = 0x11;
				break;
			case SEVENFIFTY:
				b = 0x21;
				break;
			case EIGHT:
				b=0x31;
				break;
			default:
				b = 0x01;
				break;
			}
			break;

		case CONTINU:
			switch (range) {
			case FIVE:
				b = 0x02;
				break;
			case THIRTY:
				b = 0x12;
				break;
			case SEVENFIFTY:
				b = 0x22;
				break;
			case EIGHT:
				b=0x32;
				break;
			default:
				b = 0x02;
				break;
			}
			break;

		default:
			b = 0x01;
			break;
		}
		return b;
	}
	
	public byte generateHeader2(){
		byte b = 0x00;
		
		switch(sampleFr){
		case ONE:
			b = 0x00;
			break;
			
		case TEN:
			b = 0x01;
			break;
			
		case HUNDRED:
			b = 0x02;
			break;
			
		case TWO_H:
			b = 0x03;
			break;
			
		case FIVE_H:
			b = 0x04;
			break;
			
		case ONE_K:
			b = 0x05;
			break;
			
		case TWO_K:
			b = 0x06;
			break;
			
		case FIVE_K:
			b = 0x07;
			break;
			
		case TEN_K:
			b = 0x08;
			break;
			
		case TWENTYFIVE_K:
			b = 0x09;
			break;
			
		case FIFTY_K:
			b = 0x0A;
			break;
			
		case SEVENTYFIVE_K:
			b = 0x0B;
			break;
			
		default:
			b = 0x00;
			break;
		}
		return b;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public void setSampleFr(SampleFrequency sampleFr){
		this.sampleFr = sampleFr;
	}
}
