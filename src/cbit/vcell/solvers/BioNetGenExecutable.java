package cbit.vcell.solvers;

public class BioNetGenExecutable extends MathExecutable {

	public BioNetGenExecutable(String[] command) {
		super(command);
	}

	@Override
	protected void setOutputString(String newOutputString) {
		if(outputString != null && newOutputString != null) {
			if(newOutputString.length() > outputString.length()) {
				String delta = newOutputString.substring(outputString.length());
				System.out.println(delta);
			}
		}
		super.setOutputString(newOutputString);
		checkForNewApplicationMessages(getStdoutString());
	}
	@Override
	protected void checkForNewApplicationMessages(String str) {
		//   "Iteration msg rxns\n"
		String START_TOKEN = "Iteration ";
		String END_TOKEN = " rxns\n";
		boolean bDone = false;
		while (!bDone){
			int nextMsgBegin = str.indexOf(START_TOKEN,currentStringPosition);
			int nextMsgEnd = str.indexOf(END_TOKEN,currentStringPosition);
			if (nextMsgBegin>=0 && nextMsgEnd > nextMsgBegin){
				String msg = str.substring(nextMsgBegin+START_TOKEN.length(),nextMsgEnd);
				setApplicationMessage(msg);
				currentStringPosition = nextMsgEnd+END_TOKEN.length();
			}else{
				bDone = true;
			}
		}
	}
}
