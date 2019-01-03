package findthepointnxt;

import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.addon.ColorHTSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class main {

	public static DataOutputStream dataOutputStream;
	public static DataInputStream dataInputStream;
	public static NXTConnection bluetoothConnection;

	public static void main(String[] Args) {
		ColorHTSensor ColorS = new ColorHTSensor(SensorPort.S1);
		CompassHTSensor CompassS = new CompassHTSensor(SensorPort.S2);

		int test = 0;

		lejos.robotics.Color farbe = null;
		while (test < 9000000) {

			farbe = ColorS.getColor();

			LCD.drawInt(farbe.getGreen(), 1, 1);
			LCD.drawInt(farbe.getRed(), 1, 2);
			LCD.drawInt(farbe.getBlue(), 1, 3);

			test = ColorS.getColorID();
		}

		bluetoothConnection = Bluetooth.waitForConnection();
		bluetoothConnection.setIOMode(NXTConnection.RAW);
		dataOutputStream = bluetoothConnection.openDataOutputStream();
		dataInputStream = bluetoothConnection.openDataInputStream();

		int androidmessage = 0;
		boolean end = false;

		LCD.drawString("Wartet auf nachricht", 1, 1);

		while (true) {
			try {
				androidmessage = read();
			} catch (IOException e) {

			}
			LCD.clear();
			LCD.drawString(Integer.toString(androidmessage), 1, 1);

			switch (androidmessage) {

			case 0: {

				Motor.B.stop();
				Motor.C.stop();

			}

			case 1: {

				Motor.B.stop();
				Motor.C.stop();

				Motor.B.setSpeed(50);
				Motor.C.setSpeed(50);

			}
				break;

			case 2: {

				Motor.B.stop();
				Motor.C.stop();

				Motor.B.setSpeed(50);
				Motor.C.setSpeed(-50);

			}
				break;

			case 3: {

				Motor.B.stop();
				Motor.A.stop();

				Motor.B.setSpeed(-50);
				Motor.C.setSpeed(50);

			}
				break;

			case 4: {

				try {
					write(ColorS.getColorID());
				} catch (IOException e) {

				}

			}
				break;

			case 5: {

				// con.senddata((int) CompassS.getDegreesCartesian());

			}
				break;

			case 6: {

				end = true;

			}
				break;

			case 100: {
				drive();
				while(true) {
					if(ColorS.getColorIndexNumber() == 3 || ColorS.getColorIndexNumber() == 2) {
						stop();
						try {
							write(200); // Found green point, finish sequence
						} catch (IOException e) {

						}
					}
					if(ColorS.getColorIndexNumber() == 5 || ColorS.getColorIndexNumber() == 4) {
						stop();
						try {
							write(50); // found red line, waiting for new direction
						} catch (IOException e) {

						}
						break;
					}
				}
			}
				break;

			}

			if (end == true) {
				// con.senddata(99);
				break;

			}

		}

	}

	public static void drive() {
		Motor.B.setSpeed(50);
		Motor.C.setSpeed(50);
		Motor.B.forward();
		Motor.C.forward();
	}
	
	public static void stop() {
		Motor.B.stop(true);
		Motor.C.stop(true);
	}
	

	public static int read() throws IOException {
		return dataInputStream.read();
	}

	public static void write(int x) throws IOException {
		dataOutputStream.write(x);
		dataOutputStream.flush();
	}

}