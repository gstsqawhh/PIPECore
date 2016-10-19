package uk.ac.imperial.pipe.models.petrinet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import nju.edu.software.proofchain.model.ChainHead;
import nju.edu.software.proofchain.model.ChainUnit;
import nju.edu.software.proofchain.model.LinkPointInboundArc;
import nju.edu.software.proofchain.model.LinkPointOutboundArc;
import nju.edu.software.proofchain.model.PCLinkPoint;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;
import uk.ac.imperial.pipe.parsers.EvalVisitor;
import uk.ac.imperial.pipe.parsers.FunctionalResults;
import uk.ac.imperial.pipe.parsers.FunctionalWeightParser;
import uk.ac.imperial.pipe.parsers.PetriNetWeightParser;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

import javax.xml.bind.annotation.XmlTransient;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Petri net class that houses Petri net components and performs the logic on their
 * insertion and deletion.
 */
public class PetriNet {
    /**
     * Message fired when Petri net name changes
     */
    public static final String PETRI_NET_NAME_CHANGE_MESSAGE = "nameChange";

    /**
     * Message fired when an annotation is added to the Petri net
     */
    public static final String NEW_ANNOTATION_CHANGE_MESSAGE = "newAnnotation";

    /**
     * Message fired when a place is deleted from the Petri net
     */
    public static final String DELETE_PLACE_CHANGE_MESSAGE = "deletePlace";

    /**
     * Message fired when an arc is deleted from the Petri net
     */
    public static final String DELETE_ARC_CHANGE_MESSAGE = "deleteArc";

    /**
     * Message fired when a transition is deleted from the Petri net
     */
    public static final String DELETE_TRANSITION_CHANGE_MESSAGE = "deleteTransition";

    /**
     * Message fired when an annotation is deleted from the Petri net
     */
    public static final String DELETE_ANNOTATION_CHANGE_MESSAGE = "deleteAnnotation";

    /**
     * Message fired when a Place is added to the Petri net
     */
    public static final String NEW_PLACE_CHANGE_MESSAGE = "newPlace";

    /**
     * Message fired when a transition is added to the Petri net
     */
    public static final String NEW_TRANSITION_CHANGE_MESSAGE = "newTransition";

    /**
     * Message fired when an arc is added to the Petri net
     */
    public static final String NEW_ARC_CHANGE_MESSAGE = "newArc";

    /**
     * Message fired when a token is added to the Petri net
     */
    public static final String NEW_TOKEN_CHANGE_MESSAGE = "newToken";

    /**
     * Message fired when a token is deleted
     */
    public static final String DELETE_TOKEN_CHANGE_MESSAGE = "deleteToken";

    /**
     * Message fired when a rate parameter is added
     */
    public static final String NEW_RATE_PARAMETER_CHANGE_MESSAGE = "newRateParameter";

    /**
     * Message fired when a rate parameter is deleted
     */
    public static final String DELETE_RATE_PARAMETER_CHANGE_MESSAGE = "deleteRateParameter";

    /*
     * Message fired when a link point is added
     */
    public static final String NEW_lINK_POINT_CHANGE_MESSAGE = "newLinkPoint";
    
    /*
     * Message fired when a link point is deleted
     */
    public static final String DELETE_lINK_POINT_CHANGE_MESSAGE = "deleteLinkPoint";

    /*
     * whh:需要写链头New ChainHead Message吗
     * Message fired when a chain unit is added
     */
    public static final String NEW_CHAIN_UNIT_CHANGE_MESSAGE = "newChainUnit";
    
    /*
     * Message fired when a chain unit is deleted
     */
    public static final String DELETE_CHAIN_UNIT_CHANGE_MESSAGE = "deleteChainUnit";

    
    
    
    /**
     * Property change support used to fire messages and register listeners to
     */
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Functional weight parser
     */
    private final FunctionalWeightParser<Double> functionalWeightParser = new PetriNetWeightParser(new EvalVisitor(this), this);

    /**
     * Visitor used to remove petri net components when the type is not directly known
     */
    //TODO: CYCLIC DEPENDENCY BETWEEN CREATING THIS AND PETRI NET/
    private final PetriNetComponentVisitor deleteVisitor = new PetriNetComponentRemovalVisitor(this);

    /**
     * Maps transition id -> transition
     */
    private final Map<String, Transition> transitions = new HashMap<>();

    /**
     * Maps place id -> place
     */
    private final Map<String, Place> places = new HashMap<>();

    /**
     * Maps token id -> token
     */
    private final Map<String, Token> tokens = new HashMap<>();

    /**
     * Maps inbound arc id -> inbound arc
     */
    private final Map<String, InboundArc> inboundArcs = new HashMap<>();

    /**
     * Maps outbound arc id -> outbound arc
     */
    private final Map<String, OutboundArc> outboundArcs = new HashMap<>();
    /*
     * whh Maps link point id -> PCLinkPoint
     */
    private final Map<String, PCLinkPoint> pclinkpoints = new HashMap<>();

    /*
     * whh Maps chain head id -> chain head
     */
    private final Map<String, ChainHead> chainHeads = new HashMap<>();

    /*
     * whh Maps chain unit id -> chain unit,
     * 问题：petri net上应该放ChainUnit，那么chainHeads应该放在chain unit里面，而不应该单独拎出来
     * 但同时箭头是指向ChainHead的
     */
    private final Map<String, ChainUnit> chainUnits = new HashMap<>();

    
    
    /*
     * whh new arc map: arc id -> LinkPointInboundArc
     */
    private final Map<String,LinkPointInboundArc> lp_inboundArcs = new HashMap<>();

    /*
     * whh new arc map: arc id -> LinkPointOutboundArc
     */
    private final Map<String,LinkPointOutboundArc> lp_outboundArcs = new HashMap<>();

    /*
     * whh
     *  Maps LinkPoint id -> outbound arcs out of the link point
     */
    private final Multimap<String, LinkPointOutboundArc> linkpointOutboundArcs = HashMultimap.create();

    /*
     * whh
     * Maps LinkPoint id -> inbound arcs into the LinkPoint
     */
    private final Multimap<String, LinkPointInboundArc> linkpointInboundArcs =  HashMultimap.create();

    
    /**
     * Maps rate paramter id -> rate paramter
     */
    private final Map<String, RateParameter> rateParameters = new HashMap<>();

    /**
     * Maps annotation id -> annotation
     */
    private final Map<String, Annotation> annotations = new HashMap<>();

    /**
     *  Maps transition id -> outbound arcs out of the transition
     */
    private final Multimap<String, OutboundArc> transitionOutboundArcs = HashMultimap.create();

    /**
     * Maps transition id -> inbound arcs into the transition
     */
    private final Multimap<String, InboundArc> transitionInboundArcs =  HashMultimap.create();

    /**
     * A tokens that will contain the maps specified above.
     * It's ID is the class type to tokens
     * Sadly need to cast to get the exact tokens back out of it. If you know of a better way to
     * do this then please change it. It is used to easily get a Petri net component of type T
     * by id.
     */
    private final Map<Class<? extends PetriNetComponent>, Map<String, ? extends PetriNetComponent>> componentMaps =
            new HashMap<>();

    /**
     * Used to add Petri net components to the Petri net when their type is not directlty known
     */
    private final PetriNetComponentVisitor addVisitor = new PetriNetComponentAddVisitor(this);

    /**
     * Name of hte Petri net
     */
    //TODO: IS THIS USED?
    public String pnmlName = "";

    /**
     * Petri net name
     */
    private PetriNetName petriNetName;

    /**
     * Validated
     */
    //TODO: WHAT IS THIS
    private boolean validated = false;


    /**
     * Constructor
     * @param name the name of the Petri net, it should be unique
     */
    public PetriNet(PetriNetName name) {
        this();
        this.petriNetName = name;
    }

    //TODO: INITIALISE NAME?

    /**
     * Default constructor initialises the petri net components map
     */
    public PetriNet() {
        initialiseIdMap();
    }

    /**
     * Initialises the petri net components map for addtion and retreivals
     * by mapping the component interface class to the map that contains the components
     */
    private void initialiseIdMap() {
        componentMaps.put(Place.class, places);
        componentMaps.put(Transition.class, transitions);
        componentMaps.put(InboundArc.class, inboundArcs);
        componentMaps.put(OutboundArc.class, outboundArcs);
        componentMaps.put(Token.class, tokens);
        componentMaps.put(RateParameter.class, rateParameters);
        componentMaps.put(Annotation.class, annotations);
        //whh:ChainUnit应该实现Connectable接口
        componentMaps.put(PCLinkPoint.class, pclinkpoints);
        componentMaps.put(ChainUnit.class, chainUnits);
        componentMaps.put(LinkPointInboundArc.class, lp_inboundArcs);
        componentMaps.put(LinkPointOutboundArc.class, lp_outboundArcs);       
    }

    @Override
    public int hashCode() {
        int result = transitions.hashCode();
        result = 31 * result + places.hashCode();
        result = 31 * result + tokens.hashCode();
        result = 31 * result + inboundArcs.hashCode();
        result = 31 * result + outboundArcs.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + rateParameters.hashCode();
        //whh:
        result = 31 * result + pclinkpoints.hashCode();
        result = 31 * result + chainUnits.hashCode();
        result = 31 * result + lp_inboundArcs.hashCode();
        result = 31 * result + lp_outboundArcs.hashCode();

        result = 31 * result + (petriNetName != null ? petriNetName.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PetriNet)) {
            return false;
        }

        PetriNet petriNet = (PetriNet) o;


        if (!CollectionUtils.isEqualCollection(annotations.values(), petriNet.annotations.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(inboundArcs.values(), petriNet.inboundArcs.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(outboundArcs.values(), petriNet.outboundArcs.values())) {
            return false;
        }
        if (petriNetName != null ? !petriNetName.equals(petriNet.petriNetName) : petriNet.petriNetName != null) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(places.values(), petriNet.places.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(rateParameters.values(), petriNet.rateParameters.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(tokens.values(), petriNet.tokens.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(transitions.values(), petriNet.transitions.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(pclinkpoints.values(), petriNet.pclinkpoints.values())) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection(chainUnits.values(), petriNet.chainUnits.values())) {
            return false;
        }        
        if (!CollectionUtils.isEqualCollection(lp_inboundArcs.values(), petriNet.lp_inboundArcs.values())) {
            return false;
        }         
        if (!CollectionUtils.isEqualCollection(lp_outboundArcs.values(), petriNet.lp_outboundArcs.values())) {
            return false;
        }         
        return true;
    }

    /**
     *
     * @param listener listens for changes on the Petri net
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     *
     * @param listener current listener listining to the Petri net
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     *
     * @return PNML name
     */
    @XmlTransient
    public String getPnmlName() {
        return pnmlName;
    }

    /**
     *
     * @param pnmlName file name
     */
    public void setPnmlName(String pnmlName) {
        this.pnmlName = pnmlName;
    }

    /**
     *
     * @return true if validated
     */
    @XmlTransient
    public boolean isValidated() {
        return validated;
    }

    /**
     *
     * @param validated new validated value
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Resets the Petri net name
     */
    //TODO: DELETE IF DONT USE PNML
    public void resetPNML() {
        pnmlName = null;
    }

    /**
     * Adds place to the Petri net
     *
     * @param place place to add to Petri net
     */
    public void addPlace(Place place) {
        if (!places.containsValue(place)) {
            places.put(place.getId(), place);
            place.addPropertyChangeListener(new NameChangeListener<>(place, places));
            changeSupport.firePropertyChange(NEW_PLACE_CHANGE_MESSAGE, null, place);
        }
    }

    /**
     * @return all Places currently in the Petri net
     */
    public Collection<Place> getPlaces() {
        return places.values();
    }

    /**
     * Removes the place and all arcs connected to the place from the
     * Petri net
     *
     * @param place to remove from Petri net
     * @throws PetriNetComponentException if component does not exist in the Petri net
     */
    public void removePlace(Place place) throws PetriNetComponentException {
        Collection<String> components = getComponentsReferencingId(place.getId());
        if (!components.isEmpty()) {
            throw new PetriNetComponentException("Cannot delete " + place.getId() + " it is referenced in a functional expression!");
        }
        this.places.remove(place.getId());
        for (InboundArc arc : outboundArcs(place)) {
            removeArc(arc);
        }
        changeSupport.firePropertyChange(DELETE_PLACE_CHANGE_MESSAGE, place, null);
    }

 
    /*
     * whh: Adds chain unit to the Petri net
     *
     * @param chain unit to add to Petri net
     */
    public void addChainUnit(ChainUnit chainUnit) {
        if (!chainUnits.containsValue(chainUnit)) {
            chainUnits.put(chainUnit.getId(), chainUnit);
            chainUnit.addPropertyChangeListener(new NameChangeListener<>(chainUnit, chainUnits));
            changeSupport.firePropertyChange(NEW_CHAIN_UNIT_CHANGE_MESSAGE, null, chainUnit);
        }
    }

    /*
     *whh @return all ChainUnits currently in the Petri net
     */
    public Collection<ChainUnit> getChainUnits() {
        return chainUnits.values();
    }

    /*
     * Removes the chain unit and all arcs connected to the chain unit from the
     * Petri net
     *
     * @param chain unit to remove from Petri net
     * @throws PetriNetComponentException if component does not exist in the Petri net
     */
    public void removeChainUnit(ChainUnit chainunit) throws PetriNetComponentException {
        Collection<String> components = getComponentsReferencingId(place.getId());
        if (!components.isEmpty()) {
            throw new PetriNetComponentException("Cannot delete " + place.getId() + " it is referenced in a functional expression!");
        }
        this.places.remove(place.getId());
        for (InboundArc arc : outboundArcs(place)) {
            removeArc(arc);
        }
        changeSupport.firePropertyChange(DELETE_PLACE_CHANGE_MESSAGE, place, null);
    }
    
    /**
     *
     * @param componentId component id to find
     * @return all components ids whose functional expression references the componentId
     */
    private Collection<String> getComponentsReferencingId(String componentId) {
        Set<String> results = new HashSet<>();
        for (Transition transition : getTransitions()) {
            if (referencesId(transition.getRateExpr(), componentId)) {
                results.add(transition.getId());
            }
        }
        for (Arc<?, ?> arc : getArcs()) {
            for (String expr : arc.getTokenWeights().values()) {
                if (referencesId(expr, componentId)) {
                    results.add(arc.getId());
                    break;
                }
            }
        }
        for (RateParameter rateParameter : getRateParameters()) {
            if (referencesId(rateParameter.getExpression(), componentId)) {
                results.add(rateParameter.getId());
            }
        }
        return results;
    }

    /**
     *
     * @param expr
     * @param id
     * @return true if the component id is referenced in the functional expression
     */
    private boolean referencesId(String expr, String id) {
        Collection<String> components = getComponents(expr);
        return components.contains(id);
    }

    /**
     *
     * @param expression
     * @return a list of components that the expression references
     */
    private Collection<String> getComponents(String expression) {
        FunctionalResults<Double> results = parseExpression(expression);
        return results.getComponents();
    }

    /**
     * @param place for which outbound arcs are to be retrieved 
     * @return arcs that are outbound from place
     */
    public Collection<InboundArc> outboundArcs(Place place) {
        Collection<InboundArc> outbound = new LinkedList<>();
        for (InboundArc arc : inboundArcs.values()) {
            if (arc.getSource().equals(place)) {
                outbound.add(arc);
            }
        }
        return outbound;
    }

    /**
     * Removes the specified arc from the Petri net
     *
     * @param arc to remove from the Petri net
     */
    public void removeArc(InboundArc arc) {
        inboundArcs.remove(arc.getId());
        transitionInboundArcs.remove(arc.getTarget().getId(), arc);
        changeSupport.firePropertyChange(DELETE_ARC_CHANGE_MESSAGE, arc, null);
    }

    /**
     * Adds transition to the Petri net
     *
     * @param transition transition to add to the Petri net
     */
    public void addTransition(Transition transition) {
        if (!transitions.containsValue(transition)) {
            transitions.put(transition.getId(), transition);
            transition.addPropertyChangeListener(new NameChangeListener<>(transition, transitions));
            transition.addPropertyChangeListener(new NameChangeArcListener());
            changeSupport.firePropertyChange(NEW_TRANSITION_CHANGE_MESSAGE, null, transition);
        }
    }

    /**
     * Removes transition from the petri net. Also removes any arcs connected
     * to this transition
     *
     * @param transition to remove
     */
    public void removeTransition(Transition transition) {
        this.transitions.remove(transition.getId());
        for (OutboundArc arc : outboundArcs(transition)) {
            removeArc(arc);
        }
        transitionOutboundArcs.removeAll(transition.getId());
        transitionInboundArcs.removeAll(transition.getId());
        changeSupport.firePropertyChange(DELETE_TRANSITION_CHANGE_MESSAGE, transition, null);
    }

    /**
     * An outbound arc of a transition is any arc that starts at the transition
     * and connects elsewhere
     *
     * @param transition to find outbound arcs for
     * @return arcs that are outbound from transition
     */
    public Collection<OutboundArc> outboundArcs(Transition transition) {
        return transitionOutboundArcs.get(transition.getId());
    }

    /**
     * Removes the specified arc from the Petri net
     *
     * @param arc to remove from the Petri net
     */
    public void removeArc(OutboundArc arc) {
        outboundArcs.remove(arc.getId());
        transitionOutboundArcs.remove(arc.getSource().getId(), arc);
        changeSupport.firePropertyChange(DELETE_ARC_CHANGE_MESSAGE, arc, null);
    }

    /**
     * @return all transitions in the Petri net
     */
    public Collection<Transition> getTransitions() {
        return transitions.values();
    }

    /**
     *
     * Adds this arc to the petri net
     *
     * @param inboundArc inbound arc to include in the Petri net
     */
    public void addArc(InboundArc inboundArc) {
        if (!inboundArcs.containsKey(inboundArc.getId())) {
            inboundArcs.put(inboundArc.getId(), inboundArc);
            transitionInboundArcs.put(inboundArc.getTarget().getId(), inboundArc);
            inboundArc.addPropertyChangeListener(new NameChangeListener<>(inboundArc, inboundArcs));
            changeSupport.firePropertyChange(NEW_ARC_CHANGE_MESSAGE, null, inboundArc);
        }
    }

    /**
     * Adds this arc to the petri net
     * @param outboundArc outbound arc to include in the Petri net
     */
    public void addArc(OutboundArc outboundArc) {
        if (!outboundArcs.containsKey(outboundArc.getId())) {
            outboundArcs.put(outboundArc.getId(), outboundArc);
            transitionOutboundArcs.put(outboundArc.getSource().getId(), outboundArc);
            outboundArc.addPropertyChangeListener(new NameChangeListener<>(outboundArc, outboundArcs));
            changeSupport.firePropertyChange(NEW_ARC_CHANGE_MESSAGE, null, outboundArc);
        }
    }
    
    /*
     * whh,加入向link point进来的线条
     */
    public void addArc(LinkPointInboundArc lpinboundArc){
        if (!lp_inboundArcs.containsKey(lpinboundArc.getId())) {
            lp_inboundArcs.put(lpinboundArc.getId(), lpinboundArc);
            linkpointInboundArcs.put(lpinboundArc.getTarget().getId(), lpinboundArc);
            lpinboundArc.addPropertyChangeListener(new NameChangeListener<>(lpinboundArc, lp_inboundArcs));
            changeSupport.firePropertyChange(NEW_ARC_CHANGE_MESSAGE, null, lpinboundArc);
        }
    }
    
    /*
     * 需要自己写特定的的message：DELETE_ARC_CHANGE_MESSAGE？
     */
    public void removeArc(LinkPointInboundArc arc){
    	lp_inboundArcs.remove(arc.getId());
    	linkpointInboundArcs.remove(arc.getTarget().getId(), arc);
        changeSupport.firePropertyChange(DELETE_ARC_CHANGE_MESSAGE, arc, null);
    }
    
    
    /*
     * whh,加入从link point出去的线条
     */
    public void addArc(LinkPointOutboundArc lpoutboundArc){
    	//whh TODO:要不要替换NEW_ARC_CHANGE_MESSAGE
        if (!lp_outboundArcs.containsKey(lpoutboundArc.getId())) {
            lp_outboundArcs.put(lpoutboundArc.getId(), lpoutboundArc);
            linkpointOutboundArcs.put(lpoutboundArc.getSource().getId(), lpoutboundArc);
            lpoutboundArc.addPropertyChangeListener(new NameChangeListener<>(lpoutboundArc, lp_outboundArcs));
            changeSupport.firePropertyChange(NEW_ARC_CHANGE_MESSAGE, null, lpoutboundArc);
        }
    }
    
    /*
     * whh: Removes the specified arc from the Petri net
     *
     * @param arc to remove from the Petri net
     */
    public void removeArc(LinkPointOutboundArc arc) {
        lp_outboundArcs.remove(arc.getId());
        linkpointOutboundArcs.remove(arc.getSource().getId(), arc);
        changeSupport.firePropertyChange(DELETE_ARC_CHANGE_MESSAGE, arc, null);
    }
    
    /**
     * @return Petri net's collection of arcs
     */
    public Collection<Arc<? extends Connectable, ? extends Connectable>> getArcs() {
        Collection<Arc<? extends Connectable, ? extends Connectable>> arcs = new LinkedList<>();
        arcs.addAll(getOutboundArcs());
        arcs.addAll(getInboundArcs());
        return arcs;
    }

    /**
     *
     * @return all outbound arcs in the Petri net
     */
    public Collection<OutboundArc> getOutboundArcs() {
        return outboundArcs.values();
    }

    /**
     *
     * @return all inbound arcs in the Petri net
     */
    public Collection<InboundArc> getInboundArcs() {
        return inboundArcs.values();
    }

    /**
     * Adds the token to the Petri net
     *
     * @param token to be added 
     */
    public void addToken(Token token) {
        if (!tokens.containsValue(token)) {
            tokens.put(token.getId(), token);
            token.addPropertyChangeListener(new NameChangeListener<>(token, tokens));
            token.addPropertyChangeListener(new TokenNameChanger());
            changeSupport.firePropertyChange(NEW_TOKEN_CHANGE_MESSAGE, null, token);
        }
    }

    /**
     * Tries to remove the token
     *
     * @param token token to remove
     * @throws PetriNetComponentException if places or transitions reference this token!
     */
    public void removeToken(Token token) throws PetriNetComponentException {
        Collection<Place> referencedPlaces = getPlacesContainingToken(token);
        Collection<Transition> referencedTransitions = getTransitionsReferencingToken(token);
        if (referencedPlaces.isEmpty() && referencedTransitions.isEmpty()) {
            tokens.remove(token.getId());
            changeSupport.firePropertyChange(DELETE_TOKEN_CHANGE_MESSAGE, token, null);
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append("Cannot remove ").append(token.getId()).append(" token");
        if (!referencedPlaces.isEmpty()) {
            message.append(" places: ");
            for (Place place : referencedPlaces) {
                message.append(place.getId());
            }
            message.append(" contains it\n");
        }
        if (!referencedTransitions.isEmpty()) {
            message.append(" transitions: ");
            for (Transition transition : referencedTransitions) {
                message.append(transition.getId());
            }
            message.append(" reference it\n");
        }

        throw new PetriNetComponentException(message.toString());
    }

    /**
     * @param token
     * @return collection of Places that contain 1 or more of these tokens
     */
    private Collection<Place> getPlacesContainingToken(Token token) {
        Collection<Place> result = new LinkedList<>();
        for (Place place : places.values()) {
            if (place.getTokenCount(token.getId()) > 0) {
                result.add(place);
            }
        }
        return result;
    }

    /**
     * @param token
     * @return list of transitions that reference the token in their rate expression
     */
    private Collection<Transition> getTransitionsReferencingToken(Token token) {
        Collection<Transition> result = new LinkedList<>();
        for (Transition transition : transitions.values()) {
            FunctionalResults<Double> results = functionalWeightParser.evaluateExpression(transition.getRateExpr());
            if (results.getComponents().contains(token.getId())) {
                result.add(transition);
            }
        }
        return result;
    }

    /**
     * @return Petri net's list of tokens
     */
    public Collection<Token> getTokens() {
        return tokens.values();
    }

    /**
     * Adds the annotation to the Petri net
     *
     * @param annotation to be added 
     */
    public void addAnnotation(Annotation annotation) {
        if (!annotations.containsKey(annotation.getId())) {
            annotations.put(annotation.getId(), annotation);
            annotation.addPropertyChangeListener(new NameChangeListener<>(annotation, annotations));
            changeSupport.firePropertyChange(NEW_ANNOTATION_CHANGE_MESSAGE, null, annotation);
        }
    }

    /**
     * Removes the specified annotation from the Petri net
     *
     * @param annotation annotation to remove
     */
    public void removeAnnotation(Annotation annotation) {
        annotations.remove(annotation.getId());
        changeSupport.firePropertyChange(DELETE_ANNOTATION_CHANGE_MESSAGE, annotation, null);
    }

    /**
     * @return annotations stored in the Petri net
     */
    public Collection<Annotation> getAnnotations() {
        return annotations.values();
    }

    /**
     * Adds the RateParameter to the Petri Net
     *
     * @param rateParameter to add to Petri net
     * @throws InvalidRateException if the rate is not parseable
     */
    public void addRateParameter(RateParameter rateParameter) throws InvalidRateException {
        if (!validFunctionalExpression(rateParameter.getExpression())) {
            throw new InvalidRateException(rateParameter.getExpression());
        }

        if (!rateParameters.containsValue(rateParameter)) {
            rateParameters.put(rateParameter.getId(), rateParameter);
            rateParameter.addPropertyChangeListener(new NameChangeListener<>(rateParameter, rateParameters));
            changeSupport.firePropertyChange(NEW_RATE_PARAMETER_CHANGE_MESSAGE, null, rateParameter);
        }
    }

    /**
     * Attempts to parse the expression of the rate
     *
     * @param expression functional expression to evaluate for Petri net
     * @return false if the rate's expression is invalid
     */
    public boolean validFunctionalExpression(String expression) {
        FunctionalResults<Double> result = functionalWeightParser.evaluateExpression(expression);
        return !result.hasErrors();
    }

    /**
     * Removes the rate parameter from the Petri net.
     * <p>
     * Any transitions referencing this rate parameter will have their rates
     * set to the last value of the rate parameter
     * </p>
     * @param parameter rate parameter to remove
     */
    public void removeRateParameter(RateParameter parameter) {
        removeRateParameterFromTransitions(parameter);
        rateParameters.remove(parameter.getId());
        changeSupport.firePropertyChange(DELETE_RATE_PARAMETER_CHANGE_MESSAGE, parameter, null);
    }

    /**
     * Removes the Rate Parameter from any transitions that refer to it
     * and replaces it with a {@link NormalRate} with the
     * same value
     *
     * @param parameter to remove
     */
    private void removeRateParameterFromTransitions(RateParameter parameter) {
        for (Transition transition : transitions.values()) {
            if (transition.getRate().equals(parameter)) {
                Rate rate = new NormalRate(parameter.getExpression());
                transition.setRate(rate);
            }
        }
    }

    /**
     * @return rate parameters stored in the Petri net
     */
    public Collection<RateParameter> getRateParameters() {
        return rateParameters.values();
    }

    /**
     * Add any Petri net component to this Petri net
     *
     * @param component to be added 
     * @throws PetriNetComponentException if component already exists or other logic error 
     */
    public void add(PetriNetComponent component) throws PetriNetComponentException {
        component.accept(addVisitor);
    }

    /**
     * Remove any Petri net component from the Petri net
     *
     * @param component component to remove
     * @throws PetriNetComponentException if component does not exist in the Petri net 
     */
    public void remove(PetriNetComponent component) throws PetriNetComponentException {
        if (contains(component.getId())) {
            component.accept(deleteVisitor);
        }
    }

    /**
     * @return true if the Petri net contains a default token
     */
    public boolean containsDefaultToken() {
        return tokens.containsKey("Default");
    }

    /**
     * @param id of the component
     * @return true if any component in the Petri net has this id
     */
    public boolean containsComponent(String id) {
        for (Map<String, ? extends PetriNetComponent> map : componentMaps.values()) {
            if (map.containsKey(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param id    component name
     * @param clazz PetriNetComponent class
     * @param <T>   type of Petri net component required
     * @return component with the specified id if it exists in the Petri net
     * @throws PetriNetComponentNotFoundException if component does not exist in Petri net
     */
    public <T extends PetriNetComponent> T getComponent(String id, Class<T> clazz)
            throws PetriNetComponentNotFoundException {
        Map<String, T> map = getMapForClass(clazz);
        if (map.containsKey(id)) {
            return map.get(id);
        }
        throw new PetriNetComponentNotFoundException("No component " + id + " exists in Petri net.");
    }

    /**
     *
     * @param clazz component map type, this should be the interface of the component
     * @param <T> componennt class
     * @return the map that corresponds to the clazz type.
     */
    private <T extends PetriNetComponent> Map<String, T> getMapForClass(Class<T> clazz) {
        return (Map<String, T>) componentMaps.get(clazz);
    }

    /**
     * @param transition to calculate inbound arc for
     * @return arcs that are inbound to transition, that is arcs that come into the transition
     */
    public Collection<InboundArc> inboundArcs(Transition transition) {
        return transitionInboundArcs.get(transition.getId());
    }

    /**
     *
     * @return petri net name
     */
    @XmlTransient
    public PetriNetName getName() {
        return petriNetName;
    }

    /**
     * Give the petri net a new name
     * @param name name to replace the existing name with
     */
    public void setName(PetriNetName name) {
        PetriNetName old = this.petriNetName;
        this.petriNetName = name;
        changeSupport.firePropertyChange(PETRI_NET_NAME_CHANGE_MESSAGE, old, name);
    }

    /**
     *
     * @return string representation of the Petri net name
     */
    public String getNameValue() {
        return petriNetName.getName();
    }

    /**
     * Parse the functional expression via the under lying Petri net state
     *
     * @param expr functional expression which conforms to the rate grammar
     * @return parsed expression
     */
    public FunctionalResults<Double> parseExpression(String expr) {
        return functionalWeightParser.evaluateExpression(expr);
    }

    /**
     * Listener for changing a components name in the set it is referenced by
     * @param <T>
     */
    private static class NameChangeListener<T extends PetriNetComponent> implements PropertyChangeListener {
        /**
         * Comoponent whose name will change
         */
        private final T component;

        /**
         * Component map that houses the component, needs to be updated on name change
         */
        private final Map<String, T> componentMap;

        /**
         * Constructor
         * @param component
         * @param componentMap
         */
        public NameChangeListener(T component, Map<String, T> componentMap) {
            this.component = component;
            this.componentMap = componentMap;
        }

        /**
         * If the name/id of the component changes then it is updated in the component map.
         * That is the old key is removed and the compoennt is readded with the new name.
         * @param evt
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PetriNetComponent.ID_CHANGE_MESSAGE)) {
                String oldId = (String) evt.getOldValue();
                String newId = (String) evt.getNewValue();
                componentMap.remove(oldId);
                componentMap.put(newId, component);
            }

        }
    }

    /**
     * This class is responsible for changing inbound and outbound arc references from
     * a transition id change
     */
    private class NameChangeArcListener implements PropertyChangeListener {

        /**
         * If a transition changes name then this is updated in the maps by removing the key
         * and replacing the inbound/outbound arcs with the new name as the key.
         * @param evt
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PetriNetComponent.ID_CHANGE_MESSAGE)) {
                String oldId = (String) evt.getOldValue();
                String newId = (String) evt.getNewValue();
                Collection<InboundArc> inbound = transitionInboundArcs.removeAll(oldId);
                Collection<OutboundArc> outbound = transitionOutboundArcs.removeAll(oldId);
                transitionInboundArcs.putAll(newId, inbound);
                transitionOutboundArcs.putAll(newId, outbound);
            }
        }
    }

    /**
     * Listens for name changes of a token
     */
    private class TokenNameChanger implements PropertyChangeListener {

        /**
         * When a tokens name changes then the maps in the places and arc need adjusting
         * @param evt
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PetriNetComponent.ID_CHANGE_MESSAGE)) {
                String oldId = (String) evt.getOldValue();
                String newId = (String) evt.getNewValue();
                changePlaceTokens(oldId, newId);
                changeArcTokens(oldId, newId);
            }
        }


        /**
         *
         * Changes references of token counts in place containing old id to new id
         *
         * @param oldId old token id
         * @param newId new token id
         */
        private void changePlaceTokens(String oldId, String newId) {
            for (Place place : getPlaces()) {
                int count = place.getTokenCount(oldId);
                place.removeAllTokens(oldId);
                place.setTokenCount(newId, count);
            }
        }

        /**
         *
         * Changes references of token weights in arcs from old id to new id
         *
         * @param oldId old token id
         * @param newId new token id
         */
        private void changeArcTokens(String oldId, String newId) {
            for (Arc<? extends Connectable, ? extends Connectable> arc : getArcs()) {
                if (arc.getTokenWeights().containsKey(oldId)) {
                    String weight = arc.getWeightForToken(oldId);
                    arc.removeAllTokenWeights(oldId);
                    arc.setWeight(newId, weight);
                }
            }
        }
    }

    /**
     *
     * @return a set of all component id's contained within this Petri net
     */
    public Set<String> getComponentIds() {
        Set<String> results = new HashSet<>();
        for(Map<String, ? extends PetriNetComponent> entry : componentMaps.values()) {
            results.addAll(entry.keySet());
        }
        return results;
    }

    /**
     *
     * @param id of the component
     * @return true if a component with the given id exists in the Petri net
     */
    public boolean contains(String id) {
        return getComponentIds().contains(id);
    }
}
