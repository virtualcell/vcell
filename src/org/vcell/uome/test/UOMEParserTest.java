package org.vcell.uome.test;

import org.openrdf.model.Graph;
import org.sbpax.schemas.UOMEList;
import org.sbpax.util.StringUtil;
import org.sbpax.util.sets.SetOfOne;
import org.vcell.uome.UOMEObjectPools;
import org.vcell.uome.UOMEParser;
import org.vcell.uome.UOMETextUtil;
import org.vcell.uome.core.UOMEExpression;
import org.vcell.uome.core.UOMEUnit;

public class UOMEParserTest {

	public static void main(String[] args) {
		UOMEObjectPools pools = new UOMEObjectPools();
		UOMEParser.parseUOME(new SetOfOne<Graph>(UOMEList.schema), pools);
		long i = 0;
		for(UOMEUnit unit : pools.getUnitPool().getObjects()) {
			System.out.println("Unit " + (++i) + " :" + StringUtil.concat(unit.getNames(), ", ") 
					+ " - " + StringUtil.concat(unit.getSymbols(), ", "));
			for(UOMEExpression expression : unit.getExpressions()) {
				System.out.println("  Expression type : " + expression.getClass().getSimpleName() + "   " + 
						UOMETextUtil.buildString(expression, 10, false) + "   " +
						UOMETextUtil.buildString(expression, 10, true));
			}
		}
		// TODO Auto-generated method stub

	}

}
