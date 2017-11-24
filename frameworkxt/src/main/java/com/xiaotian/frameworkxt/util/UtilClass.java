package com.xiaotian.frameworkxt.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @version 1.0.0
 * @author mac
 * @name UtilClass
 * @description Class Util
 * @date 2014-10-14
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class UtilClass {
	private static final String TYPE_NAME_PREFIX = "class ";

	// 获取泛型类的传入实际类型列表[父接口/父类]
	// 1.泛型类参数类型必须是在之类传入实际参数
	// 2.泛型类自身只能获取类变量类型[TypeVariable],不能获取参数类型[ParameterizedType]
	public static Type[] getGenericSuperParameteizedTypes(Class<?> clazz) {
		Type superClassType = clazz.getGenericSuperclass();
		if (superClassType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) superClassType; // 父类,被传入实际参数类
			return parameterizedType.getActualTypeArguments(); // 父类实际类型参数[类泛型参数类型ParameterizedType]
		}
		return new Type[0];
	}

	// Type -> Instance
	public static Object newInstance(Type type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = getClass(type);
		if (clazz == null) return null;
		return clazz.newInstance();
	}

	public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) throws ClassNotFoundException {
		Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just keep going.
				type = ((Class<?>) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class<?>) parameterizedType.getRawType();
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}
				if (!rawType.equals(baseClass)) type = rawType.getGenericSuperclass();
			}
		}
		// finally, for each actual type argument provided to baseClass, determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class<?>) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	public static Class<?> getClass(Type type) throws ClassNotFoundException {
		if (type instanceof Class) {
			// Class Type
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			// Parameterized Type
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			// GenericArray Type
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			// Type->toString->ClassName
			String className = getClassName(type);
			if (className == null || className.isEmpty()) return null;
			return Class.forName(className);
		}
	}

	public static String getClassName(Type type) {
		if (type == null) return "";
		String className = type.toString();
		if (className.startsWith(TYPE_NAME_PREFIX)) className = className.substring(TYPE_NAME_PREFIX.length());
		return className;
	}
}
