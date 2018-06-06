package is.ulrik.SciViewTutorial;

import cleargl.GLVector;
import io.scif.SCIFIOService;
import net.imagej.ImageJService;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.scijava.Context;
import org.scijava.InstantiableException;
import org.scijava.command.Command;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Plugin;
import org.scijava.service.SciJavaService;
import org.scijava.ui.UIService;
import sc.iview.SciView;
import sc.iview.SciViewService;
import sc.iview.commands.demo.GameOfLife3D;

/**
 * Plugin that opens the currently active image in SciView, and plays it if it is a time lapse.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
@Plugin( menu = { @Menu( label = "Plugins" ),
        @Menu( label = "SciView Tutorial" ),
        @Menu( label = "Mesh Overlay" ) }, description = "Demos overlaying meshes and volumes", headless = false, type = Command.class )
public class OverlayPlugin< T extends RealType< T > & NativeType< T >> implements Command {

  private SciViewService sciViewService;
  private SciView sciView;

  /**
   * Runs the HelloWorldPlugin.
   */
  @Override
  public void run() {
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


    try {
      CommandService cmds = context.service(CommandService.class);
      CommandInfo cmdInfo = cmds.getCommand(sc.iview.commands.demo.GameOfLife3D.class);
      GameOfLife3D gol = (GameOfLife3D)cmdInfo.createInstance();

      gol.setContext(context);
      gol.setInput("sciView", sciView);
      gol.run();

      gol.getVolume().setPosition(new GLVector(0.0f, 0.0f, 0.0f));
      gol.getVolume().setScale(new GLVector(1.0f, 1.0f, 1.0f));

    } catch (InstantiableException e) {
      e.printStackTrace();
    }
  }
}

