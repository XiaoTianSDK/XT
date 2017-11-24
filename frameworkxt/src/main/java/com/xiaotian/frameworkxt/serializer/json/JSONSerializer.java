package com.xiaotian.frameworkxt.serializer.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name JSONSerializer
 * @description JSON序列化处理器
 * @date 2013-10-27
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2013 小天天 Studio, All Rights Reserved.
 */
public class JSONSerializer {
    protected JSONField fieldAnnotation;
    protected Annotation annotation;
    protected Object defaultValue;
    protected Object value;
    protected String name;

    public JSONSerializer() {}

    // static method:: data -> ObjectData, List<Data>
    // 序列化
    @SuppressWarnings("unchecked")
    public String serialize(Object data) throws JSONSerializingException {
        // TODO JSON Object/Array To String
        // 获取Field注解
        Field[] fields = null;
        JSONArray jsonArray = null;
        List<Object> listData = null;
        if (data instanceof List) {
            // TODO Array -> List
            jsonArray = new JSONArray();
            listData = (List<Object>) data;
            fields = getDeclaredFields(listData.get(0).getClass());
            annotation = UtilJSONAnnotation.getJSONEntity(listData.get(0).getClass());
        } else {
            // TODO Object
            fields = getDeclaredFields(data.getClass());
            annotation = UtilJSONAnnotation.getJSONEntity(data.getClass());
        }
        // 判断是否注解序列化
        if (!(annotation instanceof JSONEntity)) {
            throw new JSONSerializingException(JSONSerializingException.UNSUPEREXCEPTION);
        }
        // 转换为字符串
        if (listData != null) {
            // TODO Array
            for (Object dt : listData) {
                jsonArray.put(createJSONObject(fields, dt));
            }
            return jsonArray.toString();
        } else {
            // TODO Object
            return createJSONObject(fields, data).toString();
        }
    }

    // 反序列化 Json String -> Instance
    @SuppressWarnings("unchecked")
    // String de serialize to Object(BaseClass/List<BaseClass>)
    public <T extends Object> T deSerialize(String value, Class<?> baseClass) throws JSONSerializingException {
        // TODO String to JSON Object/Array
        // 判断是否注解序列化
        annotation = UtilJSONAnnotation.getJSONEntity(baseClass);
        if (!(annotation instanceof JSONEntity))
            throw new JSONSerializingException(JSONSerializingException.UNSUPEREXCEPTION);
        JSONTokener tokener = new JSONTokener(value);
        Field[] fields = getDeclaredFields(baseClass);
        Constructor<?> constructor = null;
        Object root = null;
        try {
            root = tokener.nextValue();
            constructor = baseClass.getConstructor();
            if (root instanceof JSONArray) {
                List<Object> listInstance = new ArrayList<Object>();
                JSONArray jsonArray = (JSONArray) root;
                for (int i = 0; i < jsonArray.length(); i++) {
                    listInstance.add(setInstanceValue(jsonArray.getJSONObject(i), fields, constructor.newInstance()));
                }
                return (T) listInstance;
            } else if (root instanceof JSONObject) {
                return (T) setInstanceValue((JSONObject) root, fields, constructor.newInstance());
            }
        } catch (JSONException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (SecurityException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (NoSuchMethodException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalArgumentException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (InstantiationException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalAccessException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (InvocationTargetException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        }
        return null;
    }

    // 反序列化 JsonObject -> Instance
    @SuppressWarnings("unchecked")
    public <T extends Object> T deSerialize(JSONObject jsonData, Class<?> baseClass) throws JSONSerializingException {
        // TODO String to JSON Object/Array
        // 判断是否注解序列化
        annotation = UtilJSONAnnotation.getJSONEntity(baseClass);
        if (!(annotation instanceof JSONEntity))
            throw new JSONSerializingException(JSONSerializingException.UNSUPEREXCEPTION);
        Field[] fields = getDeclaredFields(baseClass);
        try {
            return (T) setInstanceValue(jsonData, fields, baseClass.getConstructor().newInstance());
        } catch (SecurityException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (NoSuchMethodException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalArgumentException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (InstantiationException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalAccessException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (InvocationTargetException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        }
    }

    // 反序列化 JsonArray -> List<Instance>
    public <T extends Object> List<T> deSerialize(JSONArray jsonArray, Class<T> baseClass) throws JSONSerializingException {
        // TODO String to JSON Object/Array
        if (jsonArray == null) return null;
        // 判断是否注解序列化
        annotation = UtilJSONAnnotation.getJSONEntity(baseClass);
        if (!(annotation instanceof JSONEntity))
            throw new JSONSerializingException(JSONSerializingException.UNSUPEREXCEPTION);
        Field[] fields = getDeclaredFields(baseClass);
        List<T> listObj = new ArrayList<T>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                listObj.add((T) setInstanceValue(jsonArray.getJSONObject(i), fields, baseClass.getConstructor().newInstance()));
            }
            return listObj;
        } catch (SecurityException e) {
            throw new JSONSerializingException(JSONSerializingException.EXCEPTION, e);
        } catch (NoSuchMethodException e) {
            throw new JSONSerializingException(JSONSerializingException.EXCEPTION, e);
        } catch (IllegalArgumentException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (InstantiationException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalAccessException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (InvocationTargetException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (JSONException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        }
    }

    // Serialize Object
    @SuppressWarnings("deprecation")
    protected JSONObject createJSONObject(Field[] fields, Object data) throws JSONSerializingException {
        // TODO Object Class To JSONObject
        JSONObject jsonObject = new JSONObject();
        try {
            for (Field field : fields) {
                annotation = field.getAnnotation(JSONField.class);
                if (annotation instanceof JSONField) {
                    // Json Field mapping
                    fieldAnnotation = (JSONField) annotation;
                    if (!fieldAnnotation.serialize()) {
                        // declare un serialize
                        continue;
                    }
                    if (JSONField.DEFAULTNAME.equals(fieldAnnotation.name())) {
                        // Only Mapping the value
                        name = fieldAnnotation.value();
                        defaultValue = JSONField.DEFAULTVALUE;
                    } else {
                        // Mapping the name and default value
                        name = fieldAnnotation.name();
                        defaultValue = fieldAnnotation.defaultValue();
                    }
                    field.setAccessible(true); // Accessible The Value
                    value = field.get(data);
                    field.setAccessible(false);
                    if (value == null && defaultValue == JSONField.DEFAULTVALUE) {
                        // un finded the value
                        continue;
                    } else if (value == null) {
                        // parser the default value to tager class[Base Class UN
                        // Primitive]
                        if (field.getType() == String.class) {
                            // String
                        } else if (field.getType() == Integer.class) {
                            // Integer
                            defaultValue = Integer.parseInt((String) defaultValue);
                        } else if (field.getType() == Boolean.class) {
                            // Boolean
                            defaultValue = Boolean.parseBoolean((String) defaultValue);
                        } else if (field.getType() == Long.class) {
                            // Long
                            defaultValue = Long.parseLong((String) defaultValue);
                        } else if (field.getType() == Float.class) {
                            // Float
                            defaultValue = Float.parseFloat((String) defaultValue);
                        } else if (field.getType() == Date.class) {
                            // Date
                            defaultValue = Date.parse((String) defaultValue);
                        } else if (field.getType() == Double.class) {
                            // Double
                            defaultValue = Double.parseDouble((String) defaultValue);
                        } else if (field.getType() == Short.class) {
                            // Short
                            defaultValue = Short.parseShort((String) defaultValue);
                        } else if (field.getType() == Character.class) {
                            // Character
                            defaultValue = ((String) defaultValue).charAt(0);
                        }
                    }
                    // add json data
                    jsonObject.put(name, value == null ? defaultValue : value);
                } else {
                    // no mapping
                }
                fieldAnnotation = null;
                annotation = null;
            }
        } catch (IllegalArgumentException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalAccessException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (JSONException e) {
            throw new JSONSerializingException(JSONSerializingException.JSONEXCEPTION, e);
        } finally {
            fieldAnnotation = null;
            defaultValue = null;
            annotation = null;
            value = null;
            name = null;
        }
        return jsonObject;
    }

    @SuppressWarnings("deprecation")
    protected <T extends Object> T setInstanceValue(JSONObject data, Field[] fields, T instance) throws JSONSerializingException {
        // Set Mapping Field Value
        try {
            for (Field field : fields) {
                fieldAnnotation = field.getAnnotation(JSONField.class);
                // 1.set the mapping field value
                if (fieldAnnotation instanceof JSONField) {
                    if (!fieldAnnotation.serialize()) continue;
                    if (JSONField.DEFAULTNAME.equals(fieldAnnotation.name())) {
                        // a.value :: Simple Mapping
                        name = fieldAnnotation.value();
                        defaultValue = fieldAnnotation.defaultValue();
                    } else {
                        // b.value / default value :: Mapping two value
                        name = fieldAnnotation.name();
                        defaultValue = fieldAnnotation.defaultValue();
                    }
                } else {
                    // 2.set no mapping field name / value
                    name = field.getName();
                }
                // Set Field Value
                if (data.has(name)) {
                    // get
                    switch (fieldAnnotation.type()) {
                        case JSONField.TYPE_STRING:
                            if (!data.isNull(name)) value = data.getString(name);
                            break;
                        case JSONField.TYPE_INTEGER:
                            if (!data.isNull(name)) value = data.getInt(name);
                            break;
                        case JSONField.TYPE_LONG:
                            if (!data.isNull(name)) value = data.getLong(name);
                            break;
                        case JSONField.TYPE_DOUBLE:
                            if (!data.isNull(name)) value = data.getDouble(name);
                            break;
                        case JSONField.TYPE_BOOLEAN:
                            if (!data.isNull(name)) value = data.getBoolean(name);
                            break;
                        case JSONField.TYPE_NULL:
                            value = null;
                            break;
                    }
                } else if (fieldAnnotation != null && !defaultValue.equals(JSONField.DEFAULTVALUE)) {
                    String stringValue = (String) defaultValue;
                    switch (fieldAnnotation.type()) {
                        case JSONField.TYPE_STRING:
                            value = defaultValue;
                            break;
                        case JSONField.TYPE_INTEGER:
                            value = stringValue.equals("") ? null : Integer.parseInt(stringValue);
                            break;
                        case JSONField.TYPE_LONG:
                            value = stringValue.equals("") ? null : Long.parseLong(stringValue);
                            break;
                        case JSONField.TYPE_DOUBLE:
                            value = stringValue.equals("") ? null : Double.parseDouble(stringValue);
                            break;
                        case JSONField.TYPE_BOOLEAN:
                            value = stringValue.equals("") ? null : Boolean.parseBoolean(stringValue);
                            break;
                        case JSONField.TYPE_NULL:
                            value = null;
                            break;
                    }
                } else {
                    value = null;
                }
                if (value != null) {
                    // translate
                    if (field.getType() == String.class) {
                        // String
                        if (!(value instanceof String)) value = value.toString();
                        ;
                    } else if (field.getType() == Integer.class) {
                        // Integer
                        if (!(value instanceof Integer)) value = Integer.parseInt(value.toString());
                    } else if (field.getType() == Boolean.class) {
                        // Boolean
                        if (!(value instanceof Boolean)) value = Boolean.parseBoolean(value.toString());
                    } else if (field.getType() == Long.class) {
                        // Long
                        if (!(value instanceof Long)) value = Long.parseLong(value.toString());
                    } else if (field.getType() == Float.class) {
                        // Float
                        if (!(value instanceof Float)) value = Float.parseFloat(value.toString());
                    } else if (field.getType() == Double.class) {
                        // Double
                        if (!(value instanceof Double)) value = Double.parseDouble(value.toString());
                    } else if (field.getType() == Short.class) {
                        // Short
                        if (!(value instanceof Short)) value = Short.parseShort(value.toString());
                    } else if (field.getType() == Date.class) {
                        // Date
                        if (!(value instanceof Date)) value = Date.parse(value.toString());
                    } else if (field.getType() == Character.class) {
                        // Character
                        if (!(value instanceof Character)) value = Character.valueOf((value.toString()).charAt(0));
                    } else {
                        // Un support class type
                        value = null;
                    }
                }
                if (value != null && !JSONObject.NULL.equals(value)) {
                    // Set Instance Value
                    field.setAccessible(true);
                    field.set(instance, value);
                    field.setAccessible(false);
                }
                name = null;
                value = null;
                defaultValue = null;
                fieldAnnotation = null;
            }
        } catch (JSONException e) {
            throw new JSONSerializingException(JSONSerializingException.JSONEXCEPTION, e);
        } catch (IllegalArgumentException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } catch (IllegalAccessException e) {
            throw new JSONSerializingException(JSONSerializingException.FIELDEXCEPTION, e);
        } finally {
            name = null;
            value = null;
            defaultValue = null;
            fieldAnnotation = null;
        }
        return instance;
    }

    protected String getJsonFieldName(JSONField jsonField) {
        String annotatedName = jsonField.name().equals(JSONField.DEFAULTVALUE) ? jsonField.value() : jsonField.name();
        return annotatedName;
    }

    protected Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields;
        List<Field> listFields = new ArrayList<Field>();
        while (clazz != null && clazz.getAnnotation(JSONEntity.class) != null) {
            fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length && !listFields.contains(fields[i]); i++) {
                listFields.add(fields[i]);
            }
            clazz = clazz.getSuperclass(); // Super class fields
        }
        fields = new Field[listFields.size()];
        if (listFields.size() > 0) {
            for (int i = 0; i < fields.length; i++) {
                fields[i] = listFields.get(i);
            }
        }
        return fields;
    }
}
