package com.sockets.sockets_example;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

// ���������� ����� ���������� ��� ��������� Android
public class MainActivity extends Activity  {

int a,b; 			// ���������� ��� �������� ������ (��������� ������� ���������� ����������) 
String buf, res;	// ���������� ��� �������� ������, ���������� �� ������� (��)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);			    //����� ����������� � ������ �������
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //�������� ������ Activity ��� ���������

		/* ���������� �� ������ ���������� ������� ��� ��������� (������ ������ �� �������� ��������� ����� MySurfaceView): */
        setContentView(new MySurfaceView(this));        
    }

/* ����� ��� ��������� �� ������:
 SurfaceView � ������� ������ ������ SurfaceHolder, ������� � ���� ������� ������ 
 �������� ������ Surface, ������������� ��� ���������� ����������� �� ������� �������.
*/
 
    class MySurfaceView extends SurfaceView {
   	
        Path path;
        SurfaceHolder surfaceHolder;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);  // �������� ����� � ������� ����� ���������: ����������� ���������
        
        public MySurfaceView(Context context) {
            super(context);
            surfaceHolder = getHolder(); // ���������� ������ SurfaceHolder ��� ���������� SurfaceView
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);                     // ������� ����� ��� ���������
            paint.setColor(Color.WHITE);                 // ���� �����     
        }

/* ����� ��� ��������� ������� �������. ������ MotionEvent, ���������� � �������� ���������
��������� ���������� ��� �������(getAction) � ���������� (getX � getY - � � y ����� ��������������).
��� �������� � ������� ���������� ����������� ��� �������:
- ������� (����� ����������� � ������) - ACTION_UP
- �������� (����� �������� �� ������) - ACTION_MOVE
- ���������� (����� ��������� �� ������) - ACTION_DOWN
 */
      @Override
        public boolean onTouchEvent(MotionEvent event) {
        	       	
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                path = new Path();                        /* ����� ������� � ������ - �� ����������� ������������ �����, � ����� ������ (�����) */
                path.moveTo(event.getX(), event.getY());  /* ����������� �� ��������� ������� � ����� � ������������, ������������� �������� getX, getY */
            } 
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                path.lineTo(event.getX(), event.getY());  /* ��������� ����� �� ������� ������� �� �������� ����� */ 
            } 
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                path.lineTo(event.getX(), event.getY());   
            }

            if (path != null) { 
                Canvas canvas = surfaceHolder.lockCanvas(); // ��������� ������� Canvas - �������� ����������� ��� ���������
                canvas.drawPath(path, paint);               // ��������� ���������, ����������� � path �� �������, ����� ������� ������� �����������
                surfaceHolder.unlockCanvasAndPost(canvas);  // ��������� ��������� - ����� ����������
                
                a=(int)event.getX(); b=(int)event.getY();   // ���������� ��������� ������� ��� �������� �� ������
    		    try{
    		    	InetAddress serverAddr = InetAddress.getByName("localhost"); // ��������� IP-������ �������
    		    	Socket socket = new Socket(serverAddr,1666);				 // �������� ����������� ������ ��� ������ ������� � �������� ����� ���� 1666
    		        PrintStream out = new PrintStream(socket.getOutputStream()); // �������� ������ ������ ��� �������� ���������� a � b �� ������
    		        out.println(a); out.println(b);                              // ����� � ����� a � b
    		        
    				DataInputStream dt = new DataInputStream(socket.getInputStream());  // �������� ������ ����� ��� ��������� �������� ��������������� �������� ��� ��������� x � y
    		        buf=null; res="";
    		        while ((buf=dt.readLine())!=null) res+=buf+"\n";    		        
    	            dt.close(); out.close(); socket.close();// �������� ������� ������ ������� � ��������
    		        }
    		     catch (Exception e) {System.out.println(e);}// ����� ���������� ��� ������������� ��������
            }
            
            return true;
    }
  }
 
// ����� ��� �������� ����
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); /*����� � �������� ������� ��������� ��������� ������, �������������� �������������
        �������� ���� � xml, � ��������� �� ������ menu, ���������� � �������� ������� ���������*/
        return true;
    }

// ����� ��� �������� ������� ����
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.Exit:         /*Exit - �������� ��������� ��������� exit, ����������� � ����� strings.xml*/
			finish();           //���������� ������ ����������
		break;
		case R.id.Expectation:  // ����� ��������������� �������� ���� �������� x � y
			AlertDialog.Builder aDb1 = new AlertDialog.Builder(MainActivity.this);
			aDb1.setIcon(R.drawable.ic_launcher);  // ������
	    	aDb1.setTitle("Expectation"); 		   // ���������
	  	  	aDb1.setMessage(res);                  // ��������� ���������� � ����  
	  	  	aDb1.setCancelable(false);	           // ���� ������� �� ����� ���� ������� ����������� "Back"
	  	  /* �������� ������ "��" � ���������� ����: */
			aDb1.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{		
			  @Override
		      public void onClick(DialogInterface dialog, int which) {} //��� ������� �� "OK" - ������� ����
			});
		    AlertDialog aD = aDb1.create(); aD.show(); // �������� � ����������� ���� ������� � ���������� ������������
			
		break;
        }
        return true;
    }
}

