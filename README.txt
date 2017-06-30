                                                      ********AIM SHOOTING BOT MOBILE APPLICATION********

***We are using this application for interfacing the aim-shooting-bot with mobile over wifi networking communication system***

**This application does image processing continously and thus detects the target based on its colour and the user can also control the bot with the interface provided in the application**

-   Firstly,a Wi-Fi hotspot is created with the help of a router.
-   Then a server-client network is established by the mobile application being the server 
    and the wifi-module on the bot(in this  case ESP-8266) being the client through connecting both of them to the  corresponding Wi-Fi network created.
-   Through this server-client network,co-ordinates of the target and commands given by the user will be transmitted to the bot from the mobile application.


                  ANDROID APP setup
				  
		The app consists of only a single activity:"MainActivity" which contains all the major image processing code through which it processes the video frame by frame continously
		        and detects the target by its colour which in-case here it is blue or red and transmits the co-ordinates of the centre of the target simultaneously to the Wi-Fi module
				through wifi for further processing to aim the gun towards the target.
				
		We have also created some buttons in the MainActivity which when pressed by the user transmits certain commands to the bot combinedly with the co-ordinates of the target.
				These commands are interpreted by the processor in the bot and the bot executes the functions corresponding to the buttons pressed by the user.In this way 
				the bot is controlled by the user through the interface provided in the application.
				
		The IP Address needed for the client to connect with the server is displayed in the application as soon as the server is created by the application.

		The data is being sent to the client in JSON format through a socket created in the server-client network.