package akka.stream.example;

import java.util.ArrayDeque;
import java.util.Queue;

import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class TwoBuffer<A> extends GraphStage<FlowShape<A, A>> {

	public final Inlet<A> in = Inlet.create("TwoBuffer.in");
	public final Outlet<A> out = Outlet.create("TwoBuffer.out");

	private final FlowShape<A, A> shape = FlowShape.of(in, out);

	public FlowShape<A, A> shape() {
		return shape;
	}

	@Override
	public GraphStageLogic createLogic(Attributes inheritedAttributes) {

		return new GraphStageLogic(shape) {

		//Setting buffer size	
			private final int SIZE = 3;
			private Queue<A> buffer = new ArrayDeque<>(SIZE);
			private boolean downstreamWaiting = false;

			private boolean isBufferFull() {
				return buffer.size() == SIZE;
			}

			@Override
			public void preStart() {
				pull(in);   // return 1 2 because we mentioned buffer size = 2
			}

			{
				setHandler(in, new AbstractInHandler() {
					public void onPush() {
						A elem = grab(in);
						buffer.add(elem);
						if (downstreamWaiting) {
							downstreamWaiting = false;
							A bufferedElem = buffer.poll();
							push(out, bufferedElem);
						}
						if (!isBufferFull()) {
							pull(in);  //  if size is not full pull an element
						}
					}

					public void onUpstreamFinish() {
						if (!buffer.isEmpty()) {
							// emit the rest if possible
							emitMultiple(out, buffer.iterator());
						}
						completeStage();
					}
				});

				setHandler(out, new AbstractOutHandler() {
					public void onPull() throws Exception {
					
						if (buffer.isEmpty()) {
							downstreamWaiting = true;
						} else {
							A elem = buffer.poll();
							push(out, elem);
						}
			
					}
				});
			}
		};

	}
}