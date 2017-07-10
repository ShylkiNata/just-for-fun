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

// Клиентская часть приложения для платформы Android
public class MainActivity extends Activity  {

int a,b; 			// Переменные для передачи данных (координат касания серверному приложению) 
String buf, res;	// Переменные для хранения данных, полученных от сервера (ПК)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);			    //Вызов суперкласса в первую очередь
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //Создание экрана Activity без заголовка

		/* Размещение на экране активности полотна для рисования (вместо ссылки на разметку передаётся класс MySurfaceView): */
        setContentView(new MySurfaceView(this));        
    }

/* Класс для рисования на экране:
 SurfaceView — обертка вокруг класса SurfaceHolder, который в свою очередь служит 
 оберткой класса Surface, используемого для обновления изображения из фоновых потоков.
*/
 
    class MySurfaceView extends SurfaceView {
   	
        Path path;
        SurfaceHolder surfaceHolder;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);  // Создание кисти и задание стиля рисования: сглаживание геометрии
        
        public MySurfaceView(Context context) {
            super(context);
            surfaceHolder = getHolder(); // возвращает объект SurfaceHolder для управления SurfaceView
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);                     // Толщина кисти для рисования
            paint.setColor(Color.WHITE);                 // Цвет кисти     
        }

/* Метод для обработки события касания. Объект MotionEvent, переданный в качестве параметра
позволяет определить тип события(getAction) и координаты (getX и getY - х и y точки соответственно).
При контакте с экраном устройства проверяется вид события:
- нажатие (палец прикоснулся к экрану) - ACTION_UP
- движение (палец движется по экрану) - ACTION_MOVE
- отпускание (палец оторвался от экрана) - ACTION_DOWN
 */
      @Override
        public boolean onTouchEvent(MotionEvent event) {
        	       	
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                path = new Path();                        /* Новое касание к экрану - не продолжение нарисованной линии, а новый объект (линия) */
                path.moveTo(event.getX(), event.getY());  /* Перемещение по плоскости полотна в точку с координатами, возвращаемыми методами getX, getY */
            } 
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                path.lineTo(event.getX(), event.getY());  /* Рисование линии от текущей позиции до заданной точки */ 
            } 
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                path.lineTo(event.getX(), event.getY());   
            }

            if (path != null) { 
                Canvas canvas = surfaceHolder.lockCanvas(); // Получение объекта Canvas - создание поверхности для рисования
                canvas.drawPath(path, paint);               // Отрисовка геометрии, накопленной в path до момента, когда событие касания завершилось
                surfaceHolder.unlockCanvasAndPost(canvas);  // Отрисовка выполнена - вывод результата
                
                a=(int)event.getX(); b=(int)event.getY();   // Сохранение координат касания для передачи на сервер
    		    try{
    		    	InetAddress serverAddr = InetAddress.getByName("localhost"); // Получение IP-адреса сервера
    		    	Socket socket = new Socket(serverAddr,1666);				 // Создание клиентского сокета для обмена данными с сервером через порт 1666
    		        PrintStream out = new PrintStream(socket.getOutputStream()); // Создание потока вывода для передачи переменных a и b на сервер
    		        out.println(a); out.println(b);                              // Вывод в поток a и b
    		        
    				DataInputStream dt = new DataInputStream(socket.getInputStream());  // Создание потока ввода для получение величины математического ожидания для координат x и y
    		        buf=null; res="";
    		        while ((buf=dt.readLine())!=null) res+=buf+"\n";    		        
    	            dt.close(); out.close(); socket.close();// закрытие потоков обмена данными с сервером
    		        }
    		     catch (Exception e) {System.out.println(e);}// Вывод исключения при возникновении такового
            }
            
            return true;
    }
  }
 
// Метод для создания меню
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); /*Метод в качестве первого параметра принимает ресурс, представляющий декларативное
        описание меню в xml, и наполняет им объект menu, переданный в качестве второго параметра*/
        return true;
    }

// Метод для создания пунктов меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.Exit:         /*Exit - значение строковой константы exit, определённой в файле strings.xml*/
			finish();           //Завершение работы приложения
		break;
		case R.id.Expectation:  // Вывод математического ожидания ряда значений x и y
			AlertDialog.Builder aDb1 = new AlertDialog.Builder(MainActivity.this);
			aDb1.setIcon(R.drawable.ic_launcher);  // Иконка
	    	aDb1.setTitle("Expectation"); 		   // Заголовок
	  	  	aDb1.setMessage(res);                  // Текстовое содержимое в окне  
	  	  	aDb1.setCancelable(false);	           // Окно диалога не может быть закрыто посредством "Back"
	  	  /* Создание кнопки "ОК" в диалоговом окне: */
			aDb1.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{		
			  @Override
		      public void onClick(DialogInterface dialog, int which) {} //При нажатии на "OK" - закрыть окно
			});
		    AlertDialog aD = aDb1.create(); aD.show(); // Создание и отображение окна диалога с указанными компонентами
			
		break;
        }
        return true;
    }
}

