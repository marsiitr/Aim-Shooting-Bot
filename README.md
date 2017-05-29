# Aim-Shooting-Bot
ABSTRACT

The aim of the project is to make a moving bot that will shoot at the target.
The gun is mounted on a platform which will give rotation about one axis and other rotation of the gun will be about the axis in perpendicular direction.
There will be a phone(1) mounted on the bot which will take real time video of the surrounding.
That phone(1) will also give signal to the arduino for controlling the bot.
Video will be live streamed to another phone(2) that will be controlled by the user.
Video coming to user phone(2) will be mapped to real coordinates via image processing.
User can click on any point on the screen at which he wants to shoot.
Then the coordinates of that point will get transferred to other phone(1) using wifi and that phone(1) will give the signal to the arduino.
Then the gun will adjust accordingly with the help of motion along given two axis using 2 servo motors and will shoot at the target.

TEAM MEMBERS
1.Nivya Theresa Jose
2.Gaurav Singhal
3.Sachin Chopra
4.Samarth Gubrele
5.Kapil Golimar
6.Abhinav Tyagi
7.Anupam 
8.Bhupesh Hada

MENTORS
Prashant Shekhar Singh
Krishna
Nitesh Arora
Aman Singh

MECHANICAL 
Ply board                              
Timing belt(4cm) with pulleys
BB Gun
L channel
Breadboard

ELECTRICAL
2 Arduino UNO
Motor driver L298
Servo motor
DC motor
7809 voltage regulator
7806 voltage regulator


CODE

int motor1Pin1 = 3; // pin 2 on L293D IC
int motor1Pin2 = 4; // pin 7 on L293D IC
int enable1Pin = 6; // pin 1 on L293D IC
int motor2Pin1 = 8; // pin 10 on L293D IC
int motor2Pin2 = 9; // pin 15 on L293D IC
int enable2Pin = 11; // pin 9 on L293D IC
int state;
int flag=0;        //makes sure that the serial only prints once the state
int stateStop=0;
void setup() {
    // sets the pins as outputs:
    pinMode(motor1Pin1, OUTPUT);
    pinMode(motor1Pin2, OUTPUT);
    pinMode(enable1Pin, OUTPUT);
    pinMode(motor2Pin1, OUTPUT);
    pinMode(motor2Pin2, OUTPUT);
    pinMode(enable2Pin, OUTPUT);
    // sets enable1Pin and enable2Pin high so that motor can turn on:
    digitalWrite(enable1Pin, HIGH);
    digitalWrite(enable2Pin, HIGH);
    // initialize serial communication at 9600 bits per second:
    Serial.begin(9600);
}

void loop() {
    //if some date is sent, reads it and saves in state
    if(Serial.available() > 0){     
      state = Serial.read();   
      flag=0;
    }   
    // if the state is 'F' the DC motor will go forward
    if (state == 'F') {
        digitalWrite(motor1Pin1, HIGH);
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, LOW);
        digitalWrite(motor2Pin2, HIGH);
        if(flag == 0){
          Serial.println("Go Forward!");
          flag=1;
        }
    }
    
    // if the state is 'R' the motor will turn left
    else if (state == 'R') {
        digitalWrite(motor1Pin1, HIGH); 
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, LOW);
        digitalWrite(motor2Pin2, LOW);
        if(flag == 0){
          Serial.println("Turn LEFT");
          flag=1;
        }
        delay(1500);
        state=3;
        stateStop=1;
    }
    // if the state is 'S' the motor will Stop
    else if (state == 'S' || stateStop == 1) {
        digitalWrite(motor1Pin1, LOW); 
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, LOW);
        digitalWrite(motor2Pin2, LOW);
        if(flag == 0){
          Serial.println("STOP!");
          flag=1;
        }
        stateStop=0;
    }
    // if the state is 'L' the motor will turn right
    else if (state == 'L') {
        digitalWrite(motor1Pin1, LOW); 
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, LOW);
        digitalWrite(motor2Pin2, HIGH);
        if(flag == 0){
          Serial.println("Turn RIGHT");
          flag=1;
        }
        delay(1500);
        state=3;
        stateStop=1;
    }
    // if the state is 'B' the motor will Reverse
    else if (state == 'B') {
        digitalWrite(motor1Pin1, LOW); 
        digitalWrite(motor1Pin2, HIGH);
        digitalWrite(motor2Pin1, HIGH);
        digitalWrite(motor2Pin2, LOW);
        if(flag == 0){
          Serial.println("Reverse!");
          flag=1;
        }
    }
    //For debugging purpose
    //Serial.println(state);
}




#include              //Servo library
 
Servo servo_test;    		//initialize a servo object for the connected servo  
                
int angle = 0;    
 
void setup() 
{ 
  servo_test.attach(9); 		 // attach the signal pin of servo to pin9 of arduino
} 
  
void loop() 
{ 
  for(angle = 0; angle < 180; angle += 1) 	 // command to move from 0 degrees to 180 degrees 
  {                                  
    servo_test.write(angle);              	 //command to rotate the servo to the specified angle
    delay(15);                       
  } 
 
  delay(1000);
  
  for(angle = 180; angle>=1; angle-=5)     // command to move from 180 degrees to 0 degrees 
  {                                
    servo_test.write(angle);              //command to rotate the servo to the specified angle
    delay(5);                       
  } 

    delay(1000);
}







