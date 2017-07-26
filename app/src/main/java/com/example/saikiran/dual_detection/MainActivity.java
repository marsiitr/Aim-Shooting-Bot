package com.example.saikiran.dual_detection;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;


import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.widget.TextView;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;



public class MainActivity extends Activity implements CvCameraViewListener2 {



    private ServerSocket serverSocket;
    private Socket socket;

    Handler updateConversationHandler;

    Thread serverThread = null;

    private TextView text;

    public static final int SERVERPORT = 8888;

    private JSONObject obj;
    private JSONObject obj1;

//    private UsbManager usbManager;
//    private UsbDevice deviceFound;
//    private UsbDeviceConnection usbDeviceConnection;
//    private UsbInterface usbInterfaceFound = null;
//    private UsbEndpoint endpointOut = null;
//    private UsbEndpoint endpointIn = null;

    private Mat mRgba;
    private Mat mHsv;
    private Mat mHsv1;
    private Mat mMask;
    private Mat mMask1;
    private Mat mDilated;
    public int i;
    public double k[]={0.0};
    private static final String TAG = "MainActivity";
    DataOutputStream dataOutputStream = null;

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    static {
//        System.loadLibrary("MyLib");
        System.loadLibrary("opencv_java3");
    }

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        //create the socket server object



        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();

        //Code below for displaying the IP Address of the server created by the mobile

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        Log.i(TAG,""+ipAddress);
        TextView tView= (TextView) findViewById(R.id.textView);
        tView.setText(ipAddress);
    //    usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);



        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setMaxFrameSize(480, 320);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//            setDevice(device);
//        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//            if (deviceFound != null && deviceFound.equals(device)) {
//                setDevice(null);
//            }
//        }

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
/*
    private void setDevice(UsbDevice device) {
        usbInterfaceFound = null;
        endpointOut = null;
        endpointIn = null;

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbif = device.getInterface(i);

            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;

            int tEndpointCnt = usbif.getEndpointCount();
            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    if (usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT) {
                            tOut = usbif.getEndpoint(j);
                        } else if (usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN) {
                            tIn = usbif.getEndpoint(j);
                        }
                    }
                }

                if (tOut != null && tIn != null) {
                    // This interface have both USB_DIR_OUT
                    // and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                    usbInterfaceFound = usbif;
                    endpointOut = tOut;
                    endpointIn = tIn;
                }
            }

        }

        if (usbInterfaceFound == null) {
            return;
        }

        deviceFound = device;

        if (device != null) {
            UsbDeviceConnection connection =
                    usbManager.openDevice(device);
            if (connection != null &&
                    connection.claimInterface(usbInterfaceFound, true)) {
                usbDeviceConnection = connection;
                Thread thread = new Thread((Runnable) this);
                thread.start();

            } else {
                usbDeviceConnection = null;
            }
        }
    }

    private void sendCommand(int control) {
        synchronized (this) {

            if (usbDeviceConnection != null) {
                byte[] message = new byte[1];

                message[0] = (byte)list_x[0];
                message[1]=  (byte)list_x[1];
                message[2]=  (byte)k[0];
                usbDeviceConnection.bulkTransfer(endpointOut,
                        message, message.length, 0);
            }
        }
    }


    public void run(){

        ByteBuffer buffer = ByteBuffer.allocate(1);
        UsbRequest request = new UsbRequest();
        request.initialize(usbDeviceConnection, endpointIn);
        while (true) {
            request.queue(buffer, 1);
            if (usbDeviceConnection.requestWait() == request) {
                byte rxCmd = buffer.get(0);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                break;
            }
        }

    }
*/

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHsv = new Mat(height, width, CvType.CV_8UC3);
        mMask = new Mat(height, width, CvType.CV_8UC1);
        mDilated = new Mat(height, width, CvType.CV_8UC2);
        mMask1 = new Mat(height, width, CvType.CV_8UC1);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    int list_x[]= {0,0};
    int list_y[] = {0,0};


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba, mHsv, Imgproc.COLOR_RGB2HSV, 3);


        for(int i=0;i<2;i++) {

            if(i==0){
                Scalar lowerThreshold = new Scalar(0, 55, 55); // Yellow color – lower hsv values
                Scalar upperThreshold = new Scalar(10, 255, 255); // Yellow color0 – higher hsv values
                Core.inRange(mHsv, lowerThreshold, upperThreshold, mMask);
                Scalar lowerThreshold1 = new Scalar(170, 55, 55); // Yellow color – lower hsv values
                Scalar upperThreshold1 = new Scalar(179, 255, 255); // Yellow color0 – higher hsv values
                Core.inRange(mHsv, lowerThreshold1, upperThreshold1, mMask1);
                Core.bitwise_or(mMask, mMask1, mMask);}

            else if (i == 1) {
                Scalar lowerThreshold = new Scalar(110, 55, 55); // blue color – lower hsv values
                Scalar upperThreshold = new Scalar(130, 255, 255); // blue\ color – higher hsv values
                Core.inRange(mHsv, lowerThreshold, upperThreshold, mMask);
            }

            Imgproc.dilate(mMask, mDilated, new Mat());

            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(mDilated, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); //Finding contours

            //if (contours.size() < 1)
            //   break;
            if (contours.size()!=0){
            double maxArea = -1;
            int maxAreaIdx = -1;
            for (int idx = 0; idx < contours.size(); idx++) {
                Mat contour = contours.get(idx);
                double contourarea = Imgproc.contourArea(contour);
                if (contourarea > maxArea) {
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                }
            }


            ////////////////////////////////////rectangle
            //for max area contour
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxAreaIdx).toArray());
            RotatedRect rect0 = Imgproc.minAreaRect(contour2f);
            MatOfPoint box0 = new MatOfPoint();
            Imgproc.boxPoints(rect0, box0);

            // List<MatOfPoint> boxcontours = new ArrayList<MatOfPoint>();
            //boxcontours.set(0,box0);
            Imgproc.drawContours(mRgba, contours, maxAreaIdx, new Scalar(255, 255, 255), 2);
           //Imgproc.drawContours(mRgba, contours, maxAreaIdx2, new Scalar(255, 255, 255), 2);


            list_x[i] = (int)(rect0.center.x-240);
            //list_y[i] = -(rect0.center.y-160);
            }else {
                list_x[i]=0;
                list_y[i]=0;
            }
        }

        //String jsondata = "{\"xdata\":\"list_x\",\"ydata\":\"list_y\",\"angledata\":\"list_angle\"}";
        // data = json.dumps(jsondata);
        try {


            JSONArray arrayx = new JSONArray();
            JSONArray arrayy = new JSONArray();
            JSONArray arrayc=new JSONArray();

            for (int i = 0; i < list_x.length; i++) {
                arrayx.put(list_x[i]);
                arrayy.put(list_y[i]);
            }

            arrayc.put(k[0]);

            obj =new JSONObject();
            obj.put("xdata",arrayx);
            obj.put("ydata",arrayy);
            obj.put("com",arrayc);

            Log.d("My App", obj.toString());
        }catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" +  "\"");
        }


Log.i(TAG,""+obj.toString());
        //  Log.i(TAG,""+list_x+","+list_y+",          "+list_ang+",         "+rect1.center.x+","+rect1.center.y);



        /////////////////DATATRANSFER//////////////



        return mRgba;
    }

    public void F(View view) {
        try {
            k[0]=1;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void B(View view) {
        try {
             k[0]=2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    public void L(View view) {
        try {
            k[0]=3;
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    public void R(View view) {
        try {
        k[0]=4;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void S(View view) {
        try {
        k[0]=5;
        } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void St(View view) {
        try {
        k[0]=0;
        }catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void Rd(View view) {
        try {
        k[0]=7;
        }catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void Bl(View view) {
        try {
        k[0]=6;
        }catch (Exception e) {
                e.printStackTrace();
            }
    }




   class ServerThread implements Runnable {

        public void run() {
            socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {


                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();

                    // updateConversationHandler.post(new updateUIThread(read));
                    Log.i(TAG,""+read);


                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(clientSocket.getOutputStream())),
                            true);
                    out.println(obj.toString());


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    } }






