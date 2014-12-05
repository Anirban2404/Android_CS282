/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\anirban\\Desktop\\Android CS282_Assignments\\BoundedService\\src\\com\\anirban\\boundedservice\\AsyncScript.aidl
 */
package com.anirban.boundedservice;
// Declare the interface.

public interface AsyncScript extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.anirban.boundedservice.AsyncScript
{
private static final java.lang.String DESCRIPTOR = "com.anirban.boundedservice.AsyncScript";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.anirban.boundedservice.AsyncScript interface,
 * generating a proxy if needed.
 */
public static com.anirban.boundedservice.AsyncScript asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.anirban.boundedservice.AsyncScript))) {
return ((com.anirban.boundedservice.AsyncScript)iin);
}
return new com.anirban.boundedservice.AsyncScript.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_downloadScript:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.anirban.boundedservice.AsyncDisplay _arg1;
_arg1 = com.anirban.boundedservice.AsyncDisplay.Stub.asInterface(data.readStrongBinder());
this.downloadScript(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.anirban.boundedservice.AsyncScript
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void downloadScript(java.lang.String urlPath, com.anirban.boundedservice.AsyncDisplay callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(urlPath);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_downloadScript, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_downloadScript = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void downloadScript(java.lang.String urlPath, com.anirban.boundedservice.AsyncDisplay callback) throws android.os.RemoteException;
}
