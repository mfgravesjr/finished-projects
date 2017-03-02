import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import javax.swing.event.ChangeListener;
import javax.swing.SpinnerModel;
import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import javax.swing.JRootPane;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.InputMap;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Calendar;
import java.util.Date;

class AlarmClock implements ActionListener
{
   private String defaultPowerScheme = "";
   private JFrame window = new JFrame("Alarm Clock");
   private JPanel root = new JPanel();
   private JPanel fr = new JPanel();
   private JPanel fr2 = new JPanel();
   private JLabel hourLbl = new JLabel("<html><center>Hour</center></html>");
   private JLabel minLbl = new JLabel("<html><center>Min</center></html>");
   private JLabel amLbl = new JLabel("<html><center>AM</center></html>");
   private JLabel pmLbl = new JLabel("<html><center>PM</center></html>");
   private SpinnerClockModel hourModel;
   private SpinnerClockModel minModel;
   private JSpinner hourFld = new JSpinner();
   private JSpinner minFld = new JSpinner();
   private JRadioButton amBtn = new JRadioButton("",true);
   private JRadioButton pmBtn = new JRadioButton("",false);
   private ButtonGroup amPmGroup = new ButtonGroup();
   private JButton setAlarmBtn = new JButton("Goodnight");
   private GridBagConstraints gbc = new GridBagConstraints();
   private GridBagLayout glay = new GridBagLayout();
   private Date alarm = null;
   private TimerTask task = null;
   private Calendar cal = null;
   private JCheckBox snooze = new JCheckBox("Snooze",true);
   private SpinnerNumberModel model = new SpinnerNumberModel(5,1,60,1);
   private JSpinner spinner = new JSpinner(model);
   private JLabel spinnerLbl = new JLabel(" Min");
   private boolean snoozeSelected = true;
   private CardLayout clay = new CardLayout();
   private JButton cancel = new JButton("Cancel Alarm");
   private Timer timer = new Timer();
   private SpinnerClockModel secondHourModel;
   private SpinnerClockModel secondMinModel;
   private JSpinner secondHourSpinner = new JSpinner();
   private JSpinner secondMinSpinner = new JSpinner();
   private JLabel secondHourLbl = new JLabel(" Hrs");
   private JLabel secondMinLbl = new JLabel(" Min");
   private JCheckBox firstOption = new JCheckBox("",true);
   private JCheckBox secondOption = new JCheckBox("",false);
   private ButtonGroup secondBtnGroup = new ButtonGroup(); 
   private JLabel time = new JLabel();
   private Timer clockManager = new Timer(true);
   private TimerTask clock = new TimerTask()
   {
      public void run()
      {
            Calendar c = new Calendar.Builder().build();
            c.setTime(new Date());
            int h = c.get(c.HOUR);
            int m = c.get(c.MINUTE);
            int s = c.get(c.SECOND);
            String hs = "";
            String ms = "";
            if(h==0)hs = "12";
            else hs = new Integer(h).toString();
            if(m<10)ms = "0";
            ms += new Integer(m).toString();
            String ap = (c.get(c.AM_PM)==c.PM?"PM":"AM");
            time.setText(hs+(s%2==0?":":" ")+ms+" "+ap);
      }
   };
   
   public AlarmClock()
   {
      clockManager.scheduleAtFixedRate(clock,new Date(),1000);
      root.setLayout(clay);
      glay.columnWidths = new int[]{50,40,50,50};
      glay.rowHeights = new int[]{20,30,20,35};
      fr.setLayout(glay);
      
      gbc.insets = new Insets(0,10,0,0);
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.anchor = gbc.SOUTH;
      gbc.gridx = 0;
      gbc.gridy = 0;
      fr.add(hourLbl,gbc);
      
      gbc.insets = new Insets(0,0,0,0);
      gbc.gridx = 1;
      fr.add(minLbl,gbc);
      
      gbc.gridx = 2;
      fr.add(amLbl,gbc);
      
      gbc.gridx = 3;
      gbc.insets = new Insets(0,0,0,0);
      fr.add(pmLbl,gbc);
      
      gbc.gridx = 4;
      gbc.gridheight = 6;
      gbc.fill = gbc.VERTICAL;
      fr.add(new JSeparator(JSeparator.VERTICAL),gbc);
      
      gbc.gridheight = 1;
      gbc.anchor = gbc.CENTER;
      gbc.fill = gbc.BOTH;
      gbc.insets = new Insets(0,10,5,0);
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.gridwidth = 1;
      fr.add(hourFld,gbc);
      
      gbc.insets = new Insets(0,0,5,0);
      gbc.gridx = 1;
      fr.add(minFld,gbc);
      
      gbc.fill = gbc.NONE;
      gbc.gridx = 2;
      fr.add(amBtn,gbc);
      
      gbc.gridx = 3;
      gbc.insets = new Insets(0,0,5,10);
      fr.add(pmBtn,gbc);
      
      gbc.fill = gbc.NONE;
      gbc.anchor = gbc.CENTER;
      gbc.gridheight = 1;
      gbc.gridx = 5;
      gbc.insets = new Insets(0,0,0,0);
      fr.add(firstOption,gbc);
      
      gbc.insets = new Insets(0,0,0,0);
      gbc.gridy = 2;
      gbc.gridx = 0;
      gbc.gridwidth = 4;
      gbc.gridheight = 1;
      gbc.anchor = gbc.CENTER;
      fr.add(new JLabel("OR"),gbc);
      
      gbc.anchor = gbc.EAST;
      gbc.gridy = 3;
      gbc.gridx = 0;
      gbc.gridwidth = 1;
      fr.add(secondHourSpinner,gbc);
      
      gbc.anchor = gbc.WEST;
      gbc.gridx = 1;
      fr.add(secondHourLbl,gbc);
      
      gbc.anchor = gbc.EAST;
      gbc.gridx = 2;
      fr.add(secondMinSpinner,gbc);
      
      gbc.anchor = gbc.WEST;
      gbc.gridx = 3;
      fr.add(secondMinLbl,gbc);
      
      gbc.gridx = 5;
      gbc.anchor = gbc.CENTER;
      fr.add(secondOption,gbc);
      
      gbc.anchor = gbc.CENTER;
      gbc.gridx = 0;
      gbc.gridy = 4;
      gbc.gridwidth = 4;
      gbc.insets = new Insets(0,0,10,0);
      fr.add(setAlarmBtn,gbc);
      
      gbc.gridx = 5;
      gbc.insets = new Insets(0,0,0,0);
      gbc.gridwidth = 1; 
      gbc.gridheight = 2;
      fr.add(time,gbc);
      
      gbc.anchor = gbc.EAST;
      gbc.insets = new Insets(10,10,10,0);
      gbc.gridx = 0;
      gbc.gridy = 5;
      gbc.gridwidth = 2;
      fr.add(snooze,gbc);
      
      gbc.gridheight = 1;
      gbc.insets = new Insets(10,0,10,0);
      gbc.gridwidth = 1;
      gbc.anchor = gbc.EAST;
      gbc.gridx = 2;
      fr.add(spinner,gbc);
      
      gbc.anchor = gbc.WEST;
      gbc.gridx = 3;
      gbc.insets = new Insets(10,0,10,10);
      fr.add(spinnerLbl,gbc);
      
//       time.setUI(new RotatedLabelUI(true));
      amPmGroup.add(amBtn);
      amPmGroup.add(pmBtn);
      secondBtnGroup.add(firstOption);
      secondBtnGroup.add(secondOption);
      secondHourSpinner.setEnabled(false);
      secondMinSpinner.setEnabled(false);
      secondHourLbl.setEnabled(false);
      secondMinLbl.setEnabled(false);
      setAlarmBtn.addActionListener(this);
      snooze.addActionListener(this);
      
      firstOption.addItemListener(new ItemListener()
      {
         public void itemStateChanged(ItemEvent e)
         {
            if(firstOption.isSelected())
            {
               secondHourSpinner.setEnabled(false);
               secondMinSpinner.setEnabled(false);
               secondHourLbl.setEnabled(false);
               secondMinLbl.setEnabled(false);
               hourFld.setEnabled(true);
               minFld.setEnabled(true);
               amBtn.setEnabled(true);
               pmBtn.setEnabled(true);
               hourLbl.setEnabled(true);
               minLbl.setEnabled(true);
               amLbl.setEnabled(true);
               pmLbl.setEnabled(true);
            }
            else
            {
               secondHourSpinner.setEnabled(true);
               secondMinSpinner.setEnabled(true);
               secondHourLbl.setEnabled(true);
               secondMinLbl.setEnabled(true);
               hourFld.setEnabled(false);
               minFld.setEnabled(false);
               amBtn.setEnabled(false);
               pmBtn.setEnabled(false);
               hourLbl.setEnabled(false);
               minLbl.setEnabled(false);
               amLbl.setEnabled(false);
               pmLbl.setEnabled(false);
            }
         }
      });
           
      window.addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            restorePowerSettings();
            System.exit(0);
         }
      });
      try
      {
         Process p = Runtime.getRuntime().exec("powercfg.exe -list");
         InputStream is = p.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String line;
         while ((line = br.readLine()) != null)
         {
             if(line.contains(") *"))break;
         }
         defaultPowerScheme = line.split(" ")[3];
      }
      catch(IOException ex){ex.printStackTrace();}
      window.add(root);
      root.add(fr,"setting timer");
      
      fr2.add(cancel);
      cancel.addActionListener(this);
      root.add(fr2,"cancel timer");
      
      cal = new Calendar.Builder().build();
      cal.setTime(new Date());
      hourModel = new SpinnerClockModel((cal.get(cal.HOUR)==0?12:cal.get(cal.HOUR)),1,12,1,amBtn,pmBtn);
      minModel = new SpinnerClockModel(cal.get(cal.MINUTE),0,59,1,hourModel);
      secondHourModel = new SpinnerClockModel(0,0,23,1);
      secondMinModel = new SpinnerClockModel(0,0,59,1,secondHourModel);
      hourFld.setModel(hourModel);
      minFld.setModel(minModel);
      secondHourSpinner.setModel(secondHourModel);
      secondMinSpinner.setModel(secondMinModel);
      if(cal.get(cal.AM_PM)==cal.AM)amBtn.setSelected(true);
      else pmBtn.setSelected(true);
      
      window.pack();
      WindowPositioner.setLocation(window,WindowPositioner.CENTER,true);
      window.setVisible(true);
   }
   
   public void actionPerformed(ActionEvent e)
   {
      if(e.getSource()==setAlarmBtn&&firstOption.isSelected())
      {
         setAlarm(Integer.parseInt(((JSpinner.DefaultEditor)hourFld.getEditor()).getTextField().getText()),Integer.parseInt(((JSpinner.DefaultEditor)minFld.getEditor()).getTextField().getText()),pmBtn.isSelected());
         cancel.setText("Cancel "+cal.get(cal.HOUR)+":"+(cal.get(cal.MINUTE)<10?"0":"")+cal.get(cal.MINUTE)+(cal.get(cal.AM_PM)==cal.PM?"PM":"AM")+" Alarm");
         clay.show(root,"cancel timer");
      }
      
      if(e.getSource()==setAlarmBtn&&secondOption.isSelected())
      {
         Calendar cal2 = new Calendar.Builder().build();
         cal2.setTime(new Date());
         cal2.add(cal2.HOUR,Integer.parseInt(((JSpinner.DefaultEditor)secondHourSpinner.getEditor()).getTextField().getText()));
         cal2.add(cal2.MINUTE,Integer.parseInt(((JSpinner.DefaultEditor)secondMinSpinner.getEditor()).getTextField().getText()));
         setAlarm(cal2.get(cal2.HOUR),cal2.get(cal2.MINUTE),(cal2.get(cal2.AM_PM)==cal2.PM?true:false));
         cancel.setText("Cancel "+cal.get(cal.HOUR)+":"+(cal.get(cal.MINUTE)<10?"0":"")+cal.get(cal.MINUTE)+(cal.get(cal.AM_PM)==cal.PM?"PM":"AM")+" Alarm");
         clay.show(root,"cancel timer");
      }
      
      if(e.getSource()==cancel)
      {
         timer.cancel();
         restorePowerSettings();
         clay.show(root,"setting timer");
      }
      
      if(e.getSource()==snooze)
      {
         spinner.setEnabled(snooze.isSelected());
         spinnerLbl.setEnabled(snooze.isSelected());
      }
   }
   
   public void restorePowerSettings()
   {
      try{Runtime.getRuntime().exec("powercfg.exe /setactive "+defaultPowerScheme);}
      catch(IOException ex){ex.printStackTrace();}
   }
   
   public void setAlarm(int hour, int min, boolean pm)
   {
      cal.setTime(new Date());
      int currentHr = cal.get(cal.HOUR);
      int currentMin = cal.get(cal.MINUTE);
      if(hour == 12)hour = 0;
      int amPm = cal.get(cal.AM_PM);
      int testerCurrentHr = currentHr;
      if(amPm==cal.PM)
      {
         testerCurrentHr+=12;
      }
      int testerHour = hour;
      if(pm)
      {
         testerHour+=12;
      }
      if(testerCurrentHr*60+currentMin>testerHour*60+min)cal.add(cal.DAY_OF_MONTH,1);
      cal.set(cal.HOUR,hour);
      cal.set(cal.MINUTE,min);
      cal.set(cal.SECOND,0);
      cal.set(cal.AM_PM,(pm?cal.PM:cal.AM));
      window.setExtendedState(window.ICONIFIED);
      alarm = cal.getTime();
      timer = new Timer();
      task = new TimerTask(){public void run(){alarmTask();}};
      
      try{Process p = Runtime.getRuntime().exec("powercfg.exe /setactive 171c2d4b-78a8-43a3-887b-e1b95faf7061");}
      catch(IOException ex){ex.printStackTrace();}
      if(snooze.isSelected())
      {
         snoozeSelected = true;
         timer.scheduleAtFixedRate(task,alarm,Integer.parseInt(((JSpinner.NumberEditor)spinner.getEditor()).getTextField().getText())*60*1000);
      }
      else timer.schedule(task,alarm);
      
      int mo = cal.get(cal.MONTH)+1;
      int day = cal.get(cal.DAY_OF_MONTH);
      int yr = cal.get(cal.YEAR);
      String date = mo+"/"+day+"/"+yr;
      int hr = cal.get(cal.HOUR);
      if(hr == 0)hr = 12;
      String m = (cal.get(cal.MINUTE)<10?"0":"")+cal.get(cal.MINUTE);
      String ap = cal.get(cal.AM_PM)==cal.AM?"AM":"PM";
      String time = hr+":"+m+ap;
      Calendar currentTime = new Calendar.Builder().build();
      currentTime.setTime(new Date());
      currentTime.set(currentTime.SECOND,0);
      currentTime.set(currentTime.MILLISECOND,0);
      int difference = (int)(cal.getTimeInMillis()-currentTime.getTimeInMillis());
      int hours = difference/1000/60/60;
      int minutes = (difference/1000/60)-(hours*60);
      JOptionPane.showMessageDialog(null,"<html><center>Alarm is set for "+date+" at "+time+", "+hours+" hour"+(hours!=1?"s":"")+" and "+minutes+" minute"+(minutes!=1?"s":"")+" from now."
         +"</center> <br><br>Alarm clock is minimized. Do not power down your machine or send computer to sleep.<br>To disable alarm simply exit the program.<br><br><center>Goodnight!</center></html>","Alarm set",JOptionPane.PLAIN_MESSAGE);
   }
   
   public void alarmTask()
   {
      Clip clip = null;
      try{clip = AudioSystem.getClip();}
      catch(LineUnavailableException ex){ex.printStackTrace();}
      try{clip.open(AudioSystem.getAudioInputStream(new File("aos-InnerQuarters(dg).mid")));}
      catch(LineUnavailableException|UnsupportedAudioFileException|IOException ex){ex.printStackTrace();}
      clip.loop(clip.LOOP_CONTINUOUSLY);
      int option = JOptionPane.showOptionDialog(null,"Alarm time reached.","Wake up!",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,(snoozeSelected?new String[]{"Disable","Snooze"}:new String[]{"Disable"}),"Snooze");
      if(option==0)
      {
         timer.cancel();
         snoozeSelected = false;
         try{Runtime.getRuntime().exec("powercfg.exe /setactive "+defaultPowerScheme);}
         catch(IOException ex){ex.printStackTrace();}
         clip.stop();
         clay.show(root,"setting timer");
         window.setExtendedState(window.NORMAL);
      }
      else
      {
         clip.stop();
      }
   }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
      
    public static void main(String[] args)
   {
      new AlarmClock();
   }
   
   public class SpinnerClockModel implements SpinnerModel
   {
      ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
      SpinnerClockModel hourModel = null;
      AbstractButton am = null;
      AbstractButton pm = null;
      Object value = null;
      int minimum;
      int maximum;
      int stepSize;
      
      public SpinnerClockModel(int value, int minimum, int maximum, int stepSize)
      {
         this(value,minimum,maximum,stepSize,null);
      }
      
      public SpinnerClockModel(int value, int minimum, int maximum, int stepSize, AbstractButton am, AbstractButton pm)
      {
         this.am = am;
         this.pm = pm;
         this.value = ((Integer)value).toString();
         this.minimum = minimum;
         this.maximum = maximum;
         this.stepSize = stepSize;
      }
      
      public SpinnerClockModel(int value, int minimum, int maximum, int stepSize, SpinnerClockModel hourModel)
      {
         this.hourModel = hourModel;
         String valueString = ((Integer)value).toString();
         this.value = (valueString.length()<2?"0":"")+valueString;
         this.minimum = minimum;
         this.maximum = maximum;
         this.stepSize = stepSize;
      }
      
      @Override
      public void addChangeListener(ChangeListener listener)
      {
         listeners.add(listener);
      }
      
      @Override
      public void removeChangeListener(ChangeListener listener)
      {
         listeners.remove(listener);
      }
      
      @Override
      public Object getValue()
      {
         return value;
      }
      
      
      public Comparable getMinimum()
      {
         return (Integer)minimum;
      }
      
      
      public Comparable getMaximum()
      {
         return (Integer)maximum;
      }
      
      @Override
      public Object getNextValue()
      {
         int i = Integer.parseInt((String)getValue())+stepSize;
         if(i>(Integer)getMaximum())i=(Integer)getMinimum();
         return ((Integer)i).toString();
      }
      
      @Override
      public Object getPreviousValue()
      {
         int i = Integer.parseInt((String)getValue())-stepSize;
         if(i<(Integer)getMinimum())i=(Integer)getMaximum();
         return ((Integer)i).toString();
      }
       
      @Override
      public void setValue(Object value)
      {
         if((Integer.parseInt((String)getValue())+"").equals(getMaximum().toString())
            &&((String)value).equals(getMinimum().toString())
            &&hourModel!=null)
               hourModel.setValue(hourModel.getNextValue());
         if((Integer.parseInt((String)getValue())+"").equals(getMinimum().toString())
            &&((String)value).equals(getMaximum().toString())
            &&hourModel!=null)
               hourModel.setValue(hourModel.getPreviousValue());
         if(hourModel==null&&am!=null&&pm!=null)
            if((Integer.parseInt((String)value)==(Integer)getMaximum()
               &&Integer.parseInt((String)getValue())==(Integer)getMaximum()-1)
               ||
               (Integer.parseInt((String)value)==(Integer)getMaximum()-1
               &&Integer.parseInt((String)getValue())==(Integer)getMaximum()))
               {
                  if(am.isSelected())pm.setSelected(true);
                  else am.setSelected(true);
               }
         this.value = ((String)value).length()<2&&hourModel!=null?"0"+(String)value:(String)value;
         for(ChangeListener l: listeners) l.stateChanged(new ChangeEvent(this));
      }
   }
}