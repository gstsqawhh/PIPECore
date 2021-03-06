package uk.ac.imperial.pipe.models;

import org.junit.Before;
import org.junit.Test;
import uk.ac.imperial.pipe.models.petrinet.AbstractArc;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.AbstractConnectable;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConnectableTest {
    private static final double DOUBLE_DELTA = 0.001;

    private Connectable connectable;

    @Before
    public void setUp() {
        connectable = new DummyConnectable("test", "test");
    }

    @Test
    public void notifiesObserversOnXChange() {
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        connectable.addPropertyChangeListener(mockListener);
        connectable.setX(10);
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void defaultNameOffsetValues() {
        assertEquals(-5, connectable.getNameXOffset(), DOUBLE_DELTA);
        assertEquals(35, connectable.getNameYOffset(), DOUBLE_DELTA);
    }

    @Test
    public void notifiesObserversOnNameChange() {
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        connectable.addPropertyChangeListener(mockListener);
        connectable.setName("");
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void notifiesObserversOnIdChange() {
        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        connectable.addPropertyChangeListener(mockListener);
        connectable.setId("");
        verify(mockListener).propertyChange(any(PropertyChangeEvent.class));
    }

    private class DummyConnectable extends AbstractConnectable {

        DummyConnectable(String id, String name) {
            super(id, name);
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public Point2D.Double getCentre() {
            return new Point2D.Double(0, 0);
        }

        @Override
        public Point2D.Double getArcEdgePoint(double angle) {
            return new Point2D.Double(0, 0);
        }

        @Override
        public boolean isEndPoint() {
            return true;
        }

        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public boolean isDraggable() {
            return false;
        }

        @Override
        public void accept(PetriNetComponentVisitor visitor) {

        }
    }


}
