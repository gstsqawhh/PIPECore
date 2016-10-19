package nju.edu.software.proofchain.model;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.AbstractConnectable;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;


/*
 * 在具体的链节实现类中，需要加入chainBody属性和chainHeads列表，并在equals和hashcode方法中使用
 */
public class DiscreteChainUnit extends AbstractConnectable implements ChainUnit {//去掉该继承extends AbstractConnectable吗
	
  private String chainBody = "";  
  private List<ChainHead> chainHeads = new ArrayList<>();

	
    /**
     * Angle at which this transition should be displayed
     */
    private int angle = 0;

    /**
     * Constructor with default rate and priority
     * @param id of the chain unit
     * @param name of the chain unit
     */
    public DiscreteChainUnit(String id, String name) {
        super(id, name);
    }

    /**
     * Constructor that sets the default name of the chain unit to its id
     * @param id of the transition
     */
    public DiscreteChainUnit(String id) {
        super(id, id);
    }

    
    /**
     * Copy constructor
     * @param transition to be copied
     */
    public DiscreteChainUnit(DiscreteChainUnit chainunit) {
        super(chainunit);
        this.angle = chainunit.angle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscreteChainUnit)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    
	@Override
	public Double getCentre() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getArcEdgePoint(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEndPoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSelectable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDraggable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void accept(PetriNetComponentVisitor visitor) throws PetriNetComponentException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAngle() {
		// TODO Auto-generated method stub
		return angle;
	}

	@Override
	public void setAngle(int angle) {
		// TODO Auto-generated method stub
        int old = this.angle;
        this.angle = angle;
        changeSupport.firePropertyChange(ANGLE_CHANGE_MESSAGE, old, angle);

	}

	@Override
	public String getChainBody() {
 		return chainBody;
	}

	@Override
	public void setChainBody(String chainBody) {
 		this.chainBody = chainBody;
	}

	@Override
	public List<ChainHead> getChainHeads() {
 		return chainHeads;
	}

	@Override
	public void setChainHeads(List<ChainHead> chainHeads) {
 		this.chainHeads = chainHeads;
	}

}
