package org.dei.perla.app;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.dei.perla.core.*;
import org.dei.perla.core.channel.http.HttpChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorMapperFactory;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.FpcCreationException;
import org.dei.perla.core.message.json.JsonMapperFactory;
import org.dei.perla.core.message.urlencoded.UrlEncodedMapperFactory;
import org.dei.perla.core.registry.DuplicateDeviceIDException;
import org.dei.perla.lang.*;
import org.dei.perla.lang.executor.SimulatorFpc;
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
    


    private static Map<Attribute, Object> createDefaultValues() {
        Map<Attribute, Object> values = new HashMap<>();
        values.put(CommonAttributes.TEMP_INT, 24);
        values.put(CommonAttributes.HUM_INT, 12);
        values.put(Attribute.TIMESTAMP, Instant.now());

        return values;
    }
    private static  SimulatorFpc fpc;
    private static PerLaSystem system;
    private static QueryMenager qm;
  //  private static Executor ex;system.injectDescriptor(new FileInputStream(descPath));
    
    
    public static void main( String[] args )
    {
        system= new PerLaSystem(plugins);
        qm = new QueryMenager(system);
        Map<Attribute, Object> values = createDefaultValues();
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
			system.injectDescriptor(new FileInputStream(descPath4));
			system.injectDescriptor(new FileInputStream(descPath4));
		//	system.injectDescriptor(new FileInputStream(descPath));
			//system.injectDescriptor(new FileInputStream(descPath1));
			//system.injectDescriptor(new FileInputStream(descPath1));
		//system.injectDescriptor(new FileInputStream(descPath3));
			
		} catch (FileNotFoundException | FpcCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
     
     
       
        for(Fpc f : system.getRegistry().getAll()) {
        	System.out.println("id: "+f.getId()+"\n");
        	for(Attribute a: f.getAttributes())
        		System.out.println("id: "+a.getId()+" type:"+a.getType()+"\n");
        }
        /*     qm.addQuery("every one " +
                "select temp_c:float " +
                "sampling every 500 milliseconds " +
                "terminate after 1 minutes" );*/
      qm.addQuery("every 3 samples " +
                "select sum( temp_c:float , 4 seconds ) " +             
                "sampling every 1 seconds "     		  
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


