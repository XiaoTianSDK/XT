package com.xiaotian.frameworkxt.serializer.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.xiaotian.frameworkxt.common.MyOut;

/**
 * @version 1.0.0
 * @author XiaoTian
 * @name XMLSerializer
 * @description XML Serializer Tool
 * @date 2014-9-22
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
public class XMLSerializer {
	public static final String DEFAULT_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String DEFAULT_ROOT_TAG = "root";
	Map<Class<?>, Field[]> mappingClassFields;
	Map<Object, String> mappingClassName;
	Annotation entityAnnotation;

	public XMLSerializer() {
		mappingClassName = new HashMap<Object, String>();
		mappingClassFields = new HashMap<Class<?>, Field[]>();
	}

	// Object->XML String
	@SuppressWarnings("unchecked")
	public String serialize(Object data) throws XMLSerializingException {
		if (data == null) return null;
		StringBuffer stringBuffer = new StringBuffer();
		Map<String, ? extends Object> mapData;
		Class<? extends Object> entityClass;
		List<? extends Object> listData;
		XMLEntity xmlEntity;
		Field[] fields;
		if (data instanceof XMLDocumentation) {
			// 文件声明
			// XML Declaration
			XMLDocumentation doc = (XMLDocumentation) data;
			stringBuffer.append(doc.getDeclaration());
			// Root TAG
			stringBuffer.append("<");
			stringBuffer.append(doc.getRootTag());
			appendAttribute(stringBuffer, doc.getMapAttribute());
			stringBuffer.append(">");
			// 1.List
			listData = doc.getListData();
			for (int i = 0; listData != null && i < listData.size(); i++) {
				Object itemData = listData.get(i);
				if (itemData instanceof XMLAtom) {
					appendXMLAtom(stringBuffer, (XMLAtom) itemData);
					continue;
				}
				entityClass = itemData.getClass();
				entityAnnotation = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
				if (!(entityAnnotation instanceof XMLEntity)) continue;
				xmlEntity = (XMLEntity) entityAnnotation;
				fields = getDeclaredFields(entityClass);
				stringBuffer.append(createXMLTagEntity(xmlEntity, itemData, fields));
			}
			// 2.Map
			mapData = doc.getMapData();
			if (mapData != null && mapData.size() > 0) {
				stringBuffer.append(createXMLTagMap(mapData));
			}
			// End Root TAG
			stringBuffer.append("</");
			stringBuffer.append(doc.getRootTag());
			stringBuffer.append(">");
		} else if (data instanceof List) {
			// List
			// XML Declaration
			listData = (List<? extends Object>) data;
			entityClass = listData.get(0).getClass();
			entityAnnotation = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
			if (!(entityAnnotation instanceof XMLEntity)) {
				throw new XMLSerializingException(XMLSerializingException.UNSUPER_EXCEPTION);
			}
			// XML Declaration
			xmlEntity = (XMLEntity) entityAnnotation;
			stringBuffer.append(DEFAULT_DECLARATION);

			// Root TAG
			stringBuffer.append("<");
			stringBuffer.append(DEFAULT_ROOT_TAG);
			stringBuffer.append(">");

			// Entity Body TAG
			fields = getDeclaredFields(entityClass);
			for (Object itemData : listData) {
				stringBuffer.append(createXMLTagEntity(xmlEntity, itemData, fields));
			}

			// End Root TAG
			stringBuffer.append("</");
			stringBuffer.append(DEFAULT_ROOT_TAG);
			stringBuffer.append(">");
		} else {
			// Object
			entityClass = data.getClass();
			// XML Entity
			entityAnnotation = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
			if (!(entityAnnotation instanceof XMLEntity)) {
				throw new XMLSerializingException(XMLSerializingException.UNSUPER_EXCEPTION);
			}
			// XML Declaration
			stringBuffer.append(DEFAULT_DECLARATION);
			// XML Body
			xmlEntity = (XMLEntity) entityAnnotation;
			fields = getDeclaredFields(entityClass);
			stringBuffer.append(createXMLTagEntity(xmlEntity, data, fields));
		}
		return stringBuffer.toString();
	}

	// XML String -> Object
	public <T extends Object> T deSerializeEntity(String stringValue, Class<?> baseClass, Class<?>... innerClass) throws XMLSerializingException {
		if (stringValue == null || stringValue.equals("")) return null;
		return createXMLEntity(stringValue, baseClass);
	}

	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream serializeToStream(Object data) throws XMLSerializingException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Element rootElement;
		Document doc;
		Field[] fields;
		XMLEntity xmlEntity;
		Class<?> entityClass;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			if (data instanceof XMLDocumentation) {
				XMLDocumentation xdc = (XMLDocumentation) data;
				rootElement = doc.createElement(xdc.getRootTag());
				addElementAttribute(doc, rootElement, xdc.getMapAttribute());
				doc.appendChild(rootElement);
				appendChileDocumentation(doc, rootElement, xdc);
			} else if (data instanceof List) {
				rootElement = doc.createElement(DEFAULT_ROOT_TAG);
				doc.appendChild(rootElement);
				List<? extends Object> listData = (List<? extends Object>) data;
				for (Object dataItem : listData) {
					entityClass = dataItem.getClass();
					xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
					if (xmlEntity == null) continue;
					fields = getDeclaredFields(entityClass);
					Element subElement = createEntityElement(doc, dataItem, xmlEntity, fields);
					rootElement.appendChild(subElement);
				}
			} else {
				rootElement = doc.createElement(DEFAULT_ROOT_TAG);
				doc.appendChild(rootElement);
				entityClass = data.getClass();
				xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
				if (xmlEntity == null) throw new XMLSerializingException(XMLSerializingException.UNSUPER_EXCEPTION);
				fields = getDeclaredFields(entityClass);
				Element subElement = createEntityElement(doc, data, xmlEntity, fields);
				rootElement.appendChild(subElement);
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (TransformerConfigurationException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (TransformerException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IllegalAccessException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IllegalArgumentException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		}
		return outputStream;
	}

	// XML String -> List Object
	@SuppressWarnings("unchecked")
	public <T extends Object> List<T> deSerializeListEntity(String stringValue, Class<?> baseClass) throws XMLSerializingException {
		if (stringValue == null || stringValue.equals("")) return null;
		return (List<T>) createListXMLEntity(stringValue, baseClass);
	}

	// XML String -> XMLDocumentation
	public XMLDocumentation deSerializeXMLDocumentation(String stringValue, Map<String, Class<?>> mappedClass) throws XMLSerializingException {
		if (stringValue == null || mappedClass == null || stringValue.equals("") || mappedClass.size() < 1) return null;
		return createXMLDocumentation(stringValue, mappedClass);
	}

	/********************************************** Object -> String **********************************************/
	// Map Data
	protected StringBuffer createXMLTagMap(Map<String, ? extends Object> mapData) throws XMLSerializingException {
		Iterator<String> ite = mapData.keySet().iterator();
		StringBuffer stringBuffer = new StringBuffer();
		Class<? extends Object> entityClass;
		List<? extends Object> listData;
		XMLEntity xmlEntity;
		Field[] fields;
		while (ite.hasNext()) {
			String key = ite.next();
			Object value = mapData.get(key);
			if (value == null) continue;
			// Star
			stringBuffer.append("<");
			stringBuffer.append(key);
			// Body
			if (value instanceof XMLDocumentation) {
				XMLDocumentation doc = (XMLDocumentation) value;
				appendAttribute(stringBuffer, doc.getMapAttribute());
				stringBuffer.append(">");
				// Document
				// 1.List
				listData = doc.getListData();
				for (int i = 0; listData != null && i < listData.size(); i++) {
					Object itemData = listData.get(i);
					if (itemData instanceof XMLAtom) {
						appendXMLAtom(stringBuffer, (XMLAtom) itemData);
						continue;
					}
					entityClass = itemData.getClass();
					xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
					if (!(xmlEntity instanceof XMLEntity)) continue;
					fields = getDeclaredFields(entityClass);
					stringBuffer.append(createXMLTagEntity(xmlEntity, itemData, fields));
				}
				// 2.Map
				Map<String, ? extends Object> subMapData = doc.getMapData();
				if (subMapData != null && subMapData.size() > 0) {
					stringBuffer.append(createXMLTagMap(subMapData));
				}
			} else {
				// Object-Atomic
				entityClass = value.getClass();
				if (isAtomicClass(entityClass)) {
					// Atomic
					stringBuffer.append(">");
					stringBuffer.append(String.valueOf(value));
				} else {
					// Page Atomic
				}
			}
			// End
			stringBuffer.append("</");
			stringBuffer.append(key);
			stringBuffer.append(">");
		}
		return stringBuffer;
	}

	// XML TAG Entity, An Object Entity TAG
	protected StringBuffer createXMLTagEntity(XMLEntity xmlEntity, Object data, Field[] fields) throws XMLSerializingException {
		StringBuffer stringBuffer = new StringBuffer();
		// Root TAG
		stringBuffer.append("<");
		stringBuffer.append(getXMLEntityName(xmlEntity));
		if (xmlEntity.attribute().length > 0) {
			for (String attribute : xmlEntity.attribute()) {
				stringBuffer.append(" ");
				stringBuffer.append(attribute);
			}
		}
		stringBuffer.append(">");

		// Entity Body TAG
		createXMLTagElement(stringBuffer, fields, data);

		// End Root TAG
		stringBuffer.append("</");
		stringBuffer.append(getXMLEntityName(xmlEntity));
		stringBuffer.append(">");
		return stringBuffer;
	}

	// XML TAG Element
	protected StringBuffer createXMLTagElement(StringBuffer stringBuffer, Field[] fields, Object data) throws XMLSerializingException {
		Annotation[] annotations;
		XMLAttribute xmlProperty;
		XMLElement xmlElement;
		try {
			for (Field field : fields) {
				field.setAccessible(true); // Accessible The Value
				annotations = field.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof XMLElement) {
						xmlElement = (XMLElement) annotation;
						if (!xmlElement.serialize()) continue;
						appendElementTag(stringBuffer, data, xmlElement, field);
					} else if (annotation instanceof XMLAttribute) {
						xmlProperty = (XMLAttribute) annotation;
						if (!xmlProperty.serialize()) continue;
						appendRootTagProperty(stringBuffer, data, xmlProperty, field);
					}
				}
			}
			return stringBuffer;
		} catch (IllegalAccessException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IllegalArgumentException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		}
	}

	public void appendRootTagProperty(StringBuffer stringBuffer, Object data, XMLAttribute xmlProperty, Field field) throws IllegalAccessException,
			IllegalArgumentException {
		String defaultValue = xmlProperty.defaultValue(); // 默认字符串
		// 获取 Field Value
		Object value = field.get(data);
		if (value == null && defaultValue == XMLElement.DEFAULT_VALUE) {
			// un finded the value
			return;
		}
		// 添加 Root 属性
		if (entityAnnotation instanceof XMLEntity) {
			XMLEntity mXMLEntity = (XMLEntity) entityAnnotation;
			int index = stringBuffer.indexOf(getXMLEntityName(mXMLEntity));
			if (index == -1) return;
			index += getXMLEntityName(mXMLEntity).length();
			// 反方向插入到 Index 位置
			String propertyName = xmlProperty.name().equals(XMLAttribute.DEFAULT_NAME) ? xmlProperty.value() : xmlProperty.name();
			stringBuffer.insert(index, "\"");
			stringBuffer.insert(index, value == null ? defaultValue : value.toString());
			stringBuffer.insert(index, "=\"");
			stringBuffer.insert(index, propertyName);
			stringBuffer.insert(index, " ");
		}
	}

	@SuppressWarnings("deprecation")
	public void appendElementTag(StringBuffer stringBuffer, Object data, XMLElement xmlElement, Field field) throws IllegalAccessException,
			IllegalArgumentException {
		Object defaultValue = xmlElement.defaultValue(); // 默认字符串
		// 获取 Field Value
		Object value = field.get(data);
		if (value == null && defaultValue.equals(XMLElement.DEFAULT_VALUE)) {
			// un finded the value
			return;
		} else if (value == null) {
			// 默认值类型转换
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
		} else {
			Class<?> entityClass = value.getClass();
			if (entityClass.getAnnotation(XMLEntity.class) != null) {
				XMLEntity xmlEntity = entityClass.getAnnotation(XMLEntity.class);
				Field[] fields = getDeclaredFields(entityClass);
				try {
					stringBuffer.append(createXMLTagEntity(xmlEntity, value, fields));
				} catch (XMLSerializingException e) {
					e.printStackTrace();
				}
			}
		}
		// 创建 TAG 的标签
		stringBuffer.append("<");
		stringBuffer.append(getXMLElementName(xmlElement));
		stringBuffer.append(">");
		stringBuffer.append(value == null ? defaultValue : value);
		stringBuffer.append("</");
		stringBuffer.append(getXMLElementName(xmlElement));
		stringBuffer.append(">");
	}

	/********************************************** Element Serialize Object Method **********************************************/
	protected void appendChileDocumentation(Document doc, Element element, XMLDocumentation xdc) {
		Map<String, Object> map = xdc.getMapData();
		List<Object> list = xdc.getListData();
		XMLEntity xmlEntity;
		Field[] fields;
		Class<?> entityClass;
		for (int i = 0; list != null && i < list.size(); i++) {
			Object data = list.get(i);
			if (data instanceof XMLDocumentation) {
				XMLDocumentation subXdc = (XMLDocumentation) data;
				Element subElement = doc.createElement(subXdc.getRootTag());
				addElementAttribute(doc, subElement, xdc.getMapAttribute());
				element.appendChild(subElement);
				appendChileDocumentation(doc, subElement, subXdc);
			} else if (data instanceof XMLAtom) {
				XMLAtom xmlAtom = (XMLAtom) data;
				if (xmlAtom.getName() == null || xmlAtom.getName().equals("")) continue;
				Element subElement = doc.createElement(xmlAtom.getName());
				subElement.appendChild(doc.createTextNode(xmlAtom.getValue()));
				addElementAttribute(doc, subElement, xmlAtom.getAttribute());
				element.appendChild(subElement);
			} else {
				entityClass = data.getClass();
				try {
					xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
					if (xmlEntity == null) continue;
					fields = getDeclaredFields(entityClass);
					Element subElement = createEntityElement(doc, data, xmlEntity, fields);
					element.appendChild(subElement);
				} catch (XMLSerializingException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		if (map == null) return;
		Iterator<String> ite = map.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			Object data = map.get(key);
			if (data instanceof XMLDocumentation) {
				XMLDocumentation subXdc = (XMLDocumentation) data;
				Element subElement = doc.createElement(key);
				element.appendChild(subElement);
				addElementAttribute(doc, subElement, subXdc.getMapAttribute());
				appendChileDocumentation(doc, subElement, subXdc);
			} else {
				Element subElement = doc.createElement(key);
				subElement.appendChild(doc.createTextNode(String.valueOf(data)));
				element.appendChild(subElement);
			}
		}
	}

	private Element createEntityElement(Document doc, Object data, XMLEntity xmlEntity, Field[] fields) throws IllegalAccessException,
			IllegalArgumentException {
		Element element = doc.createElement(xmlEntity.name());
		XMLAttribute xmlAttribute;
		XMLElement xmlElement;
		Element subElement;
		Object value;
		for (Field field : fields) {
			xmlElement = field.getAnnotation(XMLElement.class);
			if (xmlElement != null) {
				field.setAccessible(true);
				value = field.get(data);
				field.setAccessible(false);
				if (value == null) value = xmlElement.defaultValue();
				subElement = doc.createElement(getXMLElementName(xmlElement));
				subElement.appendChild(doc.createTextNode(String.valueOf(value)));
				element.appendChild(subElement);
				continue;
			}
			xmlAttribute = field.getAnnotation(XMLAttribute.class);
			if (xmlAttribute != null) {
				field.setAccessible(true);
				value = field.get(data);
				if (value == null) value = xmlAttribute.defaultValue();
				Attr attr = doc.createAttribute(getXMLAttributeName(xmlAttribute));
				attr.setValue(String.valueOf(value));
				element.setAttributeNode(attr);
				continue;
			}
		}
		return element;
	}

	protected void addElementAttribute(Document doc, Element element, Map<String, String> attrs) {
		if (attrs == null || attrs.size() < 1) return;
		Iterator<String> iteKey = attrs.keySet().iterator();
		while (iteKey.hasNext()) {
			String key = iteKey.next();
			String value = attrs.get(key);
			Attr attr = doc.createAttribute(key);
			attr.setValue(value);
			element.setAttributeNode(attr);
		}
	}

	/********************************************** String+Data -> Object **********************************************/
	protected XMLDocumentation createXMLDocumentation(final String utf8DocumentationString, final Map<String, Class<?>> mappedClass)
			throws XMLSerializingException {
		final XMLElementContainer xmlDocContainer = new XMLElementContainer();
		// SAX 解析
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			InputStream is = new ByteArrayInputStream(utf8DocumentationString.getBytes(Charset.forName("UTF-8")));
			saxParser.parse(is, new DefaultHandler() {
				// 容器堆栈
				Stack<XMLElementContainer> containerInstancStack = new Stack<XMLElementContainer>();
				// Content
				StringBuffer stringBuffer = new StringBuffer();
				Class<?> containerClass;
				boolean isEntityString;
				XMLEntity xmlEntity;
				Field[] entityFields;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					// MyOut.info("startElement : qName=" + qName);
					// 1.声明的映射,Map,TAG 不是实体类,Sub Root 是实体类
					if (!isEntityString) {
						containerClass = mappedClass.get(qName); // 配置了映射关联
						// MyOut.info("获取配置的映射容器类:" + containerClass);
						if (containerClass == null) return; // 无声明映射
						Object container = null;
						if (XMLDocumentation.class.isAssignableFrom(containerClass)) {
							// XML Element Container
							try {
								container = containerClass.newInstance();
							} catch (InstantiationException e) {
								return;
							} catch (IllegalAccessException e) {
								return;
							}
							setXMLDocumentAttribute((XMLDocumentation) container, attributes);
						} else if (XMLAtom.class.isAssignableFrom(containerClass)) {
							// Atom Element
							try {
								container = containerClass.newInstance();
								((XMLAtom) container).setName(qName);
								isEntityString = true;
							} catch (InstantiationException e) {
								MyOut.printStackTrace(e);
								return;
							} catch (IllegalAccessException e) {
								MyOut.printStackTrace(e);
								return;
							}
							setAtomElementAttribute((XMLAtom) container, attributes);
						} else {
							// 普通实体,实体有效性校验,必须@XMLEntity
							try {
								xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(containerClass);
								isEntityString = getXMLEntityName(xmlEntity).equals(qName); // Entity
							} catch (XMLSerializingException e) {
								MyOut.printStackTrace(e);
								return;
							}
							if (isEntityString) {
								// 2.1 创建实体对象
								try {
									container = containerClass.newInstance();
									entityFields = getDeclaredFields(containerClass);
									// MyOut.info("创建实体,instance=" + container);
								} catch (InstantiationException e) {
									MyOut.printStackTrace(e);
									isEntityString = false;
									return;
								} catch (IllegalAccessException e) {
									MyOut.printStackTrace(e);
									isEntityString = false;
									return;
								}
								// 2.2 设置实体属性
								if (attributes.getLength() > 0) try {
									setXMLEntityMappedAttribute(container, entityFields, attributes);
								} catch (IllegalArgumentException e) {
									MyOut.printStackTrace(e);// 忽略实体属性
								} catch (IllegalAccessException e) {
									MyOut.printStackTrace(e);
								}
							} else {
								return;
							}
						}
						// 加入堆栈
						XMLElementContainer xec = new XMLElementContainer(qName, containerClass, container);
						containerInstancStack.add(xec);// 入栈
						// MyOut.info("数据入栈: qName=" + xec.qName + " container="
						// + container);
						return;
					}
				}

				// Content Characters
				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					stringBuffer.append(ch, start, length);
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					// MyOut.info("endElement : qName=" + qName);
					// 1.End Entity String
					if (isEntityString) {
						Object instance = containerInstancStack.peek().instance; // 栈顶实体
						String value = stringBuffer.toString();
						stringBuffer.setLength(0);
						if (instance instanceof XMLAtom) {
							// Atom Entity
							((XMLAtom) instance).setValue(value);
						} else {
							// Annotation Entity
							try {
								setXMLEntityMappedElement(instance, entityFields, qName, value.length() > 0 ? value : null);
							} catch (IllegalAccessException e) {
								MyOut.printStackTrace(e);
							} catch (IllegalArgumentException e) {
								MyOut.printStackTrace(e);
							}
							if (!getXMLEntityName(xmlEntity).equals(qName)) return; // 非实体标签结束(实体内部属性结束)
						}
						// end an entity
						isEntityString = false;
						XMLElementContainer xec = containerInstancStack.pop(); // 实体出栈
						if (!containerInstancStack.isEmpty()) {
							XMLElementContainer pxec = containerInstancStack.peek(); // 上层容器
							((XMLDocumentation) pxec.instance).put(xec.instance);
						}
						return;
					}
					// 2.0 基本类型标签,忽略没有值的标签
					if (stringBuffer.length() > 0) {
						XMLElementContainer xec = containerInstancStack.peek();
						if (xec.instance instanceof XMLDocumentation) {
							// MyOut.info("读取基本类型标签,有EC容器,添加到EC Key=" + qName + " value=" + stringBuffer.toString());
							Class<?> tagInstanceClass = xec.instance.getClass();
							if (hasAnnotatedElement(tagInstanceClass, qName)) {
								// 2.0.1 XMLDocumentation中声明了属性映射
								try {
									setXMLEntityMappedElement(xec.instance, getDeclaredFields(tagInstanceClass), qName, stringBuffer.toString());
								} catch (Exception e) {}
							} else {
								// 2.0.2 XMLDocumentation没有声明属性映射
								((XMLDocumentation) xec.instance).put(qName, stringBuffer.toString());
							}
						} else {
							System.out.println("读取XML Element 类型错误.");
							// MyOut.info("读取基本类型标签,有EC容器,EC容器实体不是XMLContainer");
						}
						stringBuffer.setLength(0);
						return;
					}
					// 2.1 容器标签
					if (!containerInstancStack.isEmpty() && containerInstancStack.peek().qName.equals(qName)) {
						// end Container Map 映射容器
						XMLElementContainer xec = containerInstancStack.pop();
						if (containerInstancStack.isEmpty()) {
							xmlDocContainer.instance = xec.instance;
							return;
						}
						// 上层有容器
						// MyOut.info("读取完容器,容器保存到上一层容器中, key=" + xec.qName + " value=" + xec.instance);
						XMLElementContainer parentContainer = containerInstancStack.peek();
						if (parentContainer.instance instanceof XMLDocumentation) {
							((XMLDocumentation) parentContainer.instance).getMapData().put(xec.qName, xec.instance);
						}
						return;
					}
				}
			});
			return (XMLDocumentation) xmlDocContainer.instance;
		} catch (SAXException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IOException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (ParserConfigurationException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		}
	}

	protected List<? extends Object> createListXMLEntity(final String utf8EntityString, final Class<?> entityClass) throws XMLSerializingException {
		final XMLEntity xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
		if (xmlEntity == null) {
			throw new XMLSerializingException(XMLSerializingException.UNSUPER_EXCEPTION);
		}
		final List<Object> listInstance = new ArrayList<Object>();
		final Field[] fields = getDeclaredFields(entityClass);
		// SAX 解析
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// 创建解析器
		try {
			SAXParser saxParser = spf.newSAXParser();
			InputStream is = new ByteArrayInputStream(utf8EntityString.getBytes(Charset.forName("UTF-8")));
			saxParser.parse(is, new DefaultHandler() {
				StringBuffer stringBuffer = new StringBuffer();
				boolean isEntityString;
				Object instance;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if (!isEntityString) {
						isEntityString = getXMLEntityName(xmlEntity).equals(qName); // Entity
						if (isEntityString) {
							try {
								instance = entityClass.newInstance();
							} catch (InstantiationException e) {
								e.printStackTrace();
								isEntityString = false;
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								isEntityString = false;
							}
						} else {
							return;
						}
					}
					if (attributes.getLength() > 0) try {
						setXMLEntityMappedAttribute(instance, fields, attributes);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}

				// Content Characters
				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					if (!isEntityString) return;
					stringBuffer.append(ch, start, length);
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (!isEntityString) return;
					String value = stringBuffer.toString();
					if (getXMLEntityName(xmlEntity).equals(qName)) {
						listInstance.add(instance);
						isEntityString = false;
						return;
					}
					try {
						setXMLEntityMappedElement(instance, fields, qName, value.length() > 0 ? value : null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					stringBuffer.setLength(0);
				}
			});
			return listInstance;
		} catch (SAXException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IOException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (ParserConfigurationException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends Object> T createXMLEntity(final String utf8EntityString, final Class<?> entityClass) throws XMLSerializingException {
		final XMLEntity xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(entityClass);
		if (xmlEntity == null) {
			throw new XMLSerializingException(XMLSerializingException.UNSUPER_EXCEPTION);
		}
		final Field[] fields = getDeclaredFields(entityClass);
		// SAX 解析
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// 创建解析器
		try {
			final Object instance = entityClass.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			InputStream is = new ByteArrayInputStream(utf8EntityString.getBytes(Charset.forName("UTF-8")));
			saxParser.parse(is, new DefaultHandler() {
				StringBuffer stringBuffer = new StringBuffer();
				boolean isValidString;

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if (!isValidString && !(isValidString = getXMLEntityName(xmlEntity).equals(qName))) return;
					if (attributes.getLength() > 0) try {
						setXMLEntityMappedAttribute(instance, fields, attributes);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}

				// Content Characters
				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					if (!isValidString) return;
					stringBuffer.append(ch, start, length);
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (!isValidString) return;
					String value = stringBuffer.toString();
					try {
						setXMLEntityMappedElement(instance, fields, qName, value.length() > 0 ? value : null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					stringBuffer.setLength(0);
				}
			});
			return (T) instance;
		} catch (SAXException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IOException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (InstantiationException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (IllegalAccessException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		} catch (ParserConfigurationException e) {
			throw new XMLSerializingException(XMLSerializingException.RUNTIME_EXCEPTION, e);
		}
	}

	private void setXMLDocumentAttribute(XMLDocumentation doc, Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			doc.addAttribute(attributes.getQName(i), attributes.getValue(i));
		}
	}

	private void setAtomElementAttribute(XMLAtom xmlAtom, Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			xmlAtom.addAttribute(attributes.getQName(i), attributes.getValue(i));
		}
	}

	@SuppressWarnings("deprecation")
	protected void setXMLEntityMappedAttribute(Object instance, Field[] fields, Attributes attributes) throws IllegalAccessException,
			IllegalArgumentException {
		if (attributes.getLength() < 1) return;
		XMLAttribute xmlAttribute;
		String qName = null;
		Object value = null;
		Field field;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			// set instance value / default value
			xmlAttribute = field.getAnnotation(XMLAttribute.class);
			if (xmlAttribute == null) continue;
			for (int j = 0; j < attributes.getLength(); j++) {
				qName = attributes.getQName(j);
				if (getXMLAttributeName(xmlAttribute).equals(qName)) {
					value = attributes.getValue(j);
					if (value == null) value = xmlAttribute.defaultValue();
					break;
				}
			}
			if (value == null || value.equals("") || value.equals(XMLAttribute.DEFAULT_VALUE)) {
				value = null;
				continue;
			}
			if (field.getType() == String.class) {
				// String
				if (!(value instanceof String)) value = value.toString();;
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
				continue;
			}
			if (value == null) continue;
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(false);
		}
	}

	@SuppressWarnings("deprecation")
	protected void setXMLEntityMappedAttribute(Object instance, Field[] fields, String qName, Object value) throws IllegalAccessException,
			IllegalArgumentException {
		if (value == null) return;
		XMLAttribute xmlAttribute;
		Field field;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			// set instance value / default value
			xmlAttribute = field.getAnnotation(XMLAttribute.class);
			if (xmlAttribute == null || !getXMLAttributeName(xmlAttribute).equals(qName)) continue;
			if (field.getType() == String.class) {
				// String
				if (!(value instanceof String)) value = value.toString();;
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
			if (value == null) break;
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(false);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	protected void setXMLEntityMappedElement(Object instance, Field[] fields, String qName, Object value) throws IllegalAccessException,
			IllegalArgumentException {
		if (value == null) return;
		XMLElement xmlElement;
		Field field;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			// set instance value / default value
			xmlElement = field.getAnnotation(XMLElement.class);
			if (xmlElement == null) continue;
			if (!getXMLElementName(xmlElement).equals(qName)) continue;
			if (field.getType() == String.class) {
				// String
				if (!(value instanceof String)) value = value.toString();;
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
			if (value == null) break;
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(false);
			break;
		}
	}

	protected Map<String, Class<?>> translateAnnotationEntity(List<Class<?>> listEntity) throws XMLSerializingException {
		Map<String, Class<?>> mappedEntity = new HashMap<String, Class<?>>();
		XMLEntity xmlEntity;
		for (Class<?> clazz : listEntity) {
			xmlEntity = UtilXMLAnnotation.getXMLEntityAnnotation(clazz);
			if (xmlEntity == null) continue;
			mappedEntity.put(getXMLEntityName(xmlEntity), clazz);
		}
		return mappedEntity;
	}

	protected void appendAttribute(StringBuffer stringBuffer, Map<String, String> attribute) {
		if (attribute == null || attribute.size() < 1) return;
		Iterator<String> ite = attribute.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			stringBuffer.append(" ");
			stringBuffer.append(key);
			stringBuffer.append("=\"");
			stringBuffer.append(attribute.get(key));
			stringBuffer.append("\"");
		}
	}

	protected void appendXMLAtom(StringBuffer stringBuffer, XMLAtom xmlAtom) {
		if (xmlAtom == null || xmlAtom.getValue() == null) return;
		stringBuffer.append("<");
		stringBuffer.append(xmlAtom.getName());
		Iterator<String> ite = xmlAtom.getAttribute().keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			stringBuffer.append(" ");
			stringBuffer.append(key);
			stringBuffer.append("=\"");
			stringBuffer.append(xmlAtom.getAttribute().get(key));
			stringBuffer.append("\"");
		}
		stringBuffer.append(">");
		stringBuffer.append(xmlAtom.getValue());
		stringBuffer.append("</");
		stringBuffer.append(xmlAtom.getName());
		stringBuffer.append(">");
	}

	protected boolean isAtomicClass(Class<? extends Object> clazz) {
		if (clazz == String.class) return true;
		if (clazz == Integer.class) return true;
		if (clazz == Long.class) return true;
		if (clazz == Float.class) return true;
		if (clazz == Boolean.class) return true;
		if (clazz == Double.class) return true;
		if (clazz == Short.class) return true;
		return false;
	}

	protected Field[] getDeclaredFields(Class<?> clazz) {
		if (mappingClassFields.containsKey(clazz)) return mappingClassFields.get(clazz);
		Field[] fields = null;
		List<Field> listFields = new ArrayList<Field>();
		while (clazz != null && clazz.getAnnotation(XMLEntity.class) != null) {
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
		mappingClassFields.put(clazz, fields);
		return fields;
	}

	protected boolean hasAnnotatedAttribute(Class<?> tagEntityClass, String mappedQName) {
		Field[] fields = getDeclaredFields(tagEntityClass);
		for (int i = 0; i < fields.length; i++) {
			XMLAttribute xe = fields[0].getAnnotation(XMLAttribute.class);
			if (xe != null && getXMLAttributeName(xe).equals(mappedQName)) return true;
		}
		return false;
	}

	protected boolean hasAnnotatedElement(Class<?> tagEntityClass, String mappedQName) {
		Field[] fields = getDeclaredFields(tagEntityClass);
		for (int i = 0; i < fields.length; i++) {
			XMLElement xe = fields[i].getAnnotation(XMLElement.class);
			if (xe != null && getXMLElementName(xe).equals(mappedQName)) return true; // 配置Name
		}
		return false;
	}

	// Annotaion Name
	protected String getXMLEntityName(XMLEntity xmlEntity) {
		// if (mappingClassName.containsKey(xmlEntity)) return mappingClassName.get(xmlEntity);
		String annotatedName = (xmlEntity.name().equals(XMLEntity.DEFAULT_NAME)) ? xmlEntity.value() : xmlEntity.name();
		// mappingClassName.put(xmlEntity, annotatedName);
		return annotatedName;
	}

	protected String getXMLElementName(XMLElement xmlElement) {
		// if (mappingClassName.containsKey(xmlElement)) return mappingClassName.get(xmlElement);
		String annotatedName = xmlElement.name().equals(XMLElement.DEFAULT_NAME) ? xmlElement.value() : xmlElement.name();
		// mappingClassName.put(xmlElement, annotatedName);
		return annotatedName;
	}

	protected String getXMLAttributeName(XMLAttribute xmlAttribute) {
		// if (mappingClassName.containsKey(xmlAttribute)) return mappingClassName.get(xmlAttribute);
		String annotatedName = xmlAttribute.name().equals(XMLAttribute.DEFAULT_NAME) ? xmlAttribute.value() : xmlAttribute.name();
		// mappingClassName.put(xmlAttribute, annotatedName);
		return annotatedName;
	}

	/*********************************** Inner Class ***********************************/
	class XMLElementContainer {
		public String qName;
		public Object instance;
		public Class<?> instanceClass;

		public XMLElementContainer() {}

		public XMLElementContainer(String qName, Class<?> instanceClass, Object instance) {
			this.qName = qName;
			this.instance = instance;
			this.instanceClass = instanceClass;
		}
	}
}
