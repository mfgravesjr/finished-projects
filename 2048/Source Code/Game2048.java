/*Author: Mark Fredrick Graves, Jr.
	Last Updated: 
	About: 
	
	Table of Contents
	i.)   Imports
	ii.)  Class
		1.) Constants
		2.) Static Variables
		3.) Variables
		4.) Annonymous Inner Class Variables
		5.) Constructors
		6.) Methods
		7.) Main method
		8.) Inner Classes
	iii.) Private Classes
	*/
	
//---------------------------------------------------------------------------------------------------------------------//
//i.IMPORTS
//---------------------------------------------------------------------------------------------------------------------//
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import javax.sound.sampled.*;
//---------------------------------------------------------------------------------------------------------------------//
//ii.CLASS
//---------------------------------------------------------------------------------------------------------------------//
public class Game2048
{
   //---------------------------------------------------------------------------------------------------------------------//
   //1.CONSTANTS//
   //---------------------------------------------------------------------------------------------------------------------//
   public static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
   public static final Point CTR_OF_SCREEN = new Point(SCREEN_DIM.width/2,SCREEN_DIM.height/2);
   //---------------------------------------------------------------------------------------------------------------------//
   //2.STATIC VARIABLES//
   //---------------------------------------------------------------------------------------------------------------------//
    
   //---------------------------------------------------------------------------------------------------------------------//
   //3.VARIABLES//
   //---------------------------------------------------------------------------------------------------------------------//
   private JFrame fr = new JFrame("2048");
   private JPanel[][] pnlArr = new JPanel[4][4];
   private JPanel mainPnl = new JPanel(new GridLayout(4,4));
   private JPanel bottomPnl = new JPanel();
   private JButton reset = new JButton("Reset");
   private GridBagConstraints gbc = new GridBagConstraints();
   private boolean win = false;
   private Vector saveArr = new Vector<Tile>();
   private JButton undo = new JButton("Undo");
   private JTextField score = new JTextField("0",9);
   private Vector scoreRevert = new Vector<String>();
   private JLabel scoreLbl = new JLabel("Score:");
   private JDialog shortcutWin = new JDialog(fr,"Shortcut Key Configuration");
   private JWindow enterKey = new JWindow(shortcutWin);
   private boolean enteringKey = false;
   private boolean shortcutKeysEnabled = false;
   private JLabel keyWindowLbl = new JLabel("Please type any one key for input");
   private JWindow shortcutRcvd = new JWindow(fr);
   private JLabel shortcutRcvdLbl = new JLabel();
   
   //jmenu
   private JMenuBar jbar = new JMenuBar();
   private JMenu save = new JMenu("Save");
   private JMenu load = new JMenu("Load");
   private JMenuItem save_1 = new JMenuItem();
   private JMenuItem load_1 = new JMenuItem();
   private JMenuItem save_2 = new JMenuItem();
   private JMenuItem load_2 = new JMenuItem();
   private JMenuItem save_3 = new JMenuItem();
   private JMenuItem load_3 = new JMenuItem();
   private JMenuItem save_4 = new JMenuItem();
   private JMenuItem load_4 = new JMenuItem();
   private JMenuItem save_5 = new JMenuItem();
   private JMenuItem load_5 = new JMenuItem();
   private JMenu settings = new JMenu("Settings");
   private JCheckBoxMenuItem endless = new JCheckBoxMenuItem("Endless Mode",false);
   private JMenuItem record = new JMenuItem("Record New Game");
   private JMenu playRec = new JMenu("Play Recorded Game");
   private JMenuItem speedx1 = new JMenuItem("Speed x1");
   private JMenuItem speedx2 = new JMenuItem("Speed x2");
   private JMenuItem speedx4 = new JMenuItem("Speed x4");
   private JMenuItem speedx8 = new JMenuItem("Speed x8");
   private JMenuItem speedx16 = new JMenuItem("Speed x16");
   private JMenuItem speedx32 = new JMenuItem("Speed x32");
   private JSeparator sep = new JSeparator();
   private JMenuItem shortcuts = new JMenuItem("Configure Shortcut Keys");
   private JLabel[] lbl = {new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER),new JLabel("",SwingConstants.CENTER)};
   private JButton btn1 = new JButton("Save Slot 1");
   private JButton btn2 = new JButton("Load Slot 1");
   private JButton btn3 = new JButton("Save Slot 2");
   private JButton btn4 = new JButton("Load Slot 2");
   private JButton btn5 = new JButton("Save Slot 3");
   private JButton btn6 = new JButton("Load Slot 3");
   private JButton btn7 = new JButton("Save Slot 4");
   private JButton btn8 = new JButton("Load Slot 4");
   private JButton btn9 = new JButton("Save Slot 5");
   private JButton btn10 = new JButton("Load Slot 5");
   private JButton btn11 = new JButton("Undo");
   private JButton btn12 = new JButton("Reset");
   private JButton btn13 = new JButton("Record/Stop Recording");
   private JButton btn14 = new JButton("Pause/Unpause Recording");
   private JButton btn15 = new JButton("Playback/Speedup/Stop Playback");
   private JCheckBox btn16 = new JCheckBox("Enable Shortcut Keys",shortcutKeysEnabled);
   private JLabel hsLbl = new JLabel("High Score: ");
   private JLabel highscore = new JLabel("0");
   private int highestTile = 4;
   
   //input-output variables
   private FileInputStream fileread;
   private FileOutputStream filewrite;
   private ObjectInputStream in;
   private ObjectOutputStream out;
   
   //objects to be written out and read in
   private String hsObj;
   private Save sv1Obj;
   private Save sv2Obj;
   private Save sv3Obj;
   private Save sv4Obj;
   private Save sv5Obj;
   private Recording recObj;
   private int[] shortcutCfgObj = {49,112,50,113,51,114,52,115,53,116,8,27,82,32,46};
   private boolean recordOn = false;
   private long pauseTimeElapsed = 0;
   private long pauseStartTime = 0;
   
   //---------------------------------------------------------------------------------------------------------------------//
   //4.ANNONYMOUS INNER CLASS VARIABLES
   //---------------------------------------------------------------------------------------------------------------------//
   
   private WindowAdapter wa = 
      new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            writeCfg();
            fr.dispose();
            System.exit(0);
         }
      };
   
   private ActionListener al = 
      new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            if(e.getSource()==reset&&reset.getText().equals("Reset"))//reset
            {
               reset();
            }
            else if(e.getSource()==reset&&reset.getText().equals("Stop"))
            {
               recObj.stop();
               reset.setText("Reset");
               JOptionPane.showMessageDialog(null,"Playback Complete");
               for(Component c:jbar.getComponents())
                  if(!(c instanceof JLabel))c.setEnabled(true);
               fr.requestFocus();
            }
            else if(e.getSource()==reset&&reset.getText().equals("Pause"))
            {
               pauseStartTime = System.currentTimeMillis();
               reset.setText("Unpause");
            }
            else if(e.getSource()==reset&&reset.getText().equals("Unpause"))
            {
               pauseTimeElapsed = System.currentTimeMillis()-pauseStartTime;
               reset.setText("Pause");
               fr.requestFocus();
            }
            if(e.getSource()==undo&&settings.isEnabled()&&!reset.getText().equals("Unpause"))
            {
               undo();
               fr.requestFocus();
            }
            if(e.getSource()==record)
            {  
               if(!recordOn)
               {
                  int input = JOptionPane.YES_OPTION;
                  if(recObj!=null)input=JOptionPane.showConfirmDialog(null,"There is already a recording saved. Would you like to erase and record a new game?","Erase Recording?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                  else input=JOptionPane.showConfirmDialog(null,"You will lose your current progress. Record a new game?","Start a New Game?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                  if(input==JOptionPane.YES_OPTION)
                  {
                     recordOn=true;
                     playRec.setEnabled(false);
                     record.setText("Stop Recording");
                     recObj=new Recording();
                     reset();
                     reset.setText("Pause");
                  }
               }
               else
               {
                  recordOn=false;
                  record.setText("Record New Game");
                  playRec.setEnabled(true);
                  reset.setText("Reset");
               }
            }
            if(e.getSource()==speedx1)
            {
               if(JOptionPane.showConfirmDialog(null,"You will lose your current progress. Continue?","Continue?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
               {
                  reset.setText("Stop");
                  for(Component c:jbar.getComponents())c.setEnabled(false);
                  recObj.playRecording(fr,pnlArr,score,1,jbar,reset);
               }
            }
            if(e.getSource()==speedx2)
            {
               if(JOptionPane.showConfirmDialog(null,"You will lose your current progress. Continue?","Continue?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
               {
                  reset.setText("Stop");
                  for(Component c:jbar.getComponents())c.setEnabled(false);
                  recObj.playRecording(fr,pnlArr,score,2,jbar,reset);
               }
            }
            if(e.getSource()==speedx4)
            {
               if(JOptionPane.showConfirmDialog(null,"You will lose your current progress. Continue?","Continue?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
               {
                  reset.setText("Stop");
                  for(Component c:jbar.getComponents())c.setEnabled(false);
                  recObj.playRecording(fr,pnlArr,score,4,jbar,reset);
               }
            }
            if(e.getSource()==speedx8)
            {
               if(JOptionPane.showConfirmDialog(null,"You will lose your current progress. Continue?","Continue?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
               {
                  reset.setText("Stop");
                  for(Component c:jbar.getComponents())c.setEnabled(false);
                  recObj.playRecording(fr,pnlArr,score,8,jbar,reset);
               }
            }
            if(e.getSource()==speedx16)
            {
               if(JOptionPane.showConfirmDialog(null,"You will lose your current progress. Continue?","Continue?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
               {
                  reset.setText("Stop");
                  for(Component c:jbar.getComponents())c.setEnabled(false);
                  recObj.playRecording(fr,pnlArr,score,16,jbar,reset);
               }
            }
            if(e.getSource()==speedx32)
            {
               if(JOptionPane.showConfirmDialog(null,"You will lose your current progress. Continue?","Continue?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
               {
                  reset.setText("Stop");
                  for(Component c:jbar.getComponents())c.setEnabled(false);
                  recObj.playRecording(fr,pnlArr,score,32,jbar,reset);
               }
            }
            if(e.getSource()==save_1)
            {
               Tile[][] tiles = new Tile[4][4];
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                     if(pnlArr[i][j].getComponentCount()>0)tiles[i][j]=(Tile)pnlArr[i][j].getComponents()[0];
               sv1Obj = new Save(score.getText(),tiles);
               save_1.setText("Save 1:  "+sv1Obj.timestamp);
               load_1.setText("Save 1:  "+sv1Obj.timestamp);
               load_1.setEnabled(true);
            }
            if(e.getSource()==save_2)
            {
               Tile[][] tiles = new Tile[4][4];
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                     if(pnlArr[i][j].getComponentCount()>0)tiles[i][j]=(Tile)pnlArr[i][j].getComponents()[0];
               sv2Obj = new Save(score.getText(),tiles);
               save_2.setText("Save 2:  "+sv2Obj.timestamp);
               load_2.setText("Save 2:  "+sv2Obj.timestamp);
               load_2.setEnabled(true);
            }
            if(e.getSource()==save_3)
            {
               Tile[][] tiles = new Tile[4][4];
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                     if(pnlArr[i][j].getComponentCount()>0)tiles[i][j]=(Tile)pnlArr[i][j].getComponents()[0];
               sv3Obj = new Save(score.getText(),tiles);
               save_3.setText("Save 3:  "+sv3Obj.timestamp);
               load_3.setText("Save 3:  "+sv3Obj.timestamp);
               load_3.setEnabled(true);
            }
            if(e.getSource()==save_4)
            {
               Tile[][] tiles = new Tile[4][4];
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                     if(pnlArr[i][j].getComponentCount()>0)tiles[i][j]=(Tile)pnlArr[i][j].getComponents()[0];
               sv4Obj = new Save(score.getText(),tiles);
               save_4.setText("Save 4:  "+sv4Obj.timestamp);
               load_4.setText("Save 4:  "+sv4Obj.timestamp);
               load_4.setEnabled(true);
            }
            if(e.getSource()==save_5)
            {
               Tile[][] tiles = new Tile[4][4];
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                     if(pnlArr[i][j].getComponentCount()>0)tiles[i][j]=(Tile)pnlArr[i][j].getComponents()[0];
               sv5Obj = new Save(score.getText(),tiles);
               save_5.setText("Save 5:  "+sv5Obj.timestamp);
               load_5.setText("Save 5:  "+sv5Obj.timestamp);
               load_5.setEnabled(true);
            }
            if(e.getSource()==load_1)
            {
               highestTile = 4;
               saveArr.clear();
               scoreRevert.clear();
               undo.setEnabled(true);
               save.setEnabled(true);
               Tile[][] tiles = (Tile[][])sv1Obj.tilegrid;
               score.setText((String)sv1Obj.score);
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                  {
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     if(tiles[i][j]!=null)
                     {
                        pnlArr[i][j].add(new Tile(tiles[i][j].finalTileNo,pnlArr[i][j]));
                        if(tiles[i][j].finalTileNo>highestTile)highestTile=tiles[i][j].finalTileNo;
                        if(tiles[i][j].finalTileNo>2047)win=true;
                     }
                  }
               save();
            }
            if(e.getSource()==load_2)
            {
               highestTile = 4;
               saveArr.clear();
               scoreRevert.clear();
               undo.setEnabled(true);
               save.setEnabled(true);
               Tile[][] tiles = (Tile[][])sv2Obj.tilegrid;
               score.setText((String)sv2Obj.score);
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                  {
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     if(tiles[i][j]!=null)
                     {
                        pnlArr[i][j].add(new Tile(tiles[i][j].finalTileNo,pnlArr[i][j]));
                        if(tiles[i][j].finalTileNo>highestTile)highestTile=tiles[i][j].finalTileNo;
                        if(tiles[i][j].finalTileNo>2047)win=true;
                     }
                  }
               save();
            }
            if(e.getSource()==load_3)
            {
               highestTile = 4;
               saveArr.clear();
               scoreRevert.clear();
               undo.setEnabled(true);
               save.setEnabled(true);
               Tile[][] tiles = (Tile[][])sv3Obj.tilegrid;
               score.setText((String)sv3Obj.score);
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                  {
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     if(tiles[i][j]!=null)
                     {
                        pnlArr[i][j].add(new Tile(tiles[i][j].finalTileNo,pnlArr[i][j]));
                        if(tiles[i][j].finalTileNo>highestTile)highestTile=tiles[i][j].finalTileNo;
                        if(tiles[i][j].finalTileNo>2047)win=true;
                     }
                  }
               save();
            }
            if(e.getSource()==load_4)
            {
               highestTile = 4;
               saveArr.clear();
               scoreRevert.clear();
               undo.setEnabled(true);
               save.setEnabled(true);
               Tile[][] tiles = (Tile[][])sv4Obj.tilegrid;
               score.setText((String)sv4Obj.score);
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                  {
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     if(tiles[i][j]!=null)
                     {
                        pnlArr[i][j].add(new Tile(tiles[i][j].finalTileNo,pnlArr[i][j]));
                        if(tiles[i][j].finalTileNo>highestTile)highestTile=tiles[i][j].finalTileNo;
                        if(tiles[i][j].finalTileNo>2047)win=true;
                     }
                  }
               save();
            }
            if(e.getSource()==load_5)
            {
               highestTile = 4;
               saveArr.clear();
               scoreRevert.clear();
               undo.setEnabled(true);
               save.setEnabled(true);
               Tile[][] tiles = (Tile[][])sv5Obj.tilegrid;
               score.setText((String)sv5Obj.score);
               for(int i=0;i<4;i++)
                  for(int j=0;j<4;j++)
                  {
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     if(tiles[i][j]!=null)
                     {
                        pnlArr[i][j].add(new Tile(tiles[i][j].finalTileNo,pnlArr[i][j]));
                        if(tiles[i][j].finalTileNo>highestTile)highestTile=tiles[i][j].finalTileNo;
                        if(tiles[i][j].finalTileNo>2047)win=true;
                     }
                  }
               save();
            }
            if(e.getSource()==shortcuts)
            {
               shortcutWin.setVisible(true);
            }
            if(e.getSource()==btn1)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[0]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn2)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[1]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn3)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[2]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn4)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[3]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn5)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[4]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn6)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[5]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn7)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[6]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn8)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[7]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn9)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[8]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn10)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[9]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn11)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[10]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn12)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[11]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn13)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[12]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn14)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[13]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn15)
            {
               enterKey.setVisible(true);
               enterKey.requestFocus();
               shortcutCfgObj[14]=-1;
               enteringKey = true;
               shortcutWin.setEnabled(false);
            }
            if(e.getSource()==btn16)
            {
               if(btn16.isSelected())shortcutKeysEnabled = true;
               else shortcutKeysEnabled = false;
            }
         }
      };
   private KeyAdapter ka = 
      new KeyAdapter()
      {
         public void keyPressed(KeyEvent e)
         {
            if(enteringKey&&e.getSource()==enterKey)
            {
               boolean same =false;
               for(int i:shortcutCfgObj)
                  if(e.getKeyCode()==i||(e.getKeyCode()>36&&e.getKeyCode()<41))same=true;
               if(same){JOptionPane.showMessageDialog(null,"You must choose a key that is not reserved for another function.");enterKey.requestFocus();}
               else
               {
                  for(int i=0;i<15;i++)
                     if(shortcutCfgObj[i]==-1){shortcutCfgObj[i]=e.getKeyCode();lbl[i].setText(e.getKeyText(e.getKeyCode()));}
                  enteringKey=false;
                  enterKey.setVisible(false);
                  shortcutWin.setEnabled(true);
               }
            }
            else
            {
               if(e.getKeyCode()==37&&settings.isEnabled()&&!reset.getText().equals("Unpause"))left();
               if(e.getKeyCode()==39&&settings.isEnabled()&&!reset.getText().equals("Unpause"))right();
               if(e.getKeyCode()==38&&settings.isEnabled()&&!reset.getText().equals("Unpause"))up();
               if(e.getKeyCode()==40&&settings.isEnabled()&&!reset.getText().equals("Unpause"))down();
               if(e.getKeyCode()==shortcutCfgObj[0]&&save.isEnabled()&&shortcutKeysEnabled)//save1
               {
                  save_1.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Saved to slot 1");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[2]&&save.isEnabled()&&shortcutKeysEnabled)//save2
               {
                  save_2.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Saved to slot 2");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[4]&&save.isEnabled()&&shortcutKeysEnabled)//save3
               {
                  save_3.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Saved to slot 3");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[6]&&save.isEnabled()&&shortcutKeysEnabled)//save4
               {
                  save_4.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Saved to slot 4");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[8]&&save.isEnabled()&&shortcutKeysEnabled)//save5
               {
                  save_5.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Saved to slot 5");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[1]&&load.isEnabled()&&load_1.isEnabled()&&shortcutKeysEnabled)//load1
               {
                  load_1.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Loaded slot 1");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[3]&&load.isEnabled()&&load_2.isEnabled()&&shortcutKeysEnabled)//load2
               {
                  load_2.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Loaded slot 2");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[5]&&load.isEnabled()&&load_3.isEnabled()&&shortcutKeysEnabled)//load3
               {
                  load_3.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Loaded slot 3");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[7]&&load.isEnabled()&&load_4.isEnabled()&&shortcutKeysEnabled)//load4
               {
                  load_4.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Loaded slot 4");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[9]&&load.isEnabled()&&load_5.isEnabled()&&shortcutKeysEnabled)//load5
               {
                  load_5.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Loaded slot 5");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[10]&&settings.isEnabled()&&undo.isEnabled()&&!reset.getText().equals("Unpause")&&shortcutKeysEnabled)//undo
               {
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Undo");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
                  undo();
                  fr.requestFocus();
               }
               else if(e.getKeyCode()==shortcutCfgObj[11]&&reset.getText().equals("Reset")&&shortcutKeysEnabled)//reset
               {
                  reset();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Reset");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[12]&&settings.isEnabled()&&record.getText().equals("Record New Game")&&record.isEnabled()&&shortcutKeysEnabled)//record
               {
                  record.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Record New Game");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[12]&&record.getText().equals("Stop Recording")&&record.isEnabled()&&shortcutKeysEnabled)//stop recording
               {
                  record.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Stop Recording");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[13]&&reset.getText().equals("Pause")&&shortcutKeysEnabled)//pause
               {
                  reset.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Pause Recording");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[13]&&reset.getText().equals("Unpause")&&shortcutKeysEnabled)//unpause
               {
                  reset.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Unpause Recording");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[14]&&playRec.isEnabled()&&settings.isEnabled()&&shortcutKeysEnabled)//play recording
               {
                  speedx1.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Playback Recording x1");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[14]&&reset.getText().equals("Stop")&&recObj.playbackspeed==1&&shortcutKeysEnabled)//increase speed
               {
                  recObj.playbackspeed=2;
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Playback Recording x2");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[14]&&reset.getText().equals("Stop")&&recObj.playbackspeed==2&&shortcutKeysEnabled)//increase speed
               {
                  recObj.playbackspeed=8;
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Playback Recording x8");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
               else if(e.getKeyCode()==shortcutCfgObj[14]&&reset.getText().equals("Stop")&&recObj.playbackspeed==8&&shortcutKeysEnabled)//stop playback
               {
                  reset.doClick();
                  shortcutRcvd.setVisible(false);
                  shortcutRcvdLbl.setText("Stop Playback");
                     new SwingWorker<String,Void>()
                     {
                        protected String doInBackground() throws Exception
                        {
                           shortcutRcvd.setOpacity(1f);
                           shortcutRcvd.pack();
                           shortcutRcvd.setSize(shortcutRcvdLbl.getWidth(),shortcutRcvdLbl.getHeight());
                           shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
                           shortcutRcvd.setVisible(true);
                           try
                           {
                              for(int i=0;i<1500;i++)
                              {
                                 Thread.sleep(1);
                                 if(!shortcutRcvd.isVisible())
                                    break;
                              }
                              if(shortcutRcvd.isVisible())
                                 for(float xparent=1;xparent>=0;xparent-=.005)
                                 {
                                    shortcutRcvd.setOpacity(xparent);
                                    Thread.sleep(1);
                                    if(!shortcutRcvd.isVisible())
                                       break;
                                 }
                           }
                           catch(InterruptedException ie){ie.printStackTrace();}
                           shortcutRcvd.setVisible(false);
                           return null;
                        }
                     }.execute();
               }
            }
         }
      };
   private ComponentAdapter ca = 
      new ComponentAdapter()
      {
         public void componentMoved(ComponentEvent e)
         {
            shortcutRcvd.setLocation(fr.getLocation().x+fr.getSize().width-shortcutRcvdLbl.getSize().width-fr.getInsets().right,fr.getLocation().y+fr.getSize().height-shortcutRcvdLbl.getSize().height-fr.getInsets().bottom-bottomPnl.getHeight()-1);
         }
      };
   //---------------------------------------------------------------------------------------------------------------------//
   //5.CONSTRUCTORS//
   //---------------------------------------------------------------------------------------------------------------------//
   public Game2048()
   {
      //reading from config file
      try
      {
         fileread = new FileInputStream("game.cfg");
         in = new ObjectInputStream(fileread);
         hsObj = (String)in.readObject();
         sv1Obj = (Save)in.readObject();
         sv2Obj = (Save)in.readObject();
         sv3Obj = (Save)in.readObject();
         sv4Obj = (Save)in.readObject();
         sv5Obj = (Save)in.readObject();
         recObj = (Recording)in.readObject();
         shortcutCfgObj = (int[])in.readObject();
         shortcutKeysEnabled = ((Boolean)in.readObject()).booleanValue();
         in.close();
      }
      catch(FileNotFoundException fnfe){fnfe.printStackTrace();}
      catch(IOException ioe){ioe.printStackTrace();}
      catch(ClassNotFoundException cnfe){cnfe.printStackTrace();}
      
      //set text of save and load slots
      if(hsObj!=null)highscore.setText(hsObj);
      else hsObj = "0";
      if(sv1Obj==null){save_1.setText("Save 1:  empty");load_1.setText("Save 1:  empty");load_1.setEnabled(false);}
      else {save_1.setText("Save 1:  "+sv1Obj.timestamp);load_1.setText("Save 1:  "+sv1Obj.timestamp);}
      if(sv2Obj==null){save_2.setText("Save 2:  empty");load_2.setText("Save 2:  empty");load_2.setEnabled(false);}
      else {save_2.setText("Save 2:  "+sv2Obj.timestamp);load_2.setText("Save 2:  "+sv2Obj.timestamp);}
      if(sv3Obj==null){save_3.setText("Save 3:  empty");load_3.setText("Save 3:  empty");load_3.setEnabled(false);}
      else {save_3.setText("Save 3:  "+sv3Obj.timestamp);load_3.setText("Save 3:  "+sv3Obj.timestamp);}
      if(sv4Obj==null){save_4.setText("Save 4:  empty");load_4.setText("Save 4:  empty");load_4.setEnabled(false);}
      else {save_4.setText("Save 4:  "+sv4Obj.timestamp);load_4.setText("Save 4:  "+sv4Obj.timestamp);}
      if(sv5Obj==null){save_5.setText("Save 5:  empty");load_5.setText("Save 5:  empty");load_5.setEnabled(false);}
      else {save_5.setText("Save 5:  "+sv5Obj.timestamp);load_5.setText("Save 5:  "+sv5Obj.timestamp);}
      
      //setup text labels in shortcut key configuration
      for(int i=0;i<15;i++)lbl[i].setText(KeyEvent.getKeyText(shortcutCfgObj[i]));
      if(shortcutKeysEnabled)btn16.setSelected(true);
      
      //setting up frame
      fr.setDefaultCloseOperation(fr.DO_NOTHING_ON_CLOSE);
      fr.setLayout(new BorderLayout());
      fr.setBackground(Color.BLACK);
      fr.setResizable(false);
      
      //setting up enter key window
      enterKey.setLayout(new GridBagLayout());
      enterKey.setSize(225,125);
      enterKey.setLocation(CTR_OF_SCREEN.x-(enterKey.getSize().width/2),CTR_OF_SCREEN.y-(enterKey.getSize().height/2));
      enterKey.add(keyWindowLbl,gbc);
      keyWindowLbl.setForeground(Color.LIGHT_GRAY);
      enterKey.setFocusable(true);
      enterKey.getContentPane().setBackground(Color.DARK_GRAY);
      
      shortcutRcvd.getContentPane().setBackground(Color.DARK_GRAY);
      shortcutRcvdLbl.setForeground(Color.LIGHT_GRAY);
      
      
      //setting up shortcut configuration window
      for(JLabel _lbl: lbl)_lbl.setVerticalAlignment(SwingConstants.CENTER);
      shortcutWin.setLayout(new GridLayout(15,4));
      shortcutWin.setSize(900,400);
      shortcutWin.setLocation(CTR_OF_SCREEN.x-(shortcutWin.getSize().width/2),CTR_OF_SCREEN.y-(shortcutWin.getSize().height/2));
      shortcutWin.add(btn1);
      shortcutWin.add(lbl[0]);
      shortcutWin.add(btn2);
      shortcutWin.add(lbl[1]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn3);
      shortcutWin.add(lbl[2]);
      shortcutWin.add(btn4);
      shortcutWin.add(lbl[3]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn5);
      shortcutWin.add(lbl[4]);
      shortcutWin.add(btn6);
      shortcutWin.add(lbl[5]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn7);
      shortcutWin.add(lbl[6]);
      shortcutWin.add(btn8);
      shortcutWin.add(lbl[7]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn9);
      shortcutWin.add(lbl[8]);
      shortcutWin.add(btn10);
      shortcutWin.add(lbl[9]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn11);
      shortcutWin.add(lbl[10]);
      shortcutWin.add(btn12);
      shortcutWin.add(lbl[11]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn13);
      shortcutWin.add(lbl[12]);
      shortcutWin.add(btn14);
      shortcutWin.add(lbl[13]);
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(new JLabel());
      shortcutWin.add(btn15);
      shortcutWin.add(lbl[14]);
      shortcutWin.add(btn16);
      shortcutRcvd.add(shortcutRcvdLbl);
      
      //initiating jpanel array
      for(int i=0;i<4;i++)
      {
         for(int j=0;j<4;j++)
         {
            pnlArr[i][j] = new JPanel(new GridBagLayout());
            pnlArr[i][j].setBorder((BorderFactory.createLineBorder(Color.LIGHT_GRAY,1,true)));
            mainPnl.add(pnlArr[i][j]);
         }
      }
      
      //setting up score
      score.setFont(new Font("Arial",Font.BOLD,20));
      score.setFocusable(false);
      score.setHorizontalAlignment(JTextField.CENTER);
      
      //adding components to bottom panel
      bottomPnl.add(scoreLbl);
      bottomPnl.add(score);
      bottomPnl.add(undo);
      bottomPnl.add(reset);
      
      //creating jmenubar
      fr.setJMenuBar(jbar);
      jbar.add(save);
      save.setMnemonic('s');
      jbar.add(load);
      load.setMnemonic('l');
      jbar.add(settings);
      settings.setMnemonic('g');
      jbar.add(new JLabel("               "));
      jbar.add(hsLbl);
      jbar.add(highscore);
      hsLbl.setEnabled(false);
      highscore.setEnabled(false);
      save.add(save_1);
      save.add(save_2);
      save.add(save_3);
      save.add(save_4);
      save.add(save_5);
      load.add(load_1);
      load.add(load_2);
      load.add(load_3);
      load.add(load_4);
      load.add(load_5);
      settings.add(endless);
      endless.setMnemonic('e');
      settings.add(record);
      record.setMnemonic('r');
      settings.add(playRec);
      playRec.setMnemonic('p');
      settings.add(sep);
      settings.add(shortcuts);
      shortcuts.setMnemonic('c');
      playRec.add(speedx1);
      playRec.add(speedx2);
      playRec.add(speedx4);
      playRec.add(speedx8);
      playRec.add(speedx16);
      playRec.add(speedx32);
      if(recObj==null)playRec.setEnabled(false);
      
      //adding listeners
      fr.addWindowListener(wa);
      fr.addComponentListener(ca);
      reset.addActionListener(al);
      undo.addActionListener(al);
      fr.setFocusable(true);
      fr.setAutoRequestFocus(true);
      fr.addKeyListener(ka);
      enterKey.addKeyListener(ka);
      record.addActionListener(al);
      speedx1.addActionListener(al);
      speedx2.addActionListener(al);
      speedx4.addActionListener(al);
      speedx8.addActionListener(al);
      speedx16.addActionListener(al);
      speedx32.addActionListener(al);
      save_1.addActionListener(al);
      save_2.addActionListener(al);
      save_3.addActionListener(al);
      save_4.addActionListener(al);
      save_5.addActionListener(al);
      load_1.addActionListener(al);
      load_2.addActionListener(al);
      load_3.addActionListener(al);
      load_4.addActionListener(al);
      load_5.addActionListener(al);
      shortcuts.addActionListener(al);
      btn1.addActionListener(al);
      btn2.addActionListener(al);
      btn3.addActionListener(al);
      btn4.addActionListener(al);
      btn5.addActionListener(al);
      btn6.addActionListener(al);
      btn7.addActionListener(al);
      btn8.addActionListener(al);
      btn9.addActionListener(al);
      btn10.addActionListener(al);
      btn11.addActionListener(al);
      btn12.addActionListener(al);
      btn13.addActionListener(al);
      btn14.addActionListener(al);
      btn15.addActionListener(al);
      btn16.addActionListener(al);
      
      //adding pnls
      fr.add(mainPnl,BorderLayout.CENTER);
      fr.add(bottomPnl,BorderLayout.SOUTH);
      
      //set size and making frame visible
      fr.setVisible(true);
      fr.setSize(SCREEN_DIM.height/2,SCREEN_DIM.height/2+(int)bottomPnl.getHeight()+(int)jbar.getHeight());
      fr.setLocation(CTR_OF_SCREEN.x-(fr.getSize().width/2),CTR_OF_SCREEN.y-(fr.getSize().height/2));
      
      //create first 2 tiles
      randTile();
      randTile();
      save();
   }
   //---------------------------------------------------------------------------------------------------------------------//
   //6.METHODS//
   //---------------------------------------------------------------------------------------------------------------------//
   public void reset()
   {
      for(int i = 0;i<4;i++)
      {
         for(int j = 0;j<4;j++)
         {
            pnlArr[i][j].removeAll();
            pnlArr[i][j].setBackground(new Color(238,238,238));
         }
      }
      score.setText("0");
      win=false;
      randTile();
      randTile();
      fr.requestFocus();
      undo.setEnabled(true);
      save.setEnabled(true);
      saveArr.clear();
      scoreRevert.clear();
      highestTile = 4;
      save();
   }
   
   public void writeCfg()
   {
      hsObj=highscore.getText();
      try
      {
         filewrite = new FileOutputStream("game.cfg");
         out = new ObjectOutputStream(filewrite);
         out.writeObject(hsObj);
         out.writeObject(sv1Obj);
         out.writeObject(sv2Obj);
         out.writeObject(sv3Obj);
         out.writeObject(sv4Obj);
         out.writeObject(sv5Obj);
         out.writeObject(recObj);
         out.writeObject(shortcutCfgObj);
         out.writeObject(new Boolean(shortcutKeysEnabled));
         out.close();
      }
      catch(FileNotFoundException fnfe){fnfe.printStackTrace();}
      catch(IOException ioe){ioe.printStackTrace();}
   }
      
   public void changeScore(int scoreBonus)
   {  
      StringBuilder builder = new StringBuilder();
      for(String s: score.getText().split(","))builder.append(s);
      scoreBonus+=Integer.parseInt(builder.toString());
      if(scoreBonus>Integer.parseInt(highscore.getText()))highscore.setText(Integer.toString(scoreBonus));
      String rep = Integer.toString(scoreBonus);
      String to = "";
      for(int i=rep.length()-1;i>=0;i--)
      {
         if(scoreBonus>1000&&to.length()==3)to+=",";
         if(scoreBonus>1000000&&to.length()==7)to+=",";
         to+=rep.charAt(i);
      }
      rep = "";
      for(int i=to.length()-1;i>=0;i--)rep+=to.charAt(i);
      score.setText(rep);
   }
   
   public void save()
   {
      String str = score.getText();
      scoreRevert.add(str);
      Tile[][] temp = new Tile[4][4];
      for(int i=0;i<4;i++)
      {
         for(int j=0;j<4;j++)
            if(pnlArr[i][j].getComponents().length>0)
            {
               JPanel p = pnlArr[i][j];
               Component c = p.getComponents()[0];
               Tile t = (Tile)c;
               temp[i][j]=t;
            }
      }
      if(recordOn)recObj.record(System.currentTimeMillis()-pauseTimeElapsed,temp,str);
      
      saveArr.add(temp);
      if(saveArr.size()>6&&saveArr.size()>1)//max undos available + 1
      {
         saveArr.remove(0);
         scoreRevert.remove(0);
      }
   }
      
   public void undo()
   {
      if(saveArr.size()>1)highestTile=4;
      try
      {
         for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
               saveArr.lastElement();
               Tile t = ((Tile[][])saveArr.get(saveArr.size()-2))[i][j];
               pnlArr[i][j].removeAll();
               pnlArr[i][j].setBackground(new Color(238,238,238));
               try{pnlArr[i][j].add(new Tile(t.finalTileNo,pnlArr[i][j]));
               if(t.finalTileNo>highestTile)highestTile=t.finalTileNo;}
               catch(NullPointerException npe){}
            }
         score.setText((String)scoreRevert.get(saveArr.size()-2));
         if(recordOn)recObj.record(System.currentTimeMillis()-pauseTimeElapsed,(Tile[][])saveArr.get(saveArr.size()-2),score.getText());
         saveArr.removeElementAt(saveArr.size()-1);
         scoreRevert.removeElementAt(saveArr.size());
         win=false;
         for(int i=0;i<4;i++)for(int j=0;j<4;j++)if(pnlArr[i][j].getComponents().length>0)if(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo>2047)win=true;
      }
      catch(NoSuchElementException|ArrayIndexOutOfBoundsException e){JOptionPane.showMessageDialog(null,"There are no more undos available.");}
      fr.repaint();
   }
   
   public void slide()
   {
      try
         {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("shift.wav")));
            clip.start();
         }catch(LineUnavailableException|UnsupportedAudioFileException|IOException lue){lue.printStackTrace();}
   }
   
   public void lvlUp()
   {
      try
      {
         Clip clip = AudioSystem.getClip();
         clip.open(AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource("up.wav")));
         clip.start();
      }catch(LineUnavailableException|UnsupportedAudioFileException|IOException lue){lue.printStackTrace();}
   }
   
   public void left()
   {
      boolean pushed = false;
      boolean combined = false;
      boolean contCombining = false;
      for(int repeat=0;repeat<3;repeat++)//shifting all tiles over - no combining
         for(int i=0;i<4;i++)
         {
            for(int j=1;j<4;j++)
            {
               if(pnlArr[i][j].getComponentCount()==1)
               {
                  for(int _j=j-1;_j>=0;_j--)
                  {
                     if(pnlArr[i][_j].getComponentCount()==0)
                     {
                        pnlArr[i][_j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo,pnlArr[i][_j]));
                        pnlArr[i][j].removeAll();
                        pnlArr[i][j].setBackground(new Color(238,238,238));
                        pushed=true;
                        break;
                     }
                  }
               }
            }
         }
      for(int i=0;i<4;i++)//combining tiles now
      {
         for(int j=0;j<4;j++)
         {
            for(int _j=j-1;_j>=0;_j--)
            {
               if(pnlArr[i][j].getComponentCount()==1&&pnlArr[i][_j].getComponentCount()==1)
                  if(!combined&&((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo)
                  {
                     if(j==1)contCombining=true;
                     pnlArr[i][_j].removeAll();
                     pnlArr[i][_j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo*2,pnlArr[i][_j]));
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     
                     for(int shifting = _j+1;shifting<4;shifting++)
                     {
                        if(pnlArr[i][shifting].getComponentCount()==1&&pnlArr[i][shifting-1].getComponentCount()==0)
                        {
                           pnlArr[i][shifting-1].add(new Tile(((Tile)pnlArr[i][shifting].getComponents()[0]).finalTileNo,pnlArr[i][shifting-1]));
                           pnlArr[i][shifting].removeAll();
                           pnlArr[i][shifting].setBackground(new Color(238,238,238));
                        }
                     }
                     if(((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo;}
                     if(((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                     {
                        JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                        win=true;
                     }
                     changeScore(((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo);
                     pushed=true;
                     combined=true;
                     
                     break;
                  }
                  else 
                     break;
            }
         }
         if(contCombining)
         {
            if(pnlArr[i][2].getComponentCount()==1&&pnlArr[i][1].getComponentCount()==1)
               if(((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo)
               {
                  
                  pnlArr[i][1].removeAll();
                  pnlArr[i][1].add(new Tile(((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo*2,pnlArr[i][1]));
                  pnlArr[i][2].removeAll();
                  pnlArr[i][2].setBackground(new Color(238,238,238));
                     
                  for(int shifting = 3;shifting<4;shifting++)
                  {
                     if(pnlArr[i][shifting].getComponentCount()==1&&pnlArr[i][shifting-1].getComponentCount()==0)
                     {
                        pnlArr[i][shifting-1].add(new Tile(((Tile)pnlArr[i][shifting].getComponents()[0]).finalTileNo,pnlArr[i][shifting-1]));
                        pnlArr[i][shifting].removeAll();
                        pnlArr[i][shifting].setBackground(new Color(238,238,238));
                     }
                  }
                  if(((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo;}
                  if(((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                  {
                     JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                     win=true;
                  }
                  changeScore(((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo);
               }
            contCombining=false;
         }
         combined=false;
      }
      fr.repaint();
      if(pushed){slide();randTile();save();}
   }
   
   public void right()
   {
      boolean pushed = false;
      boolean combined = false;
      boolean contCombining = false;
      for(int repeat=0;repeat<3;repeat++)
         for(int i=0;i<4;i++)//shifting all tiles over - no combining
         {
            for(int j=2;j>-1;j--)
            {
               if(pnlArr[i][j].getComponentCount()==1)
               {
                  for(int _j=j+1;_j<4;_j++)
                  {
                     if(pnlArr[i][_j].getComponentCount()==0)
                     {
                        pnlArr[i][_j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo,pnlArr[i][_j]));
                        pnlArr[i][j].removeAll();
                        pnlArr[i][j].setBackground(new Color(238,238,238));
                        pushed=true;
                        break;
                     }
                  }
               }
            }
         }
      for(int i=0;i<4;i++)//combining tiles now
      {
         for(int j=3;j>-1;j--)
         {
            for(int _j=j+1;_j<4;_j++)
            {
               if(pnlArr[i][j].getComponentCount()==1&&pnlArr[i][_j].getComponentCount()==1)
                  if(!combined&&((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo)
                  {
                     if(j==2)contCombining=true;
                     pnlArr[i][_j].removeAll();
                     pnlArr[i][_j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo*2,pnlArr[i][_j]));
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     
                     for(int shifting = _j-1;shifting>=0;shifting--)
                     {
                        if(pnlArr[i][shifting].getComponentCount()==1&&pnlArr[i][shifting+1].getComponentCount()==0)
                        {
                           pnlArr[i][shifting+1].add(new Tile(((Tile)pnlArr[i][shifting].getComponents()[0]).finalTileNo,pnlArr[i][shifting+1]));
                           pnlArr[i][shifting].removeAll();
                           pnlArr[i][shifting].setBackground(new Color(238,238,238));
                        }
                     }
                     if(((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo;}
                     if(((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                     {
                        JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                        win=true;
                     }
                     changeScore(((Tile)pnlArr[i][_j].getComponents()[0]).finalTileNo);
                     pushed=true;
                     combined=true;
                     
                     break;
                  }
                  else 
                     break;
            }
         }
         if(contCombining)
         {
            if(pnlArr[i][2].getComponentCount()==1&&pnlArr[i][1].getComponentCount()==1)
               if(((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo)
               {
                  pnlArr[i][2].removeAll();
                  pnlArr[i][2].add(new Tile(((Tile)pnlArr[i][1].getComponents()[0]).finalTileNo*2,pnlArr[i][2]));
                  pnlArr[i][1].removeAll();
                  pnlArr[i][1].setBackground(new Color(238,238,238));
                     
                  for(int shifting = 1;shifting>0;shifting--)
                  {
                     if(pnlArr[i][shifting].getComponentCount()==1&&pnlArr[i][shifting-1].getComponentCount()==0)
                     {
                        pnlArr[i][shifting+1].add(new Tile(((Tile)pnlArr[i][shifting].getComponents()[0]).finalTileNo,pnlArr[i][shifting+1]));
                        pnlArr[i][shifting].removeAll();
                        pnlArr[i][shifting].setBackground(new Color(238,238,238));
                     }
                  }
                  if(((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo;}
                  if(((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                  {
                     JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                     win=true;
                  }
                  changeScore(((Tile)pnlArr[i][2].getComponents()[0]).finalTileNo);
               }
            contCombining=false;
         }
         combined=false;
      }
      fr.repaint();
      if(pushed){slide();randTile();save();}
   }
   
   public void up()
   {
      boolean pushed = false;
      boolean combined = false;
      boolean contCombining = false;
      for(int repeat=0;repeat<3;repeat++)
         for(int j=0;j<4;j++)//shifting all tiles over - no combining
         {
            for(int i=1;i<4;i++)
            {
               if(pnlArr[i][j].getComponentCount()==1)
               {
                  for(int _i=i-1;_i>=0;_i--)
                  {
                     if(pnlArr[_i][j].getComponentCount()==0)
                     {
                        pnlArr[_i][j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo,pnlArr[_i][j]));
                        pnlArr[i][j].removeAll();
                        pnlArr[i][j].setBackground(new Color(238,238,238));
                        pushed=true;
                        break;
                     }
                  }
               }
            }
         }
      for(int j=0;j<4;j++)//combining tiles now
      {
         for(int i=0;i<4;i++)
         {
            for(int _i=i-1;_i>=0;_i--)
            {
               if(pnlArr[i][j].getComponentCount()==1&&pnlArr[_i][j].getComponentCount()==1)
                  if(!combined&&((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo)
                  {
                     if(i==1)contCombining=true;
                     pnlArr[_i][j].removeAll();
                     pnlArr[_i][j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo*2,pnlArr[_i][j]));
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     
                     for(int shifting = _i+1;shifting<4;shifting++)
                     {
                        if(pnlArr[shifting][j].getComponentCount()==1&&pnlArr[shifting-1][j].getComponentCount()==0)
                        {
                           pnlArr[shifting-1][j].add(new Tile(((Tile)pnlArr[shifting][j].getComponents()[0]).finalTileNo,pnlArr[shifting-1][j]));
                           pnlArr[shifting][j].removeAll();
                           pnlArr[shifting][j].setBackground(new Color(238,238,238));
                        }
                     }
                     if(((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo;}
                     if(((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                     {
                        JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                        win=true;
                     }
                     changeScore(((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo);
                     pushed=true;
                     combined=true;
                     
                     break;
                  }
                  else 
                     break;
            }
         }
         if(contCombining)
         {
            if(pnlArr[2][j].getComponentCount()==1&&pnlArr[1][j].getComponentCount()==1)
               if(((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo==((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo)
               {
                  pnlArr[1][j].removeAll();
                  pnlArr[1][j].add(new Tile(((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo*2,pnlArr[1][j]));
                  pnlArr[2][j].removeAll();
                  pnlArr[2][j].setBackground(new Color(238,238,238));  
                     
                  for(int shifting = 3;shifting<4;shifting++)
                  {
                     if(pnlArr[shifting][j].getComponentCount()==1&&pnlArr[shifting-1][j].getComponentCount()==0)
                     {
                        pnlArr[shifting-1][j].add(new Tile(((Tile)pnlArr[shifting][j].getComponents()[0]).finalTileNo,pnlArr[shifting-1][j]));
                        pnlArr[shifting][j].removeAll();
                        pnlArr[shifting][j].setBackground(new Color(238,238,238));
                     }
                  }
                  if(((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo;}
                  if(((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                  {
                     JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                     win=true;
                  }
                  changeScore(((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo);
               }
            contCombining=false;
         }
         combined=false;
      }
      fr.repaint();
      if(pushed){slide();randTile();save();}
   }
   
   public void down()
   {
      boolean pushed = false;
      boolean combined = false;
      boolean contCombining = false;
      for(int repeat=0;repeat<3;repeat++)
         for(int j=0;j<4;j++)//shifting all tiles over - no combining
         {
            for(int i=2;i>-1;i--)
            {
               if(pnlArr[i][j].getComponentCount()==1)
               {
                  for(int _i=i+1;_i<4;_i++)
                  {
                     if(pnlArr[_i][j].getComponentCount()==0)
                     {
                        pnlArr[_i][j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo,pnlArr[_i][j]));
                        pnlArr[i][j].removeAll();
                        pnlArr[i][j].setBackground(new Color(238,238,238));
                        pushed=true;
                        break;
                     }
                  }
               }
            }
         }
      for(int j=0;j<4;j++)//combining tiles now
      {
         for(int i=3;i>-1;i--)
         {
            for(int _i=i+1;_i<4;_i++)
            {
               if(pnlArr[i][j].getComponentCount()==1&&pnlArr[_i][j].getComponentCount()==1)
                  if(!combined&&((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo)
                  {
                     if(i==2)contCombining=true;
                     pnlArr[_i][j].removeAll();
                     pnlArr[_i][j].add(new Tile(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo*2,pnlArr[_i][j]));
                     pnlArr[i][j].removeAll();
                     pnlArr[i][j].setBackground(new Color(238,238,238));
                     
                     for(int shifting = _i-1;shifting>=0;shifting--)
                     {
                        if(pnlArr[shifting][j].getComponentCount()==1&&pnlArr[shifting+1][j].getComponentCount()==0)
                        {
                           pnlArr[shifting+1][j].add(new Tile(((Tile)pnlArr[shifting][j].getComponents()[0]).finalTileNo,pnlArr[shifting+1][j]));
                           pnlArr[shifting][j].removeAll();
                           pnlArr[shifting][j].setBackground(new Color(238,238,238));
                        }
                     }
                     if(((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo;}
                     if(((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                     {
                        JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                        win=true;
                     }
                     changeScore(((Tile)pnlArr[_i][j].getComponents()[0]).finalTileNo);
                     pushed=true;
                     combined=true;
                     
                     break;
                  }
                  else 
                     break;
            }
         }
         if(contCombining)
         {
            if(pnlArr[2][j].getComponentCount()==1&&pnlArr[1][j].getComponentCount()==1)
               if(((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo==((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo)
               {
                  pnlArr[2][j].removeAll();
                  pnlArr[2][j].add(new Tile(((Tile)pnlArr[1][j].getComponents()[0]).finalTileNo*2,pnlArr[2][j]));
                  pnlArr[1][j].removeAll();
                  pnlArr[1][j].setBackground(new Color(238,238,238));
                     
                  for(int shifting = 1;shifting>0;shifting--)
                  {
                     if(pnlArr[shifting][j].getComponentCount()==1&&pnlArr[shifting-1][j].getComponentCount()==0)
                     {
                        pnlArr[shifting+1][j].add(new Tile(((Tile)pnlArr[shifting][j].getComponents()[0]).finalTileNo,pnlArr[shifting+1][j]));
                        pnlArr[shifting][j].removeAll();
                        pnlArr[shifting][j].setBackground(new Color(238,238,238));
                     }
                  }
                  if(((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo>highestTile){lvlUp();highestTile=((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo;}
                  if(((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo==2048&&!win&&!endless.isSelected())
                  {
                     JOptionPane.showMessageDialog(null,"YOU WIN!!!\nKeep going, there's higher tiles to be achieved!");
                     win=true;
                  }
                  changeScore(((Tile)pnlArr[2][j].getComponents()[0]).finalTileNo);
               }
            contCombining=false;
         }
         combined=false;
      }
      fr.repaint();
      if(pushed){slide();randTile();save();}
   }
   
   public void randTile()
   {
      int x = 0;
      int y = 0;
      int tile = 0;
      boolean loop = true;
      boolean cont = false;
      
      //1 in 10 chance of being 4
      if(new Random().nextInt(10)==0)tile=4;
      else tile=2;
      
      //where is new tile
      while(loop)
      {
         x=new Random().nextInt(4);
         y=new Random().nextInt(4);
         if(pnlArr[x][y].getComponentCount()==0)loop=false;
      }
      
      //place new tile
      pnlArr[x][y].add(new Tile(tile,pnlArr[x][y]),gbc);
      
      //check for gameover
      for(int i=0;i<4;i++)
         for(int j=0;j<4;j++)
            if(pnlArr[i][j].getComponentCount()==0)cont=true;
      if(!cont)
         for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
            //check for vertical potential combinations
               if(i<3)
                  if(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo==((Tile)pnlArr[i+1][j].getComponents()[0]).finalTileNo)cont=true;
            
            //check for horizontal potential combinations
               if(j<3)
                  if(((Tile)pnlArr[i][j].getComponents()[0]).finalTileNo==((Tile)pnlArr[i][j+1].getComponents()[0]).finalTileNo)cont=true;
            }
      if(!cont)
      {
         JOptionPane.showMessageDialog(null,"Game Over");
         undo.setEnabled(false);
         save.setEnabled(false);
      }
   }
   //---------------------------------------------------------------------------------------------------------------------//
   //7.MAIN//
   //---------------------------------------------------------------------------------------------------------------------//
   public static void main(String[]args)
   {
      new Game2048();
   }
   //---------------------------------------------------------------------------------------------------------------------//
   //8.INNER CLASSES//
   //---------------------------------------------------------------------------------------------------------------------//
   
}
//---------------------------------------------------------------------------------------------------------------------//
//iii.PRIVATE CLASSES//
//---------------------------------------------------------------------------------------------------------------------//
class Tile extends JLabel
{
   final int finalTileNo;
     
   public Tile(int tileNo,JPanel pan)
   {
      super();
      finalTileNo = tileNo;
      EventQueue.invokeLater(
            new Runnable(){
               public void run(){setText(Integer.toString(finalTileNo));}});
      if(tileNo>65536)setFont(new Font("Arial",Font.BOLD,25));
      else setFont(new Font("Arial",Font.BOLD,30));
      setForeground(Color.BLACK);
      //switch (tileNo)
      //{
      if(tileNo<3)pan.setBackground(Color.WHITE);
      else
         if(tileNo<5)pan.setBackground(new Color(250,250,250));
         else
            if(tileNo<9)pan.setBackground(new Color(255,255,128));
            else
               if(tileNo<17)pan.setBackground(new Color(255,191,128));
               else
                  if(tileNo<33)pan.setBackground(new Color(255,64,64));
                  else
                     if(tileNo<65)pan.setBackground(new Color(191,64,128));
                     else
                        if(tileNo<129)pan.setBackground(new Color(128,64,191));
                        else
                           if(tileNo<257)pan.setBackground(new Color(64,64,128));
                           else
                              if(tileNo<513)pan.setBackground(new Color(0,96,96));
                              else
                                 if(tileNo<1025)pan.setBackground(new Color(32,160,32));
                                 else
                                    if(tileNo<2049)pan.setBackground(new Color(224,160,0));
                                    else
                                       if(tileNo<4097)pan.setBackground(new Color(149,106,0));
                                       else
                                          if(tileNo<8193)
                                          {
                                             pan.setBackground(new Color(75,53,0));
                                             setForeground(Color.WHITE);
                                          }
                                          else
                                          {
                                             pan.setBackground(new Color(0,0,0));
                                             setForeground(Color.WHITE);
                                          }
      //}
   }
}

class Save extends Object implements Serializable
{
   final String score;
   final String timestamp;
   final Tile[][] tilegrid;
   
   public Save(String s,Tile[][] g)
   {
      timestamp = new SimpleDateFormat("MM/dd/yy hh:mma").format(new Date());
      score = s;
      tilegrid = g;
   }
}

class Recording extends Object implements Serializable
{
   Vector timeArr = new Vector<Long>();
   Vector moveArr = new Vector<Tile[][]>();
   Vector scoreArr = new Vector<String>();
   boolean stop = false;
   int playbackspeed = -1;
   
   public Recording()
   {
      timeArr.add(new Long(System.currentTimeMillis()));
   }
   
   public void record(long timeInMillis,Tile[][] tileArr,String score)
   {
      moveArr.add(tileArr);
      timeArr.add(new Long(timeInMillis));
      scoreArr.add(score);
   }
   
   public void stop()
   {
      stop = true;
   }
   
   public void playRecording(JFrame fr,JPanel[][] pnlArr,JTextField txt,int _playbackspeed,JMenuBar jbar,JButton reset)
   {
      playbackspeed = _playbackspeed;
      (
            new SwingWorker<String,Void>(){
               protected String doInBackground() throws Exception
               {
                  for(int t=0;t<moveArr.size();t++)
                  {
                     try{Thread.sleep((((Long)timeArr.get(t+1)).longValue()-((Long)timeArr.get(t)).longValue())/playbackspeed);}
                     catch(InterruptedException ie){ie.printStackTrace();}
                  
                     for(int i=0;i<4;i++)
                        for(int j=0;j<4;j++)
                        {
                           pnlArr[i][j].removeAll();
                           pnlArr[i][j].setBackground(new Color(238,238,238));
                           if(((Tile[][])moveArr.get(t))[i][j]!=null)pnlArr[i][j].add(new Tile(((Tile[][])moveArr.get(t))[i][j].finalTileNo,pnlArr[i][j]));
                           fr.repaint();
                        }
                     txt.setText((String)scoreArr.get(t));
                     if(stop){stop=false;
                        return null;}
                  }
                  try{Thread.sleep(1000);}
                  catch(InterruptedException ie){ie.printStackTrace();}
                  JOptionPane.showMessageDialog(null,"Playback Complete");
                  reset.setText("Reset");
                  for(Component c:jbar.getComponents())
                     if(!(c instanceof JLabel))c.setEnabled(true);
                  return null;
               }
            }).execute();
   }
}