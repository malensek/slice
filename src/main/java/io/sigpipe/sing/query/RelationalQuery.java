package io.sigpipe.sing.query;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.sigpipe.sing.dataset.feature.FeatureType;
import io.sigpipe.sing.graph.DataContainer;
import io.sigpipe.sing.graph.Vertex;
import io.sigpipe.sing.serialization.SerializationOutputStream;

public class RelationalQuery extends Query {

    private Set<Vertex> pruned;

    public RelationalQuery() {

    }

    @Override
    public void execute(Vertex root)
    throws IOException, QueryException {
        this.pruned = new HashSet<Vertex>();
        System.out.println("expressions: " + expressions.size());
        prune(root, 0);
        System.out.println("Pruned " + pruned.size() + " vertices");
    public void serializeResults(Vertex vertex, SerializationOutputStream out)
    throws IOException {
        if (pruned.contains(vertex)) {
            return;
        }

        vertex.getLabel().serialize(out);
        if (vertex.hasData() == false) {
            vertex.setData(new DataContainer());
        }
        vertex.getData().serialize(out);

        /* How many neighbors are still valid after the pruning process? */
        int numNeighbors = 0;
        for (Vertex v : vertex.getAllNeighbors()) {
            if (pruned.contains(v) == false) {
                numNeighbors++;
            }
        }
        out.writeInt(numNeighbors);

        for (Vertex v : vertex.getAllNeighbors()) {
            if (pruned.contains(v) == false) {
                serializeResults(v, out);
            }
        }
    }

    private boolean prune(Vertex vertex, int expressionsEvaluated)
    throws QueryException {
        if (expressionsEvaluated == this.expressions.size()) {
            /* There are no further expressions to evaluate. Therefore, we must
             * assume all children from this point are relevant to the query. */
            return true;
        }

        boolean foundSubMatch = false;
        String childFeature = vertex.getFirstNeighbor().getLabel().getName();
        List<Expression> expList = this.expressions.get(childFeature);
        if (expList != null) {
            Set<Vertex> matches = evaluate(vertex, expList);
            if (matches.size() == 0) {
                pruned.add(vertex);
                return false;
            }

            for (Vertex match : matches) {
                if (match == null) {
                    continue;
                }

                if (match.getLabel().getType() == FeatureType.NULL) {
                    continue;
                }

                if (prune(match, expressionsEvaluated + 1) == true) {
                    foundSubMatch = true;
                }
            }

            Set<Vertex> nonMatches = new HashSet<>(vertex.getAllNeighbors());
            nonMatches.removeAll(matches);
            for (Vertex nonMatch : nonMatches) {
                pruned.add(nonMatch);
            }
        } else {
            /* No expression operates on this vertex. Consider all children. */
            for (Vertex neighbor : vertex.getAllNeighbors()) {
                if (prune(neighbor, expressionsEvaluated) == true) {
                    foundSubMatch = true;
                }
            }
        }

        if (foundSubMatch == false) {
            pruned.add(vertex);
        }

        return foundSubMatch;
    }
}
