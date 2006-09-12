package cbit.rmi.event;
/**
 * Insert the type's description here.
 * Creation date: (6/19/2006 11:07:01 AM)
 * @author: Fei Gao
 */
public interface VCellMessageEventListener extends java.util.EventListener {
void onVCellMessageEvent(VCellMessageEvent event);
}