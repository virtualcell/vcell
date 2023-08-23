package org.vcell.s3;

import org.gaul.s3proxy.S3Proxy;
import org.gaul.shaded.org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.config.LocalBlobStore;
import org.jclouds.filesystem.reference.FilesystemConstants;

import java.net.URI;
import java.util.Properties;

public class S3ProxyVCell {

    public static void main(String[] args) {
        Properties properties = new Properties();
        String tmp = "/media/zeke/DiskDrive/Home/Work/CCAM/TempStorage";
        properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, tmp);

        BlobStore blobStore;

//        BlobStoreContext localBlobStore = ContextBuilder.newBuilder("filesystem").overrides(properties).build(BlobStoreContext.class);

        BlobStoreContext blobStoreContext = ContextBuilder
                .newBuilder("filesystem")
                .overrides(properties)
                .buildView(BlobStoreContext.class);
        // error
//        https://jclouds.apache.org/reference/javadoc/2.0.x/org/jclouds/blobstore/BlobStore.html

        S3Proxy s3Proxy = S3Proxy.builder()
                .blobStore(blobStoreContext.getBlobStore())
                .endpoint(URI.create("http://127.0.0.1:8080"))
                .servicePath(tmp)
                .build();

        try {
            s3Proxy.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
