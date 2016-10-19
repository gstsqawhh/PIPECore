package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public interface DiscreteChainHeadVisitor extends PetriNetComponentVisitor {
    void visit(DiscreteChainHead chainhead);

}
