import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.BasicStroke;

public class TCPServer extends JFrame {

static int [] my_array=new int [2];
static float exp_x=0, exp_y=0, num=1;
static float  res_x, res_y;

public TCPServer() {
        super("Drawing");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); setSize(320, 480);
    }

public void paint(Graphics g) {         
   Graphics2D g2 = (Graphics2D) g; 
   g2.setStroke(new BasicStroke(5.0f));    
   g2.drawLine(my_array[0], my_array[1],my_array[0], my_array[1]);  
  }

public static void main(String[] args) {

  TCPServer obj=new TCPServer();

  try{
    ServerSocket socket = new ServerSocket(1666);

      while (true) {
        Socket client = socket.accept();
        BufferedReader in=null;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));

       for(int i=0; i<2; i++) {
         my_array [i]=Integer.parseInt(in.readLine());  
         if (i==0) exp_x+=my_array[0];
         if (i==1) exp_y+=my_array[1];
         System.out.print(my_array [i]+"\t");
       } 

        System.out.print("\n"); 
        
        obj.repaint();
        obj.setVisible(true);

        res_x=exp_x/num;
        res_y=exp_y/num; num++;

        PrintStream out = new PrintStream(client.getOutputStream());
        out.println("Expected value for:\nx = " + String.format("%.3f", res_x) +"\ny = "+ String.format("%.3f", res_y));

      in.close(); client.close();
      } 
    }

  catch (Exception e) { 
    System.out.println(e); 
    } 
  }
}
