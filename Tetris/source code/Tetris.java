import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.GridLayout;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.border.TitledBorder;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.Vector;
import java.lang.reflect.InvocationTargetException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Font;
import javax.swing.JWindow;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.MenuSelectionManager;
import javax.swing.JDialog;
import java.awt.event.KeyListener;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JCheckBoxMenuItem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.awt.CardLayout;

class Tetris implements ActionListener
{
   private static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
   private static JFrame frame = new JFrame("Tetris");
   private ImagePanel window = new ImagePanel();
   private static JImageLabel[][] tiles = new JImageLabel[20][10];
   private static JPanel nextBlock = new JPanel();
   private static JPanel holdBlock = new JPanel();
   private static JLabel[][] nextBlockTiles = new JLabel[4][4];
   private static JLabel[][] holdBlockTiles = new JLabel[4][4];
   private static ImageIcon[] tetriminos = new ImageIcon[70];
   private static ImageIcon[] miniTetriminos = new ImageIcon[70];
   private static BufferedImage shadow;
   private static BufferedImage[] backgrounds = new BufferedImage[10];
   private static ImageIcon[] gameoverLetters = new ImageIcon[10];
   private static ImageIcon[] pauseLetters = new ImageIcon[10];
   private static JMenuBar jbar = new JMenuBar();
   private static JMenu file = new JMenu("File");
   private static JMenu options = new JMenu("Options");
   private static JMenu newGame = new JMenu("New Game...");
   private static JMenuItem keyConfig = new JMenuItem("Key Config");
   private static JMenuItem exit = new JMenuItem("Exit");
   private static JCheckBoxMenuItem shadowsOptn = new JCheckBoxMenuItem("Use Shadows",true);
   private static JCheckBoxMenuItem bgOptn = new JCheckBoxMenuItem("Background Images",true);
   private static JCheckBoxMenuItem musicOff = new JCheckBoxMenuItem("Mute Music",false);
   private static JCheckBoxMenuItem mute = new JCheckBoxMenuItem("Mute SFX",false);
   private static JButton spinnerBtn = new JButton("Start at Level...");
   private static SpinnerNumberModel model = new SpinnerNumberModel(1,1,20,1);
   private static JSpinner spinner = new JSpinner(model);
   private static JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
   private static Random r = new Random();
   private static JLabel pointsLbl = new JLabel("Score: 0");
   private static JLabel levelLbl = new JLabel("Level: 1");
   private static JButton resetBtn = new JButton("Reset");
   private static Thread th;
   private static Thread th2;
   private static int level = 1;
   private static int next = 0;
   private static int current = 0;
   private static int hold = -1;
   private static GridBagConstraints configGbc = new GridBagConstraints();
   private static GridBagConstraints gbc = new GridBagConstraints();
   private static Vector<ArrayCoordinates2D> tetriminoLocation = new Vector<ArrayCoordinates2D>();
   private static volatile Vector<ArrayCoordinates2D> shadowLocation = new Vector<ArrayCoordinates2D>();
   private static int rotated;
   private static volatile boolean startTh2 = false;
   private static volatile boolean gameover = false;
   private static Clip music;
   private static Clip rotateSFX;
   private static Clip harddropSFX;
   private static Clip holdSFX;
   private static Clip errorSFX;
   private static Clip clearSFX;
   private static Clip smallClearSFX;
   private static Clip pauseSFX;
   private static Clip gameoverSFX;
   private static boolean holdPressed;
   private static volatile int points = 0;
   private static volatile int oldPoints = 0;
   private static boolean dropping = false;
   private static int linesCleared = 0;
   private static Insets ins;
   private static boolean lastChance = true;
   private static boolean pause = false;
   private static int[] tileHolder = new int[10];
   private static volatile boolean dropped = false;
   private static volatile boolean down = false;
   private static JDialog keyConfigMenu = new JDialog(frame);
   private static JDialog waitForInput = new JDialog(keyConfigMenu);
   
   private static JLabel downConfigLbl = new JLabel("Down");
   private static JLabel leftConfigLbl = new JLabel("Left");
   private static JLabel rightConfigLbl = new JLabel("Right");
   private static JLabel hardDropConfigLbl = new JLabel("Hard Drop");
   private static JLabel holdConfigLbl = new JLabel("Hold");
   private static JLabel rotateCWConfigLbl = new JLabel("Rotate Clockwise");
   private static JLabel rotateCCWConfigLbl = new JLabel("Rotate Counter-Clockwise");
   private static JLabel pauseConfigLbl = new JLabel("Pause");
   private static JLabel resetConfigLbl = new JLabel("Reset");
   
   private static int downConfigVal = KeyEvent.VK_DOWN;
   private static int leftConfigVal = KeyEvent.VK_LEFT;
   private static int rightConfigVal = KeyEvent.VK_RIGHT;
   private static int hardDropConfigVal = KeyEvent.VK_SPACE;
   private static int holdConfigVal = KeyEvent.VK_UP;
   private static int rotateCWConfigVal = KeyEvent.VK_SHIFT;
   private static int rotateCCWConfigVal = KeyEvent.VK_CONTROL;
   private static int pauseConfigVal = KeyEvent.VK_ESCAPE;
   private static int resetConfigVal = KeyEvent.VK_R;
   
   private static JButton downConfig;
   private static JButton leftConfig;
   private static JButton rightConfig;
   private static JButton hardDropConfig;
   private static JButton holdConfig;
   private static JButton rotateCWConfig;
   private static JButton rotateCCWConfig;
   private static JButton pauseConfig;
   private static JButton resetConfig;
   
   private static JButton okay = new JButton("Save");
   private static JTextField lbl = new JTextField("Waiting for keyboard input...");
   private static JButton play = new JButton("Play");
   private static JPanel playWindow = new JPanel(new GridBagLayout());
   private static JPanel cards = new JPanel(new CardLayout());
   private static FileWriter fw;
   private static BufferedWriter bw;
   private static FileReader fr;
   private static BufferedReader br;
    
   public Tetris()
   {
      try
      {
         for(int i = 0; i < 70; i++)
         {
            tetriminos[i] = new ImageIcon(ImageIO.read(getClass().getResource("media/"+(i+1)+".png")));
         }
         for(int i = 0; i < 70; i++)
         {
            miniTetriminos[i] = new ImageIcon(getScaledImage(tetriminos[i].getImage(),10,10));
         }
         int i = 0;
         for(char c: new String(" gameover ").toCharArray())
         {
            gameoverLetters[i] = new ImageIcon(ImageIO.read(getClass().getResource("media/"+(new Character(c).equals(' ')?'_':c)+".png")));
            i++;
         }
         i = 0;
         for(char c: new String("  paused  ").toCharArray())
         {
            pauseLetters[i] = new ImageIcon(ImageIO.read(getClass().getResource("media/"+(new Character(c).equals(' ')?'_':c)+".png")));
            i++;
         }
         for(int b = 1; b <= 10; b++)
         {
            backgrounds[b-1] = ImageIO.read(getClass().getResource("media/bg"+b+".png"));
         }
         shadow = ImageIO.read(getClass().getResource("media/shadow.png"));
      }
      catch(IOException e){e.printStackTrace();}
      try{music = AudioSystem.getClip();
         rotateSFX = AudioSystem.getClip();
         harddropSFX = AudioSystem.getClip();
         holdSFX = AudioSystem.getClip();
         errorSFX = AudioSystem.getClip();
         clearSFX = AudioSystem.getClip();
         smallClearSFX = AudioSystem.getClip();
         pauseSFX = AudioSystem.getClip();
         gameoverSFX = AudioSystem.getClip();
         music.open(AudioSystem.getAudioInputStream(getClass().getResource("media/song.wav")));
         rotateSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/block.wav")));
         harddropSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/harddrop.wav")));
         holdSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/hold.wav")));
         errorSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/error.wav")));
         clearSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/clear.wav")));
         smallClearSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/smallclear.wav")));
         pauseSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/pause.wav")));
         gameoverSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/gameover.wav")));}
      catch(LineUnavailableException|UnsupportedAudioFileException|IOException e){e.printStackTrace();}
      for(int i = 0; i < 4; i++)tetriminoLocation.add(new ArrayCoordinates2D());
      try{fr = new FileReader("tetris.cfg");}
      catch(IOException|NullPointerException e){}
      if(fr!=null)br = new BufferedReader(fr);
      StringBuilder str = new StringBuilder();
      String s;
      if(br!=null)try{while((s = br.readLine())!=null)str.append(s);
      br.close();}
      catch(IOException ex){ex.printStackTrace();}
      String[] strArr = str.toString().split("/");
      if(strArr.length==9)
      {
         downConfigVal = Integer.parseInt(strArr[0]);
         leftConfigVal = Integer.parseInt(strArr[1]);
         rightConfigVal = Integer.parseInt(strArr[2]);
         hardDropConfigVal = Integer.parseInt(strArr[3]);
         holdConfigVal = Integer.parseInt(strArr[4]);
         rotateCWConfigVal = Integer.parseInt(strArr[5]);
         rotateCCWConfigVal = Integer.parseInt(strArr[6]);
         pauseConfigVal = Integer.parseInt(strArr[7]);
         resetConfigVal = Integer.parseInt(strArr[8]);
      }
      downConfig = new JButton(KeyEvent.getKeyText(downConfigVal));
      leftConfig = new JButton(KeyEvent.getKeyText(leftConfigVal));
      rightConfig = new JButton(KeyEvent.getKeyText(rightConfigVal));
      hardDropConfig = new JButton(KeyEvent.getKeyText(hardDropConfigVal));
      holdConfig = new JButton(KeyEvent.getKeyText(holdConfigVal));
      rotateCWConfig = new JButton(KeyEvent.getKeyText(rotateCWConfigVal));
      rotateCCWConfig = new JButton(KeyEvent.getKeyText(rotateCCWConfigVal));
      pauseConfig = new JButton(KeyEvent.getKeyText(pauseConfigVal));
      resetConfig = new JButton(KeyEvent.getKeyText(resetConfigVal));
      
      keyConfigMenu.setLayout(new GridBagLayout());
      keyConfigMenu.setTitle("Key Config");
      keyConfigMenu.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
      lbl.setEditable(false);
      waitForInput.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
      waitForInput.setUndecorated(true);
      lbl.setBackground(Color.DARK_GRAY);
      lbl.setForeground(Color.WHITE);
      waitForInput.add(lbl);
      waitForInput.pack();
      configGbc.gridx=1;
      configGbc.gridy=1;
      configGbc.anchor=configGbc.LINE_START;
      configGbc.insets=new Insets(10,10,10,10);//(top,left,bottom,right);
      keyConfigMenu.add(downConfigLbl,configGbc);
      configGbc.gridx=2;
      configGbc.gridy=1;
      keyConfigMenu.add(downConfig,configGbc);
      configGbc.gridx=1;
      configGbc.gridy=2;
      keyConfigMenu.add(leftConfigLbl,configGbc);
      configGbc.gridx=2;
      configGbc.gridy=2;
      keyConfigMenu.add(leftConfig,configGbc);
      configGbc.gridx=1;
      configGbc.gridy=3;
      keyConfigMenu.add(rightConfigLbl,configGbc);
      configGbc.gridx=2;
      configGbc.gridy=3;
      keyConfigMenu.add(rightConfig,configGbc);
      configGbc.gridx=1;
      configGbc.gridy=4;
      keyConfigMenu.add(hardDropConfigLbl,configGbc);
      configGbc.gridx=2;
      configGbc.gridy=4;
      keyConfigMenu.add(hardDropConfig,configGbc);
      configGbc.gridx=1;
      configGbc.gridy=5;
      keyConfigMenu.add(holdConfigLbl,configGbc);
      configGbc.gridx=2;
      configGbc.gridy=5;
      keyConfigMenu.add(holdConfig,configGbc);
      configGbc.gridx=3;
      configGbc.gridy=1;
      keyConfigMenu.add(rotateCWConfigLbl,configGbc);
      configGbc.gridx=4;
      configGbc.gridy=1;
      keyConfigMenu.add(rotateCWConfig,configGbc);
      configGbc.gridx=3;
      configGbc.gridy=2;
      keyConfigMenu.add(rotateCCWConfigLbl,configGbc);
      configGbc.gridx=4;
      configGbc.gridy=2;
      keyConfigMenu.add(rotateCCWConfig,configGbc);
      configGbc.gridx=3;
      configGbc.gridy=3;
      keyConfigMenu.add(pauseConfigLbl,configGbc);
      configGbc.gridx=4;
      configGbc.gridy=3;
      keyConfigMenu.add(pauseConfig,configGbc);
      configGbc.gridx=3;
      configGbc.gridy=4;
      keyConfigMenu.add(resetConfigLbl,configGbc);
      configGbc.gridx=4;
      configGbc.gridy=4;
      keyConfigMenu.add(resetConfig,configGbc);
      configGbc.gridx=3;
      configGbc.gridy=5;
      configGbc.gridwidth=2;
      configGbc.anchor=configGbc.CENTER;
      keyConfigMenu.add(okay,configGbc);
      keyConfigMenu.pack();
      file.setMnemonic('f');
      options.setMnemonic('o');
      newGame.setMnemonic('n');
      keyConfig.setMnemonic('k');
      shadowsOptn.setMnemonic('h');
      mute.setMnemonic('s');
      musicOff.setMnemonic('m');
      bgOptn.setMnemonic('b');
      exit.setMnemonic('x');
      file.add(newGame);
      file.add(keyConfig);
      file.add(exit);
      options.add(shadowsOptn);
      options.add(bgOptn);
      options.add(musicOff);
      options.add(mute);
      jbar.add(file);
      jbar.add(options);
      frame.setJMenuBar(jbar);
      spinner.setEditor(editor);
      editor.getTextField().setEditable(false);
      JPanel spinnerPnl = new JPanel();
      spinnerPnl.add(spinnerBtn);
      spinnerPnl.add(spinner);
      newGame.add(spinnerPnl);
      
      resetBtn.setEnabled(false);
      playWindow.add(play);
      playWindow.setBackground(Color.BLACK);
      cards.add(playWindow,"1");
      cards.add(window,"2");
      
      nextBlock.setLayout(new GridLayout(4,4));
      nextBlock.setBorder(new TitledBorder(new EtchedBorder(frame.getBackground().darker(),frame.getBackground().darker().darker()),"Next", TitledBorder.CENTER ,TitledBorder.TOP));
      holdBlock.setLayout(new GridLayout(4,4));
      holdBlock.setBorder(new TitledBorder(new EtchedBorder(frame.getBackground().darker(),frame.getBackground().darker().darker()),"Hold", TitledBorder.CENTER ,TitledBorder.TOP));
      ins = nextBlock.getBorder().getBorderInsets(nextBlock);
      nextBlock.setPreferredSize(new Dimension(10*4+ins.left+ins.right,10*4+ins.top+ins.bottom));
      nextBlock.setMinimumSize(new Dimension(10*4+ins.left+ins.right,10*4+ins.top+ins.bottom));
      holdBlock.setPreferredSize(new Dimension(10*4+ins.left+ins.right,10*4+ins.top+ins.bottom));
      holdBlock.setMinimumSize(new Dimension(10*4+ins.left+ins.right,10*4+ins.top+ins.bottom));
      frame.setFocusable(true);
      window.setLayout(new GridLayout(20,10));
      window.setBackground(Color.BLACK);
      cards.setBorder(new EtchedBorder(frame.getBackground().darker(),frame.getBackground().darker().darker()));
      ins = cards.getBorder().getBorderInsets(window);
      window.setPreferredSize(new Dimension(20*10+ins.left+ins.right,20*20+ins.top+ins.bottom));
      window.setMinimumSize(new Dimension(20*10+ins.left+ins.right,20*20+ins.top+ins.bottom));
      for(int i = 0; i < 20; i++)
         for(int j = 0; j < 10; j++){tiles[i][j] = new JImageLabel();window.add(tiles[i][j]);}
      for(int i = 0; i < 4; i++)
         for(int j = 0; j < 4; j++){nextBlockTiles[i][j] = new JLabel();nextBlock.add(nextBlockTiles[i][j]);}
      for(int i = 0; i < 4; i++)
         for(int j = 0; j < 4; j++){holdBlockTiles[i][j] = new JLabel();holdBlock.add(holdBlockTiles[i][j]);}
      frame.setSize(500,550);
      frame.setLocation(SCREEN_DIM.width/2-frame.getWidth()/2,SCREEN_DIM.height/2-frame.getHeight()/2);
      frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
      frame.setLayout(new GridBagLayout());
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.anchor = gbc.PAGE_START;
      frame.add(nextBlock,gbc);
      gbc.gridy = 2;
      frame.add(holdBlock,gbc);
      gbc.gridy = 1;
      gbc.gridx = 2;
      gbc.gridheight = 2;
      gbc.gridwidth = 2;
      frame.add(cards,gbc);
      gbc.gridheight = 1;
      gbc.gridwidth = 1;
      gbc.gridy = 3;
      gbc.anchor = gbc.LINE_START;
      frame.add(pointsLbl,gbc);
      gbc.gridx = 3;
      gbc.anchor = gbc.LINE_END;
      frame.add(levelLbl,gbc);
      gbc.gridy = 4;
      gbc.gridx = 2;
      gbc.gridwidth = 2;
      gbc.anchor = gbc.CENTER;
      frame.add(resetBtn,gbc);      
      frame.setVisible(true);
      
      resetBtn.addActionListener(this);
      spinnerBtn.addActionListener(this);
      exit.addActionListener(this);
      keyConfig.addActionListener(this);
      downConfig.addActionListener(this);
      leftConfig.addActionListener(this);
      rightConfig.addActionListener(this);
      holdConfig.addActionListener(this);
      hardDropConfig.addActionListener(this);
      rotateCWConfig.addActionListener(this);
      rotateCCWConfig.addActionListener(this);
      pauseConfig.addActionListener(this);
      resetConfig.addActionListener(this);
      okay.addActionListener(this);
      musicOff.addActionListener(this);
      bgOptn.addActionListener(this);
      file.addActionListener(this);
      options.addActionListener(this);
      play.addActionListener(this);
      
      frame.addKeyListener(
            new KeyAdapter()
            {
               public void keyPressed(KeyEvent e)
               {
                  if(e.getKeyCode()==e.VK_R)reset(1);
                  if(!dropping&&!gameover&&th.isAlive())
                  {
                     if(!pause&&!dropped)
                     {
                        if(e.getKeyCode()==downConfigVal)SwingUtilities.invokeLater(new Runnable(){public void run(){down=true;}});
                        if(e.getKeyCode()==leftConfigVal)
                        SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){
                                    left();
                                    }});
                        if(e.getKeyCode()==rightConfigVal)
                        SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){
                                    right();
                                    }});
                        if(e.getKeyCode()==holdConfigVal)SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run()
                                       {
                                       if(!holdPressed)hold();
                                       else playErrorSFX();}});
                        if(e.getKeyCode()==hardDropConfigVal)
                        SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){hardDrop();}});
                        if(e.getKeyCode()==rotateCWConfigVal)SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){
                                    rotateCW();
                                    }});
                        if(e.getKeyCode()==rotateCCWConfigVal)SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){
                                    rotateCCW();}});
                        if(e.getKeyCode()==pauseConfigVal)SwingUtilities.invokeLater(
                                new Runnable(){
                                    public void run(){
                                    pause(true);}});
                     }
                     else
                     {
                        if(e.getKeyCode()==pauseConfigVal)SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){
                                    pause(false);}});
                     }
                  }
               }
               public void keyReleased(KeyEvent e)
               {
                  if(e.getKeyCode()==downConfigVal)down=false;
               }
            });
      th = 
         new Thread()
         {
            long timeStart;
            long timeEnd;
            
            public void run()
            {
               while(true)
                  if(!gameover)
                  {
                     while(startTh2){
                        try{sleep(1);}
                        catch(InterruptedException e){e.printStackTrace();}}
                     long subtractor = (long)(Math.log10(Math.max(level/2,1))*730);
                     try{sleep(Math.max(70,800-subtractor-((timeEnd-timeStart)/1000000)));}
                     catch(InterruptedException|IllegalArgumentException e){e.printStackTrace();}
                  
                     if(!dropping&&!pause)
                     {
                        SwingUtilities.invokeLater(
                              new Runnable()
                              {
                                 public void run()
                                 {
                                    timeStart = System.nanoTime();
                                    if(!startTh2)down();
                                    timeEnd = System.nanoTime();
                                 } 
                              });
                     }
                  
                     while(pause)
                     {
                        for(int i = 9; i >= 0; i--)
                        {
                           if(!pause)
                              break;
                           for(int j = 0; j < 10; j++)
                           {
                              int k = (j+i>9?j+i-10:i+j);
                              tiles[9][j].setIcon(pauseLetters[k]);
                           }
                           try{sleep(200);}
                           catch(InterruptedException e){e.printStackTrace();}
                        }
                     }
                  }
            }
         };
      th2 =
         new Thread()
         {
            public void run()
            {
               while(true)
                  if(!gameover)
                  {
                     if(down&&!startTh2){SwingUtilities.invokeLater(
                              new Runnable(){
                                 public void run(){
                                 down();}});
                     for(int i = 0; i < 50; i++){
                        if(!down)
                           break;
                        try{Thread.sleep(1);}
                        catch(InterruptedException ex){ex.printStackTrace();}}}
                     if(startTh2&&!gameover)
                     {
                        Vector<Integer> linesToBeCleared = new Vector<Integer>();
                        for(int i = 0; i < 20; i++)
                        {
                           boolean clearLine = true;
                           for(int j = 0; j < 10; j++)
                           {
                              if(tiles[i][j].getIcon()==null)
                              {
                                 clearLine = false;
                              }
                           }
                           if(clearLine)linesToBeCleared.add(new Integer(i));
                        }
                        if(linesToBeCleared.size()>0)
                        {
                           final Vector<Integer> linesToBeCleared2 = linesToBeCleared;
                           for(int c = 0; c < 10; c++)
                           {
                              final int C = c;
                              SwingUtilities.invokeLater(
                                    new Runnable(){
                                       public void run(){
                                          paintLines(linesToBeCleared2,C);
                                          try{sleep(10);}
                                          catch(InterruptedException e){e.printStackTrace();}
                                       }});
                           }
                           for(int c = 0; c < 10; c++)
                           {
                              final int C = c;
                              SwingUtilities.invokeLater(
                                    new Runnable(){
                                       public void run(){
                                          clearLines(linesToBeCleared2,C);
                                          try{sleep(10);}
                                          catch(InterruptedException e){e.printStackTrace();}
                                       }});
                           }
                           SwingUtilities.invokeLater(
                                 new Runnable(){
                                    public void run(){
                                       dropLines(linesToBeCleared2);
                                       linesToBeCleared.clear();
                                       //getNextBlock();
                                    }});
                           if(linesToBeCleared.size()==1){points+=40*level;playSmallClearSFX();}
                           if(linesToBeCleared.size()==2){points+=100*level;playSmallClearSFX();}
                           if(linesToBeCleared.size()==3){points+=300*level;playSmallClearSFX();}
                           if(linesToBeCleared.size()==4){points+=1200*level;playClearSFX();}
                           linesCleared += linesToBeCleared.size();
                           if(level!=linesCleared/10+1&&level<=20)
                           {
                              level = linesCleared/10+1;
                              levelLbl.setText("Level: "+level);
                              for(JLabel[] la: holdBlockTiles)
                                 for(JLabel l: la)
                                 {
                                    for(int i = 0; i <= 6; i++)
                                    {
                                       if(l.getIcon()==miniTetriminos[i+(7*((level-2)/2))])
                                       {
                                          l.setIcon(miniTetriminos[i+(7*((level-1)/2))]);
                                       }
                                    }
                                 }
                              for(JLabel[] la: tiles)
                                 for(JLabel l: la)
                                 {
                                    for(int i = 0; i <= 6; i++)
                                    {
                                       if(l.getIcon()==tetriminos[i+(7*((level-2)/2))])l.setIcon(tetriminos[i+(7*((level-1)/2))]);
                                    }
                                 }
                              if(bgOptn.isSelected())window.setImage(backgrounds[(level-1)/2]);
                           }
                           startTh2=false;
                           if(oldPoints!=points)
                           {
                              for(int y = window.getHeight()/2; y > window.getHeight()/4; y--)
                              {
                                 Graphics g = window.getGraphics();
                                 String s = "+"+(points-oldPoints);
                                 g.setColor(Color.WHITE);
                                 g.setFont(new Font("Arial",Font.BOLD,20));
                                 g.drawString(s,window.getWidth()/2-g.getFontMetrics().stringWidth(s)/2,y);
                                 g.dispose();
                                 try{sleep(10);}
                                 catch(InterruptedException e){e.printStackTrace();}
                                 window.paintImmediately(window.getWidth()/2-g.getFontMetrics().stringWidth(s)/2,window.getHeight()/4-g.getFontMetrics().getAscent(),g.getFontMetrics().stringWidth(s),window.getHeight()/4+g.getFontMetrics().getAscent());
                                 pointsLbl.setText("Score: "+points);
                              }
                              oldPoints=points;
                           }
                        }
                        else {startTh2=false;}//getNextBlock();}
                        frame.repaint();
                     }
                  }
            }
         };
         frame.requestFocus();
   }
   
   public void actionPerformed(ActionEvent e)
   {
      if(e.getSource()==play)
      {
         play();
      }
      if(e.getSource()==resetBtn)
      {
         reset(1);
      }
      if(e.getSource()==spinnerBtn)
      {
         reset(((Integer)spinner.getValue()).intValue());
      }
      if(e.getSource()==exit)
      {
         System.exit(0);
      }
      if(e.getSource()==keyConfig)
      {
         if(!pause&&!gameover&&th.isAlive())pause(true);
         keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
         keyConfigMenu.setVisible(true);
      }
      if(e.getSource()==downConfig)
      {
         downConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               downConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               downConfig.setText(ke.getKeyText(downConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==rightConfig)
      {
         rightConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               rightConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               rightConfig.setText(ke.getKeyText(rightConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==leftConfig)
      {
         leftConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               leftConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               leftConfig.setText(ke.getKeyText(leftConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==holdConfig)
      {
         holdConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               holdConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               holdConfig.setText(ke.getKeyText(holdConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==hardDropConfig)
      {
         hardDropConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               hardDropConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               hardDropConfig.setText(ke.getKeyText(hardDropConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==rotateCWConfig)
      {
         rotateCWConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               rotateCWConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               rotateCWConfig.setText(ke.getKeyText(rotateCWConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==rotateCCWConfig)
      {
         rotateCCWConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               rotateCCWConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               rotateCCWConfig.setText(ke.getKeyText(rotateCCWConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==pauseConfig)
      {
         pauseConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               pauseConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               pauseConfig.setText(ke.getKeyText(pauseConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==resetConfig)
      {
         resetConfig.setText(" ");
         waitForInput.setLocation(keyConfigMenu.getWidth()/2-waitForInput.getWidth()/2+keyConfigMenu.getX(),keyConfigMenu.getHeight()/2-waitForInput.getHeight()/2+keyConfigMenu.getY());
         lbl.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               resetConfigVal=ke.getKeyCode();
               lbl.removeKeyListener(this);
               resetConfig.setText(ke.getKeyText(resetConfigVal));
               keyConfigMenu.pack();
               keyConfigMenu.setLocation(frame.getWidth()/2-keyConfigMenu.getWidth()/2+frame.getX(),frame.getHeight()/2-keyConfigMenu.getHeight()/2+frame.getY());
               waitForInput.setVisible(false);
            }
         });
         waitForInput.setVisible(true);
      }
      if(e.getSource()==okay)
      {
         File f = new File("tetris.cfg");
         try{fw = new FileWriter(f);}
         catch(IOException ex){ex.printStackTrace();}
         bw = new BufferedWriter(fw);
         keyConfigMenu.setVisible(false);
         StringBuilder strB = new StringBuilder();
         strB.append(downConfigVal);
         strB.append("/");
         strB.append(leftConfigVal);
         strB.append("/");
         strB.append(rightConfigVal);
         strB.append("/");
         strB.append(hardDropConfigVal);
         strB.append("/");
         strB.append(holdConfigVal);
         strB.append("/");
         strB.append(rotateCWConfigVal);
         strB.append("/");
         strB.append(rotateCCWConfigVal);
         strB.append("/");
         strB.append(pauseConfigVal);
         strB.append("/");
         strB.append(resetConfigVal);
         try{bw.write(strB.toString());
         bw.flush();
         bw.close();}
         catch(IOException ex){ex.printStackTrace();}
      }
      if(e.getSource()==musicOff)if(musicOff.isSelected())music.stop();else if(th.isAlive()) music.loop(Clip.LOOP_CONTINUOUSLY);
      if(e.getSource()==bgOptn){if(bgOptn.isSelected())window.setImage(backgrounds[(level-1)/2]);else window.setImage(null);window.repaint();}
      MenuSelectionManager.defaultManager().clearSelectedPath();
   }
   
   private void play()
   {
      if(!th.isAlive())
      {
         ((CardLayout)cards.getLayout()).show(cards,"2");
         resetBtn.setEnabled(true);
         if(bgOptn.isSelected())window.setImage(backgrounds[0]);
         if(!musicOff.isSelected())music.loop(Clip.LOOP_CONTINUOUSLY);
         generateNextBlock();
         getNextBlock();
         th.start();
         th2.start();
      }
   }
   
   private void reset(int level)
   {
      frame.requestFocus();
         if(!th.isAlive())play();
         linesCleared = (level-1)*10;
         points = 0;
         this.level = level;
         pause = false;
         gameover = false;
         holdPressed = false;
         startTh2 = false;
         if(bgOptn.isSelected())window.setImage(backgrounds[(level-1)/2]);
         pointsLbl.setText("Score: 0");
         levelLbl.setText("Level: "+level);
         hold = -1;
         for(JLabel[] la: tiles)
            for(JLabel l: la)l.setIcon(null);
         for(JLabel[] la: nextBlockTiles)
            for(JLabel l: la)l.setIcon(null);
         for(JLabel[] la: holdBlockTiles)
            for(JLabel l: la)l.setIcon(null);
         generateNextBlock();
         getNextBlock();
         music.stop();
         try{
            music = AudioSystem.getClip();
            music.open(AudioSystem.getAudioInputStream(getClass().getResource("media/song.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
         if(!musicOff.isSelected())music.loop(Clip.LOOP_CONTINUOUSLY);
   }
   
   private void drawShadow()
   {
      if(!dropping)SwingUtilities.invokeLater(
               new Runnable(){
                  public void run()
                  {
                     Vector<ArrayCoordinates2D> toBe = new Vector<ArrayCoordinates2D>();
                     boolean isShadowSet = false;
                     boolean pass = true;
                     dropped = false;
                     for(JImageLabel[] la: tiles)for(JImageLabel l: la)l.setImage(null);
                     shadowLocation.clear();
                     for(int i = 0; i < 4; i++)
                     {
                        shadowLocation.add(new ArrayCoordinates2D(tetriminoLocation.get(i).row,tetriminoLocation.get(i).col));
                        toBe.add(new ArrayCoordinates2D(tetriminoLocation.get(i).row+1,tetriminoLocation.get(i).col));
                     }
                  
                     while(!isShadowSet)
                     {
                        for(int i = 0; i < 4; i++)
                        {
                           boolean insidePass = true;
                           for(int j = 0; j < 4; j++)
                              if(tetriminoLocation.get(j).compare(toBe.get(i).row,toBe.get(i).col))insidePass=false;
                           if(insidePass&&toBe.get(i).row>0&&toBe.get(i).row<20)
                           {
                              if(tiles[toBe.get(i).row][toBe.get(i).col].getIcon()!=null)pass=false;
                           }
                           if(toBe.get(i).row>=20)
                           {
                              pass=false;
                           }
                        }
                        if(pass)
                           for(int i = 0; i < 4; i++){shadowLocation.get(i).row++;toBe.get(i).row++;}
                        else isShadowSet=true;
                     }
                     try{
                        for(ArrayCoordinates2D a: shadowLocation)
                           if(tiles[a.row][a.col].getIcon()==null&&shadowsOptn.isSelected())tiles[a.row][a.col].setImage(shadow);}
                     catch(ArrayIndexOutOfBoundsException e){}
                     window.repaint();
                  }});
   }
   
   private void pause(boolean b)
   {
      if(b)
      {
         music.stop();
         playPauseSFX();
         for(int i = 0; i < 10; i++)
         {
            tileHolder[i]=-1;
            for(int ico = 0; ico < 70; ico++)
               if(tetriminos[ico]==(ImageIcon)tiles[9][i].getIcon())tileHolder[i] = ico;
            tiles[9][i].setIcon(pauseLetters[0]);
         }
      }
      else
      {
         music.loop(Clip.LOOP_CONTINUOUSLY);
         for(int i = 0; i < 10; i++)
            try{tiles[9][i].setIcon(tetriminos[tileHolder[i]]);}
            catch(ArrayIndexOutOfBoundsException e){tiles[9][i].setIcon(null);}
      }
      pause = b;
   }
   
   private void left()
   {
      boolean pass = true;
      for(ArrayCoordinates2D a: tetriminoLocation)
      {
         if(a.col-1<0){pass=false;
            break;}
         if(a.row>=0)
            try{
               if(tiles[a.row][a.col-1].getIcon()!=null)
               {
                  boolean insidePass = true;
                  for(ArrayCoordinates2D b: tetriminoLocation)
                  {
                     if(a.row==b.row&&a.col-1==b.col)insidePass=false;
                  }
                  if(insidePass)
                  {
                     pass=false;
                     break;
                  }
               }}
            catch(ArrayIndexOutOfBoundsException e){}
      }
      if(pass)
      {
         ImageIcon ico = tetriminos[current+(7*((level-1)/2))];
         for(ArrayCoordinates2D a: tetriminoLocation)
            try{tiles[a.row][a.col].setIcon(null);}
            catch(ArrayIndexOutOfBoundsException e){}
         for(ArrayCoordinates2D a: tetriminoLocation)
         {
            a.col--;
            try{tiles[a.row][a.col].setIcon(ico);}
            catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      drawShadow();
   }
   
   private void right()
   {
      boolean pass = true;
      for(ArrayCoordinates2D a: tetriminoLocation)
      {
         if(a.col+1>9){pass=false;
            break;}
         if(a.row>=0)
            try{
               if(tiles[a.row][a.col+1].getIcon()!=null)
               {
                  boolean insidePass = true;
                  for(ArrayCoordinates2D b: tetriminoLocation)
                  {
                     if(a.row==b.row&&a.col+1==b.col)insidePass=false;
                  }
                  if(insidePass)
                  {
                     pass=false;
                     break;
                  }
               }}
            catch(ArrayIndexOutOfBoundsException e){}
      }
      if(pass)
      {
         ImageIcon ico = tetriminos[current+(7*((level-1)/2))];
         for(ArrayCoordinates2D a: tetriminoLocation)
            try{tiles[a.row][a.col].setIcon(null);}
            catch(ArrayIndexOutOfBoundsException e){}
         for(ArrayCoordinates2D a: tetriminoLocation)
         {
            a.col++;
            try{tiles[a.row][a.col].setIcon(ico);}
            catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      drawShadow();
   
   }
   
   private boolean down()
   {
      boolean pass = true;
      for(ArrayCoordinates2D a: tetriminoLocation)
      {
         if(a.row==19){pass=false;
            break;}
         if(a.row+1>0)
            try{
               if(tiles[a.row+1][a.col].getIcon()!=null)
               {
                  boolean insidePass = true;
                  for(ArrayCoordinates2D b: tetriminoLocation)
                  {
                     if(a.row+1==b.row&&a.col==b.col)insidePass=false;
                  }
                  if(insidePass)
                  {
                     pass=false;
                     for(ArrayCoordinates2D a2: tetriminoLocation)
                        if(a2.row<0&&!dropped)
                        {
                           music.stop();
                           playGameoverSFX();
                           gameover=true;
                        }
                     if(gameover)
                     {
                        for(int i = 0; i < 10; i++)tiles[9][i].setIcon(gameoverLetters[i]);
                     }
                     else if(lastChance)
                     {
                        lastChance = false;
                        return true;
                     }
                  }
               }}
            catch(ArrayIndexOutOfBoundsException e){}
      }
      if(pass)
      {
         lastChance = true;
         ImageIcon ico = tetriminos[current+(7*((level-1)/2))];
         for(ArrayCoordinates2D a: tetriminoLocation)
            try{tiles[a.row][a.col].setIcon(null);}
            catch(ArrayIndexOutOfBoundsException e){}
         for(ArrayCoordinates2D a: tetriminoLocation)
         {
            a.row++;
            try{tiles[a.row][a.col].setIcon(ico);}
            catch(ArrayIndexOutOfBoundsException e){}
         }
         drawShadow();
         return true;
      }
      lastChance = true;
      holdPressed = false;
      dropping=false;
      if(!dropped)playHarddropSFX();
      dropped=true;
      try{Thread.sleep(250);}
      catch(InterruptedException e){}
      startTh2 = true;
      getNextBlock();
      drawShadow();
      return false;
   }
   
   private void paintLines(Vector<Integer> linesToBeCleared, int c)
   {
      for(Integer r: linesToBeCleared)
      {
         tiles[r][c].setIcon(tetriminos[42]);
         tiles[r][c].paintImmediately(0,0,tiles[r][c].getWidth(),tiles[r][c].getHeight());
      }
   }
   
   private void clearLines(Vector<Integer> linesToBeCleared, int c)
   {
      for(Integer r: linesToBeCleared)
      {
         tiles[r][c].setIcon(null);
         tiles[r][c].paintImmediately(0,0,tiles[r][c].getWidth(),tiles[r][c].getHeight());
      }
   }
   
   private void dropLines(Vector<Integer> linesToBeCleared)
   {
      for(int i: linesToBeCleared)
      {
         for(int r = i; r > 0; r--)
         {
            for(int c2 = 0; c2 < 10; c2++)
            {
               if(tiles[r-1][c2].getIcon()!=null
                  &&!tetriminoLocation.get(0).compare(r-1,c2)
                  &&!tetriminoLocation.get(1).compare(r-1,c2)
                  &&!tetriminoLocation.get(2).compare(r-1,c2)
                  &&!tetriminoLocation.get(3).compare(r-1,c2))tiles[r][c2].setIcon(tiles[r-1][c2].getIcon());
               tiles[r-1][c2].setIcon(null);
            }
         }
      }
//       setBlocks.clear();
//       for(int r = 0; r < 20; r++)for(int c = 0; c < 10; c++)if(tiles[r][c].getIcon()!=null)setBlocks.add(new ArrayCoordinates2D(r,c));
      startTh2=false;
   }
   
   private void hardDrop()
   {
      dropping=true;
      while(down());
   }
   
   private void playHarddropSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         harddropSFX.start();
         try{harddropSFX = AudioSystem.getClip();
            harddropSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/harddrop.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playRotateSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         rotateSFX.start();
         try{rotateSFX = AudioSystem.getClip();
            rotateSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/block.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playHoldSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         holdSFX.start();
         try{holdSFX = AudioSystem.getClip();
            holdSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/hold.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playErrorSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         errorSFX.start();
         try{errorSFX = AudioSystem.getClip();
            errorSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/error.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playClearSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         clearSFX.start();
         try{clearSFX = AudioSystem.getClip();
            clearSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/clear.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playSmallClearSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         smallClearSFX.start();
         try{smallClearSFX = AudioSystem.getClip();
            smallClearSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/smallclear.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playPauseSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         pauseSFX.start();
         try{pauseSFX = AudioSystem.getClip();
            pauseSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/pause.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   private void playGameoverSFX()
   {
      if(!gameover&&!mute.isSelected())
      {
         gameoverSFX.start();
         try{gameoverSFX = AudioSystem.getClip();
            gameoverSFX.open(AudioSystem.getAudioInputStream(getClass().getResource("media/gameover.wav")));}
         catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      }
   }
   
   
   private void rotateCW()
   {
      ImageIcon ico = tetriminos[current+(7*((level-1)/2))];
      if(current==0)
      {
         if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-2>=0)
                  if(tiles[tetriminoLocation.get(0).row-2][tetriminoLocation.get(0).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+2>=10)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
               ||tetriminoLocation.get(3).col-1<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=2;
                  tetriminoLocation.get(0).col+=2;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException ex){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+2>=0)
                  if(tiles[tetriminoLocation.get(0).row+2][tetriminoLocation.get(0).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+2>=20
               ||tetriminoLocation.get(0).col-2<=-1)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
               ||tetriminoLocation.get(1).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=2;
                  tetriminoLocation.get(0).col-=2;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==1)
      {
         if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tetriminoLocation.get(0).row-1>=0)
                     if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)
                        if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
               ||tetriminoLocation.get(2).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+2>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).col+=2;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row+2>=0)
                  if(tiles[tetriminoLocation.get(3).row+2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+2>=20)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).row+=2;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-1<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).col-=2;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
               ||tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row-2>=0)
                  if(tiles[tetriminoLocation.get(3).row-2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row-2<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).row-=2;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==2)playRotateSFX();
      
      if(current==3)
      {
         if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
                  ||tetriminoLocation.get(2).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row+2>=0)
                  if(tiles[tetriminoLocation.get(3).row+2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+2>=20)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).row+=2;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
                  ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-2<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).col-=2;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
                  ||tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row-2>=0)
                  if(tiles[tetriminoLocation.get(3).row-2][tetriminoLocation.get(3).col].getIcon()!=null)pass = false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).row-=2;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
                  ||tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+2>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).col+=2;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==4)
      {
         if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row>=0)
                  if(tiles[tetriminoLocation.get(0).row][tetriminoLocation.get(0).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+2>=10)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
                  ||tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
                  ||tetriminoLocation.get(3).col-1<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).col+=2;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+2>=0)
                  if(tiles[tetriminoLocation.get(0).row+2][tetriminoLocation.get(0).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+2>=20)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
                  ||tetriminoLocation.get(1).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-1<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=2;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row>=0)
                  if(tiles[tetriminoLocation.get(0).row][tetriminoLocation.get(0).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-2<=-1)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).col-=2;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-2>=0)
                  if(tiles[tetriminoLocation.get(0).row-2][tetriminoLocation.get(0).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
                  ||tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=2;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==5)
      {
         if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
                  ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
                  ||tetriminoLocation.get(3).col-1<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
                  ||tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
                  ||tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-1<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
                  ||tetriminoLocation.get(1).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row+1<=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
                  ||tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      if(current==6)
      {
         if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
                  ||tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+2>=0)
                  if(tiles[tetriminoLocation.get(3).row+2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+2>=20)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).row+=2;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
                  ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
                  ||tetriminoLocation.get(2).col-1<=-1)
                  if(tetriminoLocation.get(3).row>=0)
                     if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-2<=-1)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).col-=2;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
                  ||tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<=-1)pass=false;
               if(tetriminoLocation.get(3).row-2>=0)
                  if(tiles[tetriminoLocation.get(3).row-2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).row-=2;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<=-1)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+2>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).col+=2;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
   }
   
   private void rotateCCW()
   {
      ImageIcon ico = tetriminos[current+(7*((level-1)/2))];
      if(current==0)
      {
         if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+2>=0)
                  if(tiles[tetriminoLocation.get(0).row+2][tetriminoLocation.get(0).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+2>=20)
                  if(tetriminoLocation.get(0).col-2<0)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
               ||tetriminoLocation.get(1).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=2;
                  tetriminoLocation.get(0).col-=2;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException ex){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-2>=0)
                  if(tiles[tetriminoLocation.get(0).row-2][tetriminoLocation.get(0).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+2>=10)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
               ||tetriminoLocation.get(3).col-1<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=2;
                  tetriminoLocation.get(0).col+=2;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==1)
      {
         if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
                     if(tetriminoLocation.get(0).row+1>=20
                     ||tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-2<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).col-=2;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>20
               ||tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row-2>=0)
                  if(tiles[tetriminoLocation.get(3).row-2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).row-=2;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
               ||tetriminoLocation.get(2).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).col+=2;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row+2>=0)
                  if(tiles[tetriminoLocation.get(3).row+2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+2>=20)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).row+=2;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==2)playRotateSFX();
      
      if(current==3)
      {
         if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row-2>=0)
                  if(tiles[tetriminoLocation.get(3).row-2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).row-=2;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>20
               ||tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+2>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).col+=2;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row+2>=0)
                  if(tiles[tetriminoLocation.get(3).row+2][tetriminoLocation.get(3).col].getIcon()!=null)pass = false;
               if(tetriminoLocation.get(3).row+2>=20)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).row+=2;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>20
               ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-2<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).col-=2;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==4)
      {
         if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row>=0)
                  if(tiles[tetriminoLocation.get(0).row][tetriminoLocation.get(0).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-2<0)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).col-=2;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-2>=0)
                  if(tiles[tetriminoLocation.get(0).row-2][tetriminoLocation.get(0).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
               ||tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=2;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row>=0)
                  if(tiles[tetriminoLocation.get(0).row][tetriminoLocation.get(0).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+2>=10)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
               ||tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
               ||tetriminoLocation.get(3).col-1<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).col+=2;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==0) 
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+2>=0)
                  if(tiles[tetriminoLocation.get(0).row+2][tetriminoLocation.get(0).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+2>=20)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
               ||tetriminoLocation.get(1).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-1<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=2;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      
      if(current==5) 
      {
         if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
               ||tetriminoLocation.get(1).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row-1>=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2) 
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
               ||tetriminoLocation.get(3).col+1>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col-=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col+=1;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3) 
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(1).row-1>=0)
                  if(tiles[tetriminoLocation.get(1).row-1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+1>=0)
                  if(tiles[tetriminoLocation.get(3).row+1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+1>=20
               ||tetriminoLocation.get(3).col-1<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(1).row-=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row+=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(1).row+1>=0)
                  if(tiles[tetriminoLocation.get(1).row+1][tetriminoLocation.get(1).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(1).row+1>=20
               ||tetriminoLocation.get(1).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row-1<=0)
                  if(tiles[tetriminoLocation.get(3).row-1][tetriminoLocation.get(3).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-1<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(1).row+=1;
                  tetriminoLocation.get(1).col+=1;
                  tetriminoLocation.get(3).row-=1;
                  tetriminoLocation.get(3).col-=1;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
      if(current==6) 
      {
         if(rotated==1)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row-2>=0)
                  if(tiles[tetriminoLocation.get(3).row-2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).row-=2;
                  rotated=0;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==2) 
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col-1<0)pass=false;
               if(tetriminoLocation.get(2).row-1>=0)
                  if(tiles[tetriminoLocation.get(2).row-1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).col+1>=10)
                  if(tetriminoLocation.get(3).row>=0)
                     if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col+2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col+2>=10)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col-=1;
                  tetriminoLocation.get(2).row-=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).col+=2;
                  rotated=1;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==3) 
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row-1>=0)
                  if(tiles[tetriminoLocation.get(0).row-1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
               ||tetriminoLocation.get(2).col+1>=10)pass=false;
               if(tetriminoLocation.get(3).row+2>=0)
                  if(tiles[tetriminoLocation.get(3).row+2][tetriminoLocation.get(3).col].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).row+2>=20)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row-=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col+=1;
                  tetriminoLocation.get(3).row+=2;
                  rotated=2;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
         else if(rotated==0)
         {
            boolean pass = true;
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(null);}
               catch(ArrayIndexOutOfBoundsException e){}
            try{
               if(tetriminoLocation.get(0).row+1>=0)
                  if(tiles[tetriminoLocation.get(0).row+1][tetriminoLocation.get(0).col+1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(0).row+1>=20
               ||tetriminoLocation.get(0).col+1>=10)pass=false;
               if(tetriminoLocation.get(2).row+1>=0)
                  if(tiles[tetriminoLocation.get(2).row+1][tetriminoLocation.get(2).col-1].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(2).row+1>=20
               ||tetriminoLocation.get(2).col-1<0)pass=false;
               if(tetriminoLocation.get(3).row>=0)
                  if(tiles[tetriminoLocation.get(3).row][tetriminoLocation.get(3).col-2].getIcon()!=null)pass=false;
               if(tetriminoLocation.get(3).col-2<0)pass=false;
               if(pass)
               {
                  playRotateSFX();
                  tetriminoLocation.get(0).row+=1;
                  tetriminoLocation.get(0).col+=1;
                  tetriminoLocation.get(2).row+=1;
                  tetriminoLocation.get(2).col-=1;
                  tetriminoLocation.get(3).col-=2;
                  rotated=3;
                  drawShadow();
               }}
            catch(ArrayIndexOutOfBoundsException e){}
            for(ArrayCoordinates2D a:tetriminoLocation)
               try{tiles[a.row][a.col].setIcon(ico);}
               catch(ArrayIndexOutOfBoundsException e){}
         }
      }
   }
   
   private void hold()
   {
      playHoldSFX();
      boolean isHoldNull = (hold==-1);
      holdPressed = true;
      int _hold = current;
      for(JLabel[] la: holdBlockTiles)
         for(JLabel l: la)l.setIcon(null);
      for(ArrayCoordinates2D a: tetriminoLocation)
         try{tiles[a.row][a.col].setIcon(null);}
         catch(ArrayIndexOutOfBoundsException e){}
      ImageIcon ico = miniTetriminos[_hold+(7*((level-1)/2))];
      switch(_hold)
      {
         case 0:  holdBlockTiles[2][0].setIcon(ico);
            holdBlockTiles[2][1].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            holdBlockTiles[2][3].setIcon(ico);
            break;
         case 1:  holdBlockTiles[2][1].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            holdBlockTiles[2][3].setIcon(ico);
            holdBlockTiles[1][1].setIcon(ico);
            break;
         case 2:  holdBlockTiles[1][1].setIcon(ico);
            holdBlockTiles[1][2].setIcon(ico);
            holdBlockTiles[2][1].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            break;
         case 3:  holdBlockTiles[2][1].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            holdBlockTiles[2][3].setIcon(ico);
            holdBlockTiles[1][3].setIcon(ico);
            break;
         case 4:  holdBlockTiles[1][1].setIcon(ico);
            holdBlockTiles[1][2].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            holdBlockTiles[2][3].setIcon(ico);
            break;
         case 5:  holdBlockTiles[1][2].setIcon(ico);
            holdBlockTiles[2][1].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            holdBlockTiles[2][3].setIcon(ico);
            break;
         case 6:  holdBlockTiles[2][1].setIcon(ico);
            holdBlockTiles[2][2].setIcon(ico);
            holdBlockTiles[1][2].setIcon(ico);
            holdBlockTiles[1][3].setIcon(ico);
            break;
      }
      if(!isHoldNull)
      {
         getHoldBlock();
         hold = _hold;
      }
      else
      {
         hold = _hold;
         getNextBlock();
      }
      drawShadow();
   }
   
   private static void generateNextBlock()
   {
      for(JLabel[] la: nextBlockTiles)
         for(JLabel l: la)l.setIcon(null);
      next = r.nextInt(7);
      ImageIcon ico = new ImageIcon(getScaledImage(tetriminos[next+(7*((level-1)/2))].getImage(),10,10));
      switch(next)
      {
         case 0:  nextBlockTiles[2][0].setIcon(ico);
            nextBlockTiles[2][1].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            nextBlockTiles[2][3].setIcon(ico);
            break;
         case 1:  nextBlockTiles[2][1].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            nextBlockTiles[2][3].setIcon(ico);
            nextBlockTiles[1][1].setIcon(ico);
            break;
         case 2:  nextBlockTiles[1][1].setIcon(ico);
            nextBlockTiles[1][2].setIcon(ico);
            nextBlockTiles[2][1].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            break;
         case 3:  nextBlockTiles[2][1].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            nextBlockTiles[2][3].setIcon(ico);
            nextBlockTiles[1][3].setIcon(ico);
            break;
         case 4:  nextBlockTiles[1][1].setIcon(ico);
            nextBlockTiles[1][2].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            nextBlockTiles[2][3].setIcon(ico);
            break;
         case 5:  nextBlockTiles[1][2].setIcon(ico);
            nextBlockTiles[2][1].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            nextBlockTiles[2][3].setIcon(ico);
            break;
         case 6:  nextBlockTiles[2][1].setIcon(ico);
            nextBlockTiles[2][2].setIcon(ico);
            nextBlockTiles[1][2].setIcon(ico);
            nextBlockTiles[1][3].setIcon(ico);
            break;
      }
   }
   
   private static void getNextBlock()
   {
      current = next;
      rotated = 0;
      if(next==0)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,0+3);
         tetriminoLocation.get(1).setCoordinates(2-3,1+3);
         tetriminoLocation.get(2).setCoordinates(2-3,2+3);
         tetriminoLocation.get(3).setCoordinates(2-3,3+3);
      }
      
      if(next==1)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,1+3);
         tetriminoLocation.get(1).setCoordinates(2-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,3+3);
         tetriminoLocation.get(3).setCoordinates(1-3,1+3);
      }
      
      if(next==2)
      {
         tetriminoLocation.get(0).setCoordinates(1-3,1+3);
         tetriminoLocation.get(1).setCoordinates(1-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,1+3);
         tetriminoLocation.get(3).setCoordinates(2-3,2+3);
      }
      
      if(next==3)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,1+3);
         tetriminoLocation.get(1).setCoordinates(2-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,3+3);
         tetriminoLocation.get(3).setCoordinates(1-3,3+3);
      }
      
      if(next==4)
      {
         tetriminoLocation.get(0).setCoordinates(1-3,1+3);
         tetriminoLocation.get(1).setCoordinates(1-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,2+3);
         tetriminoLocation.get(3).setCoordinates(2-3,3+3);
      }
      
      if(next==5)
      {
         tetriminoLocation.get(0).setCoordinates(1-3,2+3);
         tetriminoLocation.get(1).setCoordinates(2-3,1+3);
         tetriminoLocation.get(2).setCoordinates(2-3,2+3);
         tetriminoLocation.get(3).setCoordinates(2-3,3+3);
      }
      
      if(next==6)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,1+3);
         tetriminoLocation.get(1).setCoordinates(2-3,2+3);
         tetriminoLocation.get(2).setCoordinates(1-3,2+3);
         tetriminoLocation.get(3).setCoordinates(1-3,3+3);
      }
      generateNextBlock();
   }
   
   private void getHoldBlock()
   {
      current = hold;
      rotated = 0;
      if(current==0)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,0+3);
         tetriminoLocation.get(1).setCoordinates(2-3,1+3);
         tetriminoLocation.get(2).setCoordinates(2-3,2+3);
         tetriminoLocation.get(3).setCoordinates(2-3,3+3);
      }
      
      if(current==1)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,1+3);
         tetriminoLocation.get(1).setCoordinates(2-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,3+3);
         tetriminoLocation.get(3).setCoordinates(1-3,1+3);
      }
      
      if(current==2)
      {
         tetriminoLocation.get(0).setCoordinates(1-3,1+3);
         tetriminoLocation.get(1).setCoordinates(1-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,1+3);
         tetriminoLocation.get(3).setCoordinates(2-3,2+3);
      }
      
      if(current==3)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,1+3);
         tetriminoLocation.get(1).setCoordinates(2-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,3+3);
         tetriminoLocation.get(3).setCoordinates(1-3,3+3);
      }
      
      if(current==4)
      {
         tetriminoLocation.get(0).setCoordinates(1-3,1+3);
         tetriminoLocation.get(1).setCoordinates(1-3,2+3);
         tetriminoLocation.get(2).setCoordinates(2-3,2+3);
         tetriminoLocation.get(3).setCoordinates(2-3,3+3);
      }
      
      if(current==5)
      {
         tetriminoLocation.get(0).setCoordinates(1-3,2+3);
         tetriminoLocation.get(1).setCoordinates(2-3,1+3);
         tetriminoLocation.get(2).setCoordinates(2-3,2+3);
         tetriminoLocation.get(3).setCoordinates(2-3,3+3);
      }
      
      if(current==6)
      {
         tetriminoLocation.get(0).setCoordinates(2-3,1+3);
         tetriminoLocation.get(1).setCoordinates(2-3,2+3);
         tetriminoLocation.get(2).setCoordinates(1-3,2+3);
         tetriminoLocation.get(3).setCoordinates(1-3,3+3);
      }
   }
   
   private static BufferedImage getScaledImage(Image srcImg, int w, int h)
   {
      BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
      Graphics2D g2 = resizedImg.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(srcImg, 0, 0, w, h, null);
      g2.dispose();
      return resizedImg;
   }

   public static void main(String[]args)
   {
      new Tetris();
   }
   
   private class ArrayCoordinates2D
   {
      int row;
      int col;
      
      public ArrayCoordinates2D()
      {
         row=0;
         col=0;
      }
      
      public ArrayCoordinates2D(int row, int col)
      {
         this.row = row;
         this.col = col;
      }
      
      public void setCoordinates(int row, int col)
      {
         this.row = row;
         this.col = col;
      }
      
      public boolean compare(int row, int col)
      {
         if(this.row==row&&this.col==col)
            return true;
         else 
            return false;
      }
   }
   
   private class ImagePanel extends JPanel
   {
      BufferedImage img;
      
      public ImagePanel()
      {
         super();
      }
      
      public void setImage(BufferedImage img)
      {
         this.img = img;
      }
      
      @Override
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         g.drawImage(img,ins.left,ins.top,null);
      }
   }
   
   private class JImageLabel extends JLabel
   {
      BufferedImage img;
      
      public JImageLabel()
      {
         super();
      }
      
      public void setImage(BufferedImage img)
      {
         this.img = img;
      }
      
      @Override
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);
         g.drawImage(img,0,0,null);
      }
   }
}