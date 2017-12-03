package akka.stream.sourceshape;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.Attributes;
import akka.stream.Outlet;
import akka.stream.SourceShape;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class NumbersSource extends GraphStage<SourceShape<Integer>>{
	private final ActorSystem system = ActorSystem.create();

	public final Outlet<Integer> out = Outlet.create("NumberSource.out");
	private final LoggingAdapter log  = Logging.getLogger(system, this);

	private final SourceShape<Integer> shape = SourceShape.of(out);
	public SourceShape<Integer> shape() {
		return shape;
	}
	
	
	@Override
	public GraphStageLogic createLogic(Attributes arg0) {
		return new GraphStageLogic(shape()) {
			private int counter = 1;
			{
				setHandler(out, new AbstractOutHandler() {
					
					public void onPull() throws Exception {
						log.debug(""+counter);
						push(out, counter++);
					}
				});
			}
		};
	}
	
}
