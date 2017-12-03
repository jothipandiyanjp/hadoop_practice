package akka.stream.flowshape;

import akka.japi.Option;
import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class Duplicator<A> extends GraphStage<FlowShape<A, A>> {

	public final Inlet<A> in = Inlet.create("Duplicator.in");
	public final Outlet<A> out = Outlet.create("Duplicator.out");

	private final FlowShape<A, A> shape = FlowShape.of(in, out);

	@Override
	public FlowShape<A, A> shape() {
		return shape;
	}

	public GraphStageLogic createLogic(Attributes inheritedAttributes) {
		return new GraphStageLogic(shape) {
			Option<A> lastElem = Option.none();

			{
				setHandler(in, new AbstractInHandler() {

					@Override
					public void onPush() throws Exception {
						A elem = grab(in);
						lastElem = Option.some(elem);
						push(out, elem);
					}

					@Override
					public void onUpstreamFinish() {
						if (lastElem.isDefined()) {
							emit(out, lastElem.get());
						}

						complete(out);
					}
				});
				setHandler(out, new AbstractOutHandler() {
					public void onPull() throws Exception {
						if (lastElem.isDefined()) {
							push(out, lastElem.get());
							lastElem = Option.none();
						} else {
							pull(in);
						}
					}
				});

			}
		};
	}
}
