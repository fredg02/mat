/*******************************************************************************
 * Copyright (c) 2008 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.mat.inspections;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.extension.IClassSpecificNameResolver;
import org.eclipse.mat.snapshot.extension.Subject;
import org.eclipse.mat.snapshot.extension.Subjects;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.IPrimitiveArray;
import org.eclipse.mat.snapshot.model.PrettyPrinter;

public class CommonNameResolver
{
    @Subject("java.lang.String")
    public static class StringResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject obj) throws SnapshotException
        {
            return PrettyPrinter.objectAsString(obj, 1024);
        }
    }

    @Subjects( { "java.lang.StringBuffer", // 
                    "java.lang.StringBuilder" })
    public static class StringBufferResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject obj) throws SnapshotException
        {
            Integer count = (Integer) obj.resolveValue("count"); //$NON-NLS-1$
            if (count == null)
                return null;
            if (count == 0)
                return ""; //$NON-NLS-1$

            IPrimitiveArray charArray = (IPrimitiveArray) obj.resolveValue("value"); //$NON-NLS-1$
            if (charArray == null)
                return null;

            return PrettyPrinter.arrayAsString(charArray, 0, count, 1024);
        }
    }

    @Subject("java.lang.Thread")
    public static class ThreadResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject obj) throws SnapshotException
        {
            IObject name = (IObject) obj.resolveValue("name"); //$NON-NLS-1$
            return name != null ? name.getClassSpecificName() : null;
        }
    }

    @Subject("java.lang.ThreadGroup")
    public static class ThreadGroupResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject object) throws SnapshotException
        {
            IObject nameString = (IObject) object.resolveValue("name"); //$NON-NLS-1$
            if (nameString == null)
                return null;
            return nameString.getClassSpecificName();
        }
    }

    @Subjects( { "java.lang.Byte", //
                    "java.lang.Character", //
                    "java.lang.Short", //
                    "java.lang.Integer", //
                    "java.lang.Long", //
                    "java.lang.Float", //
                    "java.lang.Double", //
                    "java.lang.Boolean" })
    public static class ValueResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject heapObject) throws SnapshotException
        {
            return String.valueOf(heapObject.resolveValue("value")); //$NON-NLS-1$
        }
    }

    @Subject("char[]")
    public static class CharArrayResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject heapObject) throws SnapshotException
        {
            IPrimitiveArray charArray = (IPrimitiveArray) heapObject;
            return PrettyPrinter.arrayAsString(charArray, 0, charArray.getLength(), 1024);
        }
    }

    @Subject("byte[]")
    public static class ByteArrayResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject heapObject) throws SnapshotException
        {
            IPrimitiveArray arr = (IPrimitiveArray) heapObject;
            byte[] value = (byte[]) arr.getValueArray(0, Math.min(arr.getLength(), 1024));
            if (value == null)
                return null;

            // must not modify the original byte array
            StringBuilder r = new StringBuilder(value.length);
            for (int i = 0; i < value.length; i++)
            {
                // ASCII/Unicode 127 is not printable
                if (value[i] < 32 || value[i] > 126)
                    r.append('.');
                else
                    r.append((char) value[i]);
            }
            return r.toString();
        }
    }
    
    /*
     * Contributed in bug 273915
     */
    @Subject("java.net.URL")
    public static class URLResolver implements IClassSpecificNameResolver
    {
        public String resolve(IObject obj) throws SnapshotException
        {
            StringBuilder builder = new StringBuilder();
            IObject protocol = (IObject) obj.resolveValue("protocol");
            builder.append(protocol.getClassSpecificName());
            builder.append(":");
            IObject authority = (IObject) obj.resolveValue("authority");
            if(authority != null) {
                builder.append("//");
                builder.append(authority.getClassSpecificName());
            }
            IObject path = (IObject) obj.resolveValue("path");
            if(path != null) builder.append(path.getClassSpecificName());
            IObject query = (IObject) obj.resolveValue("query");
            if(query != null) {
                builder.append("?");
                builder.append(query.getClassSpecificName());
            }
            IObject ref = (IObject) obj.resolveValue("ref");
            if(ref != null) {
                builder.append("#");
                builder.append(ref.getClassSpecificName());
            }
            return builder.toString();
        }
    }
}
