package org.vcell.util.gui.exporter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

import cbit.vcell.xml.XmlHelper;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.SesameRioUtil;
import org.vcell.sedml.ModelFormat;
import org.vcell.sedml.PubMet;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.FileUtils;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.PublicationInfo;

import cbit.util.xml.XmlRdfUtil;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class OmexExtensionFilter extends SedmlExtensionFilter {
	private static final String FNAMES = ".omex";

	public OmexExtensionFilter() {
		super(FNAMES, "COMBINE archive (.omex)", SelectorExtensionFilter.Selector.FULL_MODEL);
	}

	@Override
	public void writeBioModel(DocumentManager _documentManager, BioModel bioModel, File exportFile, SimulationContext _ignored) throws Exception {
		if (bioModel != null) {
			String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
			boolean bCreateOmexArchive = false;
			if (sExt.equals("omex")) {
				bCreateOmexArchive = true;
			}
			boolean bRoundTripSBMLValidation = true;
			boolean bHasPython = false;
			Optional<PublicationInfo> publicationInfo = Optional.empty();
			if (bioModel.getVersion()!=null && bioModel.getVersion().getVersionKey()!=null){
				BioModelInfo bioModelInfo = _documentManager.getBioModelInfo(bioModel.getVersion().getVersionKey());
				if (bioModelInfo != null && bioModelInfo.getPublicationInfos()!=null && bioModelInfo.getPublicationInfos().length>0){
					publicationInfo = Optional.of(bioModelInfo.getPublicationInfos()[0]);
				}
			}
			SEDMLExporter.writeBioModel(bioModel, publicationInfo, exportFile, modelFormat, bHasPython, bRoundTripSBMLValidation, bCreateOmexArchive);
		}
	}
}
