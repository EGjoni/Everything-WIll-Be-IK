package IK.floatIK;

import java.util.ArrayList;

import data.EWBIKLoader;
import data.EWBIKSaver;
import data.JSONObject;
import data.Saveable;
import sceneGraph.math.floatV.AbstractAxes;
import sceneGraph.math.floatV.SGVec_3f;

public abstract class AbstractIKPin implements Saveable {
	
	protected boolean isEnabled; 
	protected AbstractAxes axes;
	protected AbstractBone forBone;
	protected AbstractIKPin parentPin; 		
	protected ArrayList<AbstractIKPin> childPins = new ArrayList<>();
	float pinWeight  = 1;
	
	public AbstractIKPin() {
		
	}
	
	public AbstractIKPin(AbstractAxes inAxes, boolean enabled, AbstractBone bone) {
		this.isEnabled = enabled; 
		this.axes = inAxes;
		this.forBone = bone;
	}
	
	public AbstractIKPin(AbstractAxes inAxes, AbstractBone bone) {
		this.axes = inAxes;
		this.forBone = bone;
		this.isEnabled = false;
	}
	
	public boolean isEnabled() {
		return isEnabled; 
	}
	
	public void toggle() {
		if(this.isEnabled()) disable(); 
		else this.enable();
	}
	
	public void enable() {
		this.isEnabled = true; 
	}
	
	public void disable() {
		this.isEnabled = false;
	}
	
	
	
	public AbstractAxes getAxes() {
		return axes; 
	}

	/**
	 * translates and rotates the pin to match the position 
	 * and orientation of the input Axes. The orientation 
	 * is only relevant for orientation aware solvers.
	 * @param inAxes
	 */
	public void alignToAxes(AbstractAxes inAxes) {
		this.axes.alignGlobalsTo(inAxes);
		//Rot rotation = new Rot(axes.x().heading(), axes.y().heading(), inAxes.x().heading(), inAxes.y().heading());
	}
	
	/**
	 * translates the pin to the location specified in global coordinates
	 * @param location
	 */
	public void translateTo_(SGVec_3f location) {
		this.axes.translateTo(location);
	}
	
	/**
	 * translates the pin to the location specified in local coordinates 
	 * (relative to any other Axes objects the pin may be parented to)
	 * @param location
	 */
	public void translateBy_(SGVec_3f location) {
		this.axes.translateTo(location);
	}
	
	/**
	 * @return the pin locationin global coordinates
	 */
	public SGVec_3f getLocation_() {
		return axes.origin_();
	}
	
	
	
	
	public AbstractBone forBone() {
		return this.forBone;
		
	}

	/**
	 * called when this pin is being removed entirely from the Armature. (as opposed to just being disabled)
	 */
	public void removalNotification() {
		for(AbstractIKPin cp : childPins) 
			cp.setParentPin(getParentPin());
	}
	
	
	public void setParentPin(AbstractIKPin parent) {
		if(this.parentPin != null) {
			this.parentPin.removeChildPin(this);
		}
		//set the parent to the global axes if the user
		//tries to set the pin to be its own parent

		if(parent == this || parent == null) {
			this.axes.setParent(null);			
		} else if (parent != null){
			this.axes.setParent(parent.axes);	
			parent.addChildPin(this);
			this.parentPin = parent; 
		}
	}	
	


	public void solveIKForThisAndChildren() {
		
		try { 
			for(AbstractIKPin childPin : childPins) {
				childPin.solveIKForThisAndChildren();
			}
		this.forBone.solveIKFromHere(); 
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}


	public void removeChildPin(AbstractIKPin child) {
		childPins.remove(child);
	}

	public void addChildPin(AbstractIKPin newChild) {
		if(newChild.isAncestorOf(this)) {
			this.setParentPin(newChild.getParentPin());
		}
		if(!childPins.contains(newChild)) childPins.add(newChild);
	}

	public AbstractIKPin getParentPin() {
		return this.parentPin;
	}
	
	public boolean isAncestorOf(AbstractIKPin potentialDescendent) {
		boolean result = false;
		AbstractIKPin cursor = potentialDescendent.getParentPin();
		while(cursor != null) {
			if(cursor == this) {
				result = true; 
				break; 
			} else {
				cursor = cursor.parentPin; 
			}
		}		
		return result; 
	}
	
	
	public float getPinWeight() {
		return pinWeight;
	}
	
	
	/**
	 * Currently only works with tranquil solver. 
	 * 
	 * @param weight any positive number representing how much the IK solver
	 * should prefer to satisfy this pin over competing pins. For example, setting 
	 * one pin's weight to 90 and a competing pins weight to 10 will mean the IK solver 
	 * will prefer to satisfy the pin with a weight of 90 by as much as 9 times over satisfying 
	 * the pin with a weight of 10. 
	 * 
	 * 
	 */
	public void setPinWeight(float weight) {
		
	}
	
	@Override
	public void makeSaveable() {
		EWBIKSaver.addToSaveState(this);
		getAxes().makeSaveable();
	}
	
	@Override
	public JSONObject getSaveJSON() {
		JSONObject saveJSON = new JSONObject(); 
		saveJSON.setString("identityHash", this.getIdentityHash());
		saveJSON.setString("axes", getAxes().getIdentityHash()); 
		saveJSON.setString("forBone", forBone.getIdentityHash());
		saveJSON.setBoolean("isEnabled", this.isEnabled());
		saveJSON.setFloat("pinWeight", this.pinWeight);
		return saveJSON;
	}
	
	
	public void loadFromJSONObject(JSONObject j) {
		this.axes = (AbstractAxes) EWBIKLoader.getObjectFromClassMaps(this.getAxes().getClass(), j.getString("axes"));
		this.isEnabled = j.getBoolean("isEnabled"); 
		this.pinWeight = j.getFloat("pinWeight");
		this.forBone = (AbstractBone) EWBIKLoader.getObjectFromClassMaps(this.forBone().getClass(), j.getString("forBone")); 
		
	}

	
	@Override
	public void notifyOfSaveIntent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyOfSaveCompletion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLoading() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLoading(boolean loading) {
		// TODO Auto-generated method stub
		
	}
}