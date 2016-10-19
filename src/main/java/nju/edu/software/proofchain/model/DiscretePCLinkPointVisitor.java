package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public interface DiscretePCLinkPointVisitor extends PetriNetComponentVisitor {
    void visit(DiscretePCLinkPoint discretePCLinkPoint);

}
