package org.dei.perla.app;
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
	private static final String descPath =
            "fpc_descriptor.xml";
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
    
    private static PerLaSystem system;
    private static QueryMenager qm;
    private static Executor ex;
    
    
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
        for(i=0;i<10;i++){
        SimulatorFpc fpc = new SimulatorFpc(values,i);
        //for per inserire valori casuali
        	v=r.nextInt(40);
        	System.out.println(v);
        	fpc.setValue(CommonAttributes.TEMP_INT, v);
      
        try {
			system.getRegistry().add(fpc);
		} catch (DuplicateDeviceIDException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        }
   /*     try {
			system.injectDescriptor(new FileInputStream(descPath));
			
		} catch (FileNotFoundException | FpcCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
       
        for(Attribute a : system.getRegistry().get(0).getAttributes())
        System.out.println("id: "+a.getId()+" type:"+a.getType()+"\n");
        qm.addQuery("every one " +
                "select temperature:integer " +
                "sampling every 10 seconds " +
                "terminate after 1 minutes" );
        qm.addQuery("every one " +
                "select avg ( temperature:integer , 1 minutes ) " +
                "sampling every 10 seconds " +
                "terminate after 2 selections" );
        /*        qm.addQuery("every one " +
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
        System.out.println("query added");
        
        byte[] c = new byte[10];
        try {
			System.in.read(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
