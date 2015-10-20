import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.DefaultButtonModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class JSwitchMobile extends AbstractButton {
        private Color black  = new Color(0,0,0,100);
        private Color white = new Color(255,255,255,100);
        private int gap;
        private int globalWidth = 0;
        private String trueLabel;
        private String falseLabel;
        private Dimension thumbBounds;
        private int max;
        private int x;
        private int y = 0;
        private int w;
        private int h;
        private int relMousePos = 0;
        private int curMousePos = 0;
        private boolean isOrigSel;
        private boolean dragged;
        private Color trueColor;
        private Color falseColor;
        private Color textColor;
        private Color fgFalse;
        private Color fgTrue;
        private double trueLength;
        private double falseLength;
        private double fontHeight;

        /**
        *Creates a JSwitchMobile in the off position with default colors and no text.
        */
        public JSwitchMobile()
        {
            this("OFF","ON",false);
        }
        
        /**
        *Creates a JSwitchMobile with chosen text and on/off position, using default colors.
        *@param falseLabel the text displayed when the switch is in the off position.
        *@param trueLabel the text displayed when the switch is in the on position.
        *@param selected determines whether the JSwitchMobile starts in the on or off position.
        */
        public JSwitchMobile(String falseLabel, String trueLabel, boolean selected) {
            
            this(falseLabel, trueLabel, null,null,selected);
        }
        
        /**
        *Creates a JSwitchMobile with custom colors, text, and on/off position. If any of the colors are null, default colors are used.
        *@param falseLabel the text displayed when the switch is in the on position.
        *@param trueLabel the text displayed when the switch is in the on position.
        *@param falseColor the color of the components background when the switch is in the off position.
        *@param trueColor the color of the components background when the switch is in the on position.
        *@param selected determines whether the JSwitchMobile starts in the on or off position.
        */
        public JSwitchMobile(String falseLabel, String trueLabel, Color falseColor, Color trueColor, boolean selected){
            this.trueLabel = trueLabel;
            this.falseLabel = falseLabel;
            if(falseColor!=null)this.falseColor = falseColor;
            if(trueColor!=null)this.trueColor = trueColor;
            if(this.falseColor==null)this.falseColor = new Color(95,125,150);
            if(this.trueColor==null)this.trueColor = new Color(20,235,20);
            if(trueLabel==null)this.trueLabel="";
            if(falseLabel==null)this.falseLabel="";
            setFont(new JLabel().getFont());
            setForeground(lighter(this.falseColor),lighter(this.trueColor));
            setSelected( selected );
            addMouseMotionListener(new MouseMotionAdapter(){
                @Override
                public void mouseDragged(MouseEvent e){
                    dragged = true;
                    curMousePos = e.getXOnScreen();
                    x=curMousePos-relMousePos;
                    if(isOrigSel)x+=getWidth()-w+1;
                    if(x<0){x=0;if(isSelected())setSelected(false);}
                    if(x>getWidth()-w+1){x=getWidth()-w+1;if(!isSelected())setSelected(true);}
                    repaint();
                }
            });
            addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e){
                    isOrigSel = isSelected();
                    relMousePos = e.getXOnScreen();
                    curMousePos=relMousePos;
                }
                @Override
                public void mouseReleased( MouseEvent e ) {
                    if((!isOrigSel&&(curMousePos-relMousePos)>=0)||(isOrigSel&&(curMousePos-relMousePos)<=0)||!dragged)
                    {
                        relMousePos = 0;
                        curMousePos = 0;
                        setSelected(!isOrigSel);
                    }
                    dragged = false;
                }
            });
        }
        
        /**
        *Sets the state of the component
        *@param b true if the component is in the on state, otherwise false.
        */
        @Override
        public void setSelected(boolean b){
            super.setSelected(b);
            fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_FIRST,"Button Clicked"));
            if(b)x=getPreferredSize().width-w+1;
            else x=0;
            if(b)textColor = fgTrue;
            else textColor = fgFalse;
            repaint();
        }
        
        /**
        *If the preferredSize has been set to a non-null value just returns it. If the UI delegate's getPreferredSize method returns a non null value then return that; otherwise defer to the component's layout manager.
        *@return the value of the preferredSize property.
        */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(globalWidth, thumbBounds.height);
        }
        
        /**
        *Defines the two lines of text when this component will display. If the value of text is null or empty string, nothing is displayed. 
        *@param offText the new text to be set for the off position; if null the old text will be deleted
        *@param onText the new text to be set for the on position; if null the old text will be deleted
        */
        public void setText( String offText, String onText ) {
            if(offText!=null)falseLabel = offText;
            else falseLabel = "";
            if(onText!=null)trueLabel = onText;
            else trueLabel = "";
        }
        
        /**
        *Sets the font for this component.
        *@param font the desired Font for this component
        */
        @Override
        public void setFont(Font font) {
            super.setFont(font);
            fontHeight = getFontMetrics(getFont() ).getStringBounds( this.trueLabel,getGraphics() ).getHeight();
            trueLength = getFontMetrics(getFont() ).getStringBounds( this.trueLabel,getGraphics() ).getWidth();
            falseLength = getFontMetrics(getFont() ).getStringBounds( this.falseLabel, getGraphics() ).getWidth();
            gap = (int)fontHeight/2;
            max = (int)Math.max( trueLength, falseLength )+gap;
            thumbBounds  = new Dimension(max+gap*2,(int)(fontHeight*1.75));
            w = thumbBounds.width;
            h = thumbBounds.height;
            globalWidth =  w+(int)fontHeight;
            setModel( new DefaultButtonModel() );
            setBackground(Color.GRAY);
            if(isSelected())x=getWidth()-w+1;
            else x=0;
            repaint();
        }

        /**
        *Sets the foreground color of this component.
        *@param fgFalse the desired foreground Color for this component's off position
        *@param fgTrue the desire foreground Color for this component's on position
        */
        public void setForeground(Color fgFalse, Color fgTrue){
            this.fgFalse = fgFalse;
            this.fgTrue = fgTrue;
            repaint();
        }
        
        /**
        *Gets the current foreground color of this component.
        *@return this component's foreground color; if this component does not have a foreground color, the foreground color of its parent is returned.
        */
        @Override
        public Color getForeground(){
            return textColor;
        }
        
        /**
        *Creates a new Color that is a lighter version of this Color. 
        *@param c the Color object whose rgb values are intended to be manipulated
        *@return a new Color object that is a lighter version of this Color with the same alpha value.
        */
        private Color lighter(Color c)
        {
           int r = c.getRed();
           int g = c.getGreen();
           int b = c.getBlue();
           int max = Math.max(r,g);
           max = Math.max(max,b);
           max+=100;
           r=(r+max+max)/3;
           g=(g+max+max)/3;
           b=(b+max+max)/3;
           if(r>255)r=255;
           if(g>255)g=255;
           if(b>255)b=255;
           return new Color(r,g,b,c.getAlpha());
        }
        
        /**
        *Creates a new Color that is a more transparent version of this Color.
        *@param c the Color object whose alpha value is intended to be manipulated
        *@return a new Color object that has an alpha value of 100 with the same rgb values.
        */
        private Color trans(Color c)
        {
           return new Color(c.getRed(),c.getGreen(),c.getBlue(),100);
        }
        
        /**
        *Invoked by Swing to draw components. Applications should not invoke paint directly, but should instead use the repaint method to schedule the component for redrawing.
        *This method actually delegates the work of painting to three protected methods: paintComponent, paintBorder, and paintChildren. They're called in the order listed to ensure that children appear on top of component itself. Generally speaking, the component and its children should not paint in the insets area allocated to the border. Subclasses can just override this method, as always. A subclass that just wants to specialize the UI (look and feel) delegate's paint method should just override paintComponent.
        */
        @Override
        public void paint( Graphics g ) {
            // super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            Color bg = getBackground();
            Color tranBG = new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),0);
            
            //creating background
            RenderingHints rendHint = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            rendHint.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(rendHint);
            g2.setPaint(getBackground());
            g2.fillRoundRect( 1, 1, getWidth() -3, getHeight() -3, 8 ,8 );
            g2.setColor( black );
            g2.drawRoundRect( 1, 1, getWidth()-2 - 1, getHeight()-2 - 1, 8,8 );
            g2.setColor( white );
            g2.drawRoundRect( 2, 2, getWidth()-2 - 3, getHeight()-2 - 3, 8,8 );
            g2.setPaint(new GradientPaint(1,1, Color.BLACK,1,(int)(.4*(getHeight()-3)),tranBG ));
            g2.fillRoundRect( 3, 3, getWidth()-2 -3-2, getHeight()-2 -3-2, 8 ,8 );
            
            //creating switch button
            if(isSelected())g2.setPaint(trueColor);
            else g2.setPaint(falseColor);
            g2.fillRoundRect( x+1+3, y+1+3, w-3-6, h-2-6, 5,5);
            
            g2.setPaint(trans(Color.WHITE));
            g2.fillRect(x+1+3+1, y+1+3, w-3-6-2, 1);
            g2.fillRect(x+1+3, y+1+4, 1, 1);
            g2.fillRect(x+1+3+w-3-6-1, y+1+4, 1,1);
            if(isSelected())g2.setPaint(trueColor.darker());
            else g2.setPaint(falseColor.darker());
            g2.fillRect(x+1+3+1, y+1+3+h-2-6-1, w-3-6-2, 1);
            g2.fillRect(x+1+3, y+1+3+h-2-6-2, 1,1);
            g2.fillRect(x+1+3+w-3-6-1, y+1+3+h-2-6-2,1,1);

            //creating text
            g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g2.setFont( getFont() );
            g2.setColor(new Color(100,100,100,100));
            if(!isSelected())g2.drawString(falseLabel,x+(int)(w-falseLength)/2+1,h/2+(int)fontHeight/2-3);
            if(isSelected())g2.drawString(trueLabel,x+(int)(w-trueLength)/2+1, h/2+(int)fontHeight/2-3 );
            g2.setColor(textColor);
            if(!isSelected())g2.drawString(falseLabel,x+(int)(w-falseLength)/2, y+h/2+(int)fontHeight/2-4 );
            if(isSelected())g2.drawString(trueLabel,x+(int)(w-trueLength)/2, y+h/2+(int)fontHeight/2-4 );
        }
    }