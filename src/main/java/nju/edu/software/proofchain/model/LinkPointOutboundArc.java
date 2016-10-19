package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.AbstractArc;
import uk.ac.imperial.pipe.models.petrinet.ArcType;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;
import uk.ac.imperial.state.State;

public class LinkPointOutboundArc extends AbstractArc<PCLinkPoint, ChainHead> {

	public LinkPointOutboundArc(PCLinkPoint source, ChainHead target, ArcType type) {
		super(source, target, ArcType.LINKARC);
	}

	@Override
	public boolean canFire(PetriNet petriNet, State state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void accept(PetriNetComponentVisitor visitor) throws PetriNetComponentException {
		// TODO Auto-generated method stub

	}

}
