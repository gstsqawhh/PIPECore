package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public interface ChainHeadVisitor extends PetriNetComponentVisitor {
    void visit(ChainHead chainhead);

}
