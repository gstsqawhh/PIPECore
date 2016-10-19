package uk.ac.imperial.pipe.models.petrinet;

import nju.edu.software.proofchain.model.LinkPointInboundArc;
import nju.edu.software.proofchain.model.LinkPointOutboundArc;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

/**
 * Visit inbound and outbound arcs
 */
public interface ArcVisitor extends PetriNetComponentVisitor {
    void visit(InboundArc inboundArc);

    void visit(OutboundArc outboundArc);
    
    void visit(LinkPointInboundArc lpinboundArc);
    
    void visit(LinkPointOutboundArc lpoutboundArc);

    
}
