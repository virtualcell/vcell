package cbit.vcell.message.server.sim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

import cbit.vcell.simdata.PortableCommand;



/**
 * copy files matching beginning of sim name from one directory to another
 * @author gweatherby
 */
public class CopySimFiles implements PortableCommand, FileVisitor<Path> {
	private final String jobName;
	private final String fromDirectory;
	private final String toDirectory;
	private final String logName;
	/**
	 * transient to avoid capture by PortableCommand
	 */
	private transient FileSystem fs = null; 
	private transient Exception exc = null; 
	private transient PrintWriter pw = null;
	
	
	private static Logger lg = Logger.getLogger(CopySimFiles.class);

	public CopySimFiles(String jobName, String fromDirectory, String toDirectory, String logName) {
		this.jobName = jobName;
		this.fromDirectory = fromDirectory;
		this.toDirectory = toDirectory;
		this.logName = logName;
	}

	@Override
	public int execute() {
		try {
			pw = new PrintWriter(new FileWriter(logName,true)); //append to log, if it exists already
			try {
				fs = FileSystems.getDefault( );
				Path from = fs.getPath(fromDirectory);
				File toDir = new File(toDirectory);
				if (!toDir.exists()) {
					toDir.mkdir( );
					if (lg.isDebugEnabled()) {
						lg.debug("copying " + from + " to " + toDir + " (created)");
					}
				}
				else if(lg.isDebugEnabled()) {
					lg.debug("copying " + from + " to " + toDir + " (exists)");
				}
				Set<FileVisitOption> empty = Collections.emptySet();
				Files.walkFileTree(from, empty, 1, this);
				return 0;
			}
			finally {
				pw.close();
				pw = null;
			}
		} catch (IOException e) {
			exc = e;
			return 1;
		}
	}

	@Override
	public Exception exception() {
		return exc;
	}



	/**
	 * @return {@link FileVisitResult#CONTINUE 
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path p, IOException e) throws IOException {
		return FileVisitResult.CONTINUE; 
	}

	/**
	 * @return {@link FileVisitResult#CONTINUE 
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes arg1) throws IOException {
		return FileVisitResult.CONTINUE; 
	}

	/**
	 * copy file if matches {@link #jobName}
	 * @return {@link FileVisitResult#CONTINUE 
	 */
	@Override
	public FileVisitResult visitFile(Path p, BasicFileAttributes attr) throws IOException {
		Objects.requireNonNull(pw);
		String filename = p.getFileName().toString();
		if (lg.isDebugEnabled()) {
			lg.debug("evaluating " + filename);
		}
		if (filename.startsWith(jobName)) {
			Path destination = fs.getPath(toDirectory, filename);
			String report = "CopySimFiles copying "  + p + " to " + destination;
			pw.println(report);
			lg.debug(report);
			Files.copy(p, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		}
		return FileVisitResult.CONTINUE; 
	}

	/**
	 * rethrow exception
	 */
	@Override
	public FileVisitResult visitFileFailed(Path p, IOException e) throws IOException {
		throw e;
	}

}
