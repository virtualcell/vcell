/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

/**
 * One BioModels Database entry as the desktop's BMDB tab sees it, parsed from
 * {@code vcell-client/src/main/resources/bioModelsNetInfo.xml}.
 *
 * <p><b>What that file is.</b> A <i>prediction</i>, shipped with the client and read at
 * runtime, of which BioModels archives VCell will manage to <b>open</b>. The tab shows a
 * curated icon for the ones it expects to work and a warning icon for the rest, so a user is
 * not sent into an import that cannot succeed.
 *
 * <p><b>It predicts opening, not running.</b> Selecting a model takes the ordinary
 * document-open path; the tab never simulates. So an archive that imports cleanly and then
 * fails in the solver is still {@code Supported} -- correctly, because the tab does not get
 * that far. Around seventy currently fall in that group.
 *
 * <p><b>Where the answer comes from.</b> Not from here, and not by hand. It is derived from
 * the nightly execution baseline, {@code vcell-cli/src/main/resources/test_cases.ndjson}
 * (see {@code org.vcell.sedml.testsupport.OmexTestCase}), by treating the failure types that
 * prevent an import as "will not open". {@code BioModelsNetInfoTest} fails when this file and
 * that one disagree, and regenerates it on request -- the list was hand-maintained until
 * 2026-08 and had drifted badly, flagging 55 models incompatible that VCell opens perfectly
 * well.
 *
 * <p><b>Two attributes in the XML are not represented here.</b> {@code exception=}, a legacy
 * per-model reason carried by about a tenth of the rows, is read by nothing -- so the tab can
 * say that a model is not compatible but not why. {@code Slow="true"}, on three rows, is read
 * by the parsers and forces the model to be treated as unsupported; it predates the nightly's
 * own skip list and was never reconciled with it.
 */
public class BioModelsNetModelInfo {
	private String id;
	private String name;
	private String link;
	/** Whether the client expects this archive to open. See the class comment for what decides it. */
	private boolean supported;
	
	public BioModelsNetModelInfo(String id, String name, String link) {
		this(id, name, link, true);
	}
	public BioModelsNetModelInfo(String id, String name, String link, boolean supported) {
		super();
		this.id = id;
		this.name = name;
		this.link = link;
		this.supported = supported;
	}
	public final String getId() {
		return id;
	}
	public final String getName() {
		return name;
	}
	public final String getLink() {
		return link;
	}
	public final boolean isSupported() {
		return supported;
	}
	
}
