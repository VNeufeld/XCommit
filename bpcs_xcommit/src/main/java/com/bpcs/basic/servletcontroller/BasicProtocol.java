package com.bpcs.basic.servletcontroller;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlTransient
public class BasicProtocol implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -6720232208063625631L;
	
	Logger logger = Logger.getLogger(BasicProtocol.class);

	@Override
	public boolean equals(Object obj) {
		// missing partner?
		if(obj == null)
			return false;

		// identical?
		if(this == obj)
			return true;

		// identical type?
		if( !(obj.getClass().equals(this.getClass())))
			return false;

		// all attributes have to match!
		for(Method method : this.getClass().getMethods()) {
			String methodName = method.getName();
			if( !isGetter(methodName))
				continue;
			if(methodName.equals("getClass"))
				continue;

			if( !equalsProperty(obj, method))
				return false;
		}

		// all is identical
		return true;
	}

	private boolean isGetter(String methodName) {
		return methodName.substring(0, 3).equals("get");
	}

	private boolean equalsProperty(Object obj, Method method) {
		try {
			Object thisValue = method.invoke(this);
			Object objValue = method.invoke(obj);

			if(objValue == null && thisValue == null)
				return true;
			else if(objValue == null || thisValue == null)
				return false;
			else if(thisValue.getClass().isArray())
				return equalsArray(thisValue, objValue);
			else 
				return thisValue.equals(objValue);
		} catch(SecurityException e) {
			return false;
		}catch(IllegalArgumentException e) {
			return false;
		}catch(IllegalAccessException e) {
			return false;
		}catch(InvocationTargetException e) {
			return false;
		}
	}

	private boolean equalsArray(Object thisValue, Object objValue) {
		boolean equals = true;
		int thisLength = Array.getLength(thisValue);
		int objLenght = Array.getLength(objValue);

		if(thisLength != objLenght)
			return false;

		for(int index = 0; (index < thisLength) && equals; index++) {
			equals = equals && Array.get(thisValue, index).equals(Array.get(objValue, index));
		}
		return true;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer(this.getClass().getName());
		sb.append(": ");
		boolean firstValue = true;

		for(Method method : this.getClass().getMethods()) {
			String methodName = method.getName();
			if( !isGetter(methodName))
				continue;
			if(methodName.equals("getClass"))
				continue;

			Object value;
			try {
				value = method.invoke(this);
			} catch(InvocationTargetException e) {
				// ignore that field
				continue;
			}catch(IllegalAccessException e) {
				// ignore that field
				continue;
			}catch(IllegalArgumentException e) {
				// ignore that field
				continue;
			}
			if(value == null)
				continue;
			String fieldName = makeFieldNameFromMethodName(methodName);
			if( !firstValue)
				sb.append(", ");
			if(value.getClass().isArray()) {
				sb.append(fieldName);
				sb.append(": {");
				for(int index = 0; index < Array.getLength(value); index++)
					appendField(sb, Array.get(value, index), '[' + Integer.toString(index) + ']');
				sb.append("}");
			}
			appendField(sb, value, fieldName);
			firstValue = false;
		}

		return sb.toString();
	}

	private void appendField(StringBuffer sb, Object value, String fieldName) {
		sb.append(fieldName);
		sb.append(": ");
		sb.append(value.toString());
	}

	private String makeFieldNameFromMethodName(String methodName) {
		String s = methodName.substring(3);
		s = s.substring(0, 1).toLowerCase() + s.substring(1);
		return s;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object newObject = super.clone();

		try {
			for(Method getter : this.getClass().getDeclaredMethods()) {
				if( !isGetter(getter))
					continue;
				Method setter = extractSetter(this.getClass(), getter);
				if(setter == null)
					continue;

				Class<?> innerType = getter.getReturnType();
				if(innerType.isPrimitive())
					continue; // primitive type are copied by the shallow copy clone

				// now handling complex objects
				Object attribute = getter.invoke(this);
				if (attribute == null)
					continue;
				Object newAttribute = null;
				if(Collection.class.isAssignableFrom(innerType)) {
					newAttribute = performCollectionClone((Collection) attribute);
				} else if(Map.class.isAssignableFrom(innerType)) {
					newAttribute = performMapClone((Map) attribute);
				} else {
					// simple type, just copy
					newAttribute = performClone(attribute);
				}
				setter.invoke(newObject, newAttribute);
			}
		} catch(NoSuchMethodException e) {
			handleException(e);
		}catch(SecurityException e) {
			handleException(e);
		}catch(IllegalAccessException e) {
			handleException(e);
		}catch(IllegalArgumentException e) {
			handleException(e);
		}catch(InvocationTargetException e) {
			handleException(e);
		}

		return newObject;
	}

	private void handleException(Exception e) throws CloneNotSupportedException {
		System.out.println("clone failed: ");
		e.printStackTrace();
		throw new CloneNotSupportedException();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object performMapClone(Map map) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// clone the collection
		Class mapClass = map.getClass();
		Constructor constructor = mapClass.getConstructor();
		Map newMap = null;
		try {
			newMap = (Map) constructor.newInstance();
		} catch(InstantiationException e) {
			assert false;
			return null;
		}

		// copy the content of the collection
		for(Object k : map.keySet()) {
			Object o = map.get(k);
			newMap.put(k, performClone(o));
		}

		return newMap;
	}

	@SuppressWarnings(
		{ "rawtypes", "unchecked" })
	private Object performCollectionClone(Collection collection)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		// clone the collection
		Class collectionClass = collection.getClass();
		Constructor constructor = collectionClass.getConstructor();
		Collection newCollection = null;
		try {
			newCollection = (Collection) constructor.newInstance();
		} catch(InstantiationException e) {
			assert false;
			return null;
		}

		// copy the content of the collection
		for(Object o : collection) {
			newCollection.add(performClone(o));
		}

		return newCollection;
	}

	private Object performClone(Object attribute) {

		if (attribute == null)
			return null;
		
		Method _clone = null;
		for (Method method : attribute.getClass().getMethods()) {
			if (method.getName().equals("clone")) {
				_clone = method;
				break;
			}
		}
		if (_clone == null) {
			// clone not supported
			return attribute;
		} else {
			Object attributeCopy = null;
			try {
				attributeCopy = _clone.invoke(attribute);
			} catch(InvocationTargetException e) {
				Throwable ex = e.getTargetException();
				logger.debug("encountered exception during try to copy object: " + attribute.getClass().getName(), ex);
			} catch(IllegalAccessException e) {
				logger.debug("encountered exception during try to copy object: " + attribute.getClass().getName(), e);
			}catch(IllegalArgumentException e) {
				logger.debug("encountered exception during try to copy object: " + attribute.getClass().getName(), e);
			}
			return attributeCopy;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Method extractSetter(Class target, Method getter) {
		try {
			Class returnType = getter.getReturnType();
			Method setter = target.getMethod(setterName(getter), returnType);
			return setter;
		} catch(NoSuchMethodException e) {
			return null;
		}catch(SecurityException e) {
			return null;
		}
	}

	private String setterName(Method method) {
		return "set" + method.getName().substring(3);
	}

	private static boolean isGetter(Method method) {
		return method.getName().substring(0, 3).equals("get");
	}
}
