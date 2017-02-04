import javax.swing.JOptionPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

class Mancala
{
   JFrame fr = new JFrame();
   JButton[] cup = new JButton[14];
   int[] cupQty = new int[14];
   GridBagConstraints gbc = new GridBagConstraints();
   boolean player1sTurn = true;
   
   public static void main(String[]args)
   {
      new Mancala();
   }
   
   public Mancala()
   {
      for(int i = 0; i < 14; i++)
      {
         cup[i] = new JButton();
      }
      fr.setDefaultCloseOperation(fr.EXIT_ON_CLOSE);
      fr.setSize(640,160);
      WindowPositioner.setLocation(fr,WindowPositioner.CENTER,true);
      fr.setVisible(true);
      fr.setLayout(new GridBagLayout());
      fr.setTitle("Mancala");
      
      gbc.gridheight = 1;
      gbc.fill = gbc.BOTH;
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.gridx = 1;
      gbc.gridy = 1;
      fr.add(cup[0],gbc);
      
      gbc.gridx++;
      fr.add(cup[1],gbc);
      
      gbc.gridx++;
      fr.add(cup[2],gbc);
      
      gbc.gridx++;
      fr.add(cup[3],gbc);
      
      gbc.gridx++;
      fr.add(cup[4],gbc);
      
      gbc.gridx++;
      fr.add(cup[5],gbc);
      
      gbc.gridx++;
      gbc.gridy = 0;
      gbc.gridheight = 2;
      fr.add(cup[6],gbc);
      
      gbc.gridx--;
      gbc.gridheight = 1;
      fr.add(cup[7],gbc);
      
      gbc.gridx--;
      fr.add(cup[8],gbc);
      
      gbc.gridx--;
      fr.add(cup[9],gbc);
      
      gbc.gridx--;
      fr.add(cup[10],gbc);
      
      gbc.gridx--;
      fr.add(cup[11],gbc);
      
      gbc.gridx--;
      fr.add(cup[12],gbc);
      
      gbc.gridx--;
      gbc.gridheight = 2;
      fr.add(cup[13],gbc);
      
      cup[6].setEnabled(false);
      cup[13].setEnabled(false);
      for(int i = 0; i < 6; i++)
      {
         cupQty[i] = 4;
      }
      for(int i = 7; i < 13; i++)
      {
         cupQty[i] = 4;
      }
      
      for(int i = 0; i < 14; i++)
      {
         final int btnIndex = i;
         cup[i].addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               move(btnIndex);
            }
         });
      }
      
      updateBoard();
   }
   
   public void updateBoard()
   {
      for(int i = 0; i < 14; i++)
      {
         cup[i].setText(new Integer(cupQty[i]).toString());
         if(player1sTurn)fr.setTitle("Mancala - Player 1's Turn");
         else fr.setTitle("Mancala - Player 2's Turn");
      }
      
      int totalQty = 0;
      for(int i = 0; i < 6; i++)
      {
         totalQty += cupQty[i];
         totalQty += cupQty[i+7];
      }
      if(totalQty == 0)
      {
         if(cupQty[6]>cupQty[13]) JOptionPane.showMessageDialog(fr, "Player 1 is the winner!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
         else if(cupQty[13]>cupQty[6])JOptionPane.showMessageDialog(fr, "Player 2 is the winner!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
         else if(cupQty[13] == cupQty[6])JOptionPane.showMessageDialog(fr, "Player 1 and Player 2 tied!", "Tied Game!", JOptionPane.INFORMATION_MESSAGE);
      }
   }
   
   public void move(int btnIndex)
   {
      int qty = cupQty[btnIndex];
      
      //if not your turn, beep and exit method
      if((player1sTurn&&btnIndex>6)
         || (!player1sTurn&&btnIndex<7))
         {
            Toolkit.getDefaultToolkit().beep();
            return;
         }
      
      //if you clicked a button with 0 stones, exit method
      if(qty == 0)
      {
         return;
      }
      
      int finalCupIndex = btnIndex;
      cupQty[btnIndex] = 0;
      
      //drop off stones around the board until you run out
      for(int i = 0; i < qty; i++)
      {
         int q = btnIndex+1+i;
         if(q>13)q-=14;
         if(!(q == 6 && !player1sTurn)
            &&!(q == 13 && player1sTurn))cupQty[q]++;
         else qty++;
         finalCupIndex = q;
      }
      
      //if the cup you drop your last stone in is not a mancala...
      if(cup[finalCupIndex].isEnabled())
      {
         //and you landed in a cup with 0 stones in it...
         if(cupQty[finalCupIndex] == 1)
         {
            //if it's player 1's turn and the stone was dropped on player 1's side, and adjacent cup has stones in it, capture them
            if(player1sTurn&&finalCupIndex<7&&cupQty[12-finalCupIndex]>0)
            {
               cupQty[6] += cupQty[12-finalCupIndex]+1;
               cupQty[12-finalCupIndex] = 0;
               cupQty[finalCupIndex] = 0;
            }
            
            //if it's player 2's turn and the stone was dropped on player 2's side, and adjacent cup has stones in it, capture them
            if(!player1sTurn&&finalCupIndex>6&&cupQty[(finalCupIndex-12)*-1]>0)
            {
               cupQty[13] += cupQty[(finalCupIndex-12)*-1]+1;
               cupQty[(finalCupIndex-12)*-1] = 0;
               cupQty[finalCupIndex] = 0;
            }
         }
         
         //if opponent's side of board is NOT empty, change turns
         int totalQty = 0;
         for(int i = 0; i < 6; i++)
         {
            totalQty += cupQty[i+(player1sTurn?7:0)];
         }
         if(totalQty > 0)
         {
            player1sTurn = !player1sTurn;
            Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
            if (runnable != null) runnable.run();
         }
      }
      //if the last stone you dropped was in your own mancala
      else 
      {
         //if player's side of board IS empty, change turns
         int totalQty = 0;
         for(int i = 0; i < 6; i++)
         {
            totalQty += cupQty[i+(player1sTurn?0:7)];
         }
         if(totalQty == 0)
         {
            player1sTurn = !player1sTurn;
            Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
            if (runnable != null) runnable.run();
         }
         //otherwise do nothing, and it stays the current player's turn
         //...
      }
      
      updateBoard();
   }
}