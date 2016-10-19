package nju.edu.software.proofchain.model;

import java.awt.geom.Point2D;

import uk.ac.imperial.pipe.models.petrinet.AbstractConnectable;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

public final class DiscretePCLinkPoint extends AbstractConnectable implements PCLinkPoint {

    /**
     * Marking x offset relative to the place x coordinate
     */
    private double markingXOffset = 0;

    /**
     * Marking y offset relative to the place y coordinate
     */
    private double markingYOffset = 0;

    /**
     * Constructor
     * @param id of the pc link point
     * @param name of the pc link point
     */
    public DiscretePCLinkPoint(String id, String name) {
        super(id, name);
    }

    /**
     * Constructor that sets the PCLinkPoint's name to its id
     * @param id of the pc link point
     */
    public DiscretePCLinkPoint(String id) {
        super(id, id);
    }    

    public DiscretePCLinkPoint(DiscretePCLinkPoint pclinkpoint) {
        super(pclinkpoint);
        this.markingXOffset = pclinkpoint.markingXOffset;
        this.markingYOffset = pclinkpoint.markingYOffset;
    }
    
    
    
	@Override
	public Point2D.Double getCentre() {		
		return new Point2D.Double(getX() + getWidth() / 2, getY() + getHeight() / 2);
	}

    /**
     * Since Proof Chain Link Point is a circle, performs basic trigonometry
     * based on the angle that the other object is from
     * <p>
     * Note (0,0) is top left corner of grid.  -------&gt; x
     * |
     * |
     * |
     * y V
     * </p>
     * @return point where arc attaches to this place
     */
	@Override
	public Point2D.Double getArcEdgePoint(double angle) {
		double radius = DIAMETER / 2;
        double centreX = x + radius;
        double opposite = Math.cos(angle);
        double attachX = centreX - radius * opposite;

        double centreY = y + radius;
        double adjacent = Math.sin(angle);
        double attachY = centreY - radius * adjacent;

        return new Point2D.Double(attachX, attachY);
	}

	@Override
	public boolean isEndPoint() {
		return true;
	}

	@Override
	public int getHeight() {
		return DIAMETER;
	}

	@Override
	public int getWidth() {
		return DIAMETER;
	}

	@Override
	public boolean isSelectable() {
		return true;
	}
	
	@Override
	public boolean isDraggable() {
		// can be dragged on the canvas
		return true;
	}

	@Override
	public void accept(PetriNetComponentVisitor visitor) throws PetriNetComponentException {
		//需要新建PCLinkPoint对应的Vistor
        if (visitor instanceof PCLinkPointVisitor) {
            ((PCLinkPointVisitor) visitor).visit(this);
        }
        if (visitor instanceof DiscretePCLinkPointVisitor) {
            ((DiscretePCLinkPointVisitor) visitor).visit(this);
        }
	}

	@Override
	public double getMarkingXOffset() {
		return markingXOffset;
	}

	@Override
	public void setMarkingXOffset(double markingXOffset) {
		this.markingXOffset = markingXOffset;
	}

	@Override
	public double getMarkingYOffset() {
		return markingYOffset;
	}

	@Override
	public void setMarkingYOffset(double markingYOffset) {
		this.markingYOffset = markingYOffset;
	}

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(markingXOffset);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(markingYOffset);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiscretePCLinkPoint place = (DiscretePCLinkPoint) o;

        if (!super.equals(place)) {
            return false;
        }
        
        if (Double.compare(place.markingXOffset, markingXOffset) != 0) {
            return false;
        }
        if (Double.compare(place.markingYOffset, markingYOffset) != 0) {
            return false;
        }
        return true;
    }

	
	
}
