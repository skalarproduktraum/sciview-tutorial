package is.ulrik.SciViewTutorial;

import cleargl.GLVector;
import graphics.scenery.Node;
import graphics.scenery.volumes.TransferFunction;
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
@Plugin( menuPath = "Plugins>SciView Tutorial>Mesh Overlay", description = "Demos overlaying meshes and volumes", headless = false, type = Command.class )
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
    sciView.getCamera().setPosition( new GLVector( 0.0f, 1.0f, 1.0f ) );
    sciView.getCamera().setTargeted( true );
    sciView.getCamera().setTarget( new GLVector( 0, 0, 0 ) );

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      // let's try to get a reference to the GameOfLife3D demo
      final CommandInfo cmdInfo = cmds.getCommand(sc.iview.commands.demo.GameOfLife3D.class);
      final GameOfLife3D gol = (GameOfLife3D)cmdInfo.createInstance();

      // and run that demo programmatically. we need to pass it a reference to
      // our SciView instance, and hand over the current context
      gol.setContext(ui.context());
      gol.setInput("sciView", sciView);
      gol.run();

      int i = 0;
      while(i < 20) {
        // first, we threshold the image
        final Img<BitType> bitImg = (Img<BitType>) ops.threshold().apply(gol.getImg(), new UnsignedByteType(64));
        // and then create a mesh from the binary image, via the marching cubes algorithm
        final Mesh mesh = ops.geom().marchingCubes(bitImg);
        // finally, we add the meshed volume to our scene
        final Node n = sciView.addMesh(mesh);
        n.setName("Marching Cubes of Timestep " + i);
        n.setScale(new GLVector(0.01f, 0.01f, 0.01f));
        n.setPosition(new GLVector(-0.32f, 0.68f, -0.32f));
        System.out.println("Thresholded timestep " + i);

        gol.getVolume().setTransferFunction(TransferFunction.ramp(0.2f, 0.3f));

        // sleep for a bit so that the changes are not too fast
        try {
          Thread.sleep(5000);
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }

        // let's hide the old mesh
        n.setVisible(false);
        // and iterate the game of life another round
        gol.iterate();
        i++;
      }

    } catch (final InstantiableException e) {
      e.printStackTrace();
    }
  }
}

