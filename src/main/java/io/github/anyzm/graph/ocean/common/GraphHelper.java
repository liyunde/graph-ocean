/* Copyright (c) 2022 com.github.anyzm. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package io.github.anyzm.graph.ocean.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.anyzm.graph.ocean.annotation.GraphProperty;
import io.github.anyzm.graph.ocean.dao.GraphValueFormatter;
import io.github.anyzm.graph.ocean.domain.GraphLabel;
import io.github.anyzm.graph.ocean.domain.GraphLabelBuilder;
import io.github.anyzm.graph.ocean.domain.impl.GraphEdgeType;
import io.github.anyzm.graph.ocean.domain.impl.GraphVertexType;
import io.github.anyzm.graph.ocean.enums.ErrorEnum;
import io.github.anyzm.graph.ocean.enums.GraphDataTypeEnum;
import io.github.anyzm.graph.ocean.enums.GraphKeyPolicy;
import io.github.anyzm.graph.ocean.enums.GraphPropertyTypeEnum;
import io.github.anyzm.graph.ocean.exception.NebulaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Anyzm
 * date 2020/4/16
 */
@Slf4j
public class GraphHelper {
    private static final String ENDPOINT_TEMPLATE = "%s(\"%s\")";
    private static final String STRING_ID_TEMPLATE = "%s \"%s\" ";
    public static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[\n\t\"'()<>/\\\\]");


    /**
     * nebula 中的时间戳是精确到秒的
     *
     * @return
     */
    public static long getNebulaCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }


    /**
     * 将 localDate 转换为 nebula time
     *
     * @param date
     * @return
     */
    public static long changeToNebulaTime(LocalDate date) {
        Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
        return timestamp.getTime() / 1000;
    }

    private static <T> T generateKeyPolicy(GraphKeyPolicy graphKeyPolicy, @Nonnull T vertexIdKey) {
        if (graphKeyPolicy.equals(GraphKeyPolicy.string_key)) {
            return (T) String.format(STRING_ID_TEMPLATE, graphKeyPolicy.getKeyWrapWord(), vertexIdKey);
        } else if (GraphKeyPolicy.int64.equals(graphKeyPolicy)) {
            return vertexIdKey;
        } else {
            return (T) String.format(ENDPOINT_TEMPLATE, graphKeyPolicy.getKeyWrapWord(), vertexIdKey);
        }
    }

    public static <T> T getQueryId(GraphVertexType vertexTag, T vertexKey) {
        T vertexIdKey = (T) vertexTag.getVertexIdKey(vertexKey);
        GraphKeyPolicy graphKeyPolicy = vertexTag.getGraphKeyPolicy();
        return generateKeyPolicy(graphKeyPolicy, vertexIdKey);
    }

    public static <T> T getQuerySrcId(GraphEdgeType edgeType, T vertexKey) {
        T vertexIdKey = (T) edgeType.getSrcIdKey(vertexKey);
        GraphKeyPolicy graphKeyPolicy = edgeType.getSrcVertexType().getGraphKeyPolicy();
        return generateKeyPolicy(graphKeyPolicy, vertexIdKey);
    }

    public static <T> T getQueryDstId(GraphEdgeType edgeType, T vertexKey) {
        T vertexIdKey = (T) edgeType.getDstIdKey(vertexKey);
        GraphKeyPolicy graphKeyPolicy = edgeType.getDstVertexType().getGraphKeyPolicy();
        return generateKeyPolicy(graphKeyPolicy, vertexIdKey);
    }

    /**
     * 顶点列表id
     *
     * @param vertexTag
     * @param vertexKeyList
     * @return
     */
    public static String getQueryId(GraphVertexType vertexTag, Collection<String> vertexKeyList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isEmpty(vertexKeyList)) {
            return "";
        }

        for (String vertexId : vertexKeyList) {
            stringBuilder.append(getQueryId(vertexTag, vertexId)).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    /**
     * 去掉特殊字符
     *
     * @param str
     * @return
     */
    public static String removeSpecialChar(String str) {
        return SPECIAL_CHAR_PATTERN.matcher(str).replaceAll("");
    }


    private static void collectGraphField(GraphLabelBuilder graphLabelBuilder, Field declaredField, List<String> mustProps,
                                          Map<String, String> propertyFieldMap, Map<String, GraphValueFormatter> propertyFormatMap,
                                          Map<String, GraphDataTypeEnum> dataTypeMap, boolean srcIdAsField, boolean dstIdAsField) {
        declaredField.setAccessible(true);
        GraphProperty graphProperty = declaredField.getAnnotation(GraphProperty.class);
        if (graphProperty == null) {
            return;
        }
        String value = graphProperty.value();
        dataTypeMap.put(value, graphProperty.dataType());
        Class<? extends GraphValueFormatter> formatter = graphProperty.formatter();
        GraphPropertyTypeEnum graphPropertyTypeEnum = graphProperty.propertyTypeEnum();
        switch (graphPropertyTypeEnum) {
            case GRAPH_VERTEX_ID:
                if (srcIdAsField && dstIdAsField) {
                    propertyFieldMap.put(declaredField.getName(), value);
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        graphLabelBuilder.idValueFormatter(formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            case GRAPH_EDGE_SRC_ID:
                if (srcIdAsField) {
                    propertyFieldMap.put(declaredField.getName(), value);
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        graphLabelBuilder.srcIdValueFormatter(formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            case GRAPH_EDGE_DST_ID:
                if (dstIdAsField) {
                    propertyFieldMap.put(declaredField.getName(), value);
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        graphLabelBuilder.dstIdValueFormatter(formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            case ORDINARY_PROPERTY:
                propertyFieldMap.put(declaredField.getName(), value);
                if (graphProperty.required()) {
                    mustProps.add(value);
                }
                if (GraphValueFormatter.class != formatter) {
                    try {
                        propertyFormatMap.put(value, formatter.newInstance());
                    } catch (Exception e) {
                        throw new NebulaException(ErrorEnum.FIELD_FORMAT_NO_CONSTRUCTOR);
                    }
                }
                break;
            default:
                break;
        }

    }

    public static void collectGraphProperties(GraphLabelBuilder graphLabelBuilder, Class clazz,
                                              boolean srcIdAsField, boolean dstIdAsField) throws NebulaException {
        Field[] declaredFields = clazz.getDeclaredFields();
        int size = declaredFields.length;
        List<String> mustProps = Lists.newArrayListWithExpectedSize(size);
        // 所有属性（包括必要属性）
        Map<String, String> propertyFieldMap = Maps.newHashMapWithExpectedSize(size);
        // 字段类型
        Map<String, GraphDataTypeEnum> dataTypeMap = Maps.newHashMapWithExpectedSize(size);
        // 字段转换工厂
        Map<String, GraphValueFormatter> propertyFormatMap = Maps.newHashMapWithExpectedSize(size);
        for (Field declaredField : declaredFields) {
            collectGraphField(graphLabelBuilder, declaredField, mustProps, propertyFieldMap, propertyFormatMap,
                    dataTypeMap, srcIdAsField, dstIdAsField);
        }
        Class superclass = clazz.getSuperclass();
        while (superclass != Object.class) {
            declaredFields = superclass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                collectGraphField(graphLabelBuilder, declaredField, mustProps, propertyFieldMap, propertyFormatMap,
                        dataTypeMap, srcIdAsField, dstIdAsField);
            }
            superclass = superclass.getSuperclass();
        }
        graphLabelBuilder.labelClass(clazz);
        graphLabelBuilder.dataTypeMap(dataTypeMap);
        graphLabelBuilder.mustProps(mustProps);
        graphLabelBuilder.propertyFieldMap(propertyFieldMap);
        graphLabelBuilder.propertyFormatMap(propertyFormatMap);
    }

    public static Object formatFieldValue(Field declaredField, GraphProperty graphProperty, Object input, GraphLabel graphLabel) {
        Object value = null;
        try {
            value = declaredField.get(input);
        } catch (IllegalAccessException e) {
            log.warn("获取值异常{}", input, e);
        }
        return graphLabel.formatValue(graphProperty.value(), value);
    }

}
