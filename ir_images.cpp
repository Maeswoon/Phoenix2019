#include <iostream>
#include <cv.h>
#include <opencv2/opencv.hpp>
#include <highgui.h>
#include <ntcore.h>
#include <networktables/NetworkTable.h>
#include "NetworkTableEntry.h"
#include "NetworkTableInstance.h"
#include "ntcore_cpp.h"
//#include <opencv2/cudev/functional/functional.hpp>

using std::shared_ptr;

using namespace std;

IplImage* imgTracking=	0;

int lastX1 = -1;
int lastY1 = -1;

int lastX2 = -1;
int lastY2 = -1;

double pixelToFeet = 0.13333333333333 ;

void trackObject(IplImage* imgThresh) {
  CvSeq* contour;  //hold the pointer to a contour
  CvSeq* result;   //hold sequence of points of a contour
  CvMemStorage *storage = cvCreateMemStorage(0); //storage area for all contours
  //nt::NetworkTableEntry entry;
  //nt::NetworkTableInstance inst = nt::NetworkTableInstance::GetDefault();
  //auto table = inst.GetTable("datatable");
  //entry = table->GetEntry("X");
  
  shared_ptr<NetworkTable> myTable = NetworkTable::GetTable("SmartDashboard");
  
  myTable->PutString("Example String", "sadhsahiosdasdasda");
  
  //finding all contours in the image
  cvFindContours(imgThresh, storage, &contour, sizeof(CvContour), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
  list<double> targets;
  
  //iterating through each contour
  int minTarget = 999;
  while(contour) {
    //obtain a sequence of points of the countour, pointed by the variable 'countour'
    result = cvApproxPoly(contour, sizeof(CvContour), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contour) * 0.02, 0);
    
    //approx area of target up close(~1-2ft) is 18000, ~5-6ft is 5175   ON webcam
    //if there are 4 vertices  in the contour and the area of the rect is more than 100 pixels
    if (result->total == 4 && fabs(cvContourArea(result, CV_WHOLE_SEQ)) > 100 ) {
      //iterating through each point
      CvPoint *pt[4];
      for (int i = 0; i < 4; i++) {
        pt[i] = (CvPoint*)cvGetSeqElem(result, i);
      }
      
      cout << "new below \n";
      
      int x, a, b, c, d;
      int  dist_a, dist_b, dist_c, dist_d;
      bool target = 0; //target -3 is ignorable, target 1 is long side first (R), target 2 is short side first (L)
      //origin (,0,0) is TOP left.
      
      ////CONFIRM TARGET SIDE BY Y VALUES
      
      //if point 0 and 1 are both higher than pts 2 and 3, then Left T )
      if ((pt[0]->y < pt[3]->y) && (pt[0]->y < pt[2]->y) && (pt[1]->y < pt[3]->y) && (pt[1]->y < pt[2]->y)) {
        //must be 0 is top left		(confirmed by y)
        dist_a = pt[1]->x - pt[0]->x; //a is short
        dist_b = pt[2]->y - pt[1]->y;
        dist_c = pt[2]->x - pt[3]->x;
        dist_d = pt[3]->y - pt[0]->y;
        target = 2;
        //cout << "Confirm by Y: yes t2 \n";
        
        a = sqrt((pt[1]->x - pt[0]->x)^2 + (pt[1]->y - pt[0]->y)^2);
        b = sqrt((pt[2]->x - pt[1]->x)^2 + (pt[2]->y - pt[1]->y)^2); //2, 1
        c = sqrt((pt[2]->x - pt[3]->x)^2 + (pt[2]->y - pt[3]->y)^2);
        d = sqrt((pt[3]->x - pt[0]->x)^2 + (pt[3]->y - pt[0]->y)^2); // 3,0
        cout << "point 1 (inner top) of left target: " << pt[1]->x << ", " << pt[1]->y << "\n";
        
      } else if ((pt[3]->y < pt[2]->y) && (pt[0]->y < pt[1]->y) && (pt[3]->y < pt[1]->y) && (pt[0]->y < pt[2]->y)) {
        //must be right target (0 is top right) (confirmed by y)
        dist_a = pt[0]->x - pt[3]->x; //a is long
        dist_b = pt[1]->y - pt[0]->y;
        dist_c = pt[1]->x - pt[2]->x;
        dist_d = pt[2]->y - pt[3]->y;
        target = 1;
        //cout << "entered bottom IF \n";
        // cout << "Confirm by Y: yes t1 \n";
        //dist_a
        a = sqrt((pt[3]->x - pt[0]->x)^2 + (pt[3]->y - pt[0]->y)^2);   //0,3
        b = sqrt((pt[0]->x - pt[1]->x)^2 + (pt[0]->y - pt[1]->y)^2); //1, 0
        c = sqrt((pt[1]->x - pt[2]->x)^2 + (pt[1]->y - pt[2]->y)^2);   //21
        d = sqrt((pt[3]->x - pt[2]->x)^2 + (pt[3]->y - pt[2]->y)^2); // 2, 3
        
        cout << "point 0 (inner top) of right target: " << pt[0]->x << ", " << pt[0]->y << "\n";
        
      } else {
        //temp values
        dist_a = 48;
        dist_b = 235;
        dist_c = 51;
        dist_d = 241;
        target = -3;
        //cout << "sides ERROR. Assigned Temp Fake values \n";
        //cout << "Fail by Y: t-3 \n";
      }
      
      ////CONFIRM TARGET SIDE BY X VALUES
      /*
      if ( pt[3]->x < pt[1]->x && pt[3]->x < pt[2]->x && pt[0]->x < pt[1]->x && pt[0]->x < pt[2]->x) {
      target = 2; //left target (confirmed by x)
      }
      else if ( pt[3]->x < pt[0]->x && pt[3]->x < pt[1]->x && pt[2]->x < pt[1]->x && pt[2]->x < pt[0]->x ) {
      target = 1; //right target confirmed by x
      }
      else { target = -3; }
      */
      
      //cout << "lengths dist_a, dist_b, dist_c, & dist_d: " << dist_a << " , " << dist_b << " , "  << dist_c << " , "  << dist_d << "\n";
      
      bool similarSides = false;
      bool similarSides2 = false;
      
      //CHECK for matching sides (if a is close enough to its counterpart across (c)
      if ((dist_a > (dist_c - 2)) && (dist_a < (dist_c + 2))) {
        similarSides = true;
      }
      if ((dist_b > (dist_d - 2)) && (dist_b < (dist_d + 2))) {
        similarSides2 = true;
      }
      
      //averaging similar lengths //remove?
      float e = (dist_a + dist_c) / 2; //first and third sides
      float g = (dist_b + dist_d) / 2;
      float shortSide;
      float longSide;
      int realTarget = 0;
      
      //vision targets are 5.5 x 2 inches. 5.5/2 = 2.75; 2/5.5 = 0.3636..;
      
      //ENSURE shape is taller than it is wide
      int min = 0; //5
      
      if (target == 1) { //if right target (a & c are long sides) (0 is top right)
        min = e *0.8; //threshhold is 80% of height
        
        if (e > g ) {
          target ==1;
        } else {
          //cout << "Taller than Wide: t1 Fail a \n";
          target ==-3;
        }
        
        if (dist_a > (dist_b + min) && dist_a > (dist_d +min) && dist_c > (dist_b+min) && dist_c > (dist_d+min)      && pt[0]->y < pt[1]->y - min  && pt[3]->y < pt[2]->y - min ) {
          target = 1; //good target if long sides are longer than shorts
        } else {
          //cout << "Taller than Wide: t1 Fail b\n";
          target = -3;
        }
      }
      
      if (target == 2) { //target is left (b & d are long sides) (0 it top left)
        min = g *0.8;
        if (g > e ) {
          target ==2;
        } else {
          target ==-3;
          /*cout << "Taller than Wide: t2 Fail a \n";*/
        }
        
        if (dist_b > (dist_a + min) && dist_b > (dist_c + min) && dist_d > (dist_a+ min) && dist_d > (dist_c+ min)   && pt[0]->y < pt[3]->y - min  && pt[1]->y < pt[2]->y - min ) {
          target = 2; //good target if long sides are longer than shorts
        } else {
          target = -3;
          /* cout << "Taller than Wide: t2 Fail b\n"; */
        }
      }
      
      // DETERMINE LONG and SHORT SIDES  (within range)  ///if ((e / g) == 2.75) {
      if ( ((e / g) > 2.74) && ((e/g) < 2.76) && ((g/e) > 0.34)  && ((g/e) < 0.38) ) {
        //e = long side.  long side found first, target is Right
        target = 1;
        longSide = e;
        shortSide = g;
        
      } else if ( ((g/e) > 2.74) && ((g/e) < 2.76) && ((e/g) > 0.34) && ((e/g) < 0.38 ) ) {
        //g = long side.  short side found first, target is Left
        target = 2;
        longSide = g;
        shortSide = e;
      } else {
        target = -3;   //ignore the shape
        // cout << "Long Short: Fail \n";
      }
      
      int dist1 = (pt[1]->x - pt[0]->x) * (pt[1]->x - pt[0]->x) + (pt[1]->y - pt[0]->y) * (pt[1]->y - pt[0]->y);
      int dist2 = (pt[2]->x - pt[1]->x) * (pt[2]->x - pt[1]->x) + (pt[2]->y - pt[1]->y) * (pt[2]->y - pt[1]->y);
      //cout << "dist1 " << dist1 << " dist2 " << dist2 << "\n";
      
      // DETERMINE LONG and SHORT SIDES  (within range)  ///if ((e / g) == 2.75) {
      if ( dist1 > dist2 ) {
        //e = long side.  long side found first, target is Right
        realTarget = 1;
      } else if ( dist1 < dist2) {
        //g = long side.  short side found first, target is Left
        realTarget = 2;
      }
      
      
      /*
      //triangulate to pare down more distractions
      if (target == 1 || target == 2) { //if target is Right side(1) or left side (2) (skip if neither)
      
      if (target ==1) { //if right side
        if ((pt[0]->x > pt[1]->x) && (pt[1]->x > pt[3]->x)) {
          target = 1;
        } else {
          target = -3;
          cout << "triangulate: Fail 1\n";
        }
      }
      
      if (target == 2) { //if target is left side
      if ((pt[1]->x > pt[2]->x) && (pt[2]->x > pt[0]->x)) {
      target = 2;
      }
      else {target = -3;
      cout << "triangulate: Fail 2 \n";
      }
      
      }
      
      }
      */
      
      ////  angle from bottom point to horizontal line
      //cv::cudev::atan2_func<>;
      
      //DISTANCE BETWEEN INNER TOP PTS
      //float h;
      //if (target == 2)
      

      //only draw it if these conditions are true:
      if ((target == 1 || target == 2) && similarSides && similarSides2 && pt[0]->y > imgTracking->height / 4) {
        int h = pt[2]->y - pt[0]->y;
        
        //cout << "REAL target = " << realTarget << "\n";
        //print coords
        /*cout << "Point 0 coords (x, y):  " << pt[0]->x << " , " << pt[0]->y << "\n";
        cout << "Point 1 coords (x, y):  " << pt[1]->x << " , " << pt[1]->y << "\n";
        cout << "Point 2 coords (x, y):  " << pt[2]->x << " , " << pt[2]->y << "\n";
        cout << "Point 3 coords (x, y):  " << pt[3]->x << " , " << pt[3]->y << "\n";
        */
        
        //drawing lines around the quadrilateral //the highest point vrtically is first.
        cvLine(imgTracking, *pt[0], *pt[1], cvScalar(255,0,0),4); //blue SHOULD be top
        cvLine(imgTracking, *pt[1], *pt[2], cvScalar(50,155,255),4); //yellow should be right
        cvLine(imgTracking, *pt[2], *pt[3], cvScalar(0,0,255),4); //red should be bottom
        cvLine(imgTracking, *pt[3], *pt[0], cvScalar(0,255,0),4);
        double centerX = imgTracking->width / 2.0;
        //double distanceToTarget = (focalLengthDistance * height) / h;
        
        
        //cout << "pt[0].x " << pt[0]->x << " for tergt " << target << "\n";
        //	 shared_ptr<NetworkTable> visionTable = NetworkTable::GetTable("Vision");
        //	 visionTable->PutNumber("Distance", distanceToTarget);
        //double  distanceFromCenterToVisionTarget = focalLengthCenter * lengthCenter / distanceToTarget;
        
        double pixelCount = 30;
        double distanceToCenter = 0;
        if (realTarget == 1) {
          distanceToCenter = centerX - (pt[0]->x - pixelCount);
          
        }
        if (realTarget == 2) {
          distanceToCenter = centerX -  (pt[1]->x + pixelCount);
          
        }
        distanceToCenter = distanceToCenter * pixelToFeet;
        if (h > 0) {
          cout << " and center = " << distanceToCenter << " and taret = " << target << " and realtarget " << realTarget << "\n";
        }
        if (abs(distanceToCenter) < abs(minTarget)) {
          //cout << "Set min to " << distanceToCenter << "\n";
          minTarget = distanceToCenter;
        }
        //cout << distanceToCenter << " to center\n";
        
      }
    }

    //NetworkTableEntry::ForceSetDouble(1);
    
    //obtain the next contour
    contour = contour->h_next;
    
  } //end of while(contour)
  if (minTarget != 999) {
    shared_ptr<NetworkTable> visionTable = NetworkTable::GetTable("Vision");
    visionTable->PutNumber("DistanceCenter", minTarget);
  }
  
  //cout << "target distance " << minTarget << "\n";

  cvReleaseMemStorage(&storage);
} //end of void trackObject

int main() {
  
  //Push to NetworkTables
  NetworkTable::SetClientMode();
  NetworkTable::SetIPAddress("10.23.42.2"); //10.te.am.2
  NetworkTable::Initialize();
  
  //    return 0;
  
  //load the video file to the memory
  CvCapture *capture = cvCaptureFromCAM(1);
  
  if(!capture) {
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
  while(true) {
    
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
    cvThreshold(imgGrayScale,imgGrayScale,160,255,CV_THRESH_BINARY_INV); //threshold was 150 (of 255) //180
    
    cvNamedWindow("greyscale img");
    cvShowImage("greyscale img", imgGrayScale);
    
    //track the possition of the ball
    trackObject(imgGrayScale);
    
    // Add the tracking image and the frame
    cvAdd(frame, imgTracking, frame);
    
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
