package nju.edu.software.proofchain.model;

import uk.ac.imperial.pipe.models.petrinet.Connectable;

public interface ChainHead extends Connectable{
	
	
	
    /**
     * Message fired when the angle changes
     */
    String ANGLE_CHANGE_MESSAGE = "angle";
    /**
     * Height of the transition
     */
    int CHAINHEAD_HEIGHT = 90;
    /**
     * Width of the transition
     */
    int CHAINHEAD_WIDTH = CHAINHEAD_HEIGHT / 3;

    /**
    *
    * @return angle from (0,0) facing NORTH that this transition should be displayed at
    */
   int getAngle();

   /**
    *
    * @param angle set angle from (0,0) facing NORTH that this transition should be displayed at
    */
   void setAngle(int angle);

   String getChainHeadText();
   
   void setChainHeadText(String text);
   
   
	
}
