/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package org.vcell.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import edu.uchc.connjur.wb.ExecutionTrace;
/**
 * Insert the type's description here.
 * Creation date: (8/18/2000 2:29:31 AM)
 */
public final class BeanUtils {
	private static final Logger lg = LogManager.getLogger(BeanUtils.class);
	public static final String vcDateFormat = "dd-MMM-yyyy HH:mm:ss";
	public static final DateTimeFormatter vcDateTimeFormatter = DateTimeFormatter.ofPattern(vcDateFormat);

	/**
	 * newline used in email of Content-Type: text/plain
	 */
	public static final String PLAINTEXT_EMAIL_NEWLINE = "\r\n";

	public static final String FD_EXP_MESSG = "Didn't find field functions";
	
	
	private static VCellDynamicProps vcellDynamicProps = new VCellDynamicProps(null);
	public static class VCellDynamicProps {
		private final Map<String, String> dynamicClientProperties;

		private VCellDynamicProps(Map<String, String> dynamicClientProperties) {
			super();
			this.dynamicClientProperties = dynamicClientProperties;
		}
		public String getProperty(String propertyName) {
			return (this.dynamicClientProperties == null? null : this.dynamicClientProperties.get(propertyName));
		}
	}
	public static synchronized VCellDynamicProps getDynamicClientProperties() {
		return vcellDynamicProps;
	}
	public static synchronized void updateDynamicClientProperties() {
		Map<String, String> temp = new TreeMap<>();
		HttpsURLConnection conn;
		//"https://vcell.org/webstart/dynamicClientProperties.txt"

		try {
			//HttpURLConnection conn = new HttpURLConnection(new GetMethod(), new URL("http://dockerbuild:8080"));
			//https://vcell.org/webstart/vcell_dynamic_properties.csv
//			HttpURLConnection conn = (HttpURLConnection)(new URL("http://dockerbuild:8080")).openConnection();
			conn = (HttpsURLConnection)(new URL("https://vcell.org/webstart/vcell_dynamic_properties.csv")).openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
				Iterable<CSVRecord> records = CSVFormat.DEFAULT
//	          .withHeader(HEADERS)
//	          .withFirstRecordAsHeader()
						.withIgnoreSurroundingSpaces()
						.withQuote('"')
						.parse(br);
				for (CSVRecord record : records) {
					temp.put(record.get(0),record.get(1));
				}
				vcellDynamicProps = new VCellDynamicProps(temp);
			}
		} catch (IOException e){
			lg.error("Exception occurred while creating Connection of HTTPS: " + e.getMessage(), e);
			// We don't support throwing an exception...yet!
			//TODO: Allow this to throw exception!
		} catch (Exception e){
			lg.error("Unexpected error occurred while getting client properties: " + e.getMessage(), e);
			// We don't support throwing an exception...yet!
			//TODO: Allow this to throw exception!
		}
	}

	public static class XYZ {
		public double x;
		public double y;
		public double z;
		public XYZ(){
			x = 0;
			y = 0;
			z = 0;
		}
		public XYZ(double argX, double argY, double argZ){
			x = argX;
			y = argY;
			z = argZ;
		}
	}

	public static Range selectRange(boolean bAuto, boolean bAllTimes, Range rangeFromClient, Range currentVarAndTimeValRange) {
		if(bAuto && !bAllTimes) {// value range at each current time-point
			return currentVarAndTimeValRange;
		}
		// will have been set by user, custom range
		// or will have been calculated during sim run (PostProcessing), if bAlltimes selected
		return rangeFromClient;
	}

	public static <T> T[] addElement(T[] originalArray, T element) {
		T[] largerArray = Arrays.copyOf(originalArray, originalArray.length + 1);
		largerArray[originalArray.length] = element;
		return largerArray;
	}

	public static Serializable cloneSerializable(Serializable obj) throws ClassNotFoundException, java.io.IOException {
        return fromSerialized(toSerialized(obj));
	}

	public static byte[] compress(byte[] bytes) throws java.io.IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			try (DeflaterOutputStream dos = new DeflaterOutputStream(bos)){
				dos.write(bytes,0, bytes.length);
			}
			return bos.toByteArray();
		}
	}

	public static byte[] uncompress(byte[] compressedBytes) throws java.io.IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
		InflaterInputStream iis = new InflaterInputStream(bis);
		int temp;
		byte buf[] = new byte[65536]; // 64 KiB
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while((temp = iis.read(buf,0,buf.length)) != -1){
			bos.write(buf,0,temp);
		}
		iis.close();
		bis.close();
		return bos.toByteArray();
	}

	public static Serializable fromCompressedSerialized(byte[] objData) throws ClassNotFoundException, java.io.IOException {
		long before = 0, after;
		boolean traceIsEnabled = lg.isTraceEnabled();
		if (traceIsEnabled) before = System.currentTimeMillis();

		Serializable cacheClone;
		try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(objData)){
			try (InflaterInputStream iis = new InflaterInputStream(bis)){
				try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(iis)){
					cacheClone = (Serializable) ois.readObject();
				}
			}
		}

		if (traceIsEnabled) {
			after = System.currentTimeMillis();
			lg.trace(String.format("fromCompressedSerialized(): t=%d ms, (%s)", after - before, cacheClone.toString()));
		}
		return cacheClone;
	}

	public static Serializable fromSerialized(byte[] objData) throws ClassNotFoundException, IOException {
		long before = 0, after;
		if (lg.isTraceEnabled()) before = System.currentTimeMillis();

		Serializable cacheClone;
		try (ByteArrayInputStream bis = new ByteArrayInputStream(objData)) {
			try (ObjectInputStream ois = new ObjectInputStream(bis)) {
				cacheClone = (Serializable) ois.readObject();
			}
		}

		if (lg.isTraceEnabled()) {
			after = System.currentTimeMillis();
			lg.trace("fromSerialized(): t="+(after-before)+" ms, ("+cacheClone+")");
		}
		return cacheClone;
	}

	public static byte[] toCompressedSerialized(Serializable cacheObj) throws java.io.IOException {
		byte[] bytes = toSerialized(cacheObj);
		long before = System.currentTimeMillis();

		byte[] objData = null;
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(bos);
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(dos);
		oos.writeObject(cacheObj);
		oos.flush();
		dos.close();
		bos.flush();
		objData = bos.toByteArray();
		oos.close();
		bos.close();

		long after = System.currentTimeMillis();
		if (lg.isTraceEnabled()) lg.trace("toSerialized(), t="+(after-before)+" ms, ("+cacheObj+") ratio=" + objData.length + "/" + bytes.length);

		return objData;
	}

	public static byte[] toSerialized(Serializable cacheObj) throws java.io.IOException {
		long before = 0;
		if (lg.isTraceEnabled()) {
			before = System.currentTimeMillis();
		}

		byte[] objData = null;
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
		oos.writeObject(cacheObj);
		oos.flush();
		objData = bos.toByteArray();
		oos.close();
		bos.close();

		if (lg.isTraceEnabled()) {
			long after = System.currentTimeMillis();
			lg.trace("toSerialized, t="+(after-before)+" ms, ("+cacheObj+")");
		}

		return objData;
	}

	public static int coordinateToIndex(int[] coordinates, int[] bounds) {
		// computes linearized index of a position in an n-dimensional matrix
		// see also related method indexToCoordinate()
		if (coordinates.length != bounds.length) throw new RuntimeException("coordinates and bounds arrays have different lengths");
		int index = 0;
		for (int i = 0; i < bounds.length; i++){
			if (coordinates[i] < 0 || bounds[i] < coordinates[i]) throw new RuntimeException("invalid coordinate");
			int offset = 1;
			for (int j = i + 1; j < bounds.length; j++){
				offset *= bounds[j] + 1;
			}
			index += coordinates[i] * offset;
		}
		return index;
	}

	public static int[] indexToCoordinate(int index, int[] bounds) {
		// computes coordinates of a position in an n-dimensional matrix from linearized index
		// see also related method coordinateToIndex()
		if (index < 0) throw new RuntimeException("invalid index, negative number");
		int[] coordinates = new int[bounds.length];
		for (int i = 0; i < bounds.length; i++){
			int offset = 1;
			for (int j = i + 1; j < bounds.length; j++){
				offset *= bounds[j] + 1;
			}
			coordinates[i] = index / offset;
			if (coordinates[i] > bounds[i]) throw new RuntimeException("invalid index, number too large");
			index -= offset * coordinates[i];
		}
		return coordinates;
	}

	public static int firstIndexOf(double[] dd, double d) {
		if (dd == null) return -1;
		for (int i = 0; i < dd.length; i++){
			if (d == dd[i]) return i;
		}
		return -1;
	}

	public static String forceStringLength(String originalString, int targetLength, String pad, boolean shouldPrependPad) {
		if (targetLength < 0) throw new IllegalArgumentException("Size can not be negative");
		if(targetLength == 0) return "";

		String padString = pad == null || pad.isEmpty() ? " " : pad;
		StringBuilder fullPadding = new StringBuilder();

        fullPadding.append(padString.repeat(targetLength));

		// We just want a padding
		if(originalString == null || originalString.isEmpty()) return fullPadding.substring(0,targetLength);

		// We just want a substring
		if(targetLength <= originalString.length()) return originalString.substring(0,targetLength);
		// Todo: Add option to get end-substring using `shouldPrependPad = false`

		if(shouldPrependPad) return fullPadding.substring(0,targetLength - originalString.length()) + originalString;

		return originalString + fullPadding.substring(0,targetLength-originalString.length());
	}

	public static <T> T[] getArray(Enumeration<? extends T> enumeration, Class<T> elementType) {
		List<T> list = new ArrayList<T>();
		while (enumeration.hasMoreElements()){
			list.add(enumeration.nextElement());
		}
		@SuppressWarnings("unchecked")
		T[] array = list.toArray((T[])Array.newInstance(elementType, list.size()));
		return array;
	}

	public static <T> T[] addElements(T[] firstArray, T[] appendingArray) {
		T[] largerArray = Arrays.copyOf(firstArray, firstArray.length + appendingArray.length);
		System.arraycopy(appendingArray, 0, largerArray, firstArray.length, appendingArray.length);
		return largerArray;
	}

	public static <T> boolean arrayContains(T[] objects, T object) {
		if (object == null || objects == null) return false;
		for (T obj : objects) if (object.equals(obj)) return true;
		return false;
	}

	public static <T> boolean arrayEquals(T[] a1, T[] a2) {
		return a1 == a2 || (a1 != null && a2 != null && Arrays.equals(a1, a2));
	}

	/**
	 * @return list.toArray()
	 */
	public static <T> T[] getArray(List<?> list, Class<T> elementType) {
		@SuppressWarnings("unchecked")
		T[] array = list.toArray((T[])Array.newInstance(elementType, list.size()));
		return array;
	}

	public static <T> T[] removeFirstInstanceOfElement(T[] array, T element) {
		@SuppressWarnings("unchecked")
		T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
		try {
			for (int i = 0, j = 0; i < array.length; i++, j++){
				if (element.equals(array[i]) && i == j) { // i == j to prevent more than just first occurrence
					j--;
				} else {
					newArray[j] = array[i];
				}
			}
			return newArray;
		} catch (IndexOutOfBoundsException e){
			throw new RuntimeException("Error removing " + element + ": element not in object array");
			// Todo: consider explicitly throwing IndexOutOfBoundsException instead of implicit runtime exception.
		}
	}

	public static boolean triggersPropertyChangeEvent(Object a, Object b) {
		return (a == null || !a.equals(b));
	}

	public static String getStackTrace(Throwable throwable) {
		StringWriter out = new StringWriter();
		PrintWriter pw = new PrintWriter(out);
		Throwable t = throwable;
		while (t != null) {
			t.printStackTrace(pw);
			t = t.getCause( );
		}
		pw.close();
		return out.getBuffer().toString();
	}

	/**
	 * @return String for current stack trace
	 */
	public static String getStackTrace() {
		StackTraceElement[] stackTraceArray = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		//0 and 1 are "getStackTrace" -- start with callers invocation
		for (int i = 2; i < stackTraceArray.length; i++) {
			sb.append(stackTraceArray[i]);
			sb.append('\n');
		}
		return sb.toString();
	}
	/**
	 * recursive assemble exception message
	 * @param throwable
	 * @return {@link Throwable#getMessage()}, recursively
	 */
	public static String getMessageRecursive(Throwable throwable) {
		String rval = throwable.getMessage();
		Throwable cause = throwable.getCause();
		while (cause != null) {
			rval += " caused by " + cause.getMessage();
			cause = cause.getCause();
		}

		return rval;
	}


	public static void sendSMTP(String smtpHost, int smtpPort,String from, String to,String subject, String content)
	throws AddressException, MessagingException {

		// Create a mail session
		java.util.Properties props = new java.util.Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", ""+smtpPort);
		Session session = Session.getDefaultInstance(props, null);

		// Construct the message
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		msg.setSubject(subject);
		msg.setText(content);

		// Send the message
		Transport.send(msg);
	}

	public static String readBytesFromFile(File file, ClientTaskStatusSupport clientTaskStatusSupport) throws IOException {
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = new String();
			while((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + "\n");
				if (clientTaskStatusSupport!=null && clientTaskStatusSupport.isInterrupted()){
					break;
				}
			}
		}finally{
			if (bufferedReader != null){
				bufferedReader.close();
			}
		}
		return stringBuffer.toString();
	}


	private enum FLAG_STATE {START,FINISHED,INTERRUPTED,FAILED}
	/**
	 * download bytes from URL; optionally provide progress reports; time out after 2 minutes. Note
	 * returned string could be an error message from webserver
	 * @param url not null
	 * @param clientTaskStatusSupport could be null, in which case default status messages printed
	 * @return downloadedString
	 * @throws RuntimeException
	 */
	public static String downloadBytes(final URL url,final ClientTaskStatusSupport clientTaskStatusSupport){
		final ChannelFuture[] connectFuture = new ChannelFuture[1];
		final ChannelFuture[] closeFuture = new ChannelFuture[1];
		final Exception[] exception = new Exception[1];
		final FLAG_STATE[] bFlag = new FLAG_STATE[] {FLAG_STATE.START};
		final HttpResponseHandler responseHandler = new HttpResponseHandler(clientTaskStatusSupport,url.getHost());
		final Thread readBytesThread = new Thread(new Runnable() {
			ClientBootstrap bootstrap = null;

			public void run() {
				try {
					bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));

					// Set up the event pipeline factory.
					bootstrap.setPipelineFactory(new HttpClientPipelineFactory(responseHandler));
//					int connectionTimeout = 1000 * 60 * 5;
//					bootstrap.setOption("connectTimeoutMillis", connectionTimeout);

					// Start the connection attempt.
					int port = url.getPort();
					if (port <= 0) {
						port = 80;
					}

					String host = url.getHost();
					connectFuture[0] = bootstrap.connect(new InetSocketAddress(host, port));

					// Wait until the connection attempt succeeds or fails.
					connectFuture[0].awaitUninterruptibly();

					// Now we are sure the future is completed.
					// assert future.isDone();

					if (connectFuture[0].isCancelled()) {
						bFlag[0] = FLAG_STATE.INTERRUPTED;
						return;
					} else if (!connectFuture[0].isSuccess()) {
						connectFuture[0].getCause().printStackTrace();
						if (connectFuture[0].getCause() instanceof Exception){
							throw (Exception)connectFuture[0].getCause();
						}else{
							throw new RuntimeException(exceptionMessage(connectFuture[0].getCause()));
						}
					} else {
						// Connection established successfully
					}

					// Prepare the HTTP request.
					HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url.toURI().toASCIIString());
					request.setHeader(HttpHeaders.Names.HOST, host);
					request.setHeader(HttpHeaders.Names.CONNECTION,	HttpHeaders.Values.CLOSE);
					request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

					Channel channel = connectFuture[0].getChannel();

					// Send the HTTP request.
					channel.write(request);

					// Wait for the server to close the connection.
					closeFuture[0] = channel.getCloseFuture();

					closeFuture[0].awaitUninterruptibly();

					if (closeFuture[0].isCancelled()) {
						bFlag[0] = FLAG_STATE.INTERRUPTED;
						return;
					} else if (!closeFuture[0].isSuccess()) {
						closeFuture[0].getCause().printStackTrace();
						if (closeFuture[0].getCause() instanceof Exception){
							throw (Exception)closeFuture[0].getCause();
						}else{
							throw new RuntimeException(exceptionMessage(closeFuture[0].getCause()));
						}
					} else {
						// Connection established successfully
					}

					bFlag[0] = FLAG_STATE.FINISHED;

				} catch (Exception e) {
					e.printStackTrace();
					bFlag[0] = FLAG_STATE.FAILED;
					exception[0] = new RuntimeException("contacting outside server " + url.toString() + " failed.\n" + exceptionMessage(e));
				} finally {
					if (bootstrap != null) {
						// Shut down executor threads to exit.
						bootstrap.releaseExternalResources();
					}
				}
			}
		});
		readBytesThread.start();

		//Monitor content
		long maximumElapsedTime_ms = 1000*60*2;
		long startTime_ms = System.currentTimeMillis();
		while(true){
			try {
				Thread.sleep(100);
			} catch (Exception e) { }
			long elapsedTime_ms = System.currentTimeMillis()-startTime_ms;
			if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
				bFlag[0] = FLAG_STATE.INTERRUPTED;
				if (connectFuture[0]!=null){
					connectFuture[0].cancel();
				}
				if (closeFuture[0]!=null){
					closeFuture[0].cancel();
				}
				throw UserCancelException.CANCEL_GENERIC;
			}else if(elapsedTime_ms > maximumElapsedTime_ms){
				bFlag[0] = FLAG_STATE.INTERRUPTED;
				if (connectFuture[0]!=null){
					connectFuture[0].cancel();
				}
				if (closeFuture[0]!=null){
					closeFuture[0].cancel();
				}
				readBytesThread.interrupt();
				throw new RuntimeException("Download timed out after "+(maximumElapsedTime_ms/1000)+" seconds");
			}
			if(bFlag[0] == FLAG_STATE.FINISHED){//finished normally
				break;
			}
			if(bFlag[0] == FLAG_STATE.FAILED){//finished error
				if (connectFuture[0]!=null){
					connectFuture[0].cancel();
				}
				if (closeFuture[0]!=null){
					closeFuture[0].cancel();
				}
				if(exception[0] instanceof RuntimeException){
					throw (RuntimeException)exception[0];
				}
				throw new RuntimeException(exception[0]);
			}
		}
		return responseHandler.getResponseContent().toString();
	}
	
	/**
	 * download without status message; see {@link #downloadBytes(URL, ClientTaskStatusSupport)}
	 */
	public static String downloadBytesQuietly(final URL url) {
		return downloadBytes(url,new SilentTaskSupport());
	}
	
	private static class SilentTaskSupport implements ClientTaskStatusSupport {
		@Override
		public void setMessage(String message) { }
		@Override
		public void setProgress(int progress) { }
		@Override
		public int getProgress() {
			return 0;
		}
		@Override
		public boolean isInterrupted() {
			return false;
		}
		@Override public void addProgressDialogListener(ProgressDialogListener progressDialogListener) { }
	}

	public static String exceptionMessage(Throwable e){
		if (e.getMessage()!=null){
			return e.getMessage();
		}

		if (e.getCause()!=null){
			if (e.getCause().getMessage()!=null){
				return e.getCause().getMessage();
			}else{
				return e.getCause().getClass().getName();
			}
		}else{
			return e.getClass().getName();
		}
	}

	public static final String SQL_ESCAPE_CHARACTER = "/";
	public static String convertToSQLSearchString(String searchString){
		if(searchString == null || searchString.isEmpty()){return searchString;}
		String convertedLikeString = getConvertedLikeString(searchString);
		return convertedLikeString;
	}

	private static String getConvertedLikeString(String searchString) {
		//The character "%" matches any string of zero or more characters except null.
		//The character "_" matches any single character.
		//A wildcard character is treated as a literal if preceded by the character designated as the escape character.
		//Default ESCAPE character for VCell = '/' defined in DictionaryDbDriver.getDatabaseSpecies
		StringBuilder convertedLikeString = new StringBuilder();
		StringBuilder sb = new StringBuilder(searchString);
		for(int i=0;i<sb.length();i+= 1){
			if(sb.charAt(i) == '*'){
				convertedLikeString.append("%");//sql wildcard
			}else if(sb.charAt(i) == '%'){
				convertedLikeString.append(SQL_ESCAPE_CHARACTER + "%");// "%" special in sql 'like'
			}else if(sb.charAt(i) == '_'){
				convertedLikeString.append(SQL_ESCAPE_CHARACTER + "_");// "_" special in sql 'like'
			}else {
				convertedLikeString.append(sb.charAt(i));
			}
		}
		if(!convertedLikeString.toString().contains("%") && !convertedLikeString.toString().contains("_")){
			convertedLikeString = new StringBuilder("%" + convertedLikeString + "%");
		}
		return convertedLikeString.toString();
	}

	/**
	 * @param clzz
	 * @return class name without package info
	 * @throws NullPointerException if clzz null
	 */
	public static String leafName(Class<?> clzz) {
		String cname = clzz.getName();
		final int ldot = cname.lastIndexOf('.');
		if (ldot != - 1) {
			cname = cname.substring(ldot + 1);
		}
		return cname;
	}

	/**
	 * customize error message for null pointers
	 * @param clzz type of obj
	 * @param obj to check for null
	 * @return obj (not null)
	 * @throws NullPointerException if obj is null
	 */
	public static <T> T notNull(Class<T> clzz, T obj) {
		if (obj != null) {
			return obj;
		}
		throw new NullPointerException("unexpected " + clzz.getName() + " null pointer");
	}

	/**
	 * see if numerator / divisor is integer using decimal (base 10) arithmetic instead of
	 * floating point (base 2)
	 * @param numerator
	 * @param divisor
	 * @return true if decimal numerator / decimal divisor is integer
	 */
	public static boolean isIntegerMultiple(double numerator, double divisor) {
		BigDecimal n = BigDecimal.valueOf(numerator);
		BigDecimal d = BigDecimal.valueOf(divisor);
		BigDecimal remainder = n.remainder(d);
		return remainder.compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * convert ordinal to Enum
	 * @param clzz may not be null
	 * @param ordinal
	 * @return e with e.ordinal( ) == ordinal
	 * @throws IllegalArgumentException if ordinal out of range
	 */
	public static <E extends Enum<E> > E lookupEnum(Class<E> clzz, int ordinal) {
		EnumSet<E> set = EnumSet.allOf(clzz);
		if (ordinal < set.size()) {
			Iterator<E> iter = set.iterator();
			for (int i = 0; i < ordinal; i++) {
				iter.next();
			}
			E rval = iter.next();
			assert(rval.ordinal() == ordinal);
			return rval;
		}
		throw new IllegalArgumentException("Invalid value " + ordinal + " for " + ExecutionTrace.justClassName(clzz) + ", must be < " + set.size());
	}

	/**
	 * downcast to object or return null
	 * @param clzz return type, not null
	 * @param obj may be null
	 * @return obj as T or null if obj is null or not of type T
	 */
	public static <T> T downcast(Class<T> clzz, Object obj) {
		if (obj != null && clzz.isAssignableFrom(obj.getClass())) {
			@SuppressWarnings("unchecked")
			T rval = (T) obj;
			return rval;
		}
		return null;
	}

	public interface CastInfo <T> {
		/**
		 * was cast successful?
		 */
		boolean isGood( );
		/**
		 * return type converted object
		 * @return non null pointer
		 * @throws ProgrammingException if {@link #isGood()} returns false
		 */
		T get( );
		/**
		 * @return name of desired class if {@link #isGood()} returns false
		 */
		String requiredName( );
		/**
		 * @return name of provided object if {@link #isGood()} returns false
		 */
		String actualName( );

		/**
		 * @return message explaining cast
		 */
		String castMessage( );
	}

	/**
	 * attempt to class object to specified type
	 * @param clzz desired type, not null
	 * @param obj object to cast; if null returns !isGood( ) CastInfo<T>
	 * @return CastInfo<T> object describing results
	 */
	public static <T> CastInfo<T> attemptCast(Class<T> clzz, Object obj) {
		final String rname = clzz.getName();
		if (obj != null) {
			final String aname = obj.getClass().getName();
			T result = downcast(clzz, obj);
			if (result == null) {
				return new FailInfo<>(rname, aname);
			}
			return new SucceedInfo<>(rname,aname,result);
		}
		return new FailInfo<>(rname, "null");
	}

	private abstract static class CiBase<T> implements CastInfo<T> {
		final String rname;
		final String aname;
		protected CiBase(String rname, String aname) {
			this.rname = rname;
			this.aname = aname;
		}
		public String requiredName() { return rname; }
		public String actualName() { return aname; }
		public String castMessage( ) {
			return "cast from " + aname + " to " + rname;
		}
	}
	private static class FailInfo<T> extends CiBase<T> {

		FailInfo(String rname, String aname) {
			super(rname,aname);
		}
		public boolean isGood() {
			return false;
		}
		public T get() {
			String msg = "Programming exception, " + castMessage() + " failed";
			throw new ProgrammingException(msg);
		}
	}

	private static class SucceedInfo<T> extends CiBase<T> {
		final T obj;

		SucceedInfo(String rname,String aname, T obj) {
			super(rname,aname);
			this.obj = obj;
		}
		public boolean isGood() {
			return true;
		}
		public T get() {
			return obj;
		}
	}

	/**
	 * filter subtype out of a collection
	 * @param clzz non null
	 * @param coll non null
	 * @return list containing elements from coll of type clzz
	 */
	public static <T>  List<T> filterCollection(Class<T> clzz, Collection<?> coll) {
		Objects.requireNonNull(clzz);
		Objects.requireNonNull(coll);
		return coll.stream( )
		.filter(clzz::isInstance)
		.map(clzz::cast)
		.collect(Collectors.toList());
	}

	public static Range calculateValueDomain(double[] values,BitSet domainValid){
		double min=Double.POSITIVE_INFINITY;
		double max=Double.NEGATIVE_INFINITY;
		for (int i = 0; i < values.length; i++) {
			if((domainValid == null || domainValid.get(i) || domainValid.isEmpty()) && !Double.isNaN(values[i]) && !Double.isInfinite(values[i])){
			if(values[i] < min){min = values[i];}
			if(values[i] > max){max = values[i];}
			}
		}
		return new Range(min,max);
	}

	public static String generateDateTimeString(){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		String imageName =
		year+""+
		(month < 10?"0"+month:month)+""+
		(day < 10?"0"+day:day)+
		"_"+
		(hour < 10?"0"+hour:hour)+""+
		(min < 10?"0"+min:min)+""+
		(sec < 10?"0"+sec:sec);
	
		return imageName;
	}
}
