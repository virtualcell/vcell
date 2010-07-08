package cbit.vcell.geometry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;

public class SurfaceClass implements GeometryClass {
	private Set<SubVolume> subvolumes = null;
	private String name = null;
	private transient PropertyChangeSupport propertyChangeSupport = null;
	
	private transient PropertyChangeListener listener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("name")){
				setName(createName());
			}
		}
	};

	public SurfaceClass(Set<SubVolume> arg_subvolumes) {
		super();
		if (arg_subvolumes.size() != 2) {
			throw new RuntimeException("SurfaceClass should have only 2 adjacent subvolumes");
		}
		subvolumes = new HashSet<SubVolume>();
		subvolumes.addAll(arg_subvolumes);
		this.name = createName();
		for (SubVolume sv : subvolumes) {
			if (sv == null) {
				throw new RuntimeException("SurfaceClass has a null adjacent subvolume");
			}
			sv.addPropertyChangeListener(listener);
		}
	}
	
	private String createName() {
		Iterator<SubVolume> iter = subvolumes.iterator();
		return createName(iter.next().getName(), iter.next().getName());
	}
	
	public static String createName(String subvolume1, String subvolume2){
		boolean bLess = subvolume1.compareTo(subvolume2) < 0;
		String name1 = bLess ? subvolume1 : subvolume2;
		String name2 = bLess ? subvolume2 : subvolume1;
		return TokenMangler.fixTokenStrict(name1 + "_" + name2 + "_membrane");
	}

	public Set<SubVolume> getAdjacentSubvolumes() {
		return Collections.unmodifiableSet(subvolumes);
	}
	
	public boolean isAdjacentTo(SubVolume subVolume) {
		return subvolumes.contains(subVolume);
	}
	
	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
	
	public void setName(String newName){
		String oldName = this.name;
		this.name = newName;
		getPropertyChangeSupport().firePropertyChange("name", oldName, newName);
	}

	public boolean compareEqual(Matchable obj) {
		if (obj instanceof SurfaceClass){
			SurfaceClass sc = (SurfaceClass)obj;
			if (subvolumes.containsAll(sc.subvolumes) && sc.subvolumes.containsAll(subvolumes)) {
				return true;
			}
		}
		return false;
	}
	
	public String getName() {
		return name;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

}
