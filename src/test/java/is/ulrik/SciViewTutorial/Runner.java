package is.ulrik.SciViewTutorial;

import io.scif.SCIFIOService;
import net.imagej.ImageJService;
import org.scijava.Context;
import org.scijava.io.IOService;
import org.scijava.service.SciJavaService;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Runner for the tutorial examples.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
public class Runner {
  public static void main( final String[] args ) throws IOException {
    Context context = new Context( ImageJService.class, SciJavaService.class, SCIFIOService.class );

    // get the UIService and show the ImageJ UI
    UIService ui = context.service( UIService.class );
    if( !ui.isVisible() ) ui.showUI();

    // let's open our example dataset
    final URL fileURL = ClassLoader.getSystemClassLoader().getResource( "t1-head.tif" );
    final File file = new File( fileURL.getPath() );

    IOService io = context.service(IOService.class);
    ui.show(io.open(file.getAbsolutePath()));
  }
}
