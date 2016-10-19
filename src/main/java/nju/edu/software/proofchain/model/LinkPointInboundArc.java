package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.AbstractArc;
import uk.ac.imperial.pipe.models.petrinet.ArcType;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;
import uk.ac.imperial.state.State;

public class LinkPointInboundArc extends AbstractArc<ChainHead, PCLinkPoint> {

	public LinkPointInboundArc(ChainHead source,PCLinkPoint target){
		super(source,target,ArcType.LINKARC);
	}
	

	@Override
	public void accept(PetriNetComponentVisitor visitor) throws PetriNetComponentException {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean canFire(PetriNet petriNet, State state) {
		return false;
	}

}
