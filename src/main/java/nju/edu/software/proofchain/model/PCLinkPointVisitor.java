package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public interface PCLinkPointVisitor extends PetriNetComponentVisitor {
	
    void visit(PCLinkPoint linkpoint) throws PetriNetComponentException;

}
