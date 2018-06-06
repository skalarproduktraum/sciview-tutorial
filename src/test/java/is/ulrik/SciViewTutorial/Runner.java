package is.ulrik.SciViewTutorial;

import io.scif.SCIFIOService;
import net.imagej.ImageJService;
import org.scijava.Context;
import org.scijava.service.SciJavaService;
import org.scijava.ui.UIService;

/**
 * Runner for the tutorial examples.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
public class Runner {
  public static void main( final String[] args ) {
    Context context = new Context( ImageJService.class, SciJavaService.class, SCIFIOService.class );

    UIService ui = context.service( UIService.class );
    if( !ui.isVisible() ) ui.showUI();
  }
}
