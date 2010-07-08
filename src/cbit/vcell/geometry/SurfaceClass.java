package cbit.vcell.geometry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Set;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;

public class SurfaceClass implements GeometryClass {
	private SubVolume subvolume1 = null;
	private SubVolume subvolume2 = null;
	private String name = null;
	private transient PropertyChangeSupport propertyChangeSupport = null;
	
	private transient PropertyChangeListener listener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals("name")){
				setName(createName(subvolume1.getName(),subvolume2.getName()));
			}
		}
		
	};

	public SurfaceClass(SubVolume subvolume1, SubVolume subvolume2) {
		super();
		this.subvolume1 = subvolume1;
		this.subvolume2 = subvolume2;
		this.name = createName(subvolume1.getName(), subvolume2.getName());
		subvolume1.addPropertyChangeListener(listener);
		subvolume2.addPropertyChangeListener(listener);
	}
	
	public static String createName(String subvolume1, String subvolume2){
		boolean bLess = subvolume1.compareTo(subvolume2) < 0;
		String name1 = bLess ? subvolume1 : subvolume2;
		String name2 = bLess ? subvolume2 : subvolume1;
		return TokenMangler.fixTokenStrict(name1 + "_" + name2 + "_membrane");
	}

	public Set<SubVolume> getAdjacentSubvolumes() {
		Set<SubVolume> set = new HashSet<SubVolume>();
		set.add(subvolume1);
		set.add(subvolume2);
		return set;
	}
	
	public boolean isAdjacentTo(SubVolume subVolume) {
		return (subvolume1 == subVolume || subvolume2 == subVolume);
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
			if (!Compare.isEqual(subvolume1,((SurfaceClass)obj).subvolume1)){
				return false;
			}
			if (!Compare.isEqual(subvolume2,((SurfaceClass)obj).subvolume2)){
				return false;
			}
			return true;
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
