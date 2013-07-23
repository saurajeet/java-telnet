import org.apache.commons.net.telnet.*;
import org.apache.commons.net.io.Util;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
public class MainFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TelnetClient telnet;
		  
		      telnet = new TelnetClient();
		  
		          try
		 	          {
		             telnet.connect("rainmaker.wunderground.com", 3000);
		          }
		         catch (IOException e)
		            {
		    
		        	 e.printStackTrace();
		             System.exit(1);
		         }
		  
		         IOUtil.readWrite(telnet.getInputStream(), telnet.getOutputStream(),
		                           System.in, System.out);
		  
		          try
		  	          {
		              telnet.disconnect();
		          }
		          catch (IOException e)
		  	          {
		             e.printStackTrace();
		             System.exit(1);
		          }
		  
		          System.exit(0);
	}

}

class IOUtil
{

    public static final void readWrite(final InputStream remoteInput,
                                       final OutputStream remoteOutput,
                                       final InputStream localInput,
                                       final OutputStream localOutput)
    {
        Thread reader, writer;

        reader = new Thread()
                 {
                     public void run()
                     {
                         int ch;

                         try
                         {
                             while (!interrupted() && (ch = localInput.read()) != -1)
                             {
                                 remoteOutput.write(ch);
                                 remoteOutput.flush();
                             }
                         }
                         catch (IOException e)
                         {
                             //e.printStackTrace();
                         }
                     }
                 }
                 ;


        writer = new Thread()
                 {
                     public void run()
                     {
                         try
                         {
                             Util.copyStream(remoteInput, localOutput);
                         }
                         catch (IOException e)
                         {
                             e.printStackTrace();
                             System.exit(1);
                         }
                     }
                 };


        writer.setPriority(Thread.currentThread().getPriority() + 1);

        writer.start();
        reader.setDaemon(true);
        reader.start();

        try
        {
            writer.join();
            reader.interrupt();
        }
        catch (InterruptedException e)
        {
        }
    }

}
