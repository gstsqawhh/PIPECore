package nju.edu.software.proofchain.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;


import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.AbstractConnectable;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public final class DiscreteChainHead extends AbstractConnectable implements ChainHead {

    /**
     * 一些角度值，表示chain head 框怎样与线条相连 
     * 135 degrees
     */
    public static final int DEGREES_135 = 135;

    /**
     * 45 degrees
     */
    public static final int DEGREES_45 = 45;

    public static final int DEGREES_225 = 225;
    public static final int DEGREES_315 = 315;

    /**
     * Angle at which this transition should be displayed
     */
    private int angle = 0;

    private String chainHeadText;
    
    
    
	
	
	
    /**
     * Constructor with default rate and priority
     * @param id of the chain head
     * @param name of the chain head
     */
    public DiscreteChainHead(String id, String name) {
        super(id, name);
    }

    /**
     * Constructor that sets the default rate priority and the name of the chain head to its id
     * @param id of the chain head
     */
    public DiscreteChainHead(String id) {
        super(id, id);
    }
    
    /**
     * Copy constructor
     * @param transition to be copied
     */
    public DiscreteChainHead(DiscreteChainHead chainhead) {
        super(chainhead);
        this.angle = chainhead.angle;
        this.chainHeadText = chainhead.chainHeadText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscreteChainHead)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DiscreteChainHead that = (DiscreteChainHead) o;

        if (!chainHeadText.equals(that.chainHeadText)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + chainHeadText.hashCode();
        return result;
    }    
	
	@Override
	public Double getCentre() {
        return new Point2D.Double(getX() + getWidth() / 2, getY() + getHeight() / 2);
	}

	@Override
	public Double getArcEdgePoint(double angle) {
        int halfHeight = getHeight() / 2;
        int halfWidth = getWidth() / 2;
        double centreX = x + halfWidth;
        double centreY = y + halfHeight;

        Point2D.Double connectionPoint = new Point2D.Double(centreX, centreY);

        double rotatedAngle = angle - Math.toRadians(this.angle);
        if (rotatedAngle < 0) {
            rotatedAngle = 2* Math.PI + rotatedAngle;
        }
        if (connectToTop(rotatedAngle)) {
            connectionPoint.y -= halfHeight;
        } else if (connectToBottom(rotatedAngle)) {
            connectionPoint.y += halfHeight;
        } else if (connectToRight(rotatedAngle)) {
            connectionPoint.x += halfWidth;
        } else {
            //connect to left
            connectionPoint.x -= halfWidth;
        }

        return rotateAroundCenter(Math.toRadians(this.angle), connectionPoint);

	}

    /**
    *
    * @param angle in radians between 0 and 2pi
    * @return true if an arc connecting to this should connect to the bottom edge
    * of the chain head
    */
   private boolean connectToTop(double angle) {
       return angle >= Math.toRadians(DEGREES_45) && angle < Math.toRadians(DEGREES_135);
   }

   /**
    * @param angle in radians
    * @return true if an arc connecting to this should
    * connect to the top edge of the chain head
    */
   private boolean connectToBottom(double angle) {
       return angle < Math.toRadians(DEGREES_315) && angle >= Math.toRadians(DEGREES_225);
   }

   /**
    * @param angle in radians
    * @return true if an arc connecting to this should
    * connect to the left edge of the chain head
    */
   private boolean connectToRight(double angle) {
       return angle < Math.toRadians(DEGREES_225) && angle >= Math.toRadians(DEGREES_135);
   }

   /**
    * Rotates point on chain head around chain head center
    *
    * @param angle rotation angle in degrees
    * @param point point to rotate
    * @return rotated point
    */
   private Point2D.Double rotateAroundCenter(double angle, Point2D.Double point) {
       AffineTransform tx = AffineTransform.getRotateInstance(angle, getCentre().getX(), getCentre().getY());
       Point2D center = getCentre();
       Point2D.Double rotatedPoint = new Point2D.Double();
       tx.transform(point, rotatedPoint);
       return rotatedPoint;
   }

	
	@Override
	public boolean isEndPoint() {
		return true;
	}

	@Override
	public int getHeight() {
		return CHAINHEAD_HEIGHT;
	}

	@Override
	public int getWidth() {
		return CHAINHEAD_WIDTH;
	}

	@Override
	public boolean isSelectable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isDraggable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void accept(PetriNetComponentVisitor visitor) throws PetriNetComponentException {
        if (visitor instanceof ChainHeadVisitor) {
            ((ChainHeadVisitor) visitor).visit(this);
        }
        if (visitor instanceof DiscreteChainHeadVisitor) {
            ((DiscreteChainHeadVisitor) visitor).visit(this);
        }

	}

	@Override
	public int getAngle() {
		// TODO Auto-generated method stub
		return angle;
	}

	@Override
	public void setAngle(int angle) {
		// TODO Auto-generated method stub
		this.angle = angle;
	}

	@Override
	public String getChainHeadText() {
		// TODO Auto-generated method stub
		return chainHeadText;
	}

	@Override
	public void setChainHeadText(String text) {
		// TODO Auto-generated method stub
		this.chainHeadText = text;
	}

    /**
     * @param parser parser for a given state of Petri net
     * @param arcs   set of inbound arcs to evaluate weight against the current state
     * @return map of arc place id -> arc weights associated with it
     */
//    private Map<String, Map<String, Double>> evaluateInboundArcWeights(PetriNetWeightParser parser,
//                                                                       Collection<InboundArc> arcs) {
//        Map<String, Map<String, Double>> result = new HashMap<>();
//        for (InboundArc arc : arcs) {
//            String placeId = arc.getSource().getId();
//            Map<String, String> arcWeights = arc.getTokenWeights();
//            Map<String, Double> weights = evaluateArcWeight(parser, arcWeights);
//            result.put(placeId, weights);
//        }
//
//        return result;
//    }
	
	
}
