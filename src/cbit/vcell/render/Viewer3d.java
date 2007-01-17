package cbit.vcell.render;

/**
 * Insert the type's description here.
 * Creation date: (11/30/2003 11:40:23 AM)
 * @author: Jim Schaff
 */
public class Viewer3d {
	private Camera _camera = new Camera();
	//private Light _lights = new Light[0];
	private Vect3d _currPoint = new Vect3d();
	private RECT		_rectClip;
	private POINT		_center;
	private POINT		_size;

	private boolean		_bExtentInProgress = false;
	private RECT		_screenExtent;

	private class RECT {
		double right = 0;
		double left = 0;
		double top = 0;
		double bottom = 0;
		public void set(RECT r){
			right = r.right;
			left = r.left;
			top = r.top;
			bottom = r.bottom;
		}
	}
	private class POINT {
		double x = 0;
		double y = 0;
		public void zero() {
			x = 0;
			y = 0;
		}
	}
	cbit.vcell.geometry.gui.SurfaceCanvas _drawArea = null;
	
/**
 * Viewer3d constructor comment.
 */
public Viewer3d() {
   updateSize();  

   _screenExtent.left=10000;
   _screenExtent.right=-10000;
   _screenExtent.top=10000;
   _screenExtent.bottom=-10000;
   _rectClip.left=10000;
   _rectClip.right=-10000;
   _rectClip.top=10000;
   _rectClip.bottom=-10000;
   _currPoint.zero();
   _center.x=0; _center.y=0;
   _size.x=0; _size.y=0;
}
public void beginExtents()
{
   //assert(_bExtentInProgress==FALSE);
   _screenExtent.left=10000;
   _screenExtent.right=-10000;
   _screenExtent.top=10000;
   _screenExtent.bottom=-10000;
   _bExtentInProgress=true;
}
public boolean Clip(POINT pt1, POINT pt2)
{   
int       point1, point2;
double       dx,dy;
int count;                   
final int LEFTOF  = 0x0001;
final int RIGHTOF = 0x0002;
final int BELOW   = 0x0004;
final int ABOVE   = 0x0008;

   for (count=0;count<10;count++) { 
     /*----------------------------------------------------------------*/
     /* point 1 and 2 in bounds ?                                      */
     /*----------------------------------------------------------------*/
     point1 = point2 = 0;
     if (pt1.x < _rectClip.left)     point1 |= LEFTOF;
     if (pt1.x > _rectClip.right)    point1 |= RIGHTOF;
     if (pt1.y < _rectClip.top)      point1 |= ABOVE;
     if (pt1.y > _rectClip.bottom)   point1 |= BELOW;
     if (pt2.x < _rectClip.left)     point2 |= LEFTOF;
     if (pt2.x > _rectClip.right)    point2 |= RIGHTOF;
     if (pt2.y < _rectClip.top)      point2 |= ABOVE;
     if (pt2.y > _rectClip.bottom)   point2 |= BELOW;

     /*----------------------------------------------------------------*/
     /* if both points inside, done                                    */
     /*----------------------------------------------------------------*/
     if ((point1 | point2) == 0) {
        return true;
     }

     /*----------------------------------------------------------------*/
     /* if line falls entirely outside, don't draw, done !             */
     /*----------------------------------------------------------------*/
     if ((point1 & point2) != 0) {
        return true;
     }

     /*----------------------------------------------------------------*/
     /* one or more points bad, find intercepts                        */
     /*----------------------------------------------------------------*/

     /* calculate slope of line 'y = mx + b' */
     dy = pt2.y - pt1.y;
     dx = pt2.x - pt1.x; 
     
     if (point1 != 0) {   /* chop away at point 1 */
       if ((point1 & LEFTOF) != 0) {
         pt1.y += (int)((_rectClip.left - pt1.x) * (double)dy/dx);
         pt1.x = _rectClip.left;
       }else
         if ((point1 & RIGHTOF) != 0) {
           pt1.y += (int)((_rectClip.right - pt1.x) * (double)dy/dx);
           pt1.x = _rectClip.right;
         }else
           if ((point1 & BELOW) != 0) {
             pt1.x += (int)((_rectClip.bottom - pt1.y) * (double)dx/dy);
             pt1.y = _rectClip.bottom;
           }else
             if ((point1 & ABOVE) != 0) {
               pt1.x += (int)((_rectClip.top - pt1.y) * (double)dx/dy);
               pt1.y = _rectClip.top;
             }
     }else{
       if (point2 != 0) { 
         if ((point2 & LEFTOF) != 0) {
           pt2.y += (int)((_rectClip.left - pt2.x) * (double)dy/dx);
           pt2.x = _rectClip.left;
         }else
           if ((point2 & RIGHTOF) != 0) {
             pt2.y += (int)((_rectClip.right - pt2.x) * (double)dy/dx);
             pt2.x = _rectClip.right;
           }else
             if ((point2 & BELOW) != 0) {
               pt2.x += (int)((_rectClip.bottom - pt2.y) * (double)dx/dy);
               pt2.y = _rectClip.bottom;
             }else
               if ((point2 & ABOVE) != 0) {
                 pt2.x += (int)((_rectClip.top - pt2.y) * (double)dx/dy);
                 pt2.y = _rectClip.top;
               }
       }
     }               
   } /* end of for loop */ 
   return false;
}
public boolean draw()
{
   return false;
}
public void endExtents()
{
   //assert(_bExtentInProgress);
   _bExtentInProgress=false;
}
public void getExtents(RECT screen)
{
   //assert(_bExtentInProgress==FALSE);
   screen.set(_screenExtent);
}
/////////////////////////////////////////////////////////////////////////////
// Viewer drawing
//
public void lineTo(java.awt.Graphics2D g2d, Vect3d point)
{
Vect3d v1 = new Vect3d();
Vect3d v2 = new Vect3d();
POINT pt1 = new POINT();
POINT pt2 = new POINT();

  if (_camera.projectLine(_currPoint,point,v1,v2)) {
     pt1.x = _center.x + (int)((_size.x)/2.0*v1.q[0]);
     pt1.y = _center.y + (int)((_size.y)/2.0*v1.q[1]);
     pt2.x = _center.x + (int)((_size.x)/2.0*v2.q[0]);
     pt2.y = _center.y + (int)((_size.y)/2.0*v2.q[1]);
     if (_bExtentInProgress){
        _screenExtent.left = Math.min(_screenExtent.left,Math.min(pt1.x,pt2.x));
        _screenExtent.right = Math.max(_screenExtent.right,Math.max(pt1.x,pt2.x));
        _screenExtent.top = Math.min(_screenExtent.top,Math.min(pt1.y,pt2.y));
        _screenExtent.bottom = Math.max(_screenExtent.bottom,Math.max(pt1.y,pt2.y));
     }else{
       if (Clip(pt1,pt2)){
          g2d.drawLine((int)pt1.x,(int)pt1.y,(int)pt2.x,(int)pt2.y);
       }
    }
  }
  _currPoint = point;               
}
public void moveTo(Vect3d point)
{
  _currPoint = point;
}
public void setClip(int left, int right, int top, int bottom)
{
  _rectClip.left=left;
  _rectClip.right=right;
  _rectClip.top=top;
  _rectClip.bottom=bottom;
  _center.x = (left+right)/2;
  _center.y = (bottom+top)/2;
  _size.x = (right-left);
  _size.y = (top-bottom);
}
public void updateSize()
{

   java.awt.Dimension drawAreaSize = _drawArea.getSize();
   int size=Math.min(drawAreaSize.width,drawAreaSize.height); 
   int xOffset = (drawAreaSize.width-size)/2;
   int yOffset = (drawAreaSize.height-size)/2;
   setClip(xOffset,xOffset+size,yOffset,yOffset+size);            
}
}
