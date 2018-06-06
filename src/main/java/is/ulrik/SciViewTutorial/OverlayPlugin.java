package is.ulrik.SciViewTutorial;

import cleargl.GLVector;
import graphics.scenery.Node;
import net.imagej.mesh.Mesh;
import net.imagej.ops.OpService;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.scijava.InstantiableException;
import org.scijava.command.Command;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import sc.iview.SciView;
import sc.iview.commands.demo.GameOfLife3D;

/**
 * Plugin that runs the game of life demo, and adds marching cube'd meshes of the volume data.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
@Plugin( menu = { @Menu( label = "Plugins" ),
        @Menu( label = "SciView Tutorial" ),
        @Menu( label = "Mesh Overlay" ) }, description = "Demos overlaying meshes and volumes", headless = false, type = Command.class )
public class OverlayPlugin< T extends RealType< T > & NativeType< T >> implements Command {

  // the following parameters automatically connect the services we need
  // ImageJ2 ftw!
  @Parameter
  private SciView sciView;

  @Parameter
  private OpService ops;

  @Parameter
  private UIService ui;

  @Parameter
  private CommandService cmds;

  /**
   * Runs the HelloWorldPlugin.
   */
  @Override
  public void run() {
    if( !ui.isVisible() ) ui.showUI();

    // camera setup
    sciView.getCamera().setPosition( new GLVector( 10.0f, 0.0f, 15.0f ) );
    sciView.getCamera().setTargeted( true );
    sciView.getCamera().setTarget( new GLVector( 0, 0, 0 ) );
    sciView.getCamera().setDirty( true );
    sciView.getCamera().setNeedsUpdate( true );

    try {
      // let's try to get a reference to the GameOfLife3D demo
      CommandInfo cmdInfo = cmds.getCommand(sc.iview.commands.demo.GameOfLife3D.class);
      GameOfLife3D gol = (GameOfLife3D)cmdInfo.createInstance();

      // and run that demo programmatically. we need to pass it a reference to
      // our SciView instance, and hand over the current context
      gol.setContext(ui.context());
      gol.setInput("sciView", sciView);
      gol.run();

      int i = 0;
      while(i < 20) {
        // first, we threshold the image
        Img<BitType> bitImg = (Img<BitType>) ops.threshold().apply(gol.getImg(), new UnsignedByteType(128));
        // and then create a mesh from the binary image, via the marching cubes algorithm
        Mesh mesh = ops.geom().marchingCubes(bitImg);
        // finally, we add the meshed volume to our scene
        Node n = sciView.addMesh(mesh);

        // sleep for a bit so that the changes are not too fast
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // let's hide the old mesh
        n.setVisible(false);
        // and iterate the game of life another round
        gol.iterate();
        i++;
      }

    } catch (InstantiableException e) {
      e.printStackTrace();
    }
  }
}

