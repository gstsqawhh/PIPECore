package nju.edu.software.proofchain.model;

import java.util.List;

import uk.ac.imperial.pipe.models.petrinet.Connectable;

//import uk.ac.imperial.pipe.models.petrinet.Connectable;
/*
 * 不知道transition的rate的含义是什么，能否用在ChainUnit链节中
 */
public interface ChainUnit extends Connectable{// extends Connectable 取消对Connectable的继承,真正能连接的部件是放在链节中的链头，而非链节
    /**
     * Message fired when the angle changes
     */
    String ANGLE_CHANGE_MESSAGE = "angle";

    /**
     * Height of the transition
     */
    int CHAINUNIT_HEIGHT = 300;
    /**
     * Width of the transition
     */
    int CHAINUNIT_WIDTH = CHAINUNIT_HEIGHT / 3;
    
//    String CHAIN_BODY = "";
    
//    List<ChainHead> chain_heads = new ArrayList<>();
    
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

   String getChainBody();
   
   void setChainBody(String chainBody);
   
   List<ChainHead> getChainHeads();
   
   void setChainHeads(List<ChainHead> chainHeads);
   
   
	
}
