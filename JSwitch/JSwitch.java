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

public class JSwitch extends AbstractButton {
        private Color black  = new Color(0,0,0,100);
        private Color white = new Color(255,255,255,100);
        private int gap = 5;
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
        private Color falseColor;
        private Color trueColor;
        private Color textColor;
        private double trueLength;
        private double falseLength;
        private double fontHeight;

        /**
        *Creates a JSwitch in the off position with default colors and no text.
        */
        public JSwitch()
        {
            this("     ","     ",false);
        }
        
        /**
        *Creates a JSwitch with chosen text and position, using default colors.
        *@param falseLabel the text displayed when the switch is in the off position.
        *@param trueLabel the text displayed when the switch is in the on position.
        *@param selected determines whether the JSwitch starts in the on or off position.
        */
        public JSwitch(String falseLabel, String trueLabel, boolean selected) {
            
            this(falseLabel, trueLabel, null,null,selected);
        }
        
        /**
        *Creates a JSwitch with custom colors, text, and position. If any of the colors are null, default colors are used.
        *@param falseLabel the text displayed when the switch is in the on position.
        *@param trueLabel the text displayed when the switch is in the on position.
        *@param falseColor the color of the components background when the switch is in the off position.
        *@param trueColor the color of the components background when the switch is in the on position.
        *@param selected determines whether the JSwitch starts in the on or off position.
        */
        public JSwitch(String falseLabel, String trueLabel, Color falseColor, Color trueColor, boolean selected){
            this.trueLabel = trueLabel;
            this.falseLabel = falseLabel;
            this.falseColor = falseColor;
            this.trueColor = trueColor;
            if(trueLabel==null)this.trueLabel="";
            if(falseLabel==null)this.falseLabel="";
            fontHeight = getFontMetrics(new JLabel().getFont() ).getStringBounds( this.trueLabel,getGraphics() ).getHeight();
            trueLength = getFontMetrics(new JLabel().getFont() ).getStringBounds( this.trueLabel,getGraphics() ).getWidth();
            falseLength = getFontMetrics( new JLabel().getFont() ).getStringBounds( this.falseLabel, getGraphics() ).getWidth();
            max = (int)Math.max( trueLength, falseLength );
            thumbBounds  = new Dimension(max+gap*2,(int)(fontHeight*1.35));
            globalWidth =  max + thumbBounds.width+gap*2;
            setModel( new DefaultButtonModel() );
            w = thumbBounds.width;
            h = thumbBounds.height;
            setSelected( selected );
            addMouseMotionListener(new MouseMotionAdapter(){
                @Override
                public void mouseDragged(MouseEvent e){
                    dragged = true;
                    curMousePos = e.getXOnScreen();
                    if(isEnabled())x=curMousePos-relMousePos;
                    else x=0;
                    if(isOrigSel)x+=w;
                    if(x<0){x=0;if(isSelected())setSelected(false);}
                    if(x>w){x=w;if(!isSelected())setSelected(true);}
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
                        if(isEnabled())setSelected(!isOrigSel);
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
            if(isSelected())setBackground(trueColor);
            else setBackground(falseColor);
            if(isSelected())x=w;
            else x=0;
            repaint();
        }
        
        /**
        *If the preferredSize has been set to a non-null value just returns it. If the UI delegate's getPreferredSize method returns a non null value then return that; otherwise defer to the component's layout manager.
        */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(globalWidth, thumbBounds.height);
        }
        
        /**
        *Sets the text for this component's off position. If the value of text is null or empty string, nothing is displayed.
        *@param text the new text to be set for the off position; if null the old text will be deleted
        */
        public void setOffText( String text ) {
            if(text!=null)falseLabel = text;
            else falseLabel = "";
        }
        
        /**
        *Sets the text for this component's on position. If the value of text is null or empty string, nothing is displayed.
        *@param text the new text to be set for the on position; if null the old text will be deleted
        */
        public void setOnText( String text) {
            if(text!=null)trueLabel = text;
            else trueLabel = "";
        }
        
        /**
        *Sets the font for this component.
        *@param font the desired Font for this component
        */
        @Override
        public void setFont(Font font) {
            super.setFont(font);
            fontHeight = getFontMetrics(getFont() ).getStringBounds( trueLabel,getGraphics() ).getHeight();
            trueLength = getFontMetrics(getFont() ).getStringBounds( trueLabel,getGraphics() ).getWidth();
            falseLength = getFontMetrics(getFont() ).getStringBounds( falseLabel, getGraphics() ).getWidth();
            max = (int)Math.max( trueLength, falseLength );
            thumbBounds  = new Dimension(max+gap*2,(int)(fontHeight*1.35));
            globalWidth =  max + thumbBounds.width+gap*2;
            w = thumbBounds.width;
            h = thumbBounds.height;
            if(isSelected())x=w;
            else x=0;
            repaint();
        }

        /**
        *Sets the foreground color of this component.
        *@param fg the desired foreground Color
        */
        @Override
        public void setForeground(Color fg){
            super.setForeground(fg);
            textColor = fg;
            repaint();
        }
        
        /**
        *Invoked by Swing to draw components. Applications should not invoke paint directly, but should instead use the repaint method to schedule the component for redrawing.
        *This method actually delegates the work of painting to three protected methods: paintComponent, paintBorder, and paintChildren. They're called in the order listed to ensure that children appear on top of component itself. Generally speaking, the component and its children should not paint in the insets area allocated to the border. Subclasses can just override this method, as always. A subclass that just wants to specialize the UI (look and feel) delegate's paint method should just override paintComponent.
        */
        @Override
        public void paint( Graphics g ) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            Color bg = getBackground();
            Color tranBG = new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),0);
            
            //creating background
            RenderingHints rendHint = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            rendHint.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHints(rendHint);
            g2.setPaint(getBackground());
            g2.fillRoundRect( 1, 1, getWidth() -3, getHeight() -3, 3 ,3 );
            g2.setColor( black );
            g2.drawRoundRect( 1, 1, getWidth()-2 - 1, getHeight()-2 - 1, 5,5 );
            g2.setColor( white );
            g2.drawRoundRect( 2, 2, getWidth()-2 - 3, getHeight()-2 - 3, 5,5 );
            g2.setPaint(new GradientPaint(1,1, getBackground().darker(),1,(int)(.4*(getHeight()-3)),tranBG ));
            g2.fillRoundRect( 2, 3, getWidth()-2 -3, getHeight()-3 -3, 3 ,3 );
            
            //setting button shadow 
            g2.setPaint(new GradientPaint((int)(x-.3*w),1,tranBG,x,1,getBackground().darker()));
            g2.fillRect( 3, 3, w-(x<w/2?gap:0), getHeight()-3 - 3);
            g2.setPaint(new GradientPaint(x+w,1,getBackground().darker(),(int)(1.3*w+x),1,tranBG));
            g2.fillRect( w, 3, getWidth()-4-w, getHeight()-3 - 3);
            if(1.15*w+x>getWidth()-2)
            {
               g2.setPaint(new Color(bg.getRed()/4,bg.getGreen()/4,bg.getBlue()/4,(int)(200*((  .15*w-((getWidth()-3)-x-w)  )/(.15*w+3)))));
               g2.fillRect( w+1+gap, 2+1, getWidth()-4-gap-w, getHeight()-3 - 3);
            }
            if(x<.15*w)
            {
               g2.setPaint(new Color(bg.getRed()/4,bg.getGreen()/4,bg.getBlue()/4,(int)(200* ((.15*w-x)/(.15*w)))));
               g2.fillRect(3,3,w-gap,getHeight()-3-3);
            }
            
            //creating text
            g2.setColor( getForeground() );
            g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g2.setFont( getFont() );
            g2.drawString(trueLabel,(int)((w-trueLength+gap)/2), y+h/2+h/4 );
            g2.drawString(falseLabel,(int)(w+(w-falseLength-gap)/2), y+h/2+h/4 );
            
            //creating switch button
            g2.setPaint(black);
            g2.drawRoundRect( x, y, w-1, h-1, 5,5);
            if(isEnabled())g2.setPaint( new GradientPaint(x,y+h, new Color(100,100,100) , x,(int)(y+.25*h), new Color(240,240,240)));
            else g2.setPaint( new GradientPaint(x,y+h, new Color(100,100,100).darker() , x,(int)(y+.25*h), new Color(240,240,240).darker()));
            g2.fillRoundRect( x+1, y+1, w-2, h-2 ,5,5);
            if(isEnabled())g2.setPaint( new GradientPaint(x, (int)(y+.25*h),new Color(240,240,240) , x, y, new Color(245,245,245)) );
            else g2.setPaint( new GradientPaint(x, (int)(y+.25*h),new Color(240,240,240).darker() , x, y, new Color(245,245,245).darker()) );
            g2.fillRoundRect( x+1, y+1, w-2, (int)(.25*h)-2,5,5 );

            //drawing button divets
            if (w>14){
                int size = 10;
                g2.setColor( new Color(220,220,220,100) );
                g2.fillRect(x+w/2-size/2,y+h/2-size/2, size, size);
                if(isEnabled())g2.setPaint( new GradientPaint(1,h/2-4,new Color(120,120,120),1,h/2+2,new Color(170,170,170)));
                else g2.setPaint( new GradientPaint(1,h/2-4,new Color(120,120,120).darker(),1,h/2+2,new Color(170,170,170).darker()));
                g2.fillRect(x+w/2-4,h/2-4, 2, 8);
                g2.fillRect(x+w/2-1,h/2-4, 2, 8);
                g2.fillRect(x+w/2+2,h/2-4, 2, 8);
            }
        }
    }