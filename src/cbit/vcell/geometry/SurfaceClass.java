package cbit.vcell.geometry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
				setName(createName(subvolume1,subvolume2));
			}
		}
		
	};

	public SurfaceClass(SubVolume subvolume1, SubVolume subvolume2) {
		super();
		if (subvolume1.getHandle()>=subvolume2.getHandle()){
			throw new RuntimeException("SurfaceClass: handle of subVolume1 should be less than handle of subvolume2");
		}
		this.subvolume1 = subvolume1;
		this.subvolume2 = subvolume2;
		this.name = createName(subvolume1, subvolume2);
		subvolume1.addPropertyChangeListener(listener);
		subvolume2.addPropertyChangeListener(listener);
	}
	
	private static String createName(SubVolume subvolume1, SubVolume subvolume2){
		return TokenMangler.fixTokenStrict(subvolume1.getName()+"_"+subvolume2.getName());
	}

	public SubVolume getSubvolume1() {
		return subvolume1;
	}

	public SubVolume getSubvolume2() {
		return subvolume2;
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
