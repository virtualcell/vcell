/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.quicktime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ArrayUtils;

import cbit.vcell.export.gloworm.atoms.MediaData;
import cbit.vcell.export.gloworm.atoms.SampleSize;
import cbit.vcell.export.gloworm.atoms.SampleTableDescription;
import cbit.vcell.export.gloworm.atoms.TrackAtom;
import cbit.vcell.export.gloworm.atoms.TrackHeader;
import cbit.vcell.export.gloworm.atoms.VideoSampleDescriptionEntryRaw;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.DataInputStream;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (11/5/2005 12:03:57 PM)
 * @author: Ion Moraru
 */
public class AtomReader {
	private final static Logger lg = LogManager.getLogger(AtomReader.class);

	private final String fileName;
	private DefaultMutableTreeNode rootNode = null;
	Hashtable<AtomInfo, DefaultMutableTreeNode> nodeHash = new Hashtable<>(); // key = AtomInfo; element = TreeNode
	private DataInputStream din = null;
	public static final String[] containerAtoms = new String[] {
		"moov",
		"trak",
		"edts",
		"tref",
		"mdia",
		"minf",
		"dinf",
		"stbl",
		"gmhd"
	};
	public static class AtomInfo {
		private int offset = 0;
		private int size = 0;
		private String type = "";
		private AtomInfo(int argOffset, int argSize, String argType) {
			offset = argOffset;
			size = argSize;
			type = argType;
		}
		public String toString() {
			return type+" "+Integer.toHexString(offset)+"--"+Integer.toHexString(offset+size-1)+" "+size;
		}
	}

	public AtomReader(String fileName) {
		this.fileName = fileName;
	}


	public AtomInfo[] getAtoms(String atomType) throws java.io.IOException {
		Enumeration<AtomInfo> en = nodeHash.keys();
		Vector<AtomInfo> v = new Vector<>();
		while (en.hasMoreElements()) {
			AtomInfo info = en.nextElement();
			if (info.type.equals(atomType)) {
				v.addElement(info);
			}
		}
		return v.toArray(AtomInfo[]::new);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 1:02:30 PM)
	 * @return int
	 */
	public short getColorDepth(int trackNumber) throws java.io.IOException {
		AtomInfo[] stsd = getAtoms(SampleTableDescription.type);
		java.io.File f = new java.io.File(fileName);
		din = new java.io.DataInputStream(new java.io.FileInputStream(f));
		din.skip(stsd[trackNumber - 1].offset + 16); // go to first entry
		int sampleSize = din.readInt();
		byte[] b4 = new byte[4];
		din.read(b4);
		String sampleType = new String(b4);
		if (sampleSize != VideoSampleDescriptionEntryRaw.SIZE ||
			!sampleType.equals(VideoSampleDescriptionEntryRaw.DATA_FORMAT)
			)
		{
			throw new RuntimeException("Only raw video media sample entry descriptions are supported");
		}
		din.skip(74);
		short colorDepth = din.readShort();
		din.close();
		return colorDepth;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 1:01:16 PM)
	 * @return int
	 */
	public int getDataLength() throws java.io.IOException {
		AtomInfo[] mdat = getAtoms(MediaData.type);
		return mdat[0].size - 8;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 1:00:09 PM)
	 * @return long
	 */
	public long getDataOffset() throws java.io.IOException {
		AtomInfo[] mdat = getAtoms(MediaData.type);
		return mdat[0].offset + 8;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 12:59:28 PM)
	 * @return int
	 */
	public int getHeight(int trackNumber) throws java.io.IOException {
		AtomInfo[] tkhd = getAtoms(TrackHeader.type);
		java.io.File f = new java.io.File(fileName);
		din = new java.io.DataInputStream(new java.io.FileInputStream(f));
		din.skip(tkhd[trackNumber - 1].offset + tkhd[trackNumber - 1].size - 4);
		int height = din.readShort();
		din.close();
		return height;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 12:58:36 PM)
	 * @return int
	 */
	public int getNumberOfTracks() throws java.io.IOException {
		return getAtoms(TrackAtom.type).length;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 12:59:28 PM)
	 * @return int
	 */
	public int getSampleNumber(int trackNumber) throws java.io.IOException {
		AtomInfo[] stsz = getAtoms(SampleSize.type);
		java.io.File f = new java.io.File(fileName);
		din = new java.io.DataInputStream(new java.io.FileInputStream(f));
		din.skip(stsz[trackNumber - 1].offset + 16);
		int sampleNumber = din.readInt();
		din.close();
		return sampleNumber;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/27/2005 12:59:28 PM)
	 * @return int
	 */
	public int getWidth(int trackNumber) throws java.io.IOException {
		AtomInfo[] tkhd = getAtoms(TrackHeader.type);
		java.io.File f = new java.io.File(fileName);
		din = new java.io.DataInputStream(new java.io.FileInputStream(f));
		din.skip(tkhd[trackNumber - 1].offset + tkhd[trackNumber - 1].size - 8);
		int width = din.readShort();
		din.close();
		return width;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2005 12:35:50 PM)
	 * @return boolean
	 * @param atomType java.lang.String
	 */
	public boolean isContainerAtom(String atomType) {
		return ArrayUtils.arrayContains(containerAtoms, atomType);
	}


	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {
		// Insert code to start the application here.
		try {
			AtomReader reader1 = new AtomReader(args[0]);
			AtomReader reader2 = args.length > 1 ? new AtomReader(args[1]) : null;
			reader1.readAtoms();
			if (reader2 != null) reader2.readAtoms();
			javax.swing.JFrame  frame = showTree(reader1, reader2);
			frame.pack();
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosed(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2005 7:52:24 PM)
	 */
	public void printSelectionDetails(AtomInfo info) {
		try {
			java.io.File f = new java.io.File(fileName);
			din = new java.io.DataInputStream(new java.io.FileInputStream(f));
			int offset = info.offset + 8;
			din.skipBytes(offset);
			byte[] bytes = new byte[info.size - 8];
			din.read(bytes);
			System.out.println(info);
			String s = "";
			for (int i = 0; i < bytes.length; i++){
				s += " " + Integer.toHexString(bytes[i] < 0 ? bytes[i]+256 : bytes[i]);
			}
			System.out.println(s);
			din.close();
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2005 12:07:18 PM)
	 * @return int
	 */
	public void readAtoms() throws java.io.IOException {
		AtomInfo[] rootAtoms = readRootAtoms();
		rootNode = new DefaultMutableTreeNode(fileName);
        for (AtomInfo atom : rootAtoms) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(atom);
            rootNode.add(treeNode);
            nodeHash.put(atom, treeNode);
        }
		Vector<AtomInfo> contAtoms = new Vector<>();
        for (AtomInfo rootAtom : rootAtoms) {
            if (isContainerAtom(rootAtom.type)) {
                contAtoms.add(rootAtom);
            }
        }
		while (!contAtoms.isEmpty()) {
			Vector<AtomInfo> moreContAtoms = new Vector<>();
			Enumeration<AtomInfo> en = contAtoms.elements();
			while (en.hasMoreElements()) {
				AtomInfo obj = en.nextElement();
                AtomInfo[] childAtoms = readChildAtoms(obj);
                for (AtomInfo childAtom : childAtoms) {
                    DefaultMutableTreeNode parentNode = nodeHash.get(obj);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childAtom);
                    parentNode.add(childNode);
                    nodeHash.put(childAtom, childNode);
                    if (isContainerAtom(childAtom.type)) {
                        moreContAtoms.add(childAtom);
                    }
                }
			}
			contAtoms.clear();
			contAtoms.addAll(moreContAtoms);
		}
		readSampleDetails();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2005 12:07:18 PM)
	 * @return int
	 */
	public AtomReader.AtomInfo[] readChildAtoms(AtomReader.AtomInfo parentInfo) throws java.io.IOException {
		java.io.File f = new java.io.File(fileName);
		din = new java.io.DataInputStream(new java.io.FileInputStream(f));
		int offset = parentInfo.offset + 8;
		din.skipBytes(offset);
		AtomInfo[] childAtoms = new AtomInfo[0];
		while (offset < parentInfo.offset + parentInfo.size) {
			AtomInfo info = readOneAtomSig(offset);
			din.skipBytes(info.size - 8);
			offset += info.size;
			childAtoms = ArrayUtils.addElement(childAtoms, info);
		}
		din.close();
		return childAtoms;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2005 12:23:09 PM)
	 */
	public AtomInfo readOneAtomSig(int offset) throws java.io.IOException {
		int size = din.readInt();
		byte[] b4 = new byte[4];
		din.read(b4);
		String type = new String(b4);
		AtomInfo info = new AtomInfo(offset, size, type);
		System.out.println(info);
		return info;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2005 12:07:18 PM)
	 * @return int
	 */
	public AtomReader.AtomInfo[] readRootAtoms() throws java.io.IOException {
		java.io.File f = new java.io.File(fileName);
		din = new java.io.DataInputStream(new java.io.FileInputStream(f));
		int offset = 0;
		AtomInfo[] rootAtoms = new AtomInfo[0];
		while (offset < f.length()) {
			AtomInfo info = readOneAtomSig(offset);
			din.skipBytes(info.size - 8);
			offset += info.size;
			rootAtoms = ArrayUtils.addElement(rootAtoms, info);
		}
		din.close();
		return rootAtoms;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/5/2005 12:07:18 PM)
	 */
	public void readSampleDetails() throws java.io.IOException {
		AtomInfo[] infos = Collections.list(nodeHash.keys()).toArray(AtomInfo[]::new);
		Comparator<AtomInfo> infoComp = Comparator.comparingInt(o -> o.offset);
		Arrays.sort(infos, infoComp);
        for (AtomInfo info : infos) {
            if (info.type.equals("stsc")) {
                java.io.File f = new java.io.File(fileName);
                din = new DataInputStream(new java.io.FileInputStream(f));
                din.skipBytes(info.offset + 12);
                int entries = din.readInt();
                System.out.println("stsc table");
                for (int i = 0; i < entries; i++) {
                    System.out.println(din.readInt() + " " + din.readInt() + " " + din.readInt());
                }
                din.close();
            }
            if (info.type.equals("stsz")) {
                java.io.File f = new java.io.File(fileName);
                din = new DataInputStream(new java.io.FileInputStream(f));
                din.skipBytes(info.offset + 12);
                int size = din.readInt();
                int entries = din.readInt();
                System.out.println("stsz table");
                if (size != 0) {
                    System.out.println(entries + " of size " + size);
                } else {
                    for (int i = 0; i < entries; i++) {
                        System.out.println(din.readInt());
                    }
                }
                din.close();
            }
            if (info.type.equals("stco")) {
                java.io.File f = new java.io.File(fileName);
                din = new DataInputStream(new java.io.FileInputStream(f));
                din.skipBytes(info.offset + 12);
                int entries = din.readInt();
                System.out.println("stco table");
                for (int i = 0; i < entries; i++) {
                    System.out.println(Integer.toHexString(din.readInt()));
                }
                din.close();
            }
        }
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (11/6/2005 12:25:52 AM)
	 */
	public static javax.swing.JFrame showTree(final AtomReader a1, final AtomReader a2) {
		javax.swing.tree.DefaultTreeModel treeModel1 = a1 == null ? null : new javax.swing.tree.DefaultTreeModel(a1.rootNode);
		javax.swing.tree.DefaultTreeModel treeModel2 = a2 == null ? null : new javax.swing.tree.DefaultTreeModel(a2.rootNode);
		final javax.swing.JTree tree1 = new javax.swing.JTree(treeModel1);
		final javax.swing.JTree tree2 = new javax.swing.JTree(treeModel2);
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(tree1, java.awt.BorderLayout.WEST);
		frame.getContentPane().add(tree2, java.awt.BorderLayout.EAST);
		javax.swing.event.TreeSelectionListener tcl = new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
				if (e.getSource().equals(tree1)) {
					javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
					a1.printSelectionDetails((AtomInfo)node.getUserObject());
				}
				if (e.getSource().equals(tree2)) {
					javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
					a2.printSelectionDetails((AtomInfo)node.getUserObject());
				}
			}
		};
		tree1.addTreeSelectionListener(tcl);
		tree2.addTreeSelectionListener(tcl);
		return frame;
	}
}
