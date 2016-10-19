package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.models.petrinet.Connectable;

public interface PCLinkPoint extends Connectable {
	/**
     * Proof Chain link point diameter
     */
    int DIAMETER = 50;
    
    double getMarkingXOffset();

    void setMarkingXOffset(double markingXOffset);

    double getMarkingYOffset();

    void setMarkingYOffset(double markingYOffset);

    
	
	

}
