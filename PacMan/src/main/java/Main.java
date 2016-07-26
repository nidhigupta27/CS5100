import entrants.pacman.nidhi.MinMax;
import entrants.pacman.nidhi.MyPacMan;
import entrants.pacman.nidhi.MyPacManII;
import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.StarterGhosts;
import examples.poPacMan.POPacMan;


/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    public static void main(String[] args) {

        //Executor executor = new Executor(true, true);
    	//Changing to fully observable environment
    	Executor executor = new Executor(false, true);
        //executor.runGameTimed(new MyPacMan(), new POCommGhosts(50), true);
        executor.runGameTimed(new MyPacManII(), new StarterGhosts() , true);
        
    }
}
