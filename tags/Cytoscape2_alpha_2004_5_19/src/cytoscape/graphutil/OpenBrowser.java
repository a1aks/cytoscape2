//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------

package cytoscape.graphutil;

import java.io.File;
import java.io.IOException;

public abstract class OpenBrowser {

  static String UNIX_PROTOCOL = "file:";
  static String UNIX_PATH = "gnome-moz-remote";
  static String UNIX_FLAG = "-remote openURL";

  static String WINDOWS_PATH = "cmd.exe /c start";
  static String MAC_PATH = "open";

  public static void openURL ( String url ) {

    String osName = System.getProperty("os.name" );
    
    try {
      String cmd;
      if ( osName.startsWith("Windows") ) {
        cmd =  WINDOWS_PATH + " " + url;
      } else if ( osName.startsWith("Mac" ) ) {
        cmd = MAC_PATH + " " + url;
      } else {
        //cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
        cmd = UNIX_PATH + " " + url;
      }
      // System.out.println("cmd=" + cmd);
      Process p = Runtime.getRuntime().exec(cmd);
      try {
        int exitCode = p.waitFor();
        if (exitCode != 0) {
          System.out.println("cmd failed, start new browser");
          cmd = UNIX_PATH + " "  + url;
          p = Runtime.getRuntime().exec(cmd);
        }
      } catch(InterruptedException ex) { }
    } catch(IOException ioe) { }
  }
}
