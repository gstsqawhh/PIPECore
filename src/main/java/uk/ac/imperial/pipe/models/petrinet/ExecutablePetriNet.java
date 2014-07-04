package uk.ac.imperial.pipe.models.petrinet;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;
import uk.ac.imperial.pipe.visitor.ClonePetriNet;

/**
 * Makes a PetriNet available for execution, that is, animation or analysis by a module.  Returns the state of the Petri net as a collections of its constituent components. 
 * <p>
 * If the Petri net is a composite Petri net, each import statement has been replaced with the components that comprise the imported Petri net, resulting in a single Petri net, 
 * with corresponding collections of all the constituent components.  
 * <p>
 * If this executable Petri net is animated, the markings that result from firing enabled transitions will be populated in the affected places.  
 * If the affected places are components in an imported Petri net, the markings in the updated places in the executable Petri net are mirrored to the corresponding imported Petri net.
 */
// * In the PIPE 5.0 gui, each imported Petri net is displayed in its own tab, and may be edited and persisted separately.  
// * Expanded Petri nets are not visible in the gui; their updated markings are visible in the tabs of the corresponding imported Petri net. 
public class ExecutablePetriNet implements PropertyChangeListener {

	private PetriNet petriNet;
	private Collection<Annotation> annotations;
	private Collection<Arc<? extends Connectable, ? extends Connectable>> arcs;
	private Collection<InboundArc> inboundArcs;
	private Collection<OutboundArc> outboundArcs;
	private Collection<Token> tokens;
	private Collection<RateParameter> rateParameters;
	private Collection<Place> places;
	private Collection<Transition> transitions;
	private boolean refreshRequired;

	public ExecutablePetriNet(PetriNet petriNet) {
		this.petriNet = petriNet;
		refreshRequired = true; 
		refresh(); 
	}

	public void refresh() {
		if (refreshRequired) {
		    PetriNet clonedPetriNet = ClonePetriNet.clone(petriNet);
			annotations = clonedPetriNet.getAnnotations(); 
			arcs = clonedPetriNet.getArcs(); 
			inboundArcs = clonedPetriNet.getInboundArcs();  
			outboundArcs = clonedPetriNet.getOutboundArcs();  
			tokens	= clonedPetriNet.getTokens();  
			rateParameters = clonedPetriNet.getRateParameters();  
			places = clonedPetriNet.getPlaces();  
			transitions = clonedPetriNet.getTransitions();  
			refreshRequired = false; 
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refreshRequired = true; 
	}


	/**
	 * @return all Places currently in the Petri net
	 */
	public Collection<Place> getPlaces() {
		refresh(); 
		return places;
	}
	/**
	 * @param place
	 * @return arcs that are outbound from place
	 */
	public Collection<InboundArc> outboundArcs(Place place) {
		refresh(); 
		return null;
	}
	/**
	 * An outbound arc of a transition is any arc that starts at the transition
	 * and connects elsewhere
	 *
	 * @param transition to find outbound arcs for
	 * @return arcs that are outbound from transition
	 */
//	public Collection<OutboundArc> outboundArcs(Transition transition) {
//		refresh(); 
//		return null;
//	}
	/**
	 * @return all transitions in the Petri net
	 */
	public Collection<Transition> getTransitions() {
		refresh(); 
		return transitions;
	}
	/**
	 * @return Petri net's collection of arcs
	 */
	public Collection<Arc<? extends Connectable, ? extends Connectable>> getArcs() {
		refresh(); 
		return arcs;
	}
	/**
	 *
	 * @return all outbound arcs in the Petri net
	 */
	public Collection<OutboundArc> getOutboundArcs() {
		refresh(); 
		return outboundArcs;
	}
	/**
	 *
	 * @return all inbound arcs in the Petri net
	 */
	public Collection<InboundArc> getInboundArcs() {
		refresh(); 
		return inboundArcs;
	}
	/**
	 * @return Petri net's list of tokens
	 */
	public Collection<Token> getTokens() {
		refresh(); 
		return tokens;
	}
	/**
	 * @return annotations stored in the Petri net
	 */
	public Collection<Annotation> getAnnotations() {
		refresh(); 
		return annotations;
	}
	/**
	 * @return rate parameters stored in the Petri net
	 */
	public Collection<RateParameter> getRateParameters() {
		refresh(); 
		return rateParameters;
	}
	/**
	 * @return true if the Petri net contains a default token
	 */
//	public boolean containsDefaultToken() {
//		return false; 
//	}

	/**
	 * @param id
	 * @return true if any component in the Petri net has this id
	 */
//	public boolean containsComponent(String id) {
//		return false; 
//	}

	/**
	 * @param id    component name
	 * @param clazz PetriNetComponent class
	 * @param <T>   type of Petri net component required
	 * @return component with the specified id if it exists in the Petri net
	 * @throws PetriNetComponentNotFoundException if component does not exist in Petri net
	 */
//	public <T extends PetriNetComponent> T getComponent(String id,
//			Class<T> clazz) throws PetriNetComponentNotFoundException {
//		return null; 
//	}

	/**
	 * @param transition to calculate inbound arc for
	 * @return arcs that are inbound to transition, that is arcs that come into the transition
	 */
//	public Collection<InboundArc> inboundArcs(Transition transition) {
//		return null; 
////		refresh(); 
////		return inboundArcs;
//	}
	/**
	 *
	 * @return petri net name
	 */
//	@XmlTransient
//	public PetriNetName getName() {
//		return null;
//	}

	/**
	 *
	 * @return string representation of the Petri net name
	 */
//	public String getNameValue() {
//		return null; 
//	}

	/**
	 *
	 * @return a set of all component id's contained within this Petri net
	 */
//	public Set<String> getComponentIds() {
//		return null; 
//	}
//	refresh(); 
//	return places;

	/**
	 *
	 * @param id
	 * @return true if a component with the given id exists in the Petri net
	 */
//	public boolean contains(String id) {
//		return false; 
//	}

}