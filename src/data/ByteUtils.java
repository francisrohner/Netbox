package data;

import java.io.*;
import java.net.SocketException;

/**
 * Created by Frank on 6/18/2017.
 */
public class ByteUtils
{
    private static byte[] EOT_DATA = (new String("<<EOT>>")).getBytes();
    private static byte[] DEBUG_DATA = (new String("TestMessage<<EOT>>")).getBytes();

    private static byte BYTE_FILE = 1;
    private static byte BYTE_OBJECT = 2;
    private static byte BYTE_STRING = 0;

    public enum ObjectType
    {
        STRING,
        FILE,
        OBJECT
    }

    private static int LocateEOT(byte[] data)
    {
        int eotStartIndex = -1;
        int eotIndex = 0;
        for (int i = 0; i < data.length && eotIndex < EOT_DATA.length; i++)
        {
            if (data[i] == EOT_DATA[eotIndex] && eotIndex == 0)
                eotStartIndex = i;
            if (data[i] != EOT_DATA[eotIndex++]) //reset
            {
                eotIndex = 0; //Reset check
                eotStartIndex = -1;
            }
        }
        return eotStartIndex;
    }

    public static ObjectType GetObjectType(byte[] data)
    {
        if(data[0] == BYTE_STRING)
            return ObjectType.STRING;
        else if(data[0] == BYTE_OBJECT)
            return  ObjectType.OBJECT;
        else if(data[0] == BYTE_FILE)
            return ObjectType.FILE;
        else return null;
    }

    public static Object ParseObject(byte[] data)
    {
        Object obj = null;
        if(data.length == 0) return null;
        byte[] objBytes = new byte[data.length - 1];
        System.arraycopy(data, 1, objBytes, 0, objBytes.length);
        if(data[0] == BYTE_FILE)
            obj = objBytes;
        else if(data[0] == BYTE_STRING)
            obj = new String(objBytes);
        else
            obj = BytesToObject(objBytes);
        return obj;
    }

    public static void main(String args[])
    {
        System.out.println("--Byte Utils Testing Begin--");
        if(LocateEOT(DEBUG_DATA) != -1)
            System.out.println("LocateEOT test passed");
        byte[] a = new byte[] { 0, 1};
        byte[] b = new byte[] { 2, 3};
        byte[] merge = Merge(a, b);
        if(merge.length == a.length + b.length)
            System.out.println("Merge test passed.");
        Object myObj = new String("someData");

    }

    public static byte[] Merge(byte[] a, byte[] b)
    {
        byte[] mergeBytes = new byte[a.length + b.length];
        System.arraycopy(a, 0, mergeBytes, 0, a.length);
        System.arraycopy(b, 0, mergeBytes, a.length, b.length);
        return mergeBytes;
    }

    public static byte[] GetBytesFromStream(InputStream stream) throws SocketException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        boolean eotDetected = false;
        while(!eotDetected)
        {
            int n = 0;
            try
            {
                n = stream.read(buf);
                eotDetected = LocateEOT(buf) != -1;
                if(eotDetected)
                {
                    n -= EOT_DATA.length; //Remove EOT from data
                    //System.out.println("EOT Detected");
                }
            }
            catch(SocketException ex)
            {
                throw ex;
            }
            catch (IOException e)
            {
               e.printStackTrace();
               // throw e;
            }
            if( n < 0 )
            {
                break;
            }
            baos.write(buf,0,n);
        }

        byte data[] = baos.toByteArray();
        return data;
    }

    public static byte[] TransmissionObject(Object obj, ObjectType type)
    {
        return Merge(ObjectToBytes(obj, type), EOT_DATA);

    }

    public static byte[] ObjectToBytes(Object object, ObjectType type)
    {
        byte[] objBytes = null;
        byte[] bytesOut = null;
        try
        {
            if(type == ObjectType.STRING)
            {
                objBytes = ((String)object).getBytes();
                bytesOut = new byte[objBytes.length + 1];
                bytesOut[0] = BYTE_STRING;
            }
            else if(type == ObjectType.OBJECT)
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out;
                 out = new ObjectOutputStream(bos);
                out.writeObject(object);
                out.flush();
                objBytes =  bos.toByteArray();
                bos.close();
                bytesOut = new byte[objBytes.length + 1];
                bytesOut[0] = BYTE_OBJECT;
            }
            else if(type == ObjectType.FILE)
            {
                objBytes = (byte[])object;
                bytesOut = new byte[objBytes.length + 1];
                bytesOut[0] = BYTE_FILE;
            }
            System.arraycopy(objBytes, 0, bytesOut, 1, objBytes.length);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {}
        return bytesOut;
    }


    private static Object BytesToObject(byte[] data)
    {
        Object o = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex)
            {
            }
        }
        return o;
    }

    public static void SaveFile(byte[] data, String filePath)
    {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
