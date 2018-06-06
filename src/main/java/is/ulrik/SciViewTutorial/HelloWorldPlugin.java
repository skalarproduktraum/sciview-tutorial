package is.ulrik.SciViewTutorial;

import cleargl.GLVector;
import graphics.scenery.Node;
import io.scif.SCIFIOService;
import net.imagej.ImageJService;
import net.imagej.ImgPlus;
import net.imagej.display.DatasetView;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.SciJavaService;
import org.scijava.ui.UIService;
import sc.iview.SciView;
import sc.iview.SciViewService;

/**
 * Plugin that opens the currently active image in SciView.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
@Plugin( menu = { @Menu( label = "Plugins" ),
        @Menu( label = "SciView Tutorial" ),
        @Menu( label = "Hello World" ) }, description = "Hello SciView.", headless = false, type = Command.class )
public class HelloWorldPlugin< T extends RealType< T > & NativeType< T >> implements Command {

  @Parameter( label = "3D ImgPlus to be shown." )
  private DatasetView datasetView;

  private ImgPlus< T > imgPlus;
  private SciViewService sciViewService;
  private SciView sciView;

  /**
   * Runs the HelloWorldPlugin.
   */
  @Override
  public void run() {
    imgPlus = (ImgPlus< T >) datasetView.getData().getImgPlus();

    Context context = new Context( ImageJService.class, SciJavaService.class, SCIFIOService.class );

    UIService ui = context.service( UIService.class );
    if( !ui.isVisible() ) ui.showUI();

    sciViewService = context.service( SciViewService.class );
    sciView = sciViewService.getOrCreateActiveSciView();

    sciView.getCamera().setPosition( new GLVector( 0.0f, 0.0f, 5.0f ) );
    sciView.getCamera().setTargeted( true );
    sciView.getCamera().setTarget( new GLVector( 0, 0, 0 ) );
    sciView.getCamera().setDirty( true );
    sciView.getCamera().setNeedsUpdate( true );

    Node v = sciView.addVolume(datasetView.getData());
    v.setPosition(new GLVector(0.0f, 0.0f, 0.0f));
    v.setScale(new GLVector(1.0f, 1.0f, 1.0f));
  }
}
