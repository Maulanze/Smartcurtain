package allers.service;

import java.awt.*;
import java.awt.event.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import sun.management.Sensor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Service
public class RaspberryService {

    //Defintions
    // Wieviele CM zum triggern?
    public static int TriggerDistance = 15;

    //Outputs
    public static Pin GPIO_X1 = RaspiPin.GPIO_02;
    public static Pin GPIO_X2 = RaspiPin.GPIO_03;

    //Inputs
    public static Pin GPIO_S1 = RaspiPin.GPIO_04;
    public static Pin GPIO_S2 = RaspiPin.GPIO_05;

    //GPIO Sesnor Trigger
    public static Pin GPIO_TRIGGER = RaspiPin.GPIO_22;
    //GPIO Sensiore EchoPin
    public static Pin GPIO_OUTPUT = RaspiPin.GPIO_23;

    //GPIO Sesnor Trigger2
    public static Pin GPIO_TRIGGER2 = RaspiPin.GPIO_28;
    //GPIO Sensiore EchoPin2
    public static Pin GPIO_OUTPUT2 = RaspiPin.GPIO_29;

    //Statics
    public static final int open = 1;
    public static final int close = 2;
    public static final int neutral = 3;
    public static final int continues = 4;
    public static final int opening = 5;
    public static final int closing = 6;


    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput X1 = gpio.provisionDigitalOutputPin(RaspberryService.GPIO_X1, "X1", PinState.LOW);
    final GpioPinDigitalOutput X2 = gpio.provisionDigitalOutputPin(RaspberryService.GPIO_X2, "X2", PinState.LOW);


    //final GpioPinDigitalInput S1 = gpio.provisionDigitalInputPin(RaspberryService.GPIO_S1, PinPullResistance.PULL_DOWN);
    //final GpioPinDigitalInput S2 = gpio.provisionDigitalInputPin(RaspberryService.GPIO_S2, PinPullResistance.PULL_DOWN);

    //Echo Sensor 1
    final GpioPinDigitalOutput sensorTriggerPin = gpio.provisionDigitalOutputPin(RaspberryService.GPIO_TRIGGER); // Trigger pin as OUTPUT
    final GpioPinDigitalInput sensorEchoPin = gpio.provisionDigitalInputPin(RaspberryService.GPIO_OUTPUT, PinPullResistance.PULL_DOWN); // Echo pin as INPUT

    //Echo Sensor 2
    final GpioPinDigitalOutput sensorTriggerPin2 = gpio.provisionDigitalOutputPin(RaspberryService.GPIO_TRIGGER2); // Trigger pin as OUTPUT
    final GpioPinDigitalInput sensorEchoPin2 = gpio.provisionDigitalInputPin(RaspberryService.GPIO_OUTPUT2, PinPullResistance.PULL_DOWN); // Echo pin as INPUT


    int state =0 ;

    public RaspberryService() {
        this.state = RaspberryService.neutral;
        X1.setShutdownOptions(true, PinState.LOW);
        X2.setShutdownOptions(true, PinState.LOW);

        stopCurtain();


   //  S1.addListener(new GpioPinListenerDigital() {
   //      @Override
   //      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
   //          // display pin state on console
   //          System.out.println(" --> GPIO PIN S1 STATE CHANGE: " + event.getPin() + " = " + event.getState());
   //          if (S1.getState().isLow()) {
   //              stopCurtain();
   //          }
   //      }
   //
   //  });
   //
   //  S2.addListener(new GpioPinListenerDigital() {
   //      @Override
   //      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
   //          // display pin state on console
   //          System.out.println(" --> GPIO PIN S2 STATE CHANGE: " + event.getPin() + " = " + event.getState());
   //          if (S2.getState().isLow()) {
   //              stopCurtain();
   //          }
   //      }
   //
   //  });


    }

    public void setState(int state) {
        this.state = state;


    }

    @Scheduled(fixedDelay = 1000)
    public void scheduledTask() {
        System.out.println("Scheduled Task wurde ausgeführt");// + isDistanceSmallerThan(5));
        try {
        if((this.state!= RaspberryService.continues) && (this.state!= RaspberryService.closing) && (this.state!= RaspberryService.opening)  ){
            stopCurtain();
            Thread.sleep(1000);
            switch (this.state){
                case RaspberryService.close:
                    closeCurtain();
                    this.state = RaspberryService.closing;
                    break;
                case RaspberryService.open:
                    openCurtain();
                    this.state = RaspberryService.opening;
                    break;
                case  RaspberryService.neutral:
                    stopCurtain();
                    this.state = RaspberryService.continues;
                    break;
            }
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


   @Scheduled(fixedDelay = 250)
   public void checkIfCurtainEndstate() {
       //System.out.println("Curtainmid: " + isCurtainmid(5));
       //System.out.println("CurtainOutside: " + isCurtainOutside(5));
           if ((this.state == RaspberryService.closing && isCurtainmid(RaspberryService.TriggerDistance)) || (this.state == RaspberryService.opening && isCurtainOutside(RaspberryService.TriggerDistance))) {
                  this.state = RaspberryService.neutral;
               System.out.println("Zielstellung ist erreicht");
           }

   }




    private void openCurtain() {
        System.out.println("Starte 'Open Curtain'");
        stopCurtain();
        try {
            if ( !isCurtainOutside(RaspberryService.TriggerDistance)) {
                Thread.sleep(800);
                stopCurtain();
                X1.high();
            }
            if (isCurtainOutside(RaspberryService.TriggerDistance)) {
                System.out.println("Nicht möglich bereits offen");
                this.state = RaspberryService.neutral;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void closeCurtain() {
        System.out.println("Starte 'close Curtain'");
        stopCurtain();
        try {
            if (!isCurtainmid(RaspberryService.TriggerDistance)) {
                Thread.sleep(800);
                stopCurtain();
                X2.high();

            }
            if (isCurtainmid(RaspberryService.TriggerDistance)) {
                System.out.println("Bereits im geschlossenen Zustand");
                this.state = RaspberryService.neutral;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopCurtain() {
        X1.low();
        X2.low();

    }

    public boolean isCurtainmid(int cm) {
        try {
            sensorTriggerPin.high(); // Make trigger pin HIGH
            Thread.sleep((long) 0.01);// Delay for 10 microseconds
            sensorTriggerPin.low(); //Make trigger pin LOW
            long timeoutTime = System.nanoTime() + 1000000000; // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.

            while (sensorEchoPin.isLow()) { //Wait until the ECHO pin gets HIGH
                if(System.nanoTime() == timeoutTime){
                    return false;
                }
            }
            long startTime = System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.
            while (sensorEchoPin.isHigh()) { //Wait until the ECHO pin gets LOW
                if(System.nanoTime() == timeoutTime){
                    return false;
                }
            }
            long endTime = System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.

            return ((((endTime - startTime) / 1e3) / 2) / 29.1) < cm; //Check whether Distance is smaller than 5 (cm)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isCurtainOutside(int cm) {
        try {
            sensorTriggerPin2.high(); // Make trigger pin HIGH
            Thread.sleep((long) 0.01);// Delay for 10 microseconds
            sensorTriggerPin2.low(); //Make trigger pin LOW
            long timeoutTime = System.nanoTime() + 1000000000; // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.

            while (sensorEchoPin2.isLow()) { //Wait until the ECHO pin gets HIGH
                if(System.nanoTime() == timeoutTime){
                    return false;
                }
            }
            long startTime = System.nanoTime(); // Store the surrent time to calculate ECHO pin HIGH time.
            while (sensorEchoPin2.isHigh()) { //Wait until the ECHO pin gets LOW
                if(System.nanoTime() == timeoutTime){
                    return false;
                }
            }
            long endTime = System.nanoTime(); // Store the echo pin HIGH end time to calculate ECHO pin HIGH time.

            return ((((endTime - startTime) / 1e3) / 2) / 29.1) > cm; //Check whether Distance is smaller than x (cm)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
