package org.dei.perla.app;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import org.dei.perla.core.PerLaSystem;
import org.dei.perla.core.Plugin;
import org.dei.perla.core.channel.http.HttpChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorMapperFactory;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.FpcCreationException;
import org.dei.perla.core.message.json.JsonMapperFactory;
import org.dei.perla.core.message.urlencoded.UrlEncodedMapperFactory;


/**
 * Hello world!
 *
 */
public class App 
{
	private static final String descPath ="simulator.xml";
	private static final String descPath1 ="simulator2.xml";
	private static final String descPath2 ="fpc_descriptor.xml";
	private static final String descPath3 ="fpc_descriptor2.xml";
	private static final String descPath4 ="example.xml";
	private static final String descPath5 ="slope_sensor.xml";
    private static final List<Plugin> plugins;
    static {
        List<Plugin> ps = new ArrayList<>();
        ps.add(new JsonMapperFactory());
        ps.add(new UrlEncodedMapperFactory());
        ps.add(new SimulatorMapperFactory());
        ps.add(new HttpChannelPlugin());
        ps.add(new SimulatorChannelPlugin());
        plugins = Collections.unmodifiableList(ps);
    }
    public final static String cdtString = new String(
    		"CREATE CONCEPT Icy WHEN temperature: < -5 "+
    		"AND snow_density > 0.30 "+
    		"EVALUATED ON 'EVERY 30 m SELECT temperature, snow_density "+
    		"SAMPLING EVERY 10 m "+
    		"EXECUTE IF EXISTS (temperature) OR EXISTS (snow_density)' "+
    		"CREATE CONCEPT Soft WHEN temperature BETWEEN -5 AND 6 "+
    		"AND snow_density < 0.12 "+
    		"EVALUATED ON 'EVERY 30 m SELECT temperature, snow_density "+
    		"SAMPLING EVERY 10 m "+
    		"EXECUTE IF EXISTS (temperature) OR EXISTS (snow_density)' "+
    		"CREATE CONCEPT Slushy WHEN temperature > 8 "+
    		"AND snow_density BETWEEN 0.12 AND 0.29 "+
    		"EVALUATED ON 'EVERY 3 0m SELECT temperature, snow_density "+
    		"SAMPLING EVERY 10 mh "+
    		"EXECUTE IF EXISTS (temperature) OR EXISTS (snow_density)' "+
    		"CREATE CONCEPT LOW WHEN snow_depth < 10 cm "+
    		"EVALUATED ON 'EVERY ONE SELECT snow_depth "+
    		"SAMPLING EVERY 1 d EXECUTE IF EXISTS (snow_dept)' "+
    		
    		"CREATE CONCEPT Rain WHEN rainfall_level > 5ml "+
    		"EVALUATED ON 'EVERY 30 m SELECT rainfall_level "+
    		"SAMPLING IF (rainfall_level < 2 ml) EVERY 30 m "+
    		"ELSE every 10 m "+
    		"REFRESH EVERY 30 m EXECUTE IF EXISTS(rainfall_level)' "+
    		"CREATE CONCEPT Snow WHEN snowfall_level > 5 ml "+
    		"EVALUATED ON 'EVERY 30 m SELECT snowfall_level "+
    		"SAMPLING IF (snowfall_level < 2 ml) EVERY 30 m "+
    		"ELSE every 10 m "+
    		"REFRESH EVERY 30 m EXECUTE IF EXISTS(snowfall_level)' "+
    		"CREATE CONCEPT Fog WHEN fog > 0.5 "+
    		"EVALUATED ON 'EVERY 30 m SELECT fog "+
    		"SAMPLING IF (fog > 20 meters) EVERY 30 m ELSE every 10 m "+
    		"REFRESH EVERY 30 m EXECUTE IF EXISTS(fog)'. "
    		);
	



    private static PerLaSystem system;
    private static QueryMenager qm;
  //  private static Executor ex;system.injectDescriptor(new FileInputStream(descPath));

    
    public static void main( String[] args ) 
    {


        system= new PerLaSystem(plugins);
        qm = new QueryMenager(system);

        int i;

        Random r;
        int v;
        r = new Random();
        //for per creare gli fpc
       // for(i=0;i<10;i++){
      //  fpc = new SimulatorFpc(values,0);
        //SimulatorFpc fpc1 = new SimulatorFpc(values,1);
        
        //for per inserire valori casuali
      /* 	v=r.nextInt(40);
        	System.out.println(v);
        	fpc.setValue(CommonAttributes.TEMP_INT, 20);
        	fpc1.setValue(CommonAttributes.TEMP_INT, 100);
      */
       /* try {
			system.getRegistry().add(fpc);
			system.getRegistry().add(fpc1);
		} catch (DuplicateDeviceIDException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        */
    //    }


			try {
				system.injectDescriptor(new FileInputStream(descPath5));
			} catch (FileNotFoundException | FpcCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		//	system.injectDescriptor(new FileInputStream(descPath));
			//system.injectDescriptor(new FileInputStream(descPath1));
			//system.injectDescriptor(new FileInputStream(descPath1));
		//system.injectDescriptor(new FileInputStream(descPath3));
			

     
     
       
        for(Fpc f : system.getRegistry().getAll()) {
        	System.out.println("id:  "+f.getId()+"\n");
        	for(Attribute a: f.getAttributes())
        		System.out.println("id:  "+a.getId()+" type: "+a.getType()+"\n");
        }
        /*     qm.addQuery("every one " +
                "select temp_c:float " +
                "sampling every 500 milliseconds " +
                "terminate after 1 minutes" );*/

      qm.addQuery("every 500 seconds " +
                "select avg( temperature:float , 4 samples ), avg( cacca:float, 30 seconds) " +             
                "sampling every 1 seconds  "+
                "execute if position = 'pista1' "
    		  );
      /*     qm.addQuery("every 1 samples " +
              "select temp_c:float " +             
              "sampling every 500 milliseconds "     		  
  		  );
      qm.addQuery("SET period = 10 ON 0");
        qm.addQuery("every one " +
              "select sum ( temp_c:float , 2 seconds ) " +
              "sampling every 500 milliseconds " +
              "terminate after 1 minutes" );
      qm.addQuery("every one " +
                "select avg ( temperature:integer , 1 minutes ) " +
                "sampling every 10 seconds " +
                "terminate after 10 selections" );
        qm.addQuery("every one " +
                 "select temp_c:float " +
                 "sampling every 10 milliseconds " +
                 "terminate after 50 selections" ); 
      */
       /*qm.addQuery("every one " +
               "select period:integer, temp_c:float " +
               "sampling every 10 milliseconds " +
               "terminate after 50 selections" );
     
       /*    qm.addQuery("every one " +
               "select avg( temp_c:float , 1 seconds )" +
               "sampling every 10 milliseconds " +
               "terminate after 50 selections" );
       qm.addQuery("every one " +
               "select max( temp_c:float , 1 seconds ) " +
               "sampling every 10 milliseconds " +
               "terminate after 50 selections" );
            qm.addQuery("every one " +
               "select count( * , 10 seconds ) " +
               "sampling every 10 milliseconds " +
               "terminate after 50 selections" );
       System.out.println("start");
       	qm.addQuery("every one " +
           "select min ( temperature:integer , 1 minutes ) " +
           "sampling every 10 seconds " +
           "terminate after 10 selections" );
       qm.addQuery("every one " +
                 "select min ( temperature:integer , 1 minutes ) " +
                 "sampling every 10 seconds " +
                 "terminate after 10 selections" );
         qm.addQuery("every one " +
                 "select sum ( temperature:integer , 1 minutes ) " +
                 "sampling every 10 seconds " +
                 "terminate after 10 selections" );
         Enviroment en = new Enviroment(fpc,20);
         en.run();
         Enviroment en1 = new Enviroment(fpc1,100);
         en1.run();
              qm.addQuery("every one " +
                "select count ( * , 1 minutes ) " +
                "sampling every 10 seconds " +
                "terminate after 2 selections" );
        qm.addQuery("every one " +
                "select sum ( temperature:integer , 1 minutes ) " +
                "sampling every 10 seconds " +
                "terminate after 2 selections" );
      qm.addQuery("every 30 seconds " +
                "select temperature:integer, humidity:integer " +
                "sampling every 10 milliseconds " +
                "terminate after 10 selections" );
        qm.addQuery("every 1 seconds " +
                "select temperature:integer ) " +
                "sampling every 10 milliseconds " +
                "terminate after 10 selections" );
        qm.addQuery("every 2 seconds " +
                "select humidity:integer " +
                "sampling every 10 milliseconds " +
                "terminate after 10 selections" );*/


    }
    
    
}


