/* Copyright (c) 2022 com.github.anyzm. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package io.github.anyzm.graph.ocean.domain.impl;

import io.github.anyzm.graph.ocean.domain.GraphRelation;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Anyzm
 * date 2019/2/14
 */
@Getter
@ToString
public class GraphEdgeEntity<S, T, E> extends GraphPropertyEntity implements GraphRelation<S, T, E> {
    /**
     * 起点 id
     */
    private final S srcId;
    /**
     * 终点 id
     */
    private final T dstId;

    private final GraphEdgeType<S, T, E> graphEdgeType;

    /**
     * 起点
     */
    private final GraphVertexType<S> srcVertexType;

    /**
     * 目标顶点
     */
    private final GraphVertexType<T> dstVertexType;

    @Setter
    private int level = 0;

    @Setter
    private boolean ignoreDirect;

    @Override
    public int getHashCode() {
        S startId = this.getSrcId();
        T endId = this.getDstId();
        GraphVertexType srcVertex = this.getSrcVertexType();
        GraphVertexType endVertex = this.getDstVertexType();
        return Objects.hashCode(startId, endId, srcVertex, endVertex);
    }

    @Override
    public boolean isEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphRelation graphRelation = (GraphRelation) o;

        S startId = this.getSrcId();
        T endId = this.getDstId();

        if (startId.getClass() != endId.getClass()) return false;

        GraphVertexType srcVertex = getSrcVertexType();
        GraphVertexType endVertex = this.getDstVertexType();
        return startId.equals(graphRelation.getSrcId()) &&
                endId.equals(graphRelation.getDstId()) && Objects.equal(srcVertex, graphRelation.getSrcVertexType()) &&
                Objects.equal(endVertex, graphRelation.getDstVertexType());
    }

    public GraphEdgeEntity(GraphEdgeType<S, T, E> graphEdgeType, S srcId, T dstId,
                           GraphVertexType<S> srcVertex, GraphVertexType<T> dstVertex, Map<String, Object> props) {
        super(props);
        this.graphEdgeType = graphEdgeType;
        this.srcId = srcId;
        this.dstId = dstId;
        this.srcVertexType = srcVertex;
        this.dstVertexType = dstVertex;
    }

    public GraphEdgeEntity(GraphEdgeType<S, T, E> graphEdgeType, S srcId, T dstId,
                           GraphVertexType<S> srcVertex, GraphVertexType<T> dstVertex) {
        super(Collections.emptyMap());
        this.graphEdgeType = graphEdgeType;
        this.srcId = srcId;
        this.dstId = dstId;
        this.srcVertexType = srcVertex;
        this.dstVertexType = dstVertex;
    }

    @Override
    public boolean equals(Object o) {
        return this.isEquals(o);
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public List<GraphVertexType> getVertices() {
        return Arrays.asList(this.srcVertexType, this.dstVertexType);
    }

    @Override
    public List<GraphEdgeEntity<S, T, E>> getEdges() {
        return Collections.singletonList(this);
    }
}
