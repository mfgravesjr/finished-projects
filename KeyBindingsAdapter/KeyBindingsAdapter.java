import javax.swing.JComponent;
import javax.swing.AbstractAction;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public abstract class KeyBindingsAdapter
{
   
   public static final int ALPHA_KEYS = 1000;
   public static final int NUMERIC_KEYS = 2000;
   public static final int DIRECTIONAL_KEYS = 3000;
   public static final int NUMPAD_KEYS = 4000;
   public static final int FUNCTION_KEYS = 5000;
   public static final int ALL_KEYS = 6000;
   
   /**
   *Creates a new KeyBindingsAdapter with no current Key Bindings.
   */
   public KeyBindingsAdapter(){}
   
   /**
   *Creates a new KeyBindingsAdapter with key bindings mapped to JComponent with key codes and an interval at which key events fire.
   *@param c the component that key bindings are mapped to
   *@param interval the interval at which key events are fired when specified keys are pressed
   *@param keyCodes integers corresponding to actual keys on the keyboard
   */
   public KeyBindingsAdapter(JComponent c, int interval, int... keyCodes)
   {
      createKeyBindings(c,interval,keyCodes);
   }
   
   /**
   *Maps key binding to JComponent with user-defined object, specified modifiers, and multiple key code arguments that serve as conditions to be met before event is fired.
   *For example if you want A+Enter to do something specific, you would call 
   *<p style="text-indent: 5em;"><code>createComboKeyBinding( c, "do something", 0, java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.VK_ENTER)</code> <p>
   *then override the comboPressed method to perform your desired task.
   *<p>
   *The following are valid arguments for the modifiers parameter<ul>
   *<li>java.awt.event.InputEvent.SHIFT_MASK
   *<li>java.awt.event.InputEvent.CTRL_MASK
   *<li>java.awt.event.InputEvent.META_MASK
   *<li>java.awt.event.InputEvent.ALT_MASK 
   *</ul>
   *<p>
   *Likewise, you can use the | bitwise operator to specify any combination of modifiers.
   *@param c the component that key bindings are mapped to
   *@param key a user-defined key for the new combo key binding
   *@param modifiers a bitwise-ored combination of any modifiers
   *@param keyCodes integers corresponding to actual keys on the keyboard
   */
   public void createComboKeyBinding(JComponent c, Object key, int modifiers, int... keyCodes)
   {
      String tempEventTxt = "";
      for(int i: keyCodes)
      {
         for(Field f: new KeyEvent(c, 0, 0, 0, 0, ' ').getClass().getDeclaredFields())
         {
            try
            {
               f.setAccessible(true);
               if(i==f.getInt(f))
                  try{tempEventTxt += (f.toString()+" ").split("VK_")[1];}
                  catch(ArrayIndexOutOfBoundsException ex){throw new IllegalArgumentException("keyCode parameter not valid. Cannot find applicable field in Class KeyEvent with keyCode "+ i,ex);}
            }
            catch(IllegalAccessException|IllegalArgumentException ex2){}
         }
         if(tempEventTxt.equals(""))throw new IllegalArgumentException("keyCode parameter not valid. Cannot find applicable field in Class KeyEvent with keyCode "+ i);
         
      }
      String eventTxt = tempEventTxt;
      boolean[] conditionsMet = new boolean[keyCodes.length];
      for(int i = 0; i < keyCodes.length; i++)conditionsMet[i] = false;
      CustomPressAction[] pressedActions = new CustomPressAction[keyCodes.length];
      CustomReleaseAction[] releasedActions = new CustomReleaseAction[keyCodes.length];
      for(int i = 0; i < keyCodes.length; i++)
      {
         pressedActions[i] = new CustomPressAction(i,conditionsMet,key);
         releasedActions[i] = new CustomReleaseAction(i,conditionsMet);
         c.getInputMap().put(KeyStroke.getKeyStroke(keyCodes[i],modifiers),key.toString()+i+" pressed");
         c.getInputMap().put(KeyStroke.getKeyStroke(keyCodes[i],0,true),key.toString()+i+" released");
         c.getActionMap().put(key.toString()+i+" pressed",pressedActions[i]);
         c.getActionMap().put(key.toString()+i+" released",releasedActions[i]);
      }
   }
   
   
   /**
   *Creates a new key bindings mapped to JComponent with key codes and an interval at which key events fire.
   *<p>
   *The following are valid arguments for the modifiers parameter<ul>
   *<li>java.awt.event.InputEvent.SHIFT_MASK
   *<li>java.awt.event.InputEvent.CTRL_MASK
   *<li>java.awt.event.InputEvent.META_MASK
   *<li>java.awt.event.InputEvent.ALT_MASK 
   *</ul>
   *<p>
   *Likewise, you can use the | bitwise operator to specify any combination of modifiers.
   *@param c the component that key bindings are mapped to
   *@param interval the interval at which key events are fired when specified keys are pressed
   *@param modifiers a bitwise-ored combination of any modifiers
   *@param keyCode an integer corresponding to an actual key on the keyboard
   */
   public void createKeyBinding(JComponent c, int interval, int modifiers, int keyCode)
   {
      String temp = "";
      for(Field f: new KeyEvent(c, 0, 0, 0, 0, ' ').getClass().getDeclaredFields())
      {
         try
         {
            f.setAccessible(true);
            try{if(keyCode==f.getInt(f)) temp=(f.toString()).split("VK_")[1];}
            catch(ArrayIndexOutOfBoundsException ex){throw new IllegalArgumentException("keyCode parameter not valid. Cannot find applicable field in Class KeyEvent with keyCode "+ keyCode,ex);}
         }
         catch(IllegalAccessException|IllegalArgumentException ex2){}
      }
      if(temp.equals(""))throw new IllegalArgumentException("keyCode parameter not valid. Cannot find applicable field in Class KeyEvent with keyCode "+ keyCode);
      
      Timer timer = new Timer(interval,new AbstractAction(){public void actionPerformed(ActionEvent e){keyPressed(new KeyEvent(c,KeyEvent.KEY_PRESSED,System.currentTimeMillis(),0,keyCode,KeyEvent.CHAR_UNDEFINED));}});
      
      AbstractAction pressedAction = new AbstractAction(){public void actionPerformed(ActionEvent e){if(!timer.isRunning())keyPressed(new KeyEvent(c,KeyEvent.KEY_PRESSED,System.currentTimeMillis(),0,keyCode,KeyEvent.CHAR_UNDEFINED));timer.start();}};
      AbstractAction releasedAction = new AbstractAction(){public void actionPerformed(ActionEvent e){timer.stop();keyReleased(new KeyEvent(c,KeyEvent.KEY_RELEASED,System.currentTimeMillis(),0,keyCode,KeyEvent.CHAR_UNDEFINED));}};
      c.getInputMap().put(KeyStroke.getKeyStroke(keyCode,modifiers),keyCode+" pressed");
      c.getInputMap().put(KeyStroke.getKeyStroke(keyCode,0,true),keyCode+" released");
      c.getActionMap().put(keyCode+" pressed",pressedAction);
      c.getActionMap().put(keyCode+" released",releasedAction);
   }
   
   /**
   *Creates a new KeyBindingsAdapter with key bindings mapped to JComponent with key codes and an interval at which key events fire.
   *@param c the component that key bindings are mapped to
   *@param interval the interval at which key events are fired when specified keys are pressed
   *@param keyCodes integers corresponding to actual keys on the keyboard
   */
   public void createKeyBindings(JComponent c, int interval, int... keyCodes)
   {
      boolean allKeysSelected = false;
      ArrayList<Integer> projectedKeyCodes = new ArrayList<Integer>();
      for(int k: keyCodes)
      {
         if(k==ALPHA_KEYS)for(int p = 65; p < 91; p++)projectedKeyCodes.add(new Integer(p));
         else if(k==NUMERIC_KEYS)for(int p = 48; p < 58; p++)projectedKeyCodes.add(new Integer(p));
         else if(k==DIRECTIONAL_KEYS)for(int p = 37; p < 41; p++)projectedKeyCodes.add(new Integer(p));
         else if(k==NUMPAD_KEYS)for(int p = 96; p < 112; p++)projectedKeyCodes.add(new Integer(p));
         else if(k==FUNCTION_KEYS)for(int p = 112; p < 124; p++)projectedKeyCodes.add(new Integer(p));
         else if(k==ALL_KEYS)allKeysSelected = true;
         else projectedKeyCodes.add(k);
      }
      if(allKeysSelected)
      {
         keyCodes = new int[]{10,8,9,3,12,16,17,18,19,20,27,32,33,34,35,36,37,38,39,40,44,45,46,47,48,49,50,51,52,53,54,55,56,57,59,61,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,96,97,98,99,100,101,102,103,104,105,106,107,108,108,109,110,111,127,144,145,112,113,114,115,116,117,118,119,120,121,122,123,154,155,156,157,192,222,224,225,226,227,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,150,151,152,153,160,161,162,512,513,514,515,516,517,518,519,520,521,522,523,524,525,24,28,29,30,31,21,25,240,241,242,243,244,245,256,257,258,259,260,261,262,263,0};
      }
      else
      {
         keyCodes = projectedKeyCodes.stream().mapToInt(i -> i).toArray();
      }
      
      int shiftMaskAt = -1;
      int ctrlMaskAt = -1;
      int metaMaskAt = -1;
      int altMaskAt = -1;
      for(int k = 0; k < keyCodes.length; k++)
      {
         if(keyCodes[k]==KeyEvent.VK_SHIFT)shiftMaskAt = k;
         if(keyCodes[k]==KeyEvent.VK_CONTROL)ctrlMaskAt = k;
         if(keyCodes[k]==KeyEvent.VK_META)metaMaskAt = k;
         if(keyCodes[k]==KeyEvent.VK_ALT)altMaskAt = k;
      }
      
      Timer[] timers = new Timer[keyCodes.length];
      for(int i = 0; i < keyCodes.length; i++)
      {
         int keyCode = keyCodes[i];
         timers[i] = new Timer(interval,new AbstractAction(){public void actionPerformed(ActionEvent e){keyPressed(new KeyEvent(c,KeyEvent.KEY_PRESSED,System.currentTimeMillis(),0,keyCode,KeyEvent.CHAR_UNDEFINED));}});
      }
         
      for(int i = 0; i < keyCodes.length; i++)
      {
         String tempEventTxt = "";
         int keyCode = keyCodes[i];
         for(Field f: new KeyEvent(c, 0, 0, 0, 0, ' ').getClass().getDeclaredFields())
         {
            try
            {
               f.setAccessible(true);
               if(keyCodes[i]==f.getInt(f))
                  try{tempEventTxt=f.toString().split("VK_")[1];}
                  catch(ArrayIndexOutOfBoundsException ex){throw new IllegalArgumentException("keyCode parameter not valid. Cannot find applicable field in Class KeyEvent with keyCode "+keyCodes[i],ex);
               }
            }
            catch(IllegalAccessException|IllegalArgumentException ex2){}
         }
         if(tempEventTxt.equals(""))throw new IllegalArgumentException("keyCode parameter not valid. Cannot find applicable field in Class KeyEvent with keyCode "+i);
         
         String eventTxt = tempEventTxt;
         
         Timer timer = timers[i];
         int _shiftMaskAt = shiftMaskAt;
         int _ctrlMaskAt = ctrlMaskAt;
         int _metaMaskAt = metaMaskAt;
         int _altMaskAt = altMaskAt;
         AbstractAction pressedAction = new AbstractAction(){public void actionPerformed(ActionEvent e)
         {
            if(_shiftMaskAt>0){if(keyCode!=KeyEvent.VK_SHIFT)timers[_shiftMaskAt].stop();
            else for(Timer t: timers)t.stop();}
            if(_ctrlMaskAt>0){if(keyCode!=KeyEvent.VK_CONTROL)timers[_ctrlMaskAt].stop();
            else for(Timer t: timers)t.stop();}
            if(_metaMaskAt>0){if(keyCode!=KeyEvent.VK_META)timers[_metaMaskAt].stop();
            else for(Timer t: timers)t.stop();}
            if(_altMaskAt>0){if(keyCode!=KeyEvent.VK_ALT)timers[_altMaskAt].stop();
            else for(Timer t: timers)t.stop();}
            if(!timer.isRunning())keyPressed(new KeyEvent(c,KeyEvent.KEY_PRESSED,System.currentTimeMillis(),0,keyCode,KeyEvent.CHAR_UNDEFINED));
            timer.start();}
         };
         AbstractAction releasedAction = new AbstractAction(){public void actionPerformed(ActionEvent e){timer.stop();keyReleased(new KeyEvent(c,KeyEvent.KEY_RELEASED,System.currentTimeMillis(),0,keyCode,KeyEvent.CHAR_UNDEFINED));}};
         c.getInputMap().put(KeyStroke.getKeyStroke(keyCode,(keyCode==KeyEvent.VK_SHIFT?KeyEvent.SHIFT_MASK:(keyCode==KeyEvent.VK_CONTROL?KeyEvent.CTRL_MASK:(keyCode==KeyEvent.VK_META?KeyEvent.META_MASK:(keyCode==KeyEvent.VK_ALT?KeyEvent.ALT_MASK:0))))),eventTxt+" pressed");
         c.getInputMap().put(KeyStroke.getKeyStroke(keyCode,0,true),eventTxt+" released");
         c.getActionMap().put(eventTxt+" pressed",pressedAction);
         c.getActionMap().put(eventTxt+" released",releasedAction);
      }
   }
   
   /**
   *A method that accepts KeyEvents from the current key bindings when a key has been pressed. See the class description for KeyEvent for a definition of a key pressed event.
   */
   public abstract void keyPressed(KeyEvent e);
   /**
   *A method that accepts KeyEvents from the current key bindings when a key has been released. See the class description for KeyEvent for a definition of a key released event.
   *///
   public abstract void keyReleased(KeyEvent e);
   /**
   *A method that accepts the user-defined key from a combo key binding when a key combination has been pressed.
   */
   public abstract void comboPressed(Object key);
   
   
   private class CustomPressAction extends AbstractAction
   {
      int id;
      boolean[] booleans;
      Object key;
      
      public CustomPressAction(int id, boolean[] booleans, Object key)
      {
         super();
         this.id = id;
         this.booleans = booleans;
         this.key = key;
      }
      
      public void actionPerformed(ActionEvent e)
      {
         boolean pass = true;
         booleans[id] = true;
         for(int i = 0; i < booleans.length; i++)if(booleans[i]==false)pass=false;
         if(pass)comboPressed(key);
      }
   }
   
   
   private class CustomReleaseAction extends AbstractAction
   {
      int id;
      boolean[] booleans;
      
      public CustomReleaseAction(int id, boolean[] booleans)
      {
         super();
         this.id = id;
         this.booleans = booleans;
      }
      
      public void actionPerformed(ActionEvent e)
      {
         booleans[id] = false;
      }
   }
}