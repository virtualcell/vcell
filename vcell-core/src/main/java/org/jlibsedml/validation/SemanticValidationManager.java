package org.jlibsedml.validation;
/*
 *    Copyright 2009 Richard Adams

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jlibsedml.SedML;
import org.jlibsedml.SedMLError;

/**
 * Main point of access for validating the model for semantic consistency.
 * @author radams
 *
 */
  class SemanticValidationManager implements ISedMLValidator{
	private SedML sedml;
	private Document doc;
	public SemanticValidationManager(SedML sedml, Document doc) {
		this.sedml=sedml;
		this.doc=doc;
	}

	public  List<SedMLError> validate () {
		List<SedMLError> errs = new ArrayList<SedMLError>();
		errs.addAll(new KisaoIDValidator(sedml.getSimulations(),doc).validate());
		errs.addAll(new URIValidator(sedml.getModels()).validate());
		return errs;
		
	}

}
