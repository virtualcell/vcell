package org.vcell.sbmlsim.api.common;

public enum SimulationState{
  notRequestedYet,
  newRequest,
  accepted,
  running,
  done,
  failed;

}
