package uk.ac.imperial.pipe.io;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import uk.ac.imperial.pipe.io.adapters.modelAdapter.ArcAdapter;
import uk.ac.imperial.pipe.io.adapters.modelAdapter.PlaceAdapter;
import uk.ac.imperial.pipe.io.adapters.modelAdapter.RateParameterAdapter;
import uk.ac.imperial.pipe.io.adapters.modelAdapter.TokenAdapter;
import uk.ac.imperial.pipe.io.adapters.modelAdapter.TokenSetIntegerAdapter;
import uk.ac.imperial.pipe.io.adapters.modelAdapter.TransitionAdapter;
import uk.ac.imperial.pipe.models.PetriNetHolder;
import uk.ac.imperial.pipe.models.petrinet.ColoredToken;
import uk.ac.imperial.pipe.models.petrinet.FunctionalRateParameter;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Token;
import uk.ac.imperial.pipe.models.petrinet.Transition;

public class ProofChainIOImpl {
    /**
     * JAXB context initialised in constructor
     */
    private final JAXBContext context;
    
	private Unmarshaller unmarshaller;

    protected PetriNetValidationEventHandler petriNetValidationEventHandler;

	
	public ProofChainIOImpl() throws JAXBException{
    	context = JAXBContext.newInstance(PetriNetHolder.class);
	}
	
    public void writeTo(String path, PetriNet petriNet) throws JAXBException, IOException {
		writeTo(new FileWriter(new File(path)), petriNet);
    }
    
     public void writeTo(Writer stream, PetriNet petriNet) throws JAXBException {
        Marshaller m = context.createMarshaller();
        m.setEventHandler(getEventHandler()); 
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        PetriNetHolder holder = getPetriNetHolder();
        holder.addNet(petriNet);
        try {
        	m.marshal(holder, stream);
        	getEventHandler().printMessages(); 
		} catch (JAXBException e) {
			getEventHandler().printMessages(); 
			throw e; 
		}
    }

	protected PetriNetHolder getPetriNetHolder() {
		return new PetriNetHolder();
	}

    /**
     * Reads a Petri net from the given path
     *
     * @param path xml path containing a PNML representation of a Petri net
     * @return read Petri net
     */
     public PetriNet read(String path) throws JAXBException, FileNotFoundException {
        initialiseUnmarshaller();
        getUnmarshaller().setEventHandler(getEventHandler()); 
        PetriNetHolder holder = null; 
        try {
        	holder = (PetriNetHolder) getUnmarshaller().unmarshal(new FileReader(path));
        	getEventHandler().printMessages(); 
		} catch (JAXBException e) {
			getEventHandler().printMessages(); 
			throw e;  
		} 
        PetriNet petriNet = holder.getNet(0);
        if (petriNet.getTokens().isEmpty()) {
            Token token = createDefaultToken();
            petriNet.addToken(token);
        }
        return petriNet;
    }

    /**
     * initialize unmarshaller with the correct adapters needed
     * @throws JAXBException if Petri net cannot be unmarshalled 
     */
    protected void initialiseUnmarshaller() throws JAXBException {

        unmarshaller = context.createUnmarshaller();
        Map<String, Place> places = new HashMap<>();
        Map<String, Transition> transitions = new HashMap<>();
        Map<String, Token> tokens = new HashMap<>();
        Map<String, FunctionalRateParameter> rateParameters = new HashMap<>();

        unmarshaller.setAdapter(new RateParameterAdapter(rateParameters));
        unmarshaller.setAdapter(new ArcAdapter(places, transitions));
        unmarshaller.setAdapter(new PlaceAdapter(places));
        unmarshaller.setAdapter(new TransitionAdapter(transitions, rateParameters));
        unmarshaller.setAdapter(new TokenAdapter(tokens));
        unmarshaller.setAdapter(new TokenSetIntegerAdapter(tokens));
    }
 

    /**
     * @return a new default token
     */
    private Token createDefaultToken() {
        return new ColoredToken("Default", new Color(0, 0, 0));
    }

	protected final Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	protected PetriNetValidationEventHandler getEventHandler() {
		return petriNetValidationEventHandler;
	}
    
}
