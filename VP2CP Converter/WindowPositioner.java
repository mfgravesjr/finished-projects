import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.Point;
import java.awt.GraphicsEnvironment;

public class WindowPositioner
{
   private static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
   private static final Rectangle WINDOW_BOUNDS = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
   public static final int NORTHWEST = 0;
   public static final int NORTH = 1;
   public static final int NORTHEAST = 2;
   public static final int WEST = 3;
   public static final int CENTER = 4;
   public static final int EAST = 5;
   public static final int SOUTHWEST = 6;
   public static final int SOUTH = 7;
   public static final int SOUTHEAST = 8;
   
   public static void setLocation(Window window,int position,boolean accountForTaskbar)
   {
      window.setLocation(getLocation(window,position,accountForTaskbar));
   }
   
   public static Point getLocation(Window window,int position,boolean ignoreTaskbar)
   {
      if(ignoreTaskbar)
         switch(position)
         {
            case NORTHWEST: return new Point(0,0);
            case NORTH:  return new Point(SCREEN_DIM.width/2-window.getWidth()/2,0);
            case NORTHEAST: return new Point(SCREEN_DIM.width-window.getWidth(),0);
            case WEST: return new Point(0,SCREEN_DIM.height/2-window.getHeight()/2);
            case CENTER: return new Point(SCREEN_DIM.width/2-window.getWidth()/2,SCREEN_DIM.height/2-window.getHeight()/2);
            case EAST: return new Point(SCREEN_DIM.width-window.getWidth(),SCREEN_DIM.height/2-window.getHeight()/2);
            case SOUTHWEST: return new Point(0,SCREEN_DIM.height-window.getHeight());
            case SOUTH: return new Point(SCREEN_DIM.width/2-window.getWidth()/2,SCREEN_DIM.height-window.getHeight());
            case SOUTHEAST: return new Point(SCREEN_DIM.width-window.getWidth(),SCREEN_DIM.height-window.getHeight());
            default: throw new IllegalArgumentException("Position argument not recognized");
         }
      else
         switch(position)
         {
            case NORTHWEST: return new Point(0+WINDOW_BOUNDS.x,0+WINDOW_BOUNDS.y);
            case NORTH:  return new Point(WINDOW_BOUNDS.width/2-window.getWidth()/2+WINDOW_BOUNDS.x,0+WINDOW_BOUNDS.y);
            case NORTHEAST: return new Point(WINDOW_BOUNDS.width-window.getWidth()+WINDOW_BOUNDS.x,0+WINDOW_BOUNDS.y);
            case WEST: return new Point(0+WINDOW_BOUNDS.x,WINDOW_BOUNDS.height/2-window.getHeight()/2+WINDOW_BOUNDS.y);
            case CENTER: return new Point(WINDOW_BOUNDS.width/2-window.getWidth()/2+WINDOW_BOUNDS.x,WINDOW_BOUNDS.height/2-window.getHeight()/2+WINDOW_BOUNDS.y);
            case EAST: return new Point(WINDOW_BOUNDS.width-window.getWidth()+WINDOW_BOUNDS.x,WINDOW_BOUNDS.height/2-window.getHeight()/2+WINDOW_BOUNDS.y);
            case SOUTHWEST: return new Point(0+WINDOW_BOUNDS.x,WINDOW_BOUNDS.height-window.getHeight()+WINDOW_BOUNDS.y);
            case SOUTH: return new Point(WINDOW_BOUNDS.width/2-window.getWidth()/2+WINDOW_BOUNDS.x,WINDOW_BOUNDS.height-window.getHeight()+WINDOW_BOUNDS.y);
            case SOUTHEAST: return new Point(WINDOW_BOUNDS.width-window.getWidth()+WINDOW_BOUNDS.x,WINDOW_BOUNDS.height-window.getHeight()+WINDOW_BOUNDS.y);
            default: throw new IllegalArgumentException("Position argument not recognized");
         }
   }
}