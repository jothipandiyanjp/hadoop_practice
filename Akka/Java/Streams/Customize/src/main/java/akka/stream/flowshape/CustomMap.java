package akka.stream.flowshape;

import akka.japi.Function;
import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class CustomMap<A, B> extends GraphStage<FlowShape<A, B>> {

	private final Function<A, B> f;

	public CustomMap(Function<A, B> f) {
		this.f = f;
	}

	public final Inlet<A> in = Inlet.create("Map.in");
	public final Outlet<B> out = Outlet.create("Map.out");

	
	  private final FlowShape<A, B> shape = FlowShape.of(in, out);
	  
	@Override
	public FlowShape<A, B> shape() {
		return shape;
	}
	
	@Override
	public GraphStageLogic createLogic(Attributes arg0) {
		return new GraphStageLogic(shape) {
			{
				setHandler(in, new AbstractInHandler() {
					
					@Override
					public void onPush() throws Exception {
			            push(out, f.apply(grab(in)));
					}
				});
				
				setHandler(out, new AbstractOutHandler() {
					
					@Override
					public void onPull() throws Exception {
			            pull(in);
					}
				});
			}
		};
	}
}
