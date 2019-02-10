#include <iostream>
#include <cv.h>
#include <opencv2/opencv.hpp>
#include <highgui.h>
#include "ntcore_cpp.h"
#include "NetworkTableEntry.h"
#include "NetworkTableInstance.h"

#include <ntcore.h>
#include <networktables/NetworkTable.h>
using std::shared_ptr;

using namespace std;

IplImage* imgTracking=	0;

int lastX1 = -1;
int lastY1 = -1;

int lastX2 = -1;
int lastY2 = -1;

int distanceFromCenterToVisionTarget = 1111;
int distanceRatio = 1; //knownLengthOfVisionTarget / knownDistanceFromVisionTarget;



void trackObject(IplImage* imgThresh){
        CvSeq* contour;  //hold the pointer to a contour
        CvSeq* result;     //hold sequence of points of a contour
        CvMemStorage *storage = cvCreateMemStorage(0); //storage area for all contours
 	//nt::NetworkTableEntry entry;
	//nt::NetworkTableInstance inst = nt::NetworkTableInstance::GetDefault();
	//auto table = inst.GetTable("datatable");
	//entry = table->GetEntry("X");

	shared_ptr<NetworkTable> myTable = NetworkTable::GetTable("SmartDashboard");
	myTable->PutString("Example String", "sadhsahiosdasdasda");

        //finding all contours in the image
        cvFindContours(imgThresh, storage, &contour, sizeof(CvContour), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
  
       //iterating through each contour
       while(contour)
       {
        //obtain a sequence of points of the countour, pointed by the variable 'countour'
       result = cvApproxPoly(contour, sizeof(CvContour), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contour)*0.02, 0);
           
//approx area of target up close(~1-2ft) is 18000, ~5-6ft is 5175
       //if there are 4 vertices  in the contour and the area of the rect is more than 100 pixels
       if(result->total==4 && fabs(cvContourArea(result, CV_WHOLE_SEQ))>1000 )
       {
              //iterating through each point
              CvPoint *pt[4];
              for(int i=0;i<4;i++){
                   pt[i] = (CvPoint*)cvGetSeqElem(result, i);
              }
   
///
	cout << "new below \n";

	int x, a, b, c, d;

		//origin (,0,0) is TOP left. 
//FIND SIDE LENGTHS   --this is straight. not in line (only 1 axis doesnt acct for angle)
	cout << "print before ifs: point  0 y coord = " << pt[0]->y << " and pt 3 y coord = " << pt[3]->y << "\n";
	//if point 0 and 1 are both higher than pts 2 and 3, then Left T )
	if ( (pt[0]->y < pt[3]->y) && (pt[0]->y < pt[2]->y) && (pt[1]->y < pt[3]->y) && (pt[1]->y < pt[2]->y) ) {
//
	//must be 0 is top left		
		  a = pt[1]->x - pt[0]->x; //a is short
		  b = pt[2]->y - pt[1]->y;
		  c = pt[2]->x - pt[3]->x;
		  d = pt[3]->y - pt[0]->y;
		cout << "entered top IF \n"; 


	} else if (  (pt[3]->y < pt[2]->y) && (pt[0]->y < pt[1]->y) && (pt[3]->y < pt[1]->y) && (pt[0]->y < pt[2]->y) ) {
// 
	//must be right target (0 is top right)
		 a = pt[0]->x - pt[3]->x; //a is short
		  b = pt[1]->y - pt[0]->y;
 		  c = pt[1]->x - pt[2]->x;
		  d = pt[2]->y - pt[3]->y;
		cout << "entered bottom IF \n";
	} else {
//	temp values
	a = 48; b = 235; c = 51; d = 241;

	cout << "sides ERROR. Assigned Temp Fake values \n";
	}


	cout << "lengths a, b, c, & d: " << a << " , " << b << " , "  << c << " , "  << d << "\n";

	//averaging similar lengths //remove?
	float e = (a + c) / 2; //first and third sides
	float g = (b + d) / 2;
	float shortSide;
	float longSide;
	bool target = 0;
	bool similarSides = false;
	bool similarSides2 = false;

	//at this point unsure of which (e or g) is the long side

	//vision targets are 5.5 x 2 inches. 5.5/2 = 2.75; 2/5.5 = 0.3636..;

//CHECK for matching sides (if a is close enough to its counterpart across (c)
if ( (a > (c - 10)) && (a < (c + 10)) ) {
  similarSides = true;
}
if ( (b > (d - 10)) && (b < (d + 10)) ) {
  similarSides2 = true;
}

	// DETERMINE LONG and SHORT SIDES  (within range)  ///if ((e / g) == 2.75){
	if ( ((e / g) > 2.74) && ((e/g) < 2.76) && ((g/e) > 0.34)  && ((g/e) < 0.38) ){
 	  //e = long side.  long side found first, target is Right
	 target = 1; longSide = e;  shortSide = g;
	} else if ( ((g/e) > 2.74) && ((g/e) < 2.76) && ((e/g) > 0.34) && ((e/g) < 0.38 ) ){
	  //g = long side.  short side found first, target is Left
	target = 2; longSide = g;  shortSide = e;
	 } else {
	target = -3;   //ignore the shape
	   }

 cout << "target = " << target << "\n";

//triangulate to pare down more distractions
if (target == 1 || target == 2){ //if target is Right side(1) or left side (2) (skip if neither)
	
    if (target ==1) { //if right side
	if ((pt[0]->x > pt[1]->x) && (pt[1]->x > pt[3]->x)){ 
		target = 1;
	} 
	else {target = -3;}
    } 

    if (target == 2) { //if target is left side
	  if ((pt[1]->x > pt[2]->x) && (pt[2]->x > pt[0]->x)){ 
          target = 2;
	} 
	else {target = -3;}
      
    }
	
} 
 	



	//DISTANCE BETWEEN INNER TOP PTS
//	float h;
//	if (target == 2)



///

//only draw it if these conditions are true:
	if ((target == 1 || target == 2) && similarSides && similarSides2) {

	  //print coords
 	  cout << "Point 0 coords (x, y):  " << pt[0]->x << " , " << pt[0]->y << "\n";
	  cout << "Point 1 coords (x, y):  " << pt[1]->x << " , " << pt[1]->y << "\n";
	  cout << "Point 2 coords (x, y):  " << pt[2]->x << " , " << pt[2]->y << "\n";
	  cout << "Point 3 coords (x, y):  " << pt[3]->x << " , " << pt[3]->y << "\n";


         //drawing lines around the quadrilateral //the highest point vrtically is first.
         cvLine(imgTracking, *pt[0], *pt[1], cvScalar(255,0,0),4); //blue SHOULD be top
         cvLine(imgTracking, *pt[1], *pt[2], cvScalar(50,155,255),4); //yellow should be right
         cvLine(imgTracking, *pt[2], *pt[3], cvScalar(0,0,255),4); //red should be bottom
         cvLine(imgTracking, *pt[3], *pt[0], cvScalar(0,255,0),4);
	int centerX = imgTracking->width / 2;
	 int distanceToTarget = a / distanceRatio;
	 
	 if (target == 1) {
		int distanceToCenter =  (pt[0]->x - distanceFromCenterToVisionTarget) - centerX;
	 }
	 if (target == 2) {
		int distanceToCenter =  (pt[0]->x + distanceFromCenterToVisionTarget) - centerX;
	 }
	   

///

     }
		
		  
	   //NetworkTableEntry::ForceSetDouble(1);

	
	     //obtain the next contour
            contour = contour->h_next; 

      }//end of while(contour)


       cvReleaseMemStorage(&storage);
}//end of void trackObject


int main(){

    //Push to NetworkTables 
    NetworkTable::SetClientMode();

    NetworkTable::SetIPAddress("10.23.45.2"); //10.te.am.2

    NetworkTable::Initialize();

    

    

//    return 0;


    //load the video file to the memory
    CvCapture *capture = cvCaptureFromCAM(1); 

    if(!capture){
        printf("Capture failure\n");
        return -1;
    }
      
    IplImage* frame=0;
    frame = cvQueryFrame(capture);           
    if(!frame) return -1;
   
    //create a blank image and assigned to 'imgTracking' which has the same size of original video
    imgTracking=cvCreateImage(cvGetSize(frame),IPL_DEPTH_8U, 3);
    cvZero(imgTracking); //covert the image, 'imgTracking' to black

    cvNamedWindow("Video");     
    
    //iterate through each frames of the video     
    while(true){




        frame = cvQueryFrame(capture);           
        if(!frame) break;
        cvZero(imgTracking);
	frame=cvCloneImage(frame); 
         
        //smooth the original image using Gaussian kernel
        cvSmooth(frame, frame, CV_GAUSSIAN,3,3); 

        //converting the original image into grayscale
      	IplImage* imgGrayScale = cvCreateImage(cvGetSize(frame), 8, 1); 
      	cvCvtColor(frame,imgGrayScale,CV_BGR2GRAY);
          
       //thresholding the grayscale image to get better results
       cvThreshold(imgGrayScale,imgGrayScale,150,255,CV_THRESH_BINARY_INV);
      
    	cvNamedWindow("greyscale img");
    	cvShowImage("greyscale img", imgGrayScale);
      
        //track the possition of the ball
        trackObject(imgGrayScale);

        // Add the tracking image and the frame
        cvAdd(frame, imgTracking, frame);
             
////////


        cvShowImage("Video", frame);
   
        //Clean up used images
        cvReleaseImage(&imgGrayScale);            
        cvReleaseImage(&frame);

        //Wait 10mS
        int c = cvWaitKey(10);
        //If 'ESC' is pressed, break the loop
        if((char)c==27 ) break;      
    }

    cvDestroyAllWindows();
    cvReleaseImage(&imgTracking);
    cvReleaseCapture(&capture);     

    NetworkTable::Shutdown();
    return 0;
}
