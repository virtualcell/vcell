package org.bouncycastle.cert.ocsp;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.cert.CertIOException;

public class OCSPResp
{
    private OCSPResponse    resp;

    public OCSPResp(
        OCSPResponse    resp)
    {
        this.resp = resp;
    }

    public OCSPResp(
        byte[]          resp)
        throws IOException
    {
        this(new ASN1InputStream(resp));
    }

    private OCSPResp(
        ASN1InputStream aIn)
        throws IOException
    {
        try
        {
            this.resp = OCSPResponse.getInstance(aIn.readObject());
        }
        catch (IllegalArgumentException e)
        {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        }
        catch (ClassCastException e)
        {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        }
        catch (ASN1Exception e)
        {
            throw new CertIOException("malformed response: " + e.getMessage(), e);
        }
    }

    public int getStatus()
    {
        return this.resp.getResponseStatus().getValue().intValue();
    }

    public Object getResponseObject()
        throws OCSPException
    {
        ResponseBytes   rb = this.resp.getResponseBytes();

        if (rb == null)
        {
            return null;
        }

        if (rb.getResponseType().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic))
        {
            try
            {
                ASN1Primitive obj = ASN1Primitive.fromByteArray(rb.getResponse().getOctets());
                return new BasicOCSPResp(BasicOCSPResponse.getInstance(obj));
            }
            catch (Exception e)
            {
                throw new OCSPException("problem decoding object: " + e, e);
            }
        }

        return rb.getResponse();
    }

    /**
     * return the ASN.1 encoded representation of this object.
     */
    public byte[] getEncoded()
        throws IOException
    {
    	return resp.getEncoded();
    }
    
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        
        if (!(o instanceof OCSPResp))
        {
            return false;
        }
        
        OCSPResp r = (OCSPResp)o;
        
        return resp.equals(r.resp);
    }
    
    public int hashCode()
    {
        return resp.hashCode();
    }
}
